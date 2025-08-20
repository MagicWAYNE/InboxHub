import React, { useState, useEffect } from 'react';
import { 
  StyleSheet, 
  View, 
  Text,
  TextInput,
  TouchableOpacity,
  SafeAreaView,
  Alert,
  ActivityIndicator,
} from 'react-native';
import { Picker } from '@react-native-picker/picker';
import { useAuth } from '../../contexts/AuthContext';
import { supabase } from '../../lib/supabase';
import { Database } from '../../types/database';

type ApiConfig = Database['public']['Tables']['api_configs']['Row'];

export function Home() {
  const { user } = useAuth();
  const [text, setText] = useState('');
  const [selectedApiConfig, setSelectedApiConfig] = useState<string>('');
  const [apiConfigs, setApiConfigs] = useState<ApiConfig[]>([]);
  const [loading, setLoading] = useState(false);
  const [sending, setSending] = useState(false);

  useEffect(() => {
    loadApiConfigs();
  }, []);

  const loadApiConfigs = async () => {
    if (!user) return;
    
    setLoading(true);
    try {
      const { data, error } = await supabase
        .from('api_configs')
        .select('*')
        .eq('user_id', user.id)
        .order('created_at', { ascending: true });

      if (error) throw error;
      
      setApiConfigs(data || []);
      
      // Set default API config
      const defaultConfig = data?.find(config => config.is_default);
      if (defaultConfig) {
        setSelectedApiConfig(defaultConfig.id);
      } else if (data && data.length > 0) {
        setSelectedApiConfig(data[0].id);
      }
    } catch (error) {
      Alert.alert('错误', '加载API配置失败');
    } finally {
      setLoading(false);
    }
  };

  const handleSend = async () => {
    if (!text.trim()) {
      Alert.alert('提示', '请输入要发送的内容');
      return;
    }

    if (!selectedApiConfig) {
      Alert.alert('提示', '请选择API配置');
      return;
    }

    const config = apiConfigs.find(c => c.id === selectedApiConfig);
    if (!config) {
      Alert.alert('错误', '选择的API配置不存在');
      return;
    }

    setSending(true);
    
    try {
      // Save message to database first
      const { data: messageData, error: insertError } = await supabase
        .from('text_messages')
        .insert({
          user_id: user!.id,
          content: text.trim(),
          api_config_id: selectedApiConfig,
          status: 'pending',
        })
        .select()
        .single();

      if (insertError) throw insertError;

      // Send to API
      try {
        // 使用配置的字段名，默认为 'taskContent'
        const fieldName = config.request_body_field || 'taskContent';
        const requestBody = { [fieldName]: text.trim() };
        
        const response = await fetch(config.url, {
          method: config.method,
          headers: {
            'Content-Type': 'application/json',
            ...config.headers,
          },
          body: JSON.stringify(requestBody),
        });

        // Update message status
        await supabase
          .from('text_messages')
          .update({
            status: response.ok ? 'sent' : 'failed',
            response_status: response.status,
            response_data: await response.json().catch(() => null),
            sent_at: response.ok ? new Date().toISOString() : undefined,
            error_message: response.ok ? undefined : `HTTP ${response.status}`,
          })
          .eq('id', messageData.id);

        if (response.ok) {
          Alert.alert('成功', '消息发送成功');
          setText('');
        } else {
          Alert.alert('失败', `发送失败: HTTP ${response.status}`);
        }
      } catch (apiError) {
        // Update message status on API error
        await supabase
          .from('text_messages')
          .update({
            status: 'failed',
            error_message: apiError instanceof Error ? apiError.message : '网络错误',
          })
          .eq('id', messageData.id);

        Alert.alert('错误', '网络请求失败，请检查网络连接');
      }
    } catch (error) {
      Alert.alert('错误', '保存消息失败');
    } finally {
      setSending(false);
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.appTitle}>InboxHub</Text>
        <View style={styles.headerIcons}>
          <TouchableOpacity style={styles.iconButton}>
            <Text style={styles.iconText}>👤</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.iconButton}>
            <Text style={styles.iconText}>🔔</Text>
          </TouchableOpacity>
        </View>
      </View>

      {/* Main Content */}
      <View style={styles.content}>
        <Text style={styles.instruction}>输入要发送的内容</Text>
        
        <View style={styles.inputSection}>
          <TextInput
            style={styles.textInput}
            placeholder="输入文字或调用输入法语音识别输入"
            value={text}
            onChangeText={setText}
            multiline
            autoFocus
          />
          
          <View style={styles.controlSection}>
            <View style={styles.pickerContainer}>
              <Picker
                selectedValue={selectedApiConfig}
                onValueChange={setSelectedApiConfig}
                style={styles.picker}
                enabled={!loading && apiConfigs.length > 0}
              >
                <Picker.Item label="选择API配置" value="" />
                {apiConfigs.map(config => (
                  <Picker.Item 
                    key={config.id} 
                    label={config.name} 
                    value={config.id} 
                  />
                ))}
              </Picker>
            </View>
            
            <TouchableOpacity 
              style={[styles.sendButton, sending && styles.sendButtonDisabled]}
              onPress={handleSend}
              disabled={sending || !text.trim() || !selectedApiConfig}
            >
              {sending ? (
                <ActivityIndicator color="white" />
              ) : (
                <Text style={styles.sendButtonText}>发送</Text>
              )}
            </TouchableOpacity>
          </View>
        </View>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 20,
    paddingVertical: 15,
    backgroundColor: 'white',
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
  },
  appTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333',
  },
  headerIcons: {
    flexDirection: 'row',
    gap: 15,
  },
  iconButton: {
    padding: 5,
  },
  iconText: {
    fontSize: 20,
  },
  content: {
    flex: 1,
    paddingHorizontal: 20,
    paddingTop: 30,
  },
  instruction: {
    fontSize: 18,
    color: '#666',
    marginBottom: 20,
    textAlign: 'center',
  },
  inputSection: {
    backgroundColor: 'white',
    borderRadius: 15,
    padding: 20,
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
  },
  textInput: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 10,
    paddingHorizontal: 15,
    paddingVertical: 12,
    fontSize: 16,
    minHeight: 100,
    textAlignVertical: 'top',
    backgroundColor: '#f9f9f9',
    marginBottom: 15,
  },
  controlSection: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 15,
  },
  pickerContainer: {
    flex: 1,
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 10,
    backgroundColor: '#f9f9f9',
  },
  picker: {
    height: 50,
  },
  sendButton: {
    backgroundColor: '#007AFF',
    paddingHorizontal: 30,
    paddingVertical: 15,
    borderRadius: 10,
    minWidth: 80,
    alignItems: 'center',
  },
  sendButtonDisabled: {
    opacity: 0.6,
  },
  sendButtonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: '600',
  },
});

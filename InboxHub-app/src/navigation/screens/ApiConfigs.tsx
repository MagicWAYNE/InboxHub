import React, { useState, useEffect, useCallback } from 'react';
import {
  StyleSheet,
  View,
  Text,
  TouchableOpacity,
  ScrollView,
  SafeAreaView,
  Alert,
  Modal,
  TextInput,
  ActivityIndicator,
} from 'react-native';
import { Picker } from '@react-native-picker/picker';
import { useAuth } from '../../contexts/AuthContext';
import { supabase } from '../../lib/supabase';
import { Database } from '../../types/database';

type ApiConfig = Database['public']['Tables']['api_configs']['Row'];
type InsertApiConfig = Database['public']['Tables']['api_configs']['Insert'];

export function ApiConfigs() {
  const { user } = useAuth();
  const [apiConfigs, setApiConfigs] = useState<ApiConfig[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingConfig, setEditingConfig] = useState<ApiConfig | null>(null);
  const [saving, setSaving] = useState(false);

  // Form state
  const [formData, setFormData] = useState({
    name: '',
    url: '',
    method: 'POST' as const,
    headers: '{}',
    request_body_field: 'taskContent',
    is_default: false,
  });

  const loadApiConfigs = useCallback(async () => {
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
    } catch (error) {
      Alert.alert('错误', '加载API配置失败');
    } finally {
      setLoading(false);
    }
  }, [user]);

  useEffect(() => {
    loadApiConfigs();
  }, [loadApiConfigs]);

  const resetForm = () => {
    setFormData({
      name: '',
      url: '',
      method: 'POST',
      headers: '{}',
      request_body_field: 'taskContent',
      is_default: false,
    });
    setEditingConfig(null);
  };

  const openAddModal = () => {
    resetForm();
    setShowModal(true);
  };

  const openEditModal = (config: ApiConfig) => {
    setFormData({
      name: config.name,
      url: config.url,
      method: config.method as 'GET' | 'POST' | 'PUT' | 'DELETE',
      headers: JSON.stringify(config.headers, null, 2),
      request_body_field: config.request_body_field || 'taskContent',
      is_default: config.is_default,
    });
    setEditingConfig(config);
    setShowModal(true);
  };

  const validateForm = () => {
    if (!formData.name.trim()) {
      Alert.alert('错误', '请输入配置名称');
      return false;
    }
    if (!formData.url.trim()) {
      Alert.alert('错误', '请输入API地址');
      return false;
    }
    if (!formData.request_body_field.trim()) {
      Alert.alert('错误', '请输入请求体字段名');
      return false;
    }
    try {
      JSON.parse(formData.headers);
    } catch {
      Alert.alert('错误', '请求头格式错误，请输入有效的JSON');
      return false;
    }
    return true;
  };

  const handleSave = async () => {
    if (!validateForm()) return;
    if (!user) return;

    setSaving(true);
    try {
      let parsedHeaders;
      try {
        parsedHeaders = JSON.parse(formData.headers);
      } catch {
        parsedHeaders = {};
      }

      const configData: InsertApiConfig = {
        user_id: user.id,
        name: formData.name.trim(),
        url: formData.url.trim(),
        method: formData.method,
        headers: parsedHeaders,
        request_body_field: formData.request_body_field.trim(),
        is_default: formData.is_default,
      };

      if (editingConfig) {
        // Update existing config
        const { error } = await supabase
          .from('api_configs')
          .update(configData)
          .eq('id', editingConfig.id);
        
        if (error) throw error;
      } else {
        // Create new config
        const { error } = await supabase
          .from('api_configs')
          .insert(configData);
        
        if (error) throw error;
      }

      await loadApiConfigs();
      setShowModal(false);
      resetForm();
      Alert.alert('成功', editingConfig ? '配置更新成功' : '配置添加成功');
    } catch (error) {
      Alert.alert('错误', '保存配置失败');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (config: ApiConfig) => {
    Alert.alert(
      '确认删除',
      `确定要删除配置"${config.name}"吗？`,
      [
        { text: '取消', style: 'cancel' },
        {
          text: '删除',
          style: 'destructive',
          onPress: async () => {
            try {
              const { error } = await supabase
                .from('api_configs')
                .delete()
                .eq('id', config.id);
              
              if (error) throw error;
              await loadApiConfigs();
              Alert.alert('成功', '配置删除成功');
            } catch (error) {
              Alert.alert('错误', '删除配置失败');
            }
          }
        }
      ]
    );
  };

  if (loading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color="#007AFF" />
        <Text style={styles.loadingText}>加载中...</Text>
      </View>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>API配置</Text>
      </View>

      <ScrollView style={styles.content} showsVerticalScrollIndicator={false}>
        {apiConfigs.map((config) => (
          <View key={config.id} style={styles.configCard}>
            <View style={styles.cardHeader}>
              <View style={styles.cardInfo}>
                <Text style={styles.configName}>{config.name}</Text>
                {config.is_default && (
                  <Text style={styles.defaultBadge}>默认</Text>
                )}
              </View>
              <TouchableOpacity
                style={styles.editButton}
                onPress={() => openEditModal(config)}
              >
                <Text style={styles.editButtonText}>编辑</Text>
              </TouchableOpacity>
            </View>
            
            <View style={styles.cardDetails}>
              <Text style={styles.detailLabel}>URL:</Text>
              <Text style={styles.detailValue}>{config.url}</Text>
              <Text style={styles.detailLabel}>方法:</Text>
              <Text style={styles.detailValue}>{config.method}</Text>
              <Text style={styles.detailLabel}>字段名:</Text>
              <Text style={styles.detailValue}>{config.request_body_field || 'taskContent'}</Text>
            </View>

            <TouchableOpacity
              style={styles.deleteButton}
              onPress={() => handleDelete(config)}
            >
              <Text style={styles.deleteButtonText}>删除</Text>
            </TouchableOpacity>
          </View>
        ))}

        <TouchableOpacity style={styles.addButton} onPress={openAddModal}>
          <Text style={styles.addButtonText}>+ 新增配置</Text>
        </TouchableOpacity>
      </ScrollView>

      {/* Modal for Add/Edit */}
      <Modal
        visible={showModal}
        animationType="slide"
        presentationStyle="pageSheet"
      >
        <SafeAreaView style={styles.modalContainer}>
          <View style={styles.modalHeader}>
            <TouchableOpacity
              onPress={() => setShowModal(false)}
              style={styles.cancelButton}
            >
              <Text style={styles.cancelButtonText}>取消</Text>
            </TouchableOpacity>
            <Text style={styles.modalTitle}>
              {editingConfig ? '编辑配置' : '新增配置'}
            </Text>
            <TouchableOpacity
              onPress={handleSave}
              style={styles.saveButton}
              disabled={saving}
            >
              {saving ? (
                <ActivityIndicator size="small" color="#007AFF" />
              ) : (
                <Text style={styles.saveButtonText}>保存</Text>
              )}
            </TouchableOpacity>
          </View>

          <ScrollView style={styles.modalContent}>
            <View style={styles.formGroup}>
              <Text style={styles.formLabel}>配置名称 *</Text>
              <TextInput
                style={styles.formInput}
                value={formData.name}
                onChangeText={(text) => setFormData(prev => ({ ...prev, name: text }))}
                placeholder="输入配置名称"
              />
            </View>

            <View style={styles.formGroup}>
              <Text style={styles.formLabel}>API地址 *</Text>
              <TextInput
                style={styles.formInput}
                value={formData.url}
                onChangeText={(text) => setFormData(prev => ({ ...prev, url: text }))}
                placeholder="https://api.example.com/webhook"
                autoCapitalize="none"
                autoCorrect={false}
              />
            </View>

            <View style={styles.formGroup}>
              <Text style={styles.formLabel}>HTTP方法</Text>
              <View style={styles.pickerContainer}>
                <Picker
                  selectedValue={formData.method}
                  onValueChange={(value) => setFormData(prev => ({ ...prev, method: value }))}
                  style={styles.picker}
                >
                  <Picker.Item label="POST" value="POST" />
                  <Picker.Item label="GET" value="GET" />
                  <Picker.Item label="PUT" value="PUT" />
                  <Picker.Item label="DELETE" value="DELETE" />
                </Picker>
              </View>
            </View>

            <View style={styles.formGroup}>
              <Text style={styles.formLabel}>请求体字段名 *</Text>
              <TextInput
                style={styles.formInput}
                value={formData.request_body_field}
                onChangeText={(text) => setFormData(prev => ({ ...prev, request_body_field: text }))}
                placeholder="taskContent"
                autoCapitalize="none"
                autoCorrect={false}
              />
              <Text style={styles.fieldHint}>
                API 期望接收的字段名（如: taskContent, content, message, text）
              </Text>
            </View>

            <View style={styles.formGroup}>
              <Text style={styles.formLabel}>请求头 (JSON格式)</Text>
              <TextInput
                style={[styles.formInput, styles.headersInput]}
                value={formData.headers}
                onChangeText={(text) => setFormData(prev => ({ ...prev, headers: text }))}
                placeholder='{"Authorization": "Bearer token"}'
                multiline
                autoCapitalize="none"
                autoCorrect={false}
              />
            </View>

            <View style={styles.checkboxRow}>
              <TouchableOpacity
                style={styles.checkbox}
                onPress={() => setFormData(prev => ({ ...prev, is_default: !prev.is_default }))}
              >
                <Text style={styles.checkboxIcon}>
                  {formData.is_default ? '☑️' : '☐'}
                </Text>
                <Text style={styles.checkboxLabel}>设为默认配置</Text>
              </TouchableOpacity>
            </View>
          </ScrollView>
        </SafeAreaView>
      </Modal>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#f5f5f5',
  },
  loadingText: {
    marginTop: 10,
    fontSize: 16,
    color: '#666',
  },
  header: {
    backgroundColor: 'white',
    paddingHorizontal: 20,
    paddingVertical: 15,
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333',
  },
  content: {
    flex: 1,
    paddingHorizontal: 20,
    paddingTop: 20,
  },
  configCard: {
    backgroundColor: 'white',
    borderRadius: 12,
    padding: 15,
    marginBottom: 15,
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
  },
  cardHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 10,
  },
  cardInfo: {
    flexDirection: 'row',
    alignItems: 'center',
    flex: 1,
  },
  configName: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginRight: 10,
  },
  defaultBadge: {
    backgroundColor: '#007AFF',
    color: 'white',
    fontSize: 12,
    paddingHorizontal: 8,
    paddingVertical: 2,
    borderRadius: 10,
  },
  editButton: {
    backgroundColor: '#007AFF',
    paddingHorizontal: 15,
    paddingVertical: 8,
    borderRadius: 6,
  },
  editButtonText: {
    color: 'white',
    fontSize: 14,
    fontWeight: '600',
  },
  cardDetails: {
    marginBottom: 10,
  },
  detailLabel: {
    fontSize: 14,
    color: '#666',
    marginTop: 5,
  },
  detailValue: {
    fontSize: 14,
    color: '#333',
    marginTop: 2,
  },
  deleteButton: {
    alignSelf: 'flex-end',
    backgroundColor: '#FF3B30',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 6,
  },
  deleteButtonText: {
    color: 'white',
    fontSize: 12,
    fontWeight: '600',
  },
  addButton: {
    backgroundColor: '#007AFF',
    borderRadius: 12,
    paddingVertical: 20,
    alignItems: 'center',
    marginBottom: 30,
  },
  addButtonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: '600',
  },
  modalContainer: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  modalHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 20,
    paddingVertical: 15,
    backgroundColor: 'white',
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
  },
  cancelButton: {
    padding: 5,
  },
  cancelButtonText: {
    color: '#007AFF',
    fontSize: 16,
  },
  modalTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
  },
  saveButton: {
    padding: 5,
  },
  saveButtonText: {
    color: '#007AFF',
    fontSize: 16,
    fontWeight: '600',
  },
  modalContent: {
    flex: 1,
    paddingHorizontal: 20,
    paddingTop: 20,
  },
  formGroup: {
    marginBottom: 20,
  },
  formLabel: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    marginBottom: 8,
  },
  formInput: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 10,
    paddingHorizontal: 15,
    paddingVertical: 12,
    fontSize: 16,
    backgroundColor: 'white',
  },
  fieldHint: {
    fontSize: 12,
    color: '#666',
    marginTop: 5,
    lineHeight: 16,
  },
  headersInput: {
    minHeight: 100,
    textAlignVertical: 'top',
  },
  pickerContainer: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 10,
    backgroundColor: 'white',
  },
  picker: {
    height: 50,
  },
  checkboxRow: {
    marginBottom: 30,
  },
  checkbox: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  checkboxIcon: {
    fontSize: 20,
    marginRight: 10,
  },
  checkboxLabel: {
    fontSize: 16,
    color: '#333',
  },
});
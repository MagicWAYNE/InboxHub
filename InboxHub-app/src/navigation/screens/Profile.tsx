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
  ScrollView,
} from 'react-native';
import { useAuth } from '../../contexts/AuthContext';
import { supabase } from '../../lib/supabase';
import { Database } from '../../types/database';

type Profile = Database['public']['Tables']['profiles']['Row'];

export function Profile() {
  const { user, signOut } = useAuth();
  const [profile, setProfile] = useState<Profile | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [editing, setEditing] = useState({
    display_name: false,
    phone: false,
  });

  // Form state
  const [formData, setFormData] = useState({
    display_name: '',
    phone: '',
  });

  useEffect(() => {
    loadProfile();
  }, []);

  const loadProfile = async () => {
    if (!user) return;
    
    setLoading(true);
    try {
      const { data, error } = await supabase
        .from('profiles')
        .select('*')
        .eq('id', user.id)
        .single();

      if (error) {
        // If profile doesn't exist, create one
        if (error.code === 'PGRST116') {
          const { data: newProfile, error: insertError } = await supabase
            .from('profiles')
            .insert({
              id: user.id,
              email: user.email || '',
              display_name: user.email || '',
            })
            .select()
            .single();

          if (insertError) throw insertError;
          setProfile(newProfile);
          setFormData({
            display_name: newProfile.display_name || '',
            phone: newProfile.phone || '',
          });
        } else {
          throw error;
        }
      } else {
        setProfile(data);
        setFormData({
          display_name: data.display_name || '',
          phone: data.phone || '',
        });
      }
    } catch (error) {
      Alert.alert('错误', '加载用户信息失败');
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async (field: 'display_name' | 'phone') => {
    if (!user || !profile) return;

    const value = formData[field].trim();
    
    setSaving(true);
    try {
      const { error } = await supabase
        .from('profiles')
        .update({ [field]: value || null })
        .eq('id', user.id);

      if (error) throw error;

      setProfile(prev => prev ? { ...prev, [field]: value || null } : null);
      setEditing(prev => ({ ...prev, [field]: false }));
      Alert.alert('成功', '信息更新成功');
    } catch (error) {
      Alert.alert('错误', '更新失败，请重试');
      // Reset form data on error
      setFormData(prev => ({
        ...prev,
        [field]: profile[field] || '',
      }));
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = (field: 'display_name' | 'phone') => {
    setFormData(prev => ({
      ...prev,
      [field]: profile?.[field] || '',
    }));
    setEditing(prev => ({ ...prev, [field]: false }));
  };

  const handleSignOut = async () => {
    Alert.alert(
      '确认退出',
      '确定要退出登录吗？',
      [
        { text: '取消', style: 'cancel' },
        {
          text: '退出',
          style: 'destructive',
          onPress: async () => {
            try {
              await signOut();
            } catch (error) {
              Alert.alert('错误', '退出登录失败');
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

  if (!profile) {
    return (
      <View style={styles.loadingContainer}>
        <Text style={styles.errorText}>用户信息加载失败</Text>
        <TouchableOpacity style={styles.retryButton} onPress={loadProfile}>
          <Text style={styles.retryButtonText}>重试</Text>
        </TouchableOpacity>
      </View>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>我的</Text>
      </View>

      <ScrollView style={styles.content}>
        {/* Email Section - Read Only */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>账户信息</Text>
          
          <View style={styles.fieldContainer}>
            <Text style={styles.fieldLabel}>邮箱</Text>
            <View style={styles.fieldValueContainer}>
              <Text style={styles.fieldValue}>{profile.email}</Text>
              <Text style={styles.fieldNote}>不可编辑</Text>
            </View>
          </View>
        </View>

        {/* Editable Fields */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>个人信息</Text>
          
          {/* Display Name */}
          <View style={styles.fieldContainer}>
            <Text style={styles.fieldLabel}>昵称</Text>
            {editing.display_name ? (
              <View style={styles.editContainer}>
                <TextInput
                  style={styles.editInput}
                  value={formData.display_name}
                  onChangeText={(text) => setFormData(prev => ({ ...prev, display_name: text }))}
                  placeholder="输入昵称"
                  autoFocus
                />
                <View style={styles.editButtons}>
                  <TouchableOpacity
                    style={styles.cancelButton}
                    onPress={() => handleCancel('display_name')}
                    disabled={saving}
                  >
                    <Text style={styles.cancelButtonText}>取消</Text>
                  </TouchableOpacity>
                  <TouchableOpacity
                    style={[styles.saveButton, saving && styles.saveButtonDisabled]}
                    onPress={() => handleSave('display_name')}
                    disabled={saving}
                  >
                    {saving ? (
                      <ActivityIndicator size="small" color="white" />
                    ) : (
                      <Text style={styles.saveButtonText}>保存</Text>
                    )}
                  </TouchableOpacity>
                </View>
              </View>
            ) : (
              <TouchableOpacity
                style={styles.fieldValueContainer}
                onPress={() => setEditing(prev => ({ ...prev, display_name: true }))}
              >
                <Text style={styles.fieldValue}>
                  {profile.display_name || '点击设置昵称'}
                </Text>
                <Text style={styles.editHint}>点击编辑</Text>
              </TouchableOpacity>
            )}
          </View>

          {/* Phone */}
          <View style={styles.fieldContainer}>
            <Text style={styles.fieldLabel}>手机号</Text>
            {editing.phone ? (
              <View style={styles.editContainer}>
                <TextInput
                  style={styles.editInput}
                  value={formData.phone}
                  onChangeText={(text) => setFormData(prev => ({ ...prev, phone: text }))}
                  placeholder="输入手机号"
                  keyboardType="phone-pad"
                  autoFocus
                />
                <View style={styles.editButtons}>
                  <TouchableOpacity
                    style={styles.cancelButton}
                    onPress={() => handleCancel('phone')}
                    disabled={saving}
                  >
                    <Text style={styles.cancelButtonText}>取消</Text>
                  </TouchableOpacity>
                  <TouchableOpacity
                    style={[styles.saveButton, saving && styles.saveButtonDisabled]}
                    onPress={() => handleSave('phone')}
                    disabled={saving}
                  >
                    {saving ? (
                      <ActivityIndicator size="small" color="white" />
                    ) : (
                      <Text style={styles.saveButtonText}>保存</Text>
                    )}
                  </TouchableOpacity>
                </View>
              </View>
            ) : (
              <TouchableOpacity
                style={styles.fieldValueContainer}
                onPress={() => setEditing(prev => ({ ...prev, phone: true }))}
              >
                <Text style={styles.fieldValue}>
                  {profile.phone || '点击设置手机号'}
                </Text>
                <Text style={styles.editHint}>点击编辑</Text>
              </TouchableOpacity>
            )}
          </View>
        </View>

        {/* Sign Out */}
        <View style={styles.section}>
          <TouchableOpacity style={styles.signOutButton} onPress={handleSignOut}>
            <Text style={styles.signOutButtonText}>退出登录</Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
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
  errorText: {
    fontSize: 16,
    color: '#666',
    marginBottom: 20,
  },
  retryButton: {
    backgroundColor: '#007AFF',
    paddingHorizontal: 20,
    paddingVertical: 10,
    borderRadius: 8,
  },
  retryButtonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: '600',
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
  section: {
    backgroundColor: 'white',
    borderRadius: 12,
    padding: 20,
    marginBottom: 20,
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 15,
  },
  fieldContainer: {
    marginBottom: 20,
  },
  fieldLabel: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    marginBottom: 8,
  },
  fieldValueContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 12,
    paddingHorizontal: 15,
    backgroundColor: '#f9f9f9',
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#e0e0e0',
  },
  fieldValue: {
    fontSize: 16,
    color: '#333',
  },
  fieldNote: {
    fontSize: 12,
    color: '#999',
  },
  editHint: {
    fontSize: 12,
    color: '#007AFF',
  },
  editContainer: {
    gap: 10,
  },
  editInput: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 8,
    paddingHorizontal: 15,
    paddingVertical: 12,
    fontSize: 16,
    backgroundColor: 'white',
  },
  editButtons: {
    flexDirection: 'row',
    gap: 10,
  },
  cancelButton: {
    flex: 1,
    backgroundColor: '#f0f0f0',
    paddingVertical: 12,
    borderRadius: 8,
    alignItems: 'center',
  },
  cancelButtonText: {
    color: '#666',
    fontSize: 16,
    fontWeight: '600',
  },
  saveButton: {
    flex: 1,
    backgroundColor: '#007AFF',
    paddingVertical: 12,
    borderRadius: 8,
    alignItems: 'center',
  },
  saveButtonDisabled: {
    opacity: 0.6,
  },
  saveButtonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: '600',
  },
  signOutButton: {
    backgroundColor: '#FF3B30',
    paddingVertical: 15,
    borderRadius: 8,
    alignItems: 'center',
  },
  signOutButtonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: '600',
  },
});

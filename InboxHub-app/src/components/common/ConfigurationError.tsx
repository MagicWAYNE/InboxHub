import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Linking,
  SafeAreaView,
} from 'react-native';

interface ConfigurationErrorProps {
  error: Error;
  onRetry?: () => void;
}

export function ConfigurationError({ error, onRetry }: ConfigurationErrorProps) {
  const handleOpenSupabase = () => {
    Linking.openURL('https://supabase.com');
  };

  const handleOpenDocs = () => {
    Linking.openURL('https://supabase.com/docs');
  };

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.content}>
        <View style={styles.header}>
          <Text style={styles.icon}>⚙️</Text>
          <Text style={styles.title}>需要配置 Supabase</Text>
          <Text style={styles.subtitle}>应用需要 Supabase 后端服务才能正常运行</Text>
        </View>

        <View style={styles.errorBox}>
          <Text style={styles.errorTitle}>错误信息:</Text>
          <Text style={styles.errorMessage}>{error.message}</Text>
        </View>

        <View style={styles.stepsContainer}>
          <Text style={styles.stepsTitle}>📋 配置步骤</Text>
          
          <View style={styles.step}>
            <Text style={styles.stepNumber}>1</Text>
            <View style={styles.stepContent}>
              <Text style={styles.stepTitle}>创建 Supabase 项目</Text>
              <Text style={styles.stepDescription}>访问 Supabase 官网创建新项目</Text>
              <TouchableOpacity style={styles.linkButton} onPress={handleOpenSupabase}>
                <Text style={styles.linkButtonText}>打开 Supabase</Text>
              </TouchableOpacity>
            </View>
          </View>

          <View style={styles.step}>
            <Text style={styles.stepNumber}>2</Text>
            <View style={styles.stepContent}>
              <Text style={styles.stepTitle}>获取项目信息</Text>
              <Text style={styles.stepDescription}>
                在项目设置 → API 中找到：{'\n'}
                • Project URL{'\n'}
                • anon public key
              </Text>
            </View>
          </View>

          <View style={styles.step}>
            <Text style={styles.stepNumber}>3</Text>
            <View style={styles.stepContent}>
              <Text style={styles.stepTitle}>更新 .env 文件</Text>
              <Text style={styles.stepDescription}>
                替换以下占位符：{'\n'}
                • EXPO_PUBLIC_SUPABASE_URL{'\n'}
                • EXPO_PUBLIC_SUPABASE_ANON_KEY
              </Text>
            </View>
          </View>

          <View style={styles.step}>
            <Text style={styles.stepNumber}>4</Text>
            <View style={styles.stepContent}>
              <Text style={styles.stepTitle}>运行数据库迁移</Text>
              <Text style={styles.stepDescription}>
                在 Supabase SQL Editor 中运行 supabase/migrations/ 文件夹中的 SQL 文件
              </Text>
            </View>
          </View>

          <View style={styles.step}>
            <Text style={styles.stepNumber}>5</Text>
            <View style={styles.stepContent}>
              <Text style={styles.stepTitle}>重启应用</Text>
              <Text style={styles.stepDescription}>保存配置文件后重启 Expo 开发服务器</Text>
            </View>
          </View>
        </View>

        <View style={styles.buttonContainer}>
          <TouchableOpacity style={styles.docsButton} onPress={handleOpenDocs}>
            <Text style={styles.docsButtonText}>查看 Supabase 文档</Text>
          </TouchableOpacity>
          
          {onRetry && (
            <TouchableOpacity style={styles.retryButton} onPress={onRetry}>
              <Text style={styles.retryButtonText}>重试</Text>
            </TouchableOpacity>
          )}
        </View>

        <View style={styles.helpBox}>
          <Text style={styles.helpTitle}>💡 需要帮助？</Text>
          <Text style={styles.helpDescription}>
            详细配置说明请查看项目根目录的 SUPABASE_SETUP.md 文件
          </Text>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f8f9fa',
  },
  content: {
    padding: 20,
  },
  header: {
    alignItems: 'center',
    marginBottom: 30,
  },
  icon: {
    fontSize: 48,
    marginBottom: 10,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 16,
    color: '#666',
    textAlign: 'center',
  },
  errorBox: {
    backgroundColor: '#fff5f5',
    borderColor: '#fed7d7',
    borderWidth: 1,
    borderRadius: 8,
    padding: 15,
    marginBottom: 25,
  },
  errorTitle: {
    fontSize: 14,
    fontWeight: 'bold',
    color: '#c53030',
    marginBottom: 8,
  },
  errorMessage: {
    fontSize: 14,
    color: '#742a2a',
    fontFamily: 'monospace',
  },
  stepsContainer: {
    backgroundColor: 'white',
    borderRadius: 12,
    padding: 20,
    marginBottom: 25,
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
  },
  stepsTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 20,
  },
  step: {
    flexDirection: 'row',
    marginBottom: 20,
  },
  stepNumber: {
    width: 32,
    height: 32,
    backgroundColor: '#007AFF',
    color: 'white',
    textAlign: 'center',
    lineHeight: 32,
    borderRadius: 16,
    fontWeight: 'bold',
    marginRight: 15,
  },
  stepContent: {
    flex: 1,
  },
  stepTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 5,
  },
  stepDescription: {
    fontSize: 14,
    color: '#666',
    lineHeight: 20,
    marginBottom: 10,
  },
  linkButton: {
    backgroundColor: '#007AFF',
    paddingHorizontal: 15,
    paddingVertical: 8,
    borderRadius: 6,
    alignSelf: 'flex-start',
  },
  linkButtonText: {
    color: 'white',
    fontSize: 14,
    fontWeight: '600',
  },
  buttonContainer: {
    flexDirection: 'row',
    gap: 15,
    marginBottom: 25,
  },
  docsButton: {
    flex: 1,
    backgroundColor: '#48bb78',
    paddingVertical: 15,
    borderRadius: 8,
    alignItems: 'center',
  },
  docsButtonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: '600',
  },
  retryButton: {
    flex: 1,
    backgroundColor: '#007AFF',
    paddingVertical: 15,
    borderRadius: 8,
    alignItems: 'center',
  },
  retryButtonText: {
    color: 'white',
    fontSize: 16,
    fontWeight: '600',
  },
  helpBox: {
    backgroundColor: '#ebf8ff',
    borderColor: '#90cdf4',
    borderWidth: 1,
    borderRadius: 8,
    padding: 15,
  },
  helpTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#2b6cb0',
    marginBottom: 8,
  },
  helpDescription: {
    fontSize: 14,
    color: '#2c5282',
    lineHeight: 20,
  },
});
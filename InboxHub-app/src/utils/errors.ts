import { Alert } from 'react-native';

export function handleError(error: any, defaultMessage: string = '操作失败') {
  let message = defaultMessage;
  
  if (error?.message) {
    // Handle common Supabase errors
    if (error.message.includes('Invalid login credentials')) {
      message = '邮箱或密码错误';
    } else if (error.message.includes('Email not confirmed')) {
      message = '请先确认邮箱';
    } else if (error.message.includes('User already registered')) {
      message = '用户已存在';
    } else if (error.message.includes('Password should be at least')) {
      message = '密码长度至少6位';
    } else if (error.message.includes('Invalid email')) {
      message = '邮箱格式不正确';
    } else if (error.message.includes('Network request failed')) {
      message = '网络连接失败，请检查网络';
    } else if (error.code === 'PGRST301') {
      message = '请求超时，请重试';
    } else {
      message = error.message;
    }
  }
  
  Alert.alert('错误', message);
}

export function showSuccess(message: string) {
  Alert.alert('成功', message);
}

export function showInfo(title: string, message: string) {
  Alert.alert(title, message);
}

export function confirmAction(
  title: string, 
  message: string, 
  onConfirm: () => void,
  confirmText: string = '确定',
  cancelText: string = '取消'
) {
  Alert.alert(
    title,
    message,
    [
      { text: cancelText, style: 'cancel' },
      { text: confirmText, onPress: onConfirm }
    ]
  );
}
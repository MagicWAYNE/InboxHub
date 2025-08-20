import { supabase } from '../lib/supabase';

export async function testSupabaseConnection(): Promise<{
  success: boolean;
  message: string;
  details?: any;
}> {
  try {
    // Test basic connection
    const { data, error } = await supabase.auth.getSession();
    
    if (error) {
      return {
        success: false,
        message: `Supabase 连接失败: ${error.message}`,
        details: error,
      };
    }

    // Test if we can access the database (this will fail if tables don't exist, which is expected)
    try {
      await supabase.from('profiles').select('count').limit(1);
    } catch (dbError) {
      // This is expected if tables haven't been created yet
      console.log('Database tables not yet created:', dbError);
    }

    return {
      success: true,
      message: 'Supabase 连接成功！',
      details: { hasSession: !!data.session },
    };
  } catch (error: any) {
    return {
      success: false,
      message: `连接测试失败: ${error.message}`,
      details: error,
    };
  }
}

export function validateSupabaseConfig(): {
  valid: boolean;
  message: string;
} {
  const url = process.env.EXPO_PUBLIC_SUPABASE_URL;
  const key = process.env.EXPO_PUBLIC_SUPABASE_ANON_KEY;

  if (!url || url === 'your_supabase_url_here') {
    return {
      valid: false,
      message: 'EXPO_PUBLIC_SUPABASE_URL 未配置',
    };
  }

  if (!key || key === 'your_supabase_anon_key_here') {
    return {
      valid: false,
      message: 'EXPO_PUBLIC_SUPABASE_ANON_KEY 未配置',
    };
  }

  if (!url.startsWith('https://') || !url.includes('.supabase.co')) {
    return {
      valid: false,
      message: 'Supabase URL 格式不正确，应该是 https://xxx.supabase.co',
    };
  }

  if (!key.startsWith('eyJ')) {
    return {
      valid: false,
      message: 'Supabase Anon Key 格式不正确，应该以 eyJ 开头',
    };
  }

  return {
    valid: true,
    message: '配置验证通过',
  };
}
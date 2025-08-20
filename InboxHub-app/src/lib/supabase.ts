import 'react-native-url-polyfill/auto';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { createClient } from '@supabase/supabase-js';

const supabaseUrl = process.env.EXPO_PUBLIC_SUPABASE_URL;
const supabaseAnonKey = process.env.EXPO_PUBLIC_SUPABASE_ANON_KEY;

// Validate environment variables
if (!supabaseUrl || supabaseUrl === 'your_supabase_url_here') {
  throw new Error(
    '❌ EXPO_PUBLIC_SUPABASE_URL is not configured!\n\n' +
    '请按照以下步骤配置 Supabase：\n' +
    '1. 在 https://supabase.com 创建项目\n' +
    '2. 更新 .env 文件中的 EXPO_PUBLIC_SUPABASE_URL\n' +
    '3. 更新 .env 文件中的 EXPO_PUBLIC_SUPABASE_ANON_KEY\n' +
    '4. 参考 SUPABASE_SETUP.md 完成配置'
  );
}

if (!supabaseAnonKey || supabaseAnonKey === 'your_supabase_anon_key_here') {
  throw new Error(
    '❌ EXPO_PUBLIC_SUPABASE_ANON_KEY is not configured!\n\n' +
    '请按照以下步骤配置 Supabase：\n' +
    '1. 在 https://supabase.com 创建项目\n' +
    '2. 更新 .env 文件中的 EXPO_PUBLIC_SUPABASE_URL\n' +
    '3. 更新 .env 文件中的 EXPO_PUBLIC_SUPABASE_ANON_KEY\n' +
    '4. 参考 SUPABASE_SETUP.md 完成配置'
  );
}

export const supabase = createClient(supabaseUrl, supabaseAnonKey, {
  auth: {
    storage: AsyncStorage,
    autoRefreshToken: true,
    persistSession: true,
    detectSessionInUrl: false,
  },
});
# Supabase Setup Instructions

## 1. Create Supabase Project

1. Go to [https://supabase.com/](https://supabase.com/)
2. Sign in and create a new project
3. Note down your project URL and anon key

## 2. Update Environment Variables

Update the `.env` file with your actual Supabase credentials:

```
EXPO_PUBLIC_SUPABASE_URL=https://your-project-id.supabase.co
EXPO_PUBLIC_SUPABASE_ANON_KEY=your-anon-key-here
```

## 3. Run Database Migrations

Copy the contents of the following files and run them in your Supabase SQL Editor to create the necessary tables and policies:

1. `supabase/migrations/001_create_tables.sql` - Creates api_configs and text_messages tables
2. `supabase/migrations/002_create_profiles.sql` - Creates profiles table and user signup trigger
3. `supabase/migrations/003_add_request_body_field.sql` - Adds request body field configuration

Alternatively, if you have Supabase CLI installed:

```bash
# Initialize Supabase in your project
supabase init

# Link to your project
supabase link --project-ref your-project-id

# Run migrations
supabase db push
```

## 4. Enable Email Authentication

In your Supabase dashboard:

1. Go to Authentication > Settings
2. Enable Email provider
3. Configure email templates if needed
4. Set up email provider (optional - for production)

## 5. Test Connection

现在您的 Supabase 已经配置完成！应用具有智能的配置验证功能：

### 🔄 重启应用
```bash
# 停止当前开发服务器 (Ctrl+C)
# 然后重新启动
npm start
```

### ✅ 配置验证
- 如果配置正确，应用将正常启动并显示登录页面
- 如果配置有误，应用会显示详细的配置指导页面
- 配置页面包含步骤说明和有用的链接

### 🧪 测试流程
1. **注册新用户**: 使用有效邮箱注册
2. **检查邮件**: Supabase 会发送确认邮件
3. **确认邮箱**: 点击邮件中的确认链接
4. **登录**: 使用注册的邮箱登录
5. **测试功能**: 
   - 添加 API 配置
   - 发送测试消息
   - 编辑用户资料

### 🚨 常见问题

**问题**: 出现 "Invalid URL" 错误
- **解决**: 检查 `.env` 文件中的 URL 格式是否正确

**问题**: 应用显示配置错误页面
- **解决**: 按照页面上的步骤说明重新配置

**问题**: 登录后看不到数据库表错误
- **解决**: 确保已运行所有数据库迁移文件

## Database Schema

The migrations create three main tables:

- `profiles`: Stores user profile information (display name, phone)
- `api_configs`: Stores user's API endpoint configurations
- `text_messages`: Stores sent messages and their status

All tables have Row Level Security (RLS) enabled to ensure users can only access their own data.
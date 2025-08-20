# InboxHub

<div align="center">
  <h3>🚀 快速将文本发送到任意 API 的移动应用</h3>
  <p>基于 React Native + Expo + Supabase 构建</p>
</div>

## 📋 项目概述

InboxHub 是一个专为快速文本传递而设计的移动应用。用户可以通过简洁的界面将文字内容发送到预配置的 API 端点，适用于各种自动化场景，如消息通知、数据收集、内容分发等。

## ✨ 主要功能

### 🔐 用户认证系统
- 基于邮箱的注册/登录
- Supabase 提供的安全认证
- 会话自动管理

### 📝 文本发送功能
- 直观的文本输入界面
- 支持多行文本输入
- 语音输入提示
- 实时发送状态反馈

### ⚙️ API 配置管理
- 卡片式 API 配置展示
- 支持多种 HTTP 方法 (GET/POST/PUT/DELETE)
- 自定义请求头配置
- 默认配置标记

### 👤 用户资料管理
- 个人信息编辑 (昵称、手机号)
- 账户信息显示
- 一键退出登录

### 📊 消息追踪
- 详细的发送记录
- 响应状态追踪
- 错误信息记录

## 🏗 技术架构

### 前端技术栈
- **React Native**: 跨平台移动应用框架
- **Expo**: 开发构建工具链
- **React Navigation**: 导航管理
- **TypeScript**: 类型安全

### 后端服务
- **Supabase**: 
  - 用户认证 (Auth)
  - 数据库 (PostgreSQL)
  - 实时订阅
  - 行级安全 (RLS)

### 数据库设计
```sql
-- 用户资料表
profiles (
  id: UUID (主键)
  email: VARCHAR
  display_name: VARCHAR
  phone: VARCHAR
)

-- API配置表
api_configs (
  id: UUID (主键)
  user_id: UUID (外键)
  name: VARCHAR
  url: VARCHAR
  method: VARCHAR
  headers: JSONB
  is_default: BOOLEAN
)

-- 消息记录表
text_messages (
  id: UUID (主键)
  user_id: UUID (外键)
  content: TEXT
  api_config_id: UUID (外键)
  status: VARCHAR
  response_status: INTEGER
  response_data: JSONB
  error_message: TEXT
  sent_at: TIMESTAMP
)
```

## 🚀 快速开始

### 环境要求
- Node.js 18+
- Expo CLI
- iOS 模拟器 或 Android 设备/模拟器

### 1. 克隆项目
```bash
git clone [project-url]
cd InboxHub-app
```

### 2. 安装依赖
```bash
npm install
```

### 3. 配置 Supabase
参考 [`SUPABASE_SETUP.md`](./SUPABASE_SETUP.md) 进行详细配置：

1. 创建 Supabase 项目
2. 运行数据库迁移
3. 配置环境变量
4. 启用邮箱认证

### 4. 启动开发服务器
```bash
npm start
```

### 5. 运行应用
```bash
# iOS
npm run ios

# Android
npm run android

# Web (用于测试)
npm run web
```

## 📱 应用界面

### 登录/注册页面
- 邮箱密码登录
- 新用户注册
- 错误提示处理

### 首页 - 文本发送
- 文本输入框
- API 配置选择
- 发送按钮
- 实时状态反馈

### API配置页面
- 配置卡片列表
- 添加/编辑配置
- 删除确认
- 默认配置管理

### 我的页面
- 用户信息显示
- 在线编辑功能
- 退出登录

## 🔧 项目结构

```
src/
├── components/          # 可复用组件
│   ├── auth/           # 认证相关组件
│   └── common/         # 通用组件
├── contexts/           # React Context
│   └── AuthContext.tsx
├── lib/               # 工具库
│   └── supabase.ts    # Supabase 客户端
├── navigation/        # 导航配置
│   ├── index.tsx      # 主导航
│   └── screens/       # 页面组件
├── types/             # TypeScript 类型
│   └── database.ts    # 数据库类型
├── utils/             # 工具函数
│   └── errors.ts      # 错误处理
└── screens/           # 独立页面
    └── AuthScreen.tsx # 认证页面
```

## 🔒 安全特性

- **行级安全 (RLS)**: 用户只能访问自己的数据
- **JWT 认证**: 安全的会话管理
- **数据验证**: 客户端和服务端双重验证
- **环境变量**: 敏感信息保护

## 🌟 最佳实践

### 代码规范
- TypeScript 严格模式
- ESLint + Prettier 格式化
- 组件化设计
- 错误边界处理

### 性能优化
- React.memo 优化渲染
- useCallback 防止不必要重渲染
- 图片资源预加载
- 懒加载实现

### 用户体验
- 加载状态显示
- 错误信息友好提示
- 离线状态处理
- 手势和动画反馈

## 🔄 开发工作流

### 本地开发
```bash
# 启动开发服务器
npm start

# 运行类型检查
npx tsc --noEmit

# 运行代码格式化
npx prettier --write src/
```

### 构建发布
```bash
# 构建 iOS
npm run ios

# 构建 Android
npm run android
```

## 📋 待办功能

- [ ] 消息历史查看
- [ ] 批量消息发送
- [ ] 消息模板功能
- [ ] 定时发送
- [ ] 数据导入/导出
- [ ] 推送通知
- [ ] 离线模式
- [ ] 多语言支持

## 🐛 问题反馈

如果您在使用过程中遇到任何问题或有功能建议，请：

1. 查看 [常见问题](./FAQ.md)
2. 搜索现有 [Issues](../../issues)
3. 创建新的 [Issue](../../issues/new)

## 🤝 贡献指南

我们欢迎所有形式的贡献！请参考 [贡献指南](./CONTRIBUTING.md)。

### 提交 Pull Request
1. Fork 本仓库
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 📄 许可证

本项目采用 [MIT 许可证](./LICENSE)。

## 🙏 致谢

- [React Native](https://reactnative.dev/) - 跨平台移动开发框架
- [Expo](https://expo.dev/) - React Native 开发平台
- [Supabase](https://supabase.com/) - 开源 Firebase 替代方案
- [React Navigation](https://reactnavigation.org/) - React Native 导航库

---

<div align="center">
  <p>如果这个项目对您有帮助，请给它一个 ⭐️</p>
</div>

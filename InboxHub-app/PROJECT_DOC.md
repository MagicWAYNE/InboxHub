## 项目概述
InboxHub 是一个基于 React Native 的 Android 应用，主要功能是快速将特定文字内容发送到用户自定义的 API。

### 功能描述
- InboxHub应用的核心功能是能够将用户在输入框中输入的内容发送到用户预先配置的API。
- 应用可以存储用户的多个预配置的API，当用户输入内容后，可以通过不同的按钮发送到不同的API中。

### 页面功能

#### 0. 登录页
用户必须登录才可使用APP，用户可通过邮箱进行登录。

#### 1. 导航栏：位于每页的下方
1. 首页
2. API配置
3. 我的

#### 2. 首页
InboxHub的首页以一个输入框+发送按钮的组件作为核心。从上至下分别是：
1. 标题栏：包含应用名称、用户信息icon、提醒icon。
2. 主体1：纯文字提示“输入要发送的内容”。
3. 主体2：输入框+发送按钮的组件。、
	- 输入框中提示内容“输入文字或调用输入法语音识别输入”。（用户打开APP后，激活输入框调出键盘）
	- 输入框下方是一个下拉菜单和一个发送按钮：下拉菜单可选择用户预先存储的API；发送按钮的功能是将输入的内容发送到用户选择的API。

#### 3. API配置页
InboxHub的API配置页面以卡片形式呈现，页面内容：
1. （如用户配置了API）API卡片：展示用户给这个API的自定义名称、备注。
	- API卡片可以拖动排序，这个顺序决定了首页输入框下方API选项菜单的顺序。
	- API卡片上有编辑按钮，用户点击这个按钮可以编辑API卡片的配置，以表单形式呈现。用户需要设置的项目有：API配置名称、API端点URL、HTTP方法（默认POST）、JSON格式的请求头。
2. 新增配置按钮：
	- 该按钮默认位于最后一个API卡片的后方，当没有API卡片时，该卡片置顶。
	- 用户点击新增配置按钮可以新增API卡片并输入配置信息，以表单形式呈现。用户需要设置的项目有：API配置名称、API端点URL、HTTP方法（默认POST）、JSON格式的请求头。

#### 4. 我的页面
InboxHub的用户页面，用于显示用户信息，部分信息用户可编辑。页面内显示内容如下：
1. 用户邮箱：仅显示，不可编辑。
2. 用户昵称：可编辑。
3. 用户手机：可编辑。
4. 页面的下方配置登出按钮，用户可注销登录当前登录的账户。

### 技术栈
- **前端**: React Native + Expo
- **UI组件**: React Native Pager View
- **后端服务**: Supabase (认证 + 数据库)
- **开发语言**: TypeScript

#### 补充说明
- API的成功调试CURL信息如下：
'''
curl --location --request POST 'https://pn8n.alignengine.com/webhook/99168c36-fb61-443e-915e-65170dc855e5' \
--header 'User-Agent: Apifox/1.0.0 (https://apifox.com)' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.e30.y7iEzWXC29Cm8eQ83v2e-VyvAEXFmxtNJQinqbaZ1J0' \
--header 'Accept: */*' \
--header 'Host: pn8n.alignengine.com' \
--header 'Connection: keep-alive' \
--data-raw '{
    "taskContent": "20250721 测试"
}'
'''

### 数据库设计
创建了两个核心数据表：

**api_configs 表**
- `id`: UUID 主键
- `user_id`: 用户ID（外键关联 auth.users）
- `name`: API配置名称
- `url`: API端点URL
- `method`: HTTP方法（默认POST）
- `headers`: JSON格式的请求头
- `is_default`: 是否为默认配置
- `created_at/updated_at`: 时间戳

**text_messages 表**
- `id`: UUID 主键
- `user_id`: 用户ID
- `content`: 文本内容
- `api_config_id`: API配置ID（外键）
- `status`: 发送状态（pending/sent/failed）
- `response_status`: HTTP响应状态码
- `response_data`: 响应数据
- `error_message`: 错误信息
- `sent_at`: 发送时间
- `created_at`: 创建时间

#### 4. 用户认证系统
- **AuthContext**: `contexts/AuthContext.tsx` - 认证状态管理
- **认证表单**: `components/auth/AuthForm.tsx` - 登录/注册UI
- **认证屏幕**: `app/auth.tsx` - 认证页面
- **应用布局**: 修改 `app/_layout.tsx` 实现认证路由守卫
- **主页更新**: 更新 `app/(tabs)/index.tsx` 显示用户信息和退出功能
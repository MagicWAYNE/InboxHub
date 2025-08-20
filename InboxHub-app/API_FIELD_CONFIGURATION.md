# API 请求体字段配置说明

## 🎯 功能概述

InboxHub 现在支持为每个 API 配置自定义的请求体字段名，以适配不同 API 的要求。

## 📝 使用方法

### 1. 添加新 API 配置时
1. 进入 `API配置` 页面
2. 点击 `+ 新增配置` 按钮
3. 在表单中填写：
   - **API配置名称**: 给这个配置起个名字
   - **API地址**: 目标 API 的 URL
   - **HTTP方法**: 通常是 POST
   - **请求体字段名**: 🆕 **API 期望接收的字段名**
   - **请求头**: 如需要认证等
4. 保存配置

### 2. 编辑现有配置
1. 在 API 配置卡片上点击 `编辑` 按钮
2. 修改 `请求体字段名` 字段
3. 保存更改

## 🔧 常见字段名示例

不同的 API 可能期望不同的字段名：

| API 类型 | 字段名示例 | 请求体示例 |
|---------|-----------|------------|
| n8n Webhook | `taskContent` | `{"taskContent": "用户输入"}` |
| 通用消息 API | `content` | `{"content": "用户输入"}` |
| 聊天机器人 | `message` | `{"message": "用户输入"}` |
| 文本处理 | `text` | `{"text": "用户输入"}` |
| 自定义 API | `data` | `{"data": "用户输入"}` |

## 📋 配置示例

### 示例1: n8n Webhook
```
配置名称: n8n 任务创建
API地址: https://pn8n.alignengine.com/webhook/xxx
HTTP方法: POST
请求体字段名: taskContent
请求头: {"Authorization": "Bearer your-token"}
```

发送的数据格式：
```json
{
    "taskContent": "用户在输入框中输入的文本"
}
```

### 示例2: 通用消息 API
```
配置名称: 消息通知
API地址: https://api.example.com/notifications
HTTP方法: POST
请求体字段名: content
请求头: {"Content-Type": "application/json"}
```

发送的数据格式：
```json
{
    "content": "用户在输入框中输入的文本"
}
```

## 🔍 如何查看配置

在 API 配置页面，每个配置卡片会显示：
- 配置名称
- API URL
- HTTP 方法
- **字段名**: 显示当前配置的请求体字段名

## ✅ 测试配置

1. 配置完成后，返回首页
2. 在输入框中输入测试文本
3. 选择对应的 API 配置
4. 点击发送按钮
5. 检查 API 服务器是否收到正确格式的数据

## 🚨 故障排除

### 问题1: API 收到空数据
**原因**: 字段名不匹配
**解决**: 检查 API 文档，确认正确的字段名

### 问题2: 发送失败
**原因**: 可能是 URL、认证或其他配置问题
**解决**: 
1. 检查 API 地址是否正确
2. 验证请求头配置
3. 确认网络连接

### 问题3: 字段名不确定
**解决**: 
1. 查看 API 文档
2. 使用 curl 命令测试
3. 常见字段名尝试顺序: `taskContent` → `content` → `message` → `text`

## 📚 更多信息

- 查看 `PROJECT_DOC.md` 中的 curl 示例
- 参考 `SUPABASE_SETUP.md` 进行数据库配置
- 如有问题，请查看应用内的错误提示信息
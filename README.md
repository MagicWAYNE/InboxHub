# InboxHub

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp" alt="InboxHub Logo" width="100">
</p>

## 项目简介

InboxHub是一款简洁高效的Android信息收集应用，帮助用户快速记录和管理重要信息。应用支持文本输入和语音识别，提供直观美观的用户界面，让信息收集变得轻松愉快。

## 主要功能

- 📝 **文本输入**：简洁的输入界面，让记录信息快速便捷
- 🎤 **语音识别**：支持语音输入，解放双手
- 🚀 **即时发送**：一键发送信息，响应迅速
- 📱 **现代UI**：采用Material Design 3设计，美观直观
- 🔒 **隐私安全**：只请求必要权限，保护用户隐私

## 技术栈

- **Kotlin**：100% Kotlin编写，充分利用语言特性
- **Jetpack Compose**：现代化声明式UI框架
- **Material 3**：最新的Material Design组件和主题
- **MVVM架构**：清晰的代码组织和职责分离
- **Coroutines & Flow**：简化异步操作
- **Retrofit & OkHttp**：网络请求处理

## 屏幕截图

<!-- 待添加应用截图 -->

## 安装说明

### 系统要求
- Android 9.0 (API 29)及以上版本
- 支持ARM和x86处理器架构

### 安装步骤
1. 从Releases页面下载最新APK文件
2. 在设备上启用"未知来源"安装选项
3. 打开下载的APK文件进行安装

## 开发环境设置

1. 克隆仓库：
```bash
git clone https://github.com/MagicWAYNE/InboxHub.git
```

2. 使用Android Studio打开项目

3. 同步Gradle依赖

4. 运行应用

## 项目结构

```
app/src/main/
├── java/com/example/inboxhub/    # 主代码目录
│   ├── data/                     # 数据层
│   │   ├── api/                  # API服务和客户端
│   │   ├── model/                # 数据模型
│   │   └── repository/           # 数据仓库
│   ├── service/                  # 服务层
│   │   └── speech/               # 语音识别服务
│   ├── ui/                       # UI层
│   │   ├── components/           # 可复用组件
│   │   ├── main/                 # 主界面
│   │   └── theme/                # 主题定义
│   └── utils/                    # 工具类
└── res/                          # 资源文件
```

## 贡献指南

欢迎为InboxHub项目贡献代码！如果您想要贡献，请参考以下步骤：

1. Fork本仓库
2. 创建您的特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交您的更改 (`git commit -m '添加一些特性'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建一个Pull Request

## 许可证

本项目采用MIT许可证 - 详情请查看 [LICENSE](LICENSE) 文件

## 联系方式

MagicWAYNE - lunamoshe@gmail.com

项目链接：[https://github.com/MagicWAYNE/InboxHub](https://github.com/MagicWAYNE/InboxHub)

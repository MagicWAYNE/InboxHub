# 系统模式
## MVVM架构模式
应用采用MVVM（Model-View-ViewModel）架构模式，将界面和业务逻辑分离，便于测试和扩展。
### 组件划分
- **Model**：负责数据处理和业务逻辑，包括API服务接口和数据模型
- **View**：使用Jetpack Compose构建的UI组件，负责展示数据和接收用户输入
- **ViewModel**：连接Model和View，处理UI逻辑并暴露状态给视图层

package com.example.inboxhub.ui.main

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.inboxhub.data.model.ApiConfig
import com.example.inboxhub.ui.components.MessageInput
import com.example.inboxhub.ui.components.SendButton
import com.example.inboxhub.ui.components.StatusMessage

/**
 * 主屏幕组件
 *
 * @param viewModel 主界面ViewModel
 * @param onNavigateToSettings 导航到设置页面的回调
 * @param modifier 修饰符
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val apiConfigs by viewModel.apiConfigs.collectAsState(initial = emptyList())
    val currentApiConfig by viewModel.currentApiConfig.collectAsState(initial = null)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        // 确保Scaffold不会自动应用IME insets
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "InboxHub",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    // 设置按钮
                    IconButton(onClick = { 
                        Log.d("Navigation", "点击设置按钮，准备导航到设置页面")
                        onNavigateToSettings() 
                    }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "设置",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 显示没有配置的提示
            if (apiConfigs.isEmpty()) {
                NoConfigsMessage(
                    onNavigateToSettings = onNavigateToSettings,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }
            
            // 状态消息 - 显示在顶部
            if (uiState.errorMessage != null || uiState.isSuccess) {
                StatusMessage(
                    message = uiState.errorMessage ?: "发送成功！",
                    isError = uiState.errorMessage != null,
                    isVisible = true,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopCenter)
                )
            }

            // 底部输入区域 - 包含配置标签和输入框
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    // 添加ime padding确保内容不被键盘遮挡
                    .imePadding()
                    // 增加导航栏padding
                    .navigationBarsPadding()
            ) {
                // 配置标签行 - 位于输入框上方
                ConfigTabs(
                    configs = apiConfigs.take(3),
                    currentConfigId = currentApiConfig?.id,
                    onConfigSelected = { configId ->
                        viewModel.setCurrentApiConfig(configId)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
                
                // 消息输入框 - 放在标签行下方
                MessageInput(
                    value = uiState.messageContent,
                    onValueChange = viewModel::updateMessageContent,
                    placeholder = if (apiConfigs.isEmpty()) "请先添加API配置..." else "输入您要收集的信息...",
                    enabled = apiConfigs.isNotEmpty() && currentApiConfig != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp) // 为配置标签行留出空间
                )

                // 发送按钮 - 放置在右下角
                SendButton(
                    onClick = viewModel::sendMessage,
                    isSending = uiState.isSending,
                    isSuccess = uiState.isSuccess,
                    enabled = apiConfigs.isNotEmpty() && currentApiConfig != null,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 12.dp, end = 12.dp)
                )
            }
        }
    }
}

/**
 * 无配置信息提示组件
 */
@Composable
fun NoConfigsMessage(
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
            
            Text(
                text = "未找到API配置",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            
            Text(
                text = "请先在设置中添加API配置，以便发送信息",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            
            Button(
                onClick = onNavigateToSettings,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("前往设置")
            }
        }
    }
}

/**
 * 配置标签行组件
 */
@Composable
fun ConfigTabs(
    configs: List<ApiConfig>,
    currentConfigId: String?,
    onConfigSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (configs.isEmpty()) {
            // 如果没有配置，显示提示标签
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.LightGray.copy(alpha = 0.3f))
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "没有可用的API配置",
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            // 显示实际的配置标签
            configs.forEachIndexed { index, config ->
                val isSelected = config.id == currentConfigId
                
                ConfigTab(
                    name = config.name,
                    isSelected = isSelected,
                    onClick = { onConfigSelected(config.id) },
                    enabled = true,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // 如果实际配置少于3个，填充空白位置
            val emptySlots = 3 - configs.size
            repeat(emptySlots) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                )
            }
        }
    }
}

/**
 * 单个配置标签组件
 */
@Composable
fun ConfigTab(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        !enabled -> Color.LightGray.copy(alpha = 0.3f)
        isSelected -> MaterialTheme.colorScheme.primary
        else -> Color.LightGray.copy(alpha = 0.7f)
    }
    
    val textColor = when {
        !enabled -> Color.Gray.copy(alpha = 0.5f)
        isSelected -> Color.White
        else -> Color.Black.copy(alpha = 0.7f)
    }
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .then(
                if (enabled) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            color = textColor,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
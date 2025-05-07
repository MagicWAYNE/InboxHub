package com.example.inboxhub.ui.main

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.inboxhub.ui.components.MessageInput
import com.example.inboxhub.ui.components.SendButton
import com.example.inboxhub.ui.components.SpeechButton
import com.example.inboxhub.ui.components.StatusMessage
import com.example.inboxhub.utils.PermissionUtils

/**
 * 主屏幕组件
 * 
 * @param viewModel 主界面ViewModel
 * @param modifier 修饰符
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var needsPermission by remember { mutableStateOf(false) }
    
    // 权限请求启动器
    val permissionLauncher = PermissionUtils.rememberRecordAudioPermissionLauncher { isGranted ->
        if (isGranted) {
            viewModel.startSpeechRecognition()
        } else {
            viewModel.onPermissionDenied()
        }
    }
    
    // 检查权限状态
    LaunchedEffect(Unit) {
        needsPermission = !PermissionUtils.isRecordAudioPermissionGranted(context)
    }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        // 确保Scaffold不会自动应用IME insets
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "TODOsender",
                        style = MaterialTheme.typography.titleLarge
                    )
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
            
            // 底部输入区域 - 包含输入框和按钮
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    // 添加ime padding确保内容不被键盘遮挡
                    .imePadding()
                    // 增加导航栏padding
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 消息输入
                MessageInput(
                    value = uiState.messageContent,
                    onValueChange = viewModel::updateMessageContent,
                    partialSpeechResult = uiState.partialSpeechResult
                )
                
                // 按钮行
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        // 确保按钮之间有足够间距
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 语音按钮
                    SpeechButton(
                        modifier = Modifier.weight(1f),
                        onLongPress = {
                            if (needsPermission) {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            } else {
                                viewModel.startSpeechRecognition()
                            }
                        },
                        onLongPressRelease = viewModel::stopSpeechRecognition,
                        isListening = uiState.isListening
                    )
                    
                    // 发送按钮
                    SendButton(
                        onClick = viewModel::sendMessage,
                        isSending = uiState.isSending,
                        isSuccess = uiState.isSuccess
                    )
                }
            }
        }
    }
} 
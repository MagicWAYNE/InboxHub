package com.example.inboxhub.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.inboxhub.data.repository.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 主界面ViewModel，管理UI状态和业务逻辑
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "MainViewModel"
    private val messageRepository = MessageRepository()

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    /**
     * 更新消息内容
     *
     * @param text 新的消息内容
     */
    fun updateMessageContent(text: String) {
        _uiState.update { it.copy(messageContent = text) }
    }

    /**
     * 发送消息
     */
    fun sendMessage() {
        val content = _uiState.value.messageContent.trim()
        if (content.isEmpty()) return

        Log.d(TAG, "开始发送消息: $content")
        _uiState.update { it.copy(isSending = true, errorMessage = null) }

        viewModelScope.launch {
            Log.d(TAG, "调用消息仓库发送消息")
            val result = messageRepository.sendMessage(content)

            result.fold(
                onSuccess = { responseOutput ->
                    Log.d(TAG, "消息发送成功，响应: $responseOutput")
                    _uiState.update {
                        it.copy(
                            isSending = false,
                            messageContent = "",
                            isSuccess = true,
                            errorMessage = null
                        )
                    }

                    // 重置成功状态，让UI可以在短暂显示后恢复
                    launch {
                        kotlinx.coroutines.delay(2000)
                        _uiState.update { it.copy(isSuccess = false) }
                    }
                },
                onFailure = { throwable ->
                    Log.e(TAG, "消息发送失败", throwable)
                    _uiState.update {
                        it.copy(
                            isSending = false,
                            isSuccess = false,
                            errorMessage = throwable.message ?: "发送失败"
                        )
                    }
                }
            )
        }
    }

    /**
     * 清除错误消息
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
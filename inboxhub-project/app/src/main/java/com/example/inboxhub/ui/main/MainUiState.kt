package com.example.inboxhub.ui.main

/**
 * 主界面UI状态数据类
 * 
 * @property messageContent 消息内容
 * @property isSending 是否正在发送
 * @property isSuccess 是否发送成功
 * @property errorMessage 错误消息
 * @property isListening 是否正在进行语音识别
 * @property partialSpeechResult 部分语音识别结果
 */
data class MainUiState(
    val messageContent: String = "",
    val isSending: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val isListening: Boolean = false,
    val partialSpeechResult: String = ""
) 
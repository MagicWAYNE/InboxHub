package com.example.todosender.service.speech

/**
 * 语音识别错误类型封装
 */
sealed class SpeechRecognitionError {
    /**
     * 权限错误：用户未授予麦克风权限
     */
    object Permission : SpeechRecognitionError()
    
    /**
     * 网络错误：网络连接问题
     */
    object Network : SpeechRecognitionError()
    
    /**
     * 服务不可用：语音识别服务不可用
     */
    object ServiceUnavailable : SpeechRecognitionError()
    
    /**
     * 其他错误
     * 
     * @property message 错误信息
     */
    data class Other(val message: String) : SpeechRecognitionError()
}
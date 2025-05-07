package com.example.inboxhub.service.speech

/**
 * 语音识别回调接口，定义语音识别过程中的事件回调
 */
interface SpeechRecognitionCallback {
    /**
     * 识别结果回调
     * 
     * @param text 识别出的完整文本
     */
    fun onResult(text: String)
    
    /**
     * 临时识别结果回调，用于实时反馈
     * 
     * @param text 当前识别出的部分文本
     */
    fun onPartialResult(text: String)
    
    /**
     * 错误回调
     * 
     * @param error 错误类型
     */
    fun onError(error: SpeechRecognitionError)
    
    /**
     * 开始识别回调
     */
    fun onStart()
    
    /**
     * 结束识别回调
     */
    fun onEnd()
} 
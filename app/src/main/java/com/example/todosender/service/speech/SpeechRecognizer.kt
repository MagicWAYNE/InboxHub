package com.example.todosender.service.speech

/**
 * 语音识别接口，定义语音识别的基本操作
 */
interface SpeechRecognizer {
    /**
     * 开始监听语音输入
     * 
     * @param callback 语音识别回调
     */
    fun startListening(callback: SpeechRecognitionCallback)
    
    /**
     * 停止监听，完成识别
     */
    fun stopListening()
    
    /**
     * 取消识别
     */
    fun cancel()
    
    /**
     * 检查语音识别服务是否可用
     * 
     * @return 服务是否可用
     */
    fun isAvailable(): Boolean
} 
package com.example.todosender.service.speech

import android.content.Context

/**
 * 语音识别管理器，负责管理和协调不同的语音识别实现
 * 
 * @property context 应用上下文
 */
class SpeechRecognitionManager(private val context: Context) {
    
    private var recognizer: SpeechRecognizer? = null
    private var currentCallback: SpeechRecognitionCallback? = null
    
    /**
     * 创建语音识别器
     * 当前版本仅实现Android原生语音识别，后续将集成讯飞SDK
     * 
     * @return 语音识别器实例
     */
    private fun createSpeechRecognizer(): SpeechRecognizer {
        // 未来将检查讯飞SDK是否可用，优先使用讯飞SDK
        // 当前默认使用Android原生语音识别
        return AndroidSpeechRecognizer(context)
    }
    
    /**
     * 开始语音识别
     * 
     * @param callback 语音识别回调
     */
    fun startRecognition(callback: SpeechRecognitionCallback) {
        this.currentCallback = callback
        
        if (recognizer == null) {
            recognizer = createSpeechRecognizer()
        }
        
        val wrappedCallback = object : SpeechRecognitionCallback {
            override fun onResult(text: String) {
                currentCallback?.onResult(text)
            }
            
            override fun onPartialResult(text: String) {
                currentCallback?.onPartialResult(text)
            }
            
            override fun onError(error: SpeechRecognitionError) {
                when (error) {
                    is SpeechRecognitionError.ServiceUnavailable -> {
                        // 如果当前识别器不可用，可以在这里尝试切换到备选识别器
                        // 未来实现
                        currentCallback?.onError(error)
                    }
                    else -> currentCallback?.onError(error)
                }
            }
            
            override fun onStart() {
                currentCallback?.onStart()
            }
            
            override fun onEnd() {
                currentCallback?.onEnd()
            }
        }
        
        recognizer?.startListening(wrappedCallback)
    }
    
    /**
     * 停止语音识别
     */
    fun stopRecognition() {
        recognizer?.stopListening()
    }
    
    /**
     * 取消语音识别
     */
    fun cancelRecognition() {
        recognizer?.cancel()
    }
    
    /**
     * 检查语音识别是否可用
     * 
     * @return 语音识别是否可用
     */
    fun isRecognitionAvailable(): Boolean {
        if (recognizer == null) {
            recognizer = createSpeechRecognizer()
        }
        return recognizer?.isAvailable() ?: false
    }
} 
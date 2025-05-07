package com.example.todosender.service.speech

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer as AndroidSpeech

/**
 * Android原生语音识别实现
 * 
 * @property context 应用上下文
 */
class AndroidSpeechRecognizer(private val context: Context) : SpeechRecognizer {
    
    private var speechRecognizer: AndroidSpeech? = null
    private var callback: SpeechRecognitionCallback? = null
    
    /**
     * 初始化语音识别器
     */
    private fun initializeSpeechRecognizer() {
        if (speechRecognizer == null) {
            speechRecognizer = AndroidSpeech.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(createRecognitionListener())
        }
    }
    
    /**
     * 创建语音识别监听器
     * 
     * @return 语音识别监听器
     */
    private fun createRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                callback?.onStart()
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                callback?.onEnd()
            }

            override fun onError(error: Int) {
                val speechError = when (error) {
                    AndroidSpeech.ERROR_INSUFFICIENT_PERMISSIONS -> SpeechRecognitionError.Permission
                    AndroidSpeech.ERROR_NETWORK, AndroidSpeech.ERROR_NETWORK_TIMEOUT -> SpeechRecognitionError.Network
                    AndroidSpeech.ERROR_CLIENT, AndroidSpeech.ERROR_SERVER -> SpeechRecognitionError.ServiceUnavailable
                    else -> SpeechRecognitionError.Other("错误代码: $error")
                }
                callback?.onError(speechError)
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(AndroidSpeech.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    callback?.onResult(matches[0])
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(AndroidSpeech.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    callback?.onPartialResult(matches[0])
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }
    
    /**
     * 创建语音识别意图
     * 
     * @return 语音识别意图
     */
    private fun createRecognizerIntent(): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh-CN")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
    }

    override fun startListening(callback: SpeechRecognitionCallback) {
        this.callback = callback
        
        if (!isAvailable()) {
            callback.onError(SpeechRecognitionError.ServiceUnavailable)
            return
        }
        
        initializeSpeechRecognizer()
        speechRecognizer?.startListening(createRecognizerIntent())
    }

    override fun stopListening() {
        speechRecognizer?.stopListening()
    }

    override fun cancel() {
        speechRecognizer?.cancel()
    }

    override fun isAvailable(): Boolean {
        val manager = context.packageManager
        val available = AndroidSpeech.isRecognitionAvailable(context)
        val hasRecordPermission = manager.checkPermission(
            android.Manifest.permission.RECORD_AUDIO,
            context.packageName
        ) == PackageManager.PERMISSION_GRANTED
        
        return available && hasRecordPermission
    }
} 
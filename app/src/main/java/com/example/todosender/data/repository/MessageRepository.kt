package com.example.todosender.data.repository

import android.util.Log
import com.example.todosender.data.api.ApiClient
import com.example.todosender.data.api.ApiService
import com.example.todosender.data.model.CozeParameters
import com.example.todosender.data.model.CozeRequest
import com.example.todosender.data.model.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 消息仓库类，负责管理消息数据的存储和获取
 */
class MessageRepository {
    private val TAG = "MessageRepository"
    private val apiService: ApiService = ApiClient.apiService
    private val workflowId = "7499000986859257890" // Coze工作流ID

    /**
     * 发送消息到Coze API
     *
     * @param content 消息内容
     * @return 处理结果
     */
    suspend fun sendMessage(content: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "发送消息到Coze API: $content")
                
                // 创建Coze请求
                val request = CozeRequest(
                    parameters = CozeParameters(input = content),
                    workflow_id = workflowId
                )
                
                // 发送请求到Coze API
                Log.d(TAG, "正在发送请求: $request")
                val response = apiService.sendToCoze(request)
                Log.d(TAG, "收到响应: $response")
                
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d(TAG, "响应体: $body")
                    
                    // 检查是否有错误
                    if (body?.error != null) {
                        val errorMsg = "API错误: ${body.error.message ?: "未知错误"}"
                        Log.e(TAG, errorMsg)
                        Result.failure(Exception(errorMsg))
                    } else {
                        // 返回成功结果
                        val output = body?.result?.output ?: "请求成功"
                        Log.d(TAG, "请求成功: $output")
                        Result.success(output)
                    }
                } else {
                    val errorMsg = "发送失败: ${response.code()} ${response.message()}"
                    Log.e(TAG, errorMsg)
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Log.e(TAG, "请求异常", e)
                Result.failure(e)
            }
        }
    }
} 
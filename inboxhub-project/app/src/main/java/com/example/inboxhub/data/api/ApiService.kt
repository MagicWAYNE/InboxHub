package com.example.inboxhub.data.api

import com.example.inboxhub.data.model.CozeRequest
import com.example.inboxhub.data.model.CozeResponse
import com.example.inboxhub.data.model.Message
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * API服务接口，定义与后端的通信方法
 */
interface ApiService {
    /**
     * 发送消息到服务器
     *
     * @param message 要发送的消息对象
     * @return 服务器响应
     */
    @POST("todos")
    suspend fun sendMessage(@Body message: Message): Response<Any>
    
    /**
     * 发送请求到Coze API
     *
     * @param request Coze请求对象
     * @return Coze响应
     */
    @POST("v1/workflow/run")
    suspend fun sendToCoze(@Body request: CozeRequest): Response<CozeResponse>
} 
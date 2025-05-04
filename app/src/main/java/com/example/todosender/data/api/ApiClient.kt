package com.example.todosender.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * API客户端，配置Retrofit实例
 */
object ApiClient {
    // Coze API地址
    private const val BASE_URL = "https://api.coze.cn/"
    
    // Coze API密钥，实际应用中应从安全存储中获取
    private const val API_KEY = "pat_boTjs5Mpmi1ewcAIEE9QL0CuThpzyf9fxzIfqW0WSf9wQyKAJF3ZZyrQCuAmhoUQ"
    
    /**
     * 创建OkHttpClient实例，添加认证头和日志拦截器
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Authorization", "Bearer $API_KEY")
                .header("Content-Type", "application/json")
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * 创建Retrofit实例
     */
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /**
     * 创建API服务实例
     */
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
} 
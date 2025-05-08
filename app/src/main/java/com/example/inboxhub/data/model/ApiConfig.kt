package com.example.inboxhub.data.model

import kotlinx.serialization.Serializable

/**
 * API配置数据类，包含API连接所需的所有信息
 *
 * @property id 配置的唯一标识
 * @property name 用户友好的名称
 * @property baseUrl API基础URL
 * @property apiKey API密钥
 * @property workflowId Coze工作流ID
 * @property isDefault 是否为默认配置
 */
@Serializable
data class ApiConfig(
    val id: String, // 使用UUID生成
    val name: String,
    val baseUrl: String,
    val apiKey: String,
    val workflowId: String,
    val isDefault: Boolean = false
) {
    companion object {
        /**
         * 创建默认配置
         */
        fun createDefault() = ApiConfig(
            id = "default",
            name = "默认配置",
            baseUrl = "https://api.coze.cn/",
            apiKey = "pat_boTjs5Mpmi1ewcAIEE9QL0CuThpzyf9fxzIfqW0WSf9wQyKAJF3ZZyrQCuAmhoUQ",
            workflowId = "7499000986859257890",
            isDefault = true
        )
    }
} 
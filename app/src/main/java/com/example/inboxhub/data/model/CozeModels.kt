package com.example.inboxhub.data.model

/**
 * Coze API请求模型
 * 
 * @property parameters 请求参数
 * @property workflow_id 工作流ID
 */
data class CozeRequest(
    val parameters: CozeParameters,
    val workflow_id: String
)

/**
 * Coze API请求参数
 * 
 * @property input 输入内容
 */
data class CozeParameters(
    val input: String
)

/**
 * Coze API响应模型
 * 
 * @property result 响应结果
 */
data class CozeResponse(
    val result: CozeResult? = null,
    val error: CozeError? = null
)

/**
 * Coze API响应结果
 * 
 * @property output 输出内容
 */
data class CozeResult(
    val output: String? = null
)

/**
 * Coze API错误信息
 * 
 * @property message 错误消息
 * @property code 错误代码
 */
data class CozeError(
    val message: String? = null,
    val code: String? = null
) 
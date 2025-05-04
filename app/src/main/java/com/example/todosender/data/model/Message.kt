package com.example.todosender.data.model

/**
 * 表示要发送的消息的数据模型
 *
 * @property content 消息内容
 * @property timestamp 消息创建时间戳
 */
data class Message(
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
) 
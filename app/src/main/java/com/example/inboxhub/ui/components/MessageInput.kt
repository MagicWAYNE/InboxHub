package com.example.inboxhub.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp

/**
 * 消息输入组件
 *
 * @param value 输入内容
 * @param onValueChange 输入内容变化回调
 * @param modifier 修饰符
 * @param placeholder 占位文本
 * @param enabled 是否启用输入框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "输入待办事项",
    enabled: Boolean = true
) {
    // 创建一个焦点请求器
    val focusRequester = remember { FocusRequester() }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp)
                .height(120.dp) // 设置为更高的高度，适合3行内容
                .focusRequester(focusRequester), // 添加焦点请求器
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            enabled = enabled, // 使用参数控制是否启用
            minLines = 3, // 最小3行
            maxLines = 5, // 最大5行
            shape = RoundedCornerShape(8.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)
        )
    }

    // 只有在组件启用时才请求焦点
    if (enabled) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}
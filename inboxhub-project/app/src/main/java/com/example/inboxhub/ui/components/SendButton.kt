package com.example.inboxhub.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 发送按钮组件
 * 
 * @param onClick 点击回调
 * @param isSending 是否正在发送
 * @param isSuccess 是否发送成功
 * @param modifier 修饰符
 */
@Composable
fun SendButton(
    onClick: () -> Unit,
    isSending: Boolean,
    isSuccess: Boolean,
    modifier: Modifier = Modifier
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isSending) 45f else 0f,
        label = "rotation"
    )
    
    FloatingActionButton(
        onClick = { if (!isSending && !isSuccess) onClick() },
        containerColor = when {
            isSuccess -> MaterialTheme.colorScheme.primary
            isSending -> MaterialTheme.colorScheme.surfaceVariant
            else -> MaterialTheme.colorScheme.primary
        },
        contentColor = if (isSending) MaterialTheme.colorScheme.primary else Color.White,
        modifier = modifier.size(56.dp)
    ) {
        when {
            isSuccess -> {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "发送成功",
                    modifier = Modifier.size(24.dp)
                )
            }
            isSending -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp
                )
            }
            else -> {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = "发送消息",
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotationAngle)
                )
            }
        }
    }
} 
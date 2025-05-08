package com.example.inboxhub.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
 * @param enabled 是否启用按钮
 * @param modifier 修饰符
 */
@Composable
fun SendButton(
    onClick: () -> Unit,
    isSending: Boolean,
    isSuccess: Boolean,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isSending) 45f else 0f,
        label = "rotation"
    )
    
    val isButtonEnabled = enabled && !isSending && !isSuccess
    
    FloatingActionButton(
        onClick = { if (isButtonEnabled) onClick() },
        containerColor = when {
            !enabled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            isSuccess -> MaterialTheme.colorScheme.primary
            isSending -> MaterialTheme.colorScheme.surfaceVariant
            else -> MaterialTheme.colorScheme.primary
        },
        contentColor = when {
            !enabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            isSending -> MaterialTheme.colorScheme.primary
            else -> Color.White
        },
        modifier = modifier.size(48.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        when {
            isSuccess -> {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "发送成功",
                    modifier = Modifier.size(22.dp)
                )
            }
            isSending -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp
                )
            }
            else -> {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = "发送消息",
                    modifier = Modifier
                        .size(22.dp)
                        .rotate(rotationAngle)
                )
            }
        }
    }
} 
package com.example.todosender.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

/**
 * 语音按钮组件
 * 
 * @param onLongPress 长按开始语音识别回调
 * @param onLongPressRelease 长按释放停止语音识别回调
 * @param isListening 是否正在进行语音识别
 * @param modifier 修饰符
 */
@Composable
fun SpeechButton(
    onLongPress: () -> Unit,
    onLongPressRelease: () -> Unit,
    isListening: Boolean,
    modifier: Modifier = Modifier
) {
    var isPressing by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressing) 1.1f else 1f,
        label = "scale"
    )
    
    Button(
        onClick = { /* 只响应长按，这里不处理点击 */ },
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { pressPosition ->
                        isPressing = true
                        try {
                            awaitRelease()
                        } finally {
                            isPressing = false
                            onLongPressRelease()
                        }
                    },
                    onLongPress = {
                        onLongPress()
                    }
                )
            },
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = if (isListening) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
        )
    ) {
        Text(
            text = "语音识别",
            color = androidx.compose.ui.graphics.Color.White
        )
    }
} 
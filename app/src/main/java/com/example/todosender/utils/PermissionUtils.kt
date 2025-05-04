package com.example.todosender.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat

/**
 * 权限工具类，处理应用权限相关逻辑
 */
object PermissionUtils {
    
    /**
     * 检查麦克风权限是否已授予
     * 
     * @param context 上下文
     * @return 权限是否已授予
     */
    fun isRecordAudioPermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Compose组件中请求麦克风权限的工具函数
     * 
     * @param onPermissionResult 权限请求结果回调
     * @return 权限请求启动器
     */
    @Composable
    fun rememberRecordAudioPermissionLauncher(
        onPermissionResult: (Boolean) -> Unit
    ) = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onPermissionResult(isGranted)
    }
    
    /**
     * 请求所有应用所需的权限
     * 
     * @param context 上下文
     * @param onAllPermissionsGranted 所有权限已授予回调
     * @param onPermissionDenied 权限被拒绝回调
     */
    @Composable
    fun rememberAppPermissionsLauncher(
        onAllPermissionsGranted: () -> Unit,
        onPermissionDenied: () -> Unit
    ) = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            onAllPermissionsGranted()
        } else {
            onPermissionDenied()
        }
    }
} 
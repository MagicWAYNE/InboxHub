package com.example.inboxhub.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.inboxhub.ui.main.MainScreen
import com.example.inboxhub.ui.main.MainViewModel
import com.example.inboxhub.ui.settings.SettingsScreen
import com.example.inboxhub.ui.settings.SettingsViewModel

/**
 * 应用导航路由
 */
object Routes {
    const val MAIN = "main"
    const val SETTINGS = "settings"
}

/**
 * 应用导航组件
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    // 创建ViewModel
    Log.d("Navigation", "初始化MainViewModel")
    val mainViewModel: MainViewModel = viewModel()
    Log.d("Navigation", "初始化SettingsViewModel")
    val settingsViewModel: SettingsViewModel = viewModel()
    
    NavHost(
        navController = navController,
        startDestination = Routes.MAIN
    ) {
        // 主屏幕
        composable(Routes.MAIN) {
            Log.d("Navigation", "导航到主屏幕")
            MainScreen(
                viewModel = mainViewModel,
                onNavigateToSettings = {
                    Log.d("Navigation", "从主屏幕导航到设置页面")
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }
        
        // 设置页面
        composable(Routes.SETTINGS) {
            Log.d("Navigation", "导航到设置页面")
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = {
                    Log.d("Navigation", "从设置页面返回")
                    navController.popBackStack()
                }
            )
        }
    }
} 
package com.example.inboxhub.ui.settings

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.inboxhub.data.model.ApiConfig

/**
 * 设置页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val TAG = "SettingsScreen"
    Log.d(TAG, "SettingsScreen组件创建")
    
    // 使用空列表作为初始值，避免空指针
    val apiConfigs by viewModel.apiConfigs.collectAsState(initial = emptyList())
    Log.d(TAG, "收集到API配置列表，数量: ${apiConfigs.size}")
    
    // 使用null作为初始值，表示没有当前配置
    val currentApiConfig by viewModel.currentApiConfig.collectAsState(initial = null)
    Log.d(TAG, "收集到当前API配置: ${currentApiConfig?.name ?: "无配置"}")
    
    val uiState by viewModel.uiState.collectAsState()
    Log.d(TAG, "收集到UI状态: isEditing=${uiState.isEditing}, editingConfig=${uiState.editingConfig?.name ?: "null"}")
    
    // 确认删除对话框状态
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var configToDelete by remember { mutableStateOf<ApiConfig?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("API设置", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {
                        Log.d(TAG, "点击返回按钮")
                        onNavigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            // 添加新配置的浮动按钮
            FloatingActionButton(
                onClick = { 
                    Log.d(TAG, "点击添加新配置按钮")
                    viewModel.startCreateNewConfig() 
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加新配置",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 标题
            Text(
                text = "API配置列表",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (apiConfigs.isEmpty()) {
                // 没有配置时显示提示
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无API配置，请点击右下角按钮添加",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // API配置列表
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Log.d(TAG, "渲染API配置列表，数量: ${apiConfigs.size}")
                    items(apiConfigs) { config ->
                        ApiConfigItem(
                            config = config,
                            isSelected = currentApiConfig?.id == config.id,
                            onSelect = { 
                                Log.d(TAG, "选择配置: ${config.name}")
                                viewModel.setCurrentApiConfig(config.id) 
                            },
                            onEdit = { 
                                Log.d(TAG, "编辑配置: ${config.name}")
                                viewModel.startEditConfig(config) 
                            },
                            onDelete = {
                                Log.d(TAG, "准备删除配置: ${config.name}")
                                configToDelete = config
                                showDeleteConfirmDialog = true
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
    
    // 显示编辑对话框
    if (uiState.isEditing) {
        uiState.editingConfig?.let { config ->
            Log.d(TAG, "显示编辑对话框: ${config.name}")
            ApiConfigEditDialog(
                apiConfig = config,
                onUpdate = { name, baseUrl, apiKey, workflowId, isDefault ->
                    Log.d(TAG, "更新配置: $name")
                    viewModel.updateEditingConfig(name, baseUrl, apiKey, workflowId, isDefault)
                },
                onDismiss = { 
                    Log.d(TAG, "取消编辑")
                    viewModel.cancelEdit() 
                },
                onSave = { 
                    Log.d(TAG, "保存编辑")
                    viewModel.finishEdit() 
                }
            )
        }
    }
    
    // 显示删除确认对话框
    if (showDeleteConfirmDialog && configToDelete != null) {
        Log.d(TAG, "显示删除确认对话框: ${configToDelete?.name}")
        AlertDialog(
            onDismissRequest = { 
                Log.d(TAG, "取消删除")
                showDeleteConfirmDialog = false
                configToDelete = null
            },
            title = { Text("确认删除") },
            text = { Text("确定要删除 ${configToDelete?.name} 配置吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        configToDelete?.id?.let { 
                            Log.d(TAG, "确认删除配置: ${configToDelete?.name}")
                            viewModel.deleteApiConfig(it) 
                        }
                        showDeleteConfirmDialog = false
                        configToDelete = null
                    }
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        Log.d(TAG, "取消删除对话框")
                        showDeleteConfirmDialog = false
                        configToDelete = null
                    }
                ) {
                    Text("取消")
                }
            }
        )
    }
}

/**
 * API配置项
 */
@Composable
fun ApiConfigItem(
    config: ApiConfig,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 选择指示器
            RadioButton(
                selected = isSelected,
                onClick = onSelect
            )
            
            // 配置信息
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = config.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Base URL: ${config.baseUrl}",
                    style = MaterialTheme.typography.bodySmall
                )
                if (config.isDefault) {
                    Text(
                        text = "默认配置",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // 操作按钮
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "编辑",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * API配置编辑对话框
 */
@Composable
fun ApiConfigEditDialog(
    apiConfig: ApiConfig,
    onUpdate: (name: String, baseUrl: String, apiKey: String, workflowId: String, isDefault: Boolean) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    var name by remember { mutableStateOf(apiConfig.name) }
    var baseUrl by remember { mutableStateOf(apiConfig.baseUrl) }
    var apiKey by remember { mutableStateOf(apiConfig.apiKey) }
    var workflowId by remember { mutableStateOf(apiConfig.workflowId) }
    var isDefault by remember { mutableStateOf(apiConfig.isDefault) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Determine if this is a new config or editing an existing one
                val isNewConfig = apiConfig.name == "新配置" && apiConfig.apiKey.isEmpty() && apiConfig.workflowId.isEmpty()
                
                // 标题
                Text(
                    text = if (isNewConfig) "创建新配置" else "编辑配置",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // 输入字段
                OutlinedTextField(
                    value = name,
                    onValueChange = { 
                        name = it
                        onUpdate(it, baseUrl, apiKey, workflowId, isDefault)
                    },
                    label = { Text("配置名称") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                
                OutlinedTextField(
                    value = baseUrl,
                    onValueChange = { 
                        baseUrl = it
                        onUpdate(name, it, apiKey, workflowId, isDefault)
                    },
                    label = { Text("API基础URL") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { 
                        apiKey = it
                        onUpdate(name, baseUrl, it, workflowId, isDefault)
                    },
                    label = { Text("API密钥") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                
                OutlinedTextField(
                    value = workflowId,
                    onValueChange = { 
                        workflowId = it
                        onUpdate(name, baseUrl, apiKey, it, isDefault)
                    },
                    label = { Text("工作流ID") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                
                // 默认配置选项
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isDefault,
                        onCheckedChange = { 
                            isDefault = it
                            onUpdate(name, baseUrl, apiKey, workflowId, it)
                        }
                    )
                    Text("设为默认配置")
                }
                
                // 操作按钮
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onSave) {
                        Text("保存")
                    }
                }
            }
        }
    }
} 
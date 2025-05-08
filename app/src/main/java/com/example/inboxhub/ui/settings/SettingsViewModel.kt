package com.example.inboxhub.ui.settings

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.inboxhub.data.model.ApiConfig
import com.example.inboxhub.data.repository.ApiConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 设置页面ViewModel，管理API配置列表和操作
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val TAG = "SettingsViewModel"
    
    init {
        Log.d(TAG, "SettingsViewModel初始化")
    }
    
    private val apiConfigRepository = ApiConfigRepository.getInstance(application)
    
    // 获取所有API配置
    val apiConfigs: Flow<List<ApiConfig>> = apiConfigRepository.apiConfigs
    
    // 当前选中的API配置，可能为null
    val currentApiConfig: Flow<ApiConfig?> = apiConfigRepository.currentApiConfig
    
    // UI状态
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    /**
     * 设置当前活动的API配置
     */
    fun setCurrentApiConfig(configId: String) {
        Log.d(TAG, "设置当前配置ID: $configId")
        viewModelScope.launch {
            try {
                apiConfigRepository.setCurrentApiConfig(configId)
                Log.d(TAG, "当前配置已成功设置为: $configId")
            } catch (e: Exception) {
                Log.e(TAG, "设置当前配置失败", e)
            }
        }
    }
    
    /**
     * 删除API配置
     */
    fun deleteApiConfig(configId: String) {
        Log.d(TAG, "删除配置ID: $configId")
        viewModelScope.launch {
            try {
                apiConfigRepository.deleteApiConfig(configId)
                Log.d(TAG, "配置已成功删除: $configId")
            } catch (e: Exception) {
                Log.e(TAG, "删除配置失败", e)
            }
        }
    }
    
    /**
     * 添加或更新API配置
     */
    fun saveApiConfig(config: ApiConfig) {
        Log.d(TAG, "保存配置: ${config.name}, ID: ${config.id}")
        viewModelScope.launch {
            try {
                apiConfigRepository.saveApiConfig(config)
                Log.d(TAG, "配置已成功保存: ${config.name}")
            } catch (e: Exception) {
                Log.e(TAG, "保存配置失败", e)
            }
        }
    }
    
    /**
     * 生成新配置ID
     */
    fun generateConfigId(): String {
        val newId = apiConfigRepository.generateConfigId()
        Log.d(TAG, "生成新配置ID: $newId")
        return newId
    }
    
    /**
     * 设置当前编辑的配置
     */
    fun setEditingConfig(config: ApiConfig?) {
        Log.d(TAG, "设置编辑配置: ${config?.name ?: "null"}")
        _uiState.update { it.copy(editingConfig = config) }
    }
    
    /**
     * 开始创建新的API配置
     */
    fun startCreateNewConfig() {
        Log.d(TAG, "开始创建新配置")
        val newId = apiConfigRepository.generateConfigId()
        val newConfig = ApiConfig(
            id = newId,
            name = "新配置",
            baseUrl = "https://api.coze.cn/",
            apiKey = "",
            workflowId = "",
            isDefault = false
        )
        Log.d(TAG, "创建新配置初始值: $newConfig")
        _uiState.update { it.copy(editingConfig = newConfig, isEditing = true) }
    }
    
    /**
     * 开始编辑现有配置
     */
    fun startEditConfig(config: ApiConfig) {
        Log.d(TAG, "开始编辑配置: ${config.name}, ID: ${config.id}")
        _uiState.update { it.copy(editingConfig = config, isEditing = true) }
    }
    
    /**
     * 取消编辑配置
     */
    fun cancelEdit() {
        Log.d(TAG, "取消编辑配置")
        _uiState.update { it.copy(editingConfig = null, isEditing = false) }
    }
    
    /**
     * 完成编辑配置
     */
    fun finishEdit() {
        val editingConfig = _uiState.value.editingConfig
        Log.d(TAG, "完成编辑配置: ${editingConfig?.name ?: "null"}")
        if (editingConfig != null) {
            viewModelScope.launch {
                try {
                    apiConfigRepository.saveApiConfig(editingConfig)
                    Log.d(TAG, "编辑配置已保存: ${editingConfig.name}")
                    _uiState.update { it.copy(editingConfig = null, isEditing = false) }
                } catch (e: Exception) {
                    Log.e(TAG, "保存编辑配置失败", e)
                }
            }
        }
    }
    
    /**
     * 更新编辑中的配置字段
     */
    fun updateEditingConfig(
        name: String? = null,
        baseUrl: String? = null,
        apiKey: String? = null,
        workflowId: String? = null,
        isDefault: Boolean? = null
    ) {
        val current = _uiState.value.editingConfig ?: return
        val updated = current.copy(
            name = name ?: current.name,
            baseUrl = baseUrl ?: current.baseUrl,
            apiKey = apiKey ?: current.apiKey,
            workflowId = workflowId ?: current.workflowId,
            isDefault = isDefault ?: current.isDefault
        )
        Log.d(TAG, "更新编辑配置: ${current.name} -> ${updated.name}")
        _uiState.update { it.copy(editingConfig = updated) }
    }
    
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "SettingsViewModel已销毁")
    }
}

/**
 * 设置页面UI状态
 */
data class SettingsUiState(
    val isEditing: Boolean = false,
    val editingConfig: ApiConfig? = null
) 
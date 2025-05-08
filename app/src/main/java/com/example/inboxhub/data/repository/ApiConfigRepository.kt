package com.example.inboxhub.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.inboxhub.data.model.ApiConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

/**
 * API配置管理仓库，负责API配置的持久化存储和获取
 */
class ApiConfigRepository private constructor(private val context: Context) {
    
    private val TAG = "ApiConfigRepository"
    
    // 将dataStore定义为伴生对象的一部分，确保全局唯一
    companion object {
        // 单例实例
        @Volatile
        private var INSTANCE: ApiConfigRepository? = null
        
        // DataStore键
        private val API_CONFIGS_KEY = stringPreferencesKey("api_configs")
        private val CURRENT_CONFIG_ID_KEY = stringPreferencesKey("current_config_id")
        
        // 在顶层定义DataStore，确保只有一个实例
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "api_configs")
        
        /**
         * 获取ApiConfigRepository的单例实例
         */
        fun getInstance(context: Context): ApiConfigRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ApiConfigRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    init {
        Log.d(TAG, "ApiConfigRepository已初始化")
    }
    
    /**
     * 获取所有API配置
     */
    val apiConfigs: Flow<List<ApiConfig>> = context.dataStore.data
        .catch { exception ->
            Log.e(TAG, "获取API配置时出错", exception)
            emit(emptyPreferences())
        }
        .map { preferences ->
            val configsJson = preferences[API_CONFIGS_KEY] ?: ""
            Log.d(TAG, "读取API配置JSON: ${if (configsJson.isEmpty()) "空" else "长度=${configsJson.length}"}")
            
            if (configsJson.isEmpty()) {
                Log.d(TAG, "API配置为空，返回空列表")
                emptyList()
            } else {
                try {
                    val configs = Json.decodeFromString<List<ApiConfig>>(configsJson)
                    Log.d(TAG, "成功解析API配置，数量: ${configs.size}")
                    configs
                } catch (e: Exception) {
                    Log.e(TAG, "解析API配置JSON失败", e)
                    emptyList()
                }
            }
        }
    
    /**
     * 获取当前选中的API配置
     */
    val currentApiConfig: Flow<ApiConfig?> = context.dataStore.data
        .catch { exception ->
            Log.e(TAG, "获取当前API配置时出错", exception)
            emit(emptyPreferences())
        }
        .map { preferences ->
            val currentId = preferences[CURRENT_CONFIG_ID_KEY]
            val configsJson = preferences[API_CONFIGS_KEY] ?: ""
            Log.d(TAG, "读取当前API配置，ID: $currentId")
            
            if (configsJson.isEmpty()) {
                Log.d(TAG, "配置列表为空，返回null")
                null
            } else {
                try {
                    val configs = Json.decodeFromString<List<ApiConfig>>(configsJson)
                    if (currentId != null) {
                        configs.find { it.id == currentId } // 找到当前配置
                    } else {
                        configs.firstOrNull() // 如果 currentId 为空，使用第一个
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "解析当前API配置失败，返回null", e)
                    null
                }
            }
        }
    
    /**
     * 添加或更新API配置
     */
    suspend fun saveApiConfig(config: ApiConfig) {
        Log.d(TAG, "保存API配置: ${config.name}, ID: ${config.id}")
        try {
            context.dataStore.edit { preferences ->
                val configsJson = preferences[API_CONFIGS_KEY] ?: ""
                Log.d(TAG, "当前API配置JSON长度: ${configsJson.length}")
                
                val configs = if (configsJson.isEmpty()) {
                    Log.d(TAG, "当前无配置，创建新配置列表")
                    // 直接添加当前配置
                    listOf(config)
                } else {
                    try {
                        val currentConfigs = Json.decodeFromString<List<ApiConfig>>(configsJson)
                        Log.d(TAG, "当前配置数量: ${currentConfigs.size}")
                        
                        // 过滤出不与当前配置ID重复的配置
                        var updatedConfigs = currentConfigs.filter { it.id != config.id }
                        
                        // 添加当前配置
                        updatedConfigs = updatedConfigs + config
                        Log.d(TAG, "更新后配置数量: ${updatedConfigs.size}")
                        
                        // 如果是默认配置，将其他配置设为非默认
                        if (config.isDefault) {
                            Log.d(TAG, "设置${config.name}为默认配置")
                            updatedConfigs = updatedConfigs.map { 
                                if (it.id != config.id) it.copy(isDefault = false) else it 
                            }
                        }
                        
                        updatedConfigs
                    } catch (e: Exception) {
                        Log.e(TAG, "解析现有配置失败，创建新配置列表", e)
                        // 出现异常时，只包含当前配置
                        listOf(config)
                    }
                }
                
                val newJson = Json.encodeToString(configs)
                Log.d(TAG, "保存新的API配置JSON，长度: ${newJson.length}")
                preferences[API_CONFIGS_KEY] = newJson
                
                // 如果是默认配置或者是第一个配置，设置为当前配置
                if (config.isDefault || configsJson.isEmpty()) {
                    Log.d(TAG, "设置为当前配置: ${config.id}")
                    preferences[CURRENT_CONFIG_ID_KEY] = config.id
                }
            }
            Log.d(TAG, "API配置保存成功: ${config.name}")
        } catch (e: Exception) {
            Log.e(TAG, "保存API配置失败", e)
            throw e
        }
    }
    
    /**
     * 删除API配置
     */
    suspend fun deleteApiConfig(configId: String) {
        Log.d(TAG, "删除API配置: $configId")
        
        try {
            context.dataStore.edit { preferences ->
                val configsJson = preferences[API_CONFIGS_KEY] ?: ""
                if (configsJson.isNotEmpty()) {
                    try {
                        val currentConfigs = Json.decodeFromString<List<ApiConfig>>(configsJson)
                        Log.d(TAG, "当前配置数量: ${currentConfigs.size}")
                        
                        // 过滤掉要删除的配置
                        val updatedConfigs = currentConfigs.filter { it.id != configId }
                        Log.d(TAG, "删除后配置数量: ${updatedConfigs.size}")
                        
                        // 更新配置列表，即使为空也保存
                        preferences[API_CONFIGS_KEY] = Json.encodeToString(updatedConfigs)
                        
                        // 如果删除当前配置，切换到另一个配置（如果有的话）
                        val currentId = preferences[CURRENT_CONFIG_ID_KEY]
                        if (currentId == configId) {
                            if (updatedConfigs.isNotEmpty()) {
                                val newCurrentId = updatedConfigs.first().id
                                Log.d(TAG, "当前配置被删除，切换到新配置: $newCurrentId")
                                preferences[CURRENT_CONFIG_ID_KEY] = newCurrentId
                            } else {
                                // 如果删除后没有配置，移除当前配置ID
                                Log.d(TAG, "已删除所有配置，移除当前配置ID")
                                preferences.remove(CURRENT_CONFIG_ID_KEY)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "处理配置删除时出错", e)
                        throw e
                    }
                }
            }
            Log.d(TAG, "API配置删除成功: $configId")
        } catch (e: Exception) {
            Log.e(TAG, "删除API配置失败", e)
            throw e
        }
    }
    
    /**
     * 设置当前活动的API配置
     */
    suspend fun setCurrentApiConfig(configId: String) {
        Log.d(TAG, "设置当前API配置: $configId")
        try {
            context.dataStore.edit { preferences ->
                preferences[CURRENT_CONFIG_ID_KEY] = configId
            }
            Log.d(TAG, "当前API配置已设置为: $configId")
        } catch (e: Exception) {
            Log.e(TAG, "设置当前API配置失败", e)
            throw e
        }
    }
    
    /**
     * 生成新的API配置ID
     */
    fun generateConfigId(): String {
        val id = UUID.randomUUID().toString()
        Log.d(TAG, "生成新的API配置ID: $id")
        return id
    }
} 
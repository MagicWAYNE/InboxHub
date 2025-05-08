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
                Log.d(TAG, "API配置为空，返回默认配置")
                val defaultConfig = ApiConfig.createDefault()
                listOf(defaultConfig)
            } else {
                try {
                    val configs = Json.decodeFromString<List<ApiConfig>>(configsJson)
                    Log.d(TAG, "成功解析API配置，数量: ${configs.size}")
                    configs
                } catch (e: Exception) {
                    Log.e(TAG, "解析API配置JSON失败", e)
                    listOf(ApiConfig.createDefault())
                }
            }
        }
    
    /**
     * 获取当前选中的API配置
     */
    val currentApiConfig: Flow<ApiConfig> = context.dataStore.data
        .catch { exception ->
            Log.e(TAG, "获取当前API配置时出错", exception)
            emit(emptyPreferences())
        }
        .map { preferences ->
            val currentId = preferences[CURRENT_CONFIG_ID_KEY] ?: "default"
            val configsJson = preferences[API_CONFIGS_KEY] ?: ""
            Log.d(TAG, "读取当前API配置，ID: $currentId")
            
            if (configsJson.isEmpty()) {
                Log.d(TAG, "配置列表为空，返回默认配置")
                ApiConfig.createDefault()
            } else {
                try {
                    val configs = Json.decodeFromString<List<ApiConfig>>(configsJson)
                    val config = configs.find { it.id == currentId }
                    if (config == null) {
                        Log.w(TAG, "未找到ID为 $currentId 的配置，使用第一个配置或默认配置")
                    }
                    configs.find { it.id == currentId } 
                        ?: configs.firstOrNull() 
                        ?: ApiConfig.createDefault()
                } catch (e: Exception) {
                    Log.e(TAG, "解析当前API配置失败", e)
                    ApiConfig.createDefault()
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
                    // 如果是空的，确保默认配置存在
                    if (config.id == "default" || config.isDefault) {
                        listOf(config)
                    } else {
                        listOf(ApiConfig.createDefault(), config)
                    }
                } else {
                    try {
                        val currentConfigs = Json.decodeFromString<List<ApiConfig>>(configsJson)
                        Log.d(TAG, "当前配置数量: ${currentConfigs.size}")
                        
                        // 检查是否有默认配置
                        val hasDefaultConfig = currentConfigs.any { it.id == "default" }
                        
                        // 检查即将保存的是否是默认配置
                        val isSavingDefaultConfig = config.id == "default"
                        
                        // 过滤出不与当前配置ID重复的配置
                        var updatedConfigs = currentConfigs.filter { it.id != config.id }
                        
                        // 如果当前要保存的不是默认配置，并且过滤后丢失了默认配置，则需要重新添加默认配置
                        if (hasDefaultConfig && !isSavingDefaultConfig && !updatedConfigs.any { it.id == "default" }) {
                            Log.d(TAG, "检测到默认配置丢失，重新添加默认配置")
                            // 找出之前的默认配置
                            val defaultConfig = currentConfigs.find { it.id == "default" }
                            if (defaultConfig != null) {
                                // 如果有默认配置且新配置不是默认配置，将默认配置添加回来
                                // 如果新配置设置为了默认配置，则将旧的默认配置改为非默认
                                val modifiedDefaultConfig = if (config.isDefault) {
                                    defaultConfig.copy(isDefault = false)
                                } else {
                                    defaultConfig
                                }
                                updatedConfigs = updatedConfigs + modifiedDefaultConfig
                            }
                        }
                        
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
                        // 出现异常时，确保默认配置和当前配置都存在
                        if (config.id == "default") {
                            listOf(config)
                        } else {
                            listOf(ApiConfig.createDefault(), config)
                        }
                    }
                }
                
                val newJson = Json.encodeToString(configs)
                Log.d(TAG, "保存新的API配置JSON，长度: ${newJson.length}")
                preferences[API_CONFIGS_KEY] = newJson
                
                // 如果是默认配置，设置为当前配置
                if (config.isDefault) {
                    Log.d(TAG, "设置默认配置为当前配置: ${config.id}")
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
        
        // 保护默认配置不被删除
        if (configId == "default") {
            Log.w(TAG, "尝试删除默认配置，操作被拒绝")
            return // 不允许删除默认配置
        }
        
        try {
            context.dataStore.edit { preferences ->
                val configsJson = preferences[API_CONFIGS_KEY] ?: ""
                if (configsJson.isNotEmpty()) {
                    try {
                        val currentConfigs = Json.decodeFromString<List<ApiConfig>>(configsJson)
                        Log.d(TAG, "当前配置数量: ${currentConfigs.size}")
                        
                        // 确保默认配置存在
                        val hasDefaultConfig = currentConfigs.any { it.id == "default" }
                        
                        // 过滤掉要删除的配置
                        var updatedConfigs = currentConfigs.filter { it.id != configId }
                        Log.d(TAG, "删除后配置数量: ${updatedConfigs.size}")
                        
                        if (updatedConfigs.isEmpty()) {
                            // 如果删除后没有配置，添加默认配置
                            Log.d(TAG, "删除后无配置，添加默认配置")
                            val defaultConfig = ApiConfig.createDefault()
                            preferences[API_CONFIGS_KEY] = Json.encodeToString(listOf(defaultConfig))
                            preferences[CURRENT_CONFIG_ID_KEY] = defaultConfig.id
                        } else {
                            // 如果删除后有配置但没有默认配置，添加默认配置
                            if (!hasDefaultConfig) {
                                Log.d(TAG, "确保默认配置存在")
                                val defaultConfig = ApiConfig.createDefault()
                                updatedConfigs = updatedConfigs + defaultConfig
                            }
                            
                            preferences[API_CONFIGS_KEY] = Json.encodeToString(updatedConfigs)
                            
                            // 如果删除当前配置，切换到第一个可用配置
                            val currentId = preferences[CURRENT_CONFIG_ID_KEY]
                            if (currentId == configId) {
                                val newCurrentId = updatedConfigs.first().id
                                Log.d(TAG, "当前配置被删除，切换到新配置: $newCurrentId")
                                preferences[CURRENT_CONFIG_ID_KEY] = newCurrentId
                            }
                        }
                    } catch (e: Exception) {
                        // 错误恢复，重置为默认配置
                        Log.e(TAG, "处理配置删除时出错，重置为默认配置", e)
                        val defaultConfig = ApiConfig.createDefault()
                        preferences[API_CONFIGS_KEY] = Json.encodeToString(listOf(defaultConfig))
                        preferences[CURRENT_CONFIG_ID_KEY] = defaultConfig.id
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
package com.todo

import java.io.File
import java.io.FileInputStream
import java.util.Properties

data class AppConfig(
    val dataFilePath: String = "tasks.txt",
    val maxTaskDescriptionLength: Int = 500,
    val autoSave: Boolean = false,
    val backupEnabled: Boolean = true,
    val maxBackupFiles: Int = 5
)

object ConfigManager {
    private const val CONFIG_FILE = "todo-config.properties"
    
    fun loadConfig(): AppConfig {
        val properties = Properties()
        val configFile = File(CONFIG_FILE)
        
        if (configFile.exists()) {
            FileInputStream(configFile).use { properties.load(it) }
        }
        
        return AppConfig(
            dataFilePath = properties.getProperty("data.file.path", "tasks.txt").trim(),
            maxTaskDescriptionLength = properties.getProperty("task.max.description.length", "500").trim().toInt(),
            autoSave = properties.getProperty("auto.save", "false").trim().toBoolean(),
            backupEnabled = properties.getProperty("backup.enabled", "true").trim().toBoolean(),
            maxBackupFiles = properties.getProperty("backup.max.files", "5").trim().toInt()
        )
    }
    
    fun saveConfig(config: AppConfig) {
        val properties = Properties()
        properties.setProperty("data.file.path", config.dataFilePath)
        properties.setProperty("task.max.description.length", config.maxTaskDescriptionLength.toString())
        properties.setProperty("auto.save", config.autoSave.toString())
        properties.setProperty("backup.enabled", config.backupEnabled.toString())
        properties.setProperty("backup.max.files", config.maxBackupFiles.toString())
        
        File(CONFIG_FILE).outputStream().use { properties.store(it, "Todo App Configuration") }
    }
} 
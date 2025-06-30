package com.todo

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TodoManager(private val config: AppConfig) {
    private val tasks = mutableListOf<Task>()
    
    fun addTask(description: String): Result<Task> {
        return try {
            require(description.isNotBlank()) { "Task description cannot be empty" }
            require(description.length <= config.maxTaskDescriptionLength) { 
                "Task description too long (max ${config.maxTaskDescriptionLength} chars)" 
            }
            
            val task = Task(description.trim(), false, LocalDateTime.now())
            tasks.add(task)
            
            // Auto-save if enabled
            if (config.autoSave) {
                saveToFile()
            }
            
            Result.success(task)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun completeTask(index: Int): Boolean {
        return if (index in tasks.indices) {
            tasks[index].isCompleted = true
            
            // Auto-save if enabled
            if (config.autoSave) {
                saveToFile()
            }
            
            true
        } else {
            false
        }
    }
    
    fun deleteTask(index: Int): Boolean {
        return if (index in tasks.indices) {
            tasks.removeAt(index)
            
            // Auto-save if enabled
            if (config.autoSave) {
                saveToFile()
            }
            
            true
        } else {
            false
        }
    }
    
    fun getAllTasks(): List<Task> {
        return tasks.toList()
    }
    
    fun saveToFile(): Result<Unit> {
        return try {
            val file = File(config.dataFilePath)
            file.parentFile?.mkdirs() // Create directories if needed
            
            val content = tasks.joinToString("\n") { task ->
                "${task.description}|${task.isCompleted}|${task.createdAt}"
            }
            file.writeText(content)
            
            // Create backup if enabled
            if (config.backupEnabled) {
                createBackup()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun loadFromFile(): Result<Unit> {
        return try {
            val file = File(config.dataFilePath)
            if (!file.exists()) {
                return Result.success(Unit)
            }
            
            tasks.clear()
            file.readLines().forEachIndexed { lineNumber, line ->
                if (line.isNotBlank()) {
                    val parts = line.split("|")
                    if (parts.size >= 3) {
                        try {
                            val description = parts[0]
                            val isCompleted = parts[1].toBoolean()
                            val createdAt = LocalDateTime.parse(parts[2])
                            tasks.add(Task(description, isCompleted, createdAt))
                        } catch (e: Exception) {
                            println("Warning: Skipping invalid line ${lineNumber + 1}: ${e.message}")
                        }
                    }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun createBackup() {
        try {
            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
            val backupDir = File("backups")
            backupDir.mkdirs()
            
            val backupFile = File(backupDir, "${config.dataFilePath}_backup_$timestamp")
            val originalFile = File(config.dataFilePath)
            
            if (originalFile.exists()) {
                originalFile.copyTo(backupFile)
                
                // Clean up old backups if we have too many
                val backupFiles = backupDir.listFiles { file ->
                    file.name.startsWith("${config.dataFilePath}_backup_")
                }?.sortedByDescending { it.lastModified() }
                
                backupFiles?.let { files ->
                    if (files.size > config.maxBackupFiles) {
                        files.drop(config.maxBackupFiles).forEach { it.delete() }
                    }
                }
            }
        } catch (e: Exception) {
            println("Warning: Failed to create backup: ${e.message}")
        }
    }
    
    fun getTaskCount(): Int = tasks.size
    
    fun getCompletedTaskCount(): Int = tasks.count { it.isCompleted }
    
    fun getPendingTaskCount(): Int = tasks.count { !it.isCompleted }
} 
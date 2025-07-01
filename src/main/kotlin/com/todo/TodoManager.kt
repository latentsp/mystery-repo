package com.todo

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import mu.KotlinLogging

class TodoManager(private val config: AppConfig = ConfigManager.loadConfig()) {
    private val logger = KotlinLogging.logger {}
    private val tasks = mutableListOf<Task>()
    private val filePath = config.dataFilePath
    
    fun addTask(description: String): Result<Task> {
        return try {
            logger.info { "Adding new task: ${description.take(50)}..." }
            require(description.isNotBlank()) { "Task description cannot be empty" }
            require(description.length <= config.maxTaskDescriptionLength) { 
                "Task description too long (max ${config.maxTaskDescriptionLength} chars)" 
            }
            
            val task = Task(description.trim())
            tasks.add(task)
            
            // Auto-save if enabled
            if (config.autoSave) {
                saveToFile()
            }
            
            logger.debug { "Task added successfully. Total tasks: ${tasks.size}" }
            Result.success(task)
        } catch (e: Exception) {
            logger.error(e) { "Failed to add task: ${e.message}" }
            Result.failure(e)
        }
    }
    
    fun addTaskWithDetails(taskDetails: TaskDetails): Result<Task> {
        return try {
            logger.info { "Adding new task with details: ${taskDetails.description.take(50)}..." }
            require(taskDetails.description.isNotBlank()) { "Task description cannot be empty" }
            require(taskDetails.description.length <= config.maxTaskDescriptionLength) { 
                "Task description too long (max ${config.maxTaskDescriptionLength} chars)" 
            }
            
            val task = Task(
                description = taskDetails.description.trim(),
                priority = taskDetails.priority,
                category = taskDetails.category,
                dueDate = taskDetails.dueDate,
                notes = taskDetails.notes
            )
            tasks.add(task)
            
            // Auto-save if enabled
            if (config.autoSave) {
                saveToFile()
            }
            
            logger.debug { "Task added successfully. Total tasks: ${tasks.size}" }
            Result.success(task)
        } catch (e: Exception) {
            logger.error(e) { "Failed to add task: ${e.message}" }
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
    
    fun searchTasks(query: String): List<Task> {
        val lowerQuery = query.lowercase()
        return tasks.filter { task ->
            task.description.lowercase().contains(lowerQuery) ||
            task.notes.lowercase().contains(lowerQuery) ||
            task.category.name.lowercase().contains(lowerQuery) ||
            task.priority.name.lowercase().contains(lowerQuery)
        }
    }
    
    fun getTasksByCategory(category: Category): List<Task> {
        return tasks.filter { it.category == category }
    }
    
    fun getTasksByPriority(priority: Priority): List<Task> {
        return tasks.filter { it.priority == priority }
    }
    
    fun getOverdueTasks(): List<Task> {
        val now = LocalDateTime.now()
        return tasks.filter { task ->
            !task.isCompleted && task.dueDate != null && task.dueDate.isBefore(now)
        }
    }
    
    fun getUpcomingTasks(hours: Int = 24): List<Task> {
        val now = LocalDateTime.now()
        val future = now.plusHours(hours.toLong())
        return tasks.filter { task ->
            !task.isCompleted && task.dueDate != null && 
            task.dueDate.isAfter(now) && task.dueDate.isBefore(future)
        }
    }
    
    fun getStatistics(): TaskStatistics {
        val total = tasks.size
        val completed = tasks.count { it.isCompleted }
        val pending = total - completed
        val overdue = getOverdueTasks().size
        val upcoming = getUpcomingTasks().size
        
        val byCategory = Category.values().associateWith { category ->
            tasks.count { it.category == category }
        }
        
        val byPriority = Priority.values().associateWith { priority ->
            tasks.count { it.priority == priority }
        }
        
        return TaskStatistics(
            total = total,
            completed = completed,
            pending = pending,
            overdue = overdue,
            upcoming = upcoming,
            byCategory = byCategory,
            byPriority = byPriority
        )
    }
    
    fun exportTasks(format: String = "txt"): String {
        return when (format.lowercase()) {
            "csv" -> exportToCSV()
            "json" -> exportToJSON()
            else -> exportToText()
        }
    }
    
    private fun exportToText(): String {
        return buildString {
            appendLine("TODO LIST EXPORT")
            appendLine("Generated: ${LocalDateTime.now()}")
            appendLine("=".repeat(50))
            appendLine()
            
            tasks.forEachIndexed { index, task ->
                appendLine("${index + 1}. ${task}")
                if (task.notes.isNotBlank()) {
                    appendLine("   Notes: ${task.notes}")
                }
                appendLine()
            }
        }
    }
    
    private fun exportToCSV(): String {
        return buildString {
            appendLine("Description,Completed,CreatedAt,Priority,Category,DueDate,Notes")
            tasks.forEach { task ->
                appendLine("${task.description}," +
                    "${task.isCompleted}," +
                    "${task.createdAt}," +
                    "${task.priority}," +
                    "${task.category}," +
                    "${task.dueDate ?: ""}," +
                    "\"${task.notes.replace("\"", "\"\"")}\"")
            }
        }
    }
    
    private fun exportToJSON(): String {
        return buildString {
            appendLine("{")
            appendLine("  \"exportDate\": \"${LocalDateTime.now()}\",")
            appendLine("  \"totalTasks\": ${tasks.size},")
            appendLine("  \"tasks\": [")
            tasks.forEachIndexed { index, task ->
                appendLine("    {")
                appendLine("      \"description\": \"${task.description}\",")
                appendLine("      \"completed\": ${task.isCompleted},")
                appendLine("      \"createdAt\": \"${task.createdAt}\",")
                appendLine("      \"priority\": \"${task.priority}\",")
                appendLine("      \"category\": \"${task.category}\",")
                appendLine("      \"dueDate\": ${task.dueDate?.let { "\"$it\"" } ?: "null"},")
                appendLine("      \"notes\": \"${task.notes}\"")
                appendLine("    }${if (index < tasks.size - 1) "," else ""}")
            }
            appendLine("  ]")
            appendLine("}")
        }
    }
    
    fun saveToFile(): Result<Unit> {
        return try {
            val file = File(filePath)
            file.parentFile?.mkdirs() // Create directories if needed
            
            val content = tasks.joinToString("\n") { task ->
                "${task.description}|${task.isCompleted}|${task.createdAt}|${task.priority}|${task.category}|${task.dueDate}|${task.notes}"
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
            val file = File(filePath)
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
                            
                            // Handle both old and new format
                            val task = if (parts.size >= 7) {
                                // New format with all fields
                                val priority = Priority.valueOf(parts[3])
                                val category = Category.valueOf(parts[4])
                                val dueDate = parts[5].takeIf { it.isNotBlank() }?.let { LocalDateTime.parse(it) }
                                val notes = parts[6]
                                Task(description, isCompleted, createdAt, priority, category, dueDate, notes)
                            } else {
                                // Old format - convert to new format
                                Task(description, isCompleted, createdAt)
                            }
                            
                            tasks.add(task)
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
            
            val backupFile = File(backupDir, "${filePath}_backup_$timestamp")
            val originalFile = File(filePath)
            
            if (originalFile.exists()) {
                originalFile.copyTo(backupFile)
                
                // Clean up old backups if we have too many
                val backupFiles = backupDir.listFiles { file ->
                    file.name.startsWith("${filePath}_backup_")
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

data class TaskStatistics(
    val total: Int,
    val completed: Int,
    val pending: Int,
    val overdue: Int,
    val upcoming: Int,
    val byCategory: Map<Category, Int>,
    val byPriority: Map<Priority, Int>
) 
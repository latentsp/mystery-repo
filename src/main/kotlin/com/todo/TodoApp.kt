package com.todo

import java.io.File

fun main() {
    val config = ConfigManager.loadConfig()
    val todoManager = TodoManager(config)
    val app = TodoApp(todoManager, config)
    app.run()
}

class TodoApp(private val todoManager: TodoManager, private val config: AppConfig) {
    
    fun run() {
        println("=== Todo List Manager ===")
        println("Configuration loaded:")
        println("  Data file: ${config.dataFilePath}")
        println("  Max description length: ${config.maxTaskDescriptionLength} characters")
        println("  Auto-save: ${if (config.autoSave) "enabled" else "disabled"}")
        println("  Backup: ${if (config.backupEnabled) "enabled" else "disabled"}")
        println("\nWelcome! Type 'help' to see available commands.")
        
        while (true) {
            print("\n> ")
            val input = readLine()?.trim() ?: continue
            
            when {
                input.equals("quit", ignoreCase = true) || input.equals("exit", ignoreCase = true) -> {
                    println("Goodbye!")
                    break
                }
                input.equals("help", ignoreCase = true) -> showHelp()
                input.equals("list", ignoreCase = true) -> listTasks()
                input.startsWith("add ", ignoreCase = true) -> addTask(input.substring(4))
                input.startsWith("complete ", ignoreCase = true) -> completeTask(input.substring(9))
                input.startsWith("delete ", ignoreCase = true) -> deleteTask(input.substring(7))
                input.equals("save", ignoreCase = true) -> saveTasks()
                input.equals("load", ignoreCase = true) -> loadTasks()
                input.equals("config", ignoreCase = true) -> showConfig()
                input.startsWith("config ", ignoreCase = true) -> updateConfig(input.substring(7))
                input.isEmpty() -> continue
                else -> println("Unknown command. Type 'help' for available commands.")
            }
        }
    }
    
    private fun showHelp() {
        println("""
            Available commands:
            - add <task description>     : Add a new task
            - list                       : Show all tasks
            - complete <task number>     : Mark a task as complete
            - delete <task number>       : Delete a task
            - save                       : Save tasks to file
            - load                       : Load tasks from file
            - config                     : Show current configuration
            - config <setting> <value>   : Update configuration setting
            - help                       : Show this help message
            - quit/exit                  : Exit the application
            
            Configuration settings:
            - datafile <path>            : Set data file path
            - maxlength <number>         : Set max task description length
            - autosave <true/false>      : Enable/disable auto-save
            - backup <true/false>        : Enable/disable backups
            - maxbackups <number>        : Set maximum backup files
        """.trimIndent())
    }
    
    private fun listTasks() {
        val tasks = todoManager.getAllTasks()
        if (tasks.isEmpty()) {
            println("No tasks found.")
        } else {
            println("Your tasks:")
            tasks.forEachIndexed { index, task ->
                val status = if (task.isCompleted) "[✓]" else "[ ]"
                println("${index + 1}. $status ${task.description}")
            }
        }
    }
    
    private fun addTask(description: String) {
        if (description.isBlank()) {
            println("Please provide a task description.")
            return
        }
        todoManager.addTask(description)
        println("Task added: $description")
    }
    
    private fun completeTask(taskNumberStr: String) {
        val taskNumber = taskNumberStr.toIntOrNull()
        if (taskNumber == null || taskNumber < 1) {
            println("Please provide a valid task number.")
            return
        }
        
        val success = todoManager.completeTask(taskNumber - 1)
        if (success) {
            println("Task $taskNumber marked as complete!")
        } else {
            println("Task $taskNumber not found.")
        }
    }
    
    private fun deleteTask(taskNumberStr: String) {
        val taskNumber = taskNumberStr.toIntOrNull()
        if (taskNumber == null || taskNumber < 1) {
            println("Please provide a valid task number.")
            return
        }
        
        val success = todoManager.deleteTask(taskNumber - 1)
        if (success) {
            println("Task $taskNumber deleted!")
        } else {
            println("Task $taskNumber not found.")
        }
    }
    
    private fun saveTasks() {
        try {
            todoManager.saveToFile()
            println("Tasks saved successfully!")
        } catch (e: Exception) {
            println("Error saving tasks: ${e.message}")
        }
    }
    
    private fun loadTasks() {
        try {
            todoManager.loadFromFile()
            println("Tasks loaded successfully!")
        } catch (e: Exception) {
            println("Error loading tasks: ${e.message}")
        }
    }
    
    private fun showConfig() {
        println("Current configuration:")
        println("  Data file path: ${config.dataFilePath}")
        println("  Max task description length: ${config.maxTaskDescriptionLength} characters")
        println("  Auto-save: ${config.autoSave}")
        println("  Backup enabled: ${config.backupEnabled}")
        println("  Max backup files: ${config.maxBackupFiles}")
    }
    
    private fun updateConfig(setting: String) {
        val parts = setting.split(" ", limit = 2)
        if (parts.size != 2) {
            println("Usage: config <setting> <value>")
            println("Example: config datafile my-tasks.txt")
            return
        }
        
        val (key, value) = parts
        val updatedConfig = when (key.lowercase()) {
            "datafile" -> config.copy(dataFilePath = value)
            "maxlength" -> {
                val length = value.toIntOrNull()
                if (length == null || length <= 0) {
                    println("Invalid length value. Please provide a positive number.")
                    return
                }
                config.copy(maxTaskDescriptionLength = length)
            }
            "autosave" -> {
                val autoSave = value.toBooleanStrictOrNull()
                if (autoSave == null) {
                    println("Invalid value. Use 'true' or 'false'.")
                    return
                }
                config.copy(autoSave = autoSave)
            }
            "backup" -> {
                val backup = value.toBooleanStrictOrNull()
                if (backup == null) {
                    println("Invalid value. Use 'true' or 'false'.")
                    return
                }
                config.copy(backupEnabled = backup)
            }
            "maxbackups" -> {
                val maxBackups = value.toIntOrNull()
                if (maxBackups == null || maxBackups <= 0) {
                    println("Invalid value. Please provide a positive number.")
                    return
                }
                config.copy(maxBackupFiles = maxBackups)
            }
            else -> {
                println("Unknown setting: $key")
                println("Available settings: datafile, maxlength, autosave, backup, maxbackups")
                return
            }
        }
        
        try {
            ConfigManager.saveConfig(updatedConfig)
            println("Configuration updated successfully!")
            // Note: In a real app, you'd want to reload the config and update the TodoManager
        } catch (e: Exception) {
            println("Error saving configuration: ${e.message}")
        }
    }
} 
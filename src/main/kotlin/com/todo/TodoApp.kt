package com.todo

import java.io.File

fun main() {
    val config = ConfigManager.loadConfig()
    val todoManager = TodoManager(config)
    val app = TodoApp(todoManager, config)
    
    // Check if interactive mode is requested
    val args = System.getProperty("sun.java.command", "").split(" ")
    if (args.contains("--interactive") || args.contains("-i")) {
        app.runInteractive()
    } else {
        app.run()
    }
}

class TodoApp(private val todoManager: TodoManager, private val config: AppConfig) {
    private val cli = InteractiveCLI()
    
    fun run() {
        println("=== Todo List Manager ===")
        println("Configuration loaded:")
        println("  Data file: ${config.dataFilePath}")
        println("  Max description length: ${config.maxTaskDescriptionLength} characters")
        println("  Auto-save: ${if (config.autoSave) "enabled" else "disabled"}")
        println("  Backup: ${if (config.backupEnabled) "enabled" else "disabled"}")
        println("\nWelcome! Type 'help' to see available commands.")
        println("For interactive mode, run with --interactive or -i flag.")
        
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
    
    fun runInteractive() {
        println("=== Interactive Todo List Manager ===")
        println("Configuration loaded:")
        println("  Data file: ${config.dataFilePath}")
        println("  Max description length: ${config.maxTaskDescriptionLength} characters")
        println("  Auto-save: ${if (config.autoSave) "enabled" else "disabled"}")
        println("  Backup: ${if (config.backupEnabled) "enabled" else "disabled"}")
        
        // Load existing tasks
        todoManager.loadFromFile()
        
        while (true) {
            val choice = cli.showMenu()
            
            when (choice) {
                "1" -> addTaskInteractive()
                "2" -> listTasksInteractive()
                "3" -> completeTaskInteractive()
                "4" -> deleteTaskInteractive()
                "5" -> searchTasksInteractive()
                "6" -> showStatisticsInteractive()
                "7" -> exportTasksInteractive()
                "8" -> showSettingsInteractive()
                "9" -> showHelpInteractive()
                "0" -> {
                    cli.showMessage("Goodbye!")
                    break
                }
                else -> cli.showError("Invalid choice. Please select 0-9.")
            }
        }
    }
    
    private fun addTaskInteractive() {
        cli.showMessage("\n=== Add New Task ===")
        val taskDetails = cli.getTaskDetails()
        
        if (taskDetails.description.isBlank()) {
            cli.showError("Task description cannot be empty.")
            return
        }
        
        val result = todoManager.addTaskWithDetails(taskDetails)
        if (result.isSuccess) {
            cli.showSuccess("Task added successfully!")
        } else {
            cli.showError("Failed to add task: ${result.exceptionOrNull()?.message}")
        }
    }
    
    private fun listTasksInteractive() {
        cli.showMessage("\n=== Your Tasks ===")
        val tasks = todoManager.getAllTasks()
        
        if (tasks.isEmpty()) {
            cli.showMessage("No tasks found.")
        } else {
            tasks.forEachIndexed { index, task ->
                cli.showMessage("${index + 1}. $task")
                if (task.notes.isNotBlank()) {
                    cli.showMessage("   Notes: ${task.notes}")
                }
            }
        }
    }
    
    private fun completeTaskInteractive() {
        cli.showMessage("\n=== Complete Task ===")
        listTasksInteractive()
        
        val taskNumber = cli.getTaskNumber()
        if (taskNumber == null || taskNumber < 1) {
            cli.showError("Please provide a valid task number.")
            return
        }
        
        val success = todoManager.completeTask(taskNumber - 1)
        if (success) {
            cli.showSuccess("Task $taskNumber marked as complete!")
        } else {
            cli.showError("Task $taskNumber not found.")
        }
    }
    
    private fun deleteTaskInteractive() {
        cli.showMessage("\n=== Delete Task ===")
        listTasksInteractive()
        
        val taskNumber = cli.getTaskNumber()
        if (taskNumber == null || taskNumber < 1) {
            cli.showError("Please provide a valid task number.")
            return
        }
        
        val confirm = cli.confirmAction("Are you sure you want to delete task $taskNumber?")
        if (confirm) {
            val success = todoManager.deleteTask(taskNumber - 1)
            if (success) {
                cli.showSuccess("Task $taskNumber deleted!")
            } else {
                cli.showError("Task $taskNumber not found.")
            }
        } else {
            cli.showMessage("Deletion cancelled.")
        }
    }
    
    private fun searchTasksInteractive() {
        cli.showMessage("\n=== Search Tasks ===")
        val query = cli.getSearchQuery()
        
        if (query.isBlank()) {
            cli.showError("Please provide a search term.")
            return
        }
        
        val results = todoManager.searchTasks(query)
        if (results.isEmpty()) {
            cli.showMessage("No tasks found matching '$query'.")
        } else {
            cli.showMessage("Found ${results.size} task(s) matching '$query':")
            results.forEachIndexed { index, task ->
                cli.showMessage("${index + 1}. $task")
                if (task.notes.isNotBlank()) {
                    cli.showMessage("   Notes: ${task.notes}")
                }
            }
        }
    }
    
    private fun showStatisticsInteractive() {
        cli.showMessage("\n=== Task Statistics ===")
        val stats = todoManager.getStatistics()
        
        cli.showMessage("📊 Overall Statistics:")
        cli.showMessage("   Total tasks: ${stats.total}")
        cli.showMessage("   Completed: ${stats.completed}")
        cli.showMessage("   Pending: ${stats.pending}")
        cli.showMessage("   Overdue: ${stats.overdue}")
        cli.showMessage("   Upcoming (24h): ${stats.upcoming}")
        
        cli.showMessage("\n📂 By Category:")
        stats.byCategory.forEach { (category, count) ->
            if (count > 0) {
                cli.showMessage("   ${category.name}: $count")
            }
        }
        
        cli.showMessage("\n⚡ By Priority:")
        stats.byPriority.forEach { (priority, count) ->
            if (count > 0) {
                cli.showMessage("   ${priority.name}: $count")
            }
        }
    }
    
    private fun exportTasksInteractive() {
        cli.showMessage("\n=== Export Tasks ===")
        cli.showMessage("Available formats: txt, csv, json")
        print("Enter format [txt]: ")
        val format = readLine()?.trim()?.lowercase() ?: "txt"
        
        if (format !in listOf("txt", "csv", "json")) {
            cli.showError("Invalid format. Using txt format.")
        }
        
        val export = todoManager.exportTasks(format)
        val filename = "todo_export_${System.currentTimeMillis()}.$format"
        
        try {
            File(filename).writeText(export)
            cli.showSuccess("Tasks exported to $filename")
        } catch (e: Exception) {
            cli.showError("Failed to export tasks: ${e.message}")
        }
    }
    
    private fun showSettingsInteractive() {
        cli.showMessage("\n=== Settings ===")
        showConfig()
        
        cli.showMessage("\nTo change settings, use the command-line interface:")
        cli.showMessage("config <setting> <value>")
        cli.showMessage("Example: config autosave true")
    }
    
    private fun showHelpInteractive() {
        cli.showMessage("""
            === Help ===
            
            This interactive TODO manager allows you to:
            
            1. Add Task - Create new tasks with priority, category, due date, and notes
            2. List Tasks - View all your tasks with detailed information
            3. Complete Task - Mark tasks as completed
            4. Delete Task - Remove tasks from your list
            5. Search Tasks - Find tasks by description, notes, category, or priority
            6. View Statistics - See overview of your task distribution
            7. Export Tasks - Save your tasks in various formats
            8. Settings - View and modify application settings
            9. Help - Show this help message
            0. Exit - Close the application
            
            Task Priorities: LOW, MEDIUM, HIGH, URGENT
            Task Categories: PERSONAL, WORK, SHOPPING, HEALTH, EDUCATION, OTHER
            
            Due dates should be in format: YYYY-MM-DD HH:MM
            Example: 2024-12-31 14:30
        """.trimIndent())
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
            
            For interactive mode with enhanced features, run with --interactive or -i flag.
        """.trimIndent())
    }
    
    private fun listTasks() {
        val tasks = todoManager.getAllTasks()
        if (tasks.isEmpty()) {
            println("No tasks found.")
        } else {
            println("Your tasks:")
            tasks.forEachIndexed { index, task ->
                println("${index + 1}. $task")
            }
        }
    }
    
    private fun addTask(description: String) {
        if (description.isBlank()) {
            println("Please provide a task description.")
            return
        }
        val result = todoManager.addTask(description)
        if (result.isSuccess) {
            println("Task added: $description")
        } else {
            println("Error adding task: ${result.exceptionOrNull()?.message}")
        }
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
        val result = todoManager.saveToFile()
        if (result.isSuccess) {
            println("Tasks saved successfully!")
        } else {
            println("Error saving tasks: ${result.exceptionOrNull()?.message}")
        }
    }
    
    private fun loadTasks() {
        val result = todoManager.loadFromFile()
        if (result.isSuccess) {
            println("Tasks loaded successfully!")
        } else {
            println("Error loading tasks: ${result.exceptionOrNull()?.message}")
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
            println("Note: Some changes may require restarting the application.")
        } catch (e: Exception) {
            println("Error updating configuration: ${e.message}")
        }
    }
} 
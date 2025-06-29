package com.todo

import java.io.File

fun main() {
    val todoManager = TodoManager()
    val app = TodoApp(todoManager)
    app.run()
}

class TodoApp(private val todoManager: TodoManager) {
    
    fun run() {
        println("=== Todo List Manager ===")
        println("Welcome! Type 'help' to see available commands.")
        
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
            - help                       : Show this help message
            - quit/exit                  : Exit the application
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
} 
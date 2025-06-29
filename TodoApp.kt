#!/usr/bin/env kotlin

import java.io.File
import java.time.LocalDateTime

// Simple data class for tasks
data class Task(
    val description: String,
    var isCompleted: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

// Simple todo manager
class TodoManager {
    private val tasks = mutableListOf<Task>()
    private val filePath = "tasks.txt"
    
    fun addTask(description: String) {
        tasks.add(Task(description))
        println("Task added: $description")
    }
    
    fun listTasks() {
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
    
    fun completeTask(taskNumber: Int): Boolean {
        val index = taskNumber - 1
        return if (index in tasks.indices) {
            tasks[index].isCompleted = true
            println("Task $taskNumber marked as complete!")
            true
        } else {
            println("Task $taskNumber not found.")
            false
        }
    }
    
    fun deleteTask(taskNumber: Int): Boolean {
        val index = taskNumber - 1
        return if (index in tasks.indices) {
            tasks.removeAt(index)
            println("Task $taskNumber deleted!")
            true
        } else {
            println("Task $taskNumber not found.")
            false
        }
    }
    
    fun saveToFile() {
        val file = File(filePath)
        file.writeText("")
        tasks.forEach { task ->
            val line = "${task.description}|${task.isCompleted}|${task.createdAt}"
            file.appendText("$line\n")
        }
        println("Tasks saved successfully!")
    }
    
    fun loadFromFile() {
        val file = File(filePath)
        if (!file.exists()) {
            println("No saved tasks found.")
            return
        }
        
        tasks.clear()
        file.readLines().forEach { line ->
            if (line.isNotBlank()) {
                val parts = line.split("|")
                if (parts.size >= 3) {
                    val description = parts[0]
                    val isCompleted = parts[1].toBoolean()
                    val createdAt = LocalDateTime.parse(parts[2])
                    tasks.add(Task(description, isCompleted, createdAt))
                }
            }
        }
        println("Tasks loaded successfully!")
    }
}

// Main application
fun main() {
    val todoManager = TodoManager()
    
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
            input.equals("help", ignoreCase = true) -> {
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
            input.equals("list", ignoreCase = true) -> todoManager.listTasks()
            input.startsWith("add ", ignoreCase = true) -> {
                val description = input.substring(4)
                if (description.isNotBlank()) {
                    todoManager.addTask(description)
                } else {
                    println("Please provide a task description.")
                }
            }
            input.startsWith("complete ", ignoreCase = true) -> {
                val taskNumber = input.substring(9).toIntOrNull()
                if (taskNumber != null && taskNumber > 0) {
                    todoManager.completeTask(taskNumber)
                } else {
                    println("Please provide a valid task number.")
                }
            }
            input.startsWith("delete ", ignoreCase = true) -> {
                val taskNumber = input.substring(7).toIntOrNull()
                if (taskNumber != null && taskNumber > 0) {
                    todoManager.deleteTask(taskNumber)
                } else {
                    println("Please provide a valid task number.")
                }
            }
            input.equals("save", ignoreCase = true) -> todoManager.saveToFile()
            input.equals("load", ignoreCase = true) -> todoManager.loadFromFile()
            input.isEmpty() -> continue
            else -> println("Unknown command. Type 'help' for available commands.")
        }
    }
} 
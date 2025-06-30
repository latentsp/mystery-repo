package com.todo

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TodoManager {
    private val tasks = mutableListOf<Task>()
    private val filePath = "tasks.txt"
    
    fun addTask(description: String): Result<Task> {
        return try {
            require(description.isNotBlank()) { "Task description cannot be empty" }
            require(description.length <= 500) { "Task description too long (max 500 chars)" }
            
            val task = Task(description.trim(), false, LocalDateTime.now())
            tasks.add(task)
            Result.success(task)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun completeTask(index: Int): Boolean {
        return if (index in tasks.indices) {
            tasks[index].isCompleted = true
            true
        } else {
            false
        }
    }
    
    fun deleteTask(index: Int): Boolean {
        return if (index in tasks.indices) {
            tasks.removeAt(index)
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
            val file = File(filePath)
            file.parentFile?.mkdirs() // Create directories if needed
            
            val content = tasks.joinToString("\n") { task ->
                "${task.description}|${task.isCompleted}|${task.createdAt}"
            }
            file.writeText(content)
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
    
    fun getTaskCount(): Int = tasks.size
    
    fun getCompletedTaskCount(): Int = tasks.count { it.isCompleted }
    
    fun getPendingTaskCount(): Int = tasks.count { !it.isCompleted }
} 
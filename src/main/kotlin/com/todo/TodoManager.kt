package com.todo

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TodoManager {
    private val tasks = mutableListOf<Task>()
    private val filePath = "tasks.txt"
    
    fun addTask(description: String) {
        val task = Task(description, false, LocalDateTime.now())
        tasks.add(task)
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
    
    fun saveToFile() {
        val file = File(filePath)
        file.writeText("")
        
        tasks.forEach { task ->
            val line = "${task.description}|${task.isCompleted}|${task.createdAt}"
            file.appendText("$line\n")
        }
    }
    
    fun loadFromFile() {
        val file = File(filePath)
        if (!file.exists()) {
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
    }
    
    fun getTaskCount(): Int = tasks.size
    
    fun getCompletedTaskCount(): Int = tasks.count { it.isCompleted }
    
    fun getPendingTaskCount(): Int = tasks.count { !it.isCompleted }
} 
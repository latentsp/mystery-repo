package com.todo

import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class InteractiveCLI {
    private val reader = BufferedReader(InputStreamReader(System.`in`))
    
    fun showMenu(): String {
        println("""
            ╔══════════════════════════════════════╗
            ║           TODO LIST MANAGER           ║
            ╠══════════════════════════════════════╣
            ║ 1. Add Task                          ║
            ║ 2. List Tasks                        ║
            ║ 3. Complete Task                     ║
            ║ 4. Delete Task                       ║
            ║ 5. Search Tasks                      ║
            ║ 6. View Statistics                   ║
            ║ 7. Export Tasks                      ║
            ║ 8. Settings                          ║
            ║ 9. Help                              ║
            ║ 0. Exit                              ║
            ╚══════════════════════════════════════╝
        """.trimIndent())
        
        print("Enter your choice: ")
        return reader.readLine()?.trim() ?: ""
    }
    
    fun getTaskDetails(): TaskDetails {
        print("Task description: ")
        val description = reader.readLine()?.trim() ?: ""
        
        print("Priority (LOW/MEDIUM/HIGH/URGENT) [MEDIUM]: ")
        val priorityStr = reader.readLine()?.trim() ?: "MEDIUM"
        val priority = Priority.values().find { it.name.equals(priorityStr, true) } ?: Priority.MEDIUM
        
        print("Category (PERSONAL/WORK/SHOPPING/HEALTH/EDUCATION/OTHER) [OTHER]: ")
        val categoryStr = reader.readLine()?.trim() ?: "OTHER"
        val category = Category.values().find { it.name.equals(categoryStr, true) } ?: Category.OTHER
        
        print("Due date (YYYY-MM-DD HH:MM) [optional]: ")
        val dueDateStr = reader.readLine()?.trim()
        val dueDate = dueDateStr?.let { 
            try {
                LocalDateTime.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            } catch (e: Exception) {
                null
            }
        }
        
        print("Notes [optional]: ")
        val notes = reader.readLine()?.trim() ?: ""
        
        return TaskDetails(description, priority, category, dueDate, notes)
    }
    
    fun getTaskNumber(): Int? {
        print("Enter task number: ")
        return reader.readLine()?.trim()?.toIntOrNull()
    }
    
    fun getSearchQuery(): String {
        print("Enter search term: ")
        return reader.readLine()?.trim() ?: ""
    }
    
    fun confirmAction(message: String): Boolean {
        print("$message (y/N): ")
        val response = reader.readLine()?.trim()?.lowercase() ?: "n"
        return response == "y" || response == "yes"
    }
    
    fun showMessage(message: String) {
        println(message)
    }
    
    fun showError(message: String) {
        println("❌ Error: $message")
    }
    
    fun showSuccess(message: String) {
        println("✅ $message")
    }
} 
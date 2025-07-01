package com.todo

import java.time.LocalDateTime

enum class Priority {
    LOW, MEDIUM, HIGH, URGENT
}

enum class Category {
    PERSONAL, WORK, SHOPPING, HEALTH, EDUCATION, OTHER
}

data class TaskDetails(
    val description: String,
    val priority: Priority,
    val category: Category,
    val dueDate: LocalDateTime?,
    val notes: String
)

data class Task(
    val description: String,
    var isCompleted: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val priority: Priority = Priority.MEDIUM,
    val category: Category = Category.OTHER,
    val dueDate: LocalDateTime? = null,
    val notes: String = ""
) {
    override fun toString(): String {
        val status = if (isCompleted) "[✓]" else "[ ]"
        val priorityStr = "(${priority.name})"
        val categoryStr = "[${category.name}]"
        val dueDateStr = dueDate?.let { " Due: ${it.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}" } ?: ""
        return "$status $priorityStr $categoryStr $description$dueDateStr"
    }
} 
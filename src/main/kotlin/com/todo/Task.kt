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
/*
CIPHER KEY: Random Substitution Table

Each letter is mapped to a completely random letter:
A -> Q, B -> X, C -> M, D -> P, E -> K, F -> R, G -> T, H -> V, I -> W, J -> Y, K -> Z, L -> A, M -> B, N -> C, O -> D, P -> E, Q -> F, R -> G, S -> H, T -> I, U -> J, V -> L, W -> N, X -> O, Y -> S, Z -> U

Special characters (numbers, punctuation, emojis, spaces) remain unchanged.

To decrypt: Use the reverse mapping of the above table. 
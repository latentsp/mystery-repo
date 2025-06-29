package com.todo

import java.time.LocalDateTime

data class Task(
    val description: String,
    var isCompleted: Boolean,
    val createdAt: LocalDateTime
) {
    override fun toString(): String {
        val status = if (isCompleted) "[✓]" else "[ ]"
        return "$status $description"
    }
} 
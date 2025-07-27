package com.tkjen.model

data class Task(
    val name: String,
    val description: String,
    val priority: Priority
)
enum class Priority {
    Low, Medium, High, Vital
}

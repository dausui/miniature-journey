package com.example.todoapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val dueDate: Date? = null,
    val category: String = "General",
    val priority: Priority = Priority.MEDIUM
)

enum class Priority {
    LOW, MEDIUM, HIGH, URGENT
}

enum class TodoFilter {
    ALL, ACTIVE, COMPLETED
}
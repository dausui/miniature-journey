package com.example.todoapp.domain.repository

import com.example.todoapp.data.models.Todo
import com.example.todoapp.data.models.TodoFilter
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getTodos(filter: TodoFilter): Flow<List<Todo>>
    fun searchTodos(query: String): Flow<List<Todo>>
    fun getTodosByCategory(category: String): Flow<List<Todo>>
    suspend fun getTodoById(id: Long): Todo?
    suspend fun insertTodo(todo: Todo): Long
    suspend fun updateTodo(todo: Todo)
    suspend fun deleteTodo(todo: Todo)
    suspend fun deleteTodoById(id: Long)
    suspend fun deleteCompletedTodos()
    suspend fun toggleTodoCompletion(id: Long)
    suspend fun getTotalCount(): Int
    suspend fun getActiveCount(): Int
    suspend fun getCompletedCount(): Int
}
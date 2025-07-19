package com.example.todoapp.data.database

import androidx.room.*
import com.example.todoapp.data.models.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    
    @Query("SELECT * FROM todos ORDER BY createdAt DESC")
    fun getAllTodos(): Flow<List<Todo>>
    
    @Query("SELECT * FROM todos WHERE isCompleted = 0 ORDER BY createdAt DESC")
    fun getActiveTodos(): Flow<List<Todo>>
    
    @Query("SELECT * FROM todos WHERE isCompleted = 1 ORDER BY updatedAt DESC")
    fun getCompletedTodos(): Flow<List<Todo>>
    
    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getTodoById(id: Long): Todo?
    
    @Query("SELECT * FROM todos WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchTodos(query: String): Flow<List<Todo>>
    
    @Query("SELECT * FROM todos WHERE category = :category ORDER BY createdAt DESC")
    fun getTodosByCategory(category: String): Flow<List<Todo>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: Todo): Long
    
    @Update
    suspend fun updateTodo(todo: Todo)
    
    @Delete
    suspend fun deleteTodo(todo: Todo)
    
    @Query("DELETE FROM todos WHERE id = :id")
    suspend fun deleteTodoById(id: Long)
    
    @Query("DELETE FROM todos WHERE isCompleted = 1")
    suspend fun deleteCompletedTodos()
    
    @Query("SELECT COUNT(*) FROM todos")
    suspend fun getTotalCount(): Int
    
    @Query("SELECT COUNT(*) FROM todos WHERE isCompleted = 0")
    suspend fun getActiveCount(): Int
    
    @Query("SELECT COUNT(*) FROM todos WHERE isCompleted = 1")
    suspend fun getCompletedCount(): Int
}
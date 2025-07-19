package com.example.todoapp.data.repository

import com.example.todoapp.data.database.TodoDao
import com.example.todoapp.data.models.Todo
import com.example.todoapp.data.models.TodoFilter
import com.example.todoapp.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepositoryImpl @Inject constructor(
    private val todoDao: TodoDao
) : TodoRepository {

    override fun getTodos(filter: TodoFilter): Flow<List<Todo>> {
        return when (filter) {
            TodoFilter.ALL -> todoDao.getAllTodos()
            TodoFilter.ACTIVE -> todoDao.getActiveTodos()
            TodoFilter.COMPLETED -> todoDao.getCompletedTodos()
        }
    }

    override fun searchTodos(query: String): Flow<List<Todo>> {
        return todoDao.searchTodos(query)
    }

    override fun getTodosByCategory(category: String): Flow<List<Todo>> {
        return todoDao.getTodosByCategory(category)
    }

    override suspend fun getTodoById(id: Long): Todo? {
        return todoDao.getTodoById(id)
    }

    override suspend fun insertTodo(todo: Todo): Long {
        return todoDao.insertTodo(todo)
    }

    override suspend fun updateTodo(todo: Todo) {
        todoDao.updateTodo(todo.copy(updatedAt = Date()))
    }

    override suspend fun deleteTodo(todo: Todo) {
        todoDao.deleteTodo(todo)
    }

    override suspend fun deleteTodoById(id: Long) {
        todoDao.deleteTodoById(id)
    }

    override suspend fun deleteCompletedTodos() {
        todoDao.deleteCompletedTodos()
    }

    override suspend fun toggleTodoCompletion(id: Long) {
        val todo = todoDao.getTodoById(id)
        todo?.let {
            todoDao.updateTodo(
                it.copy(
                    isCompleted = !it.isCompleted,
                    updatedAt = Date()
                )
            )
        }
    }

    override suspend fun getTotalCount(): Int {
        return todoDao.getTotalCount()
    }

    override suspend fun getActiveCount(): Int {
        return todoDao.getActiveCount()
    }

    override suspend fun getCompletedCount(): Int {
        return todoDao.getCompletedCount()
    }
}
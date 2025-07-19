package com.example.todoapp.domain.usecases

import com.example.todoapp.data.models.Todo
import com.example.todoapp.data.models.TodoFilter
import com.example.todoapp.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class TodoUseCases(
    val getTodos: GetTodosUseCase,
    val getTodoById: GetTodoByIdUseCase,
    val insertTodo: InsertTodoUseCase,
    val updateTodo: UpdateTodoUseCase,
    val deleteTodo: DeleteTodoUseCase,
    val toggleTodoCompletion: ToggleTodoCompletionUseCase,
    val searchTodos: SearchTodosUseCase,
    val deleteCompletedTodos: DeleteCompletedTodosUseCase,
    val getTodoStats: GetTodoStatsUseCase
)

class GetTodosUseCase @Inject constructor(
    private val repository: TodoRepository
) {
    operator fun invoke(filter: TodoFilter): Flow<List<Todo>> {
        return repository.getTodos(filter)
    }
}

class GetTodoByIdUseCase @Inject constructor(
    private val repository: TodoRepository
) {
    suspend operator fun invoke(id: Long): Todo? {
        return repository.getTodoById(id)
    }
}

class InsertTodoUseCase @Inject constructor(
    private val repository: TodoRepository
) {
    suspend operator fun invoke(todo: Todo): Long {
        if (todo.title.isBlank()) {
            throw IllegalArgumentException("Todo title cannot be empty")
        }
        return repository.insertTodo(todo)
    }
}

class UpdateTodoUseCase @Inject constructor(
    private val repository: TodoRepository
) {
    suspend operator fun invoke(todo: Todo) {
        if (todo.title.isBlank()) {
            throw IllegalArgumentException("Todo title cannot be empty")
        }
        repository.updateTodo(todo)
    }
}

class DeleteTodoUseCase @Inject constructor(
    private val repository: TodoRepository
) {
    suspend operator fun invoke(todo: Todo) {
        repository.deleteTodo(todo)
    }
}

class ToggleTodoCompletionUseCase @Inject constructor(
    private val repository: TodoRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.toggleTodoCompletion(id)
    }
}

class SearchTodosUseCase @Inject constructor(
    private val repository: TodoRepository
) {
    operator fun invoke(query: String): Flow<List<Todo>> {
        return repository.searchTodos(query)
    }
}

class DeleteCompletedTodosUseCase @Inject constructor(
    private val repository: TodoRepository
) {
    suspend operator fun invoke() {
        repository.deleteCompletedTodos()
    }
}

data class TodoStats(
    val total: Int,
    val active: Int,
    val completed: Int
)

class GetTodoStatsUseCase @Inject constructor(
    private val repository: TodoRepository
) {
    suspend operator fun invoke(): TodoStats {
        return TodoStats(
            total = repository.getTotalCount(),
            active = repository.getActiveCount(),
            completed = repository.getCompletedCount()
        )
    }
}
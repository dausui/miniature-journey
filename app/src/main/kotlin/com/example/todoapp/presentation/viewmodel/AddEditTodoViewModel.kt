package com.example.todoapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.models.Todo
import com.example.todoapp.data.models.Priority
import com.example.todoapp.domain.usecases.TodoUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class AddEditTodoState(
    val todo: Todo? = null,
    val title: String = "",
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val category: String = "General",
    val dueDate: Date? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)

sealed class AddEditTodoEvent {
    data class LoadTodo(val id: Long) : AddEditTodoEvent()
    data class TitleChanged(val title: String) : AddEditTodoEvent()
    data class DescriptionChanged(val description: String) : AddEditTodoEvent()
    data class PriorityChanged(val priority: Priority) : AddEditTodoEvent()
    data class CategoryChanged(val category: String) : AddEditTodoEvent()
    data class DueDateChanged(val date: Date?) : AddEditTodoEvent()
    object SaveTodo : AddEditTodoEvent()
}

@HiltViewModel
class AddEditTodoViewModel @Inject constructor(
    private val todoUseCases: TodoUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditTodoState())
    val state: StateFlow<AddEditTodoState> = _state.asStateFlow()

    fun onEvent(event: AddEditTodoEvent) {
        when (event) {
            is AddEditTodoEvent.LoadTodo -> {
                loadTodo(event.id)
            }
            is AddEditTodoEvent.TitleChanged -> {
                _state.value = _state.value.copy(title = event.title)
            }
            is AddEditTodoEvent.DescriptionChanged -> {
                _state.value = _state.value.copy(description = event.description)
            }
            is AddEditTodoEvent.PriorityChanged -> {
                _state.value = _state.value.copy(priority = event.priority)
            }
            is AddEditTodoEvent.CategoryChanged -> {
                _state.value = _state.value.copy(category = event.category)
            }
            is AddEditTodoEvent.DueDateChanged -> {
                _state.value = _state.value.copy(dueDate = event.date)
            }
            is AddEditTodoEvent.SaveTodo -> {
                saveTodo()
            }
        }
    }

    private fun loadTodo(id: Long) {
        if (id != -1L) {
            _state.value = _state.value.copy(isLoading = true)
            viewModelScope.launch {
                try {
                    val todo = todoUseCases.getTodoById(id)
                    todo?.let {
                        _state.value = _state.value.copy(
                            todo = it,
                            title = it.title,
                            description = it.description,
                            priority = it.priority,
                            category = it.category,
                            dueDate = it.dueDate,
                            isLoading = false
                        )
                    } ?: run {
                        _state.value = _state.value.copy(
                            error = "Todo not found",
                            isLoading = false
                        )
                    }
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        error = e.message ?: "Failed to load todo",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun saveTodo() {
        val currentState = _state.value
        
        if (currentState.title.isBlank()) {
            _state.value = _state.value.copy(error = "Title cannot be empty")
            return
        }

        _state.value = _state.value.copy(isLoading = true)
        
        viewModelScope.launch {
            try {
                val todo = currentState.todo?.copy(
                    title = currentState.title,
                    description = currentState.description,
                    priority = currentState.priority,
                    category = currentState.category,
                    dueDate = currentState.dueDate,
                    updatedAt = Date()
                ) ?: Todo(
                    title = currentState.title,
                    description = currentState.description,
                    priority = currentState.priority,
                    category = currentState.category,
                    dueDate = currentState.dueDate
                )

                if (currentState.todo != null) {
                    todoUseCases.updateTodo(todo)
                } else {
                    todoUseCases.insertTodo(todo)
                }

                _state.value = _state.value.copy(
                    isLoading = false,
                    isSaved = true
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Failed to save todo",
                    isLoading = false
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
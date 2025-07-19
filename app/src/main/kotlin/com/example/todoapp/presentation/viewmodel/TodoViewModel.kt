package com.example.todoapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.models.Todo
import com.example.todoapp.data.models.TodoFilter
import com.example.todoapp.data.models.Priority
import com.example.todoapp.domain.usecases.TodoUseCases
import com.example.todoapp.domain.usecases.TodoStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class TodoUiState(
    val todos: List<Todo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentFilter: TodoFilter = TodoFilter.ALL,
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val stats: TodoStats = TodoStats(0, 0, 0)
)

sealed class TodoUiEvent {
    object LoadTodos : TodoUiEvent()
    data class FilterChanged(val filter: TodoFilter) : TodoUiEvent()
    data class SearchQueryChanged(val query: String) : TodoUiEvent()
    data class ToggleTodoCompletion(val id: Long) : TodoUiEvent()
    data class DeleteTodo(val todo: Todo) : TodoUiEvent()
    object DeleteCompletedTodos : TodoUiEvent()
    object RefreshStats : TodoUiEvent()
}

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoUseCases: TodoUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodoUiState())
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _currentFilter = MutableStateFlow(TodoFilter.ALL)

    init {
        loadTodos()
        loadStats()
        observeSearchAndFilter()
    }

    fun onEvent(event: TodoUiEvent) {
        when (event) {
            is TodoUiEvent.LoadTodos -> loadTodos()
            is TodoUiEvent.FilterChanged -> {
                _currentFilter.value = event.filter
                _uiState.value = _uiState.value.copy(currentFilter = event.filter)
            }
            is TodoUiEvent.SearchQueryChanged -> {
                _searchQuery.value = event.query
                _uiState.value = _uiState.value.copy(
                    searchQuery = event.query,
                    isSearching = event.query.isNotBlank()
                )
            }
            is TodoUiEvent.ToggleTodoCompletion -> {
                toggleTodoCompletion(event.id)
            }
            is TodoUiEvent.DeleteTodo -> {
                deleteTodo(event.todo)
            }
            is TodoUiEvent.DeleteCompletedTodos -> {
                deleteCompletedTodos()
            }
            is TodoUiEvent.RefreshStats -> {
                loadStats()
            }
        }
    }

    private fun observeSearchAndFilter() {
        combine(
            _searchQuery,
            _currentFilter
        ) { query, filter ->
            Pair(query, filter)
        }.flatMapLatest { (query, filter) ->
            if (query.isBlank()) {
                todoUseCases.getTodos(filter)
            } else {
                todoUseCases.searchTodos(query)
            }
        }.onEach { todos ->
            _uiState.value = _uiState.value.copy(
                todos = todos,
                isLoading = false
            )
        }.catch { error ->
            _uiState.value = _uiState.value.copy(
                error = error.message,
                isLoading = false
            )
        }.launchIn(viewModelScope)
    }

    private fun loadTodos() {
        _uiState.value = _uiState.value.copy(isLoading = true)
    }

    private fun loadStats() {
        viewModelScope.launch {
            try {
                val stats = todoUseCases.getTodoStats()
                _uiState.value = _uiState.value.copy(stats = stats)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    private fun toggleTodoCompletion(id: Long) {
        viewModelScope.launch {
            try {
                todoUseCases.toggleTodoCompletion(id)
                loadStats()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    private fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            try {
                todoUseCases.deleteTodo(todo)
                loadStats()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    private fun deleteCompletedTodos() {
        viewModelScope.launch {
            try {
                todoUseCases.deleteCompletedTodos()
                loadStats()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
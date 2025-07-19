package com.example.todoapp.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.todoapp.data.models.Priority
import com.example.todoapp.data.models.Todo
import com.example.todoapp.data.models.TodoFilter
import com.example.todoapp.domain.usecases.TodoStats
import com.example.todoapp.domain.usecases.TodoUseCases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Date

@ExtendWith(MockitoExtension::class)
@OptIn(ExperimentalCoroutinesApi::class)
class TodoViewModelTest {

    private lateinit var todoUseCases: TodoUseCases
    private lateinit var viewModel: TodoViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    private val sampleTodo = Todo(
        id = 1L,
        title = "Test Todo",
        description = "Test Description",
        priority = Priority.HIGH,
        category = "Work",
        isCompleted = false,
        createdAt = Date(),
        updatedAt = Date()
    )

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        todoUseCases = mock()
        
        // Setup default mocks
        whenever(todoUseCases.getTodos.invoke(TodoFilter.ALL)).thenReturn(flowOf(emptyList()))
        whenever(todoUseCases.searchTodos.invoke("")).thenReturn(flowOf(emptyList()))
        runTest {
            whenever(todoUseCases.getTodoStats.invoke()).thenReturn(TodoStats(0, 0, 0))
        }
        
        viewModel = TodoViewModel(todoUseCases)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals(TodoFilter.ALL, initialState.currentFilter)
            assertEquals("", initialState.searchQuery)
            assertFalse(initialState.isSearching)
            assertFalse(initialState.isLoading)
            assertEquals(TodoStats(0, 0, 0), initialState.stats)
        }
    }

    @Test
    fun `filter changed event updates current filter`() = runTest {
        // Given
        whenever(todoUseCases.getTodos.invoke(TodoFilter.ACTIVE)).thenReturn(flowOf(listOf(sampleTodo)))

        viewModel.uiState.test {
            skipItems(1) // Skip initial state
            
            // When
            viewModel.onEvent(TodoUiEvent.FilterChanged(TodoFilter.ACTIVE))
            
            // Then
            val updatedState = awaitItem()
            assertEquals(TodoFilter.ACTIVE, updatedState.currentFilter)
        }
    }

    @Test
    fun `search query changed event updates search state`() = runTest {
        // Given
        val searchQuery = "test query"
        whenever(todoUseCases.searchTodos.invoke(searchQuery)).thenReturn(flowOf(listOf(sampleTodo)))

        viewModel.uiState.test {
            skipItems(1) // Skip initial state
            
            // When
            viewModel.onEvent(TodoUiEvent.SearchQueryChanged(searchQuery))
            
            // Then
            val updatedState = awaitItem()
            assertEquals(searchQuery, updatedState.searchQuery)
            assertTrue(updatedState.isSearching)
        }
    }

    @Test
    fun `toggle todo completion calls use case`() = runTest {
        // When
        viewModel.onEvent(TodoUiEvent.ToggleTodoCompletion(1L))
        
        // Then
        verify(todoUseCases.toggleTodoCompletion).invoke(1L)
    }

    @Test
    fun `delete todo calls use case`() = runTest {
        // When
        viewModel.onEvent(TodoUiEvent.DeleteTodo(sampleTodo))
        
        // Then
        verify(todoUseCases.deleteTodo).invoke(sampleTodo)
    }

    @Test
    fun `delete completed todos calls use case`() = runTest {
        // When
        viewModel.onEvent(TodoUiEvent.DeleteCompletedTodos)
        
        // Then
        verify(todoUseCases.deleteCompletedTodos).invoke()
    }

    @Test
    fun `search with empty query shows filtered todos`() = runTest {
        // Given
        val todos = listOf(sampleTodo)
        whenever(todoUseCases.getTodos.invoke(TodoFilter.ALL)).thenReturn(flowOf(todos))

        viewModel.uiState.test {
            skipItems(1) // Skip initial state
            
            // When
            viewModel.onEvent(TodoUiEvent.SearchQueryChanged(""))
            
            // Then
            val updatedState = awaitItem()
            assertFalse(updatedState.isSearching)
            assertEquals(todos, updatedState.todos)
        }
    }

    @Test
    fun `search with non-empty query shows search results`() = runTest {
        // Given
        val searchQuery = "work"
        val searchResults = listOf(sampleTodo)
        whenever(todoUseCases.searchTodos.invoke(searchQuery)).thenReturn(flowOf(searchResults))

        viewModel.uiState.test {
            skipItems(1) // Skip initial state
            
            // When
            viewModel.onEvent(TodoUiEvent.SearchQueryChanged(searchQuery))
            
            // Then
            val updatedState = awaitItem()
            assertTrue(updatedState.isSearching)
            assertEquals(searchResults, updatedState.todos)
        }
    }

    @Test
    fun `clearError clears error state`() = runTest {
        // Set an error state first by triggering an exception
        whenever(todoUseCases.getTodoStats.invoke()).thenThrow(RuntimeException("Test error"))
        
        viewModel.uiState.test {
            skipItems(1) // Skip initial state
            
            // When
            viewModel.onEvent(TodoUiEvent.RefreshStats)
            val errorState = awaitItem()
            assertNotNull(errorState.error)
            
            viewModel.clearError()
            val clearedState = awaitItem()
            
            // Then
            assertNull(clearedState.error)
        }
    }
}
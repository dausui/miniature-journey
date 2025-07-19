package com.example.todoapp.domain.usecases

import com.example.todoapp.data.models.Priority
import com.example.todoapp.data.models.Todo
import com.example.todoapp.data.models.TodoFilter
import com.example.todoapp.domain.repository.TodoRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Date

class TodoUseCasesTest {

    private lateinit var repository: TodoRepository
    private lateinit var useCases: TodoUseCases

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
        repository = mock()
        useCases = TodoUseCases(
            getTodos = GetTodosUseCase(repository),
            getTodoById = GetTodoByIdUseCase(repository),
            insertTodo = InsertTodoUseCase(repository),
            updateTodo = UpdateTodoUseCase(repository),
            deleteTodo = DeleteTodoUseCase(repository),
            toggleTodoCompletion = ToggleTodoCompletionUseCase(repository),
            searchTodos = SearchTodosUseCase(repository),
            deleteCompletedTodos = DeleteCompletedTodosUseCase(repository),
            getTodoStats = GetTodoStatsUseCase(repository)
        )
    }

    @Test
    fun `getTodos returns todos from repository`() = runTest {
        // Given
        val todos = listOf(sampleTodo)
        whenever(repository.getTodos(TodoFilter.ALL)).thenReturn(flowOf(todos))

        // When
        val result = useCases.getTodos(TodoFilter.ALL).first()

        // Then
        assertEquals(todos, result)
        verify(repository).getTodos(TodoFilter.ALL)
    }

    @Test
    fun `getTodoById returns todo from repository`() = runTest {
        // Given
        whenever(repository.getTodoById(1L)).thenReturn(sampleTodo)

        // When
        val result = useCases.getTodoById(1L)

        // Then
        assertEquals(sampleTodo, result)
        verify(repository).getTodoById(1L)
    }

    @Test
    fun `insertTodo throws exception when title is blank`() = runTest {
        // Given
        val todoWithBlankTitle = sampleTodo.copy(title = "")

        // When & Then
        assertThrows<IllegalArgumentException> {
            useCases.insertTodo(todoWithBlankTitle)
        }
    }

    @Test
    fun `insertTodo succeeds with valid todo`() = runTest {
        // Given
        whenever(repository.insertTodo(sampleTodo)).thenReturn(1L)

        // When
        val result = useCases.insertTodo(sampleTodo)

        // Then
        assertEquals(1L, result)
        verify(repository).insertTodo(sampleTodo)
    }

    @Test
    fun `updateTodo throws exception when title is blank`() = runTest {
        // Given
        val todoWithBlankTitle = sampleTodo.copy(title = "")

        // When & Then
        assertThrows<IllegalArgumentException> {
            useCases.updateTodo(todoWithBlankTitle)
        }
    }

    @Test
    fun `updateTodo succeeds with valid todo`() = runTest {
        // When
        useCases.updateTodo(sampleTodo)

        // Then
        verify(repository).updateTodo(sampleTodo)
    }

    @Test
    fun `deleteTodo calls repository`() = runTest {
        // When
        useCases.deleteTodo(sampleTodo)

        // Then
        verify(repository).deleteTodo(sampleTodo)
    }

    @Test
    fun `toggleTodoCompletion calls repository`() = runTest {
        // When
        useCases.toggleTodoCompletion(1L)

        // Then
        verify(repository).toggleTodoCompletion(1L)
    }

    @Test
    fun `searchTodos returns search results from repository`() = runTest {
        // Given
        val searchQuery = "test"
        val searchResults = listOf(sampleTodo)
        whenever(repository.searchTodos(searchQuery)).thenReturn(flowOf(searchResults))

        // When
        val result = useCases.searchTodos(searchQuery).first()

        // Then
        assertEquals(searchResults, result)
        verify(repository).searchTodos(searchQuery)
    }

    @Test
    fun `deleteCompletedTodos calls repository`() = runTest {
        // When
        useCases.deleteCompletedTodos()

        // Then
        verify(repository).deleteCompletedTodos()
    }

    @Test
    fun `getTodoStats returns stats from repository`() = runTest {
        // Given
        whenever(repository.getTotalCount()).thenReturn(10)
        whenever(repository.getActiveCount()).thenReturn(7)
        whenever(repository.getCompletedCount()).thenReturn(3)

        // When
        val result = useCases.getTodoStats()

        // Then
        assertEquals(TodoStats(total = 10, active = 7, completed = 3), result)
    }
}
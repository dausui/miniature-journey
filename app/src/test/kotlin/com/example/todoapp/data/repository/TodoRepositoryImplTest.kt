package com.example.todoapp.data.repository

import com.example.todoapp.data.database.TodoDao
import com.example.todoapp.data.models.Priority
import com.example.todoapp.data.models.Todo
import com.example.todoapp.data.models.TodoFilter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.util.Date

class TodoRepositoryImplTest {

    private lateinit var dao: TodoDao
    private lateinit var repository: TodoRepositoryImpl

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
        dao = mock()
        repository = TodoRepositoryImpl(dao)
    }

    @Test
    fun `getTodos with ALL filter returns all todos`() = runTest {
        // Given
        val todos = listOf(sampleTodo)
        whenever(dao.getAllTodos()).thenReturn(flowOf(todos))

        // When
        val result = repository.getTodos(TodoFilter.ALL).first()

        // Then
        assertEquals(todos, result)
        verify(dao).getAllTodos()
    }

    @Test
    fun `getTodos with ACTIVE filter returns active todos`() = runTest {
        // Given
        val todos = listOf(sampleTodo)
        whenever(dao.getActiveTodos()).thenReturn(flowOf(todos))

        // When
        val result = repository.getTodos(TodoFilter.ACTIVE).first()

        // Then
        assertEquals(todos, result)
        verify(dao).getActiveTodos()
    }

    @Test
    fun `getTodos with COMPLETED filter returns completed todos`() = runTest {
        // Given
        val todos = listOf(sampleTodo.copy(isCompleted = true))
        whenever(dao.getCompletedTodos()).thenReturn(flowOf(todos))

        // When
        val result = repository.getTodos(TodoFilter.COMPLETED).first()

        // Then
        assertEquals(todos, result)
        verify(dao).getCompletedTodos()
    }

    @Test
    fun `searchTodos returns search results from dao`() = runTest {
        // Given
        val query = "test"
        val searchResults = listOf(sampleTodo)
        whenever(dao.searchTodos(query)).thenReturn(flowOf(searchResults))

        // When
        val result = repository.searchTodos(query).first()

        // Then
        assertEquals(searchResults, result)
        verify(dao).searchTodos(query)
    }

    @Test
    fun `getTodosByCategory returns todos by category from dao`() = runTest {
        // Given
        val category = "Work"
        val todos = listOf(sampleTodo)
        whenever(dao.getTodosByCategory(category)).thenReturn(flowOf(todos))

        // When
        val result = repository.getTodosByCategory(category).first()

        // Then
        assertEquals(todos, result)
        verify(dao).getTodosByCategory(category)
    }

    @Test
    fun `getTodoById returns todo from dao`() = runTest {
        // Given
        whenever(dao.getTodoById(1L)).thenReturn(sampleTodo)

        // When
        val result = repository.getTodoById(1L)

        // Then
        assertEquals(sampleTodo, result)
        verify(dao).getTodoById(1L)
    }

    @Test
    fun `insertTodo calls dao insertTodo`() = runTest {
        // Given
        whenever(dao.insertTodo(sampleTodo)).thenReturn(1L)

        // When
        val result = repository.insertTodo(sampleTodo)

        // Then
        assertEquals(1L, result)
        verify(dao).insertTodo(sampleTodo)
    }

    @Test
    fun `updateTodo calls dao updateTodo with updated timestamp`() = runTest {
        // When
        repository.updateTodo(sampleTodo)

        // Then
        argumentCaptor<Todo>().apply {
            verify(dao).updateTodo(capture())
            assertTrue(firstValue.updatedAt.after(sampleTodo.updatedAt) || 
                      firstValue.updatedAt.equals(sampleTodo.updatedAt))
        }
    }

    @Test
    fun `deleteTodo calls dao deleteTodo`() = runTest {
        // When
        repository.deleteTodo(sampleTodo)

        // Then
        verify(dao).deleteTodo(sampleTodo)
    }

    @Test
    fun `deleteTodoById calls dao deleteTodoById`() = runTest {
        // When
        repository.deleteTodoById(1L)

        // Then
        verify(dao).deleteTodoById(1L)
    }

    @Test
    fun `deleteCompletedTodos calls dao deleteCompletedTodos`() = runTest {
        // When
        repository.deleteCompletedTodos()

        // Then
        verify(dao).deleteCompletedTodos()
    }

    @Test
    fun `toggleTodoCompletion updates todo completion status`() = runTest {
        // Given
        whenever(dao.getTodoById(1L)).thenReturn(sampleTodo)

        // When
        repository.toggleTodoCompletion(1L)

        // Then
        argumentCaptor<Todo>().apply {
            verify(dao).updateTodo(capture())
            assertTrue(firstValue.isCompleted) // Should be toggled from false to true
        }
    }

    @Test
    fun `getTotalCount returns count from dao`() = runTest {
        // Given
        whenever(dao.getTotalCount()).thenReturn(10)

        // When
        val result = repository.getTotalCount()

        // Then
        assertEquals(10, result)
        verify(dao).getTotalCount()
    }

    @Test
    fun `getActiveCount returns active count from dao`() = runTest {
        // Given
        whenever(dao.getActiveCount()).thenReturn(7)

        // When
        val result = repository.getActiveCount()

        // Then
        assertEquals(7, result)
        verify(dao).getActiveCount()
    }

    @Test
    fun `getCompletedCount returns completed count from dao`() = runTest {
        // Given
        whenever(dao.getCompletedCount()).thenReturn(3)

        // When
        val result = repository.getCompletedCount()

        // Then
        assertEquals(3, result)
        verify(dao).getCompletedCount()
    }
}
package com.example.todoapp.data.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todoapp.data.models.Priority
import com.example.todoapp.data.models.Todo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

@RunWith(AndroidJUnit4::class)
class TodoDaoTest {

    private lateinit var database: TodoDatabase
    private lateinit var dao: TodoDao

    private val sampleTodo1 = Todo(
        title = "Test Todo 1",
        description = "Description 1",
        priority = Priority.HIGH,
        category = "Work",
        isCompleted = false,
        createdAt = Date(System.currentTimeMillis() - 1000),
        updatedAt = Date(System.currentTimeMillis() - 1000)
    )

    private val sampleTodo2 = Todo(
        title = "Test Todo 2",
        description = "Description 2",
        priority = Priority.MEDIUM,
        category = "Personal",
        isCompleted = true,
        createdAt = Date(),
        updatedAt = Date()
    )

    @Before
    fun createDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TodoDatabase::class.java
        ).build()
        dao = database.todoDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndGetTodo() = runTest {
        // When
        val id = dao.insertTodo(sampleTodo1)
        val retrieved = dao.getTodoById(id)

        // Then
        assertNotNull(retrieved)
        assertEquals(sampleTodo1.title, retrieved!!.title)
        assertEquals(sampleTodo1.description, retrieved.description)
        assertEquals(sampleTodo1.priority, retrieved.priority)
        assertEquals(sampleTodo1.category, retrieved.category)
        assertEquals(sampleTodo1.isCompleted, retrieved.isCompleted)
    }

    @Test
    fun getAllTodos() = runTest {
        // Given
        dao.insertTodo(sampleTodo1)
        dao.insertTodo(sampleTodo2)

        // When
        val todos = dao.getAllTodos().first()

        // Then
        assertEquals(2, todos.size)
        // Should be ordered by createdAt DESC
        assertEquals(sampleTodo2.title, todos[0].title)
        assertEquals(sampleTodo1.title, todos[1].title)
    }

    @Test
    fun getActiveTodos() = runTest {
        // Given
        dao.insertTodo(sampleTodo1) // active
        dao.insertTodo(sampleTodo2) // completed

        // When
        val activeTodos = dao.getActiveTodos().first()

        // Then
        assertEquals(1, activeTodos.size)
        assertEquals(sampleTodo1.title, activeTodos[0].title)
        assertFalse(activeTodos[0].isCompleted)
    }

    @Test
    fun getCompletedTodos() = runTest {
        // Given
        dao.insertTodo(sampleTodo1) // active
        dao.insertTodo(sampleTodo2) // completed

        // When
        val completedTodos = dao.getCompletedTodos().first()

        // Then
        assertEquals(1, completedTodos.size)
        assertEquals(sampleTodo2.title, completedTodos[0].title)
        assertTrue(completedTodos[0].isCompleted)
    }

    @Test
    fun searchTodos() = runTest {
        // Given
        dao.insertTodo(sampleTodo1.copy(title = "Work Meeting", description = "Important meeting"))
        dao.insertTodo(sampleTodo2.copy(title = "Personal Task", description = "Buy groceries"))

        // When
        val searchResults = dao.searchTodos("meeting").first()

        // Then
        assertEquals(1, searchResults.size)
        assertTrue(searchResults[0].title.contains("Meeting"))
    }

    @Test
    fun getTodosByCategory() = runTest {
        // Given
        dao.insertTodo(sampleTodo1) // Work category
        dao.insertTodo(sampleTodo2) // Personal category

        // When
        val workTodos = dao.getTodosByCategory("Work").first()

        // Then
        assertEquals(1, workTodos.size)
        assertEquals("Work", workTodos[0].category)
    }

    @Test
    fun updateTodo() = runTest {
        // Given
        val id = dao.insertTodo(sampleTodo1)
        val originalTodo = dao.getTodoById(id)!!

        // When
        val updatedTodo = originalTodo.copy(
            title = "Updated Title",
            isCompleted = true
        )
        dao.updateTodo(updatedTodo)

        // Then
        val retrieved = dao.getTodoById(id)!!
        assertEquals("Updated Title", retrieved.title)
        assertTrue(retrieved.isCompleted)
    }

    @Test
    fun deleteTodo() = runTest {
        // Given
        val id = dao.insertTodo(sampleTodo1)
        assertNotNull(dao.getTodoById(id))

        // When
        dao.deleteTodoById(id)

        // Then
        assertNull(dao.getTodoById(id))
    }

    @Test
    fun deleteCompletedTodos() = runTest {
        // Given
        dao.insertTodo(sampleTodo1) // active
        dao.insertTodo(sampleTodo2) // completed

        // When
        dao.deleteCompletedTodos()

        // Then
        val allTodos = dao.getAllTodos().first()
        assertEquals(1, allTodos.size)
        assertFalse(allTodos[0].isCompleted)
    }

    @Test
    fun getCounts() = runTest {
        // Given
        dao.insertTodo(sampleTodo1) // active
        dao.insertTodo(sampleTodo2) // completed

        // When & Then
        assertEquals(2, dao.getTotalCount())
        assertEquals(1, dao.getActiveCount())
        assertEquals(1, dao.getCompletedCount())
    }
}
package com.example.todoapp.presentation.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todoapp.data.models.Priority
import com.example.todoapp.data.models.Todo
import com.example.todoapp.data.models.TodoFilter
import com.example.todoapp.domain.usecases.TodoStats
import com.example.todoapp.presentation.theme.TodoAppTheme
import com.example.todoapp.presentation.ui.screens.TodoListScreen
import com.example.todoapp.presentation.viewmodel.TodoUiState
import com.example.todoapp.presentation.viewmodel.TodoViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

@RunWith(AndroidJUnit4::class)
class TodoListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleTodos = listOf(
        Todo(
            id = 1L,
            title = "Test Todo 1",
            description = "Description 1",
            priority = Priority.HIGH,
            category = "Work",
            isCompleted = false,
            createdAt = Date(),
            updatedAt = Date()
        ),
        Todo(
            id = 2L,
            title = "Test Todo 2",
            description = "Description 2",
            priority = Priority.MEDIUM,
            category = "Personal",
            isCompleted = true,
            createdAt = Date(),
            updatedAt = Date()
        )
    )

    @Test
    fun todoListScreen_displaysEmptyState_whenNoTodos() {
        // Given
        val mockViewModel = mockk<TodoViewModel>()
        val emptyState = TodoUiState(
            todos = emptyList(),
            stats = TodoStats(0, 0, 0)
        )
        every { mockViewModel.uiState } returns MutableStateFlow(emptyState)

        // When
        composeTestRule.setContent {
            TodoAppTheme {
                TodoListScreen(
                    onNavigateToAddTodo = {},
                    onNavigateToEditTodo = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("No todos yet. Create your first todo!")
            .assertIsDisplayed()
    }

    @Test
    fun todoListScreen_displaysTodos_whenTodosExist() {
        // Given
        val mockViewModel = mockk<TodoViewModel>()
        val stateWithTodos = TodoUiState(
            todos = sampleTodos,
            stats = TodoStats(2, 1, 1)
        )
        every { mockViewModel.uiState } returns MutableStateFlow(stateWithTodos)

        // When
        composeTestRule.setContent {
            TodoAppTheme {
                TodoListScreen(
                    onNavigateToAddTodo = {},
                    onNavigateToEditTodo = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Test Todo 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Todo 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Description 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Description 2").assertIsDisplayed()
    }

    @Test
    fun todoListScreen_displaysStats_correctly() {
        // Given
        val mockViewModel = mockk<TodoViewModel>()
        val stateWithStats = TodoUiState(
            todos = sampleTodos,
            stats = TodoStats(total = 10, active = 7, completed = 3)
        )
        every { mockViewModel.uiState } returns MutableStateFlow(stateWithStats)

        // When
        composeTestRule.setContent {
            TodoAppTheme {
                TodoListScreen(
                    onNavigateToAddTodo = {},
                    onNavigateToEditTodo = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("10").assertIsDisplayed() // Total
        composeTestRule.onNodeWithText("7").assertIsDisplayed()  // Active
        composeTestRule.onNodeWithText("3").assertIsDisplayed()  // Completed
        composeTestRule.onNodeWithText("Total").assertIsDisplayed()
        composeTestRule.onNodeWithText("Active").assertIsDisplayed()
        composeTestRule.onNodeWithText("Completed").assertIsDisplayed()
    }

    @Test
    fun todoListScreen_displaysFilterChips() {
        // Given
        val mockViewModel = mockk<TodoViewModel>()
        val state = TodoUiState(
            todos = sampleTodos,
            currentFilter = TodoFilter.ALL,
            stats = TodoStats(2, 1, 1)
        )
        every { mockViewModel.uiState } returns MutableStateFlow(state)

        // When
        composeTestRule.setContent {
            TodoAppTheme {
                TodoListScreen(
                    onNavigateToAddTodo = {},
                    onNavigateToEditTodo = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("All").assertIsDisplayed()
        composeTestRule.onNodeWithText("Active").assertIsDisplayed()
        composeTestRule.onNodeWithText("Completed").assertIsDisplayed()
    }

    @Test
    fun todoListScreen_showsFabButton() {
        // Given
        val mockViewModel = mockk<TodoViewModel>()
        val state = TodoUiState(stats = TodoStats(0, 0, 0))
        every { mockViewModel.uiState } returns MutableStateFlow(state)

        // When
        composeTestRule.setContent {
            TodoAppTheme {
                TodoListScreen(
                    onNavigateToAddTodo = {},
                    onNavigateToEditTodo = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription("Add todo").assertIsDisplayed()
    }

    @Test
    fun todoListScreen_showsSearchWhenToggled() {
        // Given
        val mockViewModel = mockk<TodoViewModel>()
        val state = TodoUiState(stats = TodoStats(0, 0, 0))
        every { mockViewModel.uiState } returns MutableStateFlow(state)

        // When
        composeTestRule.setContent {
            TodoAppTheme {
                TodoListScreen(
                    onNavigateToAddTodo = {},
                    onNavigateToEditTodo = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Click search icon
        composeTestRule.onNodeWithContentDescription("Search todos").performClick()

        // Then
        composeTestRule.onNodeWithText("Search todos...").assertIsDisplayed()
    }

    @Test
    fun todoListScreen_displaysLoadingState() {
        // Given
        val mockViewModel = mockk<TodoViewModel>()
        val loadingState = TodoUiState(
            isLoading = true,
            stats = TodoStats(0, 0, 0)
        )
        every { mockViewModel.uiState } returns MutableStateFlow(loadingState)

        // When
        composeTestRule.setContent {
            TodoAppTheme {
                TodoListScreen(
                    onNavigateToAddTodo = {},
                    onNavigateToEditTodo = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Then
        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertIsDisplayed()
    }
}
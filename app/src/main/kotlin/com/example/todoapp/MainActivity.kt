package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.presentation.theme.TodoAppTheme
import com.example.todoapp.presentation.ui.screens.AddEditTodoScreen
import com.example.todoapp.presentation.ui.screens.TodoListScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoAppTheme {
                TodoNavigation()
            }
        }
    }
}

@Composable
fun TodoNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "todo_list"
    ) {
        composable("todo_list") {
            TodoListScreen(
                onNavigateToAddTodo = {
                    navController.navigate("add_edit_todo/-1")
                },
                onNavigateToEditTodo = { todoId ->
                    navController.navigate("add_edit_todo/$todoId")
                }
            )
        }
        
        composable("add_edit_todo/{todoId}") { backStackEntry ->
            val todoId = backStackEntry.arguments?.getString("todoId")?.toLongOrNull() ?: -1L
            AddEditTodoScreen(
                todoId = todoId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
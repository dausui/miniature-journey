package com.example.todoapp.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapp.data.models.TodoFilter
import com.example.todoapp.presentation.ui.components.FilterChips
import com.example.todoapp.presentation.ui.components.TodoItem
import com.example.todoapp.presentation.viewmodel.TodoUiEvent
import com.example.todoapp.presentation.viewmodel.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    onNavigateToAddTodo: () -> Unit,
    onNavigateToEditTodo: (Long) -> Unit,
    viewModel: TodoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSearch by remember { mutableStateOf(false) }
    var showDeleteCompletedDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.onEvent(TodoUiEvent.LoadTodos)
    }

    // Error handling
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show snackbar for errors if needed
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("My Todos") 
                },
                actions = {
                    IconButton(onClick = { showSearch = !showSearch }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search todos"
                        )
                    }
                    if (uiState.stats.completed > 0) {
                        IconButton(onClick = { showDeleteCompletedDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete completed todos"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddTodo,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add todo"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            if (showSearch) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { 
                        viewModel.onEvent(TodoUiEvent.SearchQueryChanged(it))
                    },
                    label = { Text("Search todos...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    singleLine = true
                )
            }
            
            // Stats
            if (!uiState.isSearching) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(
                            label = "Total",
                            value = uiState.stats.total.toString()
                        )
                        StatItem(
                            label = "Active",
                            value = uiState.stats.active.toString()
                        )
                        StatItem(
                            label = "Completed",
                            value = uiState.stats.completed.toString()
                        )
                    }
                }
            }
            
            // Filter chips
            if (!uiState.isSearching) {
                FilterChips(
                    currentFilter = uiState.currentFilter,
                    onFilterChanged = { filter ->
                        viewModel.onEvent(TodoUiEvent.FilterChanged(filter))
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            // Todo list
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.todos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (uiState.isSearching) {
                                "No todos found for \"${uiState.searchQuery}\""
                            } else {
                                when (uiState.currentFilter) {
                                    TodoFilter.ALL -> "No todos yet. Create your first todo!"
                                    TodoFilter.ACTIVE -> "No active todos"
                                    TodoFilter.COMPLETED -> "No completed todos"
                                }
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(
                        items = uiState.todos,
                        key = { it.id }
                    ) { todo ->
                        TodoItem(
                            todo = todo,
                            onToggleComplete = { id ->
                                viewModel.onEvent(TodoUiEvent.ToggleTodoCompletion(id))
                            },
                            onEdit = onNavigateToEditTodo,
                            onDelete = { todo ->
                                viewModel.onEvent(TodoUiEvent.DeleteTodo(todo))
                            }
                        )
                    }
                }
            }
        }
    }
    
    // Delete completed todos dialog
    if (showDeleteCompletedDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteCompletedDialog = false },
            title = { Text("Delete Completed Todos") },
            text = { 
                Text("Are you sure you want to delete all ${uiState.stats.completed} completed todos?") 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onEvent(TodoUiEvent.DeleteCompletedTodos)
                        showDeleteCompletedDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteCompletedDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
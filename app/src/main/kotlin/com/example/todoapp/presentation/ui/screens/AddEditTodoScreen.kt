package com.example.todoapp.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoapp.data.models.Priority
import com.example.todoapp.presentation.viewmodel.AddEditTodoEvent
import com.example.todoapp.presentation.viewmodel.AddEditTodoViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTodoScreen(
    todoId: Long = -1L,
    onNavigateBack: () -> Unit,
    viewModel: AddEditTodoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    LaunchedEffect(todoId) {
        if (todoId != -1L) {
            viewModel.onEvent(AddEditTodoEvent.LoadTodo(todoId))
        }
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onNavigateBack()
        }
    }

    // Error handling
    state.error?.let { error ->
        LaunchedEffect(error) {
            // Show snackbar for errors if needed
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(if (todoId == -1L) "Add Todo" else "Edit Todo")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.onEvent(AddEditTodoEvent.SaveTodo) },
                        enabled = !state.isLoading && state.title.isNotBlank()
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Save")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            OutlinedTextField(
                value = state.title,
                onValueChange = { viewModel.onEvent(AddEditTodoEvent.TitleChanged(it)) },
                label = { Text("Title *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.error?.contains("title", ignoreCase = true) == true
            )

            // Description
            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onEvent(AddEditTodoEvent.DescriptionChanged(it)) },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // Priority selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Priority",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Priority.values().forEach { priority ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = state.priority == priority,
                                onClick = { 
                                    viewModel.onEvent(AddEditTodoEvent.PriorityChanged(priority))
                                }
                            )
                            Text(
                                text = priority.name.lowercase().replaceFirstChar { it.uppercase() },
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }

            // Category
            OutlinedTextField(
                value = state.category,
                onValueChange = { viewModel.onEvent(AddEditTodoEvent.CategoryChanged(it)) },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Due date
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Due Date",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Row {
                            if (state.dueDate != null) {
                                TextButton(
                                    onClick = { 
                                        viewModel.onEvent(AddEditTodoEvent.DueDateChanged(null))
                                    }
                                ) {
                                    Text("Clear")
                                }
                            }
                            
                            TextButton(
                                onClick = {
                                    // In a real app, you would open a date picker here
                                    // For now, set to tomorrow
                                    val tomorrow = Calendar.getInstance().apply {
                                        add(Calendar.DAY_OF_MONTH, 1)
                                    }.time
                                    viewModel.onEvent(AddEditTodoEvent.DueDateChanged(tomorrow))
                                }
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Set Date")
                                }
                            }
                        }
                    }
                    
                    state.dueDate?.let { date ->
                        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
                        Text(
                            text = "Due: ${dateFormat.format(date)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            // Error display
            state.error?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
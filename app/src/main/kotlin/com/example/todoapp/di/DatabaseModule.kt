package com.example.todoapp.di

import android.content.Context
import androidx.room.Room
import com.example.todoapp.data.database.TodoDao
import com.example.todoapp.data.database.TodoDatabase
import com.example.todoapp.data.repository.TodoRepositoryImpl
import com.example.todoapp.domain.repository.TodoRepository
import com.example.todoapp.domain.usecases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TodoDatabase {
        return Room.databaseBuilder(
            context,
            TodoDatabase::class.java,
            TodoDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideTodoDao(database: TodoDatabase): TodoDao {
        return database.todoDao()
    }

    @Provides
    @Singleton
    fun provideTodoRepository(dao: TodoDao): TodoRepository {
        return TodoRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideTodoUseCases(repository: TodoRepository): TodoUseCases {
        return TodoUseCases(
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
}
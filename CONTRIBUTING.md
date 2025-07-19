# Contributing to TodoApp

Thank you for your interest in contributing to TodoApp! This document provides guidelines and information for contributors.

## 🚀 Getting Started

### Prerequisites

Before you begin, ensure you have:
- **Android Studio Hedgehog** or newer
- **JDK 17** or newer
- **Git** configured with your GitHub account
- Basic knowledge of **Kotlin** and **Android development**

### Setting Up Development Environment

1. **Fork and clone the repository**
   ```bash
   git clone https://github.com/yourusername/TodoApp.git
   cd TodoApp
   ```

2. **Create a new branch for your feature**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Set up the project**
   ```bash
   ./gradlew build
   ```

## 📝 Code Style Guidelines

### Kotlin Code Style
We follow the [official Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html).

#### Key Points:
- Use **4 spaces** for indentation
- **PascalCase** for class names
- **camelCase** for function and variable names
- **UPPER_SNAKE_CASE** for constants
- Line length limit: **120 characters**

#### Example:
```kotlin
class TodoRepository @Inject constructor(
    private val todoDao: TodoDao
) {
    suspend fun insertTodo(todo: Todo): Long {
        return todoDao.insertTodo(todo)
    }
}
```

### Compose Guidelines
- Use **@Composable** functions for UI components
- Follow **Material Design 3** principles
- Keep composables **stateless** when possible
- Use **remember** for state that survives recomposition

#### Example:
```kotlin
@Composable
fun TodoItem(
    todo: Todo,
    onToggleComplete: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        // UI implementation
    }
}
```

## 🏗️ Architecture Guidelines

### Clean Architecture Layers

1. **Presentation Layer** (`presentation/`)
   - UI components (Compose)
   - ViewModels
   - UI state management

2. **Domain Layer** (`domain/`)
   - Use cases (business logic)
   - Repository interfaces
   - Domain models

3. **Data Layer** (`data/`)
   - Repository implementations
   - Database (Room)
   - Data models

### Dependency Rules
- **Presentation** depends on **Domain**
- **Data** depends on **Domain**
- **Domain** has no dependencies on other layers

## 🧪 Testing Guidelines

### Test Structure
```
src/
├── test/                    # Unit tests
│   ├── domain/             # Use case tests
│   ├── data/               # Repository tests
│   └── presentation/       # ViewModel tests
└── androidTest/            # Integration tests
    ├── database/           # Room tests
    └── ui/                 # Compose UI tests
```

### Writing Tests

#### Unit Tests
```kotlin
@Test
fun `insertTodo should save todo to repository`() = runTest {
    // Given
    val todo = Todo(title = "Test Todo")
    
    // When
    val result = insertTodoUseCase(todo)
    
    // Then
    assertEquals(1L, result)
    verify(repository).insertTodo(todo)
}
```

#### UI Tests
```kotlin
@Test
fun todoList_displaysCorrectly() {
    composeTestRule.setContent {
        TodoListScreen(
            onNavigateToAddTodo = {},
            onNavigateToEditTodo = {}
        )
    }
    
    composeTestRule.onNodeWithText("Add Todo")
        .assertIsDisplayed()
}
```

### Test Requirements
- **Unit tests**: Cover business logic and ViewModels
- **Integration tests**: Test database operations
- **UI tests**: Test user interactions
- **Minimum coverage**: 80%

## 📋 Pull Request Process

### Before Submitting
1. **Run all tests**
   ```bash
   ./gradlew check
   ```

2. **Ensure code quality passes**
   ```bash
   ./gradlew detekt ktlintCheck
   ```

3. **Update documentation** if needed

### PR Template
When creating a PR, include:

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing completed

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] Tests pass locally
```

### Review Process
1. **Automated checks** must pass (CI/CD pipeline)
2. **Code review** by maintainers
3. **Testing** on different devices/API levels
4. **Approval** and merge

## 🐛 Bug Reports

### Bug Report Template
```markdown
**Describe the bug**
A clear description of the bug

**To Reproduce**
Steps to reproduce the behavior:
1. Go to '...'
2. Click on '....'
3. See error

**Expected behavior**
What you expected to happen

**Screenshots**
If applicable, add screenshots

**Device information:**
- Device: [e.g. Pixel 6]
- OS: [e.g. Android 13]
- App Version: [e.g. 1.0.0]
```

## ✨ Feature Requests

### Feature Request Template
```markdown
**Is your feature request related to a problem?**
A clear description of the problem

**Describe the solution you'd like**
A clear description of what you want to happen

**Describe alternatives you've considered**
Any alternative solutions or features

**Additional context**
Any other context or screenshots
```

## 🎯 Areas for Contribution

### High Priority
- 🐛 **Bug fixes**
- 📱 **UI/UX improvements**
- ⚡ **Performance optimizations**
- 🧪 **Test coverage improvements**

### Medium Priority
- 🌐 **Internationalization (i18n)**
- ♿ **Accessibility improvements**
- 📱 **Tablet UI optimizations**
- 🔄 **Additional filter options**

### Low Priority
- 🎨 **Custom themes**
- 📊 **Analytics integration**
- ☁️ **Cloud sync features**
- 📱 **Widget support**

## 📚 Resources

### Learning Resources
- [Android Developers](https://developer.android.com/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

### Tools
- [Android Studio](https://developer.android.com/studio)
- [Detekt](https://detekt.github.io/detekt/)
- [Ktlint](https://pinterest.github.io/ktlint/)

## 💬 Communication

### Channels
- **GitHub Issues**: Bug reports and feature requests
- **GitHub Discussions**: General questions and ideas
- **Pull Requests**: Code contributions and reviews

### Guidelines
- Be **respectful** and **constructive**
- **Search existing issues** before creating new ones
- Use **clear and descriptive** titles
- Provide **detailed descriptions** and context

## 📄 License

By contributing to TodoApp, you agree that your contributions will be licensed under the MIT License.

---

Thank you for contributing to TodoApp! 🎉
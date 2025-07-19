# 📝 TodoApp - Modern Android TODO List Application

[![CI/CD Pipeline](https://github.com/yourusername/TodoApp/actions/workflows/ci.yml/badge.svg)](https://github.com/yourusername/TodoApp/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/yourusername/TodoApp/branch/main/graph/badge.svg)](https://codecov.io/gh/yourusername/TodoApp)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A modern, production-ready Android TODO list application built with Kotlin, Jetpack Compose, and Clean Architecture. Features comprehensive CI/CD pipeline, extensive testing, and follows Android development best practices.

## ✨ Features

### Core Functionality
- ✅ **Add, Edit, Delete** todos with rich text support
- ✅ **Mark as Complete/Incomplete** with smooth animations
- ✅ **Filter todos** by status (All, Active, Completed)
- ✅ **Search functionality** with real-time results
- ✅ **Categories and Tags** for better organization
- ✅ **Priority levels** (Low, Medium, High, Urgent) with color coding
- ✅ **Due dates** with visual indicators
- ✅ **Statistics dashboard** showing progress
- ✅ **Bulk operations** (delete completed todos)
- ✅ **Persistent local storage** with Room database

### Technical Features
- 🏗️ **Clean Architecture** with MVVM pattern
- 🎨 **Modern UI** with Jetpack Compose and Material Design 3
- 💉 **Dependency Injection** with Hilt/Dagger
- 🔄 **Reactive Programming** with Kotlin Coroutines and Flow
- 🗄️ **Local Database** with Room
- 🧪 **Comprehensive Testing** (Unit, Integration, UI tests)
- 🚀 **CI/CD Pipeline** with GitHub Actions
- 📊 **Code Coverage** with Jacoco
- 🔍 **Static Analysis** with Detekt and Ktlint
- 🔒 **Security Scanning** for vulnerabilities
- 📱 **Support for Android 7.0+** (API level 24+)

## 📱 Screenshots

| Home Screen | Add/Edit Todo | Filter & Search |
|-------------|---------------|-----------------|
| ![Home](docs/screenshots/home.png) | ![Add/Edit](docs/screenshots/add_edit.png) | ![Filter](docs/screenshots/filter.png) |

## 🏗️ Architecture

This project follows **Clean Architecture** principles with **MVVM** pattern:

```
app/
├── data/                    # Data Layer
│   ├── database/           # Room database, DAOs, entities
│   ├── repository/         # Repository implementations
│   └── models/             # Data models
├── domain/                  # Domain Layer (Business Logic)
│   ├── usecases/           # Use cases/Interactors
│   └── repository/         # Repository interfaces
├── presentation/           # Presentation Layer
│   ├── ui/                 # Compose UI components
│   ├── viewmodel/          # ViewModels
│   └── theme/              # Material Design theme
└── di/                     # Dependency Injection modules
```

### Architecture Components

- **Presentation Layer**: Jetpack Compose UI + ViewModels
- **Domain Layer**: Use Cases + Repository Interfaces
- **Data Layer**: Room Database + Repository Implementations
- **Dependency Injection**: Hilt for dependency management
- **Navigation**: Compose Navigation for screen routing

## 🚀 Getting Started

### Prerequisites

- **Android Studio Hedgehog** or newer
- **JDK 17** or newer
- **Android SDK** with API level 34
- **Git** for version control

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/TodoApp.git
   cd TodoApp
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Sync dependencies**
   ```bash
   ./gradlew build
   ```

4. **Run the application**
   - Connect an Android device or start an emulator
   - Click "Run" button or use `Ctrl+R`

### Building from Command Line

```bash
# Debug build
./gradlew assembleDebug

# Release build (requires signing configuration)
./gradlew assembleRelease

# Run tests
./gradlew test

# Run all checks (tests, lint, detekt)
./gradlew check
```

## 🧪 Testing

The project includes comprehensive testing at multiple levels:

### Unit Tests
```bash
./gradlew testDebugUnitTest
```
- **Repository tests**: Data layer logic
- **UseCase tests**: Business logic validation
- **ViewModel tests**: UI state management

### Integration Tests
```bash
./gradlew connectedDebugAndroidTest
```
- **Database tests**: Room database operations
- **UI tests**: Compose UI components

### Code Coverage
```bash
./gradlew jacocoTestReport
```
- **Target**: >80% code coverage
- **Reports**: `app/build/reports/jacoco/`

## 📊 Code Quality

### Static Analysis
- **Detekt**: Kotlin static analysis
- **Ktlint**: Kotlin code style
- **Android Lint**: Android-specific issues

```bash
# Run all code quality checks
./gradlew detekt ktlintCheck lint
```

### Security
- **Dependency scanning**: OWASP dependency check
- **ProGuard**: Code obfuscation for release builds

## 🔄 CI/CD Pipeline

The project includes a comprehensive GitHub Actions workflow:

### Pipeline Stages
1. **Code Quality**: Detekt, Ktlint, Android Lint
2. **Testing**: Unit tests, Integration tests, UI tests
3. **Security**: Dependency vulnerability scanning
4. **Build**: Debug and Release APK generation
5. **Performance**: Build time and APK size validation
6. **Deploy**: Automated release to GitHub Releases

### Build Matrix
- **API Levels**: 29, 30, 34
- **Build Types**: Debug, Release
- **Test Coverage**: Codecov integration

### Performance Metrics
- ✅ **Build time**: <5 minutes
- ✅ **APK size**: <10MB optimized
- ✅ **Test coverage**: >80%

## 🔧 Configuration

### Database Schema
The app uses Room database with the following schema:

```kotlin
@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val category: String = "General",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val dueDate: Date? = null
)
```

### Dependency Versions
- **Kotlin**: 1.9.20+
- **Compose BOM**: 2024.02.00+
- **Room**: 2.6.1+
- **Hilt**: 2.48+
- **Coroutines**: 1.7.3+

## 🤝 Contributing

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Commit your changes**
   ```bash
   git commit -m 'Add some amazing feature'
   ```
4. **Push to the branch**
   ```bash
   git push origin feature/amazing-feature
   ```
5. **Open a Pull Request**

### Contribution Guidelines
- Follow the existing code style
- Add tests for new features
- Update documentation as needed
- Ensure CI pipeline passes
- Keep commits atomic and well-described

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Material Design 3** for design guidelines
- **Android Jetpack** for modern Android development
- **Kotlin Coroutines** for reactive programming
- **GitHub Actions** for CI/CD automation

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/yourusername/TodoApp/issues)
- **Discussions**: [GitHub Discussions](https://github.com/yourusername/TodoApp/discussions)
- **Documentation**: [Wiki](https://github.com/yourusername/TodoApp/wiki)

---

**Made with ❤️ using modern Android development practices**

# Todo List Manager

A simple console-based Todo List Manager built with Kotlin. This application allows you to manage your tasks with basic CRUD operations and file persistence.

## Features

- ✅ Add new tasks
- ✅ Mark tasks as complete
- ✅ Delete tasks
- ✅ List all tasks
- ✅ Save tasks to file
- ✅ Load tasks from file
- ✅ Simple console interface

## Prerequisites

- Java 11 or higher
- Gradle (or use the Gradle wrapper)

## Building and Running

### Using Gradle Wrapper (Recommended)

1. **Build the project:**
   ```bash
   ./gradlew build
   ```

2. **Run the application:**
   ```bash
   ./gradlew run
   ```

### Using Gradle (if installed globally)

1. **Build the project:**
   ```bash
   gradle build
   ```

2. **Run the application:**
   ```bash
   gradle run
   ```

## Usage

Once the application is running, you'll see a command prompt. Here are the available commands:

### Commands

- `add <task description>` - Add a new task
  - Example: `add Buy groceries`
  - Example: `add Call mom`

- `list` - Show all tasks with their status

- `complete <task number>` - Mark a task as complete
  - Example: `complete 1` (marks the first task as complete)

- `delete <task number>` - Delete a task
  - Example: `delete 2` (deletes the second task)

- `save` - Save all tasks to a file (tasks.txt)

- `load` - Load tasks from the saved file

- `help` - Show available commands

- `quit` or `exit` - Exit the application

### Example Session

```
=== Todo List Manager ===
Welcome! Type 'help' to see available commands.

> add Buy groceries
Task added: Buy groceries

> add Call mom
Task added: Call mom

> add Finish project
Task added: Finish project

> list
Your tasks:
1. [ ] Buy groceries
2. [ ] Call mom
3. [ ] Finish project

> complete 1
Task 1 marked as complete!

> list
Your tasks:
1. [✓] Buy groceries
2. [ ] Call mom
3. [ ] Finish project

> save
Tasks saved successfully!

> quit
Goodbye!
```

## File Storage

Tasks are saved to a file called `tasks.txt` in the project root directory. The file format is simple:

```
Task description|isCompleted|createdAt
```

For example:
```
Buy groceries|true|2023-12-01T10:30:00
Call mom|false|2023-12-01T10:31:00
```

## Project Structure

```
src/
├── main/
│   └── kotlin/
│       └── com/
│           └── todo/
│               ├── TodoApp.kt      # Main application class
│               ├── TodoManager.kt  # Business logic for task management
│               └── Task.kt         # Data class for individual tasks
build.gradle.kts                   # Gradle build configuration
README.md                          # This file
```

## Learning Points

This project demonstrates several Kotlin concepts:

- **Data Classes** - The `Task` class
- **Collections** - Using `MutableList` for task storage
- **File I/O** - Reading and writing to files
- **String Manipulation** - Parsing and formatting
- **Control Flow** - When expressions and loops
- **Null Safety** - Safe calls and null checks
- **Functions** - Extension functions and higher-order functions

## Extending the Project

Here are some ideas to extend this project:

1. **Add due dates** to tasks
2. **Add categories/tags** to tasks
3. **Add priority levels** (High, Medium, Low)
4. **Add search functionality** to find specific tasks
5. **Add sorting options** (by date, priority, etc.)
6. **Create a GUI version** using Compose for Desktop
7. **Add multiple todo lists** (work, personal, etc.)
8. **Add task descriptions** (longer text for each task)

## Troubleshooting

### Common Issues

1. **"Permission denied" when running gradlew**
   - Make the gradlew file executable: `chmod +x gradlew`

2. **"Java not found"**
   - Ensure Java 11+ is installed and JAVA_HOME is set correctly

3. **"Tasks not saving/loading"**
   - Check that the application has write permissions in the current directory
   - Verify that `tasks.txt` is not being used by another process

## License

This project is open source and available under the MIT License. 



./gradlew distZip
unzip build/distributions/mystery-repo-1.0-SNAPSHOT.zip
./mystery-repo-1.0-SNAPSHOT/bin/mystery-repo

./gradlew test --tests TodoManagerTest
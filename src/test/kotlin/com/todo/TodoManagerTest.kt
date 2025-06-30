package com.todo

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.test.*

class TodoManagerTest {
    private lateinit var todoManager: TodoManager
    
    @BeforeEach
    fun setup() {
        val config = AppConfig(
            dataFilePath = "test-tasks.txt",
            maxTaskDescriptionLength = 500,
            autoSave = false,
            backupEnabled = false,
            maxBackupFiles = 5
        )
        todoManager = TodoManager(config)
    }
    
    @Test
    fun `should add task successfully`() {
        todoManager.addTask("Test task")
        assertEquals(1, todoManager.getTaskCount())
        assertEquals("Test task", todoManager.getAllTasks()[0].description)
    }
    
    @Test
    fun `should complete task successfully`() {
        todoManager.addTask("Test task")
        assertTrue(todoManager.completeTask(0))
        assertTrue(todoManager.getAllTasks()[0].isCompleted)
    }
    
    @Test
    fun `should handle invalid task index`() {
        assertFalse(todoManager.completeTask(0))
        assertFalse(todoManager.deleteTask(0))
    }
} 
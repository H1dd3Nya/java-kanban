package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private static TaskManager manager;
    private static final Task task = new Task("First task", "My first task!");
    private static final Epic epic = new Epic("First Epic", "My first Epic", 2);
    private static final Subtask subtask = new Subtask("First subtask", "My first subtask", Status.NEW,
            epic.getId());

    @BeforeAll
    public static void beforeAll() {
        manager = Managers.getDefault();
        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSubTask(subtask);
    }

    @Test
    @DisplayName("Задача добавляется в историю после вызова метода get")
    public void taskAddedToHistoryManagerAfterCallGet() {
        manager.getTask(1);
        assertFalse(manager.getHistory().isEmpty());
        assertTrue(manager.getHistory().contains(task));
    }

    @Test
    @DisplayName("Задача, добавленная в историю, сохраняет свою предыдущую версию")
    public void tasksAddedToHistorySavePreviousVersionOfTasks() {
        Task oldVersion = task;
        Task newVersion = new Task("test", "test", oldVersion.getId(), Status.IN_PROGRESS);
        manager.updateTask(newVersion);
        manager.getTask(oldVersion.getId());
        newVersion = manager.getTask(oldVersion.getId());
        assertEquals(oldVersion, manager.getHistory().get(0));
        assertEquals(newVersion, manager.getHistory().get(1));

    }
}
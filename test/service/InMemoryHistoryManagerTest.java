package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;
import static service.InMemoryTaskManagerTest.assertEqualsTask;

class InMemoryHistoryManagerTest {
    private static final HistoryManager historyManager = Managers.getDefaultHistory();

    private static final Task task = new Task("First task", "My first task!");
    private static final Epic epic = new Epic("First Epic", "My first Epic", 2);
    private static final Subtask subtask = new Subtask("First subtask", "My first subtask", Status.NEW,
            epic.getId());


    @Test
    @DisplayName("Задача добавляется в историю")
    void getTask_taskAddedToHistory() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        assertEquals(3, historyManager.getHistory().size());
        assertEquals(task, historyManager.getHistory().getFirst());
        assertEquals(epic, historyManager.getHistory().get(1));
        assertEquals(subtask, historyManager.getHistory().get(2));
    }

    @Test
    @DisplayName("Добавление задачи в историю после достижения лимита")
    void add_returnHistory_afterHistoryReachedLimit() {
        for (int i = 0; i < 11; i++) {
            historyManager.add(new Task("" + i, ""+ i, Status.DONE));
        }
        Task firstTaskInHistory = historyManager.getHistory().getFirst();
        Task addedTask = new Task("Test", "test", Status.DONE);
        historyManager.add(addedTask);

        assertEquals(10, historyManager.getHistory().size());
        assertNotEquals(firstTaskInHistory.getName(), historyManager.getHistory().getFirst().getName());
        assertNotEquals(firstTaskInHistory.getDescription(), historyManager.getHistory().getFirst().getDescription());
        assertEqualsTask(addedTask, historyManager.getHistory().getLast());
    }

}
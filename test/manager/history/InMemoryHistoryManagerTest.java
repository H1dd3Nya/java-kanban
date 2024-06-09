package manager.history;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Менеджер Истории")
class InMemoryHistoryManagerTest {
    private static final HistoryManager historyManager = new InMemoryHistoryManager();

    private static final Task task = new Task("First task", "My first task!", 1, Status.NEW);
    private static final Epic epic = new Epic("First Epic", "My first Epic", 2);
    private static final Subtask subtask = new Subtask("First subtask", "My first subtask", 3,
            Status.NEW, epic.getId());

    @BeforeEach
    void beforeEach() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
    }


    @Test
    @DisplayName("Задача добавляется в историю")
    void getTask_taskAddedToHistory() {
        assertEquals(3, historyManager.getHistory().size());
        assertEquals(task, historyManager.getHistory().getFirst());
        assertEquals(epic, historyManager.getHistory().get(1));
        assertEquals(subtask, historyManager.getHistory().get(2));
    }

    @Test
    @DisplayName("Задача удаляется из истории")
    void remove_taskRemovedFromHistory() {
        historyManager.remove(task.getId());

        assertEquals(2, historyManager.getHistory().size());
        assertEquals(epic, historyManager.getHistory().getFirst());
        assertEquals(subtask, historyManager.getHistory().get(1));
    }

    @Test
    @DisplayName("Возращает историю с корректным порядком")
    void getHistory_returnHistoryListWithCorrectTasksOrder() {
        historyManager.add(task);
        historyManager.add(epic);
        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size());
        assertEquals(subtask, history.getFirst());
        assertEquals(task, history.get(1));
        assertEquals(epic, history.get(2));

    }

}
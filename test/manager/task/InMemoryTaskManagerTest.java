package manager.task;

import exception.ValidationException;
import manager.TaskManagerTest;
import manager.history.HistoryManager;
import manager.history.InMemoryHistoryManager;
import model.Epic;
import model.Status;
import model.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Менеджер задач")
class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    private static TaskManager manager;

    @Override
    protected InMemoryTaskManager createManager() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        return new InMemoryTaskManager(historyManager);
    }

    @BeforeEach
    public void beforeEach() {
        manager = createManager();
    }

    @Test
    @DisplayName("Подзадачи с пересечением не добавляются в менеджер")
    void createSubtask_shouldNotAddTaskToManager() {

        assertThrows(ValidationException.class, () -> {
            Epic epic = manager.createEpic(new Epic("test", "test"));
            manager.createSubTask(new Subtask("Test", "test", Status.NEW, epic.getId(),
                    LocalDateTime.now(), Duration.ofMinutes(60)));
            manager.createSubTask(new Subtask("Test", "test", Status.NEW, epic.getId(),
                    LocalDateTime.now(), Duration.ofMinutes(60)));
        });

        assertEquals(1, manager.getAllSubtasks().size());
        assertEquals(1, manager.getPrioritizedTasks().size());
    }
}
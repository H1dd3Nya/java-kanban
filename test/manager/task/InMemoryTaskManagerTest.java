package manager.task;

import exception.ValidationException;
import manager.TaskManagerTest;
import manager.history.HistoryManager;
import manager.history.InMemoryHistoryManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

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
    @DisplayName("Возвращает эпик со статусом NEW")
    void getEpicStatus_returnEpicWithStatusNew() {
        Epic epicStatusTest = manager.createEpic(new Epic("EpicStatusTest", "Epic test"));
        manager.createSubTask(new Subtask("Subtask1", "subtask", Status.NEW,
                epicStatusTest.getId(), LocalDateTime.now(), Duration.ofMinutes(100)));
        manager.createSubTask(new Subtask("Subtask2", "subtask", Status.NEW,
                epicStatusTest.getId(), LocalDateTime.now().plusDays(5), Duration.ofMinutes(100)));
        manager.createSubTask(new Subtask("Subtask3", "subtask", Status.NEW,
                epicStatusTest.getId(), LocalDateTime.now().plusDays(20), Duration.ofMinutes(100)));

        assertEquals(Status.NEW, manager.getEpic(epicStatusTest.getId()).getStatus());
    }

    @Test
    @DisplayName("Возвращает эпик со статусом DONE")
    void getEpicStatus_returnEpicWithStatusDone() {
        Epic epicStatusTest = manager.createEpic(new Epic("EpicStatusTest", "Epic test"));
        manager.createSubTask(new Subtask("Subtask1", "subtask", Status.DONE,
                epicStatusTest.getId(), LocalDateTime.now().plusDays(2), Duration.ofMinutes(100)));
        manager.createSubTask(new Subtask("Subtask2", "subtask", Status.DONE,
                epicStatusTest.getId(), LocalDateTime.now().plusDays(5), Duration.ofMinutes(100)));
        manager.createSubTask(new Subtask("Subtask3", "subtask", Status.DONE,
                epicStatusTest.getId(), LocalDateTime.now().plusDays(20), Duration.ofMinutes(100)));

        assertEquals(Status.DONE, manager.getEpic(epicStatusTest.getId()).getStatus());
    }

    @Test
    @DisplayName("Возвращает эпик со статусом IN_PROGRESS")
    void getEpicStatus_returnEpicWithStatusInProgress() {
        Epic epicStatusTest = manager.createEpic(new Epic("EpicStatusTest", "Epic test"));
        manager.createSubTask(new Subtask("Subtask1", "subtask", Status.NEW,
                epicStatusTest.getId(), LocalDateTime.now(), Duration.ofMinutes(100)));
        manager.createSubTask(new Subtask("Subtask2", "subtask", Status.DONE,
                epicStatusTest.getId(), LocalDateTime.now().plusDays(5), Duration.ofMinutes(100)));

        assertEquals(Status.IN_PROGRESS, manager.getEpic(epicStatusTest.getId()).getStatus());
    }

    @Test
    @DisplayName("Возвращает эпик со статусом IN_PROGRESS, все подзадачи со статусом IN_PROGRESS")
    void getEpicStatus_returnEpicWithStatusInProgress_AllSubtasksWithInProgressStatus() {
        Epic epicStatusTest = manager.createEpic(new Epic("EpicStatusTest", "Epic test"));
        manager.createSubTask(new Subtask("Subtask1", "subtask", Status.IN_PROGRESS,
                epicStatusTest.getId(), LocalDateTime.now(), Duration.ofMinutes(100)));
        manager.createSubTask(new Subtask("Subtask2", "subtask", Status.IN_PROGRESS,
                epicStatusTest.getId(), LocalDateTime.now().plusDays(5), Duration.ofMinutes(100)));

        assertEquals(Status.IN_PROGRESS, manager.getEpic(epicStatusTest.getId()).getStatus());
    }

    @Test
    @DisplayName("Возвращает эпик подзадачи")
    void getEpic_returnEpicOfSubtask() {
        Epic epic = manager.createEpic(new Epic("EpicStatusTest", "Epic test"));
        Subtask subtask = manager.createSubTask(new Subtask("Subtask1", "subtask", Status.NEW,
                epic.getId(), LocalDateTime.now(), Duration.ofMinutes(100)));

        assertEquals(epic.getId(), subtask.getEpicId());
        assertEqualsTask(epic, manager.getEpic(subtask.getEpicId()));
    }

    @Test
    @DisplayName("Возвращает корректный расчёт пересечения интервалов")
    void addPrioritized_returnCorrectIntervals() {
        manager.createTask(new Task("", "", Status.NEW, Duration.ofMinutes(60), LocalDateTime.now()));
        assertDoesNotThrow(() -> {
            manager.createTask(new Task("", "", Status.NEW, Duration.ofMinutes(60),
                    LocalDateTime.of(2025, 11, 10, 12, 0)));
        });
    }

    @Test
    @DisplayName("Возвращает ValidationException")
    void addPrioritized_returnValidationException() {
        assertThrows(ValidationException.class, () -> {
            manager.createTask(new Task("", "", Status.NEW, Duration.ofMinutes(60),
                    LocalDateTime.of(2025, 11, 10, 12, 0)));
            manager.createTask(new Task("", "", Status.NEW, Duration.ofMinutes(60),
                    LocalDateTime.of(2025, 11, 10, 12, 0)));
        });
    }

}
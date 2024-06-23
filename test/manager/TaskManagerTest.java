package manager;

import exception.ValidationException;
import manager.task.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Общий абстрактный класс")
public abstract class TaskManagerTest<T extends TaskManager> {
    T manager;

    protected abstract T createManager();


    @BeforeEach
    void init() {
        manager = createManager();
    }

    @Test
    @DisplayName("Должен вернуть список всех задач")
    void getAllTasks_returnTasksList() {
        Task task = manager.createTask(new Task("Test", "test", Status.NEW));

        List<Task> allTasks = manager.getAllTasks();

        assertEquals(1, allTasks.size());
        assertEquals(task, allTasks.getFirst());
    }

    @Test
    @DisplayName("Должен вернуть список всех эпиков")
    void getAllEpics_returnEpicsList() {
        Epic epic = manager.createEpic(new Epic("Test", "test"));

        List<Epic> allEpics = manager.getAllEpics();

        assertEquals(1, allEpics.size());
        assertEquals(epic, allEpics.getFirst());
    }

    @Test
    @DisplayName("Должен вернуть список всех подзадач")
    void getAllSubTasks_returnSubTasksList() {
        Epic epic = manager.createEpic(new Epic("Test", "test"));
        Subtask subtask = manager.createSubTask(new Subtask("test", "test", Status.NEW, epic.getId()));

        List<Subtask> allSubtasks = manager.getAllSubtasks();

        assertEquals(1, allSubtasks.size());
        assertEquals(subtask, allSubtasks.getFirst());
    }

    @Test
    @DisplayName("Должен удалить все задачи")
    void removeAllTasks_returnEmptyTasksList() {
        manager.createTask(new Task("Test", "test", Status.NEW));
        manager.createTask(new Task("Test2", "test", Status.NEW));
        manager.createTask(new Task("Test3", "test", Status.NEW));

        manager.removeAllTasks();

        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    @DisplayName("Должен удалить все эпики")
    void removeAllEpics_returnEmptyEpicsList() {
        manager.createEpic(new Epic("Test", "test"));
        manager.createEpic(new Epic("Test2", "test"));
        manager.createEpic(new Epic("Test3", "test"));

        manager.removeAllEpics();

        assertEquals(0, manager.getAllEpics().size());
    }

    @Test
    @DisplayName("Должен удалить все подзадачи")
    void removeAllSubTasks_returnEmptySubTasksList() {
        Epic epic = manager.createEpic(new Epic("Test", "test"));
        manager.createSubTask(new Subtask("test", "test", Status.NEW, epic.getId()));
        manager.createSubTask(new Subtask("test2", "test", Status.NEW, epic.getId()));
        manager.createSubTask(new Subtask("test3", "test", Status.NEW, epic.getId()));

        manager.removeAllSubTasks();

        assertEquals(0, manager.getAllSubtasks().size());
    }

    @Test
    @DisplayName("Получить задачу по id")
    void getTask_returnTask() {
        Task task = manager.createTask(new Task("Test", "test", Status.NEW));

        Task taskActual = manager.getTask(task.getId());

        assertEqualsTask(task, taskActual);
    }

    @Test
    @DisplayName("Получить эпик по id")
    void getEpic_returnEpic() {
        Epic epic = manager.createEpic(new Epic("Test", "test"));

        Epic epicActual = manager.getEpic(epic.getId());

        assertEqualsTask(epic, epicActual);
    }

    @Test
    @DisplayName("Получить подзадачу по id")
    void getSubTask_returnSubTask() {
        Epic epic = manager.createEpic(new Epic("Test", "test"));
        Subtask subtask = manager.createSubTask(new Subtask("test", "test", Status.NEW, epic.getId()));

        Subtask subtaskActual = manager.getSubTask(subtask.getId());

        assertEqualsTask(subtask, subtaskActual);
    }

    @Test
    @DisplayName("Должен обновить задачу")
    void updateTask_returnUpdatedTask() {
        Task task = manager.createTask(new Task("Test", "test", Status.NEW));
        Task oldTask = manager.getTask(task.getId());
        Task updatedTask = new Task(task.getName(), "Test2", task.getId(), Status.IN_PROGRESS);

        manager.updateTask(updatedTask);

        updatedTask = manager.getTask(task.getId());

        assertNotEquals(oldTask.getDescription(), updatedTask.getDescription());
        assertNotEquals(oldTask.getStatus(), updatedTask.getStatus());
    }

    @Test
    @DisplayName("Должен обновить эпик")
    void updateTask_returnUpdatedEpic() {
        Epic epic = manager.createEpic(new Epic("Test", "test"));
        Epic oldEpic = new Epic(epic.getName(), epic.getDescription(), epic.getId());
        Epic updatedEpic = new Epic("Test123", "Test123", epic.getId());

        manager.updateEpic(updatedEpic);
        updatedEpic = manager.getEpic(epic.getId());

        assertNotEquals(oldEpic.getName(), updatedEpic.getName());
        assertNotEquals(oldEpic.getDescription(), updatedEpic.getDescription());
    }

    @Test
    @DisplayName("Должен удалить задачу")
    void shouldDeleteTask() {
        Task task = manager.createTask(new Task("Test", "test", Status.NEW));

        manager.deleteTask(task.getId());

        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    @DisplayName("Должен удалить эпик")
    void deleteEpic_returnEmptyEpicList() {
        Epic epic = manager.createEpic(new Epic("Test", "test"));
        manager.deleteEpic(epic.getId());
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    @DisplayName("Должен обновить подзадачу и обновить статус эпика")
    void updateSubTask_shouldUpdateSubtaskAndEpicStatus() {
        Epic epic = manager.createEpic(new Epic("Test", "test"));
        Subtask subtask = manager.createSubTask(new Subtask("test", "test", Status.NEW, epic.getId(),
                LocalDateTime.now(), Duration.ofMinutes(60)));
        Subtask oldSubtask = manager.getSubTask(subtask.getId());
        Epic oldEpic = new Epic(epic.getName(), epic.getDescription(), epic.getId());
        Subtask updatedSubtask = new Subtask(oldSubtask.getName(), "In progress now", oldSubtask.getId(),
                Status.IN_PROGRESS, oldSubtask.getEpicId(), oldSubtask.getStartTime().plusYears(1).plusDays(2),
                Duration.ofMinutes(120));

        manager.updateSubTask(updatedSubtask);

        assertNotEquals(oldSubtask.getDescription(), updatedSubtask.getDescription());
        assertNotEquals(oldSubtask.getStatus(), updatedSubtask.getStatus());
        assertNotEquals(oldEpic.getStatus(), epic.getStatus());
    }

    @Test
    @DisplayName("Должен удалить подзадачу и обновить статус эпика")
    void deleteSubTask_returnEpicWithoutSubtask() {
        Epic epic = manager.createEpic(new Epic("Test", "test"));
        Subtask subtask = manager.createSubTask(new Subtask("test", "test", Status.NEW, epic.getId()));

        manager.deleteSubTask(subtask.getId());

        assertEquals(0, manager.getEpicSubTasks(epic).size());
    }

    @Test
    @DisplayName("Поля задачи остаются неизменными при добавлении в менеджер")
    void createTask_taskAddedWithEqualFields() {
        Task taskExpected = new Task("Test1", "Test2", Status.DONE, LocalDateTime.now().plusDays(15),
                Duration.ofMinutes(60));
        Task taskActual = manager.createTask(taskExpected);
        assertEqualsTask(taskExpected, taskActual);
    }

    @Test
    @DisplayName("Задача с заданным id не конфликтует с задачей со сгенерированным id")
    void createTask_taskAdded_providedExistingId() {
        manager.createTask(new Task("123", "123", 1, Status.DONE, LocalDateTime.now().plusYears(5),
                Duration.ofMinutes(120)));
    }

    @Test
    @DisplayName("Возвращает историю задач")
    void getHistory_returnListOfTasks() {
        Task task = manager.createTask(new Task("Test", "test", Status.NEW));
        Epic epic = manager.createEpic(new Epic("Test", "test"));
        Subtask subtask = manager.createSubTask(new Subtask("test", "test", Status.NEW, epic.getId()));

        manager.getEpic(epic.getId());
        manager.getTask(task.getId());
        manager.getSubTask(subtask.getId());
        List<Task> history = manager.getHistory();

        assertEquals(3, history.size());
        assertEqualsTask(epic, history.getFirst());
        assertEqualsTask(task, history.get(1));
        assertEqualsTask(subtask, history.get(2));
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

        Epic epic = manager.getEpic(epicStatusTest.getId());

        assertEquals(Status.NEW, epic.getStatus());
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

        Epic epic = manager.getEpic(epicStatusTest.getId());

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    @DisplayName("Возвращает эпик со статусом IN_PROGRESS")
    void getEpicStatus_returnEpicWithStatusInProgress() {
        Epic epicStatusTest = manager.createEpic(new Epic("EpicStatusTest", "Epic test"));
        manager.createSubTask(new Subtask("Subtask1", "subtask", Status.NEW,
                epicStatusTest.getId(), LocalDateTime.now(), Duration.ofMinutes(100)));
        manager.createSubTask(new Subtask("Subtask2", "subtask", Status.DONE,
                epicStatusTest.getId(), LocalDateTime.now().plusDays(5), Duration.ofMinutes(100)));

        Epic epic = manager.getEpic(epicStatusTest.getId());

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    @DisplayName("Возвращает эпик со статусом IN_PROGRESS, все подзадачи со статусом IN_PROGRESS")
    void getEpicStatus_returnEpicWithStatusInProgress_AllSubtasksWithInProgressStatus() {
        Epic epicStatusTest = manager.createEpic(new Epic("EpicStatusTest", "Epic test"));
        manager.createSubTask(new Subtask("Subtask1", "subtask", Status.IN_PROGRESS,
                epicStatusTest.getId(), LocalDateTime.now(), Duration.ofMinutes(100)));
        manager.createSubTask(new Subtask("Subtask2", "subtask", Status.IN_PROGRESS,
                epicStatusTest.getId(), LocalDateTime.now().plusDays(5), Duration.ofMinutes(100)));

        Epic epic = manager.getEpic(epicStatusTest.getId());

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    @DisplayName("Возвращает эпик подзадачи")
    void getEpic_returnEpicOfSubtask() {
        Epic epic = manager.createEpic(new Epic("EpicStatusTest", "Epic test"));
        Subtask subtask = manager.createSubTask(new Subtask("Subtask1", "subtask", Status.NEW,
                epic.getId(), LocalDateTime.now(), Duration.ofMinutes(100)));

        Epic epicFromSubtask = manager.getEpic(subtask.getEpicId());

        assertEquals(epic.getId(), epicFromSubtask.getId());
        assertEqualsTask(epic, epicFromSubtask);
    }

    @Test
    @DisplayName("Возвращает корректный расчёт пересечения интервалов")
    void addPrioritized_returnCorrectIntervals() {
        manager.createTask(new Task("", "", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(60)));
        assertDoesNotThrow(() -> {
            manager.createTask(new Task("", "", Status.NEW,
                    LocalDateTime.of(2025, 11, 10, 12, 0), Duration.ofMinutes(60)));
        });
    }

    @Test
    @DisplayName("Возвращает ValidationException")
    void createTask_returnValidationException() {
        assertThrows(ValidationException.class, () -> {
            manager.createTask(new Task("", "", Status.NEW,
                    LocalDateTime.of(2025, 11, 10, 12, 0), Duration.ofMinutes(60)));
            manager.createTask(new Task("", "", Status.NEW,
                    LocalDateTime.of(2025, 11, 10, 12, 0), Duration.ofMinutes(60)));
        });
    }

    @Test
    @DisplayName("Задачи с пересечением не добавляются в менеджер")
    void createTask_shouldNotAddTaskToManager() {
        manager.createTask(new Task("", "", Status.NEW,
                LocalDateTime.of(2025, 11, 10, 12, 0), Duration.ofMinutes(60)));

        assertThrows(ValidationException.class, () -> {
            manager.createTask(new Task("", "", Status.NEW,
                    LocalDateTime.of(2025, 11, 10, 12, 0), Duration.ofMinutes(60)));
        });

        assertEquals(1, manager.getAllTasks().size());
        assertEquals(1, manager.getPrioritizedTasks().size());
    }

    @Test
    @DisplayName("При обновлении задачи с пересечением, она не обновится и не появится копия")
    void updateTask_returnValidationException() {
        manager.createTask(new Task("", "", Status.NEW,
                LocalDateTime.of(2025, 11, 10, 12, 0), Duration.ofMinutes(60)));
        Task task = manager.createTask(new Task("", "", Status.NEW));

        assertThrows(ValidationException.class, () -> {
            Task updatedTask = new Task("Test1", "Test2", task.getId(), Status.IN_PROGRESS,
                    LocalDateTime.of(2025, 11, 10, 12, 30), Duration.ofMinutes(60));
            manager.updateTask(updatedTask);
        });

        assertEqualsTask(task, manager.getTask(task.getId()));
        assertEquals(2, manager.getAllTasks().size());
        assertEquals(1, manager.getPrioritizedTasks().size());
    }

    @Test
    @DisplayName("При обновлении задачи с пересечением, она не обновится и не появится копия")
    void updateSubtask_returnValidationException() {
        //given
        Epic epic = manager.createEpic(new Epic("test", "test"));
        manager.createSubTask(new Subtask("Test", "test", Status.NEW, epic.getId(),
                LocalDateTime.of(2025, 11, 10, 12, 0), Duration.ofMinutes(60)));
        Subtask subtask = manager.createSubTask(new Subtask("", "", Status.NEW, epic.getId()));

        //that
        assertThrows(ValidationException.class, () -> {
            Subtask subtaskUpdated = new Subtask("Test1", "test1", subtask.getId(), Status.IN_PROGRESS,
                    epic.getId(), LocalDateTime.of(2025, 11, 10, 12, 0),
                    Duration.ofMinutes(60));
            manager.updateSubTask(subtaskUpdated);
        });

        //then
        assertEqualsTask(subtask, manager.getSubTask(subtask.getId()));
        assertEquals(2, manager.getAllSubtasks().size());
        assertEquals(1, manager.getPrioritizedTasks().size());
    }

    public static void assertEqualsTask(Task expected, Task actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

}

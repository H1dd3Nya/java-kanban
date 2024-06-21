package manager;

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
    Task task;
    Epic epic;
    Subtask subtask;

    protected abstract T createManager();


    @BeforeEach
    void init() {
        manager = createManager();


        task = manager.createTask(new Task("Task", "test", 0, Status.NEW));
        epic = manager.createEpic(new Epic("Test", "test"));
        subtask = manager.createSubTask(new Subtask("Test", "Test", Status.NEW, epic.getId(),
                LocalDateTime.now().plusYears(2), Duration.ofMinutes(120)));
    }

    @Test
    @DisplayName("Должен вернуть список всех задач")
    void getAllTasks_returnTasksList() {
        List<Task> allTasks = manager.getAllTasks();

        assertEquals(1, allTasks.size());
        assertEquals(task, allTasks.getFirst());
    }

    @Test
    @DisplayName("Должен вернуть список всех эпиков")
    void getAllEpics_returnEpicsList() {
        List<Epic> allEpics = manager.getAllEpics();

        assertEquals(1, allEpics.size());
        assertEquals(epic, allEpics.getFirst());
    }

    @Test
    @DisplayName("Должен вернуть список всех подзадач")
    void getAllSubTasks_returnSubTasksList() {
        List<Subtask> allSubtasks = manager.getAllSubtasks();

        assertEquals(1, allSubtasks.size());
        assertEquals(subtask, allSubtasks.getFirst());
    }

    @Test
    @DisplayName("Должен удалить все задачи")
    void removeAllTasks_returnEmptyTasksList() {
        manager.removeAllTasks();

        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    @DisplayName("Должен удалить все эпики")
    void removeAllEpics_returnEmptyEpicsList() {
        manager.removeAllEpics();

        assertEquals(0, manager.getAllEpics().size());
    }

    @Test
    @DisplayName("Должен удалить все подзадачи")
    void removeAllSubTasks_returnEmptySubTasksList() {
        manager.removeAllSubTasks();

        assertEquals(0, manager.getAllSubtasks().size());
    }

    @Test
    @DisplayName("Получить задачу по id")
    void getTask_returnTask() {
        Task taskActual = manager.getTask(task.getId());

        assertEqualsTask(task, taskActual);
    }

    @Test
    @DisplayName("Получить эпик по id")
    void getEpic_returnEpic() {
        Epic epicActual = manager.getEpic(epic.getId());

        assertEqualsTask(epic, epicActual);
    }

    @Test
    @DisplayName("Получить подзадачу по id")
    void getSubTask_returnSubTask() {
        Subtask subtaskActual = manager.getSubTask(subtask.getId());

        assertEqualsTask(subtask, subtaskActual);
    }

    @Test
    @DisplayName("Должен обновить задачу")
    void updateTask_returnUpdatedTask() {
        Task oldTask = manager.getTask(1);
        Task updatedTask = new Task(task.getName(), "Test2", task.getId(), Status.IN_PROGRESS);

        manager.updateTask(updatedTask);

        updatedTask = manager.getTask(task.getId());

        assertNotEquals(oldTask.getDescription(), updatedTask.getDescription());
        assertNotEquals(oldTask.getStatus(), updatedTask.getStatus());
    }

    @Test
    @DisplayName("Должен обновить эпик")
    void updateTask_returnUpdatedEpic() {
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
        manager.deleteTask(1);
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    @DisplayName("Должен удалить эпик")
    void deleteEpic_returnEmptyEpicList() {
        manager.deleteEpic(2);
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    @DisplayName("Должен обновить подзадачу и обновить статус эпика")
    void updateSubTask_shouldUpdateSubtaskAndEpicStatus() {
        Subtask oldSubtask = manager.getSubTask(3);
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
        manager.deleteSubTask(subtask.getId());

        assertEquals(0, manager.getEpicSubTasks(epic).size());
    }

    @Test
    @DisplayName("Поля задачи остаются неизменными при добавлении в менеджер")
    void createTask_taskAddedWithEqualFields() {
        Task taskExpected = new Task("Test1", "Test2", Status.DONE, Duration.ofMinutes(60),
                LocalDateTime.now().plusDays(15));
        Task taskActual = manager.createTask(taskExpected);
        assertEqualsTask(taskExpected, taskActual);
    }

    @Test
    @DisplayName("Задача с заданным id не конфликтует с задачей со сгенерированным id")
    void createTask_taskAdded_providedExistingId() {
        manager.createTask(new Task("123", "123", 1, Status.DONE,
                Duration.ofMinutes(120), LocalDateTime.now().plusYears(5)));
    }

    @Test
    @DisplayName("Возвращает историю задач")
    void getHistory_returnListOfTasks() {
        manager.getEpic(epic.getId());
        manager.getTask(task.getId());
        manager.getSubTask(subtask.getId());

        List<Task> history = manager.getHistory();

        assertEquals(3, history.size());
        assertEqualsTask(epic, history.getFirst());
        assertEqualsTask(task, history.get(1));
        assertEqualsTask(subtask, history.get(2));
    }

    public static void assertEqualsTask(Task expected, Task actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

}

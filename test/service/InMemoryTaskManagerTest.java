package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Менеджер задач")
class InMemoryTaskManagerTest {

    private static TaskManager manager;
    private Task task;
    private Epic epic;
    private Subtask subtask;


    @BeforeEach
    public void beforeEach() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        manager = new InMemoryTaskManager(historyManager);

        task = manager.createTask(new Task("Test", "Test", Status.NEW));
        epic = manager.createEpic(new Epic("Test", "test"));
        subtask = manager.createSubTask(new Subtask("Test", "Test", Status.NEW, epic.getId()));
    }

    @Test
    @DisplayName("Должен вернуть список всех задач по типам")
    void shouldReturnListOfAllTasksByType() {
        List<Task> allTasks = manager.getAllTasks();
        List<Epic> allEpics = manager.getAllEpics();
        List<Subtask> allSubtasks = manager.getAllSubtasks();

        assertEquals(1, allTasks.size());
        assertEquals(task, allTasks.get(0));

        assertEquals(1, allEpics.size());
        assertEquals(epic, allEpics.get(0));

        assertEquals(1, allTasks.size());
        assertEquals(subtask, allSubtasks.get(0));
    }

    @Test
    @DisplayName("Должен удалить все задачи по типу")
    void shouldRemoveAllTasksByType() {

        manager.removeAllTasks();
        manager.removeAllSubTasks();
        manager.removeAllEpics();

        List<Task> allTasks = manager.getAllTasks();
        List<Epic> allEpics = manager.getAllEpics();
        List<Subtask> allSubtasks = manager.getAllSubtasks();

        assertTrue(allTasks.isEmpty());
        assertTrue(allEpics.isEmpty());
        assertTrue(allSubtasks.isEmpty());
    }

    @Test
    @DisplayName("Получить задачу по id")
    void shouldGetTaskById() {
        Task taskActual = manager.getTask(task.getId());
        Epic epicActual = manager.getEpic(epic.getId());
        Subtask subtaskActual = manager.getSubTask(subtask.getId());

        assertEqualsTask(task, taskActual);
        assertEqualsTask(epic, epicActual);
        assertEqualsTask(subtask, subtaskActual);
    }

    @Test
    @DisplayName("Должен обновить задачу")
    void shouldUpdateTask() {
        Task oldTask = manager.getTask(1);
        Epic oldEpic = new Epic(epic.getName(), epic.getDescription(), epic.getId());
        Task updatedTask = new Task(task.getName(), "Test2", task.getId(), Status.IN_PROGRESS);
        Epic updatedEpic = new Epic("Test123", "Test123", epic.getId());

        manager.updateTask(updatedTask);
        manager.updateEpic(updatedEpic);

        updatedTask = manager.getTask(task.getId());
        updatedEpic = manager.getEpic(epic.getId());

        assertNotEquals(oldTask.getDescription(), updatedTask.getDescription());
        assertNotEquals(oldTask.getStatus(), updatedTask.getStatus());
        assertNotEquals(oldEpic.getName(), updatedEpic.getName());
        assertNotEquals(oldEpic.getDescription(), updatedEpic.getDescription());
    }

    @Test
    @DisplayName("Должен удалить задачу")
    void shouldDeleteTask() {
        manager.deleteTask(1);
        manager.deleteEpic(2);
        assertTrue(manager.getAllTasks().isEmpty());
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    @DisplayName("Должен обновить подзадачу и обновить статус эпика")
    void shouldUpdateSubtaskAndUpdateEpicStatus() {
        Subtask oldSubtask = manager.getSubTask(3);
        Epic oldEpic = new Epic(epic.getName(), epic.getDescription(), epic.getId());
        Subtask updatedSubtask = new Subtask(oldSubtask.getName(), "In progress now", oldSubtask.getId(),
                Status.IN_PROGRESS, oldSubtask.getEpicId());

        manager.updateSubTask(updatedSubtask);

        assertNotEquals(oldSubtask.getDescription(), updatedSubtask.getDescription());
        assertNotEquals(oldSubtask.getStatus(), updatedSubtask.getStatus());
        assertNotEquals(oldEpic.getStatus(), epic.getStatus());
    }

    @Test
    @DisplayName("Должен удалить подзадачу и обновить статус эпика")
    void shouldDeleteSubtaskAndUpdateEpicStatus() {
        Epic oldEpic = new Epic(epic.getName(), epic.getDescription());
        Subtask subtaskToDelete = new Subtask("Test1", "test1", Status.IN_PROGRESS, 2);

        manager.createSubTask(subtaskToDelete);
        Epic epicUpdated = manager.getEpic(2);
        assertNotEquals(oldEpic.getStatus(), epicUpdated.getStatus());
        assertEquals(2, manager.getEpicSubTasks(epic).size());

        manager.deleteSubTask(subtaskToDelete.getId());
        assertEquals(1, manager.getEpicSubTasks(epic).size());
        assertEquals(oldEpic.getStatus(), epic.getStatus());
    }

    @Test
    @DisplayName("Поля задачи остаются неизменными при добавлении в менеджер")
    void taskFieldsShouldBeSavedWhenAddedToManager() {
        Task taskExpected = new Task("Test1", "Test2", Status.DONE);
        Task taskActual = manager.createTask(taskExpected);
        assertEqualsTask(taskExpected, taskActual);
    }

    @Test
    @DisplayName("Задача с заданным id не конфликтует с задачей со сгенерированным id")
    void taskWithProvidedIdShouldNotConflictWithTaskWithGeneratedId() {
        manager.createTask(new Task("123", "123", 1, Status.DONE));
    }


    private static void assertEqualsTask(Task expected, Task actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
    }
}
package manager.file;

import manager.history.HistoryManager;
import manager.history.InMemoryHistoryManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Файловый менеджер задач")
class FileBackedTaskManagerTest {

    private static FileBackedTaskManager manager;
    private Task task;
    private Epic epic;
    private Subtask subtask;
    private Path path;


    @BeforeEach
    public void beforeEach() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        try {
            path = File.createTempFile("tasks-test", "csv").toPath();
            manager = new FileBackedTaskManager(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Должен сохранить задачу в файл")
    void save_taskSaved() {
        task = manager.createTask(new Task("Test", "Test", Status.NEW));
        epic = manager.createEpic(new Epic("Test", "test"));
        subtask = manager.createSubTask(new Subtask("Test", "Test", Status.NEW, epic.getId()));

        String lineWithTask;
        String lineWithEpic;
        String lineWithSubtask;

        try (final BufferedReader reader = new BufferedReader(new FileReader(path.toFile(), StandardCharsets.UTF_8))) {
            reader.readLine();
            lineWithTask = reader.readLine();
            lineWithEpic = reader.readLine();
            lineWithSubtask = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(lineWithTask, TaskConverter.toString(task));
        assertEquals(lineWithEpic, TaskConverter.toString(epic));
        assertEquals(lineWithSubtask, TaskConverter.toString(subtask));


    }

    @Test
    @DisplayName("Должен сохранить задачу в файл")
    void loadDataFromFile_returnTask() {
        task = manager.createTask(new Task("Test", "Test", Status.NEW));
        epic = manager.createEpic(new Epic("Test", "test"));
        subtask = manager.createSubTask(new Subtask("Test", "Test", Status.NEW, epic.getId()));

        Task taskFromFile;
        Task epicFromFile;
        Task subtaskFromFile;

        try (final BufferedReader reader = new BufferedReader(new FileReader(path.toFile(), StandardCharsets.UTF_8))) {
            reader.readLine();
            taskFromFile = TaskConverter.fromString(reader.readLine());
            epicFromFile = TaskConverter.fromString(reader.readLine());
            subtaskFromFile = TaskConverter.fromString(reader.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEqualsTask(task, taskFromFile);
        assertEqualsTask(epic, epicFromFile);
        assertEqualsTask(subtask, subtaskFromFile);


    }

    public static void assertEqualsTask(Task expected, Task actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
    }
}
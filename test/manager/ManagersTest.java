package manager;

import manager.file.FileBackedTaskManager;
import manager.history.InMemoryHistoryManager;
import manager.task.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ManagersTest {

    @Test
    public void shouldGetDefaultTaskManager() {
        assertInstanceOf(InMemoryTaskManager.class, Managers.getDefault());
    }

    @Test
    public void shouldGetDefaultHistoryManager() {
        assertInstanceOf(InMemoryHistoryManager.class, Managers.getDefaultHistory());
    }

    @Test
    public void shouldGetDefaultFileBackedManager() {
        assertInstanceOf(FileBackedTaskManager.class, Managers.getFileBackedTaskManager(Paths.get("resources/tasks.csv")));
    }
}
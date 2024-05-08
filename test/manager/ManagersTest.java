package manager;

import org.junit.jupiter.api.Test;
import manager.history.InMemoryHistoryManager;
import manager.task.InMemoryTaskManager;


import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    public void shouldGetDefaultTaskManager() {
        assertInstanceOf(InMemoryTaskManager.class, Managers.getDefault());
    }

    @Test
    public void shouldGetDefaultHistoryManager() {
        assertInstanceOf(InMemoryHistoryManager.class, Managers.getDefaultHistory());
    }
}
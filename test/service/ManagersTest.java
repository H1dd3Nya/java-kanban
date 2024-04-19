package service;

import org.junit.jupiter.api.Test;


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
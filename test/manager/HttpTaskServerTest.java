package manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.history.InMemoryHistoryManager;
import manager.task.InMemoryTaskManager;
import manager.task.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static api.Utils.getGson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskServerTest {
    TaskManager manager;
    HttpTaskServer server;
    Gson gson;

    @BeforeEach
    public void beforeEach() throws IOException {
        manager = new InMemoryTaskManager(new InMemoryHistoryManager());
        server = new HttpTaskServer(manager);
        gson = getGson();
        server.start();
    }

    @AfterEach
    void serverStop() {
        server.stop();
    }

    @Test
    @DisplayName("Должен создать задачу")
    void POST_shouldCreateTask() throws IOException, InterruptedException {
        //given
        Task task = new Task("Test", "Testing task", Status.NEW);

        //that
        HttpResponse<String> response = setup(HttpClient.newHttpClient(), task, "http://localhost:8080/tasks",
                "POST");
        List<Task> tasksFromManager = manager.getAllTasks();

        //then
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertCorrectResponse(201, response.statusCode(), 1, tasksFromManager.size(),
                "Задача успешно создана", response.body());
        assertEqualsTask(task, tasksFromManager.getFirst());
    }

    @Test
    @DisplayName("Должен обновить задачу")
    void POST_shouldUpdateTask() throws IOException, InterruptedException {
        //given
        Task task = new Task("Test", "Testing task", Status.NEW);
        Task taskUpdated = new Task("Test updated", "updated", 1, Status.IN_PROGRESS);
        setup(HttpClient.newHttpClient(), task, "http://localhost:8080/tasks", "POST");

        //that
        HttpResponse<String> response = setup(HttpClient.newHttpClient(), taskUpdated,
                "http://localhost:8080/tasks/1", "POST");
        List<Task> tasksFromManager = manager.getAllTasks();

        //then
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertCorrectResponse(201, response.statusCode(), 1, tasksFromManager.size(),
                "Задача успешно обновлена", response.body());
        assertEqualsTask(taskUpdated, tasksFromManager.getFirst());
    }

    @Test
    @DisplayName("Должен вернуть задачу")
    void GET_shouldReturnTaskById() throws IOException, InterruptedException {
        //given
        Task task = new Task("Test", "Testing task", Status.NEW);
        setup(HttpClient.newHttpClient(), task, "http://localhost:8080/tasks", "POST");

        //that
        HttpResponse<String> response = setup(HttpClient.newHttpClient(), task,
                "http://localhost:8080/tasks/1", "GET");
        List<Task> tasksFromManager = manager.getAllTasks();
        String addedTask = gson.toJson(manager.getTask(1));
        Task taskFromServer = gson.fromJson(response.body(), Task.class);

        //then
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertCorrectResponse(200, response.statusCode(), 1, tasksFromManager.size(),
                addedTask, response.body());
        assertEqualsTask(taskFromServer, tasksFromManager.getFirst());
    }

    @Test
    @DisplayName("Должен вернуть все задачи")
    void GET_shouldReturnAllTasks() throws IOException, InterruptedException {
        //given
        Task task = new Task("Test", "Testing tasks", Status.NEW);
        for (int i = 0; i < 3; i++) {
            task.setName(task.getName() + i);
            setup(HttpClient.newHttpClient(), task, "http://localhost:8080/tasks", "POST");
        }

        //that
        HttpResponse<String> response = setup(HttpClient.newHttpClient(), null,
                "http://localhost:8080/tasks", "GET");
        List<Task> tasksFromManager = manager.getAllTasks();
        String addedTasks = gson.toJson(manager.getAllTasks());
        List<Task> tasksFromServer = gson.fromJson(response.body(), new TaskTypeToken().getType());

        //then
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertCorrectResponse(200, response.statusCode(), tasksFromServer.size(), tasksFromManager.size(),
                addedTasks, response.body());
        assertEqualsTask(tasksFromServer.getFirst(), tasksFromManager.getFirst());
    }

    @Test
    @DisplayName("Должен удалить задачу")
    void DELETE_shouldDeleteTask() throws IOException, InterruptedException {
        //given
        Task task = new Task("Test", "Testing task",
                Status.NEW);
        setup(HttpClient.newHttpClient(), task, "http://localhost:8080/tasks", "POST");

        //that
        HttpResponse<String> response = setup(HttpClient.newHttpClient(), null,
                "http://localhost:8080/tasks/1", "DELETE");
        List<Task> tasksFromManager = manager.getAllTasks();

        //then
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertCorrectResponse(204, response.statusCode(), 0, tasksFromManager.size(),
                "", response.body());
    }

    @Test
    @DisplayName("Должен создать эпик")
    void POST_shouldCreateEpic() throws IOException, InterruptedException {
        //given
        Epic epic = new Epic("Test", "Testing epic");

        //that
        HttpResponse<String> response = setup(HttpClient.newHttpClient(), epic, "http://localhost:8080/epics",
                "POST");
        List<Epic> tasksFromManager = manager.getAllEpics();

        //then
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertCorrectResponse(201, response.statusCode(), 1, tasksFromManager.size(),
                "Эпик успешно создан", response.body());
        assertEqualsTask(epic, tasksFromManager.getFirst());
    }

    @Test
    @DisplayName("Должен обновить эпик")
    void POST_shouldUpdateEpic() throws IOException, InterruptedException {
        //given
        Epic epic = new Epic("Test", "Testing epic");
        Epic epicUpdated = new Epic("Test updated", "updated", 1);
        setup(HttpClient.newHttpClient(), epic, "http://localhost:8080/epics", "POST");

        //that
        HttpResponse<String> response = setup(HttpClient.newHttpClient(), epicUpdated,
                "http://localhost:8080/epics/1", "POST");
        List<Epic> tasksFromManager = manager.getAllEpics();

        //then
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertCorrectResponse(201, response.statusCode(), 1, tasksFromManager.size(),
                "Эпик успешно обновлен", response.body());
        assertEqualsTask(epicUpdated, tasksFromManager.getFirst());

    }

    @Test
    @DisplayName("Должен вернуть эпик")
    void GET_shouldReturnEpic() throws IOException, InterruptedException {
        //given
        Epic epic = new Epic("Test", "Testing epic");
        setup(HttpClient.newHttpClient(), epic, "http://localhost:8080/epics", "POST");

        //that
        HttpResponse<String> response = setup(HttpClient.newHttpClient(), epic,
                "http://localhost:8080/epics/1", "GET");
        List<Epic> tasksFromManager = manager.getAllEpics();
        String addedTask = gson.toJson(manager.getEpic(1));
        Task taskFromServer = gson.fromJson(response.body(), Epic.class);

        //then
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertCorrectResponse(200, response.statusCode(), 1, tasksFromManager.size(),
                addedTask, response.body());
        assertEqualsTask(taskFromServer, tasksFromManager.getFirst());
    }

    @Test
    @DisplayName("Должен вернуть все эпики")
    void GET_shouldReturnAllEpics() throws IOException, InterruptedException {
        //given
        Epic epic = new Epic("Test", "Testing epics");
        for (int i = 0; i < 3; i++) {
            epic.setName(epic.getName() + i);
            setup(HttpClient.newHttpClient(), epic, "http://localhost:8080/epics", "POST");
        }

        //that
        HttpResponse<String> response = setup(HttpClient.newHttpClient(), null,
                "http://localhost:8080/epics", "GET");
        List<Epic> epicsFromManager = manager.getAllEpics();
        String addedTasks = gson.toJson(epicsFromManager);
        List<Epic> epicsFromServer = gson.fromJson(response.body(), new TaskTypeToken().getType());

        //then
        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertCorrectResponse(200, response.statusCode(), epicsFromServer.size(), epicsFromManager.size(),
                addedTasks, response.body());
        assertEqualsTask(epicsFromServer.getFirst(), epicsFromManager.getFirst());
    }

    @Test
    @DisplayName("Должен вернуть все подзадачи эпика")
    void GET_shouldReturnAllEpicSubtasks() throws IOException, InterruptedException {
        //given
        Epic epic = new Epic("Test", "Testing epic");
        setup(HttpClient.newHttpClient(), epic, "http://localhost:8080/epics", "POST");
        Subtask subtask = new Subtask("Test", "Testing subtasks", Status.NEW, 1);
        for (int i = 0; i < 3; i++) {
            subtask.setName(subtask.getName() + i);
            setup(HttpClient.newHttpClient(), subtask, "http://localhost:8080/subtasks", "POST");
        }

        //that
        HttpResponse<String> response = setup(HttpClient.newHttpClient(), null,
                "http://localhost:8080/epics/1/subtasks", "GET");
        List<Subtask> epicSubtasksFromManager = manager.getEpicSubTasks(manager.getEpic(1));
        String addedSubtasks = gson.toJson(epicSubtasksFromManager);
        List<Subtask> epicSubtasksFromServer = gson.fromJson(response.body(), new TaskTypeToken().getType());

        //then
        assertNotNull(epicSubtasksFromManager, "Задачи не возвращаются");
        assertCorrectResponse(200, response.statusCode(), epicSubtasksFromServer.size(),
                epicSubtasksFromManager.size(), addedSubtasks, response.body());
        assertEqualsTask(epicSubtasksFromServer.getFirst(), epicSubtasksFromManager.getFirst());
    }

    @Test
    @DisplayName("Должен удалить эпик")
    void DELETE_shouldDeleteEpic() throws IOException, InterruptedException {
        //given
        Epic epic = new Epic("Test", "Testing epic");
        setup(HttpClient.newHttpClient(), epic, "http://localhost:8080/epics", "POST");

        //that
        HttpResponse<String> response = setup(HttpClient.newHttpClient(), null,
                "http://localhost:8080/epics/1", "DELETE");
        List<Epic> epicsFromManager = manager.getAllEpics();

        //then
        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertCorrectResponse(204, response.statusCode(), 0, epicsFromManager.size(),
                "", response.body());
    }

    @Test
    @DisplayName("Должен создать подзадачу")
    void POST_shouldCreateSubtask() throws IOException, InterruptedException {
        //given
        Epic epic = new Epic("Test", "Testing epic");
        setup(HttpClient.newHttpClient(), epic, "http://localhost:8080/epics", "POST");
        Subtask subtask = new Subtask("Test", "Testing subtask", Status.NEW, 1);

        //that
        HttpResponse<String> response = setup(HttpClient.newHttpClient(), subtask, "http://localhost:8080/subtasks",
                "POST");
        List<Subtask> tasksFromManager = manager.getAllSubtasks();

        //then
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertCorrectResponse(201, response.statusCode(), 1, tasksFromManager.size(),
                "Подзадача успешно создана", response.body());
        assertEqualsTask(subtask, tasksFromManager.getFirst());
    }

    @Test
    @DisplayName("Должен обновить подзадачу")
    void POST_shouldUpdateSubtask() throws IOException, InterruptedException {
        //given
        Epic epic = new Epic("Test", "Testing epic");
        setup(HttpClient.newHttpClient(), epic, "http://localhost:8080/epics", "POST");
        Subtask subtask = new Subtask("Test", "Testing subtask", Status.NEW, 1);
        Subtask subtaskUpdated = new Subtask("Test updated", "updated", 2, Status.IN_PROGRESS, 1);
        setup(HttpClient.newHttpClient(), subtask, "http://localhost:8080/subtasks", "POST");

        //that
        HttpResponse<String> response = setup(HttpClient.newHttpClient(), subtaskUpdated,
                "http://localhost:8080/subtasks/2", "POST");
        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        //then
        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertCorrectResponse(201, response.statusCode(), 1, subtasksFromManager.size(),
                "Задача успешно обновлена", response.body());
        assertEqualsTask(subtaskUpdated, subtasksFromManager.getFirst());
    }

    @Test
    @DisplayName("Должен вернуть подзадачу")
    void GET_shouldReturnSubtask() throws IOException, InterruptedException {
        //given
        Epic epic = new Epic("Test", "Testing epic");
        setup(HttpClient.newHttpClient(), epic, "http://localhost:8080/epics", "POST");
        Subtask subtask = new Subtask("Test", "Testing subtask", Status.NEW, 1);
        setup(HttpClient.newHttpClient(), subtask, "http://localhost:8080/subtasks", "POST");

        //that
        HttpResponse<String> response = setup(HttpClient.newHttpClient(), subtask,
                "http://localhost:8080/subtasks/2", "GET");
        List<Subtask> subtasksFromManager = manager.getAllSubtasks();
        String addedSubtask = gson.toJson(manager.getSubTask(2));
        Subtask subtaskFromServer = gson.fromJson(response.body(), Subtask.class);

        //then
        assertNotNull(subtaskFromServer, "Задачи не возвращаются");
        assertCorrectResponse(200, response.statusCode(), 1, subtasksFromManager.size(),
                addedSubtask, response.body());
        assertEqualsTask(subtaskFromServer, subtasksFromManager.getFirst());
    }

    @Test
    @DisplayName("Должен вернуть все подзадачи")
    void GET_shouldReturnAllSubtasks() throws IOException, InterruptedException {
        //given
        Epic epic = new Epic("Test", "Testing epic");
        setup(HttpClient.newHttpClient(), epic, "http://localhost:8080/epics", "POST");
        Subtask subtask = new Subtask("Test", "Testing subtasks", Status.NEW, 1);
        for (int i = 0; i < 3; i++) {
            subtask.setName(subtask.getName() + i);
            setup(HttpClient.newHttpClient(), subtask, "http://localhost:8080/subtasks", "POST");
        }

        //that
        HttpResponse<String> response = setup(HttpClient.newHttpClient(), null,
                "http://localhost:8080/subtasks", "GET");
        List<Subtask> subtasksFromManager = manager.getAllSubtasks();
        String addedTasks = gson.toJson(manager.getAllSubtasks());
        List<Subtask> subtasksFromServer = gson.fromJson(response.body(), new TaskTypeToken().getType());

        //then
        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertCorrectResponse(200, response.statusCode(), subtasksFromServer.size(),
                subtasksFromManager.size(), addedTasks, response.body());
        assertEqualsTask(subtasksFromServer.getFirst(), subtasksFromManager.getFirst());
    }

    @Test
    @DisplayName("Должен удалить подзадачу")
    void DELETE_shouldDeleteSubtask() throws IOException, InterruptedException {
        //given
        Epic epic = new Epic("Test", "Testing epic");
        setup(HttpClient.newHttpClient(), epic, "http://localhost:8080/epics", "POST");
        Subtask subtask = new Subtask("Test", "Testing subtask",
                Status.NEW, 1);
        setup(HttpClient.newHttpClient(), subtask, "http://localhost:8080/subtasks", "POST");

        //that
        HttpResponse<String> response = setup(HttpClient.newHttpClient(), null,
                "http://localhost:8080/subtasks/2", "DELETE");
        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        //then
        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertCorrectResponse(204, response.statusCode(), 0, subtasksFromManager.size(),
                "", response.body());
    }

    @Test
    @DisplayName("Должен вернуть историю задач")
    void GET_shouldReturnTaskHistory() throws IOException, InterruptedException {
        //given
        Task task = new Task("Test", "Testing tasks",
                Status.NEW);
        for (int i = 0; i <= 3; i++) {
            task.setName(task.getName() + i);
            setup(HttpClient.newHttpClient(), task, "http://localhost:8080/tasks", "POST");
            setup(HttpClient.newHttpClient(), null, "http://localhost:8080/tasks/" + (i + 1), "GET");
        }

        //that
        HttpResponse<String> response = setup(HttpClient.newHttpClient(), null,
                "http://localhost:8080/history", "GET");
        List<Task> historyFromManager = manager.getHistory();
        String history = gson.toJson(manager.getHistory());
        List<Task> historyFromServer = gson.fromJson(response.body(), new TaskTypeToken().getType());

        //then
        assertNotNull(historyFromManager, "История пуста");
        assertCorrectResponse(200, response.statusCode(), historyFromManager.size(),
                historyFromServer.size(), history, response.body());
        assertEqualsTask(historyFromServer.getFirst(), historyFromServer.getFirst());
    }

    @Test
    @DisplayName("Должен вернуть список приоритетных задач")
    void GET_shouldReturnPrioritizedTasks() throws IOException, InterruptedException {
        //given
        Task task = new Task("Test", "Testing tasks",
                Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        for (int i = 0; i < 3; i++) {
            task.setName(task.getName() + i);
            task.setStartTime(task.getStartTime().plusDays(5));
            task.setDuration(task.getDuration().plusMinutes(10));
            setup(HttpClient.newHttpClient(), task, "http://localhost:8080/tasks", "POST");
        }

        //that
        HttpResponse<String> response = setup(HttpClient.newHttpClient(), null,
                "http://localhost:8080/prioritized", "GET");
        List<Task> prioritizedFromManager = manager.getPrioritizedTasks();
        String prioritizedTasks = gson.toJson(manager.getPrioritizedTasks());
        List<Task> prioritizedFromServer = gson.fromJson(response.body(), new TaskTypeToken().getType());

        //then
        assertNotNull(prioritizedFromManager, "Задачи не возвращаются");
        assertCorrectResponse(200, response.statusCode(), prioritizedFromManager.size(),
                prioritizedFromServer.size(), prioritizedTasks, response.body());
        assertEqualsTask(prioritizedFromServer.getFirst(), prioritizedFromServer.getFirst());
    }

    @Test
    @DisplayName("Подазадача с неверным id эпика не создается")
    void POST_taskWithoutEpicIdDoNoCreate() throws IOException, InterruptedException {
        //given
        Subtask subtask = new Subtask("Test", "Testing subtask", Status.NEW, 1);

        //that
        HttpResponse<String> response = setup(HttpClient.newHttpClient(), subtask, "http://localhost:8080/subtasks",
                "POST");
        List<Subtask> tasksFromManager = manager.getAllSubtasks();

        //then
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertCorrectResponse(404, response.statusCode(), 0, tasksFromManager.size(),
                "Эпик для подзадачи не найден\nid указан неверно или null", response.body());
    }

    @Test
    @DisplayName("Задача с разным id в URL и теле запроса не обновится")
    void POST_shouldNotUpdateTaskWithDifferentIdInURLAndBody() throws IOException, InterruptedException {
        //given
        Task task = new Task("Test", "Testing task", Status.NEW);
        Task taskUpdated = new Task("Test updated", "updated", 2, Status.IN_PROGRESS);
        setup(HttpClient.newHttpClient(), task, "http://localhost:8080/tasks", "POST");

        //that
        HttpResponse<String> response = setup(HttpClient.newHttpClient(), taskUpdated,
                "http://localhost:8080/tasks/1", "POST");
        List<Task> tasksFromManager = manager.getAllTasks();

        //then
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertCorrectResponse(400, response.statusCode(), 1, tasksFromManager.size(),
                "id указан неверно или null", response.body());
        assertEqualsTask(task, tasksFromManager.getFirst());
    }

    public static void assertEqualsTask(Task expected, Task actual) {
        if (expected.getId() != null && actual.getId() != null) {
            assertEquals(expected.getId(), actual.getId(), "Некорректный id задачи");
        }

        assertEquals(expected.getName(), actual.getName(), "Некорректное имя задачи");
        assertEquals(expected.getDescription(), actual.getDescription(), "Некорректное описание задачи");
        assertEquals(expected.getStatus(), actual.getStatus(), "Некорректный статус задачи");
    }

    private HttpResponse<String> setup(HttpClient client, Task task, String uri, String method)
            throws IOException, InterruptedException {

        try (client) {

            URI url = URI.create(uri);
            HttpRequest request;

            if (method.equals("GET")) {
                request = HttpRequest.newBuilder()
                        .uri(url)
                        .GET()
                        .build();
            } else if (method.equals("POST")) {
                String taskJson = gson.toJson(task);
                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                        .build();
            } else if (method.equals("DELETE")) {
                request = HttpRequest.newBuilder()
                        .uri(url)
                        .DELETE()
                        .build();
            } else {
                return null;
            }

            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }
    }

    public static void assertCorrectResponse(int statusExpected, int statusActual, int expectedListSize,
                                             int actualListSize, String responseBodyExpected,
                                             String responseBodyActual) {
        assertEquals(statusExpected, statusActual);
        assertEquals(expectedListSize, actualListSize, "Некорректное количество задач");
        assertEquals(responseBodyExpected, responseBodyActual);
    }

}

class TaskTypeToken extends TypeToken<List<Task>> {

}
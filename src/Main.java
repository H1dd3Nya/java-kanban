import manager.Managers;
import manager.task.TaskManager;
import model.Status;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        manager.createTask(new Task("", "", Status.NEW, Duration.ofMinutes(70), LocalDateTime.now()));
        manager.createTask(new Task("", "", Status.NEW, Duration.ofMinutes(70), LocalDateTime.now()));
    }
}
import manager.Managers;
import manager.task.TaskManager;
import model.Status;
import model.Task;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        Path path = Paths.get("resources/task.csv");

        TaskManager taskManagerFile = Managers.getDefaultFile(path);
        taskManagerFile.createTask(new Task("Test1", "Test", Status.NEW));

        TaskManager taskManagerMem = Managers.getDefault();
        taskManagerMem.createTask(new Task("Test1", "Test", Status.NEW));

        Task taskFile = taskManagerFile.getTask(1);
        Task taskMem = taskManagerMem.getTask(1);

        System.out.println(taskFile);
        System.out.println(taskMem);
        System.out.println(taskFile.equals(taskMem));
    }
}
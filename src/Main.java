import manager.Managers;
import manager.task.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        Path path = Paths.get("resources/tasks.csv");

        TaskManager taskManagerFile = Managers.getFileBackedTaskManager(path);
        taskManagerFile.createTask(new Task("Task", "Test task", Status.NEW));
        taskManagerFile.createEpic(new Epic("Epic", "Test epic"));
        taskManagerFile.createSubTask(new Subtask("Subtask", "Test subtask", Status.NEW, 2));
        System.out.println(taskManagerFile.getEpicSubTasks(taskManagerFile.getEpic(2)));


        TaskManager taskManagerMem = Managers.getDefault();
        taskManagerMem.createTask(new Task("Test1", "Test", Status.NEW));

        Task taskFile = taskManagerFile.getTask(1);
        Task taskMem = taskManagerMem.getTask(1);

        System.out.println(taskFile);
        System.out.println(taskMem);
        System.out.println(taskFile.equals(taskMem));
    }
}
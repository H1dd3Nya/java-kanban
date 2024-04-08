import model.*;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task = taskManager.createTask(new Task("First task", "First description", Status.NEW));
        System.out.println("Task created: " + task);
        Epic epic = taskManager.createEpic(new Epic("First epic", "My first epic"));
        System.out.println("Epic created: " + epic);
        Subtask subtask = taskManager.createSubTask(new Subtask("First subtask", "My first subtask",
                Status.NEW, epic.getId()));
        System.out.println("Subtask created: " + subtask);

        System.out.println();
        System.out.println("All tasks: " + taskManager.getAllTasks());
        System.out.println("All epics: " + taskManager.getAllEpics());
        System.out.println("All subtasks: " + taskManager.getAllSubtasks());

        System.out.println();
        System.out.println("Task by id [1]: " + taskManager.getTask(1));
        System.out.println("Epic by id [2]: " + taskManager.getEpic(2));
        System.out.println("Subtask by id [3]: " + taskManager.getSubTask(3));

        System.out.println();

        Task taskUpdated = new Task(task.getName(), "Finished", task.getId(), Status.DONE);
        taskManager.updateTask(taskUpdated);
        System.out.println(task + " updated with new data: " + taskUpdated);

        Epic epicUpdated = new Epic(epic.getName(), "Getting deeper", epic.getId());
        taskManager.updateEpic(epicUpdated);
        System.out.println(epic + " updated with new data: " + epicUpdated);

        Subtask subTaskUpdated = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getId(),
                Status.IN_PROGRESS, subtask.getEpicId());
        taskManager.updateSubTask(subTaskUpdated);
        System.out.println(subtask + " updated with new data: " + subTaskUpdated);

        System.out.println();
        System.out.println("All tasks: " + taskManager.getAllTasks());
        System.out.println("All epics: " + taskManager.getAllEpics());
        System.out.println("All subtasks: " + taskManager.getAllSubtasks());

        System.out.println();
        System.out.println("Task " + taskManager.getTask(task.getId()) + " deleted!");
        taskManager.deleteTask(task.getId());
        System.out.println("All tasks: " + taskManager.getAllTasks());

        Subtask subtask1 = new Subtask("Second subtask", "My second subtask", Status.DONE,
                epic.getId());
        Subtask subtask2 = new Subtask("Third subtask", "My third subtask", Status.IN_PROGRESS,
                epic.getId());
        subtask1 = taskManager.createSubTask(subtask1);
        subtask2 = taskManager.createSubTask(subtask2);

        System.out.println();
        System.out.println("Epic by id [2]: " + taskManager.getEpic(2));
        System.out.println("Epic's [id: 2] subtasks: " + taskManager.getEpicSubTasks(epic));

        subTaskUpdated = new Subtask(subTaskUpdated.getName(), subTaskUpdated.getDescription(), subTaskUpdated.getId(),
                Status.DONE, epic.getId());
        taskManager.updateSubTask(subTaskUpdated);
        subtask2 = new Subtask(subtask2.getName(), subtask2.getDescription(), subtask2.getId(), Status.DONE,
                epic.getId());
        taskManager.updateSubTask(subtask2);

        System.out.println();
        System.out.println("Epic by id [2]: " + taskManager.getEpic(2));
        System.out.println("Epic's [id: 2] subtasks: " + taskManager.getEpicSubTasks(epic));

        taskManager.removeAllSubTasks();
        System.out.println();
        System.out.println("All subtasks: " + taskManager.getAllSubtasks());
        System.out.println("Epic's [id: 2] subtasks: " + taskManager.getEpicSubTasks(epic));
    }
}

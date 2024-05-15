import manager.Managers;
import manager.task.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

//        Task task1 = new Task("Task 1", "Task 1");
//        Task task2 = new Task("Task 2", "Task 2");
//        taskManager.createTask(task1);
//        taskManager.createTask(task2);
//
        Epic epic1 = new Epic("Epic 1", "Epic 1");
//        Epic epic2 = new Epic("Epic 2", "Epic 2");
        taskManager.createEpic(epic1);
//        taskManager.createEpic(epic2);
//
        Subtask subtask1 = new Subtask("Subtask 1", "Subtask 1 ", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask 2", Status.IN_PROGRESS, epic1.getId());
        Subtask subtask3 = new Subtask("Subtask 3", "Subtask 3", Status.DONE, epic1.getId());
        taskManager.createSubTask(subtask1);
        taskManager.createSubTask(subtask2);
        taskManager.createSubTask(subtask3);

        taskManager.removeAllSubTasks();
        System.out.println(taskManager.getEpic(epic1.getId()).getSubTasks());
//
//        taskManager.getTask(task2.getId()); // {name='Task2', description='Task2',status=NEW}
//        System.out.println(taskManager.getHistory()
//                + "\n-------------------------------------------------------------\n");
//
//        taskManager.getEpic(epic2.getId()); // {name='Epic 2', description='Epic 2', status=NEW}
//        System.out.println(taskManager.getHistory()
//                + "\n-------------------------------------------------------------\n");
//
//        taskManager.getSubTask(subtask2.getId()); // {name='Subtask 2',description='Subtask 2', status=IN_PROGRESS}
//        System.out.println(taskManager.getHistory()
//                + "\n-------------------------------------------------------------\n");
//
//        taskManager.getSubTask(subtask1.getId()); // {name='Subtask 1', description='Subtask 1', status=NEW}
//        System.out.println(taskManager.getHistory()
//                + "\n-------------------------------------------------------------\n");
//
//        taskManager.getEpic(epic1.getId()); // {name='Epic 1', description='Epic 1', status=IN_PROGRESS}
//        System.out.println(taskManager.getHistory()
//                + "\n-------------------------------------------------------------\n");
//
//        taskManager.getTask(task1.getId()); // {name='Task 1', description='Task 1', status=NEW}
//        System.out.println(taskManager.getHistory()
//                + "\n-------------------------------------------------------------\n");
//
//        taskManager.getSubTask(subtask3.getId()); // {name='Subtask 3', description='Subtask 3', status=DONE}
//        System.out.println(taskManager.getHistory()
//                + "\n-------------------------------------------------------------\n");
//
//        taskManager.getTask(task1.getId()); // {name='Task 1', description='Task 1', status=NEW}
//        System.out.println(taskManager.getHistory()
//                + "\n-------------------------------------------------------------\n");
//
//
//        taskManager.deleteTask(task2.getId());
//        System.out.println(taskManager.getHistory()
//                + "\n-------------------------------------------------------------\n");
//
//        taskManager.deleteEpic(epic1.getId());
//        System.out.println(taskManager.getHistory()
//                + "\n-------------------------------------------------------------\n");
    }
}
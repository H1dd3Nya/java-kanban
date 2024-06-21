package manager.task;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getAllTasks();

    List<Subtask> getAllSubtasks();

    List<Epic> getAllEpics();

    void removeAllTasks();

    void removeAllSubTasks();

    void removeAllEpics();

    Task getTask(int id);

    Subtask getSubTask(int id);

    Epic getEpic(int id);

    Task createTask(Task task);

    Subtask createSubTask(Subtask subtask);

    Epic createEpic(Epic epic);

    void updateTask(Task task);

    void updateSubTask(Subtask subtask);

    void updateEpic(Epic epic);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubTask(int id);

    List<Subtask> getEpicSubTasks(Epic epic);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}

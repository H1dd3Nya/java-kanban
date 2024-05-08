package manager.task;

import model.Status;
import model.Task;
import model.Epic;
import model.Subtask;
import manager.history.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int seq = 1;

    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subTasks;
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = historyManager;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllSubTasks() {
        for (Epic epic : epics.values()) {
            epic.getSubTasks().clear();
            updateEpicStatus(epic);
        }
        subTasks.clear();
    }

    @Override
    public void removeAllEpics() {
        removeAllSubTasks();
        epics.clear();
    }

    @Override
    public Task getTask(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Subtask getSubTask(int id) {
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Subtask createSubTask(Subtask subtask) {
        subtask.setId(generateId());
        subTasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        addSubTask(subtask);
        updateEpicStatus(epic);
        return subtask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubTask(Subtask subtask) {
        subTasks.put(subtask.getId(), subtask);
        updateEpicStatus(epics.get(subtask.getEpicId()));
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic saved = epics.get(epic.getId());
        if (saved == null) {
            return;
        }
        saved.setName(epic.getName());
        saved.setDescription(epic.getDescription());
        epics.put(saved.getId(), saved);
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.get(id);

        for (Integer subTask : epic.getSubTasks()) {
            subTasks.remove(subTask);
            historyManager.remove(subTask);
        }

        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubTask(int id) {
        removeSubTaskFromEpic(subTasks.get(id));
        subTasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public List<Subtask> getEpicSubTasks(Epic epic) {
        List<Subtask> subtasks = new ArrayList<>();

        for (Integer subTask : epic.getSubTasks()) {
            subtasks.add(this.subTasks.get(subTask));
        }

        return subtasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private int generateId() {
        return seq++;
    }

    private void addSubTask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubTasks().add(subtask.getId());
    }

    private void updateEpicStatus(Epic epic) {
        List<Integer> subTasksIds = epic.getSubTasks();

        if (subTasksIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        int doneStatus = 0;
        int newStatus = 0;

        for (Integer subTaskId : subTasksIds) {
            if (this.subTasks.get(subTaskId).getStatus().equals(Status.NEW)) {
                newStatus++;
            } else if (this.subTasks.get(subTaskId).getStatus().equals(Status.DONE)) {
                doneStatus++;
            }
        }

        if (newStatus == subTasksIds.size()) {
            epic.setStatus(Status.NEW);
        } else if (doneStatus == subTasksIds.size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private void removeSubTaskFromEpic(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        int subtaskIndex = epic.getSubTasks().indexOf(subtask.getId());
        epic.getSubTasks().remove(subtaskIndex);
        updateEpicStatus(epic);
    }

}

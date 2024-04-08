package service;

import model.Status;
import model.Task;
import model.Epic;
import model.Subtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private int seq = 1;

    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subTasks;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    private int generateId() {
        return seq++;
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subTasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void removeAllTasks() {
        tasks.clear();
    }
    public void removeAllSubTasks() {
        for (Epic epic : epics.values()) {
            epic.getSubTasks().clear();
            updateEpicStatus(epic);
        }
        subTasks.clear();
    }
    public void removeAllEpics() {
        removeAllSubTasks();
        epics.clear();
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }
    public Subtask getSubTask(int id) {
        return subTasks.get(id);
    }
    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }
    public Subtask createSubTask(Subtask subtask) {
        subtask.setId(generateId());
        subTasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        addSubTask(subtask);
        updateEpicStatus(epic);
        return subtask;
    }
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }
    public void updateSubTask(Subtask subtask) {
        subTasks.put(subtask.getId(), subtask);
        updateEpicStatus(epics.get(subtask.getEpicId()));
    }
    public void updateEpic(Epic epic) {
        Epic saved = epics.get(epic.getId());
        if (saved == null) {
            return;
        }
        saved.setName(epic.getName());
        saved.setDescription(epic.getDescription());
        epics.put(saved.getId(), saved);
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }
    public void deleteEpic(int id) {
        Epic epic = epics.get(id);

        for (Integer subTask : epic.getSubTasks()) {
            subTasks.remove(subTask);
        }

        epics.remove(id);
    }
    public void deleteSubTask(int id) {
        removeSubTaskFromEpic(subTasks.get(id));
        subTasks.remove(id);
    }

    public List<Subtask> getEpicSubTasks(Epic epic) {
        List<Subtask> subtasks = new ArrayList<>();

        for (Integer subTask : epic.getSubTasks()) {
            subtasks.add(this.subTasks.get(subTask));
        }

        return subtasks;
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
        epic.getSubTasks().remove(subtask.getId());
        updateEpicStatus(epic);
    }

}

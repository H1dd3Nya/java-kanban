package service;

import model.Task;
import model.Epic;
import model.Subtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private int seq = 1;

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subTasks;

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
            epic.removeAllSubTasks();
        }
        subTasks.clear();
    }
    public void removeAllEpics() {
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
        Epic epic = epics.get(subtask.getEpicId());
        epic.addTask(subtask);
        epic.updateStatus(subtask);
        subTasks.put(subtask.getId(), subtask);
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
        Epic epic = epics.get(subtask.getEpicId());
        epic.updateStatus(subtask);
        subTasks.put(subtask.getId(), subtask);
    }
    public void updateEpic(Epic epic) {
        Epic saved = epics.get(epic.getId());
        if (saved == null) {
            return;
        }
        epic.setName(saved.getName());
        epic.setDescription(saved.getDescription());
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }
    public void deleteEpic(int id) {
        epics.remove(id);
    }
    public void deleteSubTask(int id) {
        subTasks.remove(id);
    }

    public List<Subtask> getEpicSubTasks(Epic epic) {
        return epic.getSubTasks();
    }
}

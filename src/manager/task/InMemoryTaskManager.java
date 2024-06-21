package manager.task;

import exception.NotFoundException;
import exception.ValidationException;
import manager.history.HistoryManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int seq = 1;

    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, Subtask> subTasks;
    protected final HistoryManager historyManager;
    protected final Set<Task> sortedTasks;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.sortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
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
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
            sortedTasks.remove(tasks.get(id));
        }
        tasks.clear();
    }

    @Override
    public void removeAllSubTasks() {
        for (Integer id : subTasks.keySet()) {
            removeSubTaskFromEpic(subTasks.get(id));
            historyManager.remove(id);
            sortedTasks.remove(subTasks.get(id));
        }
        subTasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
            sortedTasks.remove(epics.get(id));
        }
        subTasks.clear();
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
        addPrioritized(task);
        return task;
    }

    @Override
    public Subtask createSubTask(Subtask subtask) {
        subtask.setId(generateId());
        subTasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());

        addSubTask(subtask);

        try {
            addPrioritized(subtask);
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }

        updateEpicFields(epic);

        return subtask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        updateEpicFields(epic);
        return epic;
    }

    @Override
    public void updateTask(Task task) {
        Task original = tasks.get(task.getId());
        if (original == null) {
            throw new NotFoundException("Задача с id: ", task.getId());
        }

        try {
            addPrioritized(task);
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }

        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubTask(Subtask subtask) {
        Subtask original = subTasks.get(subtask.getId());

        if (original == null) {
            throw new NotFoundException("Подзадача с id: ", subtask.getId());
        }

        if (original.getEpicId() == null) {
            throw new NotFoundException("Эпик с id: ", subtask.getEpicId());
        }

        try {
            addPrioritized(subtask);
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }

        subTasks.put(subtask.getId(), subtask);
        updateEpicFields(epics.get(subtask.getEpicId()));
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic saved = epics.get(epic.getId());
        if (saved == null) {
            throw new NotFoundException("Эпик с id: ", epic.getId());
        }

        saved.setName(epic.getName());
        saved.setDescription(epic.getDescription());
        epics.put(saved.getId(), saved);
    }

    @Override
    public void deleteTask(int id) {
        sortedTasks.remove(tasks.get(id));
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
        sortedTasks.remove(subTasks.get(id));
        subTasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public List<Subtask> getEpicSubTasks(Epic epic) {
        return subTasks.values().stream()
                .filter(subtask -> subtask.getEpicId() == epic.getId())
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortedTasks);
    }

    private int generateId() {
        return seq++;
    }

    protected void addSubTask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubTasks().add(subtask.getId());
    }

    private void updateEpicFields(Epic epic) {
        List<Integer> subTasksIds = epic.getSubTasks();

        if (subTasksIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            epic.setDuration(Duration.ofMinutes(15));
            epic.setStartTime(LocalDateTime.now());
            epic.setEndTime(epic.getStartTime(), epic.getDuration());
            return;
        }

        int doneStatus = 0;
        int newStatus = 0;

        LocalDateTime epicStartTime = epic.getStartTime();
        Duration epicDuration = epic.getDuration();
        LocalDateTime epicEndTime = epic.getEndTime();

        for (Integer subTaskId : subTasksIds) {
            if (this.subTasks.get(subTaskId).getStatus().equals(Status.NEW)) {
                newStatus++;
            } else if (this.subTasks.get(subTaskId).getStatus().equals(Status.DONE)) {
                doneStatus++;
            }

            if (subTasks.get(subTaskId).getStartTime().isBefore(epicStartTime)) {
                epicStartTime = subTasks.get(subTaskId).getStartTime();
            }

            if (subTasks.get(subTaskId).getEndTime().isAfter(epicEndTime)) {
                epicEndTime = subTasks.get(subTaskId).getEndTime();
            }

            epicDuration = epicDuration.plus(subTasks.get(subTaskId).getDuration());
        }

        if (newStatus == subTasksIds.size()) {
            epic.setStatus(Status.NEW);
        } else if (doneStatus == subTasksIds.size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }

        epic.setDuration(epicDuration);
        epic.setStartTime(epicStartTime);
        epic.setEndTime(epic.getStartTime(), epic.getDuration());
    }

    private void removeSubTaskFromEpic(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());

        if (epic == null) {
            throw new NotFoundException("Эпик с id: ", subtask.getEpicId());
        }

        epic.getSubTasks().remove((Integer) subtask.getId());
        updateEpicFields(epic);
    }

    protected void addPrioritized(Task task) {
        Task original = tasks.get(task.getId());

        checkTaskTime(task);
        sortedTasks.remove(original);
        sortedTasks.add(task);

    }

    protected void addPrioritized(Subtask task) {
        Subtask original = subTasks.get(task.getId());

        checkTaskTime(task);
        sortedTasks.remove(original);
        sortedTasks.add(task);
    }

    private void checkTaskTime(Task task) {
        if (sortedTasks.isEmpty()) {
            sortedTasks.add(task);
        }

        for (Task sortedTask : sortedTasks) {

            if (sortedTask.getId() == task.getId()) {
                continue;
            }

            if (isTaskTimeConflict(task, sortedTask)) {
                throw new ValidationException("Найдено пересечение по времени: ", task.getId(), sortedTask.getId());
            }
        }
    }

    private boolean isTaskTimeConflict(Task task, Task sortedTask) {
        return (sortedTask.getStartTime().isBefore(task.getEndTime())
                && sortedTask.getStartTime().isAfter(task.getStartTime()))
                || task.getStartTime().isEqual(sortedTask.getStartTime());
    }
}
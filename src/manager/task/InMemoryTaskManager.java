package manager.task;

import exception.NotFoundException;
import exception.ValidationException;
import manager.history.HistoryManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

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
            try {
                sortedTasks.remove(tasks.get(id));
            } catch (NullPointerException e) {
                break;
            }
        }
        tasks.clear();
    }

    @Override
    public void removeAllSubTasks() {
        for (Integer id : subTasks.keySet()) {
            removeSubTaskFromEpic(subTasks.get(id));
            historyManager.remove(id);
            try {
                sortedTasks.remove(subTasks.get(id));
            } catch (NullPointerException ignored) {
                break;
            }
        }
        subTasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
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
        if (task.getStartTime() != null) {
            checkTaskTime(task);
            sortedTasks.add(task);
        }
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Subtask createSubTask(Subtask subtask) {
        subtask.setId(generateId());
        Epic epic = epics.get(subtask.getEpicId());

        if (subtask.getStartTime() != null) {
            checkTaskTime(subtask);
            sortedTasks.add(subtask);
        }

        addSubTask(subtask);
        subTasks.put(subtask.getId(), subtask);
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
        if (task.getStartTime() != null) {
            checkTaskTime(task);
            try {
                sortedTasks.remove(original);
                sortedTasks.add(task);
            } catch (NullPointerException e) {
                sortedTasks.add(task);
            }
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

        if (subtask.getStartTime() != null) {
            checkTaskTime(subtask);
            try {
                sortedTasks.remove(original);
                sortedTasks.add(subtask);
            } catch (NullPointerException e) {
                sortedTasks.add(subtask);
            }
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
        tasks.remove(id);
        historyManager.remove(id);
        sortedTasks.remove(tasks.get(id));
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
            return;
        }

        int doneStatus = 0;
        int newStatus = 0;

        for (Integer subTaskId : subTasksIds) {
            Subtask subtask = subTasks.get(subTaskId);

            if (subtask.getStatus().equals(Status.NEW)) {
                newStatus++;
            } else if (subtask.getStatus().equals(Status.DONE)) {
                doneStatus++;
            }

            if (subtask.getStartTime() != null) {
                updateEpicStartTime(epic, subtask);
                updateEpicDuration(epic, subtask);
                updateEpicEndTime(epic, subtask);
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

    private void updateEpicStartTime(Epic epic, Subtask subtask) {
        if (epic.getStartTime() == null || subtask.getStartTime().isBefore(epic.getStartTime())) {
            epic.setStartTime(subtask.getStartTime());
        }

    }

    private void updateEpicDuration(Epic epic, Subtask subtask) {
        if (epic.getDuration() == null) {
            epic.setDuration(subtask.getDuration());
            return;
        }

        epic.setDuration(epic.getDuration().plus(subtask.getDuration()));
    }

    private void updateEpicEndTime(Epic epic, Subtask subtask) {
        if (epic.getEndTime() == null || subtask.getEndTime().isAfter(epic.getEndTime())) {
            epic.setEndTime(subtask.getEndTime());
        }
    }

    private void removeSubTaskFromEpic(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());

        if (epic == null) {
            throw new NotFoundException("Эпик с id: ", subtask.getEpicId());
        }

        epic.getSubTasks().remove((Integer) subtask.getId());
        updateEpicFields(epic);
    }

    private void checkTaskTime(Task task) {
        if (sortedTasks.isEmpty()) {
            return;
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
        if (task.getStartTime() == null || task.getEndTime() == null || sortedTask.getStartTime() == null) {
            return false;
        }
        return (sortedTask.getStartTime().isBefore(task.getEndTime())
                && sortedTask.getEndTime().isAfter(task.getStartTime()))
                || task.getStartTime().isEqual(sortedTask.getStartTime());
    }
}
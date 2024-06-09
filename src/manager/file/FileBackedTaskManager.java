package manager.file;

import exception.ManagerSaveException;
import manager.Managers;
import manager.history.HistoryManager;
import manager.task.InMemoryTaskManager;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path path;

    public FileBackedTaskManager(HistoryManager historyManager, Path path) {
        super(historyManager);
        this.path = path;
    }

    public FileBackedTaskManager(Path path) {
        super(Managers.getDefaultHistory());
        this.path = path;
    }

    public static FileBackedTaskManager loadFromFile(Path path) {
        FileBackedTaskManager manager = new FileBackedTaskManager(path);
        manager.loadDataFromFile();
        return manager;
    }

    private void save() {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(String.valueOf(path)))) {
            writer.append("id,type,name,status,description,epic");
            writer.newLine();
            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                writer.append(toString(entry.getValue()));
                writer.newLine();
            }

            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                writer.append(toString(entry.getValue()));
                writer.newLine();
            }

            for (Map.Entry<Integer, Subtask> entry : subTasks.entrySet()) {
                writer.append(toString(entry.getValue()));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException(path);
        }
    }


    private void loadDataFromFile() {
        int maxId = 0;
        try (final BufferedReader reader = new BufferedReader(new FileReader(path.toFile(), StandardCharsets.UTF_8))) {
            reader.readLine();
            while (true) {
                String line = reader.readLine();
                try {
                    Task task = fromString(line);

                    final int id = task.getId();
                    if (task.getType() == Type.TASK) {
                        task = new Task(task.getName(), task.getDescription(), task.getId(), task.getStatus());
                        tasks.put(id, task);
                    } else if (task.getType() == Type.EPIC) {
                        epics.put(id, (Epic) task);
                    } else {
                        subTasks.put(id, (Subtask) task);
                    }

                    if (maxId < id) {
                        maxId = id;
                    }
                    if (line.isEmpty()) {
                        break;
                    }
                } catch (NullPointerException e) {
                    return;
                }
            }
        } catch (IOException e) {
            return;
        } catch (ManagerSaveException e) {
            throw new ManagerSaveException(path);
        }

        seq = maxId;
    }

    public String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + ","
                + task.getDescription() + "," + task.getEpicId();
    }

    public Task fromString(String value) {
        final String[] columns = value.split(",");

        String name = columns[2];
        String description = columns[4];
        Status status = Status.valueOf(columns[3]);
        Integer epicId;

        try {
            epicId = Integer.parseInt(columns[5]);
        } catch (Exception e) {
            epicId = null;
        }

        Type type = Type.valueOf(columns[1]);
        Task task = null;
        switch (type) {
            case TASK:
                task = new Task(name, description, status);
                break;
            case SUBTASK:
                task = new Subtask(name, description, status, epicId);
            case EPIC:
                task = new Epic(name, description);
        }
        return task;
    }

    @Override
    public List<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return super.getAllSubtasks();
    }

    @Override
    public List<Epic> getAllEpics() {
        return super.getAllEpics();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public Task getTask(int id) {
        return super.getTask(id);
    }

    @Override
    public Subtask getSubTask(int id) {
        return super.getSubTask(id);
    }

    @Override
    public Epic getEpic(int id) {
        return super.getEpic(id);
    }

    @Override
    public Task createTask(Task task) {
        task = super.createTask(task);
        save();
        return task;
    }

    @Override
    public Subtask createSubTask(Subtask subtask) {
        subtask = super.createSubTask(subtask);
        save();
        return subtask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic = super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(Subtask subtask) {
        super.updateSubTask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public List<Subtask> getEpicSubTasks(Epic epic) {
        return super.getEpicSubTasks(epic);
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

}

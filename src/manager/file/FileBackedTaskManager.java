package manager.file;

import exception.ManagerSaveException;
import manager.Managers;
import manager.history.HistoryManager;
import manager.task.InMemoryTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.Type;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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
                writer.append(TaskConverter.toString(entry.getValue()));
                writer.newLine();
            }

            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                writer.append(TaskConverter.toString(entry.getValue()));
                writer.newLine();
            }

            for (Map.Entry<Integer, Subtask> entry : subTasks.entrySet()) {
                writer.append(TaskConverter.toString(entry.getValue()));
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
                    Task task = TaskConverter.fromString(line);
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
            throw new ManagerSaveException(path);
        }

        for (Subtask subtask : subTasks.values()) {
            addSubTask(subtask);
            updateEpicStatus(getEpic(subtask.getEpicId()));
        }

        seq = maxId;
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

}

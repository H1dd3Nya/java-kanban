package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Epic extends Task {
    final HashMap<Integer, Subtask> subTasks = new HashMap<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }
    public Epic(String name, String description, Integer id) {
        super(name, description, id, Status.NEW);
    }


    public List<Subtask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }


    public void addTask(Subtask subtask) {
        subTasks.put(subtask.getId(), subtask);
    }

    public void removeTask(Subtask subtask) {
        subTasks.remove(subtask.getId());
    }

    public void updateStatus(Subtask subtask) {
        subTasks.put(subtask.getId(), subtask);
        status = epicStatus();
    }

    private Status epicStatus() {
        int doneStatus = 0;
        int newStatus = 0;
        ArrayList<Status> subTasksStatuses = subTasksStatuses();

        for (Status subTasksStatus : subTasksStatuses) {
            if (subTasksStatus.equals(Status.NEW)) {
                newStatus++;
            } else if (subTasksStatus.equals(Status.DONE)) {
                doneStatus++;
            }
        }
        if (newStatus == subTasksStatuses.size()) {
            return Status.NEW;
        } else if (doneStatus == subTasksStatuses.size()) {
            return Status.DONE;
        } else {
            return Status.IN_PROGRESS;
        }
    }

    private ArrayList<Status> subTasksStatuses() {
        ArrayList<Status> subTasksStatuses = new ArrayList<>();
        for (Subtask subTask : subTasks.values()) {
            subTasksStatuses.add(subTask.status);
        }
        return subTasksStatuses;
    }
}

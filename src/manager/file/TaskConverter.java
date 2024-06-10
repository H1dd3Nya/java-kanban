package manager.file;

import model.*;

public class TaskConverter {
    public static String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + ","
                + task.getDescription() + ",null";
    }

    public static String toString(Subtask subtask) {
        return subtask.getId() + "," + subtask.getType() + "," + subtask.getName() + "," + subtask.getStatus() + ","
                + subtask.getDescription() + "," + subtask.getEpicId();
    }

    public static Task fromString(String value) {
        final String[] columns = value.split(",");

        Integer epicId = null;

        Type type = Type.valueOf(columns[1]);
        if (type.equals(Type.SUBTASK)) {
            epicId = Integer.parseInt(columns[5]);
        }

        Integer id = Integer.parseInt(columns[0]);
        String name = columns[2];
        String description = columns[4];
        Status status = Status.valueOf(columns[3]);

        Task task = null;
        switch (type) {
            case TASK:
                task = new Task(name, description, id, status);
                break;
            case SUBTASK:
                task = new Subtask(name, description, id, status, epicId);
                break;
            case EPIC:
                task = new Epic(name, description, id);
                break;
        }
        return task;
    }
}

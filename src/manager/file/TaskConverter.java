package manager.file;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskConverter {
    public static String toString(Task task) {
        return task.getId() + ","
                + task.getType() + ","
                + task.getName() + ","
                + task.getStatus() + ","
                + task.getDescription() + ",null,"
                + task.getStartTime() + ","
                + task.getDuration().toMinutes();
    }

    public static String toString(Subtask subtask) {
        return subtask.getId() + ","
                + subtask.getType() + ","
                + subtask.getName() + ","
                + subtask.getStatus() + ","
                + subtask.getDescription() + ","
                + subtask.getEpicId() + ","
                + subtask.getStartTime() + ","
                + subtask.getDuration().toMinutes();
    }

    public static Task fromString(String value) {
        final String[] columns = value.split(",");

        Integer epicId = null;

        Type type = Type.valueOf(columns[1]);
        if (type.equals(Type.SUBTASK)) {
            epicId = Integer.parseInt(columns[5]);
        }

        LocalDateTime startTime = null;
        Duration duration = null;

        try {
            startTime = LocalDateTime.parse(columns[6]);
            duration = Duration.ofMinutes(Long.parseLong(columns[7]));
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }

        Integer id = Integer.parseInt(columns[0]);
        String name = columns[2];
        String description = columns[4];
        Status status = Status.valueOf(columns[3]);

        Task task = null;
        switch (type) {
            case TASK:
                task = new Task(name, description, id, status, duration, startTime);
                break;
            case SUBTASK:
                task = new Subtask(name, description, id, status, epicId, startTime, duration);
                break;
            case EPIC:
                task = new Epic(name, description, id);
                task.setStatus(status);
                task.setStartTime(startTime);
                task.setDuration(duration);
                break;
        }
        return task;
    }
}

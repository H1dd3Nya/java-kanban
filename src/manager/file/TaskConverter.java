package manager.file;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskConverter {
    public static String toString(Task task) {
        if (task.getStartTime() != null) {
            return "%d,%s,%s,%s,%s,null,%s,%d,%s".formatted(task.getId(), task.getType(), task.getName(),
                    task.getStatus(), task.getDescription(), task.getStartTime(), task.getDuration().toMinutes(),
                    task.getEndTime());
        }
        return "%d,%s,%s,%s,%s,null,null,%d,null".formatted(task.getId(), task.getType(), task.getName(),
                task.getStatus(), task.getDescription(), task.getDuration().toMinutes());
    }

    public static String toString(Subtask subtask) {
        if (subtask.getStartTime() != null) {
            return "%d,%s,%s,%s,%s,%d,%s,%d,%s".formatted(subtask.getId(), subtask.getType(), subtask.getName(),
                    subtask.getStatus(), subtask.getDescription(), subtask.getEpicId(),
                    subtask.getStartTime(), subtask.getDuration().toMinutes(), subtask.getEndTime());
        }
        return "%d,%s,%s,%s,%s,%d,null,%d,null".formatted(subtask.getId(), subtask.getType(), subtask.getName(),
                subtask.getStatus(), subtask.getDescription(), subtask.getEpicId(), subtask.getDuration().toMinutes());
    }

    public static String toString(Epic epic) {
        if (epic.getStartTime() != null) {
            return "%d,%s,%s,%s,%s,null,%s,%s,%s".formatted(epic.getId(), epic.getType(), epic.getName(),
                    epic.getStatus(), epic.getDescription(), epic.getStartTime(), epic.getDuration().toMinutes(),
                    epic.getEndTime());
        }
        return "%d,%s,%s,%s,%s,null,null,%d,null".formatted(epic.getId(), epic.getType(), epic.getName(),
                epic.getStatus(), epic.getDescription(), epic.getDuration().toMinutes());
    }

    public static Task fromString(String value) {
        final String[] columns = value.split(",");

        Integer epicId = null;

        Type type = Type.valueOf(columns[1]);
        if (type.equals(Type.SUBTASK)) {
            epicId = Integer.parseInt(columns[5]);
        }

        LocalDateTime startTime = null;
        LocalDateTime endTime = null;

        if (!(columns[6].equals("null") && columns[8].equals("null"))) {
            startTime = LocalDateTime.parse(columns[6]);
            endTime = LocalDateTime.parse(columns[8]);
        }

        Integer id = Integer.parseInt(columns[0]);
        String name = columns[2];
        String description = columns[4];
        Status status = Status.valueOf(columns[3]);
        Duration duration = Duration.ofMinutes(Long.parseLong(columns[7]));

        switch (type) {
            case TASK:
                return new Task(name, description, id, status, startTime, duration);
            case SUBTASK:
                return new Subtask(name, description, id, status, epicId, startTime, duration);
            case EPIC:
                Epic epic = new Epic(name, description, id);
                epic.setStatus(status);
                epic.setStartTime(startTime);
                epic.setDuration(duration);
                epic.setEndTime(endTime);
                return epic;
        }
        return null;
    }
}

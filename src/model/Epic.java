package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Epic extends Task {
    final List<Integer> subTasks = new ArrayList<>();
    LocalDateTime endTime = null;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Epic(String name, String description, Integer id) {
        super(name, description, id, Status.NEW);
    }

    public List<Integer> getSubTasks() {
        return subTasks;
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime startTime, Duration duration) {
        endTime = startTime.plus(duration);
    }
}

package model;

public class Subtask extends Task {
    private final Integer epicId;

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Integer id, Status status, int epicId) {
        super(name, description, id, status);
        this.epicId = epicId;
    }


    public int getEpicId() {
        return epicId;
    }

}

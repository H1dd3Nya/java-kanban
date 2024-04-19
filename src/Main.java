import model.Epic;
import service.*;

public class Main {
    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();

        Epic epic = manager.createEpic(new Epic("Test", "test"));
        Epic updatedEpic = new Epic(epic.getName(), "Test123", epic.getId());
        System.out.println(epic);
        manager.updateEpic(updatedEpic);
        System.out.println(manager.getEpic(1));
    }
}
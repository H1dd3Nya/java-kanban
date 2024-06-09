package exception;

import java.nio.file.Path;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(Path path) {
        super("Ошибка в файле: " + path.toFile().getAbsolutePath());
    }
}

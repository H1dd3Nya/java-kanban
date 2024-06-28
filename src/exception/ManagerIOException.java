package exception;

import java.nio.file.Path;

public class ManagerIOException extends RuntimeException {
    public ManagerIOException(Path path) {
        super("Ошибка в файле: " + path.toFile().getAbsolutePath());
    }
}
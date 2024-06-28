package api;

import api.adapters.DurationAdapter;
import api.adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class Utils {
    public static Gson getGson() {
        return new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    public static Optional<Integer> getId(String path) {
        String[] pathParts = path.split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException exception) {
            return Optional.empty();
        }
    }

}

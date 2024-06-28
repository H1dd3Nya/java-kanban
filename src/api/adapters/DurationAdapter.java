package api.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        if (duration != null) {
            long durationTime = duration.toMinutes();
            jsonWriter.value(durationTime);
        } else {
            jsonWriter.value("0");
        }
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        final String text = jsonReader.nextString();
        if (text.equals("null")) {
            return null;
        }
        return Duration.ofMinutes(Long.parseLong(text));
    }
}
package app.serializer;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class TimeSerializer extends JsonSerializer<Time> {

    private DateFormat format = new SimpleDateFormat("HH:mm:ss");

    @Override
    public void serialize(Time value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        }
        else {
            gen.writeString(format.format(value));
        }
    }
}
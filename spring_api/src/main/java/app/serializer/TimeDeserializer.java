package app.serializer;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class TimeDeserializer extends JsonDeserializer<Time> {
    
    private DateFormat format = new SimpleDateFormat("HH:mm:ss");

    @Override
    public Time deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        String value = jp.getText();
        try {
            return (value.isEmpty() ? null : new Time(format.parse(value).getTime()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
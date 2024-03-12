package app.serializer;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class TimestampDeserializer extends JsonDeserializer<Timestamp> {
    
    private DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    @Override
    public Timestamp deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        String value = jp.getText();
        try {
            return (value.isEmpty() ? null : new Timestamp(format.parse(value).getTime()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
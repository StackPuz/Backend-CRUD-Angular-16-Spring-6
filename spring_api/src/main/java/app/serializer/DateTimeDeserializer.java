package app.serializer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class DateTimeDeserializer extends JsonDeserializer<Date> {
    
    private DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    @Override
    public Date deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        String value = jp.getText();
        try {
            return (value.isEmpty() ? null : format.parse(value));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
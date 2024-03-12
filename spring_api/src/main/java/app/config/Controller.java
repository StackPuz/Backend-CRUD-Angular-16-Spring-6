package app.config;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class Controller {

    @ExceptionHandler(Exception.class)
    protected Object getError(Exception ex, HttpServletRequest request) {
        HttpStatusCode status = (ex.getClass() == ResponseStatusException.class ? ((ResponseStatusException)ex).getStatusCode() : HttpStatus.INTERNAL_SERVER_ERROR);
        String message = (ex.getClass() == ResponseStatusException.class ? ((ResponseStatusException)ex).getReason() : ex.getMessage());
        SimpleEntry<String, String> response = new AbstractMap.SimpleEntry<String, String>("message", message);
        return new ResponseEntity<Map.Entry<?, ?>>(response, status);
    }
}
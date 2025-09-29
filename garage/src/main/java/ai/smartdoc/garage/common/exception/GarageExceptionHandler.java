package ai.smartdoc.garage.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class GarageExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GarageExceptionHandler.class);

    public record ErrorResponse(String message, int status, long timestamp) {}

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception) {
        logger.error("Unhandled exception occurred ", exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(exception.getMessage(), 500, System.currentTimeMillis()));
    }

    @ExceptionHandler(GarageException.class)
    public ResponseEntity<ErrorResponse> handleGarageException(GarageException exception) {
        logger.error("Garage exception occurred ", exception);
        return ResponseEntity
                .status(exception.getStatus())
                .body(new ErrorResponse(exception.getMessage(), exception.getStatus().value(), System.currentTimeMillis()));
    }
}

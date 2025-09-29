package ai.smartdoc.garage.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GarageException extends RuntimeException {
    private final HttpStatus status;

    public GarageException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}

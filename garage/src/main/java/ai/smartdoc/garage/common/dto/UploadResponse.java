package ai.smartdoc.garage.common.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class UploadResponse {

    private String docId;
    private String message;
    private HttpStatus httpStatus;

    public UploadResponse(String docId, String message, HttpStatus status) {
        this.docId = docId;
        this.message = message;
        this.httpStatus = status;
    }

    public UploadResponse(String docId, HttpStatus status) {
        this.docId = docId;
        this.httpStatus = status;
    }
}

package ai.smartdoc.garage.file;

import ai.smartdoc.garage.common.dto.UploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FilePort {

    UploadResponse uploadFile(MultipartFile file) throws IOException;
}

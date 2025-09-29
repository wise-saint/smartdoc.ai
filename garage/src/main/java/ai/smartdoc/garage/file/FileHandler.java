package ai.smartdoc.garage.file;

import ai.smartdoc.garage.common.dto.UploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/sd/file")
@CrossOrigin
public class FileHandler {

    @Autowired
    FilePort filePort;

    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public ResponseEntity<UploadResponse> uploadFile(@RequestParam MultipartFile file) {
        return new ResponseEntity<>(filePort.uploadFile(file), HttpStatus.OK);
    }
}

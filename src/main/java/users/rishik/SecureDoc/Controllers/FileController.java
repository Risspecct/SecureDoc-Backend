package users.rishik.SecureDoc.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import users.rishik.SecureDoc.Services.FileService;

import java.io.IOException;

@Slf4j
@RequestMapping("/files")
@RestController
public class FileController {
    private final FileService fileService;

    FileController(FileService fileService){
        this.fileService = fileService;
    }

    @Operation( summary = "Upload a file", description = "This endpoint is used to upload a file to the uploads directory")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestPart("file") MultipartFile file) throws IOException{
        log.info("Uploading user file");
        this.fileService.uploadFile(file);
        return ResponseEntity.ok("File Uploaded Successfully");
    }
}

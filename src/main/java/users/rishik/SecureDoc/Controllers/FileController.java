package users.rishik.SecureDoc.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import users.rishik.SecureDoc.Services.FileService;

import java.io.IOException;

@RequestMapping("/files")
@RestController
public class FileController {
    private final FileService fileService;

    FileController(FileService fileService){
        this.fileService = fileService;
    }

    @Operation( summary = "Upload a file", description = "This endpoint is used to upload a file to the uploads directory")
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException{
        this.fileService.uploadFile(file);
        return ResponseEntity.ok("File Uploaded Successfully");
    }
}

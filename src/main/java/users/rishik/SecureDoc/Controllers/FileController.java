package users.rishik.SecureDoc.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import users.rishik.SecureDoc.Enums.Roles;
import users.rishik.SecureDoc.Services.FileService;

import java.io.IOException;
import java.util.HashMap;

@PreAuthorize("hasRole('USER')")
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
    public ResponseEntity<?> uploadFile(@RequestPart("file") MultipartFile file, @RequestParam(required = false) Roles accessLevel) throws IOException{
        log.info("Uploading user file");
        fileService.uploadFile(file, accessLevel);
        return ResponseEntity.ok("File Uploaded Successfully");
    }

    @Operation( summary = "Download a file", description = "This endpoint is used to download a requested file")
    @GetMapping("/download/{fileName}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileName) throws IOException {
        HashMap<String, Object> fileMap = fileService.downloadFile(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType((String) fileMap.get("content_type")))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(fileMap.get("resource"));
    }

    @Operation( summary = "Get own files", description = "This endpoint is used to fetch your own files")
    @GetMapping("/me")
    public ResponseEntity<?> getOwnFiles(){
        return ResponseEntity.ok(fileService.getFiles());
    }

    @Operation( summary = "Get accessible files", description = "This endpoint is used to get files which current user can access based on their role")
    @GetMapping("/accessible")
    public ResponseEntity<?> getAccessibleFiles(){
        return ResponseEntity.ok(fileService.getAccessibleFiles());
    }
}

package users.rishik.SecureDoc.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import users.rishik.SecureDoc.Enums.Roles;
import users.rishik.SecureDoc.Services.FileService;

import java.io.IOException;

@SuppressWarnings("unused")
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
    @ApiResponses({
            @ApiResponse( responseCode = "200", description = "File uploaded successfully"),
        @ApiResponse( responseCode = "400", description = "Malformed file upload request"),
        @ApiResponse( responseCode = "413", description = "File size limit exceeded"),
        @ApiResponse( responseCode = "415", description = "Unsupported media type"),
        @ApiResponse( responseCode = "403", description = "Access Denied"),
        @ApiResponse( responseCode = "401", description = "Not Authorized"),
        @ApiResponse( responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestPart("file") MultipartFile file, @RequestParam(required = false) Roles accessLevel) throws IOException{
        log.info("Uploading user file");
        fileService.uploadFile(file, accessLevel);
        return ResponseEntity.ok("File Uploaded Successfully");
    }

    @Operation( summary = "Download a file", description = "This endpoint is used to download a requested file")
    @ApiResponses({
            @ApiResponse( responseCode = "200", description = "File fetched successfully"),
            @ApiResponse( responseCode = "404", description = "File not found"),
            @ApiResponse( responseCode = "403", description = "Access Denied"),
            @ApiResponse( responseCode = "401", description = "Not Authorized"),
            @ApiResponse( responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/download/{fileId}")
    public ResponseEntity<?> downloadFile(@PathVariable long fileId) throws IOException {
        return fileService.getFileResource(fileId);
    }

    @Operation( summary = "Get own files", description = "This endpoint is used to fetch your own files")
    @ApiResponses({
            @ApiResponse( responseCode = "200", description = "Files fetched successfully"),
            @ApiResponse( responseCode = "404", description = "File not found"),
            @ApiResponse( responseCode = "403", description = "Access Denied"),
            @ApiResponse( responseCode = "401", description = "Not Authorized"),
            @ApiResponse( responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getOwnFiles(){
        return ResponseEntity.ok(fileService.getFiles());
    }

    @Operation( summary = "Get accessible files", description = "This endpoint is used to get files which current user can access based on their role")
    @ApiResponses({
            @ApiResponse( responseCode = "200", description = "Files fetched successfully"),
            @ApiResponse( responseCode = "404", description = "File not found"),
            @ApiResponse( responseCode = "403", description = "Access Denied"),
            @ApiResponse( responseCode = "401", description = "Not Authorized"),
            @ApiResponse( responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/accessible")
    public ResponseEntity<?> getAccessibleFiles(){
        return ResponseEntity.ok(fileService.getAccessibleFiles());
    }

    @Operation( summary = "Delete file", description = "This endpoint is used to delete requested files")
    @ApiResponses({
            @ApiResponse( responseCode = "200", description = "File deleted successfully"),
            @ApiResponse( responseCode = "404", description = "File not found"),
            @ApiResponse( responseCode = "403", description = "Access Denied"),
            @ApiResponse( responseCode = "401", description = "Not Authorized"),
            @ApiResponse( responseCode = "500", description = "Internal Server Error")
    })
    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable long fileId) throws IOException{
        fileService.deleteFile(fileId);
        return ResponseEntity.ok("File Deleted Successfully");
    }
}

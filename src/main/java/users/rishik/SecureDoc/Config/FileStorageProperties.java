package users.rishik.SecureDoc.Config;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileStorageProperties {
    private final Path uploadPath = Paths.get("uploads");

    public Path getUploadPath() throws IOException{
        if (!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }
        return uploadPath;
    }
}

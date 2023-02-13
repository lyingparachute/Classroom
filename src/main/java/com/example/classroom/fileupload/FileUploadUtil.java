package com.example.classroom.fileupload;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


public class FileUploadUtil {

    private FileUploadUtil() {
        throw new IllegalStateException("Utility class");
    }


    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        Path currentPath = Paths.get(".");
        Path absolutePath = currentPath.toAbsolutePath();
        Path uploadPath = Paths.get(absolutePath + "/src/main/resources/static/img/" + uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("Could not save image file: " + fileName, ioe);
        }
    }

    public static Path getImagesPath(String directoryName) {
        return Path.of("/img")
                .resolve(directoryName);
    }
}

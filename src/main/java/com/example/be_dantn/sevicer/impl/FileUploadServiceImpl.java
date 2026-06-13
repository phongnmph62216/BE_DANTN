package com.example.be_dantn.sevicer.impl;

import com.example.be_dantn.sevicer.FileUploadService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    private final Path rootLocation = Paths.get("uploads");

    public FileUploadServiceImpl() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }
            
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = "";
            if (originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            
            // Generate unique file name to prevent overwriting
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            Path destinationFile = this.rootLocation.resolve(Paths.get(uniqueFileName)).normalize().toAbsolutePath();
            
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new RuntimeException("Cannot store file outside current directory.");
            }
            
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            
            // Trả về đường dẫn tương đối để client có thể truy cập (sẽ được map trong AppConfig)
            return "/uploads/" + uniqueFileName;
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }
}

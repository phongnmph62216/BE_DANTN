package com.example.be_dantn.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin("*")
public class UploadController {

    private final String UPLOAD_DIR =
            System.getProperty("user.dir") + "/uploads/";

    @PostMapping
    public ResponseEntity<String> upload(
            @RequestParam("file") MultipartFile file)
            throws IOException {

        File dir = new File(UPLOAD_DIR);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName =
                System.currentTimeMillis()
                        + "_" +
                        file.getOriginalFilename();

        Path path = Paths.get(UPLOAD_DIR + fileName);

        Files.write(path, file.getBytes());

        return ResponseEntity.ok(fileName);
    }
}
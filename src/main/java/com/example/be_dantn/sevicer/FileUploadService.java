package com.example.be_dantn.sevicer;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    String storeFile(MultipartFile file);
}

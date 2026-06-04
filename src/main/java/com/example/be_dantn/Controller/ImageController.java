package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.Response.ResponseObject;
import com.example.be_dantn.sevicer.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final FileUploadService fileUploadService;

    @PostMapping("/upload")
    public ResponseEntity<ResponseObject<String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = fileUploadService.storeFile(file);
            return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Upload ảnh thành công", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject<>(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi upload ảnh: " + e.getMessage(), null));
        }
    }
}

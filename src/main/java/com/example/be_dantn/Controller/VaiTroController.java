package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.Response.ResponseObject;
import com.example.be_dantn.Entity.VaiTro;
import com.example.be_dantn.Repository.VaiTroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vai-tro")
@RequiredArgsConstructor
public class VaiTroController {

    private final VaiTroRepository vaiTroRepository;

    @GetMapping
    public ResponseEntity<ResponseObject<List<VaiTro>>> getAll() {
        List<VaiTro> list = vaiTroRepository.findAll();
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK, "Lấy danh sách vai trò thành công", list));
    }
}

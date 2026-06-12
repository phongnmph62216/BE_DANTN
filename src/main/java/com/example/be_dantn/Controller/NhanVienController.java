package com.example.be_dantn.Controller;

import com.example.be_dantn.Dto.NhanVienRespon;
import com.example.be_dantn.Dto.NhanVienrequest;
import com.example.be_dantn.sevicer.EmailService;
import com.example.be_dantn.sevicer.NhanvienService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@CrossOrigin("*")
@RequestMapping("api/nhan_vien")
public class NhanVienController {
    private final NhanvienService nhanvienService;
    private final EmailService emailService;

    @GetMapping
    public ResponseEntity<List<NhanVienRespon>> getAllNhanVien() {
        return ResponseEntity.status(HttpStatus.OK).body(nhanvienService.findAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<NhanVienRespon> getNhanVienById(@PathVariable long id) {
        return ResponseEntity.status(HttpStatus.OK).body(nhanvienService.findById(id));
    }

    @PostMapping
    public ResponseEntity<NhanVienRespon> addNhanVien(@Valid @RequestBody NhanVienrequest nhanVienrequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(nhanvienService.add(nhanVienrequest));
    }

    @PutMapping("{id}")
    public ResponseEntity<NhanVienRespon> updateNhanVien(@Valid @RequestBody NhanVienrequest nhanVienrequest, @PathVariable long id) {
        return ResponseEntity.status(HttpStatus.OK).body(nhanvienService.update(nhanVienrequest, id));
    }



    @PatchMapping("/{id}/trang_thai")
    public ResponseEntity<NhanVienRespon> doiTrangThai(
            @PathVariable long id) {

        return ResponseEntity.ok(
                nhanvienService.doiTrangThai(id)
        );
    }


    @PostMapping("/test")
    public String sendTestMail() {

        emailService.sendNhanVienAccount(
                "nguyenmieu190@gmail.com",
                "Nguyen Van b",
                "12yut56"
        );

        return "OK";
    }

    @GetMapping("/export-excel")
    public ResponseEntity<InputStreamResource> exportExcel() {

        return nhanvienService.exportExcel();
    }


}

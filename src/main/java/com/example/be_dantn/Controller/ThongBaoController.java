package com.example.be_dantn.Controller;

import com.example.be_dantn.Entity.ThongBao;
import com.example.be_dantn.Repository.ThongBaoRepository;
import com.example.be_dantn.Dto.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/thong-bao")
public class ThongBaoController {

    @Autowired
    private ThongBaoRepository thongBaoRepository;

    @GetMapping("")
    public ResponseEntity<ResponseObject<List<ThongBao>>> layTatCaThongBao() {
        List<ThongBao> list = thongBaoRepository.findAllByOrderByNgayTaoDesc();
        return ResponseEntity.ok(new ResponseObject<>("success", "Lấy danh sách thông báo thành công", list));
    }

    @GetMapping("/chua-doc/count")
    public ResponseEntity<ResponseObject<Long>> demThongBaoChuaDoc() {
        long count = thongBaoRepository.countByTrangThai(0);
        return ResponseEntity.ok(new ResponseObject<>("success", "Đếm số thông báo chưa đọc thành công", count));
    }

    @PutMapping("/{id}/da-doc")
    public ResponseEntity<ResponseObject<Void>> danhDauDaDoc(@PathVariable Long id) {
        thongBaoRepository.findById(id).ifPresent(tb -> {
            tb.setTrangThai(1);
            thongBaoRepository.save(tb);
        });
        return ResponseEntity.ok(new ResponseObject<>("success", "Đánh dấu đã đọc thành công", null));
    }

    @PutMapping("/da-doc-tat-ca")
    public ResponseEntity<ResponseObject<Void>> danhDauDaDocTatCa() {
        List<ThongBao> list = thongBaoRepository.findAll();
        for (ThongBao tb : list) {
            tb.setTrangThai(1);
        }
        thongBaoRepository.saveAll(list);
        return ResponseEntity.ok(new ResponseObject<>("success", "Đánh dấu tất cả đã đọc thành công", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<Void>> xoaThongBao(@PathVariable Long id) {
        thongBaoRepository.deleteById(id);
        return ResponseEntity.ok(new ResponseObject<>("success", "Xóa thông báo thành công", null));
    }

    @DeleteMapping("/xoa-tat-ca")
    public ResponseEntity<ResponseObject<Void>> xoaTatCaThongBao() {
        thongBaoRepository.deleteAll();
        return ResponseEntity.ok(new ResponseObject<>("success", "Xóa tất cả thông báo thành công", null));
    }
}

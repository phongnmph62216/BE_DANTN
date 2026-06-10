package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.NhanVienRespon;
import com.example.be_dantn.Dto.NhanVienrequest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface NhanvienService {
    List<NhanVienRespon> findAll();
    NhanVienRespon findById(long id);
    NhanVienRespon add(NhanVienrequest  nhanVienrequest);
    NhanVienRespon update(NhanVienrequest  nhanVienrequest, long id);
    NhanVienRespon doiTrangThai(long id);

    ResponseEntity<InputStreamResource> exportExcel();
}

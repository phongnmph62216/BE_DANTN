package com.example.be_dantn.Dto.Request;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DotGiamGiaCreateRequest {

    @NotBlank(message = "Tên đợt giảm giá không được để trống")
    private String tenDotGiamGia;

    @NotNull(message = "Phần trăm giảm không được để trống")
    @Min(value = 1, message = "Phần trăm giảm phải lớn hơn 0")
    @Max(value = 100, message = "Phần trăm giảm không được lớn hơn 100")
    private Integer phanTramGiam;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime ngayBatDau;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime ngayKetThuc;

    // Có thể rỗng nếu không áp dụng cho sản phẩm nào ngay
    private List<Long> danhSachIdChiTietSanPham;
}

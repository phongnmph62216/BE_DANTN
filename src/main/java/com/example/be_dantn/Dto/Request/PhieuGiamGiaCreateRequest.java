package com.example.be_dantn.Dto.Request;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PhieuGiamGiaCreateRequest {

    private String maPhieu;

    @NotBlank(message = "Tên phiếu không được để trống")
    private String tenPhieu;

    @NotNull(message = "Kiểu áp dụng không được để trống")
    private Integer kieuApDung; // 0: Toàn cửa hàng, 1: Cá nhân

    @NotNull(message = "Loại giảm không được để trống")
    private Integer loaiGiam; // 0: Giảm %, 1: Giảm tiền mặt

    @NotNull(message = "Giá trị giảm không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá trị giảm phải lớn hơn 0")
    private BigDecimal giaTriGiam;

    private BigDecimal giamToiDa;

    @NotNull(message = "Đơn tối thiểu không được để trống")
    @DecimalMin(value = "0.0", message = "Đơn tối thiểu không được âm")
    private BigDecimal donToiThieu;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer soLuong;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime ngayBatDau;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime ngayKetThuc;

    private List<Long> danhSachKhachHangIds;
}

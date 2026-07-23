package com.example.be_dantn.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SanPhamCreateRequest {
    private String maSanPham;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String tenSanPham;

    private String moTa;
    private String hinhAnh;

    @NotNull(message = "Thương hiệu không được để trống")
    private Long idThuongHieu;

    @NotNull(message = "Xuất xứ không được để trống")
    private Long idXuatSu;

    @NotNull(message = "Chất liệu không được để trống")
    private Long idChatLieu;

    @NotNull(message = "Loại sản phẩm không được để trống")
    private Long idLoaiSanPham;

    @NotNull(message = "Kiểu dáng không được để trống")
    private Long idKieuDang;

    @NotNull(message = "Cổ áo không được để trống")
    private Long idCoAo;

    @NotNull(message = "Tay áo không được để trống")
    private Long idTayAo;

    @NotNull(message = "Vai áo không được để trống")
    private Long idVaiAo;

    private List<BienTheRequest> danhSachBienThe;

    @Data
    public static class BienTheRequest {
        @NotNull(message = "Màu sắc không được để trống")
        private Long idMauSac;

        @NotNull(message = "Kích thước không được để trống")
        private Long idKichThuoc;

        @NotNull(message = "Số lượng tồn không được để trống")
        private Integer soLuongTon;

        private BigDecimal giaNhap;

        @NotNull(message = "Giá bán không được để trống")
        private BigDecimal giaBan;

        private String anh;
    }
}

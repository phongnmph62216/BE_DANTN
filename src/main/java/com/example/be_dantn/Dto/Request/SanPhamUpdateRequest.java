package com.example.be_dantn.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SanPhamUpdateRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String tenSanPham;

    private String moTa;
    private String hinhAnh;

    @NotNull(message = "Trạng thái không được để trống")
    private Integer trangThai;

    // 8 ID thuộc tính
    @NotNull(message = "Thương hiệu không được để trống")
    private Long idThuongHieu;
    @NotNull(message = "Chất liệu không được để trống")
    private Long idChatLieu;
    @NotNull(message = "Xuất xứ không được để trống")
    private Long idXuatSu;
    @NotNull(message = "Kiểu dáng không được để trống")
    private Long idKieuDang;
    @NotNull(message = "Loại sản phẩm không được để trống")
    private Long idLoaiSanPham;
    @NotNull(message = "Cổ áo không được để trống")
    private Long idCoAo;
    @NotNull(message = "Tay áo không được để trống")
    private Long idTayAo;
    @NotNull(message = "Vai áo không được để trống")
    private Long idVaiAo;
}

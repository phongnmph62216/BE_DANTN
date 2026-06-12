package com.example.be_dantn.sevicer;

import com.example.be_dantn.Dto.NhanVienRespon;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
public class ExcelService {
    public static ByteArrayInputStream exportExcel(
            List<NhanVienRespon> danhSachNhanVien) {

        try {

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet =
                    workbook.createSheet("Danh sách nhân viên");

            String[] columns = {
                    "Mã NV",
                    "Họ và tên",
                    "SĐT",
                    "Email",
                    "CCCD",
                    "Giới tính",
                    "Ngày sinh",
                    "Địa chỉ",
                    "Vai trò",
                    "Trạng thái",
                    "Ngày vào làm"
            };

            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < columns.length; i++) {

                Cell cell =
                        headerRow.createCell(i);

                cell.setCellValue(columns[i]);
            }

            int rowNum = 1;

            for (NhanVienRespon nv : danhSachNhanVien) {

                Row row =
                        sheet.createRow(rowNum++);

                row.createCell(0)
                        .setCellValue(nv.getMaNhanVien());

                row.createCell(1)
                        .setCellValue(nv.getHoVaTen());

                row.createCell(2)
                        .setCellValue(nv.getSoDienThoai());

                row.createCell(3)
                        .setCellValue(nv.getEmail());

                row.createCell(4)
                        .setCellValue(nv.getCccd());

                row.createCell(5)
                        .setCellValue(
                                nv.getGioiTinh() == 1
                                        ? "Nam"
                                        : "Nữ"
                        );

                row.createCell(6)
                        .setCellValue(
                                nv.getNgaySinh() == null
                                        ? ""
                                        : nv.getNgaySinh().toString()
                        );

                row.createCell(7)
                        .setCellValue(nv.getDiaChi());

                row.createCell(8)
                        .setCellValue(nv.getVaiTro());

                row.createCell(9)
                        .setCellValue(
                                nv.getTrangThai() == 1
                                        ? "Đang làm"
                                        : "Nghỉ việc"
                        );

                row.createCell(10)
                        .setCellValue(
                                nv.getNgayVaoLam() == null
                                        ? ""
                                        : nv.getNgayVaoLam().toString()
                        );
            }

            for (int i = 0; i < columns.length; i++) {

                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out =
                    new ByteArrayOutputStream();

            workbook.write(out);

            workbook.close();

            return new ByteArrayInputStream(
                    out.toByteArray()
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Lỗi xuất Excel", e);
        }
    }
}

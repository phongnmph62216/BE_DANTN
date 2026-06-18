# API Documentation

This document outlines the RESTful API endpoints for managing the system's core modules, including Products, Attributes, Sales Campaigns, and Customers. These APIs follow a standard CRUD pattern and support pagination, searching, and filtering for collection endpoints.

## General API Structure

*   **Base URL:** `/api/v1/{entity-name}`
*   **Response Wrapper:** All responses are wrapped in a standard `ResponseObject`:
    ```json
    {
      "status": "OK", // HTTP Status
      "message": "Success message",
      "data": { ... } // Actual response data
    }
    ```

---

## I. Quản Lý Hình Ảnh (Image Management)
*   **Base URL:** `/api/v1/images`
*   **POST /api/v1/images/upload**: Tải lên một file ảnh và trả về URL tương đối.

---

## II. Quản Lý Đợt Giảm Giá (Sales Campaigns)
*   **Base URL:** `/api/v1/dot-giam-gia`

### Endpoints:
*   **GET /**
    *   **Description:** Lấy danh sách các đợt giảm giá, hỗ trợ phân trang và lọc.
    *   **Query Parameters:** `keyword` (String), `trangThai` (Integer), `tuNgay` (LocalDateTime), `denNgay` (LocalDateTime), `page`, `size`.
    *   **Response Data:** `Page<DotGiamGiaResponseDTO>`

*   **GET /{id}**
    *   **Description:** Lấy thông tin chi tiết một đợt giảm giá để hiển thị lên Form cập nhật. Đặc biệt trả về thêm danh sách các biến thể sản phẩm đang được áp dụng.
    *   **Response Data:** `DotGiamGiaResponseDTO` (Bao gồm trường `danhSachIdChiTietSanPham`).

*   **POST /**
    *   **Description:** Tạo mới một đợt giảm giá và áp dụng nó cho danh sách các biến thể sản phẩm.

*   **PUT /{id}**
    *   **Description:** Cập nhật thông tin đợt giảm giá và danh sách các biến thể sản phẩm áp dụng.

*   **PATCH /{id}/status**
    *   **Description:** Đảo ngược trạng thái của một đợt giảm giá.

---

## III. Quản Lý Sản Phẩm (Product Management)

### 1. Sản Phẩm (Product)
*   **Base URL:** `/api/v1/san-pham`
*   **GET /**: Lấy danh sách sản phẩm (phân trang, lọc).
*   **GET /{id}**: Lấy chi tiết sản phẩm.
*   **GET /qr-scan/{maSanPham}**: Lấy chi tiết sản phẩm bằng mã QR.
*   **POST /**: Thêm mới sản phẩm và các biến thể.
*   **PUT /{id}**: Cập nhật thông tin cơ bản của sản phẩm.
*   **PATCH /{id}/status**: Đảo ngược trạng thái sản phẩm.
*   **GET /export-excel**: Xuất danh sách sản phẩm ra file Excel.

### 2. Biến Thể Sản Phẩm (Product Variant)
*   **Base URL:** `/api/v1/chi-tiet-san-pham`
*   **GET /**: Lấy danh sách biến thể (phân trang, lọc đa dạng).
*   **GET /{id}**: Lấy chi tiết một biến thể.
*   **GET /qr-scan/{maChiTietSanPham}**: Lấy chi tiết biến thể bằng mã QR.
*   **PUT /{id}**: Cập nhật một biến thể.
*   **PATCH /{id}/status**: Đảo ngược trạng thái một biến thể.
*   **GET /export-excel**: Xuất danh sách biến thể ra file Excel.

---

## IV. Thuộc Tính (Attributes)

### 0. Lấy tất cả thuộc tính (Dành cho Dropdown/Combobox)
*   **Endpoint:** `GET /api/v1/attributes/all-active`
*   **Description:** API tiện ích để lấy tất cả 10 thuộc tính đang hoạt động.

*... (Các API CRUD cho từng thuộc tính)*

---

## V. Quản Lý Khách Hàng (Customer Management)
*   **Base URL:** `/api/v1/khach-hang`

### 1. API Khách Hàng
*   **GET /**
    *   **Description:** Lấy danh sách khách hàng (Có phân trang & Lọc).
    *   **Query Parameters:** `keyword` (Tên, SĐT, Email), `gioiTinh`, `trangThai`, `page`, `size`.
    *   **Response Data:** `Page<KhachHangResponseDTO>` (Chứa trường `diaChiMacDinh` gộp sẵn).

*   **GET /{id}**
    *   **Description:** Lấy thông tin chi tiết khách hàng và toàn bộ danh sách địa chỉ.
    *   **Response Data:** `KhachHangDetailResponseDTO` (Chứa thông tin cơ bản và `List<DiaChiResponseDTO>` với địa chỉ mặc định được xếp lên đầu).

*   **POST /**
    *   **Description:** Thêm mới một khách hàng.
    *   **Request Body:** `KhachHangRequest`

*   **PUT /{id}**
    *   **Description:** Cập nhật thông tin cơ bản của khách hàng (Không bao gồm địa chỉ).
    *   **Request Body:** `KhachHangRequest`

*   **PATCH /{id}/status**
    *   **Description:** Đảo ngược trạng thái hoạt động của khách hàng.

*   **GET /export-excel**
    *   **Description:** Xuất danh sách khách hàng ra file Excel.

### 2. API Địa Chỉ (Lồng trong Khách Hàng)
*   **POST /{khachHangId}/dia-chi**
    *   **Description:** Thêm mới một địa chỉ cho khách hàng. Nếu là địa chỉ đầu tiên, tự động gán làm mặc định. Nếu gửi lên `kieuDiaChiLaMacDinh = true`, các địa chỉ cũ sẽ được cập nhật thành `false`.
    *   **Request Body:** `DiaChiRequest`

*   **PATCH /{khachHangId}/dia-chi/{diaChiId}/mac-dinh**
    *   **Description:** Cập nhật một địa chỉ cụ thể làm địa chỉ mặc định, đồng thời gỡ bỏ trạng thái mặc định của các địa chỉ khác thuộc cùng khách hàng.

---

## VI. Quản Lý Phiếu Giảm Giá (Voucher Management)
*   **Base URL:** `/api/v1/phieu-giam-gia`

### Endpoints:
*   **GET /**
    *   **Description:** Lấy danh sách các phiếu giảm giá, hỗ trợ phân trang và đa bộ lọc.
    *   **Query Parameters:**
        *   `keyword` (String): Tìm theo mã hoặc tên phiếu.
        *   `loaiGiam` (Integer): Lọc theo loại giảm (0: %, 1: Tiền).
        *   `tuNgay` (LocalDateTime): Lọc các phiếu có ngày kết thúc sau hoặc bằng ngày này.
        *   `denNgay` (LocalDateTime): Lọc các phiếu có ngày bắt đầu trước hoặc bằng ngày này.
        *   `trangThai` (Integer): Lọc theo trạng thái (0: Sắp diễn ra, 1: Đang diễn ra, 2: Đã kết thúc).
        *   `page`, `size`.
    *   **Response Data:** `Page<PhieuGiamGiaResponseDTO>`

*   **GET /{id}**
    *   **Description:** Lấy chi tiết thông tin phiếu giảm giá để hiển thị lên Form cập nhật. Đặc biệt trả về mảng `danhSachKhachHangIds` (chứa các ID khách hàng đang được áp dụng phiếu này).
    *   **Response Data:** `PhieuGiamGiaResponseDTO`

*   **POST /**
    *   **Description:** Tạo mới phiếu giảm giá. Lưu ý nếu áp dụng cá nhân (`kieuApDung = 1`) thì cần gửi lên mảng `danhSachKhachHangIds`.
    *   **Request Body:** `PhieuGiamGiaCreateRequest`
        ```json
        {
          "maPhieu": "SUMMER_SALE",
          "tenPhieu": "Sale Mùa Hè",
          "kieuApDung": 1,
          "loaiGiam": 0,
          "giaTriGiam": 20,
          "giamToiDa": 100000,
          "donToiThieu": 200000,
          "soLuong": 100,
          "ngayBatDau": "2024-07-01T00:00:00",
          "ngayKetThuc": "2024-07-31T23:59:59",
          "danhSachKhachHangIds": [1, 2]
        }
        ```

*   **PUT /{id}**
    *   **Description:** Cập nhật thông tin phiếu giảm giá. API này sẽ xóa toàn bộ danh sách khách hàng cũ đang áp dụng phiếu này (nếu có) và tạo mới các bản ghi dựa trên `danhSachKhachHangIds` gửi lên.
    *   **Request Body:** `PhieuGiamGiaUpdateRequest` (Cấu trúc tương tự CreateRequest).

*   **PATCH /{id}/status**
    *   **Description:** Đảo ngược trạng thái hoạt động của phiếu giảm giá (VD: Đang diễn ra <-> Đã kết thúc).

---

## VII. Quản Lý Nhân Viên (Employee Management)
*   **Base URL:** `/api/v1/nhan-vien`

### Endpoints:
*   **GET /**
    *   **Description:** Lấy danh sách nhân viên (Có phân trang & Lọc).
    *   **Query Parameters:** `keyword` (Tên, SĐT, Email, Mã NV), `trangThai`, `page`, `size`.
    *   **Response Data:** `Page<NhanVienResponseDTO>`

*   **GET /{id}**
    *   **Description:** Lấy thông tin chi tiết nhân viên để hiển thị lên Form sửa.
    *   **Response Data:** `NhanVien` (Entity)

*   **POST /**
    *   **Description:** Thêm mới một nhân viên. Mật khẩu mặc định được đặt bằng số điện thoại và tự động mã hóa BCrypt.
    *   **Request Body:** `NhanVienCreateRequest`

*   **PUT /{id}**
    *   **Description:** Cập nhật thông tin cơ bản của nhân viên.
    *   **Request Body:** `NhanVienUpdateRequest`

*   **PATCH /{id}/status**
    *   **Description:** Đảo ngược trạng thái hoạt động của nhân viên.

*   **GET /export-excel**
    *   **Description:** Xuất danh sách nhân viên ra file Excel.

---

## VIII. Quản Lý Hóa Đơn (Invoice Management)
*   **Base URL:** `/api/v1/hoa-don`

### Endpoints:
*   **GET /**
    *   **Description:** Lấy danh sách hóa đơn với bộ lọc động và phân trang.
    *   **Query Parameters:**
        *   `maHoaDon` (String): Tìm kiếm tương đối theo Mã hóa đơn, Tên khách hàng, hoặc SĐT khách hàng.
        *   `tuNgay` (LocalDateTime): Lọc theo ngày tạo hóa đơn (từ ngày).
        *   `denNgay` (LocalDateTime): Lọc theo ngày tạo hóa đơn (đến ngày).
        *   `loaiDon` (Integer): Lọc theo loại đơn (0: Tại quầy, 1: Online/Giao hàng).
        *   `trangThai` (Integer): Lọc theo trạng thái hóa đơn (0: Chưa xác nhận, 1: Đã xác nhận, 2: Chờ giao, 3: Đang giao, 4: Đã hoàn thành, 5: Đã hủy).
        *   `page` (int): Trang hiện tại (mặc định: 0).
        *   `size` (int): Số lượng bản ghi trên một trang (mặc định: 10).
    *   **Response Data:** `Page<HoaDonResponseDTO>`

*   **GET /export-excel**
    *   **Description:** Xuất danh sách hóa đơn thỏa mãn điều kiện lọc ra file Excel.
    *   **Query Parameters:** Tương tự như API lấy danh sách, nhưng không có `page` và `size`.
    *   **Response:** `ResponseEntity<byte[]>` (File .xlsx).

*   **GET /{id}**
    *   **Description:** Lấy toàn bộ thông tin chi tiết của một hóa đơn để hiển thị ở màn hình Chi tiết hóa đơn.
    *   **Response Data:** `HoaDonDetailResponseDTO` (Bao gồm nhiều khối thông tin: chung, khách hàng, tiền, sản phẩm, thanh toán, timeline).

*   **PUT /{id}/trang-thai**
    *   **Description:** Cập nhật trạng thái của một hóa đơn. API có logic để validate luồng chuyển trạng thái hợp lệ tùy theo loại đơn (Tại quầy / Online).
    *   **Request Body:** 
        ```json
        {
          "trangThaiMoi": 1,
          "ghiChu": "Khách hàng đã xác nhận đơn hàng."
        }
        ```
    *   **Response:** `ResponseObject<Void>`

*   **GET /{id}/lich-su**
    *   **Description:** Lấy danh sách lịch sử các thao tác đã thực hiện trên hóa đơn.
    *   **Response Data:** `List<LichSuHoaDonResponseDTO>`

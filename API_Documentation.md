# API Documentation for Product and Attributes

This document outlines the RESTful API endpoints for managing products, product attributes, and related functionalities. These APIs follow a standard CRUD pattern, and also support pagination, searching, and filtering for collection endpoints.

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

### Endpoints:

*   **POST /api/v1/images/upload**
    *   **Description:** Tải lên một file ảnh. API sẽ lưu file vào thư mục `uploads` trên server và trả về đường dẫn tương đối của ảnh.
    *   **Request:** `multipart/form-data` với key là `file`.
    *   **Response Data:** `String` (URL của ảnh, ví dụ: `/uploads/ten_file_duy_nhat.jpg`)

---

## II. Quản Lý Sản Phẩm (Product Management)

### 1. Sản Phẩm (Product)

*   **Base URL:** `/api/v1/san-pham`

#### Endpoints:

*   **GET /api/v1/san-pham**
    *   **Description:** Lấy danh sách sản phẩm, hỗ trợ phân trang, tìm kiếm theo `keyword` (mã/tên), và lọc theo `idThuongHieu`, `idChatLieu`, `trangThai`.
    *   **Query Parameters:** `page`, `size`, `keyword`, `idThuongHieu`, `idChatLieu`, `trangThai`
    *   **Response Data:** `Page<SanPhamResponse>`

*   **GET /api/v1/san-pham/{id}**
    *   **Description:** Lấy thông tin chi tiết của một sản phẩm (bao gồm 8 ID thuộc tính) để hiển thị lên form cập nhật.
    *   **Response Data:** `SanPhamDetailResponse`
    *   **DTO Structure (`SanPhamDetailResponse`):**
        ```json
        {
          "id": 1,
          "maSanPham": "SP001",
          "tenSanPham": "Áo Polo Nike",
          "moTa": "Mô tả...",
          "hinhAnh": "/uploads/anh.jpg",
          "trangThai": 1,
          "idThuongHieu": 1,
          "idChatLieu": 1,
          "idXuatSu": 1,
          "idKieuDang": 2,
          "idLoaiSanPham": 1,
          "idCoAo": 2,
          "idTayAo": 1,
          "idVaiAo": 2
        }
        ```

*   **GET /api/v1/san-pham/qr-scan/{maSanPham}**
    *   **Description:** Lấy thông tin chi tiết của một sản phẩm dựa vào mã QR code (`maSanPham`). Dùng chung DTO với API GET theo ID.
    *   **Response Data:** `SanPhamDetailResponse`

*   **POST /api/v1/san-pham**
    *   **Description:** Thêm mới một sản phẩm và các biến thể của nó.
    *   **Request Body:** `SanPhamCreateRequest`

*   **PUT /api/v1/san-pham/{id}**
    *   **Description:** Cập nhật thông tin cơ bản và 8 thuộc tính của một sản phẩm. API này không ảnh hưởng đến các biến thể.
    *   **Request Body:** `SanPhamUpdateRequest`
        ```json
        {
          "tenSanPham": "Áo Polo Nike Mới",
          "moTa": "Mô tả đã cập nhật",
          "hinhAnh": "/uploads/new_image.jpg",
          "trangThai": 1,
          "idThuongHieu": 1,
          "idChatLieu": 1,
          "idXuatSu": 1,
          "idKieuDang": 2,
          "idLoaiSanPham": 1,
          "idCoAo": 2,
          "idTayAo": 1,
          "idVaiAo": 2
        }
        ```
    *   **Response Data:** `SanPhamDetailResponse` (Dữ liệu sản phẩm sau khi cập nhật).

*   **PATCH /api/v1/san-pham/{id}/status**
    *   **Description:** Đảo ngược trạng thái của sản phẩm (1: Kinh doanh, 0: Ngừng kinh doanh).

*   **GET /api/v1/san-pham/export-excel**
    *   **Description:** Xuất danh sách sản phẩm ra file Excel. API này nhận các tham số lọc của API GET (trừ phân trang).
    *   **Query Parameters:** `keyword`, `idThuongHieu`, `idChatLieu`, `trangThai`.
    *   **Response:** File `.xlsx`.

### 2. Biến Thể Sản Phẩm (Product Variant)

*   **Base URL:** `/api/v1/chi-tiet-san-pham`

#### Endpoints:

*   **GET /api/v1/chi-tiet-san-pham**
    *   **Description:** Lấy danh sách tất cả các biến thể sản phẩm, hỗ trợ phân trang và bộ lọc đa dạng.
    *   **Query Parameters:** `keyword`, `idMauSac`, `idKichThuoc`, `trangThai`, `minPrice`, `maxPrice`, `page`, `size`.
    *   **Response Data:** `Page<ChiTietSanPhamResponseDTO>`

*   **GET /api/v1/chi-tiet-san-pham/{id}**
    *   **Description:** Lấy thông tin chi tiết của một biến thể để hiển thị lên form cập nhật.
    *   **Response Data:** `ChiTietSanPhamResponseDTO`

*   **GET /api/v1/chi-tiet-san-pham/qr-scan/{maChiTietSanPham}**
    *   **Description:** Lấy thông tin chi tiết của một biến thể dựa vào mã QR code (`maChiTietSanPham`).
    *   **Response Data:** `ChiTietSanPhamResponseDTO`

*   **PUT /api/v1/chi-tiet-san-pham/{id}**
    *   **Description:** Cập nhật thông tin chi tiết của một biến thể.
    *   **Request Body:** `ChiTietSanPhamUpdateRequest`

*   **PATCH /api/v1/chi-tiet-san-pham/{id}/status**
    *   **Description:** Đảo ngược trạng thái của một biến thể (1: Đang bán, 0: Ngừng bán).

*   **GET /api/v1/chi-tiet-san-pham/export-excel**
    *   **Description:** Xuất danh sách biến thể ra file Excel.
    *   **Query Parameters:** `keyword`, `idMauSac`, `idKichThuoc`, `trangThai`, `minPrice`, `maxPrice`.
    *   **Response:** File `.xlsx`.

---

## III. Thuộc Tính Sản Phẩm (Product Attributes)

### 0. Lấy tất cả thuộc tính (Dành cho Dropdown/Combobox)

*   **Endpoint:** `GET /api/v1/attributes/all-active`
*   **Description:** API tiện ích giúp lấy danh sách id và tên của TẤT CẢ 10 thuộc tính đang hoạt động (`trangThai = 1`) chỉ trong 1 lần gọi.

*... (Các API thuộc tính khác tương tự)*

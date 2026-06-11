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
*   **POST /api/v1/images/upload**: Tải lên một file ảnh và trả về URL tương đối.

---

## II. Quản Lý Đợt Giảm Giá (Sales Campaigns)
*   **Base URL:** `/api/v1/dot-giam-gia`

### Endpoints:
*   **GET /api/v1/dot-giam-gia**
    *   **Description:** Lấy danh sách các đợt giảm giá, hỗ trợ phân trang và lọc.
    *   **Query Parameters:**
        *   `keyword` (String): Tìm theo mã hoặc tên đợt giảm giá.
        *   `trangThai` (Integer): Lọc theo trạng thái.
        *   `tuNgay` (LocalDateTime): Lọc các đợt có ngày bắt đầu sau hoặc bằng ngày này (Format: `YYYY-MM-DDTHH:mm:ss`).
        *   `denNgay` (LocalDateTime): Lọc các đợt có ngày bắt đầu trước hoặc bằng ngày này (Format: `YYYY-MM-DDTHH:mm:ss`).
        *   `page`, `size`.
    *   **Response Data:** `Page<DotGiamGiaResponseDTO>`

*   **POST /api/v1/dot-giam-gia**
    *   **Description:** Tạo mới một đợt giảm giá và áp dụng nó cho danh sách các biến thể sản phẩm.
    *   **Request Body:** `DotGiamGiaCreateRequest`
        ```json
        {
          "tenDotGiamGia": "Siêu Sale Giữa Năm",
          "phanTramGiam": 20,
          "ngayBatDau": "2024-06-01T00:00:00",
          "ngayKetThuc": "2024-06-15T23:59:59",
          "danhSachIdChiTietSanPham": [1, 2, 5] // Mảng ID của các biến thể cần áp dụng
        }
        ```

*   **PATCH /api/v1/dot-giam-gia/{id}/status**
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

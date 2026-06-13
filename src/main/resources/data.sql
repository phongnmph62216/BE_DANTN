-- Seed data for chat_lieu
INSERT INTO chat_lieu (ma_chat_lieu, ten_chat_lieu, trang_thai, ngay_tao, ngay_sua, nguoi_tao, nguoi_sua) VALUES
                                                                                                              ('CL001', N'Cotton', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                              ('CL002', N'Polyester', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                              ('CL003', N'Rayon', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                              ('CL004', N'Lụa', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                              ('CL005', N'Kaki', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL);

-- Seed data for loai_san_pham
INSERT INTO loai_san_pham (ma_loai_san_pham, ten_loai_san_pham, trang_thai, ngay_tao, ngay_sua, nguoi_tao, nguoi_sua) VALUES
                                                                                                                          ('LSP001', N'Áo thun', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                                          ('LSP002', N'Áo sơ mi', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                                          ('LSP003', N'Áo cộc', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                                          ('LSP004', N'Áo mùa hè', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                                          ('LSP005', N'Áo lười', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL);

-- Seed data for thuong_hieu
INSERT INTO thuong_hieu (ma_thuong_hieu, ten_thuong_hieu, trang_thai, ngay_tao, ngay_sua, nguoi_tao, nguoi_sua) VALUES
                                                                                                                    ('TH001', N'Nike', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                                    ('TH002', N'Adidas', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                                    ('TH003', N'Zara', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                                    ('TH004', N'H&M', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                                    ('TH005', N'Uniqlo', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL);

-- Seed data for tay_ao
INSERT INTO tay_ao (ma_tay_ao, ten_tay_ao, trang_thai, ngay_tao, ngay_sua, nguoi_tao, nguoi_sua) VALUES
                                                                                                     ('TA001', N'Tay ngắn', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                     ('TA002', N'Tay dài', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                     ('TA003', N'Tay lỡ', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                     ('TA004', N'Không tay', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                     ('TA005', N'Tay phồng', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL);

-- Seed data for vai_ao
INSERT INTO vai_ao (ma_vai_ao, ten_vai_ao, trang_thai, ngay_tao, ngay_sua, nguoi_tao, nguoi_sua) VALUES
                                                                                                     ('VA001', N'Vai xuôi', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                     ('VA002', N'Vai ngang', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                     ('VA003', N'Vai raglan', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                     ('VA004', N'Vai bồng', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                     ('VA005', N'Vai trễ', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL);

-- Seed data for kieu_dang
INSERT INTO kieu_dang (ma_kieu_dang, ten_kieu_dang, trang_thai, ngay_tao, ngay_sua, nguoi_tao, nguoi_sua) VALUES
                                                                                                              ('KD001', N'Slim fit', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                              ('KD002', N'Regular fit', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                              ('KD003', N'Oversize', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                              ('KD004', N'Bodycon', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                              ('KD005', N'Suông', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL);

-- Seed data for co_ao
INSERT INTO co_ao (ma_co_ao, ten_co_ao, trang_thai, ngay_tao, ngay_sua, nguoi_tao, nguoi_sua) VALUES
                                                                                                  ('CA001', N'Cổ tròn', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                  ('CA002', N'Cổ bẻ', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                  ('CA003', N'Cổ tim', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                  ('CA004', N'Cổ trụ', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                  ('CA005', N'Cổ V', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL);

-- Seed data for mau_sac
INSERT INTO mau_sac (ma_mau_sac, ten_mau_sac, trang_thai, ngay_tao, ngay_sua, nguoi_tao, nguoi_sua) VALUES
                                                                                                        ('MS001', N'Đỏ', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                        ('MS002', N'Xanh', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                        ('MS003', N'Vàng', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                        ('MS004', N'Trắng', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                        ('MS005', N'Đen', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL);

-- Seed data for kich_thuoc
INSERT INTO kich_thuoc (ma_kich_thuoc, ten_kich_thuoc, trang_thai, ngay_tao, ngay_sua, nguoi_tao, nguoi_sua) VALUES
                                                                                                                 ('KT001', N'S', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                                 ('KT002', N'M', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                                 ('KT003', N'L', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                                 ('KT004', N'XL', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                                 ('KT005', N'XXL', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL);

-- Seed data for xuat_su
INSERT INTO xuat_su (ma_xuat_su, ten_xuat_su, trang_thai, ngay_tao, ngay_sua, nguoi_tao, nguoi_sua) VALUES
                                                                                                        ('XS001', N'Việt Nam', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                        ('XS002', N'Trung Quốc', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                        ('XS003', N'Thái Lan', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                        ('XS004', N'Hàn Quốc', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
                                                                                                        ('XS005', N'Nhật Bản', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL);

-- Seed data for san_pham
INSERT INTO san_pham (ma_san_pham, ten_san_pham, mo_ta, hinh_anh, trang_thai, id_thuong_hieu, id_chat_lieu, id_xuat_su, id_kieu_dang, id_loai_san_pham, id_co_ao, id_tay_ao, id_vai_ao, ngay_tao, ngay_sua) VALUES
                                                                                                                                                                                                                ('SP001', N'Áo Polo Nike', N'Áo polo thể thao, thoáng mát', '/uploads/243c8d88-ce3a-4d07-ac23-153e3c1db4dd.png', 1, 1, 1, 1, 2, 1, 2, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                                                                                                                ('SP002', N'Áo Thun Adidas', N'Áo thun 3 sọc huyền thoại', '/uploads/29aec001-d056-42f2-b7d1-5ec7e64e1ec1.png', 1, 2, 1, 2, 2, 1, 1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seed data for dot_giam_gia
INSERT INTO dot_giam_gia (ma_dot_giam_gia, ten_dot_giam_gia, phan_tram_giam, trang_thai, ngay_bat_dau, ngay_ket_thuc, nguoi_tao, nguoi_sua) VALUES
('DGG001', N'Giảm giá cuối năm', 15, 1, '2024-12-20 00:00:00', '2024-12-31 23:59:59', 'system', 'system'),
('DGG002', N'Chào hè sôi động', 20, 1, '2024-06-01 00:00:00', '2024-06-15 23:59:59', 'system', 'system'),
('DGG003', N'Black Friday', 50, 0, '2023-11-24 00:00:00', '2023-11-26 23:59:59', 'system', 'system'),
('DGG004', N'Xả hàng tồn kho', 30, 1, '2024-07-15 00:00:00', '2024-07-31 23:59:59', 'system', 'system'),
('DGG005', N'Mừng sinh nhật shop', 10, 1, '2024-08-08 00:00:00', '2024-08-10 23:59:59', 'system', 'system');

-- Seed data for chi_tiet_san_pham
-- Product 1: Áo Polo Nike
INSERT INTO chi_tiet_san_pham (ma_chi_tiet_san_pham, id_san_pham, id_mau_sac, id_kich_thuoc, so_luong_ton, gia_nhap, gia_ban, anh, trang_thai, ngay_tao, ngay_sua, id_dot_giam_gia) VALUES
                                                                                                                                                                       ('CTSP001', 1, 1, 2, 50, 200000, 350000, '/uploads/d4fd4a55-abcb-4441-81b1-c4159cd5c6e9.png', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
                                                                                                                                                                       ('CTSP002', 1, 1, 3, 30, 200000, 350000, '/uploads/a6680008-e37c-4bfd-9117-abb5458941a0.png', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
                                                                                                                                                                       ('CTSP003', 1, 2, 2, 60, 210000, 360000, '/uploads/a6680008-e37c-4bfd-9117-abb5458941a0.png', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, null),
                                                                                                                                                                       ('CTSP004', 1, 2, 3, 40, 210000, 360000, '/uploads/a6680008-e37c-4bfd-9117-abb5458941a0.png', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, null);

-- Product 2: Áo Thun Adidas
INSERT INTO chi_tiet_san_pham (ma_chi_tiet_san_pham, id_san_pham, id_mau_sac, id_kich_thuoc, so_luong_ton, gia_nhap, gia_ban, anh, trang_thai, ngay_tao, ngay_sua, id_dot_giam_gia) VALUES
                                                                                                                                                                       ('CTSP005', 2, 4, 1, 100, 150000, 280000, '/uploads/a6680008-e37c-4bfd-9117-abb5458941a0.png', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2),
                                                                                                                                                                       ('CTSP006', 2, 4, 2, 120, 150000, 280000, '/uploads/a6680008-e37c-4bfd-9117-abb5458941a0.png', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, null),
                                                                                                                                                                       ('CTSP007', 2, 4, 4, 80, 160000, 290000, '/uploads/a6680008-e37c-4bfd-9117-abb5458941a0.png', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, null);

-- Seed data for khach_hang
INSERT INTO khach_hang (ma_khach_hang, ho_ten, sdt, email, gioi_tinh, ngay_sinh, trang_thai, ngay_tao, ngay_sua) VALUES
('KH001', N'Nguyễn Văn An', '0987654321', 'an.nguyen@example.com', 1, '1990-05-15', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('KH002', N'Trần Thị Bình', '0912345678', 'binh.tran@example.com', 0, '1995-08-20', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('KH003', N'Lê Văn Cường', '0333444555', 'cuong.le@example.com', 1, '1988-11-30', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('KH004', N'Phạm Thị Dung', '0777888999', 'dung.pham@example.com', 0, '2001-02-10', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seed data for dia_chi
-- Addresses for Nguyễn Văn An (ID: 1)
INSERT INTO dia_chi (id_khach_hang, ten_nguoi_nhan, sdt_nguoi_nhan, dia_chi_cu_the, tinh_thanh_pho, quan_huyen, phuong_xa, kieu_dia_chi_la_mac_dinh, ngay_tao, ngay_sua) VALUES
(1, N'Nguyễn Văn An', '0987654321', N'Số 123, Đường Giải Phóng', N'Hà Nội', N'Hai Bà Trưng', N'Phường Đồng Tâm', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, N'Nguyễn Văn An', '0987654321', N'Tầng 10, Tòa nhà Keangnam', N'Hà Nội', N'Nam Từ Liêm', N'Phường Mễ Trì', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Addresses for Trần Thị Bình (ID: 2)
INSERT INTO dia_chi (id_khach_hang, ten_nguoi_nhan, sdt_nguoi_nhan, dia_chi_cu_the, tinh_thanh_pho, quan_huyen, phuong_xa, kieu_dia_chi_la_mac_dinh, ngay_tao, ngay_sua) VALUES
(2, N'Trần Thị Bình', '0912345678', N'456 Lê Lợi, Phường Bến Nghé', N'TP. Hồ Chí Minh', N'Quận 1', N'Phường Bến Nghé', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Addresses for Phạm Thị Dung (ID: 4)
INSERT INTO dia_chi (id_khach_hang, ten_nguoi_nhan, sdt_nguoi_nhan, dia_chi_cu_the, tinh_thanh_pho, quan_huyen, phuong_xa, kieu_dia_chi_la_mac_dinh, ngay_tao, ngay_sua) VALUES
(4, N'Phạm Thị Dung', '0777888999', N'Ký túc xá khu B, Đại học Quốc gia', N'TP. Hồ Chí Minh', N'Thủ Đức', N'Phường Linh Trung', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seed data for phieu_giam_gia
INSERT INTO phieu_giam_gia (ma_phieu_giam_gia, ten_phieu_giam_gia, loai_giam, gia_tri, gia_giam_toi_da, dieu_kien_giam, so_luong, kieu_ap_dung, ngay_bat_dau, ngay_ket_thuc, trang_thai, ngay_tao, ngay_sua) VALUES
('PGG_KM10', N'Khuyến mãi 10%', 0, 10.00, 50000.00, 200000.00, 100, 0, '2024-07-01 00:00:00', '2024-07-31 23:59:59', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PGG_GIAM20K', N'Giảm ngay 20K', 1, 20000.00, NULL, 100000.00, 200, 0, '2024-07-15 00:00:00', '2024-08-15 23:59:59', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PGG_BLACKFRIDAY', N'Black Friday Sale', 0, 50.00, 100000.00, 500000.00, 50, 0, '2023-11-24 00:00:00', '2023-11-26 23:59:59', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PGG_TRIANKH', N'Tri ân khách hàng thân thiết', 1, 100000.00, NULL, 1000000.00, 10, 1, '2024-08-01 00:00:00', '2024-08-31 23:59:59', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seed data for vai_tro
INSERT INTO vai_tro (ma, ten, trang_thai) VALUES
('ADMIN', N'Quản lý', 1),
('STAFF', N'Nhân viên', 1);

-- Seed data for nhan_vien
INSERT INTO nhan_vien (id_vai_tro, ma_nhan_vien, ho_va_ten, so_dien_thoai, email, mat_khau, cccd, gioi_tinh, ngay_sinh, dia_chi, trang_thai, ngay_vao_lam, ngay_tao, ngay_sua, anh) VALUES
(1, 'NV001', N'Trần Tuấn Linh', '0987654321', 'linhtt@beestylish.com', '$2a$10$E5.s4.2sL8.rZu3.r8E3A.A.r8E3A.A.r8E3A.A.r8E3A.A', '001098765432', 1, '1995-04-12', N'123 Nguyễn Văn Linh, Quận 7, TP.HCM', 1, '2023-01-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ''),
(2, 'NV002', N'Nguyễn Thị Hương', '0912345678', 'huongnt@beestylish.com', '$2a$10$E5.s4.2sL8.rZu3.r8E3A.A.r8E3A.A.r8E3A.A.r8E3A.A', '001098765433', 0, '1998-05-12', N'45 Lê Lợi, Quận 1, TP.HCM', 1, '2023-03-20', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ''),
(2, 'NV003', N'Lê Văn Minh', '0933445566', 'minhlv@beestylish.com', '$2a$10$E5.s4.2sL8.rZu3.r8E3A.A.r8E3A.A.r8E3A.A.r8E3A.A', '001098765434', 1, '1992-10-05', N'89 Trần Hưng Đạo, Quận 5, TP.HCM', 1, '2023-06-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ''),
(2, 'NV004', N'Phạm Văn Đức', '0977889900', 'ducpv@beestylish.com', '$2a$10$E5.s4.2sL8.rZu3.r8E3A.A.r8E3A.A.r8E3A.A.r8E3A.A', '001098765435', 1, '1997-08-25', N'22 Tôn Đức Thắng, Quận 1, TP.HCM', 0, '2022-09-10', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '');

-- =========================================================
-- Seed data for hoa_don
-- Trang thai:
-- 0: Tao moi
-- 1: Cho xac nhan
-- 2: Da xac nhan
-- 3: Dang giao
-- 4: Hoan thanh
-- 5: Da huy
--
-- Loai hoa don:
-- 0: Online
-- 1: Tai quay
-- 2: Giao hang
-- =========================================================

INSERT INTO hoa_don (
    id_nhan_vien,
    id_khach_hang,
    id_phieu_giam_gia,
    ma_hoa_don,
    loai_hoa_don,
    phi_van_chuyen,
    so_tien_goc,
    so_tien_giam,
    tong_tien_thanh_toan,
    ngay_thanh_toan,
    ngay_nhan_hang,
    trang_thai,
    ghi_chu,
    dia_chi,
    ngay_tao,
    ngay_sua,
    nguoi_tao,
    nguoi_sua
) VALUES
      (
          1,
          1,
          1,
          'HD000001',
          1,
          0,
          1260000,
          50000,
          1210000,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP,
          4,
          N'Khách mua trực tiếp tại quầy',
          N'123 Nguyễn Văn Linh, Quận 7, TP.HCM',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP,
          'system',
          'system'
      ),
      (
          2,
          2,
          2,
          'HD000002',
          0,
          30000,
          640000,
          20000,
          650000,
          NULL,
          NULL,
          1,
          N'Đơn online đang chờ xác nhận',
          N'456 Lê Lợi, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP,
          'system',
          'system'
      ),
      (
          3,
          4,
          NULL,
          'HD000003',
          2,
          25000,
          650000,
          0,
          675000,
          NULL,
          NULL,
          3,
          N'Đơn giao hàng đang vận chuyển cho khách',
          N'Ký túc xá khu B, Đại học Quốc gia, TP. Hồ Chí Minh',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP,
          'system',
          'system'
      ),
      (
          1,
          1,
          NULL,
          'HD000004',
          1,
          0,
          350000,
          0,
          350000,
          NULL,
          NULL,
          5,
          N'Khách hủy hóa đơn do đổi ý',
          N'123 Nguyễn Văn Linh, Quận 7, TP.HCM',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP,
          'system',
          'system'
      );

-- =========================================================
-- Seed data for hoa_don_chi_tiet
-- =========================================================

INSERT INTO hoa_don_chi_tiet (
    id_hoa_don,
    id_chi_tiet_san_pham,
    don_gia,
    so_luong,
    thanh_tien,
    trang_thai,
    ngay_tao,
    ngay_sua,
    nguoi_tao,
    nguoi_sua
) VALUES
      (
          1,
          1,
          350000,
          1,
          350000,
          1,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP,
          'system',
          'system'
      ),
      (
          1,
          5,
          280000,
          2,
          560000,
          1,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP,
          'system',
          'system'
      ),
      (
          1,
          2,
          350000,
          1,
          350000,
          1,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP,
          'system',
          'system'
      ),
      (
          2,
          3,
          360000,
          1,
          360000,
          1,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP,
          'system',
          'system'
      ),
      (
          2,
          6,
          280000,
          1,
          280000,
          1,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP,
          'system',
          'system'
      ),
      (
          3,
          7,
          290000,
          1,
          290000,
          1,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP,
          'system',
          'system'
      ),
      (
          3,
          4,
          360000,
          1,
          360000,
          1,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP,
          'system',
          'system'
      ),
      (
          4,
          1,
          350000,
          1,
          350000,
          0,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP,
          'system',
          'system'
      );

-- =========================================================
-- Seed data for lich_su_hoa_don
-- =========================================================

INSERT INTO lich_su_hoa_don (
    id_hoa_don,
    id_nhan_vien,
    trang_thai,
    hanh_dong,
    ghi_chu,
    nguoi_thuc_hien,
    thoi_gian,
    ngay_tao
) VALUES
      (
          1,
          1,
          0,
          N'Tạo hóa đơn',
          N'Hóa đơn được tạo tại quầy',
          N'Trần Tuấn Linh',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          1,
          1,
          2,
          N'Đã xác nhận',
          N'Nhân viên xác nhận đủ sản phẩm trong kho',
          N'Trần Tuấn Linh',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          1,
          1,
          4,
          N'Hoàn thành',
          N'Khách đã thanh toán và nhận hàng',
          N'Trần Tuấn Linh',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          2,
          2,
          0,
          N'Tạo hóa đơn',
          N'Hóa đơn online được tạo',
          N'Nguyễn Thị Hương',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          2,
          2,
          1,
          N'Chờ xác nhận',
          N'Đơn hàng đang chờ nhân viên xác nhận',
          N'Nguyễn Thị Hương',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          3,
          3,
          0,
          N'Tạo hóa đơn giao hàng',
          N'Hóa đơn giao hàng được tạo',
          N'Lê Văn Minh',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          3,
          3,
          2,
          N'Đã xác nhận',
          N'Đơn hàng đã được xác nhận',
          N'Lê Văn Minh',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          3,
          3,
          3,
          N'Đang giao',
          N'Đơn hàng đã bàn giao cho đơn vị vận chuyển',
          N'Lê Văn Minh',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          4,
          1,
          0,
          N'Tạo hóa đơn',
          N'Hóa đơn được tạo tại quầy',
          N'Trần Tuấn Linh',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          4,
          1,
          5,
          N'Đã hủy',
          N'Khách hủy hóa đơn do đổi ý',
          N'Trần Tuấn Linh',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      );
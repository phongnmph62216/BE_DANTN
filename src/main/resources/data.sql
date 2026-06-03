
-- Seed data for chat_lieu
INSERT INTO chat_lieu (ma_chat_lieu, ten_chat_lieu, trang_thai, ngay_tao, ngay_sua, nguoi_tao, nguoi_sua) VALUES
('CL001', 'Cotton', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('CL002', 'Polyester', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('CL003', 'Len', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('CL004', 'Lụa', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('CL005', 'Kaki', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL);

-- Seed data for loai_san_pham
INSERT INTO loai_san_pham (ma_loai_san_pham, ten_loai_san_pham, trang_thai, ngay_tao, ngay_sua, nguoi_tao, nguoi_sua) VALUES
('LSP001', 'Áo thun', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('LSP002', 'Áo sơ mi', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('LSP003', 'Quần jean', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('LSP004', 'Váy', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('LSP005', 'Áo khoác', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL);

-- Seed data for thuong_hieu
INSERT INTO thuong_hieu (ma_thuong_hieu, ten_thuong_hieu, trang_thai, ngay_tao, ngay_sua, nguoi_tao, nguoi_sua) VALUES
('TH001', 'Nike', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('TH002', 'Adidas', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('TH003', 'Zara', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('TH004', 'H&M', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('TH005', 'Uniqlo', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL);

-- Seed data for tay_ao
INSERT INTO tay_ao (ma_tay_ao, ten_tay_ao, trang_thai, ngay_tao, ngay_sua, nguoi_tao, nguoi_sua) VALUES
('TA001', 'Tay ngắn', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('TA002', 'Tay dài', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('TA003', 'Tay lỡ', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('TA004', 'Không tay', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('TA005', 'Tay phồng', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL);

-- Seed data for vai_ao
INSERT INTO vai_ao (ma_vai_ao, ten_vai_ao, trang_thai, ngay_tao, ngay_sua, nguoi_tao, nguoi_sua) VALUES
('VA001', 'Vai xuôi', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('VA002', 'Vai ngang', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('VA003', 'Vai raglan', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('VA004', 'Vai bồng', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('VA005', 'Vai trễ', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL);

-- Seed data for kieu_dang
INSERT INTO kieu_dang (ma_kieu_dang, ten_kieu_dang, trang_thai, ngay_tao, ngay_sua, nguoi_tao, nguoi_sua) VALUES
('KD001', 'Slim fit', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('KD002', 'Regular fit', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('KD003', 'Oversize', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('KD004', 'Bodycon', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('KD005', 'Suông', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL);

-- Seed data for co_ao
INSERT INTO co_ao (ma_co_ao, ten_co_ao, trang_thai, ngay_tao, ngay_sua, nguoi_tao, nguoi_sua) VALUES
('CA001', 'Cổ tròn', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('CA002', 'Cổ bẻ', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('CA003', 'Cổ tim', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('CA004', 'Cổ trụ', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('CA005', 'Cổ V', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL);

-- Seed data for mau_sac
INSERT INTO mau_sac (ma_mau_sac, ten_mau_sac, trang_thai, ngay_tao, ngay_sua, nguoi_tao, nguoi_sua) VALUES
('MS001', 'Đỏ', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('MS002', 'Xanh', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('MS003', 'Vàng', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('MS004', 'Trắng', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('MS005', 'Đen', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL);

-- Seed data for kich_thuoc
INSERT INTO kich_thuoc (ma_kich_thuoc, ten_kich_thuoc, trang_thai, ngay_tao, ngay_sua, nguoi_tao, nguoi_sua) VALUES
('KT001', 'S', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('KT002', 'M', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('KT003', 'L', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('KT004', 'XL', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('KT005', 'XXL', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL);

-- Seed data for xuat_su
INSERT INTO xuat_su (ma_xuat_su, ten_xuat_su, trang_thai, ngay_tao, ngay_sua, nguoi_tao, nguoi_sua) VALUES
('XS001', 'Việt Nam', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('XS002', 'Trung Quốc', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('XS003', 'Thái Lan', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('XS004', 'Hàn Quốc', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL),
('XS005', 'Nhật Bản', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL);

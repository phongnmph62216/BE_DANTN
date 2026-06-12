INSERT INTO nhan_vien
(ma_nhan_vien,ho_va_ten,so_dien_thoai,email,mat_khau,cccd,gioi_tinh,ngay_sinh,dia_chi,vai_tro,trang_thai,anh_dai_dien,ngay_vao_lam)
VALUES
    ('QL00001',N'Nguyễn Văn A','0911111111','a@gmail.com','xd123456','111111111111',1,'2000-01-01',N'Hà Nội',N'QUẢN LÝ',1,'1780742394835_kem.png','2024-01-01'),

    ('NV00002',N'Trần Thị B','0922222222','b@gmail.com','qr123456','222222222222',0,'2001-02-02',N'Hải Phòng','NHÂN VIÊN',1,'1780742394835_kem.png','2024-02-01'),

    ('QL00003',N'Lê Văn C','0933333333','c@gmail.com','yt123456','333333333333',1,'1999-03-03',N'Đà Nẵng',N'QUẢN LÝ',1,'1780742394835_kem.png','2024-03-01'),

    ('NV00004',N'Phạm Thị D','0944444444','d@gmail.com','ik123456','444444444444',0,'2002-04-04',N'Cần Thơ','NHÂN VIÊN',1,'1780742394835_kem.png','2024-04-01'),

    ('NV00005',N'Hoàng Văn E','0955555555','e@gmail.com','QG123456','555555555555',1,'1998-05-05',N'Hồ Chí Minh','NHÂN VIÊN',0,'1780742394835_kem.png','2024-05-01');

INSERT INTO ca_lam_viec
(ma_ca, ten_ca, gio_bat_dau, gio_ket_thuc, trang_thai)
VALUES
    ('CA001', N'Ca sáng', '08:00:00', '12:00:00', 1),

    ('CA002', N'Ca chiều', '13:00:00', '17:00:00', 1),

    ('CA003', N'Ca tối', '18:00:00', '22:00:00', 1);



INSERT INTO lich_lam_viec
(nhan_vien_id, ca_lam_viec_id, nguoi_tao_quan_ly_id,
 ngay_lam_viec, ghi_chu, trang_thai)
VALUES

    (2, 1, 1,'2026-06-12',N'Ca sáng bán hàng',0),

    (4, 2, 1,'2026-06-13',N'Ca chiều bán hàng',1);






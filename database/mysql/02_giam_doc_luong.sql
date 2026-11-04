-- =====================================================================
-- NÂNG CẤP CSDL ĐÃ CÓ DỮ LIỆU  →  thêm vai trò Giám đốc + cột lương
-- Dùng khi bạn đã có CSDL QuanLyNhanSu và KHÔNG muốn tạo lại từ đầu.
--
-- CHỈ CHẠY MỘT LẦN.
--   java -cp "out;lib/*" tools\ChayScriptSQL.java database/mysql/02_giam_doc_luong.sql
--
-- (Nếu tạo CSDL mới thì KHÔNG cần file này — dùng QuanLyNhanSu.sql là đủ.)
-- =====================================================================

USE QuanLyNhanSu;

-- 1) Thêm cột lương và gán lương ban đầu theo chức vụ
ALTER TABLE NHANVIEN ADD COLUMN Luong DECIMAL(12,2) NOT NULL DEFAULT 0;

UPDATE NHANVIEN SET Luong = 20000000 WHERE MaCV = 'CV01' AND Luong = 0;
UPDATE NHANVIEN SET Luong = 12000000 WHERE MaCV = 'CV02' AND Luong = 0;
UPDATE NHANVIEN SET Luong =  6000000 WHERE MaCV = 'CV03' AND Luong = 0;

-- 2) Cho phép vai trò 'GiamDoc' trong bảng tài khoản
ALTER TABLE TAIKHOAN DROP CHECK CK_TK_VAI_TRO;

ALTER TABLE TAIKHOAN ADD CONSTRAINT CK_TK_VAI_TRO
    CHECK (VaiTro IN ('GiamDoc', 'QuanLy', 'NhanVien'));

-- 3) Phòng ban và chức vụ cho ban giám đốc
INSERT INTO PHONGBAN (MaPB, TenPB, MoTa) VALUES
    ('PB04', 'Ban Giám đốc', 'Điều hành toàn công ty');

INSERT INTO CHUCVU (MaCV, TenCV, MoTa) VALUES
    ('CV04', 'Giám đốc', 'Quản lý toàn bộ quản lý và nhân viên');

-- 4) Ba giám đốc
INSERT INTO NHANVIEN (MaNV, HoTen, NgaySinh, GioiTinh, CCCD, SoDienThoai, Email,
                      DiaChi, NgayVaoLam, MaPB, MaCV, MaQuanLy, TrangThai, Luong) VALUES
    ('NV008', 'Nguyễn Minh Quân', '1980-02-15', 'Nam', '052206005765', '0353942865',
     'quan.nm@congty.vn', 'Tăng Nhơn Phú, Thủ Đức, HCM', '2010-01-05',
     'PB04', 'CV04', NULL, 'Đang làm', 30000000),

    ('NV009', 'Trần Văn Nhân',  '1982-06-20', 'Nam', '052206005766', '0353942866',
     'nhan.tv@congty.vn', 'Tăng Nhơn Phú, Thủ Đức, HCM', '2011-03-10',
     'PB04', 'CV04', NULL, 'Đang làm', 30000000),

    ('NV010', 'Đinh Viết Thiên', '1983-09-12', 'Nam', '052206005767', '0353942867',
     'thien.dv@congty.vn', 'Dĩ An, Bình Dương', '2012-07-01',
     'PB04', 'CV04', NULL, 'Đang làm', 30000000);

-- 5) Tài khoản đăng nhập cho 3 giám đốc (mật khẩu: 123456)
INSERT INTO TAIKHOAN (TenDangNhap, MatKhau, VaiTro, MaNV, TrangThai) VALUES
    ('gd.quan',  '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92',
     'GiamDoc', 'NV008', 'Hoạt động'),
    ('gd.nhan',  '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92',
     'GiamDoc', 'NV009', 'Hoạt động'),
    ('gd.thien', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92',
     'GiamDoc', 'NV010', 'Hoạt động');

SELECT tk.TenDangNhap, tk.VaiTro, nv.HoTen, nv.Luong
FROM TAIKHOAN tk
JOIN NHANVIEN nv ON nv.MaNV = tk.MaNV
ORDER BY tk.VaiTro, tk.TenDangNhap;

-- =====================================================================
-- ĐỒ ÁN: QUẢN LÝ NHÂN SỰ & CHẤM CÔNG 
-- =====================================================================

DROP DATABASE IF EXISTS QuanLyNhanSu;
CREATE DATABASE QuanLyNhanSu CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE QuanLyNhanSu;

/* ------------------------------- PHÒNG BAN ------------------------------- */
CREATE TABLE PHONGBAN (
    MaPB  VARCHAR(10)   NOT NULL PRIMARY KEY,
    TenPB VARCHAR(100)  NOT NULL UNIQUE,
    MoTa  VARCHAR(255)  NULL
);

/* -------------------------------- CHỨC VỤ ------------------------------- */
CREATE TABLE CHUCVU (
    MaCV  VARCHAR(10)   NOT NULL PRIMARY KEY,
    TenCV VARCHAR(100)  NOT NULL UNIQUE,
    MoTa  VARCHAR(255)  NULL
);

/* ------------------------------ CA LÀM VIỆC ----------------------------- */
CREATE TABLE CALAMVIEC (
    MaCa       VARCHAR(10) NOT NULL PRIMARY KEY,
    TenCa      VARCHAR(50) NOT NULL,
    GioBatDau  TIME        NOT NULL,
    GioKetThuc TIME        NOT NULL
);

/* ------------------------------- NHÂN VIÊN ------------------------------ */
CREATE TABLE NHANVIEN (
    MaNV        VARCHAR(10)   NOT NULL PRIMARY KEY,
    HoTen       VARCHAR(100)  NOT NULL,
    NgaySinh    DATE          NULL,
    GioiTinh    VARCHAR(10)   NULL,
    CCCD        VARCHAR(20)   NULL UNIQUE,
    SoDienThoai VARCHAR(15)   NULL,
    Email       VARCHAR(100)  NULL UNIQUE,
    DiaChi      VARCHAR(255)  NULL,
    NgayVaoLam  DATE          NULL,
    MaPB        VARCHAR(10)   NULL,
    MaCV        VARCHAR(10)   NULL,
    MaQuanLy    VARCHAR(10)   NULL,
    TrangThai   VARCHAR(20)   NOT NULL DEFAULT 'Đang làm',
    Luong       DECIMAL(12,2) NOT NULL DEFAULT 0,

    CONSTRAINT FK_NV_PHONGBAN FOREIGN KEY (MaPB) REFERENCES PHONGBAN(MaPB),
    CONSTRAINT FK_NV_CHUCVU   FOREIGN KEY (MaCV) REFERENCES CHUCVU(MaCV),
    CONSTRAINT FK_NV_QUANLY   FOREIGN KEY (MaQuanLy) REFERENCES NHANVIEN(MaNV),
    CONSTRAINT CK_NV_TRANGTHAI
        CHECK (TrangThai IN ('Đang làm', 'Tạm nghỉ', 'Đã nghỉ'))
);

/* ------------------------------- TÀI KHOẢN ------------------------------ */
CREATE TABLE TAIKHOAN (
    TenDangNhap VARCHAR(50)  NOT NULL PRIMARY KEY,
    MatKhau     VARCHAR(64)  NOT NULL,            -- SHA-256 dạng hex
    VaiTro      VARCHAR(20)  NOT NULL,            -- 'GiamDoc', 'QuanLy' hoặc 'NhanVien'
    MaNV        VARCHAR(10)  NOT NULL UNIQUE,     -- mỗi nhân viên 1 tài khoản
    TrangThai   VARCHAR(20)  NOT NULL DEFAULT 'Hoạt động',

    CONSTRAINT FK_TK_NHANVIEN FOREIGN KEY (MaNV) REFERENCES NHANVIEN(MaNV),
    CONSTRAINT CK_TK_VAI_TRO  CHECK (VaiTro IN ('GiamDoc', 'QuanLy', 'NhanVien'))
);

/* ------------------------------- CHẤM CÔNG ----------------------------- */
CREATE TABLE CHAMCONG (
    MaChamCong   INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    MaNV         VARCHAR(10)   NOT NULL,
    NgayChamCong DATE          NOT NULL,
    MaCa         VARCHAR(10)   NULL,
    GioVao       TIME          NULL,
    GioRa        TIME          NULL,
    TrangThai    VARCHAR(30)   NULL,
    GhiChu       VARCHAR(255)  NULL,

    CONSTRAINT FK_CC_NHANVIEN  FOREIGN KEY (MaNV) REFERENCES NHANVIEN(MaNV),
    CONSTRAINT FK_CC_CALAMVIEC FOREIGN KEY (MaCa) REFERENCES CALAMVIEC(MaCa),
    CONSTRAINT UQ_CC_NGAY      UNIQUE (MaNV, NgayChamCong)
);

/* ------------------------------- BẢNG LƯƠNG----------------------------- */
CREATE TABLE BANGLUONG (
    MaBangLuong   INT AUTO_INCREMENT PRIMARY KEY,
    MaNV          VARCHAR(10)   NOT NULL,
    Thang         INT           NOT NULL,
    Nam           INT           NOT NULL,
    LuongCoBan    DECIMAL(12,2) NOT NULL,
    LuongCong     DECIMAL(12,2) NOT NULL,
    NgayCongChuan INT           NOT NULL,
    NgayCongThucTe INT          NOT NULL,
    SoLanDiMuon   INT           NOT NULL,
    TienPhat      DECIMAL(12,2) NOT NULL,
    LuongThucLinh DECIMAL(12,2) NOT NULL,
    NgayChot      DATE          NOT NULL,
    CONSTRAINT FK_BL_NHANVIEN FOREIGN KEY (MaNV) REFERENCES NHANVIEN(MaNV),
    CONSTRAINT UQ_BL_THANG    UNIQUE (MaNV, Thang, Nam)
);


/* ========================= DỮ LIỆU MẪU ========================= */

-- 4 phòng ban: 3 phòng nghiệp vụ + Ban Giám đốc
INSERT INTO PHONGBAN (MaPB, TenPB, MoTa) VALUES
    ('PB01', 'Phòng Kỹ thuật',            'Phát triển và vận hành hệ thống phần mềm'),
    ('PB02', 'Phòng Hành chính - Nhân sự', 'Quản trị nhân sự và hành chính'),
    ('PB03', 'Phòng Kế toán',              'Hạch toán, lương và tài chính'),
    ('PB04', 'Ban Giám đốc',               'Điều hành toàn công ty');

INSERT INTO CHUCVU (MaCV, TenCV, MoTa) VALUES
    ('CV00', 'Giám đốc',      'Quản lý toàn bộ quản lý và nhân viên'),
    ('CV01', 'Trưởng phòng',  'Quản lý nhân viên trong phòng'),
    ('CV02', 'Nhân viên',     'Nhân viên chính thức');

INSERT INTO CALAMVIEC (MaCa, TenCa, GioBatDau, GioKetThuc) VALUES
    ('CA01', 'Ca hành chính',  '08:00:00', '17:00:00'),
    ('CA02', 'Ca chiều - tối', '13:00:00', '21:00:00');

-- Nhân viên: 3 giám đốc + 3 trưởng phòng + 15 nhân viên
INSERT INTO NHANVIEN (MaNV, HoTen, NgaySinh, GioiTinh, CCCD, SoDienThoai, Email,
                      DiaChi, NgayVaoLam, MaPB, MaCV, MaQuanLy, TrangThai, Luong) VALUES
    ('NV008', 'Nguyễn Minh Quân', '1980-02-15', 'Nam', '079000000001', '09120000007', 'gd.quan@congty.vn',
     'Tăng Nhơn Phú, Thủ Đức, HCM', '2010-01-05', 'PB04', 'CV00', NULL, 'Đang làm', 30000000),
    ('NV009', 'Trần Văn Nhân', '1982-06-20', 'Nam', '079000000002', '09120000014', 'gd.nhan@congty.vn',
     'Tăng Nhơn Phú, Thủ Đức, HCM', '2011-03-10', 'PB04', 'CV00', NULL, 'Đang làm', 30000000),
    ('NV010', 'Đinh Viết Thiên', '1983-09-12', 'Nam', '079000000003', '09120000021', 'gd.thien@congty.vn',
     'Dĩ An, Bình Dương', '2012-07-01', 'PB04', 'CV00', NULL, 'Đang làm', 30000000),
    ('NV001', 'Bạch Quốc Thoại', '1985-03-11', 'Nam', '079000000004', '09120000028', 'thoai.bq@congty.vn',
     '12 Lê Lợi, Quận 1, TP.HCM', '2014-02-10', 'PB02', 'CV01', 'NV008', 'Đang làm', 20000000),
    ('NV002', 'Lê Nguyễn Nhật Quang', '1986-07-22', 'Nam', '079000000005', '09120000035', 'quang.lnn@congty.vn',
     '45 Nguyễn Trãi, Quận 5, TP.HCM', '2015-05-04', 'PB03', 'CV01', 'NV009', 'Đang làm', 20000000),
    ('NV003', 'Hồ Gia Nguyễn', '1987-11-30', 'Nam', '079000000006', '09120000042', 'nguyen.hg@congty.vn',
     '78 Cách Mạng Tháng 8, Quận 3, TP.HCM', '2016-08-15', 'PB01', 'CV01', 'NV010', 'Đang làm', 20000000),
    ('NV011', 'Nguyễn Văn A', '1995-01-01', 'Nam', '079000000007', '09120000049', 'nva@congty.vn',
     '23 Điện Biên Phủ, Bình Thạnh, TP.HCM', '2020-03-02', 'PB02', 'CV02', 'NV001', 'Đang làm', 12000000),
    ('NV012', 'Nguyễn Văn B', '1996-02-02', 'Nam', '079000000008', '09120000056', 'nvb@congty.vn',
     '56 Trường Chinh, Tân Bình, TP.HCM', '2021-04-05', 'PB02', 'CV02', 'NV001', 'Đang làm', 12000000),
    ('NV013', 'Nguyễn Văn C', '1997-03-03', 'Nam', '079000000009', '09120000063', 'nvc@congty.vn',
     '12 Nguyễn Thị Minh Khai, Quận 1, TP.HCM', '2022-05-09', 'PB02', 'CV02', 'NV001', 'Đang làm', 12000000),
    ('NV014', 'Nguyễn Văn D', '1998-04-04', 'Nam', '079000000010', '09120000070', 'nvd@congty.vn',
     '89 Lý Thường Kiệt, Quận 10, TP.HCM', '2023-06-12', 'PB02', 'CV02', 'NV001', 'Đang làm', 12000000),
    ('NV015', 'Trần Văn A', '1995-05-05', 'Nam', '079000000011', '09120000077', 'tva@congty.vn',
     '101 Võ Văn Tần, Quận 3, TP.HCM', '2020-07-06', 'PB03', 'CV02', 'NV002', 'Đang làm', 12000000),
    ('NV016', 'Trần Văn B', '1996-06-06', 'Nam', '079000000012', '09120000084', 'tvb@congty.vn',
     '33 Pasteur, Quận 1, TP.HCM', '2021-08-02', 'PB03', 'CV02', 'NV002', 'Đang làm', 12000000),
    ('NV017', 'Trần Văn C', '1997-07-07', 'Nam', '079000000013', '09120000091', 'tvc@congty.vn',
     '67 Nguyễn Du, Quận 1, TP.HCM', '2022-09-05', 'PB03', 'CV02', 'NV002', 'Đang làm', 12000000),
    ('NV018', 'Trần Văn D', '1998-08-08', 'Nam', '079000000014', '09120000098', 'tvd@congty.vn',
     '90 Bà Huyện Thanh Quan, Quận 3, TP.HCM', '2023-10-02', 'PB03', 'CV02', 'NV002', 'Đang làm', 12000000),
    ('NV019', 'Trần Văn E', '1999-09-09', 'Nam', '079000000015', '09120000105', 'tve@congty.vn',
     '15 Hai Bà Trưng, Quận 1, TP.HCM', '2024-01-08', 'PB03', 'CV02', 'NV002', 'Đang làm', 12000000),
    ('NV020', 'Lê Văn A', '1995-10-10', 'Nam', '079000000016', '09120000112', 'lva@congty.vn',
     '22 Đinh Tiên Hoàng, Bình Thạnh, TP.HCM', '2020-11-02', 'PB01', 'CV02', 'NV003', 'Đang làm', 12000000),
    ('NV021', 'Lê Văn B', '1996-11-11', 'Nam', '079000000017', '09120000119', 'lvb@congty.vn',
     '44 Xô Viết Nghệ Tĩnh, Bình Thạnh, TP.HCM', '2021-12-06', 'PB01', 'CV02', 'NV003', 'Đang làm', 12000000),
    ('NV022', 'Lê Văn C', '1997-12-12', 'Nam', '079000000018', '09120000126', 'lvc@congty.vn',
     '8 Phan Đăng Lưu, Phú Nhuận, TP.HCM', '2022-02-07', 'PB01', 'CV02', 'NV003', 'Đang làm', 12000000),
    ('NV023', 'Lê Văn D', '1998-01-13', 'Nam', '079000000019', '09120000133', 'lvd@congty.vn',
     '71 Hoàng Sa, Quận 1, TP.HCM', '2023-03-06', 'PB01', 'CV02', 'NV003', 'Đang làm', 12000000),
    ('NV024', 'Lê Văn E', '1999-02-14', 'Nam', '079000000020', '09120000140', 'lve@congty.vn',
     '5 Nguyễn Hữu Cảnh, Bình Thạnh, TP.HCM', '2024-04-01', 'PB01', 'CV02', 'NV003', 'Đang làm', 12000000),
    ('NV025', 'Lê Văn F', '2000-03-15', 'Nam', '079000000021', '09120000147', 'lvf@congty.vn',
     '92 Nguyễn Văn Linh, Quận 7, TP.HCM', '2025-05-05', 'PB01', 'CV02', 'NV003', 'Đang làm', 12000000);

-- Mật khẩu của mọi tài khoản: 123456 (mã băm SHA-256)
SET @MatKhau123456 = '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92';

INSERT INTO TAIKHOAN (TenDangNhap, MatKhau, VaiTro, MaNV, TrangThai) VALUES
    ('gd.quan', @MatKhau123456, 'GiamDoc', 'NV008', 'Hoạt động'),
    ('gd.nhan', @MatKhau123456, 'GiamDoc', 'NV009', 'Hoạt động'),
    ('gd.thien', @MatKhau123456, 'GiamDoc', 'NV010', 'Hoạt động'),
    ('thoai.bq', @MatKhau123456, 'QuanLy', 'NV001', 'Hoạt động'),
    ('quang.lnn', @MatKhau123456, 'QuanLy', 'NV002', 'Hoạt động'),
    ('nguyen.hg', @MatKhau123456, 'QuanLy', 'NV003', 'Hoạt động'),
    ('nva', @MatKhau123456, 'NhanVien', 'NV011', 'Hoạt động'),
    ('nvb', @MatKhau123456, 'NhanVien', 'NV012', 'Hoạt động'),
    ('nvc', @MatKhau123456, 'NhanVien', 'NV013', 'Hoạt động'),
    ('nvd', @MatKhau123456, 'NhanVien', 'NV014', 'Hoạt động'),
    ('tva', @MatKhau123456, 'NhanVien', 'NV015', 'Hoạt động'),
    ('tvb', @MatKhau123456, 'NhanVien', 'NV016', 'Hoạt động'),
    ('tvc', @MatKhau123456, 'NhanVien', 'NV017', 'Hoạt động'),
    ('tvd', @MatKhau123456, 'NhanVien', 'NV018', 'Hoạt động'),
    ('tve', @MatKhau123456, 'NhanVien', 'NV019', 'Hoạt động'),
    ('lva', @MatKhau123456, 'NhanVien', 'NV020', 'Hoạt động'),
    ('lvb', @MatKhau123456, 'NhanVien', 'NV021', 'Hoạt động'),
    ('lvc', @MatKhau123456, 'NhanVien', 'NV022', 'Hoạt động'),
    ('lvd', @MatKhau123456, 'NhanVien', 'NV023', 'Hoạt động'),
    ('lve', @MatKhau123456, 'NhanVien', 'NV024', 'Hoạt động'),
    ('lvf', @MatKhau123456, 'NhanVien', 'NV025', 'Hoạt động');

/* ========================= KIỂM TRA NHANH ========================= */
SELECT 'PHÒNG BAN' AS Bảng, COUNT(*) AS SốDòng FROM PHONGBAN
UNION ALL SELECT 'CHỨC VỤ',      COUNT(*) FROM CHUCVU
UNION ALL SELECT 'CA LÀM VIỆC',  COUNT(*) FROM CALAMVIEC
UNION ALL SELECT 'NHÂN VIÊN',    COUNT(*) FROM NHANVIEN
UNION ALL SELECT 'TÀI KHOẢN',    COUNT(*) FROM TAIKHOAN
UNION ALL SELECT 'CHẤM CÔNG',    COUNT(*) FROM CHAMCONG;

SELECT nv.MaNV, nv.HoTen, cv.TenCV, pb.TenPB, nv.MaQuanLy, tk.TenDangNhap, tk.VaiTro, nv.Luong
FROM NHANVIEN nv
LEFT JOIN PHONGBAN pb ON pb.MaPB = nv.MaPB
LEFT JOIN CHUCVU   cv ON cv.MaCV = nv.MaCV
LEFT JOIN TAIKHOAN tk ON tk.MaNV = nv.MaNV
ORDER BY nv.MaNV;

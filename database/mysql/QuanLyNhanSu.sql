-- =====================================================================
-- ĐỒ ÁN: QUẢN LÝ NHÂN SỰ & CHẤM CÔNG (Dành cho MySQL / MariaDB)
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

/* ========================= DỮ LIỆU MẪU ========================= */

INSERT INTO PHONGBAN (MaPB, TenPB, MoTa) VALUES
    ('PB01', 'Phòng Kỹ thuật',         'Phát triển và vận hành hệ thống phần mềm'),
    ('PB02', 'Phòng Kinh doanh',       'Kinh doanh và chăm sóc khách hàng'),
    ('PB03', 'Phòng Hành chính - Nhân sự', 'Quản trị nhân sự và hành chính'),
    ('PB04', 'Ban Giám đốc',           'Điều hành toàn công ty');

INSERT INTO CHUCVU (MaCV, TenCV, MoTa) VALUES
    ('CV01', 'Trưởng phòng',   'Quản lý toàn bộ nhân viên trong phòng'),
    ('CV02', 'Nhân viên',      'Nhân viên chính thức'),
    ('CV03', 'Thực tập sinh',  'Nhân viên đang thực tập'),
    ('CV04', 'Giám đốc',       'Quản lý toàn bộ quản lý và nhân viên');

INSERT INTO CALAMVIEC (MaCa, TenCa, GioBatDau, GioKetThuc) VALUES
    ('CA01', 'Ca hành chính',   '08:00:00', '17:00:00'),
    ('CA02', 'Ca chiều - tối',  '13:00:00', '21:00:00');

INSERT INTO NHANVIEN (MaNV, HoTen, NgaySinh, GioiTinh, CCCD, SoDienThoai, Email,
                      DiaChi, NgayVaoLam, MaPB, MaCV, MaQuanLy, TrangThai, Luong) VALUES
    ('NV001', 'Trần Văn Quản',  '1985-04-12', 'Nam', '079085001111', '0901111222',
     'quan.tv@congty.vn', '12 Lê Lợi, Quận 1, TP.HCM', '2015-03-01',
     'PB01', 'CV01', NULL, 'Đang làm', 20000000),

    ('NV002', 'Nguyễn Thị An',  '1998-03-12', 'Nữ',  '079098002222', '0912222333',
     'an.nv@congty.vn', '45 Nguyễn Trãi, Quận 5, TP.HCM', '2021-05-04',
     'PB01', 'CV02', 'NV001', 'Đang làm', 12000000),

    ('NV003', 'Trần Thị Bình',  '1995-07-25', 'Nữ',  '079095003333', '0933333444',
     'binh.tt@congty.vn', '78 Cách Mạng Tháng 8, Quận 3, TP.HCM', '2019-09-16',
     'PB01', 'CV02', 'NV001', 'Đang làm', 12000000),

    ('NV004', 'Lê Hoàng Cường', '2000-01-08', 'Nam', '079000004444', '0944444555',
     'cuong.lh@congty.vn', '23 Điện Biên Phủ, Bình Thạnh, TP.HCM', '2023-02-01',
     'PB01', 'CV03', 'NV001', 'Đang làm', 6000000),

    ('NV005', 'Phạm Thu Dung',  '1997-11-30', 'Nữ',  '079097005555', '0955555666',
     'dung.pt@congty.vn', '56 Trường Chinh, Tân Bình, TP.HCM', '2022-07-11',
     'PB01', 'CV02', 'NV001', 'Tạm nghỉ', 12000000),

    ('NV008', 'Nguyễn Minh Quân', '1980-02-15', 'Nam', '052206005765', '0353942865',
     'quan.nm@congty.vn', 'Tăng Nhơn Phú, Thủ Đức, HCM', '2010-01-05',
     'PB04', 'CV04', NULL, 'Đang làm', 30000000),

    ('NV009', 'Trần Văn Nhân',  '1982-06-20', 'Nam', '052206005766', '0353942866',
     'nhan.tv@congty.vn', 'Tăng Nhơn Phú, Thủ Đức, HCM', '2011-03-10',
     'PB04', 'CV04', NULL, 'Đang làm', 30000000),

    ('NV010', 'Đinh Viết Thiên', '1983-09-12', 'Nam', '052206005767', '0353942867',
     'thien.dv@congty.vn', 'Dĩ An, Bình Dương', '2012-07-01',
     'PB04', 'CV04', NULL, 'Đang làm', 30000000);

-- Mật khẩu của mọi tài khoản mẫu: 123456 (Mã băm SHA-256)
SET @MatKhau123456 = '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92';

INSERT INTO TAIKHOAN (TenDangNhap, MatKhau, VaiTro, MaNV, TrangThai) VALUES
    ('quanly',   @MatKhau123456, 'QuanLy',   'NV001', 'Hoạt động'),
    ('an.nv',    @MatKhau123456, 'NhanVien', 'NV002', 'Hoạt động'),
    ('binh.tt',  @MatKhau123456, 'NhanVien', 'NV003', 'Hoạt động'),
    ('cuong.lh', @MatKhau123456, 'NhanVien', 'NV004', 'Hoạt động'),
    ('gd.quan',  @MatKhau123456, 'GiamDoc',  'NV008', 'Hoạt động'),
    ('gd.nhan',  @MatKhau123456, 'GiamDoc',  'NV009', 'Hoạt động'),
    ('gd.thien', @MatKhau123456, 'GiamDoc',  'NV010', 'Hoạt động');

-- Dữ liệu chấm công mẫu
INSERT INTO CHAMCONG (MaNV, NgayChamCong, MaCa, GioVao, GioRa, TrangThai, GhiChu) VALUES
    ('NV002', DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'CA01', '07:55:00', '17:03:00', 'Đủ ca',            'Đúng giờ'),
    ('NV003', DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'CA01', '08:20:00', '16:40:00', 'Đi muộn, về sớm',  'Có việc riêng buổi chiều'),
    ('NV004', DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'CA01', NULL,        NULL,        'Vắng mặt',         'Chưa chấm công'),
    ('NV002', DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'CA01', '08:02:00', '17:00:00', 'Đủ ca',            NULL),
    ('NV005', DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'CA02', '13:05:00', '21:00:00', 'Đủ ca',            NULL),
    ('NV001', DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'CA01', '07:50:00', '17:30:00', 'Đủ ca',            'Họp đến muộn');

/* ========================= KIỂM TRA NHANH ========================= */
SELECT CONCAT('Số phòng ban : ', COUNT(*)) AS ThongKe FROM PHONGBAN
UNION ALL SELECT CONCAT('Số nhân viên : ', COUNT(*)) FROM NHANVIEN
UNION ALL SELECT CONCAT('Số tài khoản : ', COUNT(*)) FROM TAIKHOAN
UNION ALL SELECT CONCAT('Số bản ghi chấm công: ', COUNT(*)) FROM CHAMCONG;

-- Xem danh sách nhân viên kèm phòng ban, chức vụ và tài khoản
SELECT nv.MaNV, nv.HoTen, nv.MaQuanLy, pb.TenPB, cv.TenCV, tk.TenDangNhap, tk.VaiTro
FROM NHANVIEN nv
LEFT JOIN PHONGBAN pb ON pb.MaPB = nv.MaPB
LEFT JOIN CHUCVU   cv ON cv.MaCV = nv.MaCV
LEFT JOIN TAIKHOAN tk ON tk.MaNV = nv.MaNV
ORDER BY nv.MaNV;
/* =====================================================================
   ĐỒ ÁN: QUẢN LÝ NHÂN SỰ & CHẤM CÔNG
   Hệ quản trị CSDL: Microsoft SQL Server 2016 trở lên
   ---------------------------------------------------------------------
   Cách chạy (BẮT BUỘC có -f 65001 để không lỗi dấu tiếng Việt):
     sqlcmd -S localhost -U sa -P <mat-khau> -C -f 65001 -i database\QuanLyNhanSu.sql
   hoặc mở bằng SSMS rồi Execute.
   ---------------------------------------------------------------------
   Tài khoản mẫu - mật khẩu của tất cả đều là: 123456
   (cột MatKhau lưu SHA-256 của mật khẩu, xem util.MatKhau):
     quanly    / 123456  -> Quản lý   (Trần Văn Quản - NV001)
     an.nv     / 123456  -> Nhân viên (Nguyễn Thị An  - NV002)
     binh.tt   / 123456  -> Nhân viên (Trần Thị Bình  - NV003)
     cuong.lh  / 123456  -> Nhân viên (Lê Hoàng Cường - NV004)
   ===================================================================== */

IF DB_ID(N'QuanLyNhanSu') IS NOT NULL
BEGIN
    ALTER DATABASE QuanLyNhanSu SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE QuanLyNhanSu;
END
GO

CREATE DATABASE QuanLyNhanSu;
GO

USE QuanLyNhanSu;
GO

/* ------------------------------- PHÒNG BAN ------------------------------- */
CREATE TABLE PHONGBAN (
    MaPB  VARCHAR(10)   NOT NULL PRIMARY KEY,
    TenPB NVARCHAR(100) NOT NULL UNIQUE,
    MoTa  NVARCHAR(255) NULL
);

/* -------------------------------- CHỨC VỤ ------------------------------- */
CREATE TABLE CHUCVU (
    MaCV  VARCHAR(10)   NOT NULL PRIMARY KEY,
    TenCV NVARCHAR(100) NOT NULL UNIQUE,
    MoTa  NVARCHAR(255) NULL
);

/* ------------------------------ CA LÀM VIỆC ----------------------------- */
CREATE TABLE CALAMVIEC (
    MaCa       VARCHAR(10) NOT NULL PRIMARY KEY,
    TenCa      NVARCHAR(50) NOT NULL,
    GioBatDau  TIME(0)     NOT NULL,
    GioKetThuc TIME(0)     NOT NULL
);

/* ------------------------------- NHÂN VIÊN ------------------------------ */
CREATE TABLE NHANVIEN (
    MaNV        VARCHAR(10)   NOT NULL PRIMARY KEY,
    HoTen       NVARCHAR(100) NOT NULL,
    NgaySinh    DATE          NULL,
    GioiTinh    NVARCHAR(10)  NULL,
    CCCD        VARCHAR(20)   NULL UNIQUE,
    SoDienThoai VARCHAR(15)   NULL,
    Email       VARCHAR(100)  NULL UNIQUE,
    DiaChi      NVARCHAR(255) NULL,
    NgayVaoLam  DATE          NULL,
    MaPB        VARCHAR(10)   NULL,
    MaCV        VARCHAR(10)   NULL,
    -- Cấp trên trực tiếp: dùng để xác định "nhân viên cấp dưới" của quản lý
    MaQuanLy    VARCHAR(10)   NULL,
    TrangThai   NVARCHAR(20)  NOT NULL
        CONSTRAINT DF_NV_TRANGTHAI DEFAULT N'Đang làm',
    Luong       DECIMAL(12,2) NOT NULL
        CONSTRAINT DF_NV_LUONG DEFAULT 0,

    CONSTRAINT FK_NV_PHONGBAN FOREIGN KEY (MaPB) REFERENCES PHONGBAN(MaPB),
    CONSTRAINT FK_NV_CHUCVU   FOREIGN KEY (MaCV) REFERENCES CHUCVU(MaCV),
    CONSTRAINT FK_NV_QUANLY   FOREIGN KEY (MaQuanLy) REFERENCES NHANVIEN(MaNV),
    CONSTRAINT CK_NV_TRANGTHAI
        CHECK (TrangThai IN (N'Đang làm', N'Tạm nghỉ', N'Đã nghỉ'))
);

/* ------------------------------- TÀI KHOẢN ------------------------------ */
CREATE TABLE TAIKHOAN (
    TenDangNhap VARCHAR(50)  NOT NULL PRIMARY KEY,
    MatKhau     VARCHAR(64)  NOT NULL,           -- SHA-256 dạng hex
    VaiTro      VARCHAR(20)  NOT NULL,           -- 'GiamDoc', 'QuanLy' hoặc 'NhanVien'
    MaNV        VARCHAR(10)  NOT NULL UNIQUE,    -- mỗi nhân viên 1 tài khoản
    TrangThai   NVARCHAR(20) NOT NULL
        CONSTRAINT DF_TK_TRANGTHAI DEFAULT N'Hoạt động',

    CONSTRAINT FK_TK_NHANVIEN FOREIGN KEY (MaNV) REFERENCES NHANVIEN(MaNV),
    CONSTRAINT CK_TK_VAI_TRO  CHECK (VaiTro IN ('GiamDoc', 'QuanLy', 'NhanVien'))
);

/* ------------------------------- CHẤM CÔNG ----------------------------- */
CREATE TABLE CHAMCONG (
    MaChamCong   INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    MaNV         VARCHAR(10)   NOT NULL,
    NgayChamCong DATE          NOT NULL,
    MaCa         VARCHAR(10)   NULL,
    GioVao       TIME(0)       NULL,
    GioRa        TIME(0)       NULL,
    TrangThai    NVARCHAR(30)  NULL,
    GhiChu       NVARCHAR(255) NULL,

    CONSTRAINT FK_CC_NHANVIEN  FOREIGN KEY (MaNV) REFERENCES NHANVIEN(MaNV),
    CONSTRAINT FK_CC_CALAMVIEC FOREIGN KEY (MaCa) REFERENCES CALAMVIEC(MaCa),
    -- Mỗi nhân viên chỉ có tối đa 1 bản ghi chấm công trong 1 ngày
    CONSTRAINT UQ_CC_NGAY      UNIQUE (MaNV, NgayChamCong)
);
GO

/* ========================= DỮ LIỆU MẪU ========================= */

INSERT INTO PHONGBAN (MaPB, TenPB, MoTa) VALUES
    ('PB01', N'Phòng Kỹ thuật',        N'Phát triển và vận hành hệ thống phần mềm'),
    ('PB02', N'Phòng Kinh doanh',      N'Kinh doanh và chăm sóc khách hàng'),
    ('PB03', N'Phòng Hành chính - Nhân sự', N'Quản trị nhân sự và hành chính'),
    ('PB04', N'Ban Giám đốc',          N'Điều hành toàn công ty');

INSERT INTO CHUCVU (MaCV, TenCV, MoTa) VALUES
    ('CV00', N'Giám đốc',       N'Quản lý toàn bộ quản lý và nhân viên'),
    ('CV01', N'Trưởng phòng',   N'Quản lý toàn bộ nhân viên trong phòng'),
    ('CV02', N'Nhân viên',      N'Nhân viên chính thức');

INSERT INTO CALAMVIEC (MaCa, TenCa, GioBatDau, GioKetThuc) VALUES
    ('CA01', N'Ca hành chính',   '08:00', '17:00'),
    ('CA02', N'Ca chiều - tối',  '13:00', '21:00');

-- NV001 là quản lý (không có cấp trên), NV002..NV005 là cấp dưới của NV001
-- NV008..NV010 là ban giám đốc, quản lý toàn bộ quản lý và nhân viên
INSERT INTO NHANVIEN (MaNV, HoTen, NgaySinh, GioiTinh, CCCD, SoDienThoai, Email,
                      DiaChi, NgayVaoLam, MaPB, MaCV, MaQuanLy, TrangThai, Luong) VALUES
    ('NV001', N'Trần Văn Quản',  '1985-04-12', N'Nam', '079085001111', '0901111222',
     'quan.tv@congty.vn', N'12 Lê Lợi, Quận 1, TP.HCM', '2015-03-01',
     'PB01', 'CV01', NULL, N'Đang làm', 20000000),

    ('NV002', N'Nguyễn Thị An',  '1998-03-12', N'Nữ',  '079098002222', '0912222333',
     'an.nv@congty.vn', N'45 Nguyễn Trãi, Quận 5, TP.HCM', '2021-05-04',
     'PB01', 'CV02', 'NV001', N'Đang làm', 12000000),

    ('NV003', N'Trần Thị Bình',  '1995-07-25', N'Nữ',  '079095003333', '0933333444',
     'binh.tt@congty.vn', N'78 Cách Mạng Tháng 8, Quận 3, TP.HCM', '2019-09-16',
     'PB01', 'CV02', 'NV001', N'Đang làm', 12000000),

    ('NV004', N'Lê Hoàng Cường', '2000-01-08', N'Nam', '079000004444', '0944444555',
     'cuong.lh@congty.vn', N'23 Điện Biên Phủ, Bình Thạnh, TP.HCM', '2023-02-01',
     'PB01', 'CV02', 'NV001', N'Đang làm', 6000000),

    ('NV005', N'Phạm Thu Dung',  '1997-11-30', N'Nữ',  '079097005555', '0955555666',
     'dung.pt@congty.vn', N'56 Trường Chinh, Tân Bình, TP.HCM', '2022-07-11',
     'PB01', 'CV02', 'NV001', N'Tạm nghỉ', 12000000),

    ('NV008', N'Nguyễn Minh Quân', '1980-02-15', N'Nam', '052206005765', '0353942865',
     'quan.nm@congty.vn', N'Tăng Nhơn Phú, Thủ Đức, HCM', '2010-01-05',
     'PB04', 'CV00', NULL, N'Đang làm', 30000000),

    ('NV009', N'Trần Văn Nhân',  '1982-06-20', N'Nam', '052206005766', '0353942866',
     'nhan.tv@congty.vn', N'Tăng Nhơn Phú, Thủ Đức, HCM', '2011-03-10',
     'PB04', 'CV00', NULL, N'Đang làm', 30000000),

    ('NV010', N'Đinh Viết Thiên', '1983-09-12', N'Nam', '052206005767', '0353942867',
     'thien.dv@congty.vn', N'Dĩ An, Bình Dương', '2012-07-01',
     'PB04', 'CV00', NULL, N'Đang làm', 30000000);

-- Mật khẩu của mọi tài khoản mẫu: 123456
DECLARE @MatKhau123456 VARCHAR(64) = '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92';

INSERT INTO TAIKHOAN (TenDangNhap, MatKhau, VaiTro, MaNV, TrangThai) VALUES
    ('quanly',   @MatKhau123456, 'QuanLy',   'NV001', N'Hoạt động'),
    ('an.nv',    @MatKhau123456, 'NhanVien', 'NV002', N'Hoạt động'),
    ('binh.tt',  @MatKhau123456, 'NhanVien', 'NV003', N'Hoạt động'),
    ('cuong.lh', @MatKhau123456, 'NhanVien', 'NV004', N'Hoạt động'),
    ('gd.quan',  @MatKhau123456, 'GiamDoc',  'NV008', N'Hoạt động'),
    ('gd.nhan',  @MatKhau123456, 'GiamDoc',  'NV009', N'Hoạt động'),
    ('gd.thien', @MatKhau123456, 'GiamDoc',  'NV010', N'Hoạt động');

-- Vài bản ghi chấm công của những ngày trước (hôm nay để trống để thử chấm công)
INSERT INTO CHAMCONG (MaNV, NgayChamCong, MaCa, GioVao, GioRa, TrangThai, GhiChu) VALUES
    ('NV002', DATEADD(DAY, -1, CAST(GETDATE() AS DATE)), 'CA01', '07:55', '17:03', N'Đủ ca',           N'Đúng giờ'),
    ('NV003', DATEADD(DAY, -1, CAST(GETDATE() AS DATE)), 'CA01', '08:20', '16:40', N'Đi muộn, về sớm', N'Có việc riêng buổi chiều'),
    ('NV004', DATEADD(DAY, -1, CAST(GETDATE() AS DATE)), 'CA01', NULL,    NULL,    N'Vắng mặt',        N'Chưa chấm công'),
    ('NV002', DATEADD(DAY, -2, CAST(GETDATE() AS DATE)), 'CA01', '08:02', '17:00', N'Đủ ca',           NULL),
    ('NV005', DATEADD(DAY, -2, CAST(GETDATE() AS DATE)), 'CA02', '13:05', '21:00', N'Đủ ca',           NULL),
    ('NV001', DATEADD(DAY, -1, CAST(GETDATE() AS DATE)), 'CA01', '07:50', '17:30', N'Đủ ca',           N'Họp đến muộn');
GO

/* ========================= KIỂM TRA NHANH ========================= */
SELECT N'PHÒNG BAN'   AS Bang, COUNT(*) AS SoDong FROM PHONGBAN
UNION ALL SELECT N'CHỨC VỤ',     COUNT(*) FROM CHUCVU
UNION ALL SELECT N'CA LÀM VIỆC', COUNT(*) FROM CALAMVIEC
UNION ALL SELECT N'NHÂN VIÊN',   COUNT(*) FROM NHANVIEN
UNION ALL SELECT N'TÀI KHOẢN',   COUNT(*) FROM TAIKHOAN
UNION ALL SELECT N'CHẤM CÔNG',   COUNT(*) FROM CHAMCONG;

SELECT nv.MaNV, nv.HoTen, nv.MaQuanLy, pb.TenPB, cv.TenCV, tk.TenDangNhap, tk.VaiTro
FROM NHANVIEN nv
LEFT JOIN PHONGBAN pb ON pb.MaPB = nv.MaPB
LEFT JOIN CHUCVU   cv ON cv.MaCV = nv.MaCV
LEFT JOIN TAIKHOAN tk ON tk.MaNV = nv.MaNV
ORDER BY nv.MaNV;
GO

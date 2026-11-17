# Giải thích từng thành phần dữ liệu (CSDL QuanLyNhanSu)

Tài liệu này giải thích **từng bảng, từng cột** trong CSDL mà ứng dụng dùng, kèm:
kiểu dữ liệu, ràng buộc, ý nghĩa nghiệp vụ, và lớp Java nào đọc/ghi cột đó.

Toàn bộ CSDL dùng `utf8mb4` / `utf8mb4_unicode_ci` (tiếng Việt + emoji, so sánh không
phân biệt hoa–thường).

---

## Sơ đồ quan hệ

```text
PHONGBAN ──1──n── NHANVIEN ──1──1── TAIKHOAN
                     │  n
CHUCVU  ──1──n───────┤  │
                     │  └── n ── CHAMCONG ── n──1── CALAMVIEC
                     └── n (tự tham chiếu: MaQuanLy → MaNV)
```

Bảng **trung tâm là `NHANVIEN`**: mọi bảng khác đều nối về nó (trừ 3 bảng danh mục
`PHONGBAN`, `CHUCVU`, `CALAMVIEC` chỉ là "từ điển").

---

## 1. `PHONGBAN` — phòng ban (4 dòng)

| Cột | Kiểu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `MaPB` | VARCHAR(10) | **PK** | Mã phòng ban, dạng `PB01`, `PB04` |
| `TenPB` | VARCHAR(100) | NOT NULL, **UNIQUE** | Tên phòng ban; không cho 2 phòng trùng tên |
| `MoTa` | VARCHAR(255) | NULL | Mô tả ngắn chức năng phòng |

Dữ liệu hiện có: `PB01` Phòng Kỹ thuật, `PB02` Kinh doanh, `PB03` Hành chính - Nhân sự,
`PB04` Ban Giám đốc.

Dùng ở: `DanhMucDAO.layPhongBan()` (đổ vào combo), `NhanVienDAO` (JOIN để lấy `TenPB`
hiển thị), `NhanVienFormDialog`.

---

## 2. `CHUCVU` — chức vụ (3 dòng)

| Cột | Kiểu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `MaCV` | VARCHAR(10) | **PK** | Mã chức vụ |
| `TenCV` | VARCHAR(100) | NOT NULL, **UNIQUE** | Tên chức vụ |
| `MoTa` | VARCHAR(255) | NULL | Mô tả quyền hạn |

Dữ liệu hiện có và **ý nghĩa đặc biệt** (code dựa vào đúng mã này):

| Mã | Tên | Vai trò trong code |
|---|---|---|
| `CV00` | Giám đốc | `NhanVienFormDialog` nhận biết "người này là giám đốc" |
| `CV01` | Trưởng phòng | `NhanVienDAO.MA_CV_QUAN_LY` — chức vụ được gán khi **thăng chức** |
| `CV02` | Nhân viên | Chức vụ mặc định |

> Chức vụ "Thực tập sinh" (`CV03`) đã bị **bỏ** ở phiên bản mới; nhân viên cũ được
> chuyển sang `CV02`.

Dùng ở: `DanhMucDAO.layChucVu()`, `NhanVienDAO.thangChuc()`.

---

## 3. `CALAMVIEC` — ca làm việc (2 dòng)

| Cột | Kiểu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `MaCa` | VARCHAR(10) | **PK** | Mã ca (`CA01`, `CA02`) |
| `TenCa` | VARCHAR(50) | NOT NULL | Tên ca để hiển thị |
| `GioBatDau` | TIME | NOT NULL | Giờ bắt đầu ca, dùng để tính **đi muộn** |
| `GioKetThuc` | TIME | NOT NULL | Giờ kết thúc ca, dùng để tính **về sớm** |

Dữ liệu: `CA01` Ca hành chính 08:00–17:00, `CA02` Ca chiều - tối 13:00–21:00.

Dùng ở: `DanhMucDAO.layCaLamViec()`, `ChamCongService.phutDiMuon/phutVeSom/soGioLam`.

---

## 4. `NHANVIEN` — nhân viên (10 dòng) ⭐ bảng trung tâm

| Cột | Kiểu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `MaNV` | VARCHAR(10) | **PK** | Mã nhân viên, dạng `NV001`… do app sinh tự động (`sinhMaNhanVien`) |
| `HoTen` | VARCHAR(100) | **NOT NULL** | Họ tên đầy đủ |
| `NgaySinh` | DATE | NULL | Ngày sinh; app kiểm tra không ở tương lai, phải trước ngày vào làm |
| `GioiTinh` | VARCHAR(10) | NULL | "Nam" / "Nữ" / "Khác" |
| `CCCD` | VARCHAR(20) | **UNIQUE**, NULL | Số CCCD; UNIQUE để 1 người không có 2 hồ sơ |
| `SoDienThoai` | VARCHAR(15) | NULL | Số điện thoại (app kiểm tra 9–11 số) |
| `Email` | VARCHAR(100) | **UNIQUE**, NULL | Email; UNIQUE để không trùng |
| `DiaChi` | VARCHAR(255) | NULL | Địa chỉ thường trú |
| `NgayVaoLam` | DATE | NULL | Ngày bắt đầu làm việc |
| `MaPB` | VARCHAR(10) | **FK → PHONGBAN** | Nhân viên thuộc phòng nào |
| `MaCV` | VARCHAR(10) | **FK → CHUCVU** | Chức vụ hiện tại |
| `MaQuanLy` | VARCHAR(10) | **FK → NHANVIEN(MaNV)**, NULL | **Cấp trên trực tiếp** (tự tham chiếu). NULL = cấp cao nhất |
| `TrangThai` | VARCHAR(20) | NOT NULL, `DEFAULT 'Đang làm'`, **CHECK** | Chỉ được: `Đang làm`, `Tạm nghỉ`, `Đã nghỉ` |
| `Luong` | DECIMAL(12,2) | NOT NULL, `DEFAULT 0` | Mức lương hiện tại (đồng). `DECIMAL(12,2)` = tối đa 9.999.999.999,99 |

**Vì sao chọn kiểu đó**
- `DECIMAL` chứ không `FLOAT`: tiền phải chính xác tuyệt đối, `FLOAT` gây sai số làm tròn.
- `MaQuanLy` tự tham chiếu: cho phép cây phân cấp nhiều tầng (nhân viên → quản lý → giám đốc).
- `CCCD`, `Email` UNIQUE nhưng cho phép NULL: người chưa có CCCD/email vẫn lưu được,
  nhưng nếu đã nhập thì không được trùng người khác.
- `CHECK` chặn dữ liệu rác ngay ở tầng CSDL (không phụ thuộc app).

Dùng ở: `model.NhanVien`, `NhanVienDAO` (mọi hàm), `NhanVienPanel`, `NhanVienFormDialog`,
`GiamDocPanel`, `ThongTinCaNhanPanel`.

---

## 5. `TAIKHOAN` — tài khoản đăng nhập (9 dòng)

| Cột | Kiểu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `TenDangNhap` | VARCHAR(50) | **PK** | Tên đăng nhập, không dấu (`quanly`, `gd.quan`, `an.nv`) |
| `MatKhau` | VARCHAR(64) | NOT NULL | **Mã băm SHA-256** dạng hex (64 ký tự) — không lưu mật khẩu thật |
| `VaiTro` | VARCHAR(20) | NOT NULL, **CHECK** | Chỉ được: `GiamDoc`, `QuanLy`, `NhanVien` |
| `MaNV` | VARCHAR(10) | NOT NULL, **UNIQUE**, **FK → NHANVIEN** | Mỗi nhân viên chỉ có **1** tài khoản |
| `TrangThai` | VARCHAR(20) | NOT NULL, `DEFAULT 'Hoạt động'` | `Hoạt động` = đăng nhập được; `Bị khóa` = không |

**Vì sao 64 ký tự**: SHA-256 = 256 bit = 32 byte, mỗi byte viết bằng 2 ký tự hex → 64.
Ví dụ `123456` → `8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92`.

**Mối quan hệ với quyền**: `VaiTro` quyết định giao diện mở ra (`MainFrame`):
`GiamDoc` → panel giám đốc; `QuanLy` → panel quản lý nhân viên; `NhanVien` → chỉ hồ sơ + chấm công.

Dùng ở: `model.TaiKhoan`, `TaiKhoanDAO` (`dangNhap`, `doiMatKhau`, `capNhatVaiTro`),
`LoginFrame`, `DoiMatKhauDialog`.

---

## 6. `CHAMCONG` — bản ghi chấm công (10 dòng)

| Cột | Kiểu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `MaChamCong` | INT | **PK, AUTO_INCREMENT** | Khoá kỹ thuật, app không cần nhập; CSDL tự tăng |
| `MaNV` | VARCHAR(10) | NOT NULL, **FK → NHANVIEN** | Ai chấm công |
| `NgayChamCong` | DATE | NOT NULL | Ngày chấm công |
| `MaCa` | VARCHAR(10) | **FK → CALAMVIEC**, NULL | Ca áp dụng (để tính muộn/sớm) |
| `GioVao` | TIME | NULL | Giờ chấm vào; NULL = chưa vào |
| `GioRa` | TIME | NULL | Giờ chấm ra; NULL = chưa ra |
| `TrangThai` | VARCHAR(30) | NULL | `Đang làm`, `Đủ ca`, `Đi muộn`, `Về sớm`, `Đi muộn, về sớm`, `Vắng mặt` |
| `GhiChu` | VARCHAR(255) | NULL | Câu mô tả do `ChamCongService.moTa()` sinh ra |

**Ràng buộc quan trọng nhất**: `UNIQUE (MaNV, NgayChamCong)`
→ mỗi người **chỉ có 1 bản ghi mỗi ngày**. Nhờ đó `ChamCongDAO.luu()` làm việc kiểu
"upsert": có rồi thì `UPDATE` (chấm ra ghi thêm giờ ra), chưa có thì `INSERT`.

Dùng ở: `model.ChamCong`, `ChamCongDAO`, `ChamCongService`, `ChamCongPanel`,
`QuanLyChamCongPanel`.

---

## Luồng dữ liệu khi dùng app (ví dụ thực tế)

**a) Thêm nhân viên** (quản lý/giám đốc bấm Lưu)
```text
NhanVienFormDialog
  → NhanVienDAO.sinhMaNhanVien()      đọc NHANVIEN để sinh NV011
  → NhanVienDAO.themKemTaiKhoan()     TRANSACTION:
        INSERT NHANVIEN   (14 cột, gồm Luong)
        INSERT TAIKHOAN   (MatKhau = SHA-256 của mật khẩu nhập)
     lỗi giữa chừng → ROLLBACK, không có nhân viên "mồ côi"
```

**b) Chấm công vào/ra**
```text
ChamCongPanel → ChamCongService.xacDinhTrangThai(ca, giờ vào, giờ ra)
              → ChamCongDAO.luu():
                    UPDATE CHAMCONG WHERE MaNV = ? AND NgayChamCong = ?
                    nếu 0 dòng → INSERT CHAMCONG
```

**c) Đăng nhập**
```text
LoginFrame → TaiKhoanDAO.dangNhap(ten, mật khẩu)
                SELECT TAIKHOAN JOIN NHANVIEN WHERE TenDangNhap = ?
                so SHA-256(mật khẩu) với cột MatKhau
           → kiểm tra TrangThai = 'Hoạt động'
           → MainFrame(tài khoản, nhân viên)
```

**d) Giám đốc tăng lương / thăng chức / cho thôi việc**
```text
GiamDocPanel → NhanVienDAO.tangLuong()   UPDATE NHANVIEN SET Luong = ?
             → NhanVienDAO.thangChuc()   TRANSACTION: NHANVIEN.MaCV = 'CV01'
                                                     + TAIKHOAN.VaiTro = 'QuanLy'
             → NhanVienDAO.thoiViec()    TRANSACTION: NHANVIEN.TrangThai = 'Đã nghỉ'
                                                     + TAIKHOAN.TrangThai = 'Bị khóa'
```

---

## Bảng tóm tắt kiểu dữ liệu

| Kiểu | Dùng cho | Ghi chú |
|---|---|---|
| `VARCHAR(n)` | mã, tên, email, địa chỉ… | `n` = số ký tự tối đa; đủ cho tiếng Việt vì CSDL dùng `utf8mb4` |
| `DATE` | NgaySinh, NgayVaoLam, NgayChamCong | Chỉ ngày, không giờ |
| `TIME` | GioBatDau, GioKetThuc, GioVao, GioRa | Chỉ giờ:phút:giây |
| `DECIMAL(12,2)` | Luong | Số chính xác: 12 chữ số, 2 số lẻ |
| `INT AUTO_INCREMENT` | MaChamCong | Số tự tăng do CSDL sinh |

## Quy tắc dữ liệu cần nhớ

1. Không xoá được phòng ban/chức vụ/nhân viên đang được bảng khác tham chiếu (khoá ngoại chặn).
2. `NHANVIEN.TrangThai` chỉ 3 giá trị; `TAIKHOAN.VaiTro` chỉ 3 giá trị (CHECK chặn).
3. Mỗi nhân viên tối đa **1 tài khoản**, mỗi ngày tối đa **1 bản ghi chấm công**.
4. Mật khẩu chỉ lưu mã băm — quên thì phải **đặt lại**, không xem được.
5. Tiền lương luôn là `DECIMAL`, không dùng số thực.

# Quản lý Nhân sự & Chấm công (Java Swing + JDBC + MySQL)

## 1. Chức năng chính

Ứng dụng desktop quản lý nhân sự, chấm công và tính lương, đăng nhập phân quyền theo 3 vai trò:

| Vai trò | Chức năng chính |
|---|---|
| **Giám đốc** | Xem toàn bộ quản lý và nhân viên dưới quyền từng quản lý; thêm/sửa hồ sơ; tăng lương; thăng chức nhân viên lên quản lý; cho thôi việc; xem bảng lương toàn công ty |
| **Quản lý** | Quản lý nhân viên cấp dưới (thêm, sửa, cho nghỉ việc); chấm công nhân viên (chấm vào/ra, chấm hộ); xem bảng lương cấp dưới |
| **Nhân viên** | Xem hồ sơ của mình; tự chấm công vào/ra; xem lịch sử chấm công, số ngày công, số lần đi muộn; xem bảng lương của mình |
| **Mọi vai trò** | Đổi mật khẩu đang dùng |

## 2. Chi tiết

- **CSDL**: MySQL 8.0, 7 bảng `PHONGBAN`, `CHUCVU`, `CALAMVIEC`, `NHANVIEN`, `TAIKHOAN`, `CHAMCONG`, `BANGLUONG`; `NHANVIEN` có cột `Luong`, cột `MaQuanLy` tự tham chiếu để tạo quan hệ cấp trên – cấp dưới.
- **Kiến trúc**: `Main` → `view` (giao diện) → `dao` (SQL) → `model` (dữ liệu); `service` chứa nghiệp vụ, `util` chứa tiện ích dùng chung.
- **Chấm công**: mỗi nhân viên 1 bản ghi/ngày, so với giờ ca và dung sai 5 phút → `Đủ ca`, `Đi muộn`, `Về sớm`, `Đi muộn, về sớm`, `Vắng mặt`.
- **Bảng lương**: lương công = lương cơ bản × ngày công thực tế ÷ ngày công chuẩn, trừ tiền phạt đi muộn; có **Chốt lương** (lưu vào bảng `BANGLUONG`) và **Hủy chốt**.
- **Bảo mật**: mật khẩu lưu dạng băm SHA-256; phân quyền kiểm tra ở cả giao diện lẫn câu lệnh SQL.
- **Cấu hình**: đọc `db.properties` (file chứa mật khẩu, không đưa lên Git) — tạo bằng cách copy `db.properties.example`.
- **Dữ liệu mẫu**: 21 nhân viên (3 giám đốc, 3 trưởng phòng, 15 nhân viên) thuộc 4 phòng ban, 2 ca làm việc, 798 bản ghi chấm công tháng 10–11/2026.

**Chạy** (JDK 22+):
```bat
copy db.properties.example db.properties   :: rồi điền mật khẩu MySQL
java src\Main.java
```

Hoặc chạy `chay.bat`. Tài khoản demo (mật khẩu `123456`): `gd.quan` (giám đốc), `thoai.bq` (trưởng phòng), `nva` (nhân viên).

## 3. Các chức năng, kiến thức được sử dụng trong bài

### Chức năng

- Đăng nhập, phân quyền 3 vai trò; đổi mật khẩu.
- Quản lý nhân viên: thêm, sửa, đổi trạng thái, cho thôi việc; kiểm tra dữ liệu (CCCD, số điện thoại, email, ngày tháng, trùng CCCD/email).
- Chấm công: chấm vào/ra cho bản thân, chấm hộ nhân viên, lịch sử theo tháng, thống kê ngày công và số lần đi muộn.
- Nghiệp vụ nhân sự: tăng lương, thăng chức (đổi chức vụ + vai trò tài khoản), cho thôi việc (đổi trạng thái + khóa tài khoản).
- Bảng lương: tính lương theo tháng, chốt lương, hủy chốt, xem theo vai trò (cá nhân / cấp dưới / toàn công ty).
- Tìm kiếm, lọc nhân viên; hiển thị dạng bảng; định dạng ngày, số, tiền theo kiểu Việt Nam.

### Kiến thức

- **Java OOP**: lớp, đóng gói, kế thừa, enum; kế thừa `JFrame`/`JPanel`/`JDialog`.
- **Java Swing**: `BorderLayout`, `GridBagLayout`, `GridLayout`, `CardLayout`, `JTable` + `TableModel`, `JSplitPane`, `JComboBox`, xử lý sự kiện (`ActionListener`, `MouseListener`, `DocumentListener`), `Timer`.
- **JDBC**: `DriverManager`, `Connection`, `PreparedStatement` (chống SQL Injection), `ResultSet`, `try-with-resources`, transaction (`setAutoCommit`/`commit`/`rollback`), câu lệnh upsert.
- **SQL**: DDL/DML, `PRIMARY KEY`/`FOREIGN KEY`/`UNIQUE`/`CHECK`, `AUTO_INCREMENT`, `JOIN` nhiều bảng, `LIKE`, `COUNT`, `ORDER BY`.
- **Bảo mật**: băm mật khẩu SHA-256 (không lưu mật khẩu gốc), kiểm tra quyền trước khi thao tác.
- **Kiến trúc**: mô hình phân tầng/MVC, tách DAO khỏi giao diện, lớp tiện ích dùng chung.
- **Xử lý lỗi**: bắt ngoại lệ, thông báo tiếng Việt dễ hiểu, kiểm tra dữ liệu nhập trước khi ghi CSDL.
- **Khác**: đọc file cấu hình `.properties`, mã hoá UTF-8/`utf8mb4`, `DateTimeFormatter`, `BigDecimal` cho tiền tệ, Git.

# Quản lý Nhân sự & Chấm công (Java Swing + JDBC)

Ứng dụng desktop viết **hoàn toàn bằng Java** (Swing + JDBC).
Hỗ trợ **2 hệ CSDL**: **SQL Server** (đang cấu hình sẵn, chạy được ngay) và
**MySQL 8.0** (có sẵn script + cấu hình mẫu, chỉ cần thêm driver và mật khẩu).

---

## 1. Chức năng theo vai trò

| Vai trò | Chức năng |
|---|---|
| **Giám đốc** (`gd.quan`, `gd.nhan`, `gd.thien`) | Xem **toàn bộ quản lý** và **nhân viên dưới quyền từng quản lý** (bấm 1 quản lý là thấy danh sách của quản lý đó); **tăng lương** cho cả quản lý và nhân viên; **thăng chức** nhân viên lên quản lý; **cho thôi việc** (khóa tài khoản); xem hồ sơ của mình, tự chấm công |
| **Quản lý** (`quanly`) | Xem **nhân viên cấp dưới**, **thêm nhân viên** (kèm tài khoản + lương), sửa hồ sơ, cho nghỉ việc, **chấm công nhân viên** (theo ngày, chấm hộ vào/ra, xem lịch sử tháng), xem hồ sơ của mình, tự chấm công |
| **Nhân viên** (`an.nv`, …) | Chỉ xem **thông tin của chính mình** (chỉ đọc) và **chấm công của mình** (vào/ra, lịch sử tháng, số ngày công, số lần đi muộn) |
| **Mọi vai trò** | **Đổi mật khẩu** (menu Hệ thống → Đổi mật khẩu) |

Quan hệ cấp trên – cấp dưới dựa vào cột `NHANVIEN.MaQuanLy`; vai trò đăng nhập nằm ở
cột `TAIKHOAN.VaiTro` (`GiamDoc` / `QuanLy` / `NhanVien`).

---

## 2. Cấu trúc dự án

```
Btapnhom/
├── database/
│   ├── QuanLyNhanSu.sql          Script cho SQL Server
│   ├── mysql/QuanLyNhanSu.sql    Script cho MySQL 8.0 (utf8mb4)
│   └── mysql/02_giam_doc_luong.sql  Nâng cấp CSDL cũ: thêm vai trò Giám đốc + cột lương
├── db.properties                 Cấu hình kết nối (đang trỏ MySQL)
├── chay.bat                      Biên dịch + chạy
├── src/
│   ├── Main.java                 Điểm khởi động
│   ├── model/                    NhanVien (có Lương), TaiKhoan, PhongBan, ChucVu, CaLamViec, ChamCong
│   ├── dao/                      TaiKhoanDAO, NhanVienDAO, ChamCongDAO, DanhMucDAO
│   ├── service/ChamCongService   Nghiệp vụ: đi muộn, về sớm, số giờ làm, trạng thái
│   ├── util/                     KetNoiCSDL, MatKhau (SHA-256), DinhDang
│   └── view/                     LoginFrame, MainFrame, GiaoDien, ThongTinCaNhanPanel,
│                                 NhanVienPanel, NhanVienFormDialog, ChamCongPanel,
│                                 QuanLyChamCongPanel, GiamDocPanel, DoiMatKhauDialog
├── tools/                        KiemTraKetNoi, ChayScriptSQL, ChuyenDuLieu, BamMatKhau
├── lib/                          Driver JDBC (MySQL + SQL Server)
├── LENH.md                       Danh sách lệnh cần thực hiện
├── TAI-LIEU-HAM.md               Giải thích chức năng từng hàm
└── README.md
```

---

## 3. Chạy ngay (SQL Server đã cấu hình sẵn)

Máy đã có: JDK, SQL Server (TCP 1433 + Mixed Mode đã bật), driver
`lib\mssql-jdbc-12.8.1.jre11.jar`, CSDL `QuanLyNhanSu` với dữ liệu mẫu.

**Cách 1 — chỉ cần chạy `Main.java`** (yêu cầu **JDK 22 trở lên** — máy đang dùng JDK 26):

```bat
cd /d C:\Quann\Btapnhom
java src\Main.java
```

Không cần `-cp`, không cần biên dịch trước, không cần `chay.bat`:
- Java tự biên dịch `Main.java` **và các file nó dùng** (`view`, `dao`, `model`,
  `service`, `util`) rồi chạy luôn.
- Driver JDBC trong `lib\` được chương trình **tự nạp lúc chạy** (xem
  `util.KetNoiCSDL.timDriverTrongThuMucLib()`), nên không phải khai báo `-cp`.

Mỗi lần chạy tốn thêm 1–2 giây để biên dịch, đổi lại sửa code xong chạy lại
lệnh trên là thấy kết quả. **Phải `cd` vào thư mục dự án trước**, vì
`db.properties` được đọc theo thư mục hiện tại.

**Cách 2 — dùng `chay.bat`** (biên dịch ra thư mục `out` rồi chạy; dùng được cho
mọi JDK từ 8 trở lên):

```bat
cd /d C:\Quann\Btapnhom
chay.bat
```

**Cách 3 — chỉ JDK 8–21** (JDK cũ không chạy thẳng file nguồn được): biên dịch
một lần rồi chạy:

```bat
javac -encoding UTF-8 -d out src\*.java src\dao\*.java src\model\*.java src\service\*.java src\util\*.java src\view\*.java
java -cp "out;lib/*" Main
```

Đăng nhập: `quanly` / `123456` hoặc `an.nv` / `123456`.

Kiểm tra nhanh kết nối, không cần mở giao diện (dùng bản đã biên dịch trong `out`):

```bat
java -cp "out;lib/*" tools\KiemTraKetNoi.java
```

Nếu cần tạo lại CSDL SQL Server (LƯU Ý: **phải có `-f 65001`** để tiếng Việt không lỗi dấu):

```bat
sqlcmd -S localhost -U sa -P 123456 -C -f 65001 -i "C:\Quann\Btapnhom\database\QuanLyNhanSu.sql"
```

---

## 4. Chuyển sang MySQL (đang chuyển)

`db.properties` **đã được đặt sẵn cho MySQL**. Còn 2 việc chỉ bạn làm được
(driver cần tải từ Internet, mật khẩu MySQL chỉ bạn biết):

**Bước 1** — tải **MySQL Connector/J** (<https://dev.mysql.com/downloads/connector/j/>),
chép file jar vào `lib\`:

```
Btapnhom\lib\mysql-connector-j-8.4.0.jar
```

**Bước 2** — mở `db.properties`, sửa **đúng một dòng**:

```properties
db.password=<mật khẩu MySQL của bạn>
```

**Bước 3** — tạo CSDL MySQL (chọn 1 trong 2 cách):

```bat
:: Cách A - bằng dòng lệnh
chay.bat build
java -cp "out;lib/*" tools\ChayScriptSQL.java database/mysql/QuanLyNhanSu.sql
```

Hoặc **MySQL Workbench** → kết nối `localhost:3306` → `File → Open SQL Script…`
→ chọn `database\mysql\QuanLyNhanSu.sql` → **Execute**.

**Bước 4** — kiểm tra và chạy:

```bat
java -cp "out;lib/*" tools\KiemTraKetNoi.java
:: phải hiện: Hệ quản trị: MySQL / KẾT NỐI CSDL: THÀNH CÔNG
chay.bat
```

### Chuyển dữ liệu đang có ở SQL Server sang MySQL (tùy chọn)

Script MySQL ở bước 3 chỉ tạo **dữ liệu mẫu**. Nếu muốn mang theo dữ liệu thật
(nhân viên, tài khoản, chấm công đã nhập), chạy thêm công cụ chuyển dữ liệu:

```bat
java -cp "out;lib/*" tools\ChuyenDuLieu.java
```

Công cụ sẽ: đọc toàn bộ bảng từ SQL Server → xoá dữ liệu cũ trong MySQL → ghi lại
đúng thứ tự khoá ngoại, rồi in số dòng của từng bảng ở cả hai bên.
(Nếu tài khoản SQL Server không phải `sa`/`123456`, truyền vào sau lệnh:
`tools\ChuyenDuLieu.java <user> <password>`.)

### Quay lại SQL Server

Mở `db.properties`, bỏ `#` ở 4 dòng SQL Server (cuối file) và thêm `#` vào 4 dòng
MySQL. Dữ liệu bên SQL Server vẫn còn nguyên.

---

## 5. Tài khoản mẫu (mật khẩu đều là `123456`)

| Tên đăng nhập | Vai trò | Nhân viên |
|---|---|---|
| `gd.quan` | Giám đốc | NV008 - Nguyễn Minh Quân (Ban Giám đốc) |
| `gd.nhan` | Giám đốc | NV009 - Trần Văn Nhân (Ban Giám đốc) |
| `gd.thien` | Giám đốc | NV010 - Đinh Viết Thiên (Ban Giám đốc) |
| `quanly` | Quản lý | NV001 - Trần Văn Quản (Trưởng phòng Kỹ thuật) |
| `an.nv` | Nhân viên | NV002 - Nguyễn Thị An |
| `binh.tt` | Nhân viên | NV003 - Trần Thị Bình |
| `cuong.lh` | Nhân viên | NV004 - Lê Hoàng Cường |

Mật khẩu lưu dạng **SHA-256** (`8d969eef…adc6c92`), so khớp bằng `util.MatKhau.khop()`.
Người dùng đổi mật khẩu ngay trong app: **Hệ thống → Đổi mật khẩu**.
Nếu quên, dùng `tools\BamMatKhau.java` để tạo mã băm mới rồi `UPDATE TAIKHOAN SET MatKhau = '…'`.

---

## 6. Quy tắc nghiệp vụ chấm công

| Tình huống | Trạng thái |
|---|---|
| Chưa có giờ vào | `Vắng mặt` |
| Có giờ vào, chưa có giờ ra | `Đang làm` / `Đi muộn` |
| Vào sau giờ bắt đầu **> 5 phút** | `Đi muộn` (kèm số phút) |
| Ra trước giờ kết thúc **> 5 phút** | `Về sớm` (kèm số phút) |
| Cả hai | `Đi muộn, về sớm` |
| Không vi phạm | `Đủ ca` |

Mỗi nhân viên chỉ có 1 bản ghi/ngày; dung sai `ChamCongService.PHUT_DUNG_SAI = 5`.

---

## 7. Ghi chú kỹ thuật

- **Phân quyền**: `TaiKhoan.isQuanLy()`; menu/panel quản lý chỉ tạo cho vai trò quản lý;
  truy vấn cấp dưới luôn lọc `WHERE MaQuanLy = ?`.
- **Giao dịch**: thêm nhân viên + tạo tài khoản trong một transaction (rollback nếu lỗi).
- **Bảo mật**: `PreparedStatement` cho mọi câu lệnh; mật khẩu băm SHA-256.
- **Tương thích CSDL**: dùng `COALESCE`, tham số hoá chuỗi tiếng Việt (không dùng
  literal `N'…'`), nên chạy được cả SQL Server và MySQL.
- **Tiếng Việt**: CSDL MySQL tạo với `utf8mb4`; script SQL Server khi nạp bằng `sqlcmd`
  **phải** có `-f 65001`, nếu không dữ liệu lỗi dấu → app báo “Tài khoản đã bị khóa”.
- Cảnh báo `[serial]`/`[this-escape]` khi biên dịch bằng JDK 21+ là đặc thù của Swing.

---

## 8. Công cụ trong `tools/`

| File | Công dụng | Lệnh |
|---|---|---|
| `KiemTraKetNoi.java` | kiểm tra kết nối theo `db.properties` (in cả hệ quản trị đang dùng) | `java -cp "out;lib/*" tools\KiemTraKetNoi.java` |
| `ChayScriptSQL.java` | nạp file .sql qua JDBC (đọc UTF-8; hỗ trợ cả `GO` và `;`) | `java -cp "out;lib/*" tools\ChayScriptSQL.java database\mysql\QuanLyNhanSu.sql` |
| `ChuyenDuLieu.java` | chép toàn bộ dữ liệu SQL Server → MySQL | `java -cp "out;lib/*" tools\ChuyenDuLieu.java` |
| `BamMatKhau.java` | băm mật khẩu để đặt lại | `java -cp out tools\BamMatKhau.java 123456` |

---

## 9. Hướng phát triển thêm

- Đổi mật khẩu trong app, màn hình quản lý tài khoản (xem/đặt lại mật khẩu).
- Nghỉ phép, bảng lương theo tháng từ dữ liệu chấm công.
- Xuất báo cáo Excel/PDF, thống kê theo phòng ban.

---


## 11. Nếu bạn NHẬN folder này từ người khác

Folder đã có sẵn mọi thứ: mã nguồn (`src`), **cả 2 driver JDBC** trong `lib`
(`mysql-connector-j-26.10.0.jar` và `mssql-jdbc-12.8.1.jre11.jar`), script CSDL
(`database/`), `db.properties`, `chay.bat`, tài liệu (`README.md`, `LENH.md`,
`CLEANCODE.md`, `TAI-LIEU-HAM.md`).

Mặc định dự án dùng **MySQL 8.0**. Người nhận chỉ phải: cài JDK + MySQL, khai báo
mật khẩu MySQL, tạo CSDL.

### 11.1. Cần có trên máy

| Thứ cần | Kiểm tra bằng | Ghi chú |
|---|---|---|
| **JDK 22 trở lên** (khuyến nghị) | `java -version` | JDK 8–21 vẫn chạy được nhưng phải biên dịch trước bằng `javac` |
| **MySQL 8.0 đang chạy** | `sc query MySQL80` và `netstat -ano \| findstr :3306` | |
| Internet | không cần | Driver JDBC đã nằm trong `lib\` |

> Chương trình **bắt buộc** phải có CSDL, vì dữ liệu nằm trong CSDL chứ không trong code.

### 11.2. Các lệnh cần chạy (đúng thứ tự)

```bat
:: 1) giải nén, vào thư mục dự án
cd /d <thư mục đã giải nén>

:: 2) khai báo mật khẩu MySQL — sửa đúng 1 dòng rồi lưu (Ctrl+S)
notepad db.properties
::     db.user=root
::     db.password=<mật khẩu MySQL của máy này>

:: 3) biên dịch (cần cho các công cụ trong tools\)
chay.bat build

:: 4) tạo CSDL + dữ liệu mẫu
java -cp "out;lib/*" tools\ChayScriptSQL.java database/mysql/QuanLyNhanSu.sql

:: 5) kiểm tra kết nối — phải in "Hệ quản trị: MySQL" và "THÀNH CÔNG"
java -cp "out;lib/*" tools\KiemTraKetNoi.java

:: 6) chạy chương trình
java src\Main.java
```

Bước 4 có thể làm bằng **MySQL Workbench** thay cho dòng lệnh:
kết nối `localhost:3306` → `File → Open SQL Script…` → chọn
`database\mysql\QuanLyNhanSu.sql` → bấm **Execute** (tia chớp).

### 11.3. Đăng nhập

| Tên đăng nhập | Mật khẩu | Vai trò |
|---|---|---|
| `quanly` | `123456` | Quản lý |
| `an.nv`, `binh.tt`, `cuong.lh` | `123456` | Nhân viên |

### 11.4. Nếu máy người nhận dùng SQL Server thay vì MySQL

```bat
:: 1) sửa db.properties: bỏ # ở 4 dòng SQL Server (cuối file), thêm # vào 4 dòng MySQL
notepad db.properties

:: 2) nạp script SQL Server — BẮT BUỘC có -f 65001 để tiếng Việt không lỗi dấu
sqlcmd -S localhost -U sa -P <mật-khẩu-sa> -C -f 65001 -i "database\QuanLyNhanSu.sql"

:: 3) chạy lại
java src\Main.java
```

### 11.5. Lỗi thường gặp trên máy mới

| Hiện tượng | Cách xử lý |
|---|---|
| `Chưa nạp được driver JDBC` | Chạy sai thư mục (phải đứng ở thư mục dự án có `lib\`), hoặc `lib` không có jar |
| `Access denied for user 'root'@'localhost'` | Chưa sửa `db.password` trong `db.properties` (hoặc sai mật khẩu MySQL) |
| `Unknown database 'QuanLyNhanSu'` | Chưa chạy bước 4 (tạo CSDL) |
| `Communications link failure` | MySQL chưa chạy: `net start MySQL80`, kiểm tra `netstat -ano \| findstr :3306` |
| Mở app nhưng chữ trong bảng bị mất dấu | Lỗi của bộ gõ tiếng Việt (UniKey/EVKey), không phải lỗi app — xem mục 12 |
| `Cannot find file main` / `Không tìm thấy Main` | Phải gõ `java src\Main.java` (có đuôi `.java`) và đứng ở thư mục dự án |

### 11.6. Trước khi nén gửi đi

- Nên xoá `out\` cho gọn (người nhận chạy `chay.bat build` sẽ tạo lại).
- **Che mật khẩu**: nếu gửi cho nhiều người, đổi `db.password` trong `db.properties`
  về lại `MAT_KHAU_MYSQL_CUA_BAN` trước khi nén (người nhận tự điền mật khẩu máy họ).
- Cần gửi đủ: `src\`, `lib\` (2 file jar), `database\`, `tools\`, `db.properties`,
  `chay.bat`, các file `.md`.

---

## 12. Lỗi gõ tiếng Việt trong app (không phải lỗi chương trình)

Java Swing nhận chữ tiếng Việt qua bộ gõ UniKey/EVKey, **không** nhận tốt bộ gõ
Telex tích hợp của Windows. Nếu gõ chữ không dấu được mà gõ dấu thì mất dấu:

1. Mở **UniKey** (`C:\Program Files\UniKey\UniKeyNT.exe`), bấm biểu tượng ở khay
   hệ thống để chuyển sang **V** (Vietnamese).
2. Chuột phải biểu tượng UniKey → **Bảng mã: Unicode**, **Kiểu gõ: Telex**.
3. Chuyển bàn phím Windows sang **ENG** (`Win + Space`) để tránh xung đột.

CSDL MySQL đã tạo với `utf8mb4` nên khi nhập đúng, tiếng Việt lưu và hiển thị đúng.

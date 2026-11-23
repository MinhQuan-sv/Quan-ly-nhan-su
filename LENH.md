# Danh sách lệnh - Quản lý Nhân sự & Chấm công

Dự án dùng **MySQL 8.0**. Mọi lệnh chạy trong `cmd` tại thư mục dự án.

```bat
cd /d C:\Quann\Btapnhom
```

---

## 1. Kiểm tra môi trường

```bat
java -version
javac -version
sc query MySQL80
netstat -ano | findstr :3306
```

## 2. Cấu hình kết nối (chỉ làm 1 lần)

Nếu chưa có `db.properties`:

```bat
copy db.properties.example db.properties
notepad db.properties
::   sửa dòng: db.password=<mật khẩu MySQL của bạn>
```

File `db.properties` chứa mật khẩu thật nên **không** đưa lên Git (đã có trong `.gitignore`).

## 3. Kiểm tra kết nối

```bat
chay.bat build
java -cp "out;lib/*" tools\KiemTraKetNoi.java
```

Kỳ vọng: `Hệ quản trị: MySQL` và `KẾT NỐI CSDL: THÀNH CÔNG`.

## 4. Biên dịch

```bat
chay.bat build
```

Hoặc không dùng `chay.bat`:

```bat
javac -encoding UTF-8 -d out src\*.java src\dao\*.java src\model\*.java src\service\*.java src\util\*.java src\view\*.java
```

## 5. Chạy ứng dụng

```bat
:: Cách 1 (JDK 22+): chạy thẳng file nguồn, không cần biên dịch, không cần -cp
java src\Main.java

:: Cách 2: dùng script
chay.bat

:: Cách 3: chạy bản đã biên dịch
java -cp "out;lib/*" Main
```

Đăng nhập: `gd.quan` / `123456` (giám đốc), `thoai.bq` / `123456` (trưởng phòng),
`nva` / `123456` (nhân viên).

## 6. Tạo lại CSDL MySQL (xoá sạch dữ liệu cũ)

⚠️ Lệnh này **xoá toàn bộ dữ liệu** rồi nạp lại dữ liệu mẫu.

```bat
chay.bat build
java -cp "out;lib/*" tools\ChayScriptSQL.java database\mysql\QuanLyNhanSu.sql
```

Hoặc bằng MySQL Workbench: kết nối `localhost:3306` → `File → Open SQL Script…`
→ chọn `database\mysql\QuanLyNhanSu.sql` → **Execute**.

## 7. Xem dữ liệu bằng dòng lệnh

```bat
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p
```

```sql
USE QuanLyNhanSu;
SELECT nv.MaNV, nv.HoTen, cv.TenCV, nv.Luong, tk.TenDangNhap, tk.VaiTro
FROM NHANVIEN nv
LEFT JOIN CHUCVU cv  ON cv.MaCV = nv.MaCV
LEFT JOIN TAIKHOAN tk ON tk.MaNV = nv.MaNV
ORDER BY nv.MaNV;

SELECT * FROM CHAMCONG WHERE NgayChamCong = CURDATE();
```

## 8. Đặt lại mật khẩu tài khoản ứng dụng

Tạo mã băm SHA-256 của mật khẩu mới:

```powershell
[BitConverter]::ToString([Security.Cryptography.SHA256]::Create()
  .ComputeHash([Text.Encoding]::UTF8.GetBytes('matkhaumoi'))).Replace('-','').ToLower()
```

Rồi chạy trong MySQL:

```sql
UPDATE TAIKHOAN SET MatKhau = '<mã-băm-vừa-tạo>' WHERE TenDangNhap = 'nva';
```

## 9. Dọn dẹp (tùy chọn)

```bat
rmdir /s /q out
```

## 10. Xử lý sự cố

| Thông báo | Nguyên nhân / cách sửa |
|---|---|
| `Chưa nạp được driver JDBC` | Thiếu file jar trong `lib\`, hoặc chạy sai thư mục (phải đứng ở thư mục dự án) |
| `Access denied for user 'root'@'localhost'` | Sai `db.password` trong `db.properties` |
| `Unknown database 'QuanLyNhanSu'` | Chưa chạy mục 6 (tạo CSDL) |
| `Communications link failure` | MySQL chưa chạy: `net start MySQL80`; kiểm tra cổng 3306 |
| App báo “Tài khoản đã bị khóa” | Tài khoản đúng là đang bị khóa, hoặc CSDL nạp sai encoding → tạo lại bằng mục 6 |
| Chữ tiếng Việt bị mất dấu khi nhập | Bật UniKey/EVKey (bảng mã Unicode, Telex), chuyển bàn phím Windows sang ENG |

---

## Phụ lục A — Nếu bạn NHẬN folder này từ người khác

```bat
:: 1) vào thư mục dự án
cd /d <thư mục đã giải nén>

:: 2) tạo file cấu hình từ bản mẫu rồi điền mật khẩu MySQL
copy db.properties.example db.properties
notepad db.properties

:: 3) biên dịch
chay.bat build

:: 4) tạo CSDL + dữ liệu mẫu
java -cp "out;lib/*" tools\ChayScriptSQL.java database\mysql\QuanLyNhanSu.sql

:: 5) kiểm tra kết nối (phải THÀNH CÔNG)
java -cp "out;lib/*" tools\KiemTraKetNoi.java

:: 6) chạy
java src\Main.java
```

## Phụ lục B — Tài khoản mẫu (mật khẩu `123456`)

| Tên đăng nhập | Vai trò | Nhân viên |
|---|---|---|
| `gd.quan`, `gd.nhan`, `gd.thien` | Giám đốc | NV008, NV009, NV010 (Ban Giám đốc) |
| `thoai.bq` | Trưởng phòng | NV001 Bạch Quốc Thoại (Hành chính - Nhân sự) |
| `quang.lnn` | Trưởng phòng | NV002 Lê Nguyễn Nhật Quang (Kế toán) |
| `nguyen.hg` | Trưởng phòng | NV003 Hồ Gia Nguyễn (Kỹ thuật) |
| `nva`, `nvb`, `nvc`, `nvd` | Nhân viên | NV011–NV014 (Hành chính - Nhân sự) |
| `tva`, `tvb`, `tvc`, `tvd`, `tve` | Nhân viên | NV015–NV019 (Kế toán) |
| `lva`, `lvb`, `lvc`, `lvd`, `lve`, `lvf` | Nhân viên | NV020–NV025 (Kỹ thuật) |

# Danh sách lệnh cần thực hiện (Windows)

Trạng thái hiện tại: **SQL Server** đã cấu hình xong và chạy được.
Chạy trong Command Prompt tại thư mục dự án.

---

## 0. Vào thư mục dự án

```bat
cd /d C:\Quann\Btapnhom
```

## 1. Kiểm tra môi trường

```bat
javac -version
sc query MSSQLSERVER
netstat -ano | findstr :1433
dir lib
```

Cần có: JDK chạy được, dịch vụ SQL Server `RUNNING`, cổng 1433 `LISTENING`,
trong `lib` có `mssql-jdbc-*.jar`.

## 2. Kiểm tra kết nối CSDL

```bat
java -cp "out;lib/*" tools\KiemTraKetNoi.java
```

Kỳ vọng: `KẾT NỐI CSDL   : THÀNH CÔNG`.

Nếu báo lỗi, xem mục 7 bên dưới.

## 3. Biên dịch

**Không bắt buộc** nếu dùng JDK 22+ (xem mục 4, cách 1). Nếu muốn biên dịch sẵn:

```bat
chay.bat build
```

## 4. Chạy ứng dụng

**Cách 1 — chỉ cần chạy Main.java (JDK 22+): không cần biên dịch, không cần chay.bat, không cần -cp**

```bat
cd /d C:\Quann\Btapnhom
java src\Main.java
```

Driver JDBC trong `lib\` được chương trình tự nạp khi chạy, nên không phải khai
báo classpath.

**Cách 2 — dùng chay.bat (mọi JDK từ 8):**

```bat
chay.bat
```

**Cách 3 — biên dịch rồi chạy bằng java (mọi JDK từ 8):**

```bat
javac -encoding UTF-8 -d out src\*.java src\dao\*.java src\model\*.java src\service\*.java src\util\*.java src\view\*.java
java -cp "out;lib/*" Main
```

Đăng nhập: `quanly` / `123456` (quản lý) hoặc `an.nv` / `123456` (nhân viên).

## 5. Tạo lại CSDL (chỉ khi cần) — SQL Server

```bat
sqlcmd -S localhost -U sa -P 123456 -C -f 65001 -i "C:\Quann\Btapnhom\database\QuanLyNhanSu.sql"
```

> ⚠️ **Bắt buộc có `-f 65001`**: file là UTF-8, thiếu tham số này tiếng Việt sẽ lỗi dấu
> (`Hoạt động` → `Hoáº¡t Ä‘á»™ng`) và app báo “Tài khoản đã bị khóa”. Nếu lỡ nạp sai,
> phải tạo lại CSDL bằng đúng lệnh trên (không sửa tay từng ô được vì CHECK constraint
> cũng bị hỏng theo).

Cách khác (không phụ thuộc codepage, đọc file bằng UTF-8):

```bat
java -cp "out;lib/*" tools\ChayScriptSQL.java database\QuanLyNhanSu.sql
```

## 6. Kiểm tra dữ liệu

```bat
sqlcmd -S localhost -U sa -P 123456 -C -d QuanLyNhanSu -Q "SELECT nv.MaNV, nv.HoTen, nv.MaQuanLy, tk.TenDangNhap, tk.VaiTro FROM NHANVIEN nv LEFT JOIN TAIKHOAN tk ON tk.MaNV = nv.MaNV ORDER BY nv.MaNV"
sqlcmd -S localhost -U sa -P 123456 -C -d QuanLyNhanSu -Q "SELECT * FROM CHAMCONG WHERE NgayChamCong = CAST(GETDATE() AS DATE)"
```

## 7. Xử lý sự cố

| Thông báo | Nguyên nhân / cách sửa |
|---|---|
| `Chưa nạp được driver JDBC` | Thiếu file jar trong `lib\`, hoặc chạy sai classpath (phải `-cp "out;lib/*"`) |
| `TCP/IP connection to the host ... has failed` | SQL Server tắt TCP/IP: `SQLServerManager16.msc` → Protocols for MSSQLSERVER → TCP/IP = Enabled, IPAll TCP Port = 1433 → Restart dịch vụ |
| `Login failed for user 'sa'` | Chưa bật Mixed Mode hoặc sai mật khẩu: SSMS → Properties → Security → Mixed Mode; `ALTER LOGIN sa WITH PASSWORD = '...'` |
| `Access denied for user 'root'@'localhost'` | Đang cấu hình MySQL: sửa `db.password` trong `db.properties` |
| App báo “Tài khoản đã bị khóa” | CSDL bị nạp script sai encoding → tạo lại bằng lệnh ở mục 5 |
| `Unknown database` / `Cannot open database` | Chưa nạp script, hoặc sai tên database trong `db.url` |

## 8. Đặt lại mật khẩu tài khoản ứng dụng

```bat
java -cp out tools\BamMatKhau.java 123456
```

Chép câu `UPDATE TAIKHOAN SET MatKhau = '…' WHERE MaNV = '…';` in ra rồi chạy bằng
`sqlcmd -d QuanLyNhanSu -Q "…"` hoặc trong SSMS / Workbench.

## 9. Dọn dẹp (tùy chọn)

```bat
rmdir /s /q out
```

## 10. Nâng cấp CSDL đã có dữ liệu (thêm Giám đốc + cột lương)

Chỉ dùng khi đã có CSDL `QuanLyNhanSu` và muốn thêm vai trò Giám đốc mà không tạo
lại CSDL (chạy **một lần**):

```bat
java -cp "out;lib/*" tools\ChayScriptSQL.java database\mysql\02_giam_doc_luong.sql
```

Tạo CSDL mới thì bỏ qua bước này — `database\mysql\QuanLyNhanSu.sql` đã có sẵn
3 giám đốc, cột lương và vai trò `GiamDoc`.

---

## Phụ lục A — Chuyển sang MySQL

`db.properties` đã đặt sẵn cho MySQL; chỉ còn 2 việc:

1. Tải MySQL Connector/J → chép vào `lib\mysql-connector-j-8.4.0.jar`
2. Sửa **một dòng** trong `db.properties`: `db.password=<mật khẩu MySQL>`

Rồi chạy theo thứ tự:

```bat
chay.bat build
java -cp "out;lib/*" tools\KiemTraKetNoi.java          :: phải hiện Hệ quản trị: MySQL
java -cp "out;lib/*" tools\ChayScriptSQL.java database/mysql/QuanLyNhanSu.sql

:: (tùy chọn) mang dữ liệu thật từ SQL Server sang MySQL
java -cp "out;lib/*" tools\ChuyenDuLieu.java

java "src\Main.java"                                    :: hoặc: java src\Main.java
```

Quay lại SQL Server: bỏ `#` ở 4 dòng SQL Server cuối `db.properties`, thêm `#` vào
4 dòng MySQL.

## Phụ lục B — Bật TCP/IP + Mixed Mode cho SQL Server (nếu cài lại máy)

```bat
SQLServerManager16.msc
:: Protocols for MSSQLSERVER -> TCP/IP = Enabled
:: TCP/IP -> Properties -> IP Addresses -> IPAll: TCP Port = 1433, Dynamic Ports = (trống)
:: SQL Server Services -> SQL Server (MSSQLSERVER) -> Restart
```

Mở cmd **Run as administrator** rồi:

```bat
reg add "HKLM\SOFTWARE\Microsoft\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQLServer" /v LoginMode /t REG_DWORD /d 2 /f
net stop MSSQLSERVER
net start MSSQLSERVER
sqlcmd -S localhost -E -C -Q "ALTER LOGIN sa ENABLE; ALTER LOGIN sa WITH PASSWORD = '123456', CHECK_POLICY = OFF;"
```

---

## Phụ lục C — Nếu bạn NHẬN folder này từ người khác

Mặc định dự án dùng **MySQL**. Driver JDBC đã có sẵn trong `lib\`, không cần tải.

```bat
:: 1) vào thư mục dự án
cd /d <thư mục đã giải nén>

:: 2) điền mật khẩu MySQL vào db.properties (sửa 1 dòng: db.password=...)
notepad db.properties

:: 3) biên dịch
chay.bat build

:: 4) tạo CSDL + dữ liệu mẫu
java -cp "out;lib/*" tools\ChayScriptSQL.java database/mysql/QuanLyNhanSu.sql

:: 5) kiểm tra kết nối (phải THÀNH CÔNG)
java -cp "out;lib/*" tools\KiemTraKetNoi.java

:: 6) chạy
java src\Main.java
```

Đăng nhập: `quanly` / `123456` hoặc `an.nv` / `123456`.

Nếu máy dùng SQL Server thay vì MySQL: bỏ `#` ở 4 dòng SQL Server cuối
`db.properties`, thêm `#` vào 4 dòng MySQL, rồi nạp script
`database\QuanLyNhanSu.sql` bằng `sqlcmd ... -f 65001`.
# Quản lý Nhân sự & Chấm công (Java Swing + JDBC)

Ứng dụng desktop viết **hoàn toàn bằng Java** (Swing + JDBC).
Hỗ trợ **2 hệ CSDL**: **SQL Server** (đang cấu hình sẵn, chạy được ngay) và
**MySQL 8.0** (có sẵn script + cấu hình mẫu, chỉ cần thêm driver và mật khẩu).

---

## 1. Chức năng theo vai trò

| Vai trò | Chức năng |
|---|---|
| **Quản lý** (`quanly`) | Xem **nhân viên cấp dưới**, **thêm nhân viên** (kèm tài khoản), sửa hồ sơ, cho nghỉ việc, **chấm công nhân viên** (theo ngày, chấm hộ vào/ra, xem lịch sử tháng), xem hồ sơ của mình, tự chấm công |
| **Nhân viên** (`an.nv`, …) | Chỉ xem **thông tin của chính mình** (chỉ đọc) và **chấm công của mình** (vào/ra, lịch sử tháng, số ngày công, số lần đi muộn) |

Quan hệ cấp trên – cấp dưới dựa vào cột `NHANVIEN.MaQuanLy`.

---

## 2. Cấu trúc dự án

```
Btapnhom/
├── database/
│   ├── QuanLyNhanSu.sql          Script cho SQL Server (bản đang dùng)
│   └── mysql/QuanLyNhanSu.sql    Script cho MySQL 8.0 (utf8mb4)
├── db.properties                 Cấu hình kết nối (đang trỏ SQL Server)
├── chay.bat                      Biên dịch + chạy
├── src/
│   ├── app/Main.java             Điểm khởi động
│   ├── model/                    NhanVien, TaiKhoan, PhongBan, ChucVu, CaLamViec, ChamCong
│   ├── dao/                      TaiKhoanDAO, NhanVienDAO, ChamCongDAO, DanhMucDAO
│   ├── service/ChamCongService   Nghiệp vụ: đi muộn, về sớm, số giờ làm, trạng thái
│   ├── util/                     KetNoiCSDL, MatKhau (SHA-256), DinhDang
│   └── view/                     LoginFrame, MainFrame, GiaoDien, ThongTinCaNhanPanel,
│                                 NhanVienPanel, NhanVienFormDialog, ChamCongPanel,
│                                 QuanLyChamCongPanel
├── tools/                        KiemTraKetNoi, ChayScriptSQL, BamMatKhau
├── lib/                          Driver JDBC (mssql-jdbc hiện có sẵn)
├── LENH.md                       Danh sách lệnh cần thực hiện
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

## 4. Chuyển sang MySQL (tùy chọn)

MySQL 8.0 đang chạy trên máy (cổng 3306) nhưng chưa có driver Java và chưa biết
mật khẩu `root`, nên cần 3 bước:

**Bước 1** — tải **MySQL Connector/J** (<https://dev.mysql.com/downloads/connector/j/>),
chép vào `lib\`:

```
Btapnhom\lib\mysql-connector-j-8.4.0.jar
```

**Bước 2** — trong `db.properties`: bỏ `#` ở 4 dòng MySQL (cuối file), thêm `#` vào
4 dòng SQL Server, và điền mật khẩu MySQL:

```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/QuanLyNhanSu?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh&allowMultiQueries=true&useSSL=false&allowPublicKeyRetrieval=true
db.user=root
db.password=<mật khẩu MySQL của bạn>
```

**Bước 3** — tạo CSDL. Cách nhanh: mở **MySQL Workbench** → kết nối `localhost:3306`
→ `File → Open SQL Script…` → chọn `database\mysql\QuanLyNhanSu.sql` → **Execute**
(tia chớp). Hoặc bằng dòng lệnh:

```bat
chay.bat build
java -cp "out;lib/*" tools\ChayScriptSQL.java database/mysql/QuanLyNhanSu.sql
java -cp "out;lib/*" tools\KiemTraKetNoi.java
chay.bat
```

---

## 5. Tài khoản mẫu (mật khẩu đều là `123456`)

| Tên đăng nhập | Vai trò | Nhân viên |
|---|---|---|
| `quanly` | Quản lý | NV001 - Trần Văn Quản (Trưởng phòng Kỹ thuật) |
| `an.nv` | Nhân viên | NV002 - Nguyễn Thị An |
| `binh.tt` | Nhân viên | NV003 - Trần Thị Bình |
| `cuong.lh` | Nhân viên | NV004 - Lê Hoàng Cường |

Mật khẩu lưu dạng **SHA-256** (`8d969eef…adc6c92`), so khớp bằng `util.MatKhau.khop()`.
Đặt lại mật khẩu: `java -cp out tools\BamMatKhau.java <mật-khẩu-mới>` rồi chạy câu
`UPDATE TAIKHOAN SET MatKhau = '…'` mà công cụ in ra.

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
| `KiemTraKetNoi.java` | kiểm tra kết nối theo `db.properties` | `java -cp "out;lib/*" tools\KiemTraKetNoi.java` |
| `ChayScriptSQL.java` | nạp file .sql qua JDBC (đọc UTF-8; hỗ trợ cả `GO` và `;`) | `java -cp "out;lib/*" tools\ChayScriptSQL.java database\QuanLyNhanSu.sql` |
| `BamMatKhau.java` | băm mật khẩu để đặt lại | `java -cp out tools\BamMatKhau.java 123456` |

---

## 9. Hướng phát triển thêm

- Đổi mật khẩu trong app, màn hình quản lý tài khoản (xem/đặt lại mật khẩu).
- Nghỉ phép, bảng lương theo tháng từ dữ liệu chấm công.
- Xuất báo cáo Excel/PDF, thống kê theo phòng ban.

---

## 11. Nếu bạn NHẬN folder này từ người khác

Folder đã có sẵn: mã nguồn (`src`), driver SQL Server (`lib\mssql-jdbc-...jar`),
script CSDL (`database\`), file cấu hình (`db.properties`), bản biên dịch (`out`).

### 11.1. Cần cài gì trên máy mới

| Thứ cần | Kiểm tra bằng | Ghi chú |
|---|---|---|
| **JDK 8 trở lên** (hoặc JRE 8+ nếu không sửa code) | `java -version`, `javac -version` | Không có `javac` thì vẫn chạy được bản đã biên dịch trong `out\` |
| **Một hệ CSDL**: SQL Server hoặc MySQL 8 | `sc query MSSQLSERVER` / `sc query MySQL80` | Xem 11.2 hoặc 11.3 |
| (không cần mạng) | | Driver SQL Server đã có sẵn trong `lib\` |

> Chương trình **bắt buộc** phải có CSDL để chạy, vì toàn bộ dữ liệu (nhân viên,
> tài khoản, chấm công) nằm trong CSDL chứ không lưu trong code.

### 11.2. Cách A — dùng SQL Server (dễ nhất vì driver đã kèm sẵn)

1. Cài **SQL Server Express/Developer** (nếu chưa có).
2. Bật kết nối TCP và đăng nhập SQL:
   - `SQLServerManager16.msc` → `Protocols for MSSQLSERVER` → **TCP/IP = Enabled**;
     `IP Addresses` → `IPAll`: `TCP Port = 1433`, `TCP Dynamic Ports` = trống;
     `SQL Server Services` → **Restart**.
   - SSMS → chuột phải instance → `Properties` → `Security` → chọn
     **SQL Server and Windows Authentication mode** → OK → restart dịch vụ.
   - Trong SSMS (đang nối bằng Windows Authentication) chạy:
     ```sql
     ALTER LOGIN sa ENABLE;
     ALTER LOGIN sa WITH PASSWORD = '123456', CHECK_POLICY = OFF;
     ```
3. Nạp CSDL (đường dẫn tuyệt đối cho chắc, **bắt buộc `-f 65001`**):
   ```bat
   sqlcmd -S localhost -U sa -P 123456 -C -f 65001 -i "C:\...\Btapnhom\database\QuanLyNhanSu.sql"
   ```
4. Kiểm tra kết nối rồi chạy:
   ```bat
   cd /d <thư mục đã giải nén>
   java -cp "out;lib/*" tools\KiemTraKetNoi.java     :: phải in THÀNH CÔNG
   chay.bat
   ```
5. Đăng nhập: `quanly` / `123456` hoặc `an.nv` / `123456`.

Nếu mật khẩu `sa` của máy mới khác, sửa `db.password` trong `db.properties` cho khớp.

### 11.3. Cách B — dùng MySQL 8

1. Cài **MySQL 8.0** (hoặc XAMPP).
2. Tải **MySQL Connector/J** (`mysql-connector-j-*.jar`) ở
   <https://dev.mysql.com/downloads/connector/j/> rồi chép vào `lib\`.
3. Trong `db.properties`: bỏ dấu `#` ở 4 dòng MySQL (cuối file), thêm `#` vào 4 dòng
   SQL Server, rồi điền `db.user` / `db.password` của MySQL.
4. Nạp CSDL: MySQL Workbench → `File → Open SQL Script…` →
   `database\mysql\QuanLyNhanSu.sql` → **Execute**. Hoặc:
   ```bat
   chay.bat build
   java -cp "out;lib/*" tools\ChayScriptSQL.java database\mysql\QuanLyNhanSu.sql
   ```
5. Chạy `chay.bat` như trên.

### 11.4. Checklist nhanh cho người nhận

- [ ] `java -version` chạy được
- [ ] `db.properties` có đúng `db.url`, `db.user`, `db.password` của máy mình
- [ ] Đã nạp script CSDL (có database `QuanLyNhanSu`)
- [ ] `java -cp "out;lib/*" tools\KiemTraKetNoi.java` in `KẾT NỐI CSDL   : THÀNH CÔNG`
- [ ] `chay.bat` mở được màn hình đăng nhập và đăng nhập được

### 11.5. Lỗi thường gặp khi chạy trên máy mới

| Hiện tượng | Cách xử lý |
|---|---|
| `Chưa nạp được driver JDBC` | Thiếu file jar trong `lib\`, hoặc chạy sai classpath (phải `-cp "out;lib/*"`) |
| `TCP/IP connection to the host ... has failed` | Chưa bật TCP/IP cho SQL Server (bước 11.2.2) |
| `Login failed for user 'sa'` | Chưa bật Mixed Mode hoặc sai mật khẩu `sa` |
| `Cannot open database "QuanLyNhanSu"` | Chưa nạp script CSDL |
| App báo **“Tài khoản đã bị khóa”** | CSDL bị nạp script sai encoding (thiếu `-f 65001`) → nạp lại bằng lệnh ở 11.2.3 |
| Chữ tiếng Việt trong app bị lỗi dấu | Chạy trên Windows có font Segoe UI; nếu vẫn lỗi, kiểm tra console đang dùng mã UTF-8 |

### 11.6. Trước khi nén gửi đi

- Có thể xóa thư mục `out\` cho gọn (người nhận chạy `chay.bat` sẽ tự biên dịch lại).
- **Đừng** gửi file `QuanliNhansu.sql` ở thư mục gốc (đây là file cũ, không dùng).
- Cần gửi đủ: `src\`, `lib\` (có file jar), `database\`, `db.properties`, `chay.bat`,
  `README.md`, `LENH.md`, `CLEANCODE.md` (và `tools\` nếu muốn dùng công cụ kiểm tra).
- Không cần gửi: `out\` (bản biên dịch) — nhưng gửi cũng không sao, người nhận chạy được ngay.


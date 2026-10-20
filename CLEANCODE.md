# Quy ước viết code của dự án (dành cho người mới học Java & SQL)

File này ghi lại **cách viết code** trong dự án, để cả nhóm viết giống nhau và
người mới đọc là hiểu. Mọi file trong `src/` đều tuân theo các quy ước dưới đây.

---

## 1. Chương trình chạy từ đâu?

```
src/Main.java   ← điểm bắt đầu, chạy class này
```

Luồng chạy:

```
Main  →  LoginFrame (đăng nhập)  →  MainFrame (màn hình chính)
                                     ├── ThongTinCaNhanPanel  (hồ sơ của tôi)
                                     ├── NhanVienPanel        (quản lý cấp dưới - chỉ quản lý)
                                     ├── ChamCongPanel        (chấm công của tôi)
                                     └── QuanLyChamCongPanel  (chấm công nhân viên - chỉ quản lý)
```

Các tầng (layer) và nhiệm vụ:

| Tầng | Thư mục | Nhiệm vụ | Ví dụ |
|---|---|---|---|
| Giao diện | `src/view` | Hiển thị, nhận thao tác của người dùng | `LoginFrame`, `NhanVienPanel` |
| Nghiệp vụ | `src/service` | Tính toán, quy tắc (không liên quan CSDL) | `ChamCongService` |
| Truy vấn CSDL | `src/dao` | Chỉ chứa câu lệnh SQL | `NhanVienDAO` |
| Dữ liệu | `src/model` | Lớp chứa dữ liệu, gần giống 1 bảng | `NhanVien`, `ChamCong` |
| Tiện ích | `src/util` | Việc dùng chung: kết nối, băm mật khẩu, định dạng | `KetNoiCSDL`, `MatKhau` |

**Quy tắc một chiều**: `view` gọi `dao`/`service`; `dao` dùng `model` và `util`.
Không bao giờ để `dao` gọi ngược lên `view`.

---

## 2. Đặt tên

| Loại | Quy ước | Ví dụ |
|---|---|---|
| Lớp (class) | Danh từ, viết hoa chữ đầu mỗi từ | `NhanVien`, `ChamCongPanel` |
| Phương thức | Động từ + đối tượng, camelCase | `layTheoMa()`, `chamCongVao()` |
| Biến | Danh từ, camelCase, tiếng Việt không dấu | `hoTen`, `ngayChamCong`, `danhSachNhanVien` |
| Hằng số | Viết HOA toàn bộ | `PHUT_DUNG_SAI`, `DANG_LAM` |
| Bảng/cột SQL | Viết HOA, không dấu | `NHANVIEN`, `NgayChamCong` |

Tên phải nói **ý nghĩa**, không viết tắt tuỳ tiện:
`nv`, `cc`, `pb` chỉ dùng cho mã (`MaNV`, `MaPB`) vì đó là quy ước CSDL;
biến trong code thì viết đủ: `nhanVien`, `chamCong`, `phongBan`.

---

## 3. Cấu trúc một file

```java
package view;                       // 1. khai báo gói

import java.sql.SQLException;       // 2. import những gì cần dùng

/**
 * 3. Một câu mô tả lớp này làm gì.
 *    Ghi rõ ai gọi nó, dùng cho ai (quản lý / nhân viên).
 */
public class ViDuPanel extends JPanel {

    // 4. Hằng số (static final)
    private static final int SO_COT = 5;

    // 5. Các thành phần giao diện / DAO (final nếu không đổi)
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    // 6. Hàm tạo: gọi các hàm nhỏ để dựng giao diện
    public ViDuPanel() {
        taoGiaoDien();
        napDuLieu();
    }

    // 7. Các hàm nhỏ, mỗi hàm một việc
    private void taoGiaoDien() { ... }

    private void napDuLieu() { ... }
}
```

Thứ tự khai báo luôn là: hằng số → biến → hàm tạo → phương thức công khai →
phương thức riêng → phương thức tĩnh (static).

---

## 4. Comment (chú thích) — giữ tối thiểu

Mã nguồn hiện tại **không dùng comment** để code gọn, dễ đọc; tên biến và tên
phương thức tự giải thích ý nghĩa:

```java
public boolean isDangLam() {
    return DANG_LAM.equalsIgnoreCase(trangThai);
}
```

Quy ước khi cần thêm ghi chú:

1. Chỉ comment khi **lý do** không thể hiện được qua code (ví dụ: một hằng số
   nghiệp vụ, một đoạn xử lý đặc biệt của CSDL).
2. Không comment cho getter/setter/hàm tạo — đọc tên là hiểu.
3. Không comment lặp lại code: `i++; // tăng i` là vô nghĩa.
4. Nếu cần ghi chú, viết **tiếng Việt có dấu**, ngắn (1 dòng).
5. Ưu tiên **đổi tên biến cho rõ** thay vì thêm comment giải thích.

---

## 5. Phương thức ngắn, một việc

- Một phương thức chỉ nên làm **một việc**, dài dưới ~30 dòng.
- Nếu thấy chỗ nào quá dài, tách thành hàm nhỏ, đặt tên theo việc nó làm.
  Ví dụ trong `ChamCongPanel`: `taoPhanDau()`, `taoBang()`, `capNhatTrangThaiNut()`.
- Tham số nên ≤ 4. Nếu cần nhiều hơn, gom thành một đối tượng (`model`).

---

## 6. Quy ước viết SQL (trong tầng `dao`)

1. **Luôn dùng `PreparedStatement`** với dấu `?` — để tránh SQL Injection và
   tránh lỗi khi dữ liệu có dấu nháy:

```java
String sql = "SELECT * FROM NHANVIEN WHERE MaNV = ?";
```

2. Câu SQL viết **hoa từ khoá**, mỗi mệnh đề một dòng cho dễ đọc:

```java
private static final String SQL_TIM_THEO_MA =
        "SELECT MaNV, HoTen, TrangThai "
        + "FROM NHANVIEN "
        + "WHERE MaNV = ?";
```

3. Đặt câu SQL thành **hằng số** ở đầu lớp, không viết rải trong thân hàm.
4. `SELECT` chỉ nêu những cột cần dùng (đừng dùng `SELECT *` trong mã nộp bài).
5. Khi đọc kết quả, đọc theo **tên cột**: `duLieu.getString("HoTen")` — dễ hiểu
   hơn đọc theo số thứ tự.
6. Câu lệnh làm thay đổi dữ liệu (`INSERT`, `UPDATE`, `DELETE`) nên chạy trong
   **transaction** nếu đi thành nhóm (xem `NhanVienDAO.themKemTaiKhoan`).
7. Mỗi bảng có 1 lớp DAO tương ứng, mỗi DAO có các hàm: `layTatCa`,
   `layTheoMa`, `them`, `capNhat`, `xoa`.

---

## 7. Xử lý lỗi

- Lỗi CSDL: `throws SQLException` ở tầng DAO, tầng giao diện bắt lại và hiển thị
  bằng `GiaoDien.loiKetNoi(this, loi)` — câu thông báo đã được viết lại cho dễ hiểu.
- Lỗi dữ liệu người dùng nhập: kiểm tra **trước khi** gọi CSDL, báo bằng nhãn đỏ
  hoặc `GiaoDien.canhBao(...)`.
- Không dùng `catch (Exception e) {}` rỗng (nuốt lỗi).

---

## 8. Giao diện (Swing) — mẫu chung

Mọi panel đều theo cùng một khuôn:

```java
setLayout(new BorderLayout(0, 10));       // bố cục ngoài
setBorder(new EmptyBorder(14, 14, 14, 14)); // lề
add(taoPhanDau(), BorderLayout.NORTH);    // tiêu đề + thanh công cụ
add(GiaoDien.cuon(bang), BorderLayout.CENTER); // bảng dữ liệu
add(taoPhanCuoi(), BorderLayout.SOUTH);   // dòng trạng thái
```

- Dùng lại các hàm trong `view/GiaoDien` (`GiaoDien.nut(...)`, `GiaoDien.nhan(...)`,
  `GiaoDien.tieuDe(...)`, `GiaoDien.loi(...)`) để giao diện đồng nhất.
- Bảng dữ liệu: `DefaultTableModel` chỉ đọc + `napBang()` để đổ dữ liệu.
- Sự kiện: viết ngắn gọn `nutThem.addActionListener(e -> themNhanVien());`
  (`e -> ...` là lambda: "khi bấm nút thì gọi hàm này").

---

## 9. Kiểm tra trước khi nộp

```bat
java src\Main.java                              :: JDK 22+: chạy thẳng file nguồn (không cần -cp)
chay.bat build                                  :: phải biên dịch không lỗi
java -cp "out;lib/*" tools\KiemTraKetNoi.java   :: phải in KẾT NỐI CSDL: THÀNH CÔNG
chay.bat                                        :: đăng nhập quanly / 123456
```

Checklist tự kiểm tra:

- [ ] Đăng nhập sai mật khẩu có báo lỗi đúng, không bị treo.
- [ ] Nhân viên `an.nv` **không** thấy menu quản lý nhân viên / chấm công nhân viên.
- [ ] Quản lý `quanly` thấy 4 nhân viên cấp dưới.
- [ ] Thêm nhân viên mới → đăng nhập được bằng tài khoản vừa tạo.
- [ ] Chấm công vào rồi chấm công ra → trạng thái đổi đúng (Đủ ca / Đi muộn / Về sớm).
- [ ] Không có `System.out.println` rác trong mã nộp bài.

---

## 10. Muốn thêm một chức năng mới? Làm theo 4 bước

Ví dụ thêm chức năng "in danh sách nhân viên ra màn hình":

1. **CSDL**: nếu cần cột mới → sửa script trong `database/`.
2. **model**: thêm thuộc tính + getter/setter trong lớp tương ứng.
3. **dao**: thêm câu SQL và hàm (ví dụ `layTatCa()`), trả về `List<NhanVien>`.
4. **view**: thêm nút trong `taoPhanDau()`, viết hàm xử lý, gọi DAO, bắt
   `SQLException` và hiển thị kết quả.

Không cần sửa `Main.java` nếu chức năng nằm trong màn hình đã có.

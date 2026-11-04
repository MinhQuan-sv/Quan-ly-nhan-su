# Chức năng của từng hàm trong dự án

Tài liệu tra cứu: mỗi lớp là một mục, mỗi hàm một dòng giải thích.
Getters/setters của lớp `model` được gộp chung (vì cùng một việc: đọc/ghi thuộc tính).

---

## 1. `Main.java` — điểm khởi động

| Hàm | Chức năng |
|---|---|
| `main(String[] args)` | Điểm bắt đầu: cài giao diện → kiểm tra kết nối CSDL → mở `LoginFrame`. Nếu thiếu driver JDBC thì hiện hộp thoại báo. |
| `caiDatGiaoDien()` | Đặt Look & Feel theo Windows và font Segoe UI 13 cho toàn bộ thành phần (để chữ tiếng Việt hiển thị đẹp). |

---

## 2. `model` — lớp dữ liệu (ánh xạ bảng CSDL)

Cả 6 lớp đều có: **hàm tạo rỗng** + **hàm tạo đầy đủ**, và các cặp
`getXxx()/setXxx()` để đọc/ghi từng thuộc tính (ví dụ `getHoTen()`, `setHoTen(...)`).

### `NhanVien`
| Hàm | Chức năng |
|---|---|
| `isDangLam()` | `true` nếu trạng thái là "Đang làm". |
| `getLuong()/setLuong(BigDecimal)` | Đọc/ghi mức lương hiện tại (cột `NHANVIEN.Luong`). |
| `getTenHienThi()` | Trả về họ tên; nếu họ tên null thì trả về mã nhân viên (để hiển thị không bị trống). |
| `hashCode()` / `equals()` | Hai nhân viên bằng nhau khi cùng `maNV` (dùng được trong `List`, `Set`). |
| `toString()` | Chuỗi mô tả nhanh: mã, tên, phòng, chức vụ, cấp trên, trạng thái. |

### `TaiKhoan`
| Hàm | Chức năng |
|---|---|
| `isQuanLy()` | `true` nếu `vaiTro` = "QuanLy" — dùng để phân quyền giao diện. |
| `isGiamDoc()` | `true` nếu `vaiTro` = "GiamDoc" — mở giao diện giám đốc. |
| `isHoatDong()` | `true` nếu `trangThai` = "Hoạt động" (tài khoản được phép đăng nhập). |
| `getTenVaiTro()` | Trả về "Giám đốc" / "Quản lý" / "Nhân viên" để hiển thị. |
| `toString()` | Mô tả tài khoản, **không in mật khẩu**. |

### `ChamCong`
| Hàm | Chức năng |
|---|---|
| `isDaVao()` | Đã có giờ vào chưa. |
| `isDaRa()` | Đã có giờ ra chưa. |
| `isHoanTat()` | Đã có cả giờ vào và giờ ra (ngày công hoàn tất). |

### `PhongBan`, `ChucVu`, `CaLamViec`
| Hàm | Chức năng |
|---|---|
| `toString()` | Trả chuỗi cho `JComboBox` hiển thị, ví dụ `"PB01 - Phòng Kỹ thuật"`, `"CA01 - Ca hành chính (08:00 - 17:00)"`. |
| `hashCode()` / `equals()` | So sánh theo mã (`maPB` / `maCV` / `maCa`). |

---

## 3. `util` — tiện ích dùng chung

### `KetNoiCSDL` (quản lý kết nối)
| Hàm | Chức năng |
|---|---|
| `ketNoi()` | Mở kết nối tới database trong `db.properties`. |
| `ketNoi(String tenDatabase)` | Mở kết nối tới database khác (ví dụ `master`/`mysql`) — dùng cho công cụ tạo CSDL. |
| `kiemTraKetNoi()` | Thử kết nối; trả `null` nếu OK, ngược lại trả câu thông báo lỗi dễ hiểu. |
| `tenHeQuanTri()` | Cho biết đang cấu hình `MySQL` hay `SQLServer` (đọc từ driver/URL). |
| `tenDatabase()` | Lấy tên database trong URL (để hiển thị khi chẩn đoán). |
| `tenTaiKhoan()` | Lấy `db.user` (hoặc "(Windows Authentication)" nếu để trống). |
| `moKetNoi(urlDung)` *(private)* | Mở kết nối thật: nếu driver nạp từ `lib` thì gọi trực tiếp driver, ngược lại dùng `DriverManager`. |
| `napDriver()` *(private)* | Nạp driver: thử `Class.forName` trước, không được thì tìm trong `lib`. |
| `timDriverTrongThuMucLib()` *(private)* | Quét `lib/*.jar`, tạo `URLClassLoader`, nạp lớp driver và trả về đối tượng `Driver` (nhờ đó chạy `java src/Main.java` không cần `-cp`). |
| `docCauHinhNeuCan()` *(private)* | Đọc `db.properties` đúng một lần rồi lưu vào biến static. |
| `napFileCauHinh(...)` *(private)* | Đọc file `db.properties` ở thư mục chạy, nếu không có thì lấy trong classpath. |
| `thayTenDatabase(url, ten)` *(private)* | Đổi tên database trong chuỗi kết nối (hỗ trợ cả 2 kiểu URL). |
| `moTaLoi(SQLException)` | Dịch lỗi JDBC thành hướng dẫn sửa: thiếu driver, sai mật khẩu, chưa thay mật khẩu mẫu, chưa tạo CSDL, lỗi TLS… |

### `MatKhau` (băm mật khẩu)
| Hàm | Chức năng |
|---|---|
| `bam(String matKhau)` | Băm mật khẩu bằng SHA-256, trả chuỗi hex 64 ký tự. |
| `khop(matKhauNhap, matKhauDaLuu)` | Băm lại mật khẩu nhập rồi so với mã băm trong CSDL; trả `true` nếu khớp. |

### `DinhDang` (định dạng/đọc dữ liệu)
| Hàm | Chức năng |
|---|---|
| `ngay(LocalDate)` | Ngày → `"dd/MM/yyyy"` (null → chuỗi rỗng). |
| `gio(LocalTime)` | Giờ → `"HH:mm"`. |
| `docNgay(String)` | `"dd/MM/yyyy"` → `LocalDate`; rỗng → null; sai định dạng → ném `IllegalArgumentException`. |
| `docGio(String)` | `"HH:mm"` → `LocalTime` (cùng quy tắc như trên). |
| `so(double, int)` | In số thực với số chữ số thập phân, ví dụ `7,50`. |
| `soNguyen(int)` | In số nguyên có phân cách nghìn: `1.234`. |
| `tien(BigDecimal)` | In số tiền có phân cách nghìn: `12.000.000`. |

---

## 4. `dao` — nơi duy nhất chứa câu lệnh SQL

### `TaiKhoanDAO`
| Hàm | Chức năng |
|---|---|
| `dangNhap(tenDangNhap, matKhau)` | Lấy tài khoản theo tên đăng nhập, so mật khẩu bằng `MatKhau.khop`; trả tài khoản nếu đúng, `null` nếu sai tên/mật khẩu. Tài khoản bị khoá vẫn trả về (để giao diện báo đúng lý do), nhưng đã xoá mã băm. |
| `tenDangNhapDaTonTai(ten)` | `true` nếu tên đăng nhập đã có người dùng (kiểm tra trước khi tạo tài khoản). |
| `doiMatKhau(tenDangNhap, matKhauMoi)` | Đổi mật khẩu: băm SHA-256 mật khẩu mới rồi `UPDATE` (dùng cho chức năng Đổi mật khẩu). |
| `capNhatVaiTro(maNV, vaiTro)` | Đổi vai trò tài khoản (`GiamDoc`/`QuanLy`/`NhanVien`). |
| `docTaiKhoan(ResultSet, ten)` *(private)* | Chuyển một dòng kết quả thành đối tượng `TaiKhoan`. |

### `NhanVienDAO`
| Hàm | Chức năng |
|---|---|
| `layCapDuoi(maQuanLy)` | Danh sách nhân viên có `MaQuanLy` = mã quản lý (kèm tên phòng ban, chức vụ, tên cấp trên qua JOIN). |
| `timCapDuoi(maQuanLy, tuKhoa)` | Như trên nhưng lọc theo từ khoá (mã, họ tên, điện thoại, email) bằng `LIKE`. |
| `layTheoMa(maNV)` | Lấy một nhân viên theo mã; `null` nếu không có. |
| `demCapDuoi(maQuanLy)` | Đếm số nhân viên cấp dưới trực tiếp. |
| `layTatCaQuanLy()` | Danh sách **mọi quản lý** trong công ty (JOIN `TAIKHOAN` với `VaiTro = 'QuanLy'`) — dùng cho màn hình giám đốc. |
| `tangLuong(maNV, luongMoi)` | Cập nhật lương mới; báo lỗi nếu lương ≤ 0 hoặc không tìm thấy nhân viên. |
| `thoiViec(maNV)` | **Transaction**: nhân viên → "Đã nghỉ" và tài khoản → "Bị khóa". |
| `thangChuc(maNV, maCVQuanLy)` | **Transaction**: đổi chức vụ sang `CV01` (Trưởng phòng) và vai trò tài khoản sang `QuanLy`. |
| `MA_CV_QUAN_LY` (hằng số) | Mã chức vụ quản lý dùng khi thăng chức (`CV01`). |
| `sinhMaNhanVien()` | Sinh mã kế tiếp dạng `NVxxx`: đọc các mã hiện có, lấy số lớn nhất + 1. |
| `themKemTaiKhoan(nv, tenDangNhap, matKhau, vaiTro)` | **Transaction**: thêm nhân viên + tạo tài khoản (mật khẩu được băm). Lỗi giữa chừng thì `rollback` để không có nhân viên "mồ côi". |
| `capNhat(nv)` | Cập nhật hồ sơ (không đổi mã nhân viên, không đổi cấp trên). |
| `doiTrangThai(maNV, trangThai)` | Đổi trạng thái làm việc (Đang làm / Tạm nghỉ / Đã nghỉ). |
| `cccdDaTonTai(cccd, maNVDangSua)` | Kiểm tra CCCD đã thuộc nhân viên **khác** chưa. |
| `emailDaTonTai(email, maNVDangSua)` | Kiểm tra email đã thuộc nhân viên khác chưa. |
| `demGiaTriTrung(...)` *(private)* | Hàm dùng chung cho 2 hàm kiểm tra trùng ở trên. |
| `docNhanVien(ResultSet)` *(private)* | Chuyển dòng dữ liệu thành đối tượng `NhanVien`. |
| `ganThongTinNhanVien(lenh, nv)` *(private)* | Gán 14 giá trị của nhân viên (gồm lương) vào 14 dấu `?` của câu INSERT. |
| `ganNgay(lenh, chiSo, ngay)` / `docNgay(rs, cot)` *(private)* | Ghi/đọc cột kiểu DATE (null được ghi là `NULL`). |

### `ChamCongDAO`
| Hàm | Chức năng |
|---|---|
| `layTheoNgay(maNV, ngay)` | Bản ghi chấm công của một nhân viên trong một ngày (`null` nếu chưa chấm). |
| `layTheoThang(maNV, thang, nam)` | Toàn bộ bản ghi của nhân viên trong tháng, sắp theo ngày. |
| `layTheoNgayCuaNhom(maQuanLy, ngay)` | Chấm công trong ngày của **tất cả nhân viên cấp dưới** (quản lý dùng). |
| `luu(chamCong)` | Lưu kiểu "upsert": `UPDATE` theo (MaNV, Ngày); nếu 0 dòng bị ảnh hưởng thì `INSERT`. Nhờ vậy chấm vào và chấm ra dùng chung một bản ghi/ngày. |
| `demNgayCong(maNV, thang, nam)` | Đếm số ngày công (ngày có giờ vào) trong tháng. |
| `demLanDiMuon(maNV, thang, nam)` | Đếm số lần có trạng thái chứa "Đi muộn". |
| `dem(...)` *(private)* | Hàm đếm dùng chung, có thể chèn thêm tham số `LIKE`. |
| `docChamCong(ResultSet)` *(private)* | Chuyển dòng dữ liệu thành `ChamCong` (kèm đối tượng `CaLamViec` nếu có). |
| `ganNgay` / `ganGio` / `docNgay` / `docGio` *(private)* | Ghi/đọc cột DATE và TIME, xử lý `NULL`. |

### `DanhMucDAO`
| Hàm | Chức năng |
|---|---|
| `layPhongBan()` | Danh sách phòng ban (sắp theo tên) để đổ vào JComboBox. |
| `layChucVu()` | Danh sách chức vụ. |
| `layCaLamViec()` | Danh sách ca làm việc (sắp theo giờ bắt đầu). |
| `layCa(maCa)` | Lấy một ca theo mã. |
| `docCaLamViec(ResultSet)` *(private)* | Chuyển dòng dữ liệu thành `CaLamViec` (đổi `java.sql.Time` → `LocalTime`). |

---

## 5. `service.ChamCongService` — quy tắc nghiệp vụ (không dùng CSDL)

| Hàm | Chức năng |
|---|---|
| `phutDiMuon(ca, gioVao)` | Số phút vào muộn so với giờ bắt đầu ca; trong mức dung sai 5 phút thì trả 0. |
| `phutVeSom(ca, gioRa)` | Số phút ra sớm so với giờ kết thúc ca; trong dung sai 5 phút thì trả 0. |
| `soGioLam(gioVao, gioRa)` | Số giờ làm = (giờ ra − giờ vào), làm tròn 2 chữ số; thiếu dữ liệu → 0. |
| `xacDinhTrangThai(ca, gioVao, gioRa)` | Trả trạng thái: `Vắng mặt` / `Đang làm` / `Đi muộn` / `Về sớm` / `Đi muộn, về sớm` / `Đủ ca`. |
| `moTa(ca, gioVao, gioRa)` | Câu mô tả ngắn, ví dụ "Đi muộn 20 phút, về sớm 10 phút". |

---

## 6. `view` — giao diện

### `GiaoDien` (bộ thành phần dùng chung)
| Hàm | Chức năng |
|---|---|
| `nut(nhan, mauNen)` | Tạo nút phẳng (chữ trắng, nền màu, con trỏ bàn tay). |
| `tieuDe(nhan)` / `nhan(nhan)` / `giaTri(nhan)` | Tạo nhãn tiêu đề / nhãn đậm / nhãn giá trị. |
| `thanhCongCu()` | Panel ngang chứa nút và ô nhập. |
| `truong(soCot)` / `combo(mang)` | Tạo ô nhập / hộp chọn có sẵn font. |
| `moHinhBang(tenCot)` | Tạo model bảng **chỉ đọc**; hàm `isCellEditable` bên trong luôn trả `false`. |
| `bang(model)` / `cuon(bang)` | Tạo JTable đã cấu hình font, chiều cao dòng / bọc vào khung cuộn. |
| `canhPhai(bang, cot...)` | Căn phải các cột số. |
| `the(nhan, lblGiaTri, mau)` / `hangThe(...)` | Tạo thẻ số liệu / xếp nhiều thẻ thành một hàng. |
| `tin` / `canhBao` / `loi` | Hộp thoại thông báo / cảnh báo / lỗi. |
| `xacNhan(cha, cauHoi)` | Hộp thoại Có/Không; `true` nếu chọn Yes. |
| `loiKetNoi(cha, loi)` | Hiện lỗi CSDL kèm hướng dẫn (lấy từ `KetNoiCSDL.moTaLoi`). |

### `LoginFrame` (đăng nhập)
| Hàm | Chức năng |
|---|---|
| `LoginFrame(loiKetNoi)` | Dựng cửa sổ đăng nhập; hiện tình trạng CSDL ở chân màn hình. |
| `taoNoiDung()` / `taoPhanDau()` / `taoForm()` / `taoPhanCuoi()` | Dựng các phần: khung chung, tiêu đề, form nhập, chân màn hình. |
| `capNhatTrangThaiKetNoi(loi)` | Đổi màu/chữ ở chân màn hình (xanh = kết nối được, đỏ = lỗi); gắn sự kiện bấm để xem chi tiết. |
| `mouseClicked(...)` | Bấm vào dòng tình trạng CSDL → hiện hộp thoại chi tiết lỗi. |
| `khiBamDangNhap(ActionEvent)` | Xử lý khi bấm nút ĐĂNG NHẬP. |
| `xuLyDangNhap()` | 6 bước: đọc dữ liệu → kiểm tra rỗng → hỏi CSDL (`TaiKhoanDAO.dangNhap`) → kiểm tra bị khoá → lấy hồ sơ nhân viên → mở `MainFrame`. |

### `MainFrame` (màn hình chính)
| Hàm | Chức năng |
|---|---|
| `MainFrame(taiKhoan, nhanVien)` | Dựng menu, thanh bên, vùng nội dung theo **vai trò** (Giám đốc / Quản lý / Nhân viên), dựng giao diện một lần. |
| `taoThanhMenu()` | Tạo menu Hệ thống / Chức năng / Trợ giúp. |
| `taoMuc(nhan, tenThe)` | Tạo mục menu mở một thẻ chức năng. |
| `taoPhanDau()` | Thanh tiêu đề: tên app, người đang đăng nhập, nút Đăng xuất. |
| `taoThanhBen()` / `taoNutMenu(nhan, tenThe)` | Thanh bên: các nút chức năng (thêm nút giám đốc / quản lý tùy vai trò). |
| `taoNoiDung()` | Tạo các panel và gắn vào `CardLayout` theo tên thẻ. |
| `taoThanhTrangThai()` | Thanh dưới cùng chứa đồng hồ. |
| `hienThi(tenThe)` | Mở thẻ chức năng, tô màu nút đang chọn, cập nhật dòng trạng thái. |
| `tenChucNang(tenThe)` | Đổi tên thẻ thành tên tiếng Việt để hiển thị. |
| `dangXuat()` | Hỏi xác nhận → đóng cửa sổ → mở lại `LoginFrame`. |
| `batDauDongHo()` / `capNhatDongHo()` | Hẹn giờ 1 giây/lần và cập nhật giờ hiện tại lên thanh trạng thái. |
| `moDoiMatKhau()` | Mở `DoiMatKhauDialog` cho tài khoản đang đăng nhập (mọi vai trò). |
| `CARD_GIAM_DOC` (hằng số) | Tên thẻ chức năng "Quản lý toàn công ty" của giám đốc. |

### `ThongTinCaNhanPanel` (hồ sơ của tôi)
| Hàm | Chức năng |
|---|---|
| `ThongTinCaNhanPanel(taiKhoan, nhanVien)` | Dựng panel và nạp dữ liệu. |
| `taoPhanDau()` / `taoHoSo()` / `taoPhanCuoi()` | Dựng tiêu đề / khung hồ sơ / nút Làm mới + dòng trạng thái. |
| `taoGiaTri()` | Tạo nhãn giá trị mới. |
| `themTieuMuc` / `themCap` / `themDongDai` | Ba hàm xếp hàng vào `GridBagLayout`: một tiêu mục, một cặp nhãn–giá trị (2 cặp/hàng), một dòng dài. |
| `taiLai()` | Đọc lại hồ sơ + đếm số cấp dưới từ CSDL rồi vẽ lên nhãn. |
| `capNhatHienThi()` | Đưa dữ liệu của `nhanVien` hiện tại lên các nhãn (null → "--"). |
| `hoac(...)` × 3 | Ba hàm chọn giá trị hiển thị: ưu tiên giá trị đầu, dự phòng giá trị sau, cuối cùng là câu mặc định. |

### `NhanVienPanel` (nhân viên cấp dưới)
| Hàm | Chức năng |
|---|---|
| `NhanVienPanel(quanLy)` | Dựng panel, gắn sự kiện nháy đúp, nạp dữ liệu. |
| `mouseClicked(...)` | Nháy đúp một dòng → mở form sửa. |
| `taoPhanDau()` | Tiêu đề + thanh công cụ (tìm kiếm, lọc trạng thái, 4 nút). |
| `insertUpdate` / `removeUpdate` / `changedUpdate` | Ba hàm của `DocumentListener`: mỗi khi nội dung ô tìm kiếm đổi → lọc lại bảng. |
| `taoPhanCuoi()` | Dòng đếm và dòng trạng thái. |
| `taiLai()` | Tải danh sách cấp dưới (và danh mục phòng ban/chức vụ lần đầu) rồi vẽ bảng. |
| `napBang()` | Lọc theo từ khoá + trạng thái, đổ dữ liệu vào bảng (11 cột, gồm Lương), cập nhật dòng đếm. |
| `khopTuKhoa(nv, tuKhoa)` / `chua(giaTri, tuKhoa)` | Kiểm tra một nhân viên có khớp từ khoá không (mã, tên, điện thoại, email). |
| `hoac(giaTri)` | null → chuỗi rỗng khi hiển thị. |
| `themNhanVien()` | Sinh mã mới → mở form THÊM → nếu đã lưu thì tải lại bảng. |
| `suaNhanVien()` | Mở form SỬA cho dòng đang chọn → nếu đã lưu thì tải lại. |
| `choNghiViec()` | Hỏi xác nhận rồi đổi trạng thái nhân viên sang "Đã nghỉ". |
| `nhanVienDangChon()` | Đổi dòng đang chọn trong bảng thành đối tượng `NhanVien`. |

### `NhanVienFormDialog` (form thêm/sửa)
| Hàm | Chức năng |
|---|---|
| `NhanVienFormDialog(...)` | Dựng hộp thoại theo chế độ THÊM/SUA, đổ danh mục vào combo, điền dữ liệu ban đầu. |
| `isDaLuu()` | `true` nếu đã lưu thành công (panel cha dùng để quyết định tải lại bảng). |
| `dienDuLieuBanDau(maNVMoi)` | THÊM: điền mã mới, ngày vào làm hôm nay, tài khoản gợi ý; SỬA: điền hồ sơ hiện có. |
| `chonPhongBan(maPB)` / `chonChucVu(maCV)` | Chọn đúng mục trong JComboBox theo mã. |
| `taoNoiDung()` / `themTruong(...)` | Dựng form (ô nhập + nhãn) và 2 nút Hủy/Lưu. |
| `luu()` | Kiểm tra dữ liệu từng trường → kiểm tra logic ngày → gọi `themMoi` hoặc `capNhat`; lỗi thì hiện ở nhãn đỏ. |
| `themMoi(nv)` | Kiểm tra mật khẩu (≥6, nhập lại khớp), CCCD/email/tên đăng nhập trùng → gọi DAO thêm nhân viên + tài khoản. |
| `capNhat(nv)` | Kiểm tra trùng CCCD/email với người khác → gọi DAO cập nhật. |
| `kiemTraNgayThang(nv)` | Ngày sinh/ngày vào làm không ở tương lai; vào làm phải sau ngày sinh. |
| `kiemTraBatBuoc` / `kiemTraCccd` / `kiemTraDienThoai` / `kiemTraEmail` | Kiểm tra trường bắt buộc, CCCD (9/12 số), điện thoại (9–11 số), email đúng dạng. |
| `docLuong(chuoi)` | Đọc ô lương thành `BigDecimal`; bỏ dấu chấm/phẩy phân cách, rỗng = 0, không phải số thì báo lỗi. |
| `rongThanhNull(giaTri)` | Chuỗi rỗng/toàn khoảng trắng → `null` (để CSDL lưu NULL). |
| `thongDiepLoi(SQLException)` | Dịch lỗi SQL thành câu dễ hiểu (đặc biệt lỗi trùng UNIQUE 2627/2601). |

### `ChamCongPanel` (chấm công của tôi)
| Hàm | Chức năng |
|---|---|
| `ChamCongPanel(nhanVien)` | Dựng panel, chọn tháng/năm hiện tại, nạp dữ liệu. |
| `taoPhanDau()` | Tiêu đề + thanh công cụ (chọn ca, tháng, năm, nút Xem/Chấm vào/Chấm ra). |
| `taoNoiDung()` / `taoKhuHomNay()` / `taoO(...)` | Thẻ "tình hình hôm nay" + 3 thẻ số liệu + bảng lịch sử. |
| `taoPhanCuoi()` | Dòng trạng thái. |
| `taiLai()` | Nạp danh mục ca (lần đầu), lịch sử tháng, bản ghi hôm nay → vẽ bảng/thẻ/nút. |
| `napBang()` | Đổ lịch sử tháng vào bảng (ngày, ca, giờ vào/ra, số giờ, trạng thái, ghi chú). |
| `capNhatKhuHomNay()` | Cập nhật thẻ "hôm nay" (ngày, ca, giờ vào, giờ ra, trạng thái). |
| `capNhatThongKe(thang, nam)` | Tính tổng giờ làm trong tháng + số ngày công + số lần đi muộn. |
| `capNhatTrangThaiNut()` | Bật/tắt nút: chưa vào → chỉ bấm được "vào"; đã vào chưa ra → bấm được "ra". |
| `chamCongVao()` | Ghi giờ hiện tại làm giờ vào, tính trạng thái + ghi chú, lưu (không cho chấm 2 lần). |
| `chamCongRa()` | Ghi giờ ra, tính lại trạng thái/số giờ, lưu (phải chấm vào trước). |
| `caDangChon()` | Lấy ca trong combo; chưa có ca thì báo lỗi. |
| `docNam()` | Đọc ô năm; sai định dạng thì báo lỗi và trả null. |
| `hoac(giaTri)` | null/rỗng → "--". |

### `QuanLyChamCongPanel` (chấm công nhân viên)
| Hàm | Chức năng |
|---|---|
| `QuanLyChamCongPanel(quanLy)` | Dựng panel 2 phần (bảng theo ngày + lịch sử tháng), gắn sự kiện chọn dòng, nạp dữ liệu. |
| `taoPhanDau()` | Tiêu đề + thanh công cụ (ngày, ca, Xem/Chấm vào/Chấm ra). |
| `taoNoiDung()` | Hai khối xếp dọc bằng `JSplitPane`, kèm thanh chọn tháng/năm cho phần lịch sử. |
| `taoPhanCuoi()` | Dòng đếm + dòng trạng thái. |
| `taiLai()` | Nạp danh sách nhân viên cấp dưới (lần đầu), ca, chấm công của nhóm trong ngày → map tra cứu → vẽ bảng. |
| `napBangTheoNgay(ngay)` | Vẽ bảng theo ngày: mỗi nhân viên một dòng, ai chưa chấm hiện "Vắng mặt". |
| `napLichSu()` | Vẽ lịch sử tháng của nhân viên đang chọn + tính tổng giờ. |
| `chamCong(laVao)` | Chấm công vào/ra **thay** nhân viên đang chọn (chặn ngày tương lai, chặn chấm trùng), rồi tải lại và chọn lại đúng dòng. |
| `docNgay()` | Đọc ô ngày (dd/MM/yyyy), sai thì báo lỗi. |
| `docNam(truong)` | Đọc ô năm, sai thì báo lỗi. |
| `maNhanVienDangChon()` | Mã nhân viên ở dòng đang chọn. |
| `tenNhanVien(maNV)` | Đổi mã thành họ tên để hiện ở dòng mô tả lịch sử. |
| `chonDongTheoMa(maNV)` | Chọn lại dòng của một nhân viên sau khi bảng vẽ lại. |
| `hoac(giaTri)` | null/rỗng → "--". |

### `GiamDocPanel` (giám đốc - quản lý toàn công ty)
| Hàm | Chức năng |
|---|---|
| `GiamDocPanel(giamDoc)` | Dựng panel 2 bảng (quản lý / nhân viên dưới quyền), gắn sự kiện chọn dòng + nháy đúp, nạp dữ liệu. |
| `taoPhanDau()` | Tiêu đề + 5 nút: Xem thông tin, Tăng lương, Thăng chức quản lý, Cho thôi việc, Làm mới. |
| `taoNoiDung()` | Hai bảng xếp dọc bằng `JSplitPane`. |
| `taoPhanCuoi()` | Người đang đăng nhập + dòng trạng thái. |
| `taiLai()` | Nạp danh sách **mọi quản lý** (`layTatCaQuanLy`) kèm số nhân viên, phòng ban, lương. |
| `napBangNhanVien()` | Nạp nhân viên dưới quyền của quản lý đang chọn (`layCapDuoi`). |
| `quanLyDangChon()` / `nhanVienDangChon()` | Lấy đối tượng ở dòng đang chọn của từng bảng. |
| `doiTuongDangChon()` | Chọn "đối tượng để thao tác": ưu tiên bảng người dùng vừa bấm, sau đó bảng quản lý. |
| `xemThongTinChon()` | Hiện hộp thoại đầy đủ hồ sơ của người đang chọn. |
| `tangLuong()` | Hỏi lương mới (gợi ý +10%), kiểm tra lớn hơn lương cũ, gọi `tangLuong`. |
| `thoiViec()` | Hỏi xác nhận rồi gọi `NhanVienDAO.thoiViec` (nghỉ việc + khóa tài khoản). |
| `thangChuc()` | Hỏi xác nhận rồi gọi `NhanVienDAO.thangChuc` (chức vụ Trưởng phòng + vai trò Quản lý). |
| `laQuanLy(maNV)` | Kiểm tra một người đã nằm trong danh sách quản lý chưa (để chặn thăng chức trùng). |
| `chonLaiQuanLy(maNV)` / `tien(NhanVien)` / `tien(maNV)` / `hoac` / `thay` | Các hàm phụ: chọn lại dòng, định dạng lương, thay giá trị rỗng bằng "--". |

### `DoiMatKhauDialog` (đổi mật khẩu - mọi vai trò)
| Hàm | Chức năng |
|---|---|
| `DoiMatKhauDialog(cha, taiKhoan)` | Dựng hộp thoại 3 ô: mật khẩu hiện tại, mật khẩu mới, nhập lại. |
| `isDaDoi()` | `true` nếu đã đổi thành công. |
| `taoPhanDau()` / `taoForm()` / `taoPhanCuoi()` | Dựng tiêu đề + tên tài khoản / 3 ô nhập / nhãn lỗi + 2 nút. |
| `doiMatKhau()` | 6 bước kiểm tra: đủ 3 ô → mật khẩu mới ≥ 6 ký tự → nhập lại khớp → khác mật khẩu cũ → mật khẩu cũ đúng (`TaiKhoanDAO.dangNhap`) → `TaiKhoanDAO.doiMatKhau`. |

---

## Bảng tóm tắt kiến trúc

```text
Main → LoginFrame → MainFrame → (ThongTinCaNhanPanel | NhanVienPanel | ChamCongPanel
                                | QuanLyChamCongPanel | GiamDocPanel | DoiMatKhauDialog)
                                        │
                                        ├── gọi dao.*  (SQL)  → util.KetNoiCSDL → CSDL MySQL/SQL Server
                                        ├── gọi service.ChamCongService (quy tắc chấm công)
                                        └── hiển thị model.* (dữ liệu), dùng util.DinhDang, util.MatKhau

Phân quyền theo TAIKHOAN.VaiTro:
    GiamDoc  → GiamDocPanel  (xem mọi quản lý + nhân viên, tăng lương, thăng chức, thôi việc)
    QuanLy   → NhanVienPanel + QuanLyChamCongPanel (quản lý nhân viên cấp dưới, chấm công hộ)
    NhanVien → chỉ xem hồ sơ của mình + tự chấm công
```

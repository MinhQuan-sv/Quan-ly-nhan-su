package view;

import dao.NhanVienDAO;
import dao.TaiKhoanDAO;
import model.ChucVu;
import model.NhanVien;
import model.PhongBan;
import model.TaiKhoan;
import util.DinhDang;
import util.KetNoiCSDL;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class NhanVienFormDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    public enum CheDo {
        THEM,

        SUA
    }

    private static final String MAU_EMAIL = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    private final CheDo cheDo;

    private final NhanVien nhanVienDangSua;

    private final String maQuanLy;

    private boolean daLuu = false;

    private final JTextField txtMaNV = GiaoDien.truong(20);
    private final JTextField txtHoTen = GiaoDien.truong(20);
    private final JTextField txtNgaySinh = GiaoDien.truong(20);
    private final JComboBox<String> cbGioiTinh = GiaoDien.combo(new String[]{"Nam", "Nữ", "Khác"});
    private final JTextField txtCccd = GiaoDien.truong(20);
    private final JTextField txtSoDienThoai = GiaoDien.truong(20);
    private final JTextField txtEmail = GiaoDien.truong(20);
    private final JTextField txtDiaChi = GiaoDien.truong(20);
    private final JTextField txtNgayVaoLam = GiaoDien.truong(20);
    private final JComboBox<PhongBan> cbPhongBan = new JComboBox<>();
    private final JComboBox<ChucVu> cbChucVu = new JComboBox<>();
    private final JComboBox<String> cbTrangThai = GiaoDien.combo(NhanVien.TRANG_THAI);

    private final JTextField txtLuong = GiaoDien.truong(20);

    private final JTextField txtTenDangNhap = GiaoDien.truong(20);
    private final JTextField txtMatKhau = GiaoDien.truong(20);
    private final JTextField txtNhapLaiMatKhau = GiaoDien.truong(20);

    private final JLabel lblLoi = new JLabel(" ");

    public NhanVienFormDialog(Window cha, CheDo cheDo, NhanVien nhanVienDangSua,
                              String maNVMoi, String maQuanLy,
                              List<PhongBan> dsPhongBan, List<ChucVu> dsChucVu) {
        super(cha, cheDo == CheDo.THEM ? "Thêm nhân viên" : "Cập nhật nhân viên",
                ModalityType.APPLICATION_MODAL);

        this.cheDo = cheDo;
        this.nhanVienDangSua = nhanVienDangSua;
        this.maQuanLy = maQuanLy;

        for (PhongBan phongBan : dsPhongBan) {
            cbPhongBan.addItem(phongBan);
        }
        for (ChucVu chucVu : dsChucVu) {
            cbChucVu.addItem(chucVu);
        }

        txtNgaySinh.setToolTipText("Định dạng dd/MM/yyyy");
        txtNgayVaoLam.setToolTipText("Định dạng dd/MM/yyyy");
        txtTenDangNhap.setToolTipText("Tên đăng nhập, không dấu, không khoảng trắng");
        txtMatKhau.setToolTipText("Tối thiểu 6 ký tự");
        txtLuong.setToolTipText("Chỉ nhập số, ví dụ: 12000000");
        txtMaNV.setEditable(false);

        dienDuLieuBanDau(maNVMoi);

        setContentPane(taoNoiDung());
        pack();
        setLocationRelativeTo(cha);
    }

    public boolean isDaLuu() {
        return daLuu;
    }

    private void dienDuLieuBanDau(String maNVMoi) {
        if (cheDo == CheDo.THEM) {
            txtMaNV.setText(maNVMoi);
            cbTrangThai.setSelectedItem(NhanVien.DANG_LAM);
            txtNgayVaoLam.setText(DinhDang.ngay(LocalDate.now()));

            txtTenDangNhap.setText(maNVMoi == null ? "" : maNVMoi.toLowerCase());
            txtMatKhau.setText("123456");
            txtNhapLaiMatKhau.setText("123456");
            txtLuong.setText("12000000");
            return;
        }

        txtMaNV.setText(nhanVienDangSua.getMaNV());
        txtHoTen.setText(nhanVienDangSua.getHoTen());
        txtNgaySinh.setText(DinhDang.ngay(nhanVienDangSua.getNgaySinh()));
        if (nhanVienDangSua.getGioiTinh() != null) {
            cbGioiTinh.setSelectedItem(nhanVienDangSua.getGioiTinh());
        }
        txtCccd.setText(nhanVienDangSua.getCccd());
        txtSoDienThoai.setText(nhanVienDangSua.getSoDienThoai());
        txtEmail.setText(nhanVienDangSua.getEmail());
        txtDiaChi.setText(nhanVienDangSua.getDiaChi());
        txtNgayVaoLam.setText(DinhDang.ngay(nhanVienDangSua.getNgayVaoLam()));
        chonPhongBan(nhanVienDangSua.getMaPB());
        chonChucVu(nhanVienDangSua.getMaCV());
        if (nhanVienDangSua.getTrangThai() != null) {
            cbTrangThai.setSelectedItem(nhanVienDangSua.getTrangThai());
        }
        if (nhanVienDangSua.getLuong() != null) {
            txtLuong.setText(nhanVienDangSua.getLuong().toPlainString());
        }
    }

    private void chonPhongBan(String maPB) {
        if (maPB == null) {
            return;
        }
        for (int i = 0; i < cbPhongBan.getItemCount(); i++) {
            if (maPB.equals(cbPhongBan.getItemAt(i).getMaPB())) {
                cbPhongBan.setSelectedIndex(i);
                return;
            }
        }
    }

    private void chonChucVu(String maCV) {
        if (maCV == null) {
            return;
        }
        for (int i = 0; i < cbChucVu.getItemCount(); i++) {
            if (maCV.equals(cbChucVu.getItemAt(i).getMaCV())) {
                cbChucVu.setSelectedIndex(i);
                return;
            }
        }
    }

    private JPanel taoNoiDung() {
        JPanel goc = new JPanel(new BorderLayout());
        goc.setBackground(Color.WHITE);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(12, 16, 4, 16));

        GridBagConstraints viTri = new GridBagConstraints();
        viTri.insets = new Insets(3, 4, 3, 4);
        viTri.fill = GridBagConstraints.HORIZONTAL;
        viTri.anchor = GridBagConstraints.WEST;

        int hang = 0;

        hang = themTruong(form, viTri, hang, "Mã nhân viên", txtMaNV);
        hang = themTruong(form, viTri, hang, "Họ và tên (*)", txtHoTen);
        hang = themTruong(form, viTri, hang, "Ngày sinh", txtNgaySinh);
        hang = themTruong(form, viTri, hang, "Giới tính", cbGioiTinh);
        hang = themTruong(form, viTri, hang, "CCCD", txtCccd);
        hang = themTruong(form, viTri, hang, "Số điện thoại", txtSoDienThoai);
        hang = themTruong(form, viTri, hang, "Email", txtEmail);
        hang = themTruong(form, viTri, hang, "Địa chỉ", txtDiaChi);
        hang = themTruong(form, viTri, hang, "Ngày vào làm", txtNgayVaoLam);
        hang = themTruong(form, viTri, hang, "Phòng ban (*)", cbPhongBan);
        hang = themTruong(form, viTri, hang, "Chức vụ (*)", cbChucVu);
        hang = themTruong(form, viTri, hang, "Trạng thái", cbTrangThai);
        hang = themTruong(form, viTri, hang, "Lương (đồng)", txtLuong);

        if (cheDo == CheDo.THEM) {
            viTri.gridx = 0;
            viTri.gridy = hang++;
            viTri.gridwidth = 2;

            JLabel lblTieuDeTaiKhoan = new JLabel("TÀI KHOẢN ĐĂNG NHẬP CHO NHÂN VIÊN");
            lblTieuDeTaiKhoan.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblTieuDeTaiKhoan.setForeground(GiaoDien.XANH_CHINH);
            form.add(lblTieuDeTaiKhoan, viTri);

            viTri.gridwidth = 1;

            hang = themTruong(form, viTri, hang, "Tên đăng nhập (*)", txtTenDangNhap);
            hang = themTruong(form, viTri, hang, "Mật khẩu (*)", txtMatKhau);
            hang = themTruong(form, viTri, hang, "Nhập lại mật khẩu (*)", txtNhapLaiMatKhau);
        }

        lblLoi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLoi.setForeground(GiaoDien.DO);
        viTri.gridx = 0;
        viTri.gridy = hang;
        viTri.gridwidth = 2;
        form.add(lblLoi, viTri);
        viTri.gridwidth = 1;

        JScrollPane cuon = new JScrollPane(form);
        cuon.setBorder(null);
        cuon.getViewport().setBackground(Color.WHITE);
        cuon.setPreferredSize(new Dimension(440, Math.min(620, 90 + hang * 34)));
        goc.add(cuon, BorderLayout.CENTER);

        JPanel phanNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        phanNut.setBackground(Color.WHITE);
        JButton nutHuy = GiaoDien.nut("Hủy", GiaoDien.XAM);
        JButton nutLuu = GiaoDien.nut("Lưu", GiaoDien.XANH_CHINH);
        nutHuy.addActionListener(e -> dispose());
        nutLuu.addActionListener(e -> luu());
        phanNut.add(nutHuy);
        phanNut.add(nutLuu);
        goc.add(phanNut, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(nutLuu);
        return goc;
    }

    private int themTruong(JPanel panel, GridBagConstraints viTri, int hang,
                           String nhan, JComponent truong) {
        viTri.gridx = 0;
        viTri.gridy = hang;
        viTri.gridwidth = 1;
        viTri.weightx = 0;
        panel.add(GiaoDien.nhan(nhan), viTri);

        viTri.gridx = 1;
        viTri.weightx = 1;
        panel.add(truong, viTri);

        return hang + 1;
    }

    private void luu() {
        lblLoi.setText(" ");

        NhanVien nhanVien = new NhanVien();
        try {
            nhanVien.setMaNV(cheDo == CheDo.THEM
                    ? txtMaNV.getText().trim()
                    : nhanVienDangSua.getMaNV());
            nhanVien.setHoTen(kiemTraBatBuoc(txtHoTen.getText(), "Họ và tên"));
            nhanVien.setNgaySinh(DinhDang.docNgay(txtNgaySinh.getText()));
            nhanVien.setGioiTinh((String) cbGioiTinh.getSelectedItem());
            nhanVien.setCccd(kiemTraCccd(txtCccd.getText()));
            nhanVien.setSoDienThoai(kiemTraDienThoai(txtSoDienThoai.getText()));
            nhanVien.setEmail(kiemTraEmail(txtEmail.getText()));
            nhanVien.setDiaChi(rongThanhNull(txtDiaChi.getText()));
            nhanVien.setNgayVaoLam(DinhDang.docNgay(txtNgayVaoLam.getText()));
            nhanVien.setTrangThai((String) cbTrangThai.getSelectedItem());
            nhanVien.setLuong(docLuong(txtLuong.getText()));
            nhanVien.setMaQuanLy(maQuanLy);

            PhongBan phongBan = (PhongBan) cbPhongBan.getSelectedItem();
            ChucVu chucVu = (ChucVu) cbChucVu.getSelectedItem();
            if (phongBan == null || chucVu == null) {
                lblLoi.setText("Vui lòng chọn phòng ban và chức vụ.");
                return;
            }
            nhanVien.setMaPB(phongBan.getMaPB());
            nhanVien.setMaCV(chucVu.getMaCV());

            kiemTraNgayThang(nhanVien);
        } catch (IllegalArgumentException loiDuLieu) {
            lblLoi.setText(loiDuLieu.getMessage());
            return;
        }

        try {
            if (cheDo == CheDo.THEM) {
                themMoi(nhanVien);
            } else {
                capNhat(nhanVien);
            }
        } catch (SQLException loi) {
            lblLoi.setText(thongDiepLoi(loi));
        }
    }

    private void themMoi(NhanVien nhanVien) throws SQLException {
        String tenDangNhap = kiemTraBatBuoc(txtTenDangNhap.getText(), "Tên đăng nhập");
        String matKhau = txtMatKhau.getText();
        String nhapLai = txtNhapLaiMatKhau.getText();

        if (matKhau.length() < 6) {
            lblLoi.setText("Mật khẩu phải có ít nhất 6 ký tự.");
            return;
        }
        if (!matKhau.equals(nhapLai)) {
            lblLoi.setText("Mật khẩu nhập lại không khớp.");
            return;
        }
        if (nhanVien.getCccd() != null && nhanVienDAO.cccdDaTonTai(nhanVien.getCccd(), null)) {
            lblLoi.setText("Số CCCD này đã được dùng cho nhân viên khác.");
            return;
        }
        if (nhanVien.getEmail() != null && nhanVienDAO.emailDaTonTai(nhanVien.getEmail(), null)) {
            lblLoi.setText("Email này đã được dùng cho nhân viên khác.");
            return;
        }
        if (taiKhoanDAO.tenDangNhapDaTonTai(tenDangNhap)) {
            lblLoi.setText("Tên đăng nhập \"" + tenDangNhap + "\" đã tồn tại.");
            return;
        }

        nhanVienDAO.themKemTaiKhoan(nhanVien, tenDangNhap, matKhau, TaiKhoan.NHAN_VIEN);

        daLuu = true;
        GiaoDien.tin(this, "Đã thêm nhân viên " + nhanVien.getHoTen()
                + " (" + nhanVien.getMaNV() + ").\nTài khoản đăng nhập: " + tenDangNhap);
        dispose();
    }

    private void capNhat(NhanVien nhanVien) throws SQLException {
        if (nhanVien.getCccd() != null
                && nhanVienDAO.cccdDaTonTai(nhanVien.getCccd(), nhanVien.getMaNV())) {
            lblLoi.setText("Số CCCD này đã được dùng cho nhân viên khác.");
            return;
        }
        if (nhanVien.getEmail() != null
                && nhanVienDAO.emailDaTonTai(nhanVien.getEmail(), nhanVien.getMaNV())) {
            lblLoi.setText("Email này đã được dùng cho nhân viên khác.");
            return;
        }

        nhanVienDAO.capNhat(nhanVien);

        daLuu = true;
        GiaoDien.tin(this, "Đã cập nhật thông tin nhân viên " + nhanVien.getHoTen() + ".");
        dispose();
    }

    private static BigDecimal docLuong(String chuoi) {
        String so = chuoi == null ? "" : chuoi.trim()
                .replace(".", "").replace(",", "").replace(" ", "");
        if (so.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            BigDecimal luong = new BigDecimal(so);
            if (luong.signum() < 0) {
                throw new IllegalArgumentException("Lương không được là số âm.");
            }
            return luong;
        } catch (NumberFormatException loi) {
            throw new IllegalArgumentException("Lương phải là số, ví dụ: 12000000");
        }
    }

    private void kiemTraNgayThang(NhanVien nhanVien) {
        LocalDate homNay = LocalDate.now();

        if (nhanVien.getNgaySinh() != null && nhanVien.getNgaySinh().isAfter(homNay)) {
            throw new IllegalArgumentException("Ngày sinh không được ở tương lai.");
        }
        if (nhanVien.getNgayVaoLam() != null && nhanVien.getNgayVaoLam().isAfter(homNay)) {
            throw new IllegalArgumentException("Ngày vào làm không được ở tương lai.");
        }
        if (nhanVien.getNgaySinh() != null && nhanVien.getNgayVaoLam() != null
                && nhanVien.getNgayVaoLam().isBefore(nhanVien.getNgaySinh())) {
            throw new IllegalArgumentException("Ngày vào làm phải sau ngày sinh.");
        }
    }

    private static String kiemTraBatBuoc(String giaTri, String tenTruong) {
        if (giaTri == null || giaTri.trim().isEmpty()) {
            throw new IllegalArgumentException(tenTruong + " không được để trống.");
        }
        return giaTri.trim();
    }

    private static String kiemTraCccd(String giaTri) {
        String giaTriSach = rongThanhNull(giaTri);
        if (giaTriSach == null) {
            return null;
        }
        if (!giaTriSach.matches("\\d{9}|\\d{12}")) {
            throw new IllegalArgumentException("CCCD phải gồm 9 hoặc 12 chữ số.");
        }
        return giaTriSach;
    }

    private static String kiemTraDienThoai(String giaTri) {
        String giaTriSach = rongThanhNull(giaTri);
        if (giaTriSach == null) {
            return null;
        }
        if (!giaTriSach.matches("\\d{9,11}")) {
            throw new IllegalArgumentException("Số điện thoại phải gồm 9 đến 11 chữ số.");
        }
        return giaTriSach;
    }

    private static String kiemTraEmail(String giaTri) {
        String giaTriSach = rongThanhNull(giaTri);
        if (giaTriSach == null) {
            return null;
        }
        if (!giaTriSach.matches(MAU_EMAIL)) {
            throw new IllegalArgumentException("Email không đúng định dạng, ví dụ: ten.nv@congty.vn");
        }
        return giaTriSach;
    }

    private static String rongThanhNull(String giaTri) {
        return giaTri == null || giaTri.trim().isEmpty() ? null : giaTri.trim();
    }

    private static String thongDiepLoi(SQLException loi) {
        int maLoi = loi.getErrorCode();
        if (maLoi == 2627 || maLoi == 2601) {
            String thongDiep = loi.getMessage() == null ? "" : loi.getMessage();
            if (thongDiep.contains("CCCD")) {
                return "Số CCCD đã tồn tại.";
            }
            if (thongDiep.contains("Email")) {
                return "Email đã tồn tại.";
            }
            return "Dữ liệu bị trùng với bản ghi đã có (mã nhân viên, CCCD, email "
                    + "hoặc tên đăng nhập).";
        }
        return KetNoiCSDL.moTaLoi(loi);
    }
}

package view;

import dao.DanhMucDAO;
import dao.NhanVienDAO;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import model.ChucVu;
import model.NhanVien;
import model.PhongBan;
import util.DinhDang;
import util.KetNoiCSDL;

public class NhanVienPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final String TAT_CA = "Tất cả";

    private static final String[] TEN_COT = {
            "Mã NV", "Họ tên", "Ngày sinh", "Giới tính", "Số điện thoại", "Email",
            "Phòng ban", "Chức vụ", "Ngày vào làm", "Trạng thái", "Lương"
    };

    private final NhanVien quanLy;

    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final DanhMucDAO danhMucDAO = new DanhMucDAO();

    private final List<NhanVien> danhSachNhanVien = new ArrayList<>();
    private final List<PhongBan> danhSachPhongBan = new ArrayList<>();
    private final List<ChucVu> danhSachChucVu = new ArrayList<>();

    private final DefaultTableModel moHinhBang = GiaoDien.moHinhBang(TEN_COT);
    private final JTable bang = GiaoDien.bang(moHinhBang);
    private final JTextField txtTimKiem = GiaoDien.truong(18);
    private final JComboBox<String> cbLocTrangThai = GiaoDien.combo(new String[]{
            TAT_CA, NhanVien.DANG_LAM, NhanVien.TAM_NGHI});
    private final JLabel lblDem = new JLabel();
    private final JLabel lblThongBao = new JLabel(" ");

    public NhanVienPanel(NhanVien quanLy) {
        this.quanLy = quanLy;

        setLayout(new BorderLayout(0, 10));
        setBackground(GiaoDien.NEN);
        setBorder(new EmptyBorder(14, 14, 14, 14));

        add(taoPhanDau(), BorderLayout.NORTH);
        add(GiaoDien.cuon(bang), BorderLayout.CENTER);
        add(taoPhanCuoi(), BorderLayout.SOUTH);

        lblDem.setFont(GiaoDien.CHU_DAM);
        lblDem.setForeground(GiaoDien.CHU);

        bang.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent suKien) {
                if (suKien.getClickCount() == 2) {
                    suaNhanVien();
                }
            }
        });

        taiLai();
    }

    private JPanel taoPhanDau() {
        JPanel phan = new JPanel(new BorderLayout(0, 8));
        phan.setOpaque(false);

        JPanel hangTieuDe = new JPanel(new BorderLayout());
        hangTieuDe.setOpaque(false);
        hangTieuDe.add(GiaoDien.tieuDe("NHÂN VIÊN CẤP DƯỚI"), BorderLayout.WEST);

        JLabel lblQuanLy = new JLabel("Quản lý: " + quanLy.getHoTen() + " (" + quanLy.getMaNV() + ")");
        lblQuanLy.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblQuanLy.setForeground(GiaoDien.CHU_NHAT);
        hangTieuDe.add(lblQuanLy, BorderLayout.EAST);
        phan.add(hangTieuDe, BorderLayout.NORTH);

        JPanel thanhCongCu = GiaoDien.thanhCongCu();

        thanhCongCu.add(GiaoDien.nhan("Tìm kiếm:"));
        txtTimKiem.setToolTipText("Tìm theo mã, họ tên, số điện thoại hoặc email");
        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent suKien) {
                napBang();
            }

            @Override
            public void removeUpdate(DocumentEvent suKien) {
                napBang();
            }

            @Override
            public void changedUpdate(DocumentEvent suKien) {
                napBang();
            }
        });
        thanhCongCu.add(txtTimKiem);

        thanhCongCu.add(GiaoDien.nhan("Trạng thái:"));
        cbLocTrangThai.addActionListener(e -> napBang());
        thanhCongCu.add(cbLocTrangThai);

        JButton nutThem = GiaoDien.nut("Thêm nhân viên", GiaoDien.XANH_CHINH);
        JButton nutSua = GiaoDien.nut("Sửa hồ sơ", GiaoDien.XANH_LA);
        JButton nutNghi = GiaoDien.nut("Cho nghỉ việc", GiaoDien.DO);
        JButton nutLamMoi = GiaoDien.nut("Làm mới", GiaoDien.XAM);

        nutThem.addActionListener(e -> themNhanVien());
        nutSua.addActionListener(e -> suaNhanVien());
        nutNghi.addActionListener(e -> choNghiViec());
        nutLamMoi.addActionListener(e -> {
            txtTimKiem.setText("");
            cbLocTrangThai.setSelectedIndex(0);
            taiLai();
        });

        thanhCongCu.add(nutThem);
        thanhCongCu.add(nutSua);
        thanhCongCu.add(nutNghi);
        thanhCongCu.add(nutLamMoi);

        phan.add(thanhCongCu, BorderLayout.CENTER);
        return phan;
    }

    private JPanel taoPhanCuoi() {
        JPanel phan = new JPanel(new BorderLayout());
        phan.setOpaque(false);

        lblThongBao.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblThongBao.setForeground(GiaoDien.CHU_NHAT);

        JPanel benTrai = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        benTrai.setOpaque(false);
        benTrai.add(lblDem);
        benTrai.add(new JLabel("     "));
        benTrai.add(lblThongBao);

        phan.add(benTrai, BorderLayout.WEST);
        return phan;
    }

    private void taiLai() {
        try {
            danhSachNhanVien.clear();
            danhSachNhanVien.addAll(nhanVienDAO.layCapDuoi(quanLy.getMaNV()));

            if (danhSachPhongBan.isEmpty()) {
                danhSachPhongBan.addAll(danhMucDAO.layPhongBan());
            }
            if (danhSachChucVu.isEmpty()) {
                danhSachChucVu.addAll(danhMucDAO.layChucVu());
            }

            lblThongBao.setForeground(GiaoDien.CHU_NHAT);
            lblThongBao.setText(" ");
            lblThongBao.setToolTipText(null);
        } catch (SQLException loi) {
            lblThongBao.setForeground(GiaoDien.DO);
            lblThongBao.setText("Không tải được dữ liệu từ CSDL.");
            lblThongBao.setToolTipText("<html><pre>" + KetNoiCSDL.moTaLoi(loi) + "</pre></html>");
        }
        napBang();
    }

    private void napBang() {
        String tuKhoa = txtTimKiem.getText() == null
                ? "" : txtTimKiem.getText().trim().toLowerCase(Locale.forLanguageTag("vi-VN"));
        String trangThaiLoc = (String) cbLocTrangThai.getSelectedItem();

        moHinhBang.setRowCount(0);
        int soDongHienThi = 0;

        for (NhanVien nhanVien : danhSachNhanVien) {
            if (!khopTuKhoa(nhanVien, tuKhoa)) {
                continue;
            }
            if (trangThaiLoc != null && !TAT_CA.equals(trangThaiLoc)
                    && !trangThaiLoc.equalsIgnoreCase(nhanVien.getTrangThai())) {
                continue;
            }

            moHinhBang.addRow(new Object[]{
                    nhanVien.getMaNV(),
                    nhanVien.getHoTen(),
                    DinhDang.ngay(nhanVien.getNgaySinh()),
                    hoac(nhanVien.getGioiTinh()),
                    hoac(nhanVien.getSoDienThoai()),
                    hoac(nhanVien.getEmail()),
                    hoac(nhanVien.getTenPB()),
                    hoac(nhanVien.getTenCV()),
                    DinhDang.ngay(nhanVien.getNgayVaoLam()),
                    hoac(nhanVien.getTrangThai()),
                    nhanVien.getLuong() == null ? "--" : DinhDang.tien(nhanVien.getLuong())
            });
            soDongHienThi++;
        }

        if (danhSachNhanVien.isEmpty()) {
            lblDem.setText("Chưa có nhân viên cấp dưới nào.");
        } else {
            lblDem.setText("Hiển thị " + soDongHienThi + " / " + danhSachNhanVien.size()
                    + " nhân viên cấp dưới");
        }
    }

    private static boolean khopTuKhoa(NhanVien nhanVien, String tuKhoa) {
        if (tuKhoa.isEmpty()) {
            return true;
        }
        return chua(nhanVien.getMaNV(), tuKhoa)
                || chua(nhanVien.getHoTen(), tuKhoa)
                || chua(nhanVien.getSoDienThoai(), tuKhoa)
                || chua(nhanVien.getEmail(), tuKhoa);
    }

    private static boolean chua(String giaTri, String tuKhoa) {
        return giaTri != null
                && giaTri.toLowerCase(Locale.forLanguageTag("vi-VN")).contains(tuKhoa);
    }

    private static String hoac(String giaTri) {
        return giaTri == null ? "" : giaTri;
    }

    private void themNhanVien() {
        try {
            String maNVMoi = nhanVienDAO.sinhMaNhanVien();

            NhanVienFormDialog hopThoai = new NhanVienFormDialog(
                    SwingUtilities.getWindowAncestor(this),
                    NhanVienFormDialog.CheDo.THEM,
                    null,
                    maNVMoi,
                    quanLy.getMaNV(),
                    danhSachPhongBan,
                    danhSachChucVu);

            hopThoai.setVisible(true);

            if (hopThoai.isDaLuu()) {
                taiLai();
            }
        } catch (SQLException loi) {
            GiaoDien.loiKetNoi(this, loi);
        }
    }

    private void suaNhanVien() {
        NhanVien nhanVienChon = nhanVienDangChon();
        if (nhanVienChon == null) {
            GiaoDien.canhBao(this, "Vui lòng chọn một nhân viên cần sửa hồ sơ.");
            return;
        }

        NhanVienFormDialog hopThoai = new NhanVienFormDialog(
                SwingUtilities.getWindowAncestor(this),
                NhanVienFormDialog.CheDo.SUA,
                nhanVienChon,
                null,
                quanLy.getMaNV(),
                danhSachPhongBan,
                danhSachChucVu);

        hopThoai.setVisible(true);
        if (hopThoai.isDaLuu()) {
            taiLai();
        }
    }

    private void choNghiViec() {
        NhanVien nhanVienChon = nhanVienDangChon();
        if (nhanVienChon == null) {
            GiaoDien.canhBao(this, "Vui lòng chọn một nhân viên cần cho nghỉ việc.");
            return;
        }

        if (NhanVien.DA_NGHI.equalsIgnoreCase(nhanVienChon.getTrangThai())) {
            GiaoDien.tin(this, "Nhân viên " + nhanVienChon.getHoTen()
                    + " đã ở trạng thái \"Đã nghỉ\".");
            return;
        }

        boolean dongY = GiaoDien.xacNhan(this, "Cho nhân viên \""
                + nhanVienChon.getHoTen() + "\" (" + nhanVienChon.getMaNV()
                + ") nghỉ việc?\n\n"
                + "Nhân viên sẽ bị xóa khỏi danh sách và tài khoản bị khóa.");
        if (!dongY) {
            return;
        }

        try {
            nhanVienDAO.thoiViec(nhanVienChon.getMaNV());
            taiLai();
            lblThongBao.setForeground(GiaoDien.DO);
            lblThongBao.setText("Đã cho " + nhanVienChon.getHoTen()
                    + " nghỉ việc và xóa khỏi danh sách.");
        } catch (SQLException loi) {
            GiaoDien.loiKetNoi(this, loi);
        }
    }

    private NhanVien nhanVienDangChon() {
        int dong = bang.getSelectedRow();
        if (dong < 0) {
            return null;
        }

        String maNV = String.valueOf(moHinhBang.getValueAt(dong, 0));
        for (NhanVien nhanVien : danhSachNhanVien) {
            if (maNV.equalsIgnoreCase(nhanVien.getMaNV())) {
                return nhanVien;
            }
        }
        return null;
    }
}

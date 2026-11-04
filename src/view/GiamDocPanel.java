package view;

import dao.NhanVienDAO;
import dao.DanhMucDAO;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import model.ChucVu;
import model.NhanVien;
import model.PhongBan;
import util.DinhDang;

public class GiamDocPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final String[] COT_QUAN_LY = {
            "Mã QL", "Tên quản lý", "Phòng ban", "Số nhân viên", "Trạng thái", "Lương"
    };

    private static final String[] COT_NHAN_VIEN = {
            "Mã NV", "Họ tên", "Chức vụ", "Trạng thái", "Lương"
    };

    private final NhanVien giamDoc;

    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final DanhMucDAO danhMucDAO = new DanhMucDAO();

    private final DefaultTableModel moHinhQuanLy = GiaoDien.moHinhBang(COT_QUAN_LY);
    private final JTable bangQuanLy = GiaoDien.bang(moHinhQuanLy);

    private final DefaultTableModel moHinhNhanVien = GiaoDien.moHinhBang(COT_NHAN_VIEN);
    private final JTable bangNhanVien = GiaoDien.bang(moHinhNhanVien);

    private final JLabel lblTieuDeNhanVien = GiaoDien.giaTri("Chọn một phòng ban ở bảng trên để xem nhân viên.");
    private final JLabel lblThongBao = new JLabel(" ");

    // Class nội bộ lưu mỗi dòng của bảng trên
    private static class DongBang {
        String maPB;
        String tenPB;
        String maQL;
        String tenQL;
        String trangThai;
        int soNhanVien;
        NhanVien quanLy; // null nếu chưa có
    }

    private final List<DongBang> danhSachDong = new ArrayList<>();
    private final List<NhanVien> danhSachNhanVien = new ArrayList<>();

    private final List<PhongBan> dsPhongBan = new ArrayList<>();
    private final List<ChucVu> dsChucVu = new ArrayList<>();

    private JTable bangDangChon;

    public GiamDocPanel(NhanVien giamDoc) {
        this.giamDoc = giamDoc;

        setLayout(new BorderLayout(0, 10));
        setBackground(GiaoDien.NEN);
        setBorder(new EmptyBorder(14, 14, 14, 14));

        add(taoPhanDau(), BorderLayout.NORTH);
        add(taoNoiDung(), BorderLayout.CENTER);
        add(taoPhanCuoi(), BorderLayout.SOUTH);

        lblThongBao.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblThongBao.setForeground(GiaoDien.CHU_NHAT);

        bangQuanLy.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent suKien) {
                bangDangChon = bangQuanLy;
                bangNhanVien.clearSelection();
                if (suKien.getClickCount() == 2) {
                    xemThongTinChon();
                }
                napBangNhanVien();
            }
        });

        bangQuanLy.getSelectionModel().addListSelectionListener(suKien -> {
            if (!suKien.getValueIsAdjusting() && bangQuanLy.getSelectedRow() >= 0) {
                bangDangChon = bangQuanLy;
                napBangNhanVien();
            }
        });

        bangNhanVien.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent suKien) {
                bangDangChon = bangNhanVien;
                bangQuanLy.clearSelection();
                if (suKien.getClickCount() == 2) {
                    xemThongTinChon();
                }
            }
        });

        taiLai();
    }

    private JPanel taoPhanDau() {
        JPanel phan = new JPanel(new BorderLayout(0, 8));
        phan.setOpaque(false);
        phan.add(GiaoDien.tieuDe("GIÁM ĐỐC - QUẢN LÝ TOÀN CÔNG TY"), BorderLayout.NORTH);

        JPanel thanhCongCu = GiaoDien.thanhCongCu();

        JButton nutXem = GiaoDien.nut("Xem thông tin", GiaoDien.XAM);
        JButton nutLuong = GiaoDien.nut("Tăng lương", GiaoDien.XANH_LA);
        JButton nutThangChuc = GiaoDien.nut("Thăng chức quản lý", GiaoDien.XANH_CHINH);
        JButton nutThem = GiaoDien.nut("Thêm nhân viên", GiaoDien.XANH_LA);
        JButton nutSua = GiaoDien.nut("Sửa hồ sơ", GiaoDien.XANH_CHINH);
        JButton nutThoiViec = GiaoDien.nut("Cho thôi việc", GiaoDien.DO);
        JButton nutLamMoi = GiaoDien.nut("Làm mới", GiaoDien.XAM);

        nutXem.addActionListener(e -> xemThongTinChon());
        nutLuong.addActionListener(e -> tangLuong());
        nutThangChuc.addActionListener(e -> thangChuc());
        nutThem.addActionListener(e -> themNhanVien());
        nutSua.addActionListener(e -> suaHoSo());
        nutThoiViec.addActionListener(e -> thoiViec());
        nutLamMoi.addActionListener(e -> taiLai());

        thanhCongCu.add(nutXem);
        thanhCongCu.add(nutLuong);
        thanhCongCu.add(nutThangChuc);
        thanhCongCu.add(nutThem);
        thanhCongCu.add(nutSua);
        thanhCongCu.add(nutThoiViec);
        thanhCongCu.add(nutLamMoi);

        phan.add(thanhCongCu, BorderLayout.CENTER);
        return phan;
    }

    private JPanel taoNoiDung() {
        JPanel khungQuanLy = new JPanel(new BorderLayout(0, 6));
        khungQuanLy.setOpaque(false);

        JLabel lblQuanLy = new JLabel("Danh sách phòng ban - bấm một dòng để xem nhân viên");
        lblQuanLy.setFont(GiaoDien.CHU_DAM);
        lblQuanLy.setForeground(GiaoDien.XANH_DAM);

        khungQuanLy.add(lblQuanLy, BorderLayout.NORTH);
        khungQuanLy.add(GiaoDien.cuon(bangQuanLy), BorderLayout.CENTER);

        JPanel khungNhanVien = new JPanel(new BorderLayout(0, 6));
        khungNhanVien.setOpaque(false);

        lblTieuDeNhanVien.setFont(GiaoDien.CHU_DAM);
        lblTieuDeNhanVien.setForeground(GiaoDien.XANH_DAM);
        khungNhanVien.add(lblTieuDeNhanVien, BorderLayout.NORTH);

        JScrollPane cuonNhanVien = GiaoDien.cuon(bangNhanVien);
        khungNhanVien.add(cuonNhanVien, BorderLayout.CENTER);

        JSplitPane chia = new JSplitPane(JSplitPane.VERTICAL_SPLIT, khungQuanLy, khungNhanVien);
        chia.setResizeWeight(0.5);
        chia.setDividerSize(8);
        chia.setBorder(null);
        chia.setOpaque(false);

        JPanel bao = new JPanel(new BorderLayout());
        bao.setOpaque(false);
        bao.add(chia, BorderLayout.CENTER);
        return bao;
    }

    private JPanel taoPhanCuoi() {
        JPanel phan = new JPanel(new BorderLayout());
        phan.setOpaque(false);

        JLabel lblNguoiDung = new JLabel("Đăng nhập: " + giamDoc.getHoTen()
                + " (" + giamDoc.getMaNV() + ")");
        lblNguoiDung.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNguoiDung.setForeground(GiaoDien.CHU_NHAT);

        phan.add(lblNguoiDung, BorderLayout.WEST);
        phan.add(lblThongBao, BorderLayout.CENTER);
        return phan;
    }

    private void taiLai() {
        try {
            danhSachDong.clear();
            moHinhQuanLy.setRowCount(0);

            List<PhongBan> tatCaPhongBan = danhMucDAO.layPhongBan();

            for (PhongBan pb : tatCaPhongBan) {
                if ("PB04".equalsIgnoreCase(pb.getMaPB())) {
                    continue;
                }

                String maTP = nhanVienDAO.layMaTruongPhong(pb.getMaPB());
                NhanVien quanLy = null;

                List<NhanVien> dsNVPhongBan = nhanVienDAO.layTheoPhongBan(pb.getMaPB());
                int soNV = dsNVPhongBan.size();

                if (soNV == 0 && maTP == null) {
                    continue;
                }

                if (maTP != null) {
                    for (NhanVien nv : dsNVPhongBan) {
                        if (nv.getMaNV().equalsIgnoreCase(maTP)) {
                            quanLy = nv;
                            break;
                        }
                    }
                }

                DongBang dong = new DongBang();
                dong.maPB = pb.getMaPB();
                dong.tenPB = pb.getTenPB();
                dong.soNhanVien = soNV;
                dong.quanLy = quanLy;
                dong.maQL = (quanLy != null) ? quanLy.getMaNV() : null;
                dong.tenQL = (quanLy != null) ? quanLy.getHoTen() : "(Chưa có quản lý)";
                dong.trangThai = (quanLy != null) ? quanLy.getTrangThai() : "--";

                danhSachDong.add(dong);

                moHinhQuanLy.addRow(new Object[] {
                        dong.maQL != null ? dong.maQL : "",
                        dong.tenQL,
                        dong.tenPB,
                        dong.soNhanVien,
                        dong.trangThai,
                        quanLy != null ? tien(quanLy) : "--"
                });
            }

            bangDangChon = null;
            danhSachNhanVien.clear();
            moHinhNhanVien.setRowCount(0);
            lblTieuDeNhanVien.setText("Chọn một phòng ban ở bảng trên để xem nhân viên.");

            lblThongBao.setForeground(GiaoDien.CHU_NHAT);
            lblThongBao.setText("Có " + danhSachDong.size() + " phòng ban trong hệ thống.");

        } catch (SQLException loi) {
            lblThongBao.setForeground(GiaoDien.DO);
            lblThongBao.setText("Không tải được danh sách phòng ban.");
            GiaoDien.loiKetNoi(this, loi);
        }
    }

    private void napBangNhanVien() {
        int dong = bangQuanLy.getSelectedRow();
        if (dong < 0 || dong >= danhSachDong.size()) {
            return;
        }

        DongBang dongChon = danhSachDong.get(dong);

        try {
            danhSachNhanVien.clear();
            danhSachNhanVien.addAll(nhanVienDAO.layTheoPhongBan(dongChon.maPB));

            moHinhNhanVien.setRowCount(0);
            for (NhanVien nhanVien : danhSachNhanVien) {
                moHinhNhanVien.addRow(new Object[] {
                        nhanVien.getMaNV(),
                        nhanVien.getHoTen(),
                        hoac(nhanVien.getTenCV()),
                        hoac(nhanVien.getTrangThai()),
                        tien(nhanVien)
                });
            }

            String tenQL = (dongChon.quanLy != null)
                    ? dongChon.quanLy.getHoTen()
                    : "(Chưa có quản lý)";

            lblTieuDeNhanVien.setText("Phòng " + dongChon.tenPB + " - " + tenQL
                    + " - " + danhSachNhanVien.size() + " người");

        } catch (SQLException loi) {
            GiaoDien.loiKetNoi(this, loi);
        }
    }

    private NhanVien quanLyDangChon() {
        int dong = bangQuanLy.getSelectedRow();
        if (dong < 0 || dong >= danhSachDong.size()) {
            return null;
        }
        return danhSachDong.get(dong).quanLy;
    }

    private NhanVien nhanVienDangChon() {
        int dong = bangNhanVien.getSelectedRow();
        if (dong < 0 || dong >= danhSachNhanVien.size()) {
            return null;
        }
        return danhSachNhanVien.get(dong);
    }

    private NhanVien doiTuongDangChon() {
        // Ưu tiên bảng dưới (nhân viên) nếu có chọn
        if (bangNhanVien.getSelectedRow() >= 0) {
            return nhanVienDangChon();
        }
        // Nếu chọn bảng trên, trả về quản lý của phòng (nếu có)
        return quanLyDangChon();
    }

    private void themNhanVien() {
        try {
            if (dsPhongBan.isEmpty())
                dsPhongBan.addAll(danhMucDAO.layPhongBan());
            if (dsChucVu.isEmpty())
                dsChucVu.addAll(danhMucDAO.layChucVu());

            String maNVMoi = nhanVienDAO.sinhMaNhanVien();

            NhanVienFormDialog dialog = new NhanVienFormDialog(
                    SwingUtilities.getWindowAncestor(this),
                    NhanVienFormDialog.CheDo.THEM,
                    null,
                    maNVMoi,
                    this.giamDoc,
                    dsPhongBan,
                    dsChucVu);
            dialog.setVisible(true);

            if (dialog.isDaLuu()) {
                taiLai();
                lblThongBao.setForeground(GiaoDien.XANH_LA);
                lblThongBao.setText("Đã thêm nhân viên mới.");
            }
        } catch (SQLException loi) {
            GiaoDien.loiKetNoi(this, loi);
        }
    }

    private void suaHoSo() {
        NhanVien nhanVien = doiTuongDangChon();
        if (nhanVien == null) {
            GiaoDien.canhBao(this, "Vui lòng chọn một quản lý hoặc một nhân viên.");
            return;
        }
        try {
            if (dsPhongBan.isEmpty())
                dsPhongBan.addAll(danhMucDAO.layPhongBan());
            if (dsChucVu.isEmpty())
                dsChucVu.addAll(danhMucDAO.layChucVu());

            NhanVienFormDialog dialog = new NhanVienFormDialog(
                    SwingUtilities.getWindowAncestor(this),
                    NhanVienFormDialog.CheDo.SUA,
                    nhanVien,
                    null,
                    this.giamDoc,
                    dsPhongBan,
                    dsChucVu);
            dialog.setVisible(true);

            if (dialog.isDaLuu()) {
                taiLai();
                lblThongBao.setForeground(GiaoDien.XANH_LA);
                lblThongBao.setText("Đã cập nhật hồ sơ cho " + nhanVien.getHoTen());
            }
        } catch (SQLException loi) {
            GiaoDien.loiKetNoi(this, loi);
        }
    }

    private void xemThongTinChon() {
        NhanVien nhanVien = doiTuongDangChon();
        if (nhanVien == null) {
            GiaoDien.canhBao(this, "Vui lòng chọn một quản lý hoặc một nhân viên.");
            return;
        }

        String noiDung = "Mã nhân viên     : " + nhanVien.getMaNV()
                + "\nHọ và tên        : " + hoac(nhanVien.getHoTen())
                + "\nGiới tính        : " + hoac(nhanVien.getGioiTinh())
                + "\nNgày sinh        : " + thay(DinhDang.ngay(nhanVien.getNgaySinh()))
                + "\nCCCD             : " + hoac(nhanVien.getCccd())
                + "\nĐiện thoại       : " + hoac(nhanVien.getSoDienThoai())
                + "\nEmail            : " + hoac(nhanVien.getEmail())
                + "\nĐịa chỉ          : " + hoac(nhanVien.getDiaChi())
                + "\nPhòng ban        : " + hoac(nhanVien.getTenPB())
                + "\nChức vụ          : " + hoac(nhanVien.getTenCV())
                + "\nNgười quản lý    : " + hoac(nhanVien.getTenQuanLy())
                + "\nNgày vào làm     : " + thay(DinhDang.ngay(nhanVien.getNgayVaoLam()))
                + "\nTrạng thái       : " + hoac(nhanVien.getTrangThai())
                + "\nLương            : " + tien(nhanVien) + " đ";

        JOptionPane.showMessageDialog(this, noiDung, "Thông tin nhân viên",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void tangLuong() {
        NhanVien nhanVien = doiTuongDangChon();
        if (nhanVien == null) {
            GiaoDien.canhBao(this, "Vui lòng chọn người cần tăng lương.");
            return;
        }

        BigDecimal luongHienTai = nhanVien.getLuong() == null
                ? BigDecimal.ZERO
                : nhanVien.getLuong();
        BigDecimal goiY = luongHienTai.multiply(new BigDecimal("1.10"));

        JTextField oLuong = GiaoDien.truong(16);
        oLuong.setText(goiY.setScale(0, java.math.RoundingMode.HALF_UP).toPlainString());

        JPanel khung = new JPanel(new BorderLayout(0, 8));
        khung.add(new JLabel("Tăng lương cho: " + nhanVien.getHoTen()
                + " (" + nhanVien.getMaNV() + ")"), BorderLayout.NORTH);
        khung.add(new JLabel("Lương hiện tại: " + tien(nhanVien) + " đ"), BorderLayout.CENTER);
        khung.add(oLuong, BorderLayout.SOUTH);

        int luaChon = JOptionPane.showConfirmDialog(this, khung, "Tăng lương",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (luaChon != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            BigDecimal luongMoi = new BigDecimal(oLuong.getText().trim().replace(".", ""));
            if (luongMoi.compareTo(luongHienTai) <= 0) {
                GiaoDien.canhBao(this, "Lương mới phải lớn hơn lương hiện tại.");
                return;
            }

            nhanVienDAO.tangLuong(nhanVien.getMaNV(), luongMoi);
            taiLai();
            lblThongBao.setForeground(GiaoDien.XANH_LA);
            lblThongBao.setText("Đã tăng lương cho " + nhanVien.getHoTen()
                    + " lên " + DinhDang.tien(luongMoi) + " đ");
        } catch (NumberFormatException loi) {
            GiaoDien.canhBao(this, "Lương phải là số, ví dụ: 15000000");
        } catch (SQLException loi) {
            GiaoDien.loiKetNoi(this, loi);
        }
    }

    private void thoiViec() {
        NhanVien nhanVien = doiTuongDangChon();
        if (nhanVien == null) {
            GiaoDien.canhBao(this, "Vui lòng chọn người cần cho thôi việc.");
            return;
        }

        if (NhanVien.DA_NGHI.equals(nhanVien.getTrangThai())) {
            GiaoDien.tin(this, nhanVien.getHoTen() + " đã nghỉ việc trước đó.");
            return;
        }

        boolean dongY = GiaoDien.xacNhan(this, "Cho " + nhanVien.getHoTen()
                + " (" + nhanVien.getMaNV() + ") nghỉ việc?\n\n"
                + "Nhân viên sẽ bị xóa khỏi danh sách và tài khoản bị khóa.");
        if (!dongY) {
            return;
        }

        try {
            nhanVienDAO.thoiViec(nhanVien.getMaNV());

            if ("CV01".equalsIgnoreCase(nhanVien.getMaCV())) {
                nhanVienDAO.capNhatQuanLyChoPhongBan(
                        nhanVien.getMaPB(),
                        this.giamDoc.getMaNV(),
                        nhanVien.getMaNV());
            }

            taiLai();
            lblThongBao.setForeground(GiaoDien.DO);
            lblThongBao.setText("Đã cho " + nhanVien.getHoTen() + " nghỉ việc và xóa khỏi danh sách.");
        } catch (SQLException loi) {
            GiaoDien.loiKetNoi(this, loi);
        }
    }

    private void thangChuc() {
        NhanVien nhanVien = doiTuongDangChon();
        if (nhanVien == null) {
            GiaoDien.canhBao(this, "Vui lòng chọn nhân viên cần thăng chức.");
            return;
        }

        if (laQuanLy(nhanVien.getMaNV())) {
            GiaoDien.tin(this, nhanVien.getHoTen() + " đã là quản lý.");
            return;
        }

        try {
            if (nhanVienDAO.phongBanDaCoTruongPhong(nhanVien.getMaPB())) {
                GiaoDien.canhBao(this, "Phòng ban " + nhanVien.getTenPB()
                        + " đã có Trưởng phòng. Không thể thăng chức thêm!");
                return;
            }

            boolean dongY = GiaoDien.xacNhan(this, "Thăng chức " + nhanVien.getHoTen()
                    + " (" + nhanVien.getMaNV() + ") lên quản lý phòng " + nhanVien.getTenPB() + "?\n\n"
                    + "Chức vụ thành \"Trưởng phòng\" và tài khoản có quyền quản lý.\n"
                    + "Toàn bộ nhân viên trong phòng sẽ được chuyển sang báo cáo cho người này.");
            if (!dongY) {
                return;
            }

            nhanVienDAO.thangChuc(nhanVien.getMaNV(), NhanVienDAO.MA_CV_QUAN_LY);

            nhanVienDAO.capNhatQuanLyChoPhongBan(
                    nhanVien.getMaPB(),
                    nhanVien.getMaNV(),
                    nhanVien.getMaNV());

            taiLai();
            lblThongBao.setForeground(GiaoDien.XANH_CHINH);
            lblThongBao.setText("Đã thăng chức " + nhanVien.getHoTen()
                    + " thành Trưởng phòng. Nhân viên trong phòng đã được cập nhật.");
        } catch (SQLException loi) {
            GiaoDien.loiKetNoi(this, loi);
        }
    }

    private boolean laQuanLy(String maNV) {
        for (DongBang dong : danhSachDong) {
            if (dong.maQL != null && dong.maQL.equalsIgnoreCase(maNV)) {
                return true;
            }
        }
        return false;
    }

    private String tien(NhanVien nhanVien) {
        return nhanVien.getLuong() == null || nhanVien.getLuong().signum() == 0
                ? "--"
                : DinhDang.tien(nhanVien.getLuong());
    }

    private static String hoac(String giaTri) {
        return giaTri == null || giaTri.trim().isEmpty() ? "--" : giaTri;
    }

    private static String thay(String giaTri) {
        return giaTri == null || giaTri.trim().isEmpty() ? "--" : giaTri;
    }
}
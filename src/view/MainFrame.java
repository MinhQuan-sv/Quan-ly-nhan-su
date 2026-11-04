package view;

import model.NhanVien;
import model.TaiKhoan;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    public static final String CARD_THONG_TIN = "THONG_TIN";

    public static final String CARD_NHAN_VIEN = "NHAN_VIEN";

    public static final String CARD_CHAM_CONG = "CHAM_CONG";

    public static final String CARD_QL_CHAM_CONG = "QL_CHAM_CONG";

    public static final String CARD_GIAM_DOC = "GIAM_DOC";

    private static final DateTimeFormatter DINH_DANG_DONG_HO =
            DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy");

    private final TaiKhoan taiKhoan;

    private final NhanVien nhanVien;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel pnlNoiDung = new JPanel(cardLayout);

    private final Map<String, JButton> nutTheoCard = new LinkedHashMap<>();

    private final JLabel lblViTri = new JLabel();
    private final JLabel lblDongHo = new JLabel();

    public MainFrame(TaiKhoan taiKhoan, NhanVien nhanVien) {
        this.taiKhoan = taiKhoan;
        this.nhanVien = nhanVien;

        setTitle("Quản lý Nhân sự & Chấm công - " + nhanVien.getTenHienThi()
                + " (" + taiKhoan.getTenVaiTro() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 760);
        setMinimumSize(new Dimension(1040, 640));
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        setJMenuBar(taoThanhMenu());
        add(taoPhanDau(), BorderLayout.NORTH);
        add(taoThanhBen(), BorderLayout.WEST);
        add(taoNoiDung(), BorderLayout.CENTER);
        add(taoThanhTrangThai(), BorderLayout.SOUTH);

        hienThi(taiKhoan.isGiamDoc() ? CARD_GIAM_DOC
                : (taiKhoan.isQuanLy() ? CARD_NHAN_VIEN : CARD_THONG_TIN));
        batDauDongHo();
    }

    private JMenuBar taoThanhMenu() {
        JMenuBar thanhMenu = new JMenuBar();

        JMenu mnuHeThong = new JMenu("Hệ thống");
        JMenuItem miDoiMatKhau = new JMenuItem("Đổi mật khẩu");
        miDoiMatKhau.addActionListener(e -> moDoiMatKhau());
        JMenuItem miDangXuat = new JMenuItem("Đăng xuất");
        miDangXuat.addActionListener(e -> dangXuat());
        JMenuItem miThoat = new JMenuItem("Thoát chương trình");
        miThoat.addActionListener(e -> System.exit(0));
        mnuHeThong.add(miDoiMatKhau);
        mnuHeThong.addSeparator();
        mnuHeThong.add(miDangXuat);
        mnuHeThong.addSeparator();
        mnuHeThong.add(miThoat);

        JMenu mnuChucNang = new JMenu("Chức năng");
        mnuChucNang.add(taoMuc("Thông tin của tôi", CARD_THONG_TIN));
        if (taiKhoan.isGiamDoc()) {
            mnuChucNang.add(taoMuc("Quản lý toàn công ty", CARD_GIAM_DOC));
        }
        if (taiKhoan.isQuanLy()) {
            mnuChucNang.add(taoMuc("Nhân viên cấp dưới", CARD_NHAN_VIEN));
        }
        mnuChucNang.add(taoMuc("Chấm công của tôi", CARD_CHAM_CONG));
        if (taiKhoan.isQuanLy()) {
            mnuChucNang.add(taoMuc("Chấm công nhân viên", CARD_QL_CHAM_CONG));
        }

        JMenu mnuTroGiup = new JMenu("Trợ giúp");
        JMenuItem miGioiThieu = new JMenuItem("Giới thiệu");
        miGioiThieu.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Ứng dụng Quản lý Nhân sự & Chấm công (Java Swing + JDBC)\n"
                        + "Phiên bản 1.1\n\n"
                        + "Giám đốc: xem mọi quản lý và nhân viên, tăng lương, thăng chức, cho thôi việc.\n"
                        + "Quản lý: xem/thêm nhân viên cấp dưới, chấm công nhân viên.\n"
                        + "Nhân viên: xem hồ sơ của mình, tự chấm công.\n"
                        + "Mọi vai trò đều đổi được mật khẩu.",
                "Giới thiệu", JOptionPane.INFORMATION_MESSAGE));
        mnuTroGiup.add(miGioiThieu);

        thanhMenu.add(mnuHeThong);
        thanhMenu.add(mnuChucNang);
        thanhMenu.add(mnuTroGiup);
        return thanhMenu;
    }

    private JMenuItem taoMuc(String nhan, final String tenThe) {
        JMenuItem muc = new JMenuItem(nhan);
        muc.addActionListener(e -> hienThi(tenThe));
        return muc;
    }

    private JPanel taoPhanDau() {
        JPanel phanDau = new JPanel(new BorderLayout(12, 0));
        phanDau.setBackground(GiaoDien.XANH_DAM);
        phanDau.setBorder(new EmptyBorder(10, 16, 10, 16));

        JPanel phanTrai = new JPanel();
        phanTrai.setOpaque(false);
        phanTrai.setLayout(new BoxLayout(phanTrai, BoxLayout.Y_AXIS));

        JLabel lblApp = new JLabel("HỆ THỐNG QUẢN LÝ NHÂN SỰ & CHẤM CÔNG");
        lblApp.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblApp.setForeground(Color.WHITE);
        lblApp.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblViTri.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblViTri.setForeground(new Color(0xB9C6DD));
        lblViTri.setAlignmentX(Component.LEFT_ALIGNMENT);

        phanTrai.add(lblApp);
        phanTrai.add(Box.createVerticalStrut(2));
        phanTrai.add(lblViTri);
        phanDau.add(phanTrai, BorderLayout.WEST);

        JPanel phanPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        phanPhai.setOpaque(false);

        JLabel lblNguoiDung = new JLabel(nhanVien.getTenHienThi() + " - " + nhanVien.getMaNV()
                + " (" + taiKhoan.getTenVaiTro() + ")");
        lblNguoiDung.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblNguoiDung.setForeground(new Color(0xD7DEEA));

        JButton btnDangXuat = GiaoDien.nut("Đăng xuất", GiaoDien.XANH_CHINH);
        btnDangXuat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDangXuat.addActionListener(e -> dangXuat());

        phanPhai.add(lblNguoiDung);
        phanPhai.add(btnDangXuat);
        phanDau.add(phanPhai, BorderLayout.EAST);
        return phanDau;
    }

    private JPanel taoThanhBen() {
        JPanel thanhBen = new JPanel();
        thanhBen.setLayout(new BoxLayout(thanhBen, BoxLayout.Y_AXIS));
        thanhBen.setBackground(GiaoDien.XANH_DAM);
        thanhBen.setBorder(new EmptyBorder(12, 8, 12, 8));
        thanhBen.setPreferredSize(new Dimension(220, 0));

        thanhBen.add(taoNutMenu("Thông tin của tôi", CARD_THONG_TIN));

        if (taiKhoan.isGiamDoc()) {
            thanhBen.add(Box.createVerticalStrut(4));
            thanhBen.add(taoNutMenu("Quản lý toàn công ty", CARD_GIAM_DOC));
        }

        if (taiKhoan.isQuanLy()) {
            thanhBen.add(Box.createVerticalStrut(4));
            thanhBen.add(taoNutMenu("Nhân viên cấp dưới", CARD_NHAN_VIEN));
        }

        thanhBen.add(Box.createVerticalStrut(4));
        thanhBen.add(taoNutMenu("Chấm công của tôi", CARD_CHAM_CONG));

        if (taiKhoan.isQuanLy()) {
            thanhBen.add(Box.createVerticalStrut(4));
            thanhBen.add(taoNutMenu("Chấm công nhân viên", CARD_QL_CHAM_CONG));
        }

        thanhBen.add(Box.createVerticalGlue());
        return thanhBen;
    }

    private JButton taoNutMenu(String nhan, final String tenThe) {
        JButton nut = new JButton(nhan);
        nut.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nut.setForeground(new Color(0xD7DEEA));
        nut.setBackground(GiaoDien.XANH_DAM);
        nut.setOpaque(true);
        nut.setContentAreaFilled(true);
        nut.setBorderPainted(false);
        nut.setFocusPainted(false);
        nut.setHorizontalAlignment(SwingConstants.LEFT);
        nut.setBorder(new EmptyBorder(10, 16, 10, 8));
        nut.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        nut.setAlignmentX(Component.LEFT_ALIGNMENT);
        nut.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nut.addActionListener(e -> hienThi(tenThe));

        nutTheoCard.put(tenThe, nut);
        return nut;
    }

    private JPanel taoNoiDung() {
        pnlNoiDung.setBackground(GiaoDien.NEN);

        pnlNoiDung.add(new ThongTinCaNhanPanel(taiKhoan, nhanVien), CARD_THONG_TIN);
        if (taiKhoan.isGiamDoc()) {
            pnlNoiDung.add(new GiamDocPanel(nhanVien), CARD_GIAM_DOC);
        }
        if (taiKhoan.isQuanLy()) {
            pnlNoiDung.add(new NhanVienPanel(nhanVien), CARD_NHAN_VIEN);
            pnlNoiDung.add(new QuanLyChamCongPanel(nhanVien), CARD_QL_CHAM_CONG);
        }
        pnlNoiDung.add(new ChamCongPanel(nhanVien), CARD_CHAM_CONG);
        return pnlNoiDung;
    }

    private JPanel taoThanhTrangThai() {
        JPanel thanh = new JPanel(new BorderLayout());
        thanh.setBackground(new Color(0xE8EDF6));
        thanh.setBorder(new EmptyBorder(5, 14, 5, 14));

        JLabel lblSanSang = new JLabel("Sẵn sàng");
        lblSanSang.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSanSang.setForeground(GiaoDien.CHU);

        lblDongHo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDongHo.setForeground(GiaoDien.CHU);

        thanh.add(lblSanSang, BorderLayout.WEST);
        thanh.add(lblDongHo, BorderLayout.EAST);
        return thanh;
    }

    public void hienThi(String tenThe) {
        cardLayout.show(pnlNoiDung, tenThe);

        for (Map.Entry<String, JButton> muc : nutTheoCard.entrySet()) {
            boolean dangChon = muc.getKey().equals(tenThe);
            muc.getValue().setBackground(dangChon ? GiaoDien.XANH_CHINH : GiaoDien.XANH_DAM);
            muc.getValue().setForeground(dangChon ? Color.WHITE : new Color(0xD7DEEA));
        }

        lblViTri.setText("Chức năng: " + tenChucNang(tenThe));
    }

    private static String tenChucNang(String tenThe) {
        switch (tenThe) {
            case CARD_THONG_TIN:
                return "Thông tin của tôi";
            case CARD_NHAN_VIEN:
                return "Nhân viên cấp dưới";
            case CARD_CHAM_CONG:
                return "Chấm công của tôi";
            case CARD_QL_CHAM_CONG:
                return "Chấm công nhân viên";
            case CARD_GIAM_DOC:
                return "Quản lý toàn công ty";
            default:
                return "";
        }
    }

    private void moDoiMatKhau() {
        DoiMatKhauDialog hopThoai = new DoiMatKhauDialog(this, taiKhoan);
        hopThoai.setVisible(true);
    }

    private void dangXuat() {
        if (!GiaoDien.xacNhan(this, "Bạn có chắc muốn đăng xuất?")) {
            return;
        }

        dispose();
        String loiKetNoi = util.KetNoiCSDL.kiemTraKetNoi();
        SwingUtilities.invokeLater(() -> new LoginFrame(loiKetNoi).setVisible(true));
    }

    private void batDauDongHo() {
        capNhatDongHo();
        new Timer(1000, e -> capNhatDongHo()).start();
    }

    private void capNhatDongHo() {
        lblDongHo.setText(LocalDateTime.now().format(DINH_DANG_DONG_HO));
    }
}

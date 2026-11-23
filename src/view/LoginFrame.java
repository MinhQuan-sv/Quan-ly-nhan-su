package view;

import dao.NhanVienDAO;
import dao.TaiKhoanDAO;
import model.NhanVien;
import model.TaiKhoan;
import util.KetNoiCSDL;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    private final JTextField txtTenDangNhap = GiaoDien.truong(20);
    private final JPasswordField txtMatKhau = new JPasswordField(20);
    private final JCheckBox chkHienMatKhau = new JCheckBox("Hiện mật khẩu");
    private final JButton btnDangNhap = GiaoDien.nut("ĐĂNG NHẬP", GiaoDien.XANH_CHINH);
    private final JButton btnThoat = GiaoDien.nut("THOÁT", GiaoDien.XAM);
    private final JLabel lblLoi = new JLabel(" ");
    private final JLabel lblKetNoi = new JLabel(" ");

    public LoginFrame(String loiKetNoi) {
        setTitle("Đăng nhập - Quản lý Nhân sự & Chấm công");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(taoNoiDung());
        setSize(460, 560);
        setResizable(false);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(btnDangNhap);

        capNhatTrangThaiKetNoi(loiKetNoi);
        SwingUtilities.invokeLater(txtTenDangNhap::requestFocusInWindow);
    }

    private JPanel taoNoiDung() {
        JPanel goc = new JPanel(new BorderLayout());
        goc.setBackground(GiaoDien.NEN);

        goc.add(taoPhanDau(), BorderLayout.NORTH);
        goc.add(taoForm(), BorderLayout.CENTER);
        goc.add(taoPhanCuoi(), BorderLayout.SOUTH);
        return goc;
    }

    private JPanel taoPhanDau() {
        JPanel phanDau = new JPanel(new BorderLayout());
        phanDau.setBackground(GiaoDien.XANH_DAM);
        phanDau.setBorder(new EmptyBorder(22, 26, 22, 26));

        JPanel cot = new JPanel(new BorderLayout(0, 4));
        cot.setOpaque(false);

        JLabel lblTieuDe = new JLabel("QUẢN LÝ NHÂN SỰ & CHẤM CÔNG");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTieuDe.setForeground(Color.WHITE);

        JLabel lblPhuDe = new JLabel("Đăng nhập bằng tài khoản được cấp");
        lblPhuDe.setFont(GiaoDien.CHU_THUONG);
        lblPhuDe.setForeground(new Color(0xB9C6DD));

        cot.add(lblTieuDe, BorderLayout.NORTH);
        cot.add(lblPhuDe, BorderLayout.CENTER);
        phanDau.add(cot, BorderLayout.WEST);
        return phanDau;
    }

    private JPanel taoForm() {
        JPanel than = new JPanel(new GridBagLayout());
        than.setBackground(Color.WHITE);
        than.setBorder(new EmptyBorder(18, 30, 10, 30));

        GridBagConstraints viTri = new GridBagConstraints();
        viTri.gridx = 0;
        viTri.fill = GridBagConstraints.HORIZONTAL;
        viTri.weightx = 1;
        viTri.insets = new Insets(4, 0, 4, 0);

        viTri.gridy = 0;
        than.add(GiaoDien.nhan("Tên đăng nhập"), viTri);

        viTri.gridy = 1;
        txtTenDangNhap.setPreferredSize(new Dimension(240, 32));
        than.add(txtTenDangNhap, viTri);

        viTri.gridy = 2;
        than.add(GiaoDien.nhan("Mật khẩu"), viTri);

        viTri.gridy = 3;
        txtMatKhau.setPreferredSize(new Dimension(240, 32));
        txtMatKhau.setFont(GiaoDien.CHU_THUONG);
        txtMatKhau.addActionListener(e -> xuLyDangNhap());
        than.add(txtMatKhau, viTri);

        viTri.gridy = 4;
        chkHienMatKhau.setOpaque(false);
        chkHienMatKhau.setFont(GiaoDien.CHU_THUONG);
        chkHienMatKhau.addActionListener(e ->
                txtMatKhau.setEchoChar(chkHienMatKhau.isSelected() ? (char) 0 : '\u2022'));
        than.add(chkHienMatKhau, viTri);

        viTri.gridy = 5;
        lblLoi.setFont(GiaoDien.CHU_THUONG);
        lblLoi.setForeground(GiaoDien.DO);
        than.add(lblLoi, viTri);

        viTri.gridy = 6;
        JPanel hangNut = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        hangNut.setOpaque(false);
        btnDangNhap.setPreferredSize(new Dimension(150, 38));
        btnThoat.setPreferredSize(new Dimension(110, 38));
        btnDangNhap.addActionListener(this::khiBamDangNhap);
        btnThoat.addActionListener(e -> System.exit(0));
        hangNut.add(btnDangNhap);
        hangNut.add(btnThoat);
        than.add(hangNut, viTri);

        viTri.gridy = 7;
        JLabel lblGoiY = new JLabel("<html>Tài khoản mẫu (mật khẩu 123456):<br>"
                + "<b>gd.quan</b>, <b>gd.nhan</b>, <b>gd.thien</b> - giám đốc<br>"
                + "<b>thoai.bq</b>, <b>quang.lnn</b>, <b>nguyen.hg</b> - trưởng phòng<br>"
                + "<b>nva</b> ... <b>lvf</b> - nhân viên</html>");
        lblGoiY.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblGoiY.setForeground(GiaoDien.CHU_NHAT);
        than.add(lblGoiY, viTri);
        return than;
    }

    private JPanel taoPhanCuoi() {
        JPanel phanCuoi = new JPanel(new BorderLayout(6, 0));
        phanCuoi.setBackground(GiaoDien.NEN);
        phanCuoi.setBorder(new EmptyBorder(6, 18, 12, 18));

        lblKetNoi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblKetNoi.setForeground(GiaoDien.CHU_NHAT);
        lblKetNoi.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblPhienBan = new JLabel("v1.0");
        lblPhienBan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPhienBan.setForeground(GiaoDien.CHU_NHAT);

        phanCuoi.add(lblKetNoi, BorderLayout.WEST);
        phanCuoi.add(lblPhienBan, BorderLayout.EAST);
        return phanCuoi;
    }

    private void capNhatTrangThaiKetNoi(String loiKetNoi) {
        if (loiKetNoi == null) {
            lblKetNoi.setText("● Đã kết nối CSDL");
            lblKetNoi.setForeground(GiaoDien.XANH_LA);
            lblKetNoi.setToolTipText(null);
            return;
        }

        lblKetNoi.setText("● Chưa kết nối được CSDL (bấm để xem chi tiết)");
        lblKetNoi.setForeground(GiaoDien.DO);
        lblKetNoi.setToolTipText("<html><pre>" + loiKetNoi + "</pre></html>");

        for (java.awt.event.MouseListener boLangNghe : lblKetNoi.getMouseListeners()) {
            lblKetNoi.removeMouseListener(boLangNghe);
        }
        lblKetNoi.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent suKien) {
                GiaoDien.loi(LoginFrame.this, loiKetNoi);
            }
        });
    }

    private void khiBamDangNhap(ActionEvent suKien) {
        xuLyDangNhap();
    }

    private void xuLyDangNhap() {
        String tenDangNhap = txtTenDangNhap.getText() == null
                ? "" : txtTenDangNhap.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());

        if (tenDangNhap.isEmpty() || matKhau.isEmpty()) {
            lblLoi.setText("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            return;
        }

        btnDangNhap.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        lblLoi.setText(" ");

        try {
            TaiKhoan taiKhoan = taiKhoanDAO.dangNhap(tenDangNhap, matKhau);
            if (taiKhoan == null) {
                lblLoi.setText("Sai tên đăng nhập hoặc mật khẩu.");
                txtMatKhau.setText("");
                txtMatKhau.requestFocusInWindow();
                return;
            }

            if (!taiKhoan.isHoatDong()) {
                lblLoi.setText("Tài khoản đã bị khóa, liên hệ quản trị viên.");
                return;
            }

            NhanVien nhanVien = nhanVienDAO.layTheoMa(taiKhoan.getMaNV());
            if (nhanVien == null) {
                lblLoi.setText("Tài khoản chưa được gắn với hồ sơ nhân viên.");
                return;
            }

            dispose();
            SwingUtilities.invokeLater(() -> new MainFrame(taiKhoan, nhanVien).setVisible(true));
        } catch (SQLException loi) {
            lblLoi.setText("Không đăng nhập được: " + loi.getMessage());
            capNhatTrangThaiKetNoi(KetNoiCSDL.moTaLoi(loi));
            GiaoDien.loiKetNoi(this, loi);
        } finally {
            btnDangNhap.setEnabled(true);
            setCursor(Cursor.getDefaultCursor());
        }
    }
}

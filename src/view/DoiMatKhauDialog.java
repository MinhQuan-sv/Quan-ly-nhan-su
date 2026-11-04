package view;

import dao.TaiKhoanDAO;
import model.TaiKhoan;
import util.KetNoiCSDL;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

public class DoiMatKhauDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final TaiKhoan taiKhoan;
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    private final JPasswordField txtMatKhauCu = new JPasswordField(18);
    private final JPasswordField txtMatKhauMoi = new JPasswordField(18);
    private final JPasswordField txtNhapLai = new JPasswordField(18);
    private final JLabel lblLoi = new JLabel(" ");

    private boolean daDoi = false;

    public DoiMatKhauDialog(Frame cha, TaiKhoan taiKhoan) {
        super(cha, "Đổi mật khẩu", true);
        this.taiKhoan = taiKhoan;

        setLayout(new BorderLayout(0, 12));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);

        add(taoPhanDau(), BorderLayout.NORTH);
        add(taoForm(), BorderLayout.CENTER);
        add(taoPhanCuoi(), BorderLayout.SOUTH);

        txtMatKhauCu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtMatKhauMoi.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNhapLai.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        getRootPane().setDefaultButton(null);
        pack();
        setLocationRelativeTo(cha);
    }

    public boolean isDaDoi() {
        return daDoi;
    }

    private JPanel taoPhanDau() {
        JPanel phan = new JPanel(new BorderLayout(0, 4));
        phan.setBackground(GiaoDien.XANH_DAM);
        phan.setBorder(new EmptyBorder(14, 16, 12, 16));

        JLabel lblTieuDe = new JLabel("ĐỔI MẬT KHẨU");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTieuDe.setForeground(java.awt.Color.WHITE);

        JLabel lblCon = new JLabel("Tài khoản: " + taiKhoan.getTenDangNhap()
                + "  •  " + taiKhoan.getTenVaiTro());
        lblCon.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCon.setForeground(new java.awt.Color(0xB9C6DD));

        phan.add(lblTieuDe, BorderLayout.NORTH);
        phan.add(lblCon, BorderLayout.SOUTH);
        return phan;
    }

    private JPanel taoForm() {
        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(new EmptyBorder(6, 18, 0, 18));
        form.setOpaque(false);

        form.add(nhan("Mật khẩu hiện tại:"));
        form.add(txtMatKhauCu);
        form.add(nhan("Mật khẩu mới:"));
        form.add(txtMatKhauMoi);
        form.add(nhan("Nhập lại mật khẩu mới:"));
        form.add(txtNhapLai);

        return form;
    }

    private JPanel taoPhanCuoi() {
        JPanel phan = new JPanel(new BorderLayout(0, 8));
        phan.setBorder(new EmptyBorder(6, 18, 14, 18));
        phan.setOpaque(false);

        lblLoi.setForeground(GiaoDien.DO);
        lblLoi.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel hangNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        hangNut.setOpaque(false);

        JButton nutHuy = GiaoDien.nut("Hủy", GiaoDien.XAM);
        nutHuy.addActionListener(e -> dispose());

        JButton nutLuu = GiaoDien.nut("Đổi mật khẩu", GiaoDien.XANH_CHINH);
        nutLuu.addActionListener(e -> doiMatKhau());

        hangNut.add(nutHuy);
        hangNut.add(nutLuu);

        phan.add(lblLoi, BorderLayout.NORTH);
        phan.add(hangNut, BorderLayout.SOUTH);
        return phan;
    }

    private static JLabel nhan(String noiDung) {
        JLabel lbl = new JLabel(noiDung);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(GiaoDien.CHU);
        return lbl;
    }

    private void doiMatKhau() {
        String matKhauCu = new String(txtMatKhauCu.getPassword());
        String matKhauMoi = new String(txtMatKhauMoi.getPassword());
        String nhapLai = new String(txtNhapLai.getPassword());

        if (matKhauCu.trim().isEmpty() || matKhauMoi.trim().isEmpty() || nhapLai.trim().isEmpty()) {
            lblLoi.setText("Vui lòng nhập đủ cả 3 ô.");
            return;
        }
        if (matKhauMoi.length() < 6) {
            lblLoi.setText("Mật khẩu mới phải có ít nhất 6 ký tự.");
            return;
        }
        if (!matKhauMoi.equals(nhapLai)) {
            lblLoi.setText("Mật khẩu mới nhập lại không khớp.");
            return;
        }
        if (matKhauMoi.equals(matKhauCu)) {
            lblLoi.setText("Mật khẩu mới phải khác mật khẩu hiện tại.");
            return;
        }

        try {
            if (taiKhoanDAO.dangNhap(taiKhoan.getTenDangNhap(), matKhauCu) == null) {
                lblLoi.setText("Mật khẩu hiện tại không đúng.");
                return;
            }

            taiKhoanDAO.doiMatKhau(taiKhoan.getTenDangNhap(), matKhauMoi);
            daDoi = true;

            GiaoDien.tin(this, "Đã đổi mật khẩu.\nLần đăng nhập sau hãy dùng mật khẩu mới.");
            dispose();
        } catch (SQLException loi) {
            GiaoDien.loiKetNoi(this, loi);
        }
    }
}

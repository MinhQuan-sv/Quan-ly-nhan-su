package view;

import dao.NhanVienDAO;
import model.NhanVien;
import model.TaiKhoan;
import util.DinhDang;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.time.LocalTime;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

public class ThongTinCaNhanPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final TaiKhoan taiKhoan;
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    private NhanVien nhanVien;

    private final JLabel lblMaNV = taoGiaTri();
    private final JLabel lblHoTen = taoGiaTri();
    private final JLabel lblNgaySinh = taoGiaTri();
    private final JLabel lblGioiTinh = taoGiaTri();
    private final JLabel lblCccd = taoGiaTri();
    private final JLabel lblSoDienThoai = taoGiaTri();
    private final JLabel lblEmail = taoGiaTri();
    private final JLabel lblDiaChi = taoGiaTri();
    private final JLabel lblNgayVaoLam = taoGiaTri();
    private final JLabel lblTrangThai = taoGiaTri();
    private final JLabel lblPhongBan = taoGiaTri();
    private final JLabel lblChucVu = taoGiaTri();
    private final JLabel lblCapTren = taoGiaTri();
    private final JLabel lblTaiKhoan = taoGiaTri();
    private final JLabel lblVaiTro = taoGiaTri();
    private final JLabel lblSoCapDuoi = taoGiaTri();

    private final JLabel lblThongBao = new JLabel(" ");

    public ThongTinCaNhanPanel(TaiKhoan taiKhoan, NhanVien nhanVien) {
        this.taiKhoan = taiKhoan;
        this.nhanVien = nhanVien;

        setLayout(new BorderLayout(0, 10));
        setBackground(GiaoDien.NEN);
        setBorder(new EmptyBorder(14, 14, 14, 14));

        add(taoPhanDau(), BorderLayout.NORTH);
        add(taoHoSo(), BorderLayout.CENTER);
        add(taoPhanCuoi(), BorderLayout.SOUTH);

        capNhatHienThi();
        taiLai();
    }

    private JPanel taoPhanDau() {
        JPanel phan = new JPanel(new BorderLayout(0, 2));
        phan.setOpaque(false);
        phan.add(GiaoDien.tieuDe("THÔNG TIN CỦA TÔI"), BorderLayout.NORTH);

        JLabel lblPhu = new JLabel("Hồ sơ nhân sự - chỉ xem, mọi thay đổi do quản lý thực hiện");
        lblPhu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPhu.setForeground(GiaoDien.CHU_NHAT);
        phan.add(lblPhu, BorderLayout.CENTER);
        return phan;
    }

    private JScrollPane taoHoSo() {
        JPanel hoSo = new JPanel(new GridBagLayout());
        hoSo.setBackground(Color.WHITE);
        hoSo.setBorder(new EmptyBorder(16, 18, 16, 18));

        GridBagConstraints viTri = new GridBagConstraints();
        viTri.insets = new Insets(6, 6, 6, 14);
        viTri.anchor = GridBagConstraints.WEST;
        viTri.fill = GridBagConstraints.HORIZONTAL;

        int hang = 0;

        hang = themTieuMuc(hoSo, viTri, hang, "THÔNG TIN CÁ NHÂN");
        hang = themCap(hoSo, viTri, hang, "Mã nhân viên", lblMaNV, "Họ và tên", lblHoTen);
        hang = themCap(hoSo, viTri, hang, "Ngày sinh", lblNgaySinh, "Giới tính", lblGioiTinh);
        hang = themCap(hoSo, viTri, hang, "CCCD", lblCccd, "Số điện thoại", lblSoDienThoai);
        hang = themCap(hoSo, viTri, hang, "Email", lblEmail, "Trạng thái", lblTrangThai);
        hang = themDongDai(hoSo, viTri, hang, "Địa chỉ", lblDiaChi);

        viTri.gridx = 0;
        viTri.gridy = hang++;
        viTri.gridwidth = 4;
        JPanel vach = new JPanel();
        vach.setBackground(GiaoDien.VIEN);
        vach.setPreferredSize(new java.awt.Dimension(10, 1));
        hoSo.add(vach, viTri);
        viTri.gridwidth = 1;
        viTri.insets = new Insets(6, 6, 6, 14);

        hang = themTieuMuc(hoSo, viTri, hang, "CÔNG VIỆC");
        hang = themCap(hoSo, viTri, hang, "Phòng ban", lblPhongBan, "Chức vụ", lblChucVu);
        hang = themCap(hoSo, viTri, hang, "Ngày vào làm", lblNgayVaoLam,
                "Cấp trên trực tiếp", lblCapTren);
        hang = themCap(hoSo, viTri, hang, "Tài khoản", lblTaiKhoan, "Vai trò", lblVaiTro);

        if (taiKhoan.isQuanLy()) {
            themCap(hoSo, viTri, hang, "Nhân viên cấp dưới", lblSoCapDuoi, "", GiaoDien.giaTri(""));
        }

        JScrollPane cuon = new JScrollPane(hoSo);
        cuon.setBorder(javax.swing.BorderFactory.createLineBorder(GiaoDien.VIEN));
        cuon.getViewport().setBackground(Color.WHITE);
        cuon.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return cuon;
    }

    private JPanel taoPhanCuoi() {
        JPanel phan = new JPanel(new BorderLayout());
        phan.setOpaque(false);

        lblThongBao.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblThongBao.setForeground(GiaoDien.CHU_NHAT);

        JButton nutLamMoi = GiaoDien.nut("Làm mới", GiaoDien.XANH_CHINH);
        nutLamMoi.addActionListener(e -> taiLai());

        JPanel benPhai = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        benPhai.setOpaque(false);
        benPhai.add(nutLamMoi);

        phan.add(lblThongBao, BorderLayout.WEST);
        phan.add(benPhai, BorderLayout.EAST);
        return phan;
    }

    private static JLabel taoGiaTri() {
        JLabel nhan = new JLabel("--");
        nhan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nhan.setForeground(GiaoDien.CHU);
        return nhan;
    }

    private int themTieuMuc(JPanel panel, GridBagConstraints viTri, int hang, String tieuDe) {
        JLabel nhan = new JLabel(tieuDe);
        nhan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nhan.setForeground(GiaoDien.XANH_CHINH);

        viTri.gridx = 0;
        viTri.gridy = hang;
        viTri.gridwidth = 4;
        viTri.weightx = 1;
        panel.add(nhan, viTri);

        viTri.gridwidth = 1;
        viTri.weightx = 0;
        return hang + 1;
    }

    private int themCap(JPanel panel, GridBagConstraints viTri, int hang,
                        String nhan1, JLabel giaTri1, String nhan2, JLabel giaTri2) {
        viTri.gridy = hang;

        viTri.gridx = 0;
        viTri.weightx = 0;
        panel.add(GiaoDien.nhan(nhan1), viTri);

        viTri.gridx = 1;
        viTri.weightx = 1;
        panel.add(giaTri1, viTri);

        viTri.gridx = 2;
        viTri.weightx = 0;
        panel.add(GiaoDien.nhan(nhan2), viTri);

        viTri.gridx = 3;
        viTri.weightx = 1;
        panel.add(giaTri2, viTri);

        return hang + 1;
    }

    private int themDongDai(JPanel panel, GridBagConstraints viTri, int hang,
                            String nhan, JLabel giaTri) {
        viTri.gridy = hang;

        viTri.gridx = 0;
        viTri.weightx = 0;
        panel.add(GiaoDien.nhan(nhan), viTri);

        viTri.gridx = 1;
        viTri.gridwidth = 3;
        viTri.weightx = 1;
        panel.add(giaTri, viTri);

        viTri.gridwidth = 1;
        return hang + 1;
    }

    private void taiLai() {
        try {
            NhanVien banMoi = nhanVienDAO.layTheoMa(taiKhoan.getMaNV());
            if (banMoi != null) {
                nhanVien = banMoi;
            }

            if (taiKhoan.isQuanLy()) {
                int soCapDuoi = nhanVienDAO.demCapDuoi(taiKhoan.getMaNV());
                lblSoCapDuoi.setText(soCapDuoi + " người");
            }

            capNhatHienThi();

            lblThongBao.setForeground(GiaoDien.CHU_NHAT);
            lblThongBao.setToolTipText(null);
            lblThongBao.setText("Cập nhật lúc " + DinhDang.gio(LocalTime.now()));
        } catch (SQLException loi) {
            capNhatHienThi();
            lblThongBao.setForeground(GiaoDien.DO);
            lblThongBao.setText("Không tải được dữ liệu mới nhất từ CSDL.");
            lblThongBao.setToolTipText("<html><pre>" + util.KetNoiCSDL.moTaLoi(loi) + "</pre></html>");
        }
    }

    private void capNhatHienThi() {
        lblMaNV.setText(hoac(nhanVien.getMaNV()));
        lblHoTen.setText(hoac(nhanVien.getHoTen()));
        lblNgaySinh.setText(hoac(DinhDang.ngay(nhanVien.getNgaySinh())));
        lblGioiTinh.setText(hoac(nhanVien.getGioiTinh()));
        lblCccd.setText(hoac(nhanVien.getCccd()));
        lblSoDienThoai.setText(hoac(nhanVien.getSoDienThoai()));
        lblEmail.setText(hoac(nhanVien.getEmail()));
        lblDiaChi.setText(hoac(nhanVien.getDiaChi()));
        lblNgayVaoLam.setText(hoac(DinhDang.ngay(nhanVien.getNgayVaoLam())));

        lblPhongBan.setText(hoac(nhanVien.getTenPB(), nhanVien.getMaPB()));
        lblChucVu.setText(hoac(nhanVien.getTenCV(), nhanVien.getMaCV()));
        lblCapTren.setText(hoac(nhanVien.getTenQuanLy(), nhanVien.getMaQuanLy(),
                "Không có (cấp cao nhất)"));

        lblTrangThai.setText(hoac(nhanVien.getTrangThai()));
        lblTrangThai.setForeground(nhanVien.isDangLam() ? GiaoDien.XANH_LA : GiaoDien.CAM);

        lblTaiKhoan.setText(hoac(taiKhoan.getTenDangNhap()));
        lblVaiTro.setText(taiKhoan.getTenVaiTro());
    }

    private static String hoac(String giaTri) {
        return giaTri == null || giaTri.trim().isEmpty() ? "--" : giaTri;
    }

    private static String hoac(String uuTien, String duPhong) {
        if (uuTien != null && !uuTien.trim().isEmpty()) {
            return uuTien;
        }
        return hoac(duPhong);
    }

    private static String hoac(String uuTien, String duPhong, String macDinh) {
        if (uuTien != null && !uuTien.trim().isEmpty()) {
            return uuTien;
        }
        if (duPhong != null && !duPhong.trim().isEmpty()) {
            return duPhong;
        }
        return macDinh;
    }
}

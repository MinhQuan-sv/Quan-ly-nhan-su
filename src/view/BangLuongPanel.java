package view;

import dao.BangLuongDAO;
import dao.ChamCongDAO;
import dao.NhanVienDAO;
import java.awt.BorderLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import model.BangLuong;
import model.NhanVien;
import model.TaiKhoan;
import service.LuongService;
import util.DinhDang;
import util.KetNoiCSDL;

public class BangLuongPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final String[] TEN_COT = {
            "Mã NV", "Họ tên", "Phòng ban", "Lương CB",
            "Lương công", "Ngày công", "Đi muộn", "Tiền phạt", "Thực lĩnh"
    };

    private final TaiKhoan taiKhoan;
    private final NhanVien nhanVien;

    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final ChamCongDAO chamCongDAO = new ChamCongDAO();
    private final BangLuongDAO bangLuongDAO = new BangLuongDAO();

    private final JComboBox<Integer> cbThang = new JComboBox<>();
    private final JTextField txtNam = GiaoDien.truong(6);
    private final JButton btnXem = GiaoDien.nut("Xem bảng lương", GiaoDien.XANH_CHINH);
    private final JButton btnChotLuong = GiaoDien.nut("Chốt lương", GiaoDien.XANH_LA);
    private final JButton btnHuyChot = GiaoDien.nut("Hủy chốt", GiaoDien.DO);

    private final DefaultTableModel moHinhBang = GiaoDien.moHinhBang(TEN_COT);
    private final JTable bang = GiaoDien.bang(moHinhBang);

    private final JLabel lblTong = new JLabel(" ");
    private final JLabel lblThongBao = new JLabel(" ");
    private final JLabel lblTrangThaiChot = new JLabel(" ");

    public BangLuongPanel(TaiKhoan taiKhoan, NhanVien nhanVien) {
        this.taiKhoan = taiKhoan;
        this.nhanVien = nhanVien;

        setLayout(new BorderLayout(0, 10));
        setBackground(GiaoDien.NEN);
        setBorder(new EmptyBorder(14, 14, 14, 14));

        add(taoPhanDau(), BorderLayout.NORTH);
        add(GiaoDien.cuon(bang), BorderLayout.CENTER);
        add(taoPhanCuoi(), BorderLayout.SOUTH);

        GiaoDien.canhPhai(bang, 3, 4, 5, 6, 7, 8);

        cbThang.setSelectedItem(LocalDate.now().getMonthValue());
        txtNam.setText(String.valueOf(LocalDate.now().getYear()));

        taiLai();
    }

    private JPanel taoPhanDau() {
        JPanel phan = new JPanel(new BorderLayout(0, 8));
        phan.setOpaque(false);
        phan.add(GiaoDien.tieuDe(tieuDe()), BorderLayout.NORTH);

        JPanel thanhCongCu = GiaoDien.thanhCongCu();

        thanhCongCu.add(GiaoDien.nhan("Tháng:"));
        for (int thang = 1; thang <= 12; thang++) {
            cbThang.addItem(thang);
        }
        thanhCongCu.add(cbThang);

        thanhCongCu.add(GiaoDien.nhan("Năm:"));
        thanhCongCu.add(txtNam);

        btnXem.addActionListener(e -> taiLai());
        thanhCongCu.add(btnXem);

        if (taiKhoan.isGiamDoc()) {
            btnChotLuong.addActionListener(e -> chotLuong());
            btnHuyChot.addActionListener(e -> huyChot());
            thanhCongCu.add(btnChotLuong);
            thanhCongCu.add(btnHuyChot);
        }

        JPanel wrap = new JPanel(new BorderLayout(0, 2));
        wrap.setOpaque(false);
        wrap.add(thanhCongCu, BorderLayout.CENTER);

        JLabel ghiChu = new JLabel(
                "Quy tắc phạt: Lần 1 phạt 50k, lần 2 phạt 100k, lần 3 phạt 200k, "
                + "lần 4 phạt 500k. Từ lần 5 trở lên trừ 30% lương tháng.");
        ghiChu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ghiChu.setForeground(GiaoDien.CHU_NHAT);
        wrap.add(ghiChu, BorderLayout.SOUTH);

        phan.add(wrap, BorderLayout.CENTER);
        return phan;
    }

    private String tieuDe() {
        if (taiKhoan.isGiamDoc()) return "BẢNG LƯƠNG TOÀN CÔNG TY";
        if (taiKhoan.isQuanLy()) return "BẢNG LƯƠNG NHÂN VIÊN CẤP DƯỚI";
        return "LƯƠNG CỦA TÔI";
    }

    private JPanel taoPhanCuoi() {
        JPanel phan = new JPanel(new BorderLayout());
        phan.setOpaque(false);

        lblTong.setFont(GiaoDien.CHU_DAM);
        lblTong.setForeground(GiaoDien.XANH_DAM);

        lblThongBao.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblThongBao.setForeground(GiaoDien.CHU_NHAT);

        lblTrangThaiChot.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTrangThaiChot.setHorizontalAlignment(JLabel.CENTER);

        phan.add(lblTong, BorderLayout.WEST);
        phan.add(lblTrangThaiChot, BorderLayout.CENTER);
        phan.add(lblThongBao, BorderLayout.EAST);
        return phan;
    }

    private void taiLai() {
        Integer thang = (Integer) cbThang.getSelectedItem();
        Integer nam = docNam();
        if (thang == null || nam == null) return;

        try {
            List<BangLuong> danhSach = null;

            if (taiKhoan.isGiamDoc()) {
                danhSach = bangLuongDAO.layTheoThang(thang, nam);
            } else if (taiKhoan.isQuanLy()) {
                danhSach = bangLuongDAO.layCuaQuanLy(nhanVien.getMaNV(), thang, nam);
            } else {
                BangLuong mot = bangLuongDAO.layCuaNhanVien(nhanVien.getMaNV(), thang, nam);
                danhSach = new ArrayList<>();
                if (mot != null) danhSach.add(mot);
            }

            boolean daChot = !danhSach.isEmpty();

            if (!daChot) {
                List<NhanVien> dsNhanVien = layDanhSachTheoVaiTro();
                danhSach = LuongService.tinhBangLuong(dsNhanVien, thang, nam, chamCongDAO);
            }

            napBang(danhSach);

            BigDecimal tong = BigDecimal.ZERO;
            for (BangLuong bl : danhSach) {
                tong = tong.add(bl.getLuongThucLinh());
            }
            lblTong.setText("Tổng " + danhSach.size() + " nhân viên — Tổng quỹ lương: "
                    + DinhDang.tien(tong) + " đ");

            if (daChot) {
                lblTrangThaiChot.setText("● Đã chốt lương tháng " + thang + "/" + nam);
                lblTrangThaiChot.setForeground(GiaoDien.XANH_LA);
            } else {
                lblTrangThaiChot.setText("○ Chưa chốt lương tháng " + thang + "/" + nam);
                lblTrangThaiChot.setForeground(GiaoDien.CAM);
            }

            if (taiKhoan.isGiamDoc()) {
                btnChotLuong.setEnabled(!daChot);
                btnHuyChot.setEnabled(daChot);
            }

            lblThongBao.setForeground(GiaoDien.CHU_NHAT);
            lblThongBao.setToolTipText(null);
            lblThongBao.setText("Ngày công chuẩn tháng " + thang + "/" + nam + ": "
                    + LuongService.ngayCongChuan(thang, nam) + " ngày");

        } catch (SQLException loi) {
            moHinhBang.setRowCount(0);
            lblTong.setText(" ");
            lblThongBao.setForeground(GiaoDien.DO);
            lblThongBao.setText("Không tải được bảng lương.");
            lblThongBao.setToolTipText("<html><pre>" + KetNoiCSDL.moTaLoi(loi) + "</pre></html>");
        }
    }

    private void chotLuong() {
        Integer thang = (Integer) cbThang.getSelectedItem();
        Integer nam = docNam();
        if (thang == null || nam == null) return;

        if (!GiaoDien.xacNhan(this,
                "Chốt bảng lương tháng " + thang + "/" + nam + "?\n\n"
                + "Sau khi chốt, dữ liệu lương tháng này sẽ được lưu vĩnh viễn\n"
                + "và KHÔNG bị thay đổi khi sửa chấm công hồi tố.")) {
            return;
        }

        try {
            List<NhanVien> dsNhanVien = nhanVienDAO.layTatCaDangLam();
            List<BangLuong> danhSach = LuongService.tinhBangLuong(dsNhanVien, thang, nam, chamCongDAO);
            bangLuongDAO.chotLuong(danhSach, thang, nam);

            GiaoDien.tin(this, "Đã chốt lương tháng " + thang + "/" + nam
                    + " cho " + danhSach.size() + " nhân viên.");
            taiLai();
        } catch (SQLException loi) {
            GiaoDien.loiKetNoi(this, loi);
        }
    }

    private void huyChot() {
        Integer thang = (Integer) cbThang.getSelectedItem();
        Integer nam = docNam();
        if (thang == null || nam == null) return;

        if (!GiaoDien.xacNhan(this,
                "Hủy chốt lương tháng " + thang + "/" + nam + "?\n\n"
                + "Toàn bộ bảng lương tháng này sẽ bị xóa khỏi DB.\n"
                + "Lần xem sau sẽ tính lại theo dữ liệu chấm công hiện tại.")) {
            return;
        }

        try {
            bangLuongDAO.huyChot(thang, nam);
            GiaoDien.tin(this, "Đã hủy chốt lương tháng " + thang + "/" + nam + ".");
            taiLai();
        } catch (SQLException loi) {
            GiaoDien.loiKetNoi(this, loi);
        }
    }

    private List<NhanVien> layDanhSachTheoVaiTro() throws SQLException {
        if (taiKhoan.isGiamDoc()) return nhanVienDAO.layTatCaDangLam();
        if (taiKhoan.isQuanLy()) return nhanVienDAO.layCapDuoi(nhanVien.getMaNV());

        List<NhanVien> chiMot = new ArrayList<>();
        NhanVien moi = nhanVienDAO.layTheoMa(nhanVien.getMaNV());
        if (moi != null) chiMot.add(moi);
        return chiMot;
    }

    private void napBang(List<BangLuong> danhSach) {
        moHinhBang.setRowCount(0);
        for (BangLuong bangLuong : danhSach) {
            moHinhBang.addRow(new Object[]{
                    bangLuong.getMaNV(),
                    bangLuong.getHoTen() == null ? "" : bangLuong.getHoTen(),
                    bangLuong.getTenPB() == null ? "--" : bangLuong.getTenPB(),
                    DinhDang.tien(bangLuong.getLuongCoBan()),
                    DinhDang.tien(bangLuong.getLuongCong()),
                    bangLuong.getNgayCongThucTe() + "/" + bangLuong.getNgayCongChuan(),
                    bangLuong.getSoLanDiMuon(),
                    DinhDang.tien(bangLuong.getTienPhat()),
                    DinhDang.tien(bangLuong.getLuongThucLinh())
            });
        }
    }

    private Integer docNam() {
        try {
            return Integer.valueOf(txtNam.getText().trim());
        } catch (RuntimeException loi) {
            GiaoDien.canhBao(this, "Năm không hợp lệ, ví dụ: 2026.");
            return null;
        }
    }
}
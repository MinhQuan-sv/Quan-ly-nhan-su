package view;

import dao.ChamCongDAO;
import dao.DanhMucDAO;
import model.CaLamViec;
import model.ChamCong;
import model.NhanVien;
import service.ChamCongService;
import util.DinhDang;
import util.KetNoiCSDL;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
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

public class ChamCongPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final String[] TEN_COT = {
            "Ngày", "Ca", "Giờ vào", "Giờ ra", "Số giờ làm", "Trạng thái", "Ghi chú"
    };

    private final NhanVien nhanVien;

    private final ChamCongDAO chamCongDAO = new ChamCongDAO();
    private final DanhMucDAO danhMucDAO = new DanhMucDAO();

    private final JComboBox<Integer> cbThang = new JComboBox<>();
    private final JTextField txtNam = GiaoDien.truong(6);
    private final JComboBox<CaLamViec> cbCa = new JComboBox<>();
    private final JButton btnVao = GiaoDien.nut("Chấm công vào", GiaoDien.XANH_CHINH);
    private final JButton btnRa = GiaoDien.nut("Chấm công ra", GiaoDien.XANH_LA);

    private final DefaultTableModel moHinhBang = GiaoDien.moHinhBang(TEN_COT);
    private final JTable bang = GiaoDien.bang(moHinhBang);

    private final JLabel lblNgayHomNay = GiaoDien.giaTri("--");
    private final JLabel lblCaHomNay = GiaoDien.giaTri("--");
    private final JLabel lblVaoHomNay = GiaoDien.giaTri("--");
    private final JLabel lblRaHomNay = GiaoDien.giaTri("--");
    private final JLabel lblTrangThaiHomNay = GiaoDien.giaTri("--");

    private final JLabel lblSoNgayCong = new JLabel("0");
    private final JLabel lblSoDiMuon = new JLabel("0");
    private final JLabel lblTongGio = new JLabel("0");

    private final JLabel lblThongBao = new JLabel(" ");

    private final List<ChamCong> danhSachTrongThang = new ArrayList<>();

    private ChamCong chamCongHomNay;

    public ChamCongPanel(NhanVien nhanVien) {
        this.nhanVien = nhanVien;

        setLayout(new BorderLayout(0, 10));
        setBackground(GiaoDien.NEN);
        setBorder(new EmptyBorder(14, 14, 14, 14));

        add(taoPhanDau(), BorderLayout.NORTH);
        add(taoNoiDung(), BorderLayout.CENTER);
        add(taoPhanCuoi(), BorderLayout.SOUTH);

        cbThang.setSelectedItem(LocalDate.now().getMonthValue());
        txtNam.setText(String.valueOf(LocalDate.now().getYear()));

        GiaoDien.canhPhai(bang, 4);
        taiLai();
    }

    private JPanel taoPhanDau() {
        JPanel phan = new JPanel(new BorderLayout(0, 8));
        phan.setOpaque(false);
        phan.add(GiaoDien.tieuDe("CHẤM CÔNG CỦA TÔI"), BorderLayout.NORTH);

        JPanel thanhCongCu = GiaoDien.thanhCongCu();

        thanhCongCu.add(GiaoDien.nhan("Ca làm việc:"));
        thanhCongCu.add(cbCa);

        thanhCongCu.add(GiaoDien.nhan("Tháng:"));
        for (int thang = 1; thang <= 12; thang++) {
            cbThang.addItem(thang);
        }
        thanhCongCu.add(cbThang);

        thanhCongCu.add(GiaoDien.nhan("Năm:"));
        thanhCongCu.add(txtNam);

        JButton nutXem = GiaoDien.nut("Xem lịch sử", GiaoDien.XAM);
        nutXem.addActionListener(e -> taiLai());

        btnVao.addActionListener(e -> chamCongVao());
        btnRa.addActionListener(e -> chamCongRa());

        thanhCongCu.add(nutXem);
        thanhCongCu.add(btnVao);
        thanhCongCu.add(btnRa);

        phan.add(thanhCongCu, BorderLayout.CENTER);
        return phan;
    }

    private JPanel taoNoiDung() {
        JPanel noiDung = new JPanel(new BorderLayout(0, 10));
        noiDung.setOpaque(false);
        noiDung.add(taoKhuHomNay(), BorderLayout.NORTH);
        noiDung.add(GiaoDien.cuon(bang), BorderLayout.CENTER);
        return noiDung;
    }

    private JPanel taoKhuHomNay() {
        JPanel khu = new JPanel(new BorderLayout(0, 8));
        khu.setOpaque(false);

        JPanel theHomNay = new JPanel(new BorderLayout());
        theHomNay.setBackground(Color.WHITE);
        theHomNay.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(GiaoDien.VIEN),
                new EmptyBorder(10, 14, 10, 14)));

        JLabel lblTieuDe = new JLabel("Tình hình hôm nay");
        lblTieuDe.setFont(GiaoDien.CHU_DAM);
        lblTieuDe.setForeground(GiaoDien.XANH_DAM);
        theHomNay.add(lblTieuDe, BorderLayout.NORTH);

        JPanel hangGiaTri = new JPanel(new GridLayout(1, 5, 12, 0));
        hangGiaTri.setOpaque(false);
        hangGiaTri.add(taoO("Ngày", lblNgayHomNay));
        hangGiaTri.add(taoO("Ca", lblCaHomNay));
        hangGiaTri.add(taoO("Giờ vào", lblVaoHomNay));
        hangGiaTri.add(taoO("Giờ ra", lblRaHomNay));
        hangGiaTri.add(taoO("Trạng thái", lblTrangThaiHomNay));
        theHomNay.add(hangGiaTri, BorderLayout.CENTER);

        khu.add(theHomNay, BorderLayout.NORTH);

        JPanel hangThe = GiaoDien.hangThe(
                GiaoDien.the("Ngày công trong tháng", lblSoNgayCong, GiaoDien.XANH_CHINH),
                GiaoDien.the("Lần đi muộn", lblSoDiMuon, GiaoDien.CAM),
                GiaoDien.the("Tổng giờ làm", lblTongGio, GiaoDien.XANH_LA));
        khu.add(hangThe, BorderLayout.SOUTH);
        return khu;
    }

    private static JPanel taoO(String nhan, JLabel giaTri) {
        JPanel o = new JPanel(new BorderLayout(0, 2));
        o.setOpaque(false);

        JLabel lblNhan = new JLabel(nhan);
        lblNhan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNhan.setForeground(GiaoDien.CHU_NHAT);

        o.add(lblNhan, BorderLayout.NORTH);
        o.add(giaTri, BorderLayout.CENTER);
        return o;
    }

    private JPanel taoPhanCuoi() {
        JPanel phan = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        phan.setOpaque(false);
        lblThongBao.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblThongBao.setForeground(GiaoDien.CHU_NHAT);
        phan.add(lblThongBao);
        return phan;
    }

    private void taiLai() {
        Integer thang = (Integer) cbThang.getSelectedItem();
        Integer nam = docNam();
        if (thang == null || nam == null) {
            return;
        }

        try {
            if (cbCa.getItemCount() == 0) {
                for (CaLamViec ca : danhMucDAO.layCaLamViec()) {
                    cbCa.addItem(ca);
                }
            }

            danhSachTrongThang.clear();
            danhSachTrongThang.addAll(
                    chamCongDAO.layTheoThang(nhanVien.getMaNV(), thang, nam));
            chamCongHomNay = chamCongDAO.layTheoNgay(nhanVien.getMaNV(), LocalDate.now());

            napBang();
            capNhatKhuHomNay();
            capNhatThongKe(thang, nam);
            capNhatTrangThaiNut();

            lblThongBao.setForeground(GiaoDien.CHU_NHAT);
            lblThongBao.setToolTipText(null);
            lblThongBao.setText("Dữ liệu tháng " + thang + "/" + nam
                    + " của " + nhanVien.getHoTen());
        } catch (SQLException loi) {
            lblThongBao.setForeground(GiaoDien.DO);
            lblThongBao.setText("Không tải được dữ liệu chấm công.");
            lblThongBao.setToolTipText("<html><pre>" + KetNoiCSDL.moTaLoi(loi) + "</pre></html>");
            napBang();
        }
    }

    private void napBang() {
        moHinhBang.setRowCount(0);

        for (ChamCong chamCong : danhSachTrongThang) {
            String tenCa = chamCong.getCa() != null
                    ? chamCong.getCa().getTenCa() : chamCong.getMaCa();

            double soGio = ChamCongService.soGioLam(chamCong.getGioVao(), chamCong.getGioRa());

            moHinhBang.addRow(new Object[]{
                    DinhDang.ngay(chamCong.getNgayChamCong()),
                    tenCa == null ? "" : tenCa,
                    DinhDang.gio(chamCong.getGioVao()),
                    DinhDang.gio(chamCong.getGioRa()),
                    soGio == 0d ? "" : DinhDang.so(soGio, 2),
                    hoac(chamCong.getTrangThai()),
                    chamCong.getGhiChu() == null ? "" : chamCong.getGhiChu()
            });
        }
    }

    private void capNhatKhuHomNay() {
        lblNgayHomNay.setText(DinhDang.ngay(LocalDate.now()));

        if (chamCongHomNay == null) {
            lblCaHomNay.setText("--");
            lblVaoHomNay.setText("Chưa chấm");
            lblRaHomNay.setText("--");
            lblTrangThaiHomNay.setText(ChamCongService.VANG_MAT);
            lblTrangThaiHomNay.setForeground(GiaoDien.CHU_NHAT);
            return;
        }

        lblCaHomNay.setText(chamCongHomNay.getCa() != null
                ? chamCongHomNay.getCa().getTenCa() : hoac(chamCongHomNay.getMaCa()));
        lblVaoHomNay.setText(chamCongHomNay.getGioVao() == null
                ? "Chưa chấm" : DinhDang.gio(chamCongHomNay.getGioVao()));
        lblRaHomNay.setText(chamCongHomNay.getGioRa() == null
                ? "Chưa chấm" : DinhDang.gio(chamCongHomNay.getGioRa()));
        lblTrangThaiHomNay.setText(hoac(chamCongHomNay.getTrangThai()));
        lblTrangThaiHomNay.setForeground(
                ChamCongService.DU_CA.equalsIgnoreCase(chamCongHomNay.getTrangThai())
                        ? GiaoDien.XANH_LA : GiaoDien.CAM);
    }

    private void capNhatThongKe(int thang, int nam) {
        double tongGio = 0d;
        for (ChamCong chamCong : danhSachTrongThang) {
            tongGio += ChamCongService.soGioLam(chamCong.getGioVao(), chamCong.getGioRa());
        }
        lblTongGio.setText(DinhDang.so(tongGio, 1) + " giờ");

        try {
            lblSoNgayCong.setText(String.valueOf(
                    chamCongDAO.demNgayCong(nhanVien.getMaNV(), thang, nam)));
            lblSoDiMuon.setText(String.valueOf(
                    chamCongDAO.demLanDiMuon(nhanVien.getMaNV(), thang, nam)));
        } catch (SQLException loi) {
            lblSoNgayCong.setText("--");
            lblSoDiMuon.setText("--");
        }
    }

    private void capNhatTrangThaiNut() {
        boolean daVao = chamCongHomNay != null && chamCongHomNay.getGioVao() != null;
        boolean daRa = chamCongHomNay != null && chamCongHomNay.getGioRa() != null;

        btnVao.setEnabled(!daVao);
        btnRa.setEnabled(daVao && !daRa);
    }

    private void chamCongVao() {
        CaLamViec ca = caDangChon();
        if (ca == null) {
            return;
        }

        LocalDate ngay = LocalDate.now();
        try {
            ChamCong banGhi = chamCongDAO.layTheoNgay(nhanVien.getMaNV(), ngay);
            if (banGhi != null && banGhi.getGioVao() != null) {
                GiaoDien.tin(this, "Hôm nay bạn đã chấm công vào lúc "
                        + DinhDang.gio(banGhi.getGioVao()) + ".");
                return;
            }

            if (banGhi == null) {
                banGhi = new ChamCong();
                banGhi.setMaNV(nhanVien.getMaNV());
                banGhi.setNgayChamCong(ngay);
            }

            LocalTime bayGio = LocalTime.now().withSecond(0).withNano(0);
            banGhi.setMaCa(ca.getMaCa());
            banGhi.setGioVao(bayGio);
            banGhi.setTrangThai(ChamCongService.xacDinhTrangThai(ca, bayGio, null));

            String moTa = ChamCongService.moTa(ca, bayGio, null);
            banGhi.setGhiChu(moTa.isEmpty() ? null : moTa);

            chamCongDAO.luu(banGhi);

            GiaoDien.tin(this, "Đã chấm công vào lúc " + DinhDang.gio(bayGio)
                    + (moTa.isEmpty() ? "" : " (" + moTa + ")"));
            taiLai();
        } catch (SQLException loi) {
            GiaoDien.loiKetNoi(this, loi);
        }
    }

    private void chamCongRa() {
        LocalDate ngay = LocalDate.now();

        try {
            ChamCong banGhi = chamCongDAO.layTheoNgay(nhanVien.getMaNV(), ngay);

            if (banGhi == null || banGhi.getGioVao() == null) {
                GiaoDien.canhBao(this, "Bạn chưa chấm công vào hôm nay.");
                return;
            }
            if (banGhi.getGioRa() != null) {
                GiaoDien.tin(this, "Hôm nay bạn đã chấm công ra lúc "
                        + DinhDang.gio(banGhi.getGioRa()) + ".");
                return;
            }

            CaLamViec ca = banGhi.getCa() != null ? banGhi.getCa() : caDangChon();

            LocalTime bayGio = LocalTime.now().withSecond(0).withNano(0);
            banGhi.setGioRa(bayGio);
            banGhi.setTrangThai(ChamCongService.xacDinhTrangThai(ca, banGhi.getGioVao(), bayGio));

            String moTa = ChamCongService.moTa(ca, banGhi.getGioVao(), bayGio);
            banGhi.setGhiChu(moTa.isEmpty() ? null : moTa);

            chamCongDAO.luu(banGhi);

            double soGio = ChamCongService.soGioLam(banGhi.getGioVao(), bayGio);
            GiaoDien.tin(this, "Đã chấm công ra lúc " + DinhDang.gio(bayGio)
                    + ".\nSố giờ làm: " + DinhDang.so(soGio, 2) + " giờ"
                    + (moTa.isEmpty() ? "" : "\n" + moTa));
            taiLai();
        } catch (SQLException loi) {
            GiaoDien.loiKetNoi(this, loi);
        }
    }

    private CaLamViec caDangChon() {
        CaLamViec ca = (CaLamViec) cbCa.getSelectedItem();
        if (ca == null) {
            GiaoDien.canhBao(this, "Chưa có ca làm việc trong hệ thống.");
        }
        return ca;
    }

    private Integer docNam() {
        try {
            return Integer.valueOf(txtNam.getText().trim());
        } catch (RuntimeException loi) {
            GiaoDien.canhBao(this, "Năm không hợp lệ, ví dụ: 2026.");
            return null;
        }
    }

    private static String hoac(String giaTri) {
        return giaTri == null || giaTri.trim().isEmpty() ? "--" : giaTri;
    }
}

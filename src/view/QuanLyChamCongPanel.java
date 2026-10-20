package view;

import dao.ChamCongDAO;
import dao.DanhMucDAO;
import dao.NhanVienDAO;
import model.CaLamViec;
import model.ChamCong;
import model.NhanVien;
import service.ChamCongService;
import util.DinhDang;
import util.KetNoiCSDL;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class QuanLyChamCongPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final String[] COT_THEO_NGAY = {
            "Mã NV", "Họ tên", "Ca", "Giờ vào", "Giờ ra", "Trạng thái", "Ghi chú"
    };
    private static final String[] COT_LICH_SU = {
            "Ngày", "Ca", "Giờ vào", "Giờ ra", "Số giờ làm", "Trạng thái"
    };

    private final NhanVien quanLy;

    private final ChamCongDAO chamCongDAO = new ChamCongDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final DanhMucDAO danhMucDAO = new DanhMucDAO();

    private final JTextField txtNgay = GiaoDien.truong(10);
    private final JComboBox<CaLamViec> cbCa = new JComboBox<>();

    private final JComboBox<Integer> cbThangLichSu = new JComboBox<>();
    private final JTextField txtNamLichSu = GiaoDien.truong(6);

    private final DefaultTableModel moHinhTheoNgay = GiaoDien.moHinhBang(COT_THEO_NGAY);
    private final JTable bangTheoNgay = GiaoDien.bang(moHinhTheoNgay);
    private final DefaultTableModel moHinhLichSu = GiaoDien.moHinhBang(COT_LICH_SU);
    private final JTable bangLichSu = GiaoDien.bang(moHinhLichSu);

    private final JLabel lblLichSu = GiaoDien.giaTri(
            "Chọn một nhân viên ở bảng trên để xem lịch sử.");
    private final JLabel lblDem = new JLabel();
    private final JLabel lblThongBao = new JLabel(" ");

    private final List<NhanVien> danhSachCapDuoi = new ArrayList<>();

    private final Map<String, ChamCong> chamCongTheoMaNV = new HashMap<>();

    public QuanLyChamCongPanel(NhanVien quanLy) {
        this.quanLy = quanLy;

        setLayout(new BorderLayout(0, 10));
        setBackground(GiaoDien.NEN);
        setBorder(new EmptyBorder(14, 14, 14, 14));

        add(taoPhanDau(), BorderLayout.NORTH);
        add(taoNoiDung(), BorderLayout.CENTER);
        add(taoPhanCuoi(), BorderLayout.SOUTH);

        txtNgay.setText(DinhDang.ngay(LocalDate.now()));
        for (int thang = 1; thang <= 12; thang++) {
            cbThangLichSu.addItem(thang);
        }
        cbThangLichSu.setSelectedItem(LocalDate.now().getMonthValue());
        txtNamLichSu.setText(String.valueOf(LocalDate.now().getYear()));

        GiaoDien.canhPhai(bangLichSu, 4);

        bangTheoNgay.getSelectionModel().addListSelectionListener(suKien -> {
            if (!suKien.getValueIsAdjusting()) {
                napLichSu();
            }
        });

        lblDem.setFont(GiaoDien.CHU_DAM);
        lblDem.setForeground(GiaoDien.CHU);

        taiLai();
    }

    private JPanel taoPhanDau() {
        JPanel phan = new JPanel(new BorderLayout(0, 8));
        phan.setOpaque(false);
        phan.add(GiaoDien.tieuDe("CHẤM CÔNG NHÂN VIÊN"), BorderLayout.NORTH);

        JPanel thanhCongCu = GiaoDien.thanhCongCu();

        thanhCongCu.add(GiaoDien.nhan("Ngày:"));
        txtNgay.setToolTipText("Định dạng dd/MM/yyyy - chọn ngày cần xem hoặc chấm công bù");
        thanhCongCu.add(txtNgay);

        thanhCongCu.add(GiaoDien.nhan("Ca:"));
        thanhCongCu.add(cbCa);

        JButton nutXem = GiaoDien.nut("Xem", GiaoDien.XAM);
        JButton nutVao = GiaoDien.nut("Chấm công vào", GiaoDien.XANH_CHINH);
        JButton nutRa = GiaoDien.nut("Chấm công ra", GiaoDien.XANH_LA);

        nutXem.addActionListener(e -> taiLai());
        nutVao.addActionListener(e -> chamCong(true));
        nutRa.addActionListener(e -> chamCong(false));

        thanhCongCu.add(nutXem);
        thanhCongCu.add(nutVao);
        thanhCongCu.add(nutRa);

        phan.add(thanhCongCu, BorderLayout.CENTER);
        return phan;
    }

    private JPanel taoNoiDung() {
        JPanel khungTheoNgay = new JPanel(new BorderLayout(0, 6));
        khungTheoNgay.setOpaque(false);

        JLabel lblTieuDeNgay = new JLabel("Bảng chấm công theo ngày");
        lblTieuDeNgay.setFont(GiaoDien.CHU_DAM);
        lblTieuDeNgay.setForeground(GiaoDien.XANH_DAM);
        khungTheoNgay.add(lblTieuDeNgay, BorderLayout.NORTH);
        khungTheoNgay.add(GiaoDien.cuon(bangTheoNgay), BorderLayout.CENTER);

        JPanel khungLichSu = new JPanel(new BorderLayout(0, 6));
        khungLichSu.setOpaque(false);

        JPanel hangTieuDe = new JPanel(new BorderLayout());
        hangTieuDe.setOpaque(false);

        JLabel lblTieuDeLichSu = new JLabel("Lịch sử chấm công của nhân viên được chọn");
        lblTieuDeLichSu.setFont(GiaoDien.CHU_DAM);
        lblTieuDeLichSu.setForeground(GiaoDien.XANH_DAM);
        hangTieuDe.add(lblTieuDeLichSu, BorderLayout.WEST);

        JPanel thanhLichSu = GiaoDien.thanhCongCu();
        thanhLichSu.add(GiaoDien.nhan("Tháng:"));
        thanhLichSu.add(cbThangLichSu);
        thanhLichSu.add(GiaoDien.nhan("Năm:"));
        thanhLichSu.add(txtNamLichSu);

        JButton nutXemLichSu = GiaoDien.nut("Xem lịch sử", GiaoDien.XAM);
        nutXemLichSu.addActionListener(e -> napLichSu());
        thanhLichSu.add(nutXemLichSu);

        hangTieuDe.add(thanhLichSu, BorderLayout.EAST);
        khungLichSu.add(hangTieuDe, BorderLayout.NORTH);

        JPanel thanLichSu = new JPanel(new BorderLayout(0, 4));
        thanLichSu.setOpaque(false);
        thanLichSu.add(GiaoDien.cuon(bangLichSu), BorderLayout.CENTER);

        lblLichSu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLichSu.setForeground(GiaoDien.CHU_NHAT);
        thanLichSu.add(lblLichSu, BorderLayout.SOUTH);

        khungLichSu.add(thanLichSu, BorderLayout.CENTER);

        JSplitPane chia = new JSplitPane(JSplitPane.VERTICAL_SPLIT, khungTheoNgay, khungLichSu);
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
        LocalDate ngay = docNgay();
        if (ngay == null) {
            return;
        }
        if (ngay.isAfter(LocalDate.now())) {
            GiaoDien.canhBao(this, "Không xem hay chấm công cho ngày ở tương lai.");
            return;
        }

        try {
            if (danhSachCapDuoi.isEmpty()) {
                danhSachCapDuoi.addAll(nhanVienDAO.layCapDuoi(quanLy.getMaNV()));
            }
            if (cbCa.getItemCount() == 0) {
                for (CaLamViec ca : danhMucDAO.layCaLamViec()) {
                    cbCa.addItem(ca);
                }
            }

            chamCongTheoMaNV.clear();
            for (ChamCong chamCong : chamCongDAO.layTheoNgayCuaNhom(quanLy.getMaNV(), ngay)) {
                chamCongTheoMaNV.put(chamCong.getMaNV(), chamCong);
            }

            napBangTheoNgay(ngay);
            lblThongBao.setForeground(GiaoDien.CHU_NHAT);
            lblThongBao.setText(" ");
            lblThongBao.setToolTipText(null);
        } catch (SQLException loi) {
            napBangTheoNgay(ngay);
            lblThongBao.setForeground(GiaoDien.DO);
            lblThongBao.setText("Không tải được dữ liệu chấm công từ CSDL.");
            lblThongBao.setToolTipText("<html><pre>" + KetNoiCSDL.moTaLoi(loi) + "</pre></html>");
        }
    }

    private void napBangTheoNgay(LocalDate ngay) {
        moHinhTheoNgay.setRowCount(0);
        int soNguoiDaCham = 0;

        for (NhanVien nhanVien : danhSachCapDuoi) {
            ChamCong chamCong = chamCongTheoMaNV.get(nhanVien.getMaNV());

            String tenCa = "";
            if (chamCong != null) {
                tenCa = chamCong.getCa() != null
                        ? chamCong.getCa().getTenCa() : chamCong.getMaCa();
            }

            moHinhTheoNgay.addRow(new Object[]{
                    nhanVien.getMaNV(),
                    nhanVien.getHoTen(),
                    tenCa == null ? "" : tenCa,
                    chamCong == null ? "" : DinhDang.gio(chamCong.getGioVao()),
                    chamCong == null ? "" : DinhDang.gio(chamCong.getGioRa()),
                    chamCong == null ? ChamCongService.VANG_MAT : hoac(chamCong.getTrangThai()),
                    chamCong == null || chamCong.getGhiChu() == null ? "" : chamCong.getGhiChu()
            });

            if (chamCong != null && chamCong.getGioVao() != null) {
                soNguoiDaCham++;
            }
        }

        lblDem.setText("Đã chấm công: " + soNguoiDaCham + " / " + danhSachCapDuoi.size()
                + " nhân viên - ngày " + DinhDang.ngay(ngay));
    }

    private void napLichSu() {
        String maNV = maNhanVienDangChon();
        if (maNV == null) {
            moHinhLichSu.setRowCount(0);
            lblLichSu.setText("Chọn một nhân viên ở bảng trên để xem lịch sử.");
            return;
        }

        Integer thang = (Integer) cbThangLichSu.getSelectedItem();
        Integer nam = docNam(txtNamLichSu);
        if (thang == null || nam == null) {
            return;
        }

        try {
            List<ChamCong> danhSach = chamCongDAO.layTheoThang(maNV, thang, nam);

            moHinhLichSu.setRowCount(0);
            double tongGio = 0d;

            for (ChamCong chamCong : danhSach) {
                double soGio = ChamCongService.soGioLam(chamCong.getGioVao(), chamCong.getGioRa());
                tongGio += soGio;

                String tenCa = chamCong.getCa() != null
                        ? chamCong.getCa().getTenCa() : chamCong.getMaCa();

                moHinhLichSu.addRow(new Object[]{
                        DinhDang.ngay(chamCong.getNgayChamCong()),
                        tenCa == null ? "" : tenCa,
                        DinhDang.gio(chamCong.getGioVao()),
                        DinhDang.gio(chamCong.getGioRa()),
                        soGio == 0d ? "" : DinhDang.so(soGio, 2),
                        hoac(chamCong.getTrangThai())
                });
            }

            lblLichSu.setToolTipText(null);
            lblLichSu.setText("Lịch sử tháng " + thang + "/" + nam + " của "
                    + tenNhanVien(maNV) + ": " + danhSach.size() + " ngày, tổng "
                    + DinhDang.so(tongGio, 1) + " giờ");
        } catch (SQLException loi) {
            moHinhLichSu.setRowCount(0);
            lblLichSu.setText("Không tải được lịch sử chấm công.");
            lblLichSu.setToolTipText("<html><pre>" + KetNoiCSDL.moTaLoi(loi) + "</pre></html>");
        }
    }

    private void chamCong(boolean laVao) {
        int dong = bangTheoNgay.getSelectedRow();
        if (dong < 0) {
            GiaoDien.canhBao(this, "Vui lòng chọn nhân viên cần chấm công.");
            return;
        }

        String maNV = String.valueOf(moHinhTheoNgay.getValueAt(dong, 0));
        String tenNV = String.valueOf(moHinhTheoNgay.getValueAt(dong, 1));

        LocalDate ngay = docNgay();
        if (ngay == null) {
            return;
        }
        if (ngay.isAfter(LocalDate.now())) {
            GiaoDien.canhBao(this, "Không chấm công cho ngày ở tương lai.");
            return;
        }

        CaLamViec caChon = (CaLamViec) cbCa.getSelectedItem();
        if (caChon == null) {
            GiaoDien.canhBao(this, "Chưa có ca làm việc trong hệ thống.");
            return;
        }

        try {
            ChamCong banGhi = chamCongDAO.layTheoNgay(maNV, ngay);

            if (laVao) {
                if (banGhi != null && banGhi.getGioVao() != null) {
                    GiaoDien.tin(this, tenNV + " đã chấm công vào lúc "
                            + DinhDang.gio(banGhi.getGioVao()) + " ngày "
                            + DinhDang.ngay(ngay) + ".");
                    return;
                }
                if (banGhi == null) {
                    banGhi = new ChamCong();
                    banGhi.setMaNV(maNV);
                    banGhi.setNgayChamCong(ngay);
                }
                banGhi.setMaCa(caChon.getMaCa());
                banGhi.setGioVao(LocalTime.now().withSecond(0).withNano(0));
            } else {
                if (banGhi == null || banGhi.getGioVao() == null) {
                    GiaoDien.canhBao(this, tenNV + " chưa chấm công vào ngày "
                            + DinhDang.ngay(ngay) + ".");
                    return;
                }
                if (banGhi.getGioRa() != null) {
                    GiaoDien.tin(this, tenNV + " đã chấm công ra lúc "
                            + DinhDang.gio(banGhi.getGioRa()) + ".");
                    return;
                }
                banGhi.setGioRa(LocalTime.now().withSecond(0).withNano(0));
            }

            CaLamViec caDung = banGhi.getCa() != null ? banGhi.getCa() : caChon;

            String trangThai = ChamCongService.xacDinhTrangThai(caDung,
                    banGhi.getGioVao(), banGhi.getGioRa());
            String moTa = ChamCongService.moTa(caDung, banGhi.getGioVao(), banGhi.getGioRa());

            banGhi.setTrangThai(trangThai);
            banGhi.setGhiChu(moTa.isEmpty() ? null : moTa);

            chamCongDAO.luu(banGhi);

            taiLai();
            chonDongTheoMa(maNV);
            napLichSu();

            GiaoDien.tin(this, (laVao ? "Đã chấm công vào" : "Đã chấm công ra")
                    + " cho " + tenNV + " ngày " + DinhDang.ngay(ngay)
                    + "\nTrạng thái: " + trangThai
                    + (moTa.isEmpty() ? "" : " (" + moTa + ")"));
        } catch (SQLException loi) {
            GiaoDien.loiKetNoi(this, loi);
        }
    }

    private LocalDate docNgay() {
        try {
            LocalDate ngay = DinhDang.docNgay(txtNgay.getText());
            if (ngay == null) {
                GiaoDien.canhBao(this, "Vui lòng nhập ngày cần xem (dd/MM/yyyy).");
            }
            return ngay;
        } catch (IllegalArgumentException loi) {
            GiaoDien.canhBao(this, loi.getMessage());
            return null;
        }
    }

    private Integer docNam(JTextField truong) {
        try {
            return Integer.valueOf(truong.getText().trim());
        } catch (RuntimeException loi) {
            GiaoDien.canhBao(this, "Năm không hợp lệ, ví dụ: 2026.");
            return null;
        }
    }

    private String maNhanVienDangChon() {
        int dong = bangTheoNgay.getSelectedRow();
        return dong < 0 ? null : String.valueOf(moHinhTheoNgay.getValueAt(dong, 0));
    }

    private String tenNhanVien(String maNV) {
        for (NhanVien nhanVien : danhSachCapDuoi) {
            if (maNV.equalsIgnoreCase(nhanVien.getMaNV())) {
                return nhanVien.getHoTen();
            }
        }
        return maNV;
    }

    private void chonDongTheoMa(String maNV) {
        for (int dong = 0; dong < moHinhTheoNgay.getRowCount(); dong++) {
            if (maNV.equalsIgnoreCase(String.valueOf(moHinhTheoNgay.getValueAt(dong, 0)))) {
                bangTheoNgay.setRowSelectionInterval(dong, dong);
                return;
            }
        }
    }

    private static String hoac(String giaTri) {
        return giaTri == null || giaTri.trim().isEmpty() ? "--" : giaTri;
    }
}

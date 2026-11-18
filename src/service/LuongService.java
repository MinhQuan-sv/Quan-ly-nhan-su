package service;

import dao.ChamCongDAO;
import model.BangLuong;
import model.NhanVien;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public final class LuongService {
    public static final BigDecimal PHAT_MOI_LAN = new BigDecimal("50000");

    public static final int SO_LAN_MUON_TOI_DA = 5;

    public static final BigDecimal HE_SO_PHAT_NANG = new BigDecimal("0.5");

    private LuongService() {
    }

    public static int ngayCongChuan(int thang, int nam) {
        YearMonth ym = YearMonth.of(nam, thang);
        int tongNgay = ym.lengthOfMonth();
        int soChuNhat = 0;
        for (int ngay = 1; ngay <= tongNgay; ngay++) {
            if (ym.atDay(ngay).getDayOfWeek() == DayOfWeek.SUNDAY) {
                soChuNhat++;
            }
        }
        return tongNgay - soChuNhat;
    }

    public static List<BangLuong> tinhBangLuong(List<NhanVien> danhSach, int thang, int nam,
                                                ChamCongDAO chamCongDAO) throws SQLException {
        int ngayChuan = ngayCongChuan(thang, nam);
        List<BangLuong> ketQua = new ArrayList<>();

        for (NhanVien nhanVien : danhSach) {
            int ngayCong = chamCongDAO.demNgayCong(nhanVien.getMaNV(), thang, nam);
            int diMuon = chamCongDAO.demLanDiMuon(nhanVien.getMaNV(), thang, nam);
            ketQua.add(tinhMotNguoi(nhanVien, ngayChuan, ngayCong, diMuon));
        }
        return ketQua;
    }

    public static BangLuong tinhMotNguoi(NhanVien nhanVien, int ngayChuan,
                                         int ngayCong, int diMuon) {
        BangLuong bangLuong = new BangLuong();
        bangLuong.setMaNV(nhanVien.getMaNV());
        bangLuong.setHoTen(nhanVien.getHoTen());
        bangLuong.setTenPB(nhanVien.getTenPB());
        bangLuong.setNgayCongChuan(ngayChuan);
        bangLuong.setNgayCongThucTe(ngayCong);
        bangLuong.setSoLanDiMuon(diMuon);

        BigDecimal luongCB = nhanVien.getLuong() == null
                ? BigDecimal.ZERO : nhanVien.getLuong();
        bangLuong.setLuongCoBan(luongCB);

        BigDecimal luongTheoNgay = BigDecimal.ZERO;
        if (ngayChuan > 0 && ngayCong > 0) {
            luongTheoNgay = luongCB.multiply(BigDecimal.valueOf(ngayCong))
                    .divide(BigDecimal.valueOf(ngayChuan), 0, RoundingMode.HALF_UP);
        }
        bangLuong.setLuongTheoNgay(luongTheoNgay);

        BigDecimal tienPhat;
        BigDecimal thucLinh;

        if (diMuon > SO_LAN_MUON_TOI_DA) {
            thucLinh = luongTheoNgay.multiply(HE_SO_PHAT_NANG)
                    .setScale(0, RoundingMode.HALF_UP);
            tienPhat = luongTheoNgay.subtract(thucLinh);
        } else {
            tienPhat = PHAT_MOI_LAN.multiply(BigDecimal.valueOf(diMuon));
            thucLinh = luongTheoNgay.subtract(tienPhat);
            if (thucLinh.signum() < 0) {
                thucLinh = BigDecimal.ZERO;
            }
        }
        bangLuong.setTienPhat(tienPhat);
        bangLuong.setLuongThucLinh(thucLinh);
        return bangLuong;
    }
}
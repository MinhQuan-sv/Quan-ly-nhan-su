package service;

import dao.ChamCongDAO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import model.BangLuong;
import model.NhanVien;

public final class LuongService {
    
    public static final BigDecimal[] MUC_PHAT = {
            new BigDecimal("0"),
            new BigDecimal("50000"),
            new BigDecimal("100000"),
            new BigDecimal("200000"),
            new BigDecimal("500000")
    };
    
    public static final BigDecimal HE_SO_PHAT_NANG = new BigDecimal("0.3");

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

    public static List<BangLuong> tinhBangLuong(List<NhanVien> danhSach, int thang, int nam, ChamCongDAO chamCongDAO) throws SQLException {
        int ngayChuan = ngayCongChuan(thang, nam);
        List<BangLuong> ketQua = new ArrayList<>();

        for (NhanVien nhanVien : danhSach) {
            int ngayCong = chamCongDAO.demNgayCong(nhanVien.getMaNV(), thang, nam);
            int diMuon = chamCongDAO.demLanDiMuon(nhanVien.getMaNV(), thang, nam);
            
            ketQua.add(tinhMotNguoi(nhanVien, ngayChuan, ngayCong, diMuon));
        }
        
        return ketQua;
    }

    public static BangLuong tinhMotNguoi(NhanVien nhanVien, int ngayChuan, int ngayCong, int diMuon) {
        BangLuong bangLuong = new BangLuong();
        bangLuong.setMaNV(nhanVien.getMaNV());
        bangLuong.setHoTen(nhanVien.getHoTen());
        bangLuong.setTenPB(nhanVien.getTenPB());
        bangLuong.setNgayCongChuan(ngayChuan);
        bangLuong.setNgayCongThucTe(ngayCong);
        bangLuong.setSoLanDiMuon(diMuon);

        BigDecimal luongCB = nhanVien.getLuong() == null ? BigDecimal.ZERO : nhanVien.getLuong();
        bangLuong.setLuongCoBan(luongCB);

        BigDecimal luongCong = BigDecimal.ZERO;
        if (ngayChuan > 0 && ngayCong > 0) {
            luongCong = luongCB.multiply(BigDecimal.valueOf(ngayCong))
                    .divide(BigDecimal.valueOf(ngayChuan), 0, RoundingMode.HALF_UP);
        }
        bangLuong.setLuongCong(luongCong);

        BigDecimal tienPhat = BigDecimal.ZERO;
        BigDecimal thucLinh = luongCong;

        if (diMuon >= 5) {
            thucLinh = luongCong.multiply(BigDecimal.ONE.subtract(HE_SO_PHAT_NANG))
                    .setScale(0, RoundingMode.HALF_UP);
            tienPhat = luongCong.subtract(thucLinh);
        } else if (diMuon > 0) {
            tienPhat = MUC_PHAT[diMuon];
            thucLinh = luongCong.subtract(tienPhat);
            
            if (thucLinh.signum() < 0) {
                thucLinh = BigDecimal.ZERO;
            }
        }

        bangLuong.setTienPhat(tienPhat);
        bangLuong.setLuongThucLinh(thucLinh);
        
        return bangLuong;
    }
}
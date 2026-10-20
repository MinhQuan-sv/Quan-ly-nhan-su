package dao;

import model.CaLamViec;
import model.ChamCong;
import util.KetNoiCSDL;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ChamCongDAO {
    private static final String SQL_LAY_THEO_NGAY =
            "SELECT cc.MaChamCong, cc.MaNV, cc.NgayChamCong, cc.MaCa, cc.GioVao, cc.GioRa, "
            + "       cc.TrangThai, cc.GhiChu, nv.HoTen AS TenNV, "
            + "       ca.TenCa, ca.GioBatDau, ca.GioKetThuc "
            + "FROM CHAMCONG cc "
            + "JOIN NHANVIEN nv ON nv.MaNV = cc.MaNV "
            + "LEFT JOIN CALAMVIEC ca ON ca.MaCa = cc.MaCa "
            + "WHERE cc.MaNV = ? AND cc.NgayChamCong = ?";

    private static final String SQL_LAY_THEO_THANG =
            "SELECT cc.MaChamCong, cc.MaNV, cc.NgayChamCong, cc.MaCa, cc.GioVao, cc.GioRa, "
            + "       cc.TrangThai, cc.GhiChu, nv.HoTen AS TenNV, "
            + "       ca.TenCa, ca.GioBatDau, ca.GioKetThuc "
            + "FROM CHAMCONG cc "
            + "JOIN NHANVIEN nv ON nv.MaNV = cc.MaNV "
            + "LEFT JOIN CALAMVIEC ca ON ca.MaCa = cc.MaCa "
            + "WHERE cc.MaNV = ? "
            + "  AND MONTH(cc.NgayChamCong) = ? "
            + "  AND YEAR(cc.NgayChamCong) = ? "
            + "ORDER BY cc.NgayChamCong";

    private static final String SQL_LAY_THEO_NGAY_CUA_NHOM =
            "SELECT cc.MaChamCong, cc.MaNV, cc.NgayChamCong, cc.MaCa, cc.GioVao, cc.GioRa, "
            + "       cc.TrangThai, cc.GhiChu, nv.HoTen AS TenNV, "
            + "       ca.TenCa, ca.GioBatDau, ca.GioKetThuc "
            + "FROM CHAMCONG cc "
            + "JOIN NHANVIEN nv ON nv.MaNV = cc.MaNV "
            + "LEFT JOIN CALAMVIEC ca ON ca.MaCa = cc.MaCa "
            + "WHERE nv.MaQuanLy = ? AND cc.NgayChamCong = ? "
            + "ORDER BY nv.HoTen";

    private static final String SQL_CAP_NHAT =
            "UPDATE CHAMCONG SET "
            + "       MaCa = ?, GioVao = ?, GioRa = ?, TrangThai = ?, GhiChu = ? "
            + "WHERE  MaNV = ? AND NgayChamCong = ?";

    private static final String SQL_THEM =
            "INSERT INTO CHAMCONG (MaNV, NgayChamCong, MaCa, GioVao, GioRa, TrangThai, GhiChu) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_DEM_NGAY_CONG =
            "SELECT COUNT(*) FROM CHAMCONG "
            + "WHERE MaNV = ? AND GioVao IS NOT NULL "
            + "  AND MONTH(NgayChamCong) = ? AND YEAR(NgayChamCong) = ?";

    private static final String SQL_DEM_DI_MUON =
            "SELECT COUNT(*) FROM CHAMCONG "
            + "WHERE MaNV = ? AND TrangThai LIKE ? "
            + "  AND MONTH(NgayChamCong) = ? AND YEAR(NgayChamCong) = ?";

    public ChamCong layTheoNgay(String maNV, LocalDate ngay) throws SQLException {
        try (Connection ketNoi = KetNoiCSDL.ketNoi();
             PreparedStatement lenh = ketNoi.prepareStatement(SQL_LAY_THEO_NGAY)) {
            lenh.setString(1, maNV);
            ganNgay(lenh, 2, ngay);

            try (ResultSet duLieu = lenh.executeQuery()) {
                return duLieu.next() ? docChamCong(duLieu) : null;
            }
        }
    }

    public List<ChamCong> layTheoThang(String maNV, int thang, int nam) throws SQLException {
        List<ChamCong> ketQua = new ArrayList<>();

        try (Connection ketNoi = KetNoiCSDL.ketNoi();
             PreparedStatement lenh = ketNoi.prepareStatement(SQL_LAY_THEO_THANG)) {
            lenh.setString(1, maNV);
            lenh.setInt(2, thang);
            lenh.setInt(3, nam);

            try (ResultSet duLieu = lenh.executeQuery()) {
                while (duLieu.next()) {
                    ketQua.add(docChamCong(duLieu));
                }
            }
        }
        return ketQua;
    }

    public List<ChamCong> layTheoNgayCuaNhom(String maQuanLy, LocalDate ngay) throws SQLException {
        List<ChamCong> ketQua = new ArrayList<>();

        try (Connection ketNoi = KetNoiCSDL.ketNoi();
             PreparedStatement lenh = ketNoi.prepareStatement(SQL_LAY_THEO_NGAY_CUA_NHOM)) {
            lenh.setString(1, maQuanLy);
            ganNgay(lenh, 2, ngay);

            try (ResultSet duLieu = lenh.executeQuery()) {
                while (duLieu.next()) {
                    ketQua.add(docChamCong(duLieu));
                }
            }
        }
        return ketQua;
    }

    public void luu(ChamCong chamCong) throws SQLException {
        try (Connection ketNoi = KetNoiCSDL.ketNoi()) {
            try (PreparedStatement lenh = ketNoi.prepareStatement(SQL_CAP_NHAT)) {
                lenh.setString(1, chamCong.getMaCa());
                ganGio(lenh, 2, chamCong.getGioVao());
                ganGio(lenh, 3, chamCong.getGioRa());
                lenh.setString(4, chamCong.getTrangThai());
                lenh.setString(5, chamCong.getGhiChu());
                lenh.setString(6, chamCong.getMaNV());
                ganNgay(lenh, 7, chamCong.getNgayChamCong());

                int soDongCapNhat = lenh.executeUpdate();
                if (soDongCapNhat > 0) {
                    return;
                }
            }

            try (PreparedStatement lenh = ketNoi.prepareStatement(SQL_THEM)) {
                lenh.setString(1, chamCong.getMaNV());
                ganNgay(lenh, 2, chamCong.getNgayChamCong());
                lenh.setString(3, chamCong.getMaCa());
                ganGio(lenh, 4, chamCong.getGioVao());
                ganGio(lenh, 5, chamCong.getGioRa());
                lenh.setString(6, chamCong.getTrangThai());
                lenh.setString(7, chamCong.getGhiChu());
                lenh.executeUpdate();
            }
        }
    }

    public int demNgayCong(String maNV, int thang, int nam) throws SQLException {
        return dem(SQL_DEM_NGAY_CONG, maNV, null, thang, nam);
    }

    public int demLanDiMuon(String maNV, int thang, int nam) throws SQLException {
        return dem(SQL_DEM_DI_MUON, maNV, "%Đi muộn%", thang, nam);
    }

    private int dem(String cauLenh, String maNV, String tuKhoaLoc, int thang, int nam)
            throws SQLException {
        try (Connection ketNoi = KetNoiCSDL.ketNoi();
             PreparedStatement lenh = ketNoi.prepareStatement(cauLenh)) {
            int viTri = 1;
            lenh.setString(viTri++, maNV);
            if (tuKhoaLoc != null) {
                lenh.setString(viTri++, tuKhoaLoc);
            }
            lenh.setInt(viTri++, thang);
            lenh.setInt(viTri, nam);

            try (ResultSet duLieu = lenh.executeQuery()) {
                return duLieu.next() ? duLieu.getInt(1) : 0;
            }
        }
    }

    private static ChamCong docChamCong(ResultSet duLieu) throws SQLException {
        ChamCong chamCong = new ChamCong();
        chamCong.setMaChamCong(duLieu.getInt("MaChamCong"));
        chamCong.setMaNV(duLieu.getString("MaNV"));
        chamCong.setTenNV(duLieu.getString("TenNV"));
        chamCong.setNgayChamCong(docNgay(duLieu, "NgayChamCong"));
        chamCong.setMaCa(duLieu.getString("MaCa"));
        chamCong.setGioVao(docGio(duLieu, "GioVao"));
        chamCong.setGioRa(docGio(duLieu, "GioRa"));
        chamCong.setTrangThai(duLieu.getString("TrangThai"));
        chamCong.setGhiChu(duLieu.getString("GhiChu"));

        String maCa = duLieu.getString("MaCa");
        if (maCa != null) {
            CaLamViec ca = new CaLamViec(
                    maCa,
                    duLieu.getString("TenCa"),
                    docGio(duLieu, "GioBatDau"),
                    docGio(duLieu, "GioKetThuc"));
            chamCong.setCa(ca);
        }
        return chamCong;
    }

    private static void ganNgay(PreparedStatement lenh, int chiSo, LocalDate ngay)
            throws SQLException {
        if (ngay == null) {
            lenh.setNull(chiSo, Types.DATE);
        } else {
            lenh.setDate(chiSo, Date.valueOf(ngay));
        }
    }

    private static void ganGio(PreparedStatement lenh, int chiSo, LocalTime gio) throws SQLException {
        if (gio == null) {
            lenh.setNull(chiSo, Types.TIME);
        } else {
            lenh.setTime(chiSo, Time.valueOf(gio));
        }
    }

    private static LocalDate docNgay(ResultSet duLieu, String tenCot) throws SQLException {
        Date ngay = duLieu.getDate(tenCot);
        return ngay == null ? null : ngay.toLocalDate();
    }

    private static LocalTime docGio(ResultSet duLieu, String tenCot) throws SQLException {
        Time gio = duLieu.getTime(tenCot);
        return gio == null ? null : gio.toLocalTime();
    }
}

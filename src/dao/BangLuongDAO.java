package dao;

import model.BangLuong;
import util.KetNoiCSDL;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BangLuongDAO {

    private static final String SQL_LAY_THEO_THANG =
            "SELECT bl.*, nv.HoTen, pb.TenPB FROM BANGLUONG bl "
            + "JOIN NHANVIEN nv ON nv.MaNV = bl.MaNV "
            + "LEFT JOIN PHONGBAN pb ON pb.MaPB = nv.MaPB "
            + "WHERE bl.Thang = ? AND bl.Nam = ? ORDER BY nv.MaNV";

    private static final String SQL_LAY_CUA_NV =
            "SELECT bl.*, nv.HoTen, pb.TenPB FROM BANGLUONG bl "
            + "JOIN NHANVIEN nv ON nv.MaNV = bl.MaNV "
            + "LEFT JOIN PHONGBAN pb ON pb.MaPB = nv.MaPB "
            + "WHERE bl.MaNV = ? AND bl.Thang = ? AND bl.Nam = ?";

    private static final String SQL_LAY_CUA_NHOM =
            "SELECT bl.*, nv.HoTen, pb.TenPB FROM BANGLUONG bl "
            + "JOIN NHANVIEN nv ON nv.MaNV = bl.MaNV "
            + "LEFT JOIN PHONGBAN pb ON pb.MaPB = nv.MaPB "
            + "WHERE nv.MaQuanLy = ? AND bl.Thang = ? AND bl.Nam = ? ORDER BY nv.HoTen";

    private static final String SQL_THEM =
            "INSERT INTO BANGLUONG (MaNV, Thang, Nam, LuongCoBan, LuongCong, "
            + "NgayCongChuan, NgayCongThucTe, SoLanDiMuon, TienPhat, LuongThucLinh, NgayChot) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_XOA_THANG =
            "DELETE FROM BANGLUONG WHERE Thang = ? AND Nam = ?";

    public List<BangLuong> layTheoThang(int thang, int nam) throws SQLException {
        return layDanhSach(SQL_LAY_THEO_THANG, null, thang, nam);
    }

    public List<BangLuong> layCuaQuanLy(String maQuanLy, int thang, int nam) throws SQLException {
        return layDanhSach(SQL_LAY_CUA_NHOM, maQuanLy, thang, nam);
    }

    public BangLuong layCuaNhanVien(String maNV, int thang, int nam) throws SQLException {
        List<BangLuong> ds = layDanhSach(SQL_LAY_CUA_NV, maNV, thang, nam);
        return ds.isEmpty() ? null : ds.get(0);
    }

    public void chotLuong(List<BangLuong> danhSach, int thang, int nam) throws SQLException {
        try (Connection ketNoi = KetNoiCSDL.ketNoi()) {
            boolean cheDoCu = ketNoi.getAutoCommit();
            ketNoi.setAutoCommit(false);
            try {
                try (PreparedStatement lenh = ketNoi.prepareStatement(SQL_THEM)) {
                    for (BangLuong bl : danhSach) {
                        lenh.setString(1, bl.getMaNV());
                        lenh.setInt(2, thang);
                        lenh.setInt(3, nam);
                        lenh.setBigDecimal(4, bl.getLuongCoBan());
                        lenh.setBigDecimal(5, bl.getLuongCong());
                        lenh.setInt(6, bl.getNgayCongChuan());
                        lenh.setInt(7, bl.getNgayCongThucTe());
                        lenh.setInt(8, bl.getSoLanDiMuon());
                        lenh.setBigDecimal(9, bl.getTienPhat());
                        lenh.setBigDecimal(10, bl.getLuongThucLinh());
                        lenh.setDate(11, Date.valueOf(LocalDate.now()));
                        lenh.addBatch();
                    }
                    lenh.executeBatch();
                }
                ketNoi.commit();
            } catch (SQLException loi) {
                ketNoi.rollback();
                throw loi;
            } finally {
                ketNoi.setAutoCommit(cheDoCu);
            }
        }
    }

    public void huyChot(int thang, int nam) throws SQLException {
        try (Connection ketNoi = KetNoiCSDL.ketNoi();
             PreparedStatement lenh = ketNoi.prepareStatement(SQL_XOA_THANG)) {
            lenh.setInt(1, thang);
            lenh.setInt(2, nam);
            lenh.executeUpdate();
        }
    }

    private List<BangLuong> layDanhSach(String cauLenh, String thamSoDau, int thang, int nam)
            throws SQLException {
        List<BangLuong> ketQua = new ArrayList<>();
        try (Connection ketNoi = KetNoiCSDL.ketNoi();
             PreparedStatement lenh = ketNoi.prepareStatement(cauLenh)) {
            int viTri = 1;
            if (thamSoDau != null) lenh.setString(viTri++, thamSoDau);
            lenh.setInt(viTri++, thang);
            lenh.setInt(viTri, nam);

            try (ResultSet duLieu = lenh.executeQuery()) {
                while (duLieu.next()) {
                    ketQua.add(docBangLuong(duLieu));
                }
            }
        }
        return ketQua;
    }

    private static BangLuong docBangLuong(ResultSet duLieu) throws SQLException {
        BangLuong bl = new BangLuong();
        bl.setMaNV(duLieu.getString("MaNV"));
        bl.setHoTen(duLieu.getString("HoTen"));
        bl.setTenPB(duLieu.getString("TenPB"));
        bl.setLuongCoBan(duLieu.getBigDecimal("LuongCoBan"));
        bl.setLuongCong(duLieu.getBigDecimal("LuongCong"));
        bl.setNgayCongChuan(duLieu.getInt("NgayCongChuan"));
        bl.setNgayCongThucTe(duLieu.getInt("NgayCongThucTe"));
        bl.setSoLanDiMuon(duLieu.getInt("SoLanDiMuon"));
        bl.setTienPhat(duLieu.getBigDecimal("TienPhat"));
        bl.setLuongThucLinh(duLieu.getBigDecimal("LuongThucLinh"));
        return bl;
    }
}
package dao;

import model.CaLamViec;
import model.ChucVu;
import model.PhongBan;
import util.KetNoiCSDL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class DanhMucDAO {
    private static final String SQL_LAY_PHONG_BAN =
            "SELECT MaPB, TenPB, MoTa "
            + "FROM PHONGBAN "
            + "ORDER BY TenPB";

    private static final String SQL_LAY_CHUC_VU =
            "SELECT MaCV, TenCV, MoTa "
            + "FROM CHUCVU "
            + "ORDER BY TenCV";

    private static final String SQL_LAY_CA_LAM_VIEC =
            "SELECT MaCa, TenCa, GioBatDau, GioKetThuc "
            + "FROM CALAMVIEC "
            + "ORDER BY GioBatDau";

    private static final String SQL_LAY_MOT_CA =
            "SELECT MaCa, TenCa, GioBatDau, GioKetThuc "
            + "FROM CALAMVIEC "
            + "WHERE MaCa = ?";

    public List<PhongBan> layPhongBan() throws SQLException {
        List<PhongBan> ketQua = new ArrayList<>();

        try (Connection ketNoi = KetNoiCSDL.ketNoi();
             PreparedStatement lenh = ketNoi.prepareStatement(SQL_LAY_PHONG_BAN);
             ResultSet duLieu = lenh.executeQuery()) {
            while (duLieu.next()) {
                PhongBan phongBan = new PhongBan();
                phongBan.setMaPB(duLieu.getString("MaPB"));
                phongBan.setTenPB(duLieu.getString("TenPB"));
                phongBan.setMoTa(duLieu.getString("MoTa"));
                ketQua.add(phongBan);
            }
        }
        return ketQua;
    }

    public List<ChucVu> layChucVu() throws SQLException {
        List<ChucVu> ketQua = new ArrayList<>();

        try (Connection ketNoi = KetNoiCSDL.ketNoi();
             PreparedStatement lenh = ketNoi.prepareStatement(SQL_LAY_CHUC_VU);
             ResultSet duLieu = lenh.executeQuery()) {
            while (duLieu.next()) {
                ChucVu chucVu = new ChucVu();
                chucVu.setMaCV(duLieu.getString("MaCV"));
                chucVu.setTenCV(duLieu.getString("TenCV"));
                chucVu.setMoTa(duLieu.getString("MoTa"));
                ketQua.add(chucVu);
            }
        }
        return ketQua;
    }

    public List<CaLamViec> layCaLamViec() throws SQLException {
        List<CaLamViec> ketQua = new ArrayList<>();

        try (Connection ketNoi = KetNoiCSDL.ketNoi();
             PreparedStatement lenh = ketNoi.prepareStatement(SQL_LAY_CA_LAM_VIEC);
             ResultSet duLieu = lenh.executeQuery()) {
            while (duLieu.next()) {
                ketQua.add(docCaLamViec(duLieu));
            }
        }
        return ketQua;
    }

    public CaLamViec layCa(String maCa) throws SQLException {
        if (maCa == null) {
            return null;
        }

        try (Connection ketNoi = KetNoiCSDL.ketNoi();
             PreparedStatement lenh = ketNoi.prepareStatement(SQL_LAY_MOT_CA)) {
            lenh.setString(1, maCa);

            try (ResultSet duLieu = lenh.executeQuery()) {
                return duLieu.next() ? docCaLamViec(duLieu) : null;
            }
        }
    }

    private static CaLamViec docCaLamViec(ResultSet duLieu) throws SQLException {
        Time gioBatDau = duLieu.getTime("GioBatDau");
        Time gioKetThuc = duLieu.getTime("GioKetThuc");

        CaLamViec ca = new CaLamViec();
        ca.setMaCa(duLieu.getString("MaCa"));
        ca.setTenCa(duLieu.getString("TenCa"));
        ca.setGioBatDau(gioBatDau == null ? null : gioBatDau.toLocalTime());
        ca.setGioKetThuc(gioKetThuc == null ? null : gioKetThuc.toLocalTime());
        return ca;
    }
}

package dao;

import model.TaiKhoan;
import util.KetNoiCSDL;
import util.MatKhau;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TaiKhoanDAO {
    private static final String SQL_LAY_THEO_TEN_DANG_NHAP =
            "SELECT tk.TenDangNhap, tk.MatKhau, tk.VaiTro, tk.MaNV, tk.TrangThai, nv.HoTen "
            + "FROM TAIKHOAN tk "
            + "JOIN NHANVIEN nv ON nv.MaNV = tk.MaNV "
            + "WHERE tk.TenDangNhap = ?";

    private static final String SQL_DEM_THEO_TEN_DANG_NHAP =
            "SELECT COUNT(*) FROM TAIKHOAN WHERE TenDangNhap = ?";

    private static final String SQL_CAP_NHAT_MAT_KHAU =
            "UPDATE TAIKHOAN SET MatKhau = ? WHERE TenDangNhap = ?";

    private static final String SQL_CAP_NHAT_VAI_TRO =
            "UPDATE TAIKHOAN SET VaiTro = ? WHERE MaNV = ?";

    public TaiKhoan dangNhap(String tenDangNhap, String matKhau) throws SQLException {
        try (Connection ketNoi = KetNoiCSDL.ketNoi();
             PreparedStatement lenh = ketNoi.prepareStatement(SQL_LAY_THEO_TEN_DANG_NHAP)) {
            lenh.setString(1, tenDangNhap);

            try (ResultSet duLieu = lenh.executeQuery()) {
                if (!duLieu.next()) {
                    return null;
                }

                String matKhauTrongCSDL = duLieu.getString("MatKhau");
                if (!MatKhau.khop(matKhau, matKhauTrongCSDL)) {
                    return null;
                }

                return docTaiKhoan(duLieu, tenDangNhap);
            }
        }
    }

    public boolean tenDangNhapDaTonTai(String tenDangNhap) throws SQLException {
        try (Connection ketNoi = KetNoiCSDL.ketNoi();
             PreparedStatement lenh = ketNoi.prepareStatement(SQL_DEM_THEO_TEN_DANG_NHAP)) {
            lenh.setString(1, tenDangNhap);

            try (ResultSet duLieu = lenh.executeQuery()) {
                if (duLieu.next() && duLieu.getInt(1) > 0) {
                    return true;
                }
                return false;
            }
        }
    }

    public void doiMatKhau(String tenDangNhap, String matKhauMoi) throws SQLException {
        try (Connection ketNoi = KetNoiCSDL.ketNoi();
             PreparedStatement lenh = ketNoi.prepareStatement(SQL_CAP_NHAT_MAT_KHAU)) {
            lenh.setString(1, MatKhau.bam(matKhauMoi));
            lenh.setString(2, tenDangNhap);

            if (lenh.executeUpdate() == 0) {
                throw new SQLException("Không tìm thấy tài khoản " + tenDangNhap + ".");
            }
        }
    }

    public void capNhatVaiTro(String maNV, String vaiTro) throws SQLException {
        try (Connection ketNoi = KetNoiCSDL.ketNoi();
             PreparedStatement lenh = ketNoi.prepareStatement(SQL_CAP_NHAT_VAI_TRO)) {
            lenh.setString(1, vaiTro);
            lenh.setString(2, maNV);
            lenh.executeUpdate();
        }
    }

    private static TaiKhoan docTaiKhoan(ResultSet duLieu, String tenDangNhap) throws SQLException {
        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setTenDangNhap(tenDangNhap);
        taiKhoan.setVaiTro(duLieu.getString("VaiTro"));
        taiKhoan.setMaNV(duLieu.getString("MaNV"));
        taiKhoan.setTrangThai(duLieu.getString("TrangThai"));
        taiKhoan.setHoTen(duLieu.getString("HoTen"));

        taiKhoan.setMatKhau(null);
        return taiKhoan;
    }
}

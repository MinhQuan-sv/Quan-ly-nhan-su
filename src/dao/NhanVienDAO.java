package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.NhanVien;
import util.KetNoiCSDL;
import util.MatKhau;

public class NhanVienDAO {
    public static final String MA_CV_QUAN_LY = "CV01";

    private static final String SQL_LAY_CAP_DUOI =
            "SELECT nv.MaNV, nv.HoTen, nv.NgaySinh, nv.GioiTinh, nv.CCCD, nv.SoDienThoai, "
            + "       nv.Email, nv.DiaChi, nv.NgayVaoLam, nv.MaPB, nv.MaCV, nv.MaQuanLy, "
            + "       nv.TrangThai, nv.Luong, pb.TenPB, cv.TenCV, ql.HoTen AS TenQuanLy "
            + "FROM NHANVIEN nv "
            + "LEFT JOIN PHONGBAN pb ON pb.MaPB = nv.MaPB "
            + "LEFT JOIN CHUCVU   cv ON cv.MaCV = nv.MaCV "
            + "LEFT JOIN NHANVIEN ql ON ql.MaNV = nv.MaQuanLy "
            + "WHERE nv.MaQuanLy = ? AND nv.TrangThai <> ? "
            + "ORDER BY nv.HoTen";

    private static final String SQL_TIM_CAP_DUOI =
            "SELECT nv.MaNV, nv.HoTen, nv.NgaySinh, nv.GioiTinh, nv.CCCD, nv.SoDienThoai, "
            + "       nv.Email, nv.DiaChi, nv.NgayVaoLam, nv.MaPB, nv.MaCV, nv.MaQuanLy, "
            + "       nv.TrangThai, nv.Luong, pb.TenPB, cv.TenCV, ql.HoTen AS TenQuanLy "
            + "FROM NHANVIEN nv "
            + "LEFT JOIN PHONGBAN pb ON pb.MaPB = nv.MaPB "
            + "LEFT JOIN CHUCVU   cv ON cv.MaCV = nv.MaCV "
            + "LEFT JOIN NHANVIEN ql ON ql.MaNV = nv.MaQuanLy "
            + "WHERE nv.MaQuanLy = ? AND nv.TrangThai <> ? "
            + "  AND (nv.MaNV LIKE ? OR nv.HoTen LIKE ? "
            + "       OR COALESCE(nv.SoDienThoai, '') LIKE ? "
            + "       OR COALESCE(nv.Email, '') LIKE ?) "
            + "ORDER BY nv.HoTen";

    private static final String SQL_LAY_THEO_MA =
            "SELECT nv.MaNV, nv.HoTen, nv.NgaySinh, nv.GioiTinh, nv.CCCD, nv.SoDienThoai, "
            + "       nv.Email, nv.DiaChi, nv.NgayVaoLam, nv.MaPB, nv.MaCV, nv.MaQuanLy, "
            + "       nv.TrangThai, nv.Luong, pb.TenPB, cv.TenCV, ql.HoTen AS TenQuanLy "
            + "FROM NHANVIEN nv "
            + "LEFT JOIN PHONGBAN pb ON pb.MaPB = nv.MaPB "
            + "LEFT JOIN CHUCVU   cv ON cv.MaCV = nv.MaCV "
            + "LEFT JOIN NHANVIEN ql ON ql.MaNV = nv.MaQuanLy "
            + "WHERE nv.MaNV = ?";

   private static final String SQL_LAY_QUAN_LY =
            "SELECT nv.MaNV, nv.HoTen, nv.NgaySinh, nv.GioiTinh, nv.CCCD, nv.SoDienThoai, "
            + "       nv.Email, nv.DiaChi, nv.NgayVaoLam, nv.MaPB, nv.MaCV, nv.MaQuanLy, "
            + "       nv.TrangThai, nv.Luong, pb.TenPB, cv.TenCV, ql.HoTen AS TenQuanLy "
            + "FROM NHANVIEN nv "
            + "JOIN TAIKHOAN tk ON tk.MaNV = nv.MaNV AND tk.VaiTro = 'QuanLy' "
            + "LEFT JOIN PHONGBAN pb ON pb.MaPB = nv.MaPB "
            + "LEFT JOIN CHUCVU   cv ON cv.MaCV = nv.MaCV "
            + "LEFT JOIN NHANVIEN ql ON ql.MaNV = nv.MaQuanLy "
            + "WHERE nv.TrangThai <> ? "
            + "ORDER BY nv.HoTen";

    private static final String SQL_TANG_LUONG =
            "UPDATE NHANVIEN SET Luong = ? WHERE MaNV = ?";

    private static final String SQL_THOI_VIEC_NHAN_VIEN =
            "UPDATE NHANVIEN SET TrangThai = 'Đã nghỉ' WHERE MaNV = ?";

    private static final String SQL_THOI_VIEC_TAI_KHOAN =
            "UPDATE TAIKHOAN SET TrangThai = 'Bị khóa' WHERE MaNV = ?";

    private static final String SQL_THANG_CHUC_NHAN_VIEN =
            "UPDATE NHANVIEN SET MaCV = ? WHERE MaNV = ?";

    private static final String SQL_THANG_CHUC_TAI_KHOAN =
            "UPDATE TAIKHOAN SET VaiTro = 'QuanLy' WHERE MaNV = ?";

    private static final String SQL_DEM_CAP_DUOI =
            "SELECT COUNT(*) FROM NHANVIEN WHERE MaQuanLy = ? AND TrangThai <> ?";
    
    private static final String SQL_LAY_TAT_CA_MA =
            "SELECT MaNV FROM NHANVIEN";

    private static final String SQL_THEM_NHAN_VIEN =
            "INSERT INTO NHANVIEN "
            + "       (MaNV, HoTen, NgaySinh, GioiTinh, CCCD, SoDienThoai, Email, "
            + "        DiaChi, NgayVaoLam, MaPB, MaCV, MaQuanLy, TrangThai, Luong) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_THEM_TAI_KHOAN =
            "INSERT INTO TAIKHOAN (TenDangNhap, MatKhau, VaiTro, MaNV, TrangThai) "
            + "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_CAP_NHAT =
            "UPDATE NHANVIEN SET "
            + "       HoTen = ?, NgaySinh = ?, GioiTinh = ?, CCCD = ?, SoDienThoai = ?, "
            + "       Email = ?, DiaChi = ?, NgayVaoLam = ?, MaPB = ?, MaCV = ?, "
            + "       TrangThai = ?, Luong = ? "
            + "WHERE  MaNV = ?";

    private static final String SQL_DOI_TRANG_THAI =
            "UPDATE NHANVIEN SET TrangThai = ? WHERE MaNV = ?";

    private static final String SQL_DEM_CCCD_TRUNG =
            "SELECT COUNT(*) FROM NHANVIEN WHERE CCCD = ? AND MaNV <> ?";

    private static final String SQL_DEM_EMAIL_TRUNG =
            "SELECT COUNT(*) FROM NHANVIEN WHERE Email = ? AND MaNV <> ?";

    public List<NhanVien> layCapDuoi(String maQuanLy) throws SQLException {
            List<NhanVien> ketQua = new ArrayList<>();

            try (Connection ketNoi = KetNoiCSDL.ketNoi();
                PreparedStatement lenh = ketNoi.prepareStatement(SQL_LAY_CAP_DUOI)) {
                lenh.setString(1, maQuanLy);
                lenh.setString(2, NhanVien.DA_NGHI);

                try (ResultSet duLieu = lenh.executeQuery()) {
                    while (duLieu.next()) {
                        ketQua.add(docNhanVien(duLieu));
                    }
                }
            }
            return ketQua;
        }

    private static final String SQL_LAY_TAT_CA_DANG_LAM =
            "SELECT nv.MaNV, nv.HoTen, nv.NgaySinh, nv.GioiTinh, nv.CCCD, nv.SoDienThoai, "
            + "       nv.Email, nv.DiaChi, nv.NgayVaoLam, nv.MaPB, nv.MaCV, nv.MaQuanLy, "
            + "       nv.TrangThai, nv.Luong, pb.TenPB, cv.TenCV, ql.HoTen AS TenQuanLy "
            + "FROM NHANVIEN nv "
            + "LEFT JOIN PHONGBAN pb ON pb.MaPB = nv.MaPB "
            + "LEFT JOIN CHUCVU   cv ON cv.MaCV = nv.MaCV "
            + "LEFT JOIN NHANVIEN ql ON ql.MaNV = nv.MaQuanLy "
            + "WHERE nv.TrangThai = ? "
            + "ORDER BY nv.MaNV";

    public List<NhanVien> layTatCaDangLam() throws SQLException {
        List<NhanVien> ketQua = new ArrayList<>();

        try (Connection ketNoi = KetNoiCSDL.ketNoi();
            PreparedStatement lenh = ketNoi.prepareStatement(SQL_LAY_TAT_CA_DANG_LAM)) {
            lenh.setString(1, NhanVien.DANG_LAM);

            try (ResultSet duLieu = lenh.executeQuery()) {
                while (duLieu.next()) {
                    ketQua.add(docNhanVien(duLieu));
                }
            }
        }
        return ketQua;
    }
    
    public List<NhanVien> timCapDuoi(String maQuanLy, String tuKhoa) throws SQLException {
            List<NhanVien> ketQua = new ArrayList<>();

            String mau = "%" + (tuKhoa == null ? "" : tuKhoa.trim()) + "%";

            try (Connection ketNoi = KetNoiCSDL.ketNoi();
                PreparedStatement lenh = ketNoi.prepareStatement(SQL_TIM_CAP_DUOI)) {
                lenh.setString(1, maQuanLy);
                lenh.setString(2, NhanVien.DA_NGHI);
                for (int chiSo = 3; chiSo <= 6; chiSo++) {
                    lenh.setString(chiSo, mau);
                }

                try (ResultSet duLieu = lenh.executeQuery()) {
                    while (duLieu.next()) {
                        ketQua.add(docNhanVien(duLieu));
                    }
                }
            }
            return ketQua;
    }


    public NhanVien layTheoMa(String maNV) throws SQLException {
        try (Connection ketNoi = KetNoiCSDL.ketNoi();
             PreparedStatement lenh = ketNoi.prepareStatement(SQL_LAY_THEO_MA)) {
            lenh.setString(1, maNV);

            try (ResultSet duLieu = lenh.executeQuery()) {
                return duLieu.next() ? docNhanVien(duLieu) : null;
            }
        }
    }

    public int demCapDuoi(String maQuanLy) throws SQLException {
            try (Connection ketNoi = KetNoiCSDL.ketNoi();
                PreparedStatement lenh = ketNoi.prepareStatement(SQL_DEM_CAP_DUOI)) {
                lenh.setString(1, maQuanLy);
                lenh.setString(2, NhanVien.DA_NGHI);

                try (ResultSet duLieu = lenh.executeQuery()) {
                    return duLieu.next() ? duLieu.getInt(1) : 0;
                }
            }
        }

    public String sinhMaNhanVien() throws SQLException {
        int soLonNhat = 0;

        try (Connection ketNoi = KetNoiCSDL.ketNoi();
             PreparedStatement lenh = ketNoi.prepareStatement(SQL_LAY_TAT_CA_MA);
             ResultSet duLieu = lenh.executeQuery()) {
            while (duLieu.next()) {
                String maNV = duLieu.getString("MaNV");
                if (maNV == null || maNV.length() <= 2) {
                    continue;
                }

                if (!maNV.regionMatches(true, 0, "NV", 0, 2)) {
                    continue;
                }
                try {
                    int so = Integer.parseInt(maNV.substring(2));
                    if (so > soLonNhat) {
                        soLonNhat = so;
                    }
                } catch (NumberFormatException loi) {
                }
            }
        }

        return String.format("NV%03d", soLonNhat + 1);
    }

    public void themKemTaiKhoan(NhanVien nhanVien, String tenDangNhap, String matKhauTho,
                                String vaiTro) throws SQLException {
        try (Connection ketNoi = KetNoiCSDL.ketNoi()) {
            boolean cheDoTuDongBanDau = ketNoi.getAutoCommit();
            ketNoi.setAutoCommit(false);

            try {
                try (PreparedStatement lenh = ketNoi.prepareStatement(SQL_THEM_NHAN_VIEN)) {
                    ganThongTinNhanVien(lenh, nhanVien);
                    lenh.executeUpdate();
                }

                try (PreparedStatement lenh = ketNoi.prepareStatement(SQL_THEM_TAI_KHOAN)) {
                    lenh.setString(1, tenDangNhap);
                    lenh.setString(2, MatKhau.bam(matKhauTho));
                    lenh.setString(3, vaiTro);
                    lenh.setString(4, nhanVien.getMaNV());
                    lenh.setString(5, "Hoạt động");
                    lenh.executeUpdate();
                }

                ketNoi.commit();
            } catch (SQLException loi) {
                ketNoi.rollback();
                throw loi;
            } finally {
                ketNoi.setAutoCommit(cheDoTuDongBanDau);
            }
        }
    }

    public void capNhat(NhanVien nhanVien) throws SQLException {
        try (Connection ketNoi = KetNoiCSDL.ketNoi();
             PreparedStatement lenh = ketNoi.prepareStatement(SQL_CAP_NHAT)) {
            lenh.setString(1, nhanVien.getHoTen());
            ganNgay(lenh, 2, nhanVien.getNgaySinh());
            lenh.setString(3, nhanVien.getGioiTinh());
            lenh.setString(4, nhanVien.getCccd());
            lenh.setString(5, nhanVien.getSoDienThoai());
            lenh.setString(6, nhanVien.getEmail());
            lenh.setString(7, nhanVien.getDiaChi());
            ganNgay(lenh, 8, nhanVien.getNgayVaoLam());
            lenh.setString(9, nhanVien.getMaPB());
            lenh.setString(10, nhanVien.getMaCV());
            lenh.setString(11, nhanVien.getTrangThai());
            lenh.setBigDecimal(12, luongHoacKhong(nhanVien));
            lenh.setString(13, nhanVien.getMaNV());

            lenh.executeUpdate();
        }
    }

    public void doiTrangThai(String maNV, String trangThai) throws SQLException {
        try (Connection ketNoi = KetNoiCSDL.ketNoi();
             PreparedStatement lenh = ketNoi.prepareStatement(SQL_DOI_TRANG_THAI)) {
            lenh.setString(1, trangThai);
            lenh.setString(2, maNV);
            lenh.executeUpdate();
        }
    }

   public List<NhanVien> layTatCaQuanLy() throws SQLException {
            List<NhanVien> ketQua = new ArrayList<>();

            try (Connection ketNoi = KetNoiCSDL.ketNoi();
                PreparedStatement lenh = ketNoi.prepareStatement(SQL_LAY_QUAN_LY)) {
                lenh.setString(1, NhanVien.DA_NGHI);

                try (ResultSet duLieu = lenh.executeQuery()) {
                    while (duLieu.next()) {
                        ketQua.add(docNhanVien(duLieu));
                    }
                }
            }
            return ketQua;
        }
    public void tangLuong(String maNV, BigDecimal luongMoi) throws SQLException {
        if (luongMoi == null || luongMoi.signum() <= 0) {
            throw new SQLException("Lương mới phải lớn hơn 0.");
        }

        try (Connection ketNoi = KetNoiCSDL.ketNoi();
             PreparedStatement lenh = ketNoi.prepareStatement(SQL_TANG_LUONG)) {
            lenh.setBigDecimal(1, luongMoi);
            lenh.setString(2, maNV);

            if (lenh.executeUpdate() == 0) {
                throw new SQLException("Không tìm thấy nhân viên " + maNV + ".");
            }
        }
    }

    public void thoiViec(String maNV) throws SQLException {
        try (Connection ketNoi = KetNoiCSDL.ketNoi()) {
            boolean cheDoBanDau = ketNoi.getAutoCommit();
            ketNoi.setAutoCommit(false);

            try {
                try (PreparedStatement lenh = ketNoi.prepareStatement(SQL_THOI_VIEC_NHAN_VIEN)) {
                    lenh.setString(1, maNV);
                    lenh.executeUpdate();
                }
                try (PreparedStatement lenh = ketNoi.prepareStatement(SQL_THOI_VIEC_TAI_KHOAN)) {
                    lenh.setString(1, maNV);
                    lenh.executeUpdate();
                }
                ketNoi.commit();
            } catch (SQLException loi) {
                ketNoi.rollback();
                throw loi;
            } finally {
                ketNoi.setAutoCommit(cheDoBanDau);
            }
        }
    }
    public void thangChuc(String maNV, String maCVQuanLy) throws SQLException {
        try (Connection ketNoi = KetNoiCSDL.ketNoi()) {
            boolean cheDoBanDau = ketNoi.getAutoCommit();
            ketNoi.setAutoCommit(false);

            try {
                try (PreparedStatement lenh = ketNoi.prepareStatement(SQL_THANG_CHUC_NHAN_VIEN)) {
                    lenh.setString(1, maCVQuanLy);
                    lenh.setString(2, maNV);
                    lenh.executeUpdate();
                }
                try (PreparedStatement lenh = ketNoi.prepareStatement(SQL_THANG_CHUC_TAI_KHOAN)) {
                    lenh.setString(1, maNV);
                    lenh.executeUpdate();
                }
                ketNoi.commit();
            } catch (SQLException loi) {
                ketNoi.rollback();
                throw loi;
            } finally {
                ketNoi.setAutoCommit(cheDoBanDau);
            }
        }
    }

    public boolean cccdDaTonTai(String cccd, String maNVDangSua) throws SQLException {
        return demGiaTriTrung(SQL_DEM_CCCD_TRUNG, cccd, maNVDangSua);
    }

    public boolean emailDaTonTai(String email, String maNVDangSua) throws SQLException {
        return demGiaTriTrung(SQL_DEM_EMAIL_TRUNG, email, maNVDangSua);
    }

    private boolean demGiaTriTrung(String cauLenh, String giaTri, String maNVDangSua)
            throws SQLException {
        if (giaTri == null || giaTri.trim().isEmpty()) {
            return false;
        }

        try (Connection ketNoi = KetNoiCSDL.ketNoi();
             PreparedStatement lenh = ketNoi.prepareStatement(cauLenh)) {
            lenh.setString(1, giaTri.trim());

            lenh.setString(2, maNVDangSua == null ? "" : maNVDangSua);

            try (ResultSet duLieu = lenh.executeQuery()) {
                return duLieu.next() && duLieu.getInt(1) > 0;
            }
        }
    }

    private static NhanVien docNhanVien(ResultSet duLieu) throws SQLException {
        NhanVien nhanVien = new NhanVien();
        nhanVien.setMaNV(duLieu.getString("MaNV"));
        nhanVien.setHoTen(duLieu.getString("HoTen"));
        nhanVien.setNgaySinh(docNgay(duLieu, "NgaySinh"));
        nhanVien.setGioiTinh(duLieu.getString("GioiTinh"));
        nhanVien.setCccd(duLieu.getString("CCCD"));
        nhanVien.setSoDienThoai(duLieu.getString("SoDienThoai"));
        nhanVien.setEmail(duLieu.getString("Email"));
        nhanVien.setDiaChi(duLieu.getString("DiaChi"));
        nhanVien.setNgayVaoLam(docNgay(duLieu, "NgayVaoLam"));
        nhanVien.setMaPB(duLieu.getString("MaPB"));
        nhanVien.setMaCV(duLieu.getString("MaCV"));
        nhanVien.setMaQuanLy(duLieu.getString("MaQuanLy"));
        nhanVien.setTrangThai(duLieu.getString("TrangThai"));
        nhanVien.setLuong(duLieu.getBigDecimal("Luong"));

        nhanVien.setTenPB(duLieu.getString("TenPB"));
        nhanVien.setTenCV(duLieu.getString("TenCV"));
        nhanVien.setTenQuanLy(duLieu.getString("TenQuanLy"));
        return nhanVien;
    }

    private static void ganThongTinNhanVien(PreparedStatement lenh, NhanVien nhanVien)
            throws SQLException {
        lenh.setString(1, nhanVien.getMaNV());
        lenh.setString(2, nhanVien.getHoTen());
        ganNgay(lenh, 3, nhanVien.getNgaySinh());
        lenh.setString(4, nhanVien.getGioiTinh());
        lenh.setString(5, nhanVien.getCccd());
        lenh.setString(6, nhanVien.getSoDienThoai());
        lenh.setString(7, nhanVien.getEmail());
        lenh.setString(8, nhanVien.getDiaChi());
        ganNgay(lenh, 9, nhanVien.getNgayVaoLam());
        lenh.setString(10, nhanVien.getMaPB());
        lenh.setString(11, nhanVien.getMaCV());
        lenh.setString(12, nhanVien.getMaQuanLy());
        lenh.setString(13, nhanVien.getTrangThai());
        lenh.setBigDecimal(14, luongHoacKhong(nhanVien));
    }

    private static BigDecimal luongHoacKhong(NhanVien nhanVien) {
        return nhanVien.getLuong() == null ? BigDecimal.ZERO : nhanVien.getLuong();
    }

    private static void ganNgay(PreparedStatement lenh, int chiSo, LocalDate ngay)
            throws SQLException {
        if (ngay == null) {
            lenh.setNull(chiSo, Types.DATE);
        } else {
            lenh.setDate(chiSo, Date.valueOf(ngay));
        }
    }

    private static LocalDate docNgay(ResultSet duLieu, String tenCot) throws SQLException {
        Date ngay = duLieu.getDate(tenCot);
        return ngay == null ? null : ngay.toLocalDate();
    }
}

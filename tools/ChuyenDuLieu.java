import util.KetNoiCSDL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ChuyenDuLieu {

    private static final String SQLSERVER_URL =
            "jdbc:sqlserver://localhost:1433;databaseName=QuanLyNhanSu"
            + ";encrypt=true;trustServerCertificate=true;loginTimeout=10";
    private static final String SQLSERVER_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    private static final String[] XOA_THEO_THU_TU = {
            "CHAMCONG", "TAIKHOAN", "NHANVIEN", "CALAMVIEC", "CHUCVU", "PHONGBAN"
    };

    public static void main(String[] args) throws Exception {
        String taiKhoanSqlServer = args.length > 0 ? args[0] : "sa";
        String matKhauSqlServer = args.length > 1 ? args[1] : "123456";

        System.out.println("Nguồn : SQL Server (localhost, database QuanLyNhanSu)");
        System.out.println("Đích  : MySQL theo db.properties ("
                + KetNoiCSDL.tenTaiKhoan() + " / " + KetNoiCSDL.tenDatabase() + ")");
        System.out.println();

        Class.forName(SQLSERVER_DRIVER);

        try (Connection nguon = DriverManager.getConnection(
                SQLSERVER_URL, taiKhoanSqlServer, matKhauSqlServer)) {

            System.out.println("Đã đọc dữ liệu từ SQL Server:");
            System.out.println("  PHONGBAN  : " + dem(nguon, "PHONGBAN") + " dòng");
            System.out.println("  CHUCVU    : " + dem(nguon, "CHUCVU") + " dòng");
            System.out.println("  CALAMVIEC : " + dem(nguon, "CALAMVIEC") + " dòng");
            System.out.println("  NHANVIEN  : " + dem(nguon, "NHANVIEN") + " dòng");
            System.out.println("  TAIKHOAN  : " + dem(nguon, "TAIKHOAN") + " dòng");
            System.out.println("  CHAMCONG  : " + dem(nguon, "CHAMCONG") + " dòng");
            System.out.println();

            try (Connection dich = KetNoiCSDL.ketNoi()) {
                xoaDuLieuCu(dich);

                saoChep(nguon, dich, "PHONGBAN",
                        new String[]{"MaPB", "TenPB", "MoTa"}, "MaPB");
                saoChep(nguon, dich, "CHUCVU",
                        new String[]{"MaCV", "TenCV", "MoTa"}, "MaCV");
                saoChep(nguon, dich, "CALAMVIEC",
                        new String[]{"MaCa", "TenCa", "GioBatDau", "GioKetThuc"}, "MaCa");
                saoChep(nguon, dich, "NHANVIEN",
                        new String[]{"MaNV", "HoTen", "NgaySinh", "GioiTinh", "CCCD",
                                "SoDienThoai", "Email", "DiaChi", "NgayVaoLam",
                                "MaPB", "MaCV", "MaQuanLy", "TrangThai"},
                        "CASE WHEN MaQuanLy IS NULL THEN 0 ELSE 1 END, MaNV");
                saoChep(nguon, dich, "TAIKHOAN",
                        new String[]{"TenDangNhap", "MatKhau", "VaiTro", "MaNV", "TrangThai"},
                        "TenDangNhap");
                saoChep(nguon, dich, "CHAMCONG",
                        new String[]{"MaNV", "NgayChamCong", "MaCa", "GioVao", "GioRa",
                                "TrangThai", "GhiChu"},
                        "NgayChamCong, MaNV");

                System.out.println();
                System.out.println("Đã ghi sang MySQL:");
                for (String bang : new String[]{"PHONGBAN", "CHUCVU", "CALAMVIEC",
                        "NHANVIEN", "TAIKHOAN", "CHAMCONG"}) {
                    System.out.printf("  %-10s: %d dòng%n", bang, dem(dich, bang));
                }
                System.out.println();
                System.out.println("XONG. Chạy kiểm tra: java -cp \"out;lib/*\" tools/KiemTraKetNoi.java");
            }
        }
    }

    private static int dem(Connection ketNoi, String bang) throws SQLException {
        try (Statement lenh = ketNoi.createStatement();
             ResultSet duLieu = lenh.executeQuery("SELECT COUNT(*) FROM " + bang)) {
            return duLieu.next() ? duLieu.getInt(1) : 0;
        }
    }

    private static void xoaDuLieuCu(Connection dich) throws SQLException {
        for (String bang : XOA_THEO_THU_TU) {
            try (Statement lenh = dich.createStatement()) {
                lenh.executeUpdate("DELETE FROM " + bang);
            }
        }
    }

    private static void saoChep(Connection nguon, Connection dich, String bang,
                                String[] cot, String sapXep) throws SQLException {
        String danhSachCot = String.join(", ", cot);
        String cauDoc = "SELECT " + danhSachCot + " FROM " + bang + " ORDER BY " + sapXep;

        StringBuilder dauHoi = new StringBuilder();
        for (int i = 0; i < cot.length; i++) {
            dauHoi.append(i == 0 ? "?" : ", ?");
        }
        String cauGhi = "INSERT INTO " + bang + " (" + danhSachCot + ") VALUES (" + dauHoi + ")";

        int soDong = 0;
        try (Statement lenhDoc = nguon.createStatement();
             ResultSet duLieu = lenhDoc.executeQuery(cauDoc);
             PreparedStatement lenhGhi = dich.prepareStatement(cauGhi)) {

            while (duLieu.next()) {
                for (int i = 1; i <= cot.length; i++) {
                    lenhGhi.setObject(i, duLieu.getObject(i));
                }
                lenhGhi.executeUpdate();
                soDong++;
            }
        }
        System.out.printf("  đã chép %-10s %d dòng%n", bang, soDong);
    }
}

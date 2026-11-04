package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class KetNoiCSDL {
    private static final String TEN_FILE_CAU_HINH = "db.properties";

    private static final String MAT_KHAU_MAU = "MAT_KHAU_MYSQL_CUA_BAN";

    private static final String[] THU_MUC_CHUA_DRIVER = {"lib", "../lib"};

    private static final String LOI_THIEU_DRIVER =
            "Chưa nạp được driver JDBC của CSDL.\n\n"
            + "Cách khắc phục:\n"
            + "1) Tải driver JDBC (SQL Server: mssql-jdbc-*.jar | MySQL: mysql-connector-j-*.jar).\n"
            + "2) Chép file jar vào thư mục lib của dự án.\n"
            + "3) Chạy lại chương trình.\n";

    private static String driver;
    private static String url;
    private static String taiKhoan;
    private static String matKhau;
    private static boolean daDocCauHinh = false;

    private static boolean daNapDriver = false;

    private static Driver driverNapTuLib = null;

    private KetNoiCSDL() {
    }

    public static Connection ketNoi() throws SQLException {
        return ketNoi(null);
    }

    public static Connection ketNoi(String tenDatabase) throws SQLException {
        docCauHinhNeuCan();
        napDriver();

        String urlDung = url;
        if (tenDatabase != null && !tenDatabase.trim().isEmpty()) {
            urlDung = thayTenDatabase(url, tenDatabase.trim());
        }

        return moKetNoi(urlDung);
    }

    public static String kiemTraKetNoi() {
        try (Connection ketNoi = ketNoi()) {
            return ketNoi == null ? "Không mở được kết nối." : null;
        } catch (SQLException loi) {
            return moTaLoi(loi);
        }
    }

    public static String tenHeQuanTri() throws SQLException {
        docCauHinhNeuCan();
        String driverThuong = driver.toLowerCase();
        String urlThuong = url.toLowerCase();

        if (driverThuong.contains("mysql") || urlThuong.startsWith("jdbc:mysql")) {
            return "MySQL";
        }
        if (driverThuong.contains("sqlserver") || urlThuong.startsWith("jdbc:sqlserver")) {
            return "SQLServer";
        }
        return "Khác";
    }

    public static String tenDatabase() throws SQLException {
        docCauHinhNeuCan();
        Matcher timSqlServer = Pattern.compile("(?i)databaseName=([^;]+)").matcher(url);
        if (timSqlServer.find()) {
            return timSqlServer.group(1);
        }
        Matcher timMySql = Pattern.compile("jdbc:mysql://[^/]+/([^?]+)").matcher(url);
        if (timMySql.find()) {
            return timMySql.group(1);
        }
        return "(không có trong db.url)";
    }

    public static String tenTaiKhoan() throws SQLException {
        docCauHinhNeuCan();
        return taiKhoan.isEmpty() ? "(Windows Authentication)" : taiKhoan;
    }

    private static Connection moKetNoi(String urlDung) throws SQLException {
        Properties thongSo = new Properties();
        if (!taiKhoan.isEmpty()) {
            thongSo.setProperty("user", taiKhoan);
            thongSo.setProperty("password", matKhau);
        }

        if (driverNapTuLib != null) {
            Connection ketNoi = driverNapTuLib.connect(urlDung, thongSo);
            if (ketNoi == null) {
                throw new SQLException("Driver không chấp nhận đường dẫn: " + urlDung);
            }
            return ketNoi;
        }

        if (taiKhoan.isEmpty()) {
            return DriverManager.getConnection(urlDung);
        }
        return DriverManager.getConnection(urlDung, taiKhoan, matKhau);
    }

    private static synchronized void napDriver() throws SQLException {
        if (daNapDriver) {
            return;
        }

        try {
            Class.forName(driver);
            daNapDriver = true;
            return;
        } catch (ClassNotFoundException loi) {
        }

        driverNapTuLib = timDriverTrongThuMucLib();
        if (driverNapTuLib == null) {
            throw new SQLException(LOI_THIEU_DRIVER);
        }
        daNapDriver = true;
    }

    private static Driver timDriverTrongThuMucLib() {
        for (String tenThuMuc : THU_MUC_CHUA_DRIVER) {
            File thuMuc = new File(tenThuMuc);
            File[] cacTepJar = thuMuc.listFiles(
                    (dir, tenTep) -> tenTep.toLowerCase().endsWith(".jar"));
            if (cacTepJar == null || cacTepJar.length == 0) {
                continue;
            }

            try {
                List<URL> danhSachUrl = new ArrayList<>();
                for (File tepJar : cacTepJar) {
                    danhSachUrl.add(tepJar.toURI().toURL());
                }
                URLClassLoader boNap = new URLClassLoader(
                        danhSachUrl.toArray(new URL[0]),
                        KetNoiCSDL.class.getClassLoader());

                Class<?> lopDriver = Class.forName(driver, true, boNap);
                return (Driver) lopDriver.getDeclaredConstructor().newInstance();
            } catch (Exception loi) {
            }
        }
        return null;
    }

    private static synchronized void docCauHinhNeuCan() throws SQLException {
        if (daDocCauHinh) {
            return;
        }

        Properties thongSo = new Properties();

        thongSo.setProperty("db.driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        thongSo.setProperty("db.url",
                "jdbc:sqlserver://localhost:1433;databaseName=QuanLyNhanSu");
        thongSo.setProperty("db.user", "");
        thongSo.setProperty("db.password", "");

        try {
            napFileCauHinh(thongSo);
        } catch (IOException loi) {
            throw new SQLException("Không đọc được " + TEN_FILE_CAU_HINH + ": "
                    + loi.getMessage(), loi);
        }

        driver = thongSo.getProperty("db.driver").trim();
        url = thongSo.getProperty("db.url").trim();
        taiKhoan = thongSo.getProperty("db.user", "").trim();
        matKhau = thongSo.getProperty("db.password", "");
        daDocCauHinh = true;
    }

    private static void napFileCauHinh(Properties thongSo) throws IOException {
        File tep = new File(TEN_FILE_CAU_HINH);
        if (tep.isFile()) {
            try (InputStream vao = new FileInputStream(tep)) {
                thongSo.load(new InputStreamReader(vao, StandardCharsets.UTF_8));
            }
            return;
        }

        try (InputStream vao = KetNoiCSDL.class.getResourceAsStream("/" + TEN_FILE_CAU_HINH)) {
            if (vao != null) {
                thongSo.load(new InputStreamReader(vao, StandardCharsets.UTF_8));
            }
        }
    }

    private static String thayTenDatabase(String urlGoc, String tenDatabase) {
        if (urlGoc.toLowerCase().startsWith("jdbc:mysql")) {
            return urlGoc.replaceAll("(?i)(jdbc:mysql://[^/]+/)[^?]*", "$1" + tenDatabase);
        }

        return urlGoc.replaceAll("(?i)databaseName=[^;]*", "databaseName=" + tenDatabase);
    }

    public static String moTaLoi(SQLException loi) {
        String thongDiep = loi.getMessage() == null ? "" : loi.getMessage();

        if (thongDiep.startsWith("Chưa nạp được driver")
                || thongDiep.contains("Không đọc được " + TEN_FILE_CAU_HINH)) {
            return thongDiep;
        }

        StringBuilder ketQua = new StringBuilder();
        ketQua.append("Không kết nối được CSDL.\n\n");
        ketQua.append("Chi tiết: ").append(thongDiep).append("\n\n");

        if (thongDiep.contains("TCP/IP connection to the host")
                || thongDiep.contains("Communications link failure")) {
            ketQua.append("NGUYÊN NHÂN: không mở được cổng kết nối tới máy chủ CSDL.\n\n")
                    .append("Cách sửa:\n")
                    .append("- MySQL: kiểm tra dịch vụ MySQL đang chạy và cổng 3306 mở.\n")
                    .append("- SQL Server: bật TCP/IP trong SQL Server Configuration Manager\n")
                    .append("  (SQLServerManager16.msc), cổng 1433, rồi restart dịch vụ.\n")
                    .append("- Xem lại db.url trong db.properties.\n");
        } else if (thongDiep.contains("Access denied for user")
                || thongDiep.contains("Login failed for user")) {
            if (MAT_KHAU_MAU.equals(matKhau)) {
                ketQua.append("NGUYÊN NHÂN: bạn CHƯA thay mật khẩu mẫu trong db.properties.\n\n")
                        .append("Cách sửa: mở db.properties, đổi dòng\n")
                        .append("   db.password=").append(MAT_KHAU_MAU).append('\n')
                        .append("thành mật khẩu MySQL thật, ví dụ:\n")
                        .append("   db.password=123456\n")
                        .append("Lưu file (Ctrl+S) rồi chạy lại.\n");
            } else {
                ketQua.append("NGUYÊN NHÂN: sai tài khoản hoặc mật khẩu CSDL.\n\n")
                        .append("Cách sửa: sửa db.user / db.password trong db.properties cho đúng.\n")
                        .append("Nếu không nhớ mật khẩu MySQL, đặt lại bằng lệnh:\n")
                        .append("   ALTER USER 'root'@'localhost' IDENTIFIED BY '123456';\n");
            }
        } else if (thongDiep.contains("Cannot open database")
                || thongDiep.contains("Unknown database")) {
            ketQua.append("NGUYÊN NHÂN: CSDL chưa được tạo.\n\n")
                    .append("Cách sửa: nạp script trong thư mục database (xem README.md).\n");
        } else if (thongDiep.contains("SSL") || thongDiep.contains("ncrypt")
                || thongDiep.contains("ecure connection")) {
            ketQua.append("NGUYÊN NHÂN: lỗi mã hoá TLS giữa driver và máy chủ.\n\n")
                    .append("Cách sửa: thêm encrypt=false;trustServerCertificate=true ")
                    .append("(SQL Server) hoặc useSSL=false (MySQL) vào db.url.\n");
        } else {
            ketQua.append("Hãy kiểm tra: dịch vụ CSDL đang chạy, đã nạp script CSDL, ")
                    .append("và db.properties có đúng thông tin.\n");
        }

        ketQua.append("\nKiểm tra nhanh: java tools/KiemTraKetNoi.java");
        return ketQua.toString();
    }
}

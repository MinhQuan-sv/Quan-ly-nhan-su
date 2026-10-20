import util.KetNoiCSDL;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ChayScriptSQL {
    public static void main(String[] args) {
        String duongDan = args.length > 0 ? args[0] : "database/mysql/QuanLyNhanSu.sql";
        Path tep = Paths.get(duongDan);
        if (!Files.isRegularFile(tep)) {
            System.out.println("Không tìm thấy file: " + tep.toAbsolutePath());
            System.out.println("Ví dụ: java -cp \"out;lib/*\" tools/ChayScriptSQL.java database/mysql/QuanLyNhanSu.sql");
            System.exit(2);
        }

        List<String> lo;
        try {
            lo = chiaLo(new String(Files.readAllBytes(tep), StandardCharsets.UTF_8));
        } catch (IOException ex) {
            System.out.println("Không đọc được file: " + ex.getMessage());
            System.exit(2);
            return;
        }

        System.out.println("File   : " + tep.toAbsolutePath());
        System.out.println("Số lô  : " + lo.size());

        String database;
        try {
            database = "MySQL".equalsIgnoreCase(KetNoiCSDL.tenHeQuanTri()) ? "mysql" : "master";
        } catch (SQLException ex) {
            System.out.println("Không đọc được cấu hình: " + ex.getMessage());
            System.exit(1);
            return;
        }
        System.out.println("Kết nối vào database hệ thống: " + database);
        System.out.println();
        try (Connection ketNoi = KetNoiCSDL.ketNoi(database);
             Statement lenh = ketNoi.createStatement()) {
            Connection dangDung = ketNoi;
            Statement lenhDangDung = lenh;

            for (int i = 0; i < lo.size(); i++) {
                String cauLenh = lo.get(i);
                String rutGon = cauLenh.trim();
                if (rutGon.isEmpty()) {
                    continue;
                }

                if (rutGon.regionMatches(true, 0, "USE ", 0, 4)) {
                    String tenMoi = rutGon.substring(4).replace(";", "").trim();
                    System.out.println("[lo " + (i + 1) + "] chuyển sang database: " + tenMoi);
                    if (!tenMoi.equalsIgnoreCase(database)) {
                        lenhDangDung.close();
                        dangDung.close();
                        database = tenMoi;
                        dangDung = KetNoiCSDL.ketNoi(database);
                        lenhDangDung = dangDung.createStatement();
                    }
                    continue;
                }

                try {
                    lenhDangDung.execute(cauLenh);
                    System.out.println("[lo " + (i + 1) + "] OK  - "
                            + rutGon.replaceAll("\\s+", " ").substring(0,
                                    Math.min(70, rutGon.replaceAll("\\s+", " ").length())));
                } catch (SQLException ex) {
                    System.out.println("[lo " + (i + 1) + "] LỖI: " + ex.getMessage());
                    System.out.println("        Nội dung lô: "
                            + rutGon.replaceAll("\\s+", " "));
                    lenhDangDung.close();
                    dangDung.close();
                    System.exit(1);
                }
            }
            lenhDangDung.close();
            dangDung.close();
        } catch (SQLException ex) {
            System.out.println("Không kết nối được CSDL: " + KetNoiCSDL.moTaLoi(ex));
            System.exit(1);
        }

        System.out.println();
        System.out.println("Đã chạy xong script. Kiểm tra dữ liệu bằng:");
        System.out.println("  java -cp \"out;lib/*\" tools/KiemTraKetNoi.java");
    }

    private static List<String> chiaLo(String noiDung) {
        List<String> ketQua = new ArrayList<>();
        StringBuilder loHienTai = new StringBuilder();
        for (String dong : noiDung.split("\\R")) {
            if (dong.trim().equalsIgnoreCase("GO")) {
                if (loHienTai.toString().trim().length() > 0) {
                    ketQua.add(loHienTai.toString());
                }
                loHienTai.setLength(0);
            } else {
                loHienTai.append(dong).append('\n');
            }
        }
        if (loHienTai.toString().trim().length() > 0) {
            ketQua.add(loHienTai.toString());
        }

        if (ketQua.size() == 1) {
            List<String> theoChamPhay = new ArrayList<>();
            for (String cauLenh : ketQua.get(0).split(";")) {
                if (cauLenh.trim().length() > 0) {
                    theoChamPhay.add(cauLenh);
                }
            }
            if (theoChamPhay.size() > 1) {
                return theoChamPhay;
            }
        }
        return ketQua;
    }
}

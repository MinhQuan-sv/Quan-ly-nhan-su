package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class MatKhau {
    private MatKhau() {
    }

    public static String bam(String matKhau) {
        if (matKhau == null) {
            return null;
        }

        try {
            MessageDigest boBam = MessageDigest.getInstance("SHA-256");

            byte[] ketQuaBam = boBam.digest(matKhau.getBytes(StandardCharsets.UTF_8));

            StringBuilder chuoiHex = new StringBuilder(ketQuaBam.length * 2);
            for (byte giaTri : ketQuaBam) {
                chuoiHex.append(String.format("%02x", giaTri));
            }
            return chuoiHex.toString();
        } catch (NoSuchAlgorithmException loi) {
            throw new IllegalStateException("Máy ảo Java không hỗ trợ SHA-256", loi);
        }
    }

    public static boolean khop(String matKhauNhap, String matKhauDaLuu) {
        if (matKhauNhap == null || matKhauDaLuu == null) {
            return false;
        }

        String maBamCuaNguoiDung = bam(matKhauNhap);
        String maBamTrongCSDL = matKhauDaLuu.trim();

        return maBamCuaNguoiDung.equalsIgnoreCase(maBamTrongCSDL);
    }
}

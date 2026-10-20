package util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public final class DinhDang {
    public static final DateTimeFormatter KI_NAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static final DateTimeFormatter GIO_PHUT = DateTimeFormatter.ofPattern("HH:mm");

    public static final Locale VIET_NAM = Locale.forLanguageTag("vi-VN");

    private DinhDang() {
    }

    public static String ngay(LocalDate ngay) {
        if (ngay == null) {
            return "";
        }
        return KI_NAY.format(ngay);
    }

    public static String gio(LocalTime gio) {
        if (gio == null) {
            return "";
        }
        return GIO_PHUT.format(gio);
    }

    public static LocalDate docNgay(String chuoi) {
        if (chuoi == null || chuoi.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(chuoi.trim(), KI_NAY);
        } catch (DateTimeParseException loi) {
            throw new IllegalArgumentException(
                    "Ngày \"" + chuoi.trim() + "\" không hợp lệ, cần theo dạng dd/MM/yyyy.");
        }
    }

    public static LocalTime docGio(String chuoi) {
        if (chuoi == null || chuoi.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalTime.parse(chuoi.trim(), GIO_PHUT);
        } catch (DateTimeParseException loi) {
            throw new IllegalArgumentException(
                    "Giờ \"" + chuoi.trim() + "\" không hợp lệ, cần theo dạng HH:mm.");
        }
    }

    public static String so(double giaTri, int chuSoThapPhan) {
        NumberFormat dinhDang = NumberFormat.getNumberInstance(VIET_NAM);
        dinhDang.setMinimumFractionDigits(chuSoThapPhan);
        dinhDang.setMaximumFractionDigits(chuSoThapPhan);
        return dinhDang.format(giaTri);
    }

    public static String soNguyen(int giaTri) {
        return NumberFormat.getIntegerInstance(VIET_NAM).format(giaTri);
    }

    public static String tien(BigDecimal giaTri) {
        if (giaTri == null) {
            return "";
        }
        return NumberFormat.getNumberInstance(VIET_NAM).format(giaTri);
    }
}

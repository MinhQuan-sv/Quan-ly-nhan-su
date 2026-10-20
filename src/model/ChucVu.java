package model;

import java.util.Objects;

public class ChucVu {
    private String maCV;

    private String tenCV;

    private String moTa;

    public ChucVu() {
    }

    public ChucVu(String maCV, String tenCV, String moTa) {
        this.maCV = maCV;
        this.tenCV = tenCV;
        this.moTa = moTa;
    }

    public String getMaCV() {
        return maCV;
    }

    public void setMaCV(String maCV) {
        this.maCV = maCV;
    }

    public String getTenCV() {
        return tenCV;
    }

    public void setTenCV(String tenCV) {
        this.tenCV = tenCV;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    @Override
    public String toString() {
        return maCV + " - " + tenCV;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maCV);
    }

    @Override
    public boolean equals(Object doiTuongKhac) {
        if (this == doiTuongKhac) {
            return true;
        }
        if (doiTuongKhac == null || getClass() != doiTuongKhac.getClass()) {
            return false;
        }
        ChucVu chucVuKhac = (ChucVu) doiTuongKhac;
        return Objects.equals(maCV, chucVuKhac.maCV);
    }
}

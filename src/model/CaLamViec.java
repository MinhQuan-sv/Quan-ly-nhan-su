package model;

import java.time.LocalTime;
import java.util.Objects;

public class CaLamViec {
    private String maCa;

    private String tenCa;

    private LocalTime gioBatDau;

    private LocalTime gioKetThuc;

    public CaLamViec() {
    }

    public CaLamViec(String maCa, String tenCa, LocalTime gioBatDau, LocalTime gioKetThuc) {
        this.maCa = maCa;
        this.tenCa = tenCa;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
    }

    public String getMaCa() {
        return maCa;
    }

    public void setMaCa(String maCa) {
        this.maCa = maCa;
    }

    public String getTenCa() {
        return tenCa;
    }

    public void setTenCa(String tenCa) {
        this.tenCa = tenCa;
    }

    public LocalTime getGioBatDau() {
        return gioBatDau;
    }

    public void setGioBatDau(LocalTime gioBatDau) {
        this.gioBatDau = gioBatDau;
    }

    public LocalTime getGioKetThuc() {
        return gioKetThuc;
    }

    public void setGioKetThuc(LocalTime gioKetThuc) {
        this.gioKetThuc = gioKetThuc;
    }

    @Override
    public String toString() {
        String batDau = gioBatDau == null ? "?" : gioBatDau.toString();
        String ketThuc = gioKetThuc == null ? "?" : gioKetThuc.toString();
        return maCa + " - " + tenCa + " (" + batDau + " - " + ketThuc + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(maCa);
    }

    @Override
    public boolean equals(Object doiTuongKhac) {
        if (this == doiTuongKhac) {
            return true;
        }
        if (doiTuongKhac == null || getClass() != doiTuongKhac.getClass()) {
            return false;
        }
        CaLamViec caKhac = (CaLamViec) doiTuongKhac;
        return Objects.equals(maCa, caKhac.maCa);
    }
}

package model;

import java.util.Objects;

public class PhongBan {
    private String maPB;

    private String tenPB;

    private String moTa;

    public PhongBan() {
    }

    public PhongBan(String maPB, String tenPB, String moTa) {
        this.maPB = maPB;
        this.tenPB = tenPB;
        this.moTa = moTa;
    }

    public String getMaPB() {
        return maPB;
    }

    public void setMaPB(String maPB) {
        this.maPB = maPB;
    }

    public String getTenPB() {
        return tenPB;
    }

    public void setTenPB(String tenPB) {
        this.tenPB = tenPB;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    @Override
    public String toString() {
        return maPB + " - " + tenPB;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPB);
    }

    @Override
    public boolean equals(Object doiTuongKhac) {
        if (this == doiTuongKhac) {
            return true;
        }
        if (doiTuongKhac == null || getClass() != doiTuongKhac.getClass()) {
            return false;
        }
        PhongBan phongBanKhac = (PhongBan) doiTuongKhac;
        return Objects.equals(maPB, phongBanKhac.maPB);
    }
}

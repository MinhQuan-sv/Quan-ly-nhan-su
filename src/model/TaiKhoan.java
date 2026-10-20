package model;

import java.util.Objects;

public class TaiKhoan {
    public static final String QUAN_LY = "QuanLy";

    public static final String NHAN_VIEN = "NhanVien";

    public static final String HOAT_DONG = "Hoạt động";

    public static final String BI_KHOA = "Bị khóa";

    private String tenDangNhap;

    private String matKhau;

    private String vaiTro;

    private String maNV;

    private String trangThai;

    private String hoTen;

    public TaiKhoan() {
    }

    public TaiKhoan(String tenDangNhap, String vaiTro, String maNV, String trangThai, String hoTen) {
        this.tenDangNhap = tenDangNhap;
        this.vaiTro = vaiTro;
        this.maNV = maNV;
        this.trangThai = trangThai;
        this.hoTen = hoTen;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public boolean isQuanLy() {
        return QUAN_LY.equalsIgnoreCase(vaiTro);
    }

    public boolean isHoatDong() {
        return HOAT_DONG.equalsIgnoreCase(trangThai);
    }

    public String getTenVaiTro() {
        return isQuanLy() ? "Quản lý" : "Nhân viên";
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenDangNhap);
    }

    @Override
    public boolean equals(Object doiTuongKhac) {
        if (this == doiTuongKhac) {
            return true;
        }
        if (doiTuongKhac == null || getClass() != doiTuongKhac.getClass()) {
            return false;
        }
        TaiKhoan taiKhoanKhac = (TaiKhoan) doiTuongKhac;
        return Objects.equals(tenDangNhap, taiKhoanKhac.tenDangNhap);
    }

    @Override
    public String toString() {
        return "TaiKhoan{" + tenDangNhap + ", vaiTro=" + vaiTro
                + ", maNV=" + maNV + ", trangThai=" + trangThai + '}';
    }
}

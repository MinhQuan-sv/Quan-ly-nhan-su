package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class NhanVien {
    public static final String DANG_LAM = "Đang làm";

    public static final String TAM_NGHI = "Tạm nghỉ";

    public static final String DA_NGHI = "Đã nghỉ";

    public static final String[] TRANG_THAI = {DANG_LAM, TAM_NGHI, DA_NGHI};

    private String maNV;

    private String hoTen;

    private LocalDate ngaySinh;

    private String gioiTinh;

    private String cccd;

    private String soDienThoai;

    private String email;

    private String diaChi;

    private LocalDate ngayVaoLam;

    private String maPB;

    private String maCV;

    private String maQuanLy;

    private String trangThai;

    private BigDecimal luong;

    private String tenPB;

    private String tenCV;

    private String tenQuanLy;

    public NhanVien() {
    }

    public NhanVien(String maNV, String hoTen) {
        this.maNV = maNV;
        this.hoTen = hoTen;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public LocalDate getNgayVaoLam() {
        return ngayVaoLam;
    }

    public void setNgayVaoLam(LocalDate ngayVaoLam) {
        this.ngayVaoLam = ngayVaoLam;
    }

    public String getMaPB() {
        return maPB;
    }

    public void setMaPB(String maPB) {
        this.maPB = maPB;
    }

    public String getMaCV() {
        return maCV;
    }

    public void setMaCV(String maCV) {
        this.maCV = maCV;
    }

    public String getMaQuanLy() {
        return maQuanLy;
    }

    public void setMaQuanLy(String maQuanLy) {
        this.maQuanLy = maQuanLy;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public BigDecimal getLuong() {
        return luong;
    }

    public void setLuong(BigDecimal luong) {
        this.luong = luong;
    }

    public String getTenPB() {
        return tenPB;
    }

    public void setTenPB(String tenPB) {
        this.tenPB = tenPB;
    }

    public String getTenCV() {
        return tenCV;
    }

    public void setTenCV(String tenCV) {
        this.tenCV = tenCV;
    }

    public String getTenQuanLy() {
        return tenQuanLy;
    }

    public void setTenQuanLy(String tenQuanLy) {
        this.tenQuanLy = tenQuanLy;
    }

    public boolean isDangLam() {
        return DANG_LAM.equalsIgnoreCase(trangThai);
    }

    public String getTenHienThi() {
        return hoTen == null ? maNV : hoTen;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNV);
    }

    @Override
    public boolean equals(Object doiTuongKhac) {
        if (this == doiTuongKhac) {
            return true;
        }
        if (doiTuongKhac == null || getClass() != doiTuongKhac.getClass()) {
            return false;
        }
        NhanVien nhanVienKhac = (NhanVien) doiTuongKhac;
        return Objects.equals(maNV, nhanVienKhac.maNV);
    }

    @Override
    public String toString() {
        return "NhanVien{" + maNV + ", " + hoTen
                + ", maPB=" + maPB + ", maCV=" + maCV
                + ", maQuanLy=" + maQuanLy + ", trangThai=" + trangThai + '}';
    }
}

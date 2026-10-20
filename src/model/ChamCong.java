package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class ChamCong {
    private int maChamCong;

    private String maNV;

    private LocalDate ngayChamCong;

    private String maCa;

    private LocalTime gioVao;

    private LocalTime gioRa;

    private String trangThai;

    private String ghiChu;

    private String tenNV;

    private CaLamViec ca;

    public ChamCong() {
    }

    public ChamCong(String maNV, LocalDate ngayChamCong, String maCa,
                    LocalTime gioVao, LocalTime gioRa, String trangThai, String ghiChu) {
        this.maNV = maNV;
        this.ngayChamCong = ngayChamCong;
        this.maCa = maCa;
        this.gioVao = gioVao;
        this.gioRa = gioRa;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
    }

    public int getMaChamCong() {
        return maChamCong;
    }

    public void setMaChamCong(int maChamCong) {
        this.maChamCong = maChamCong;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public LocalDate getNgayChamCong() {
        return ngayChamCong;
    }

    public void setNgayChamCong(LocalDate ngayChamCong) {
        this.ngayChamCong = ngayChamCong;
    }

    public String getMaCa() {
        return maCa;
    }

    public void setMaCa(String maCa) {
        this.maCa = maCa;
    }

    public LocalTime getGioVao() {
        return gioVao;
    }

    public void setGioVao(LocalTime gioVao) {
        this.gioVao = gioVao;
    }

    public LocalTime getGioRa() {
        return gioRa;
    }

    public void setGioRa(LocalTime gioRa) {
        this.gioRa = gioRa;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getTenNV() {
        return tenNV;
    }

    public void setTenNV(String tenNV) {
        this.tenNV = tenNV;
    }

    public CaLamViec getCa() {
        return ca;
    }

    public void setCa(CaLamViec ca) {
        this.ca = ca;
    }

    public boolean isDaVao() {
        return gioVao != null;
    }

    public boolean isDaRa() {
        return gioRa != null;
    }

    public boolean isHoanTat() {
        return gioVao != null && gioRa != null;
    }

    @Override
    public String toString() {
        return "ChamCong{" + maNV + ", " + ngayChamCong
                + ", vao=" + gioVao + ", ra=" + gioRa
                + ", trangThai=" + trangThai + '}';
    }
}

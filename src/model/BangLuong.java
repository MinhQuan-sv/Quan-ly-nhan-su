package model;

import java.math.BigDecimal;

public class BangLuong {
    private String maNV;
    private String hoTen;
    private String tenPB;
    private BigDecimal luongCoBan;
    private int ngayCongChuan;
    private int ngayCongThucTe;
    private int soLanDiMuon;
    private BigDecimal luongTheoNgay;
    private BigDecimal tienPhat;
    private BigDecimal luongThucLinh;

    public BangLuong() {
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

    public String getTenPB() {
        return tenPB;
    }

    public void setTenPB(String tenPB) {
        this.tenPB = tenPB;
    }

    public BigDecimal getLuongCoBan() {
        return luongCoBan;
    }

    public void setLuongCoBan(BigDecimal luongCoBan) {
        this.luongCoBan = luongCoBan;
    }

    public int getNgayCongChuan() {
        return ngayCongChuan;
    }

    public void setNgayCongChuan(int ngayCongChuan) {
        this.ngayCongChuan = ngayCongChuan;
    }

    public int getNgayCongThucTe() {
        return ngayCongThucTe;
    }

    public void setNgayCongThucTe(int ngayCongThucTe) {
        this.ngayCongThucTe = ngayCongThucTe;
    }

    public int getSoLanDiMuon() {
        return soLanDiMuon;
    }

    public void setSoLanDiMuon(int soLanDiMuon) {
        this.soLanDiMuon = soLanDiMuon;
    }

    public BigDecimal getLuongTheoNgay() {
        return luongTheoNgay;
    }

    public void setLuongTheoNgay(BigDecimal luongTheoNgay) {
        this.luongTheoNgay = luongTheoNgay;
    }

    public BigDecimal getTienPhat() {
        return tienPhat;
    }

    public void setTienPhat(BigDecimal tienPhat) {
        this.tienPhat = tienPhat;
    }

    public BigDecimal getLuongThucLinh() {
        return luongThucLinh;
    }

    public void setLuongThucLinh(BigDecimal luongThucLinh) {
        this.luongThucLinh = luongThucLinh;
    }
}
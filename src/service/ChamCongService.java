package service;

import model.CaLamViec;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public final class ChamCongService {
    public static final int PHUT_DUNG_SAI = 5;

    public static final String DANG_LAM = "Đang làm";

    public static final String DU_CA = "Đủ ca";

    public static final String DI_MUON = "Đi muộn";

    public static final String VE_SOM = "Về sớm";

    public static final String DI_MUON_VE_SOM = "Đi muộn, về sớm";

    public static final String VANG_MAT = "Vắng mặt";

    private ChamCongService() {
    }

    public static int phutDiMuon(CaLamViec ca, LocalTime gioVao) {
        if (ca == null || ca.getGioBatDau() == null || gioVao == null) {
            return 0;
        }

        long soPhut = ChronoUnit.MINUTES.between(ca.getGioBatDau(), gioVao);

        if (soPhut <= PHUT_DUNG_SAI) {
            return 0;
        }
        return (int) soPhut;
    }

    public static int phutVeSom(CaLamViec ca, LocalTime gioRa) {
        if (ca == null || ca.getGioKetThuc() == null || gioRa == null) {
            return 0;
        }

        long soPhut = ChronoUnit.MINUTES.between(gioRa, ca.getGioKetThuc());
        if (soPhut <= PHUT_DUNG_SAI) {
            return 0;
        }
        return (int) soPhut;
    }

    public static double soGioLam(LocalTime gioVao, LocalTime gioRa) {
        if (gioVao == null || gioRa == null) {
            return 0d;
        }

        long soPhut = ChronoUnit.MINUTES.between(gioVao, gioRa);
        if (soPhut <= 0) {
            return 0d;
        }

        double soGio = soPhut / 60.0;
        return Math.round(soGio * 100.0) / 100.0;
    }

    public static String xacDinhTrangThai(CaLamViec ca, LocalTime gioVao, LocalTime gioRa) {
        if (gioVao == null) {
            return VANG_MAT;
        }

        boolean biDiMuon = phutDiMuon(ca, gioVao) > 0;

        if (gioRa == null) {
            return biDiMuon ? DI_MUON : DANG_LAM;
        }

        boolean biVeSom = phutVeSom(ca, gioRa) > 0;

        if (biDiMuon && biVeSom) {
            return DI_MUON_VE_SOM;
        }
        if (biDiMuon) {
            return DI_MUON;
        }
        return biVeSom ? VE_SOM : DU_CA;
    }

    public static String moTa(CaLamViec ca, LocalTime gioVao, LocalTime gioRa) {
        int phutMuon = phutDiMuon(ca, gioVao);
        int phutSom = phutVeSom(ca, gioRa);

        if (phutMuon > 0 && phutSom > 0) {
            return "Đi muộn " + phutMuon + " phút, về sớm " + phutSom + " phút";
        }
        if (phutMuon > 0) {
            return "Đi muộn " + phutMuon + " phút";
        }
        if (phutSom > 0) {
            return "Về sớm " + phutSom + " phút";
        }
        return "";
    }
}

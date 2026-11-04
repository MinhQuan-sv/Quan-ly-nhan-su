import util.KetNoiCSDL;

public class KiemTraKetNoi {
    public static void main(String[] args) {
        System.out.println("Thư mục làm việc : " + System.getProperty("user.dir"));

        try {
            System.out.println("Hệ quản trị     : " + KetNoiCSDL.tenHeQuanTri());
            System.out.println("Database đích   : " + KetNoiCSDL.tenDatabase());
            System.out.println("Tài khoản CSDL  : " + KetNoiCSDL.tenTaiKhoan());
        } catch (Exception loi) {
            System.out.println("Không đọc được db.properties: " + loi.getMessage());
        }

        String loi = KetNoiCSDL.kiemTraKetNoi();
        if (loi == null) {
            System.out.println("KẾT NỐI CSDL   : THÀNH CÔNG");
            System.exit(0);
        }
        System.out.println("KẾT NỐI CSDL   : THẤT BẠI");
        System.out.println();
        System.out.println(loi);
        System.exit(1);
    }
}

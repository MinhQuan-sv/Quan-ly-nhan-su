import util.MatKhau;

public class BamMatKhau {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Cách dùng: java -cp out tools/BamMatKhau.java <mat-khau>");
            System.out.println("Ví dụ    : java -cp out tools/BamMatKhau.java 123456");
            System.exit(2);
        }
        String matKhau = args[0];
        String maBam = MatKhau.bam(matKhau);
        System.out.println("Mật khẩu : " + matKhau);
        System.out.println("SHA-256  : " + maBam);
        System.out.println();
        System.out.println("Câu lệnh đặt lại mật khẩu cho 1 nhân viên:");
        System.out.println("UPDATE TAIKHOAN SET MatKhau = '" + maBam + "' WHERE MaNV = 'NV003';");
        System.out.println();
        System.out.println("Đặt lại cho TẤT CẢ tài khoản:");
        System.out.println("UPDATE TAIKHOAN SET MatKhau = '" + maBam + "';");
    }
}

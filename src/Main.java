import util.KetNoiCSDL;
import view.LoginFrame;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.awt.Font;

public class Main {
    public static void main(String[] args) {
        caiDatGiaoDien();

        String loiKetNoi = KetNoiCSDL.kiemTraKetNoi();

        if (loiKetNoi != null && loiKetNoi.startsWith("Chưa nạp được driver")) {
            JOptionPane.showMessageDialog(null, loiKetNoi,
                    "Thiếu driver JDBC", JOptionPane.ERROR_MESSAGE);
        }

        LoginFrame manHinhDangNhap = new LoginFrame(loiKetNoi);
        manHinhDangNhap.setVisible(true);
    }

    private static void caiDatGiaoDien() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception loi) {
            System.out.println("Không cài được giao diện hệ thống: " + loi.getMessage());
        }

        Font fontTiengViet = new Font("Segoe UI", Font.PLAIN, 13);
        String[] tenCacThanhPhan = {
                "Label.font", "Button.font", "TextField.font", "PasswordField.font",
                "ComboBox.font", "Table.font", "TableHeader.font", "TitledBorder.font",
                "OptionPane.messageFont", "OptionPane.buttonFont", "Menu.font",
                "MenuItem.font", "TabbedPane.font", "TextArea.font", "CheckBox.font"
        };
        for (String ten : tenCacThanhPhan) {
            UIManager.put(ten, fontTiengViet);
        }
    }
}

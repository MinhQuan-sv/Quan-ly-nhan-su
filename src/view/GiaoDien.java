package view;

import util.KetNoiCSDL;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public final class GiaoDien {
    public static final Color XANH_DAM = new Color(0x1F2A44);

    public static final Color XANH_CHINH = new Color(0x2D6CDF);

    public static final Color XANH_LA = new Color(0x0F9D58);

    public static final Color DO = new Color(0xD64545);

    public static final Color CAM = new Color(0xE08A00);

    public static final Color XAM = new Color(0x6B7280);

    public static final Color NEN = new Color(0xF4F6FB);

    public static final Color CHU = new Color(0x374151);

    public static final Color CHU_NHAT = new Color(0x6B7280);

    public static final Color VIEN = new Color(0xDFE4EC);

    public static final Font CHU_THUONG = new Font("Segoe UI", Font.PLAIN, 13);

    public static final Font CHU_DAM = new Font("Segoe UI", Font.BOLD, 13);

    public static final Font TIEU_DE = new Font("Segoe UI", Font.BOLD, 20);

    public static final Font SO_LON = new Font("Segoe UI", Font.BOLD, 17);

    private GiaoDien() {
    }

    public static JButton nut(String nhan, Color mauNen) {
        JButton nut = new JButton(nhan);
        nut.setFont(CHU_DAM);
        nut.setForeground(Color.WHITE);
        nut.setBackground(mauNen);
        nut.setOpaque(true);
        nut.setContentAreaFilled(true);
        nut.setBorderPainted(false);
        nut.setFocusPainted(false);
        nut.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nut.setBorder(new EmptyBorder(7, 14, 7, 14));
        nut.setMargin(new Insets(0, 0, 0, 0));
        return nut;
    }

    public static JLabel tieuDe(String noiDung) {
        JLabel nhan = new JLabel(noiDung);
        nhan.setFont(TIEU_DE);
        nhan.setForeground(XANH_DAM);
        return nhan;
    }

    public static JLabel nhan(String noiDung) {
        JLabel nhan = new JLabel(noiDung);
        nhan.setFont(CHU_DAM);
        nhan.setForeground(CHU);
        return nhan;
    }

    public static JLabel giaTri(String noiDung) {
        JLabel nhan = new JLabel(noiDung == null ? "" : noiDung);
        nhan.setFont(CHU_THUONG);
        nhan.setForeground(CHU);
        return nhan;
    }

    public static JPanel thanhCongCu() {
        JPanel thanh = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        thanh.setOpaque(false);
        return thanh;
    }

    public static JTextField truong(int soCot) {
        JTextField truong = new JTextField(soCot);
        truong.setFont(CHU_THUONG);
        return truong;
    }

    public static <T> JComboBox<T> combo(T[] giaTri) {
        JComboBox<T> combo = new JComboBox<>(giaTri);
        combo.setFont(CHU_THUONG);
        return combo;
    }

    public static DefaultTableModel moHinhBang(String[] tenCot) {
        return new DefaultTableModel(tenCot, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int dong, int cot) {
                return false;
            }
        };
    }

    public static JTable bang(DefaultTableModel moHinh) {
        JTable bang = new JTable(moHinh);
        bang.setFont(CHU_THUONG);
        bang.getTableHeader().setFont(CHU_DAM);
        bang.getTableHeader().setReorderingAllowed(false);
        bang.setRowHeight(26);
        bang.setGridColor(new Color(0xE3E7EF));
        bang.setShowVerticalLines(false);
        bang.setFillsViewportHeight(true);
        bang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return bang;
    }

    public static JScrollPane cuon(JTable bang) {
        JScrollPane cuon = new JScrollPane(bang);
        cuon.getViewport().setBackground(Color.WHITE);
        cuon.setBorder(BorderFactory.createLineBorder(VIEN));
        return cuon;
    }

    public static void canhPhai(JTable bang, int... chiSoCot) {
        DefaultTableCellRenderer canhPhai = new DefaultTableCellRenderer();
        canhPhai.setHorizontalAlignment(SwingConstants.RIGHT);

        for (int chiSo : chiSoCot) {
            if (chiSo >= 0 && chiSo < bang.getColumnCount()) {
                bang.getColumnModel().getColumn(chiSo).setCellRenderer(canhPhai);
            }
        }
    }

    public static JPanel the(String nhanThe, JLabel lblGiaTri, Color mauGiaTri) {
        JPanel the = new JPanel(new java.awt.BorderLayout(0, 4));
        the.setBackground(Color.WHITE);
        the.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(VIEN), new EmptyBorder(8, 12, 8, 12)));

        JLabel lblNhan = new JLabel(nhanThe);
        lblNhan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNhan.setForeground(CHU_NHAT);

        lblGiaTri.setFont(SO_LON);
        lblGiaTri.setForeground(mauGiaTri);

        the.add(lblNhan, java.awt.BorderLayout.NORTH);
        the.add(lblGiaTri, java.awt.BorderLayout.CENTER);
        return the;
    }

    public static JPanel hangThe(JComponent... the) {
        JPanel hang = new JPanel(new GridLayout(1, the.length, 10, 0));
        hang.setOpaque(false);
        for (JComponent motThe : the) {
            hang.add(motThe);
        }
        return hang;
    }

    public static void tin(Component cha, String noiDung) {
        JOptionPane.showMessageDialog(cha, noiDung, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void canhBao(Component cha, String noiDung) {
        JOptionPane.showMessageDialog(cha, noiDung, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }

    public static void loi(Component cha, String noiDung) {
        JOptionPane.showMessageDialog(cha, noiDung, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean xacNhan(Component cha, String cauHoi) {
        return JOptionPane.showConfirmDialog(cha, cauHoi, "Xác nhận",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public static void loiKetNoi(Component cha, SQLException loi) {
        loi(cha, KetNoiCSDL.moTaLoi(loi));
    }
}

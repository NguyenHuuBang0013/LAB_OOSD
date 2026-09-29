package com.quanlykhachsan.forms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FrmMain extends JFrame {
    public FrmMain() {
        UI.prepare(this, "Quản lý khách sạn", 720, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(new Color(245, 245, 245));
        root.setBorder(new EmptyBorder(25, 35, 25, 35));

        JLabel title = new JLabel("HỆ THỐNG QUẢN LÝ KHÁCH SẠN", SwingConstants.CENTER);
        title.setFont(UI.TITLE);
        title.setForeground(new Color(30, 70, 115));
        root.add(title, BorderLayout.NORTH);

        JPanel menu = new JPanel(new GridLayout(2, 3, 14, 14));
        menu.setOpaque(false);
        JButton dm = UI.button("Danh mục");
        JButton phong = UI.button("Phòng - Tiện nghi");
        JButton dat = UI.button("Đặt / Nhận phòng");
        JButton dv = UI.button("Sử dụng dịch vụ");
        JButton tra = UI.button("Trả phòng - Thanh toán");
        JButton tk = UI.button("Thống kê");
        for (JButton b : new JButton[]{dm, phong, dat, dv, tra, tk}) {
            b.setFont(new Font("Segoe UI", Font.BOLD, 14));
            b.setPreferredSize(new Dimension(195, 62));
        }
        menu.add(dm); menu.add(phong); menu.add(dat);
        menu.add(dv); menu.add(tra); menu.add(tk);
        root.add(menu, BorderLayout.CENTER);

        JPanel exitPanel = new JPanel();
        exitPanel.setOpaque(false);
        JButton thoat = UI.button("Thoát");
        thoat.setPreferredSize(new Dimension(185, 45));
        exitPanel.add(thoat);
        root.add(exitPanel, BorderLayout.SOUTH);

        add(root);

        dm.addActionListener(e -> new FrmDanhMuc().setVisible(true));
        phong.addActionListener(e -> new FrmPhongTienNghi().setVisible(true));
        dat.addActionListener(e -> new FrmDatPhong().setVisible(true));
        dv.addActionListener(e -> new FrmDichVu().setVisible(true));
        tra.addActionListener(e -> new FrmTraPhong().setVisible(true));
        tk.addActionListener(e -> new FrmThongKe().setVisible(true));
        thoat.addActionListener(e -> { if (UI.yes(this, "Bạn có thực sự muốn thoát?")) System.exit(0); });
    }
}

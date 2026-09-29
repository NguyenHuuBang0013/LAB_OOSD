package com.quanlykhachsan.forms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class UI {
    private UI() {}

    public static final Font FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font TITLE = new Font("Segoe UI", Font.BOLD, 25);

    public static void install() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        UIManager.put("Button.font", FONT);
        UIManager.put("Label.font", FONT);
        UIManager.put("TextField.font", FONT);
        UIManager.put("ComboBox.font", FONT);
        UIManager.put("Table.font", FONT);
        UIManager.put("TableHeader.font", FONT_BOLD);
        UIManager.put("TabbedPane.font", FONT_BOLD);
    }

    public static void prepare(JFrame f, String title, int w, int h) {
        f.setTitle(title);
        f.setSize(w, h);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        f.getContentPane().setBackground(new Color(245, 245, 245));
    }

    public static JPanel formPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(8, 10, 8, 10));
        return p;
    }

    public static void add(JPanel p, Component c, int row, int col, int w) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = col;
        g.gridy = row;
        g.gridwidth = w;
        g.weightx = (col % 2 == 1 || c instanceof JButton || c instanceof JComboBox || c instanceof JSpinner) ? 1.0 : 0.0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(5, 6, 5, 6);
        p.add(c, g);
    }

    public static JTextField text() {
        JTextField t = new JTextField(16);
        t.setPreferredSize(new Dimension(145, 28));
        return t;
    }

    public static JButton button(String s) {
        JButton b = new JButton(s);
        b.setFont(FONT);
        b.setPreferredSize(new Dimension(125, 30));
        return b;
    }

    public static JLabel label(String s) {
        JLabel l = new JLabel(s);
        l.setFont(FONT);
        return l;
    }

    public static JLabel heading(String s) {
        JLabel l = new JLabel(s);
        l.setFont(FONT_BOLD);
        return l;
    }

    public static JPanel whitePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(8, 8, 8, 8));
        return p;
    }

    public static JScrollPane scroll(JTable t) {
        table(t);
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createLineBorder(new Color(190, 190, 190)));
        return sp;
    }

    public static void table(JTable t) {
        t.setAutoCreateRowSorter(true);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setRowHeight(25);
        t.setFont(FONT);
        t.setGridColor(new Color(220, 220, 220));
        t.setFillsViewportHeight(true);
        JTableHeader h = t.getTableHeader();
        h.setFont(FONT_BOLD);
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(h.getPreferredSize().width, 28));
    }

    public static void styleTabs(JTabbedPane tabs) {
        tabs.setFont(FONT_BOLD);
        tabs.setBorder(new EmptyBorder(5, 8, 5, 8));
    }

    public static void dateSpinner(JSpinner s) {
        s.setEditor(new JSpinner.DateEditor(s, "dd/MM/yyyy"));
        s.setPreferredSize(new Dimension(125, 28));
    }

    public static void moneySpinner(JSpinner s) {
        s.setPreferredSize(new Dimension(130, 28));
        s.setEditor(new JSpinner.NumberEditor(s, "#,##0"));
    }

    public static String money(double value) {
        return NumberFormat.getInstance().format(value);
    }

    public static void msg(Component c, String s) {
        JOptionPane.showMessageDialog(c, s);
    }

    public static boolean yes(Component c, String s) {
        return JOptionPane.showConfirmDialog(c, s, "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}

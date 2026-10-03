package com.eshopping.forms;
import com.eshopping.service.*;
import com.eshopping.util.UiUtil;
import javax.swing.*;
import java.awt.*;
public class FrmMain extends JFrame {
    private final TaiKhoanService taiKhoanService = new TaiKhoanService();
    private final SanPhamService sanPhamService = new SanPhamService();
    private final GioHangService gioHangService = new GioHangService();
    private final ThanhToanService thanhToanService = new ThanhToanService();
    private final EmailService emailService = new EmailService();
    private final DonHangService donHangService = new DonHangService(
            sanPhamService,
            gioHangService,
            thanhToanService,
            emailService);
    private final JLabel lblUser = new JLabel("Chưa đăng nhập");
    private final JButton btnLogout = new JButton("Đăng xuất");
    public FrmMain() {
        super("e-SHOPPING - Hệ thống bán hàng online");
        buildUI();
    }
    private void buildUI() {
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel title = new JLabel("HỆ THỐNG e-SHOPPING", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        root.add(title, BorderLayout.NORTH);
        JPanel center = new JPanel(new GridLayout(2, 3, 15, 15));
        JButton btnProduct = new JButton("Sản phẩm");
        JButton btnCart = new JButton("Giỏ hàng");
        JButton btnLogin = new JButton("Đăng nhập");
        JButton btnRegister = new JButton("Đăng ký");
        for (JButton b : new JButton[] {
                btnProduct,
                btnCart,
                btnLogin,
                btnRegister,
                btnLogout
        }) {
            UiUtil.styleButton(b);
        }
        center.add(btnProduct);
        center.add(btnCart);
        center.add(btnLogin);
        center.add(btnRegister);
        center.add(btnLogout);
        root.add(center, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout());
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bottom.add(lblUser, BorderLayout.WEST);
        JButton btnExit = new JButton("Thoát");
        UiUtil.styleButton(btnExit);
        bottom.add(btnExit, BorderLayout.EAST);
        root.add(bottom, BorderLayout.SOUTH);
        // Mở sản phẩm
        btnProduct.addActionListener(e -> new FrmSanPham(
                this,
                sanPhamService,
                gioHangService,
                donHangService).setVisible(true));
        // Mở giỏ hàng
        btnCart.addActionListener(e -> new FrmGioHang(
                this,
                gioHangService,
                donHangService).setVisible(true));
        // Đăng nhập
        btnLogin.addActionListener(e -> new FrmDangNhap(
                this,
                taiKhoanService,
                this::updateUser).setVisible(true));
        // Đăng ký
        btnRegister.addActionListener(e -> new FrmDangKy(this, taiKhoanService).setVisible(true));
        // Đăng xuất có xác nhận
        btnLogout.addActionListener(e -> confirmLogout());
        // Thoát có xác nhận
        btnExit.addActionListener(e -> confirmExit());
        setContentPane(root);
        updateUser();
    }
    /**
     * Xác nhận trước khi đăng xuất.
     */
    private void confirmLogout() {
        // Chỉ cho phép đăng xuất khi đang có tài khoản đăng nhập.
        if (!Session.isLoggedIn()) {
            return;
        }
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn đăng xuất",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            Session.logout();
            updateUser();
            JOptionPane.showMessageDialog(
                    this,
                    "Đã đăng xuất thành công.",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
    /**
     * Xác nhận trước khi thoát ứng dụng.
     */
    private void confirmExit() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn thoát",
                "Xác nhận thoát",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    private void updateUser() {
        if (Session.isLoggedIn()) {
            lblUser.setText(
                    "Đang đăng nhập: "
                            + Session.getCurrentAccount().getKhachHang().getHoTen()
                            + " | "
                            + Session.getCurrentAccount().getTenDangNhap());
        } else {
            lblUser.setText("Chưa đăng nhập");
        }
        // Không cho nhấn Đăng xuất khi chưa đăng nhập.
        btnLogout.setEnabled(Session.isLoggedIn());
    }
}

//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//public class DashboardForm extends JFrame {
//    private final JLabel userLabel;
//    private final JLabel dateTimeLabel;
//    private final Timer timer;
//
//    public DashboardForm(String username) {
//        setTitle("Admin Dashboard");
//        setSize(600, 450);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);
//        setLayout(new BorderLayout());
//
//        // Panel atas untuk profil pengguna dan waktu
//        JPanel topPanel = new JPanel(new BorderLayout());
//        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//
//        userLabel = new JLabel("User: " + username);
//        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
//
//        dateTimeLabel = new JLabel();
//        dateTimeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
//
//        topPanel.add(userLabel, BorderLayout.WEST);
//        topPanel.add(dateTimeLabel, BorderLayout.EAST);
//        add(topPanel, BorderLayout.NORTH);
//
//        // Panel utama dengan tombol
//        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
//        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//
//        JButton userManagementButton = createModernButton("Manajemen Pengguna");
//
//        userManagementButton.addActionListener(e -> userManagement());
//
//        panel.add(userManagementButton);
//        add(panel, BorderLayout.CENTER);
//
//        // Panel bawah untuk tombol Logout
//        JPanel bottomPanel = new JPanel();
//        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
//
//        JButton logoutButton = createLogoutButton("Logout");
//        logoutButton.addActionListener(e -> logout());
//
//        bottomPanel.add(logoutButton);
//        add(bottomPanel, BorderLayout.SOUTH);
//
//        // Timer untuk memperbarui waktu setiap detik
//        timer = new Timer(1000, e -> updateDateTime());
//        timer.start();
//    }
//
//    DashboardForm() {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
//    }
//
////    DashboardForm(String admin) {
////        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
////    }
//
////    DashboardForm(String admin) {
////        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
////    }
//
//    private JButton createModernButton(String text) {
//        JButton button = new JButton(text) {
//            @Override
//            protected void paintComponent(Graphics g) {
//                if (!isOpaque()) {
//                    int width = getWidth();
//                    int height = getHeight();
//                    Graphics2D g2 = (Graphics2D) g.create();
//
//                    
//                    // Gradasi warna modern
//                    GradientPaint paint = new GradientPaint(0, 0, new Color(51, 153, 255),
//                            0, height, new Color(102, 0, 153));
//                    g2.setPaint(paint);
//                    g2.fillRoundRect(0, 0, width, height, 20, 20);
//                    g2.dispose();
//                }
//                super.paintComponent(g);
//            }
//        };
//
//        // Styling tombol
//        button.setFont(new Font("Arial", Font.BOLD, 14));
//        button.setForeground(Color.WHITE);
//        button.setFocusPainted(false);
//        button.setBorderPainted(false);
//        button.setContentAreaFilled(false);
//        button.setOpaque(false);
//        button.setPreferredSize(new Dimension(200, 50));
//        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
//
//        // Efek hover & klik
//        button.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseEntered(MouseEvent e) {
//                button.setForeground(Color.YELLOW);
//            }
//            @Override
//            public void mouseExited(MouseEvent e) {
//                button.setForeground(Color.WHITE);
//            }
//            @Override
//            public void mousePressed(MouseEvent e) {
//                button.setForeground(Color.BLACK);
//            }
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                button.setForeground(Color.WHITE);
//            }
//        });
//        return button;
//    }
//
//    private JButton createLogoutButton(String text) {
//        JButton button = new JButton(text) {
//            @Override
//            protected void paintComponent(Graphics g) {
//                if (!isOpaque()) {
//                    int width = getWidth();
//                    int height = getHeight();
//                    Graphics2D g2 = (Graphics2D) g.create();
//
//                    // Gradasi warna merah
//                    GradientPaint paint = new GradientPaint(0, 0, new Color(102, 0, 153),
//                            0, height, new Color(51, 204, 255));
//                    g2.setPaint(paint);
//                    g2.fillRoundRect(0, 0, width, height, 10, 10);
//                    g2.dispose();
//                }
//                super.paintComponent(g);
//            }
//        };
//
//        // Styling tombol Logout
//        button.setFont(new Font("Arial", Font.BOLD, 16));
//        button.setForeground(Color.WHITE);
//        button.setFocusPainted(false);
//        button.setBorderPainted(false);
//        button.setContentAreaFilled(false);
//        button.setOpaque(false);
//        button.setPreferredSize(new Dimension(560, 50)); // Ukuran lebih besar untuk bagian bawah
//        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
//
//        // Efek hover & klik
//        button.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseEntered(MouseEvent e) {
//                button.setForeground(Color.YELLOW);
//            }
//            @Override
//            public void mouseExited(MouseEvent e) {
//                button.setForeground(Color.WHITE);
//            }
//            @Override
//            public void mousePressed(MouseEvent e) {
//                button.setForeground(Color.BLACK);
//            }
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                button.setForeground(Color.WHITE);
//            }
//        });
//        return button;
//    }
//
//    private void updateDateTime() {
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        dateTimeLabel.setText(formatter.format(new Date()));
//    }
//
//    private void userManagement() {
//        new Dashboardcrud().setVisible(true);
//        this.dispose(); // Menutup jendela
//    }
//    
//    private void transactionManagement() {
//        new Dashboardcrud().setVisible(true);
//        this.dispose(); // Menutup jendela
//    }
//    
//    private void logout() {
//        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
//        if (confirm == JOptionPane.YES_OPTION) {
//            new LoginForm().setVisible(true);
//            dispose(); // Menutup jendela
//        }
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new DashboardForm("Admin").setVisible(true));
//    }
//}
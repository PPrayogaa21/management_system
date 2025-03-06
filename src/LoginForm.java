import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginForm extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private Connection connection;

    public LoginForm() {
        // Set frame properties
        setTitle("Login Form");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // Add username label and text field
        add(new JLabel("Username:"));
        usernameField = new JTextField(20);
        add(usernameField);

        // Add password label and text field
        add(new JLabel("Password:"));
        passwordField = new JPasswordField(20);
        add(passwordField);

        // Add login button
        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        add(loginButton);

        // Connect to database
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/management_system", "root", "");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username dan Password harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            } else {
                authenticate(username, password);
            }
        }
    }

    private void authenticate(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Login Berhasil, ambil User berdasarkan username
                    User currentUser = new User(username, resultSet.getInt("id"));
                    JOptionPane.showMessageDialog(this, "Login Berhasil!");

                    // Arahkan ke Dashboard dengan mengirimkan objek User
                    new Dashboardcrud(currentUser).setVisible(true);
                    this.dispose(); // Tutup LoginForm
                } else {
                    JOptionPane.showMessageDialog(this, "Username atau password salah!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
        });
    }
}

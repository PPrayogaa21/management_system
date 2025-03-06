/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author prayo
 */
import java.sql.*;

public class User {
    private String username;
    private int userId;

    // Konstruktor untuk User dengan username
    public User(String username, int userId) {
        this.username = username;
        this.userId = userId;
    }

    // Getter untuk username
    public String getUsername() {
        return username;
    }

    // Getter untuk userId
    public int getId() {
        return userId;
    }

    // Method untuk mendapatkan User berdasarkan username dari database
    public static User getUserByUsername(String username, Connection connection) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    return new User(username, userId);
                }
            }
        }
        return null; // Jika user tidak ditemukan
    }
}


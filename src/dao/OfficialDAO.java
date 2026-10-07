package dao;

import db.DBConnection;
import model.Official;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OfficialDAO {
    public Official authenticate(String email, String password) throws SQLException {
        String sql = "SELECT * FROM OFFICIAL WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    if (util.PasswordUtil.verifyPassword(password, storedHash)) {
                        return new Official(
                            rs.getInt("official_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            storedHash
                        );
                    }
                }
            }
        }
        return null;
    }
}

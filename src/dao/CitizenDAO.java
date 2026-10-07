package dao;

import db.DBConnection;
import model.Citizen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CitizenDAO {
    public Citizen authenticate(String email, String password) throws SQLException {
        String sql = "SELECT * FROM CITIZEN WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    if (util.PasswordUtil.verifyPassword(password, storedHash)) {
                        return new Citizen(
                            rs.getInt("citizen_id"),
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

    public void registerCitizen(Citizen citizen) throws SQLException {
        String sql = "INSERT INTO CITIZEN (name, email, password) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, citizen.getName());
            stmt.setString(2, citizen.getEmail());
            stmt.setString(3, util.PasswordUtil.hashPassword(citizen.getPassword()));
            stmt.executeUpdate();
        }
    }
}

package dao;

import db.DBConnection;
import model.Complaint;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ComplaintDAO {
    
    public int insertComplaint(Complaint complaint) throws SQLException {
        String sql = "INSERT INTO COMPLAINT (citizen_id, category, description, status) VALUES (?, ?, ?, 'Pending')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"complaint_id"})) {
            stmt.setInt(1, complaint.getCitizenId());
            stmt.setString(2, complaint.getCategory());
            stmt.setString(3, complaint.getDescription());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public List<Complaint> getComplaintsByCitizen(int citizenId) throws SQLException {
        String sql = "SELECT * FROM COMPLAINT WHERE citizen_id = ? ORDER BY complaint_date DESC";
        return fetchComplaints(sql, citizenId);
    }

    public List<Complaint> getAllComplaints() throws SQLException {
        String sql = "SELECT * FROM COMPLAINT ORDER BY complaint_date DESC";
        return fetchComplaints(sql, null);
    }

    private List<Complaint> fetchComplaints(String sql, Integer citizenId) throws SQLException {
        List<Complaint> complaints = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (citizenId != null) {
                stmt.setInt(1, citizenId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Complaint c = new Complaint();
                    c.setComplaintId(rs.getInt("complaint_id"));
                    c.setCitizenId(rs.getInt("citizen_id"));
                    int offId = rs.getInt("official_id");
                    if (!rs.wasNull()) {
                        c.setOfficialId(offId);
                    }
                    c.setCategory(rs.getString("category"));
                    c.setDescription(rs.getString("description"));
                    c.setComplaintDate(rs.getTimestamp("complaint_date"));
                    c.setStatus(rs.getString("status"));
                    c.setOfficialRemarks(rs.getString("official_remarks"));
                    c.setResolvedDate(rs.getTimestamp("resolved_date"));
                    complaints.add(c);
                }
            }
        }
        return complaints;
    }

    public void updateComplaintStatus(int complaintId, int officialId, String status, String remarks) throws SQLException {
        String sql;
        if ("Resolved".equalsIgnoreCase(status)) {
            sql = "UPDATE COMPLAINT SET official_id = ?, status = ?, official_remarks = ?, resolved_date = SYSDATE WHERE complaint_id = ?";
        } else {
            sql = "UPDATE COMPLAINT SET official_id = ?, status = ?, official_remarks = ? WHERE complaint_id = ?";
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, officialId);
            stmt.setString(2, status);
            if (remarks == null) {
                stmt.setNull(3, Types.VARCHAR);
            } else {
                stmt.setString(3, remarks);
            }
            stmt.setInt(4, complaintId);
            stmt.executeUpdate();
        }
    }

    public int countByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM COMPLAINT";
        if (status != null && !status.isEmpty()) {
            sql += " WHERE status = ?";
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (status != null && !status.isEmpty()) {
                stmt.setString(1, status);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
}

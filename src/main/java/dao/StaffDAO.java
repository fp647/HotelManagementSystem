package dao;

import model.Staff;
import util.DBUtil; 

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {

    public List<Staff> getStaffByHotelId(int hotelId) throws SQLException {
        List<Staff> staffList = new ArrayList<>();
        String SQL = "SELECT * FROM Staff WHERE hotel_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, hotelId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                staffList.add(new Staff(
                    rs.getInt("id"),
                    rs.getInt("hotel_id"),
                    rs.getString("name"),
                    rs.getString("role"),
                    rs.getString("contact_info")
                ));
            }
        }
        return staffList;
    }

    public void addStaff(Staff staff) throws SQLException {
        String SQL = "INSERT INTO Staff (hotel_id, name, role, contact_info) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, staff.getHotelId());
            pstmt.setString(2, staff.getName());
            pstmt.setString(3, staff.getRole());
            pstmt.setString(4, staff.getContactInfo());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    staff.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void updateStaff(Staff staff) throws SQLException {
        String SQL = "UPDATE Staff SET name = ?, role = ?, contact_info = ? WHERE id = ? AND hotel_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, staff.getName());
            pstmt.setString(2, staff.getRole());
            pstmt.setString(3, staff.getContactInfo());
            pstmt.setInt(4, staff.getId());
            pstmt.setInt(5, staff.getHotelId());
            pstmt.executeUpdate();
        }
    }

    public void deleteStaff(int staffId) throws SQLException {
        String SQL = "DELETE FROM Staff WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, staffId);
            pstmt.executeUpdate();
        }
    }
}
package dao;

import model.BookingService;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingServiceDAO {

    public void addBookingService(int bookingId, int serviceId, int quantity) throws SQLException {
        String sql = "INSERT INTO booking_services (booking_id, service_id, quantity) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection(); 
        	PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            stmt.setInt(2, serviceId);
            stmt.setInt(3, quantity);
            stmt.executeUpdate();
        }
    }

    public List<BookingService> getServicesByBookingId(int bookingId) throws SQLException {
        List<BookingService> services = new ArrayList<>();
        String sql = "SELECT booking_id, service_id, quantity FROM booking_services WHERE booking_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                services.add(new BookingService(
                    rs.getInt("booking_id"),
                    rs.getInt("service_id"),
                    rs.getInt("quantity")
                ));
            }
        }
        return services;
    }

    public void deleteBookingService(int bookingId, int serviceId) throws SQLException {
        String sql = "DELETE FROM booking_services WHERE booking_id = ? AND service_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            stmt.setInt(2, serviceId);
            stmt.executeUpdate();
        }
    }

    public void deleteAllServicesForBooking(int bookingId) throws SQLException {
        String sql = "DELETE FROM booking_services WHERE booking_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            stmt.executeUpdate();
        }
    }
}

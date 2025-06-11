package dao;
import model.Service;

import util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class ServiceDAO {

    public void addService(Service service) throws SQLException {
        String sql = "INSERT INTO services (hotel_id, name, price) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, service.getHotelId());
            stmt.setString(2, service.getName());
            stmt.setDouble(3, service.getPrice());
            stmt.executeUpdate();
        }
    }

    public List<Service> getServicesByHotelId(int hotelId) throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM services WHERE hotel_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hotelId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Service service = new Service();
                service.setId(rs.getInt("service_id"));
                service.setHotelId(rs.getInt("hotel_id"));
                service.setName(rs.getString("name"));
                service.setPrice(rs.getDouble("price"));
                services.add(service);
            }
        }
        return services;
    }
    
    public List<Service> getAllServices() throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM services";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Service service = new Service();
                service.setId(rs.getInt("service_id"));
                service.setHotelId(rs.getInt("hotel_id"));
                service.setName(rs.getString("name"));
                service.setPrice(rs.getDouble("price"));
                services.add(service);
            }
        }
        return services;
    }

    
    public Service getServiceById(int id) throws SQLException {
        String sql = "SELECT * FROM services WHERE service_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Service service = new Service();
                service.setId(rs.getInt("service_id"));
                service.setHotelId(rs.getInt("hotel_id"));
                service.setName(rs.getString("name"));
                service.setPrice(rs.getDouble("price"));
                return service;
            }
            return null;
        }
    }

    public void updateService(Service service) throws SQLException {
        String sql = "UPDATE services SET hotel_id = ?, name = ?, price = ? WHERE service_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, service.getHotelId());
            stmt.setString(2, service.getName());
            stmt.setDouble(3, service.getPrice());
            stmt.setInt(4, service.getId());
            stmt.executeUpdate();
        }
    }
    public void deleteService(int serviceId) throws SQLException {
        String sql = "DELETE FROM services WHERE service_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, serviceId);
            stmt.executeUpdate();
        }
    }
}

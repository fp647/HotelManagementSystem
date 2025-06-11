package dao;

import model.Hotel;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HotelDAO {

    public void addHotel(Hotel hotel) throws SQLException {
        String sql = "INSERT INTO hotels (name, address, city, country, phone, email, picture) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hotel.getName());
            stmt.setString(2, hotel.getAddress());
            stmt.setString(3, hotel.getCity());
            stmt.setString(4, hotel.getCountry());
            stmt.setString(5, hotel.getPhone());
            stmt.setString(6, hotel.getEmail());
            stmt.setString(7, hotel.getPicture());
            stmt.executeUpdate();
        }
    }

    public void updateHotel(Hotel hotel) throws SQLException {
        String sql = "UPDATE hotels SET name = ?, address = ?, city = ?, country = ?, phone = ?, email = ?, picture = ? WHERE hotel_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hotel.getName());
            stmt.setString(2, hotel.getAddress());
            stmt.setString(3, hotel.getCity());
            stmt.setString(4, hotel.getCountry());
            stmt.setString(5, hotel.getPhone());
            stmt.setString(6, hotel.getEmail());    
            stmt.setString(7, hotel.getPicture());  
            stmt.setInt(8, hotel.getId());          
            stmt.executeUpdate();
        }
    }

    public void deleteHotel(int id) throws SQLException {
        String sql = "DELETE FROM hotels WHERE hotel_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Hotel> getAllHotels() throws SQLException {
        List<Hotel> hotels = new ArrayList<>();
        String sql = "SELECT * FROM hotels";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                hotels.add(new Hotel(
                        rs.getInt("hotel_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getString("country"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("picture")
                ));
            }
        }
        return hotels;
    }

    public Hotel getHotelById(int id) throws SQLException {
        String sql = "SELECT * FROM hotels WHERE hotel_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Hotel(
                        rs.getInt("hotel_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getString("country"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("picture")
                );
            }
        }
        return null;
    }
}
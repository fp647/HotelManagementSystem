package dao;
import model.Room;
import model.RoomAvailabilityDTO;
import util.DBUtil;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class RoomDAO {
    private static final String INSERT_ROOM_SQL = "INSERT INTO rooms (hotel_id, room_number, floor, type, size, base_price, available, max_occupancy, amenities, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_ROOM_BY_ID = "SELECT * FROM rooms WHERE room_id = ?";
    private static final String SELECT_ALL_ROOMS = "SELECT * FROM rooms";
    private static final String SELECT_ROOMS_BY_HOTEL = "SELECT * FROM rooms WHERE hotel_id = ?";
    private static final String UPDATE_ROOM_SQL = "UPDATE rooms SET hotel_id = ?, room_number = ?, floor = ?, type = ?, size = ?, base_price = ?, available = ?, max_occupancy = ?, amenities = ?, image = ? WHERE room_id = ?";
    private static final String DELETE_ROOM_SQL = "DELETE FROM rooms WHERE room_id = ?";
    private static final String UPDATE_ROOM_AVAILABILITY_SQL = "UPDATE rooms SET available = ? WHERE room_id = ?";
    private static final String UPDATE_ROOM_PRICE_SQL = "UPDATE rooms SET base_price = ? WHERE room_id = ?";

    public void addRoom(Room room) {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ROOM_SQL)) {
            preparedStatement.setInt(1, room.getHotelId());
            preparedStatement.setString(2, room.getRoomNumber());
            preparedStatement.setInt(3, room.getFloor());
            preparedStatement.setString(4, room.getCategory());
            preparedStatement.setString(5, room.getSize());
            preparedStatement.setDouble(6, room.getBasePrice());
            preparedStatement.setBoolean(7, room.isAvailable());
            preparedStatement.setInt(8, room.getMaxOccupancy());
            preparedStatement.setString(9, room.getAmenities());
            preparedStatement.setString(10, room.getImage()); 

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Room getRoomById(int id) {
        Room room = null;
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ROOM_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                room = mapResultSetToRoom(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return room;
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_ROOMS)) {
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public List<Room> getRoomsByHotel(int hotelId) {
        List<Room> rooms = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ROOMS_BY_HOTEL)) {
            preparedStatement.setInt(1, hotelId);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public void updateRoom(Room room) {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ROOM_SQL)) {
            preparedStatement.setInt(1, room.getHotelId());
            preparedStatement.setString(2, room.getRoomNumber());
            preparedStatement.setInt(3, room.getFloor());
            preparedStatement.setString(4, room.getCategory());
            preparedStatement.setString(5, room.getSize());
            preparedStatement.setDouble(6, room.getBasePrice());
            preparedStatement.setBoolean(7, room.isAvailable());
            preparedStatement.setInt(8, room.getMaxOccupancy());
            preparedStatement.setString(9, room.getAmenities());
            preparedStatement.setString(10, room.getImage()); // Added image
            preparedStatement.setInt(11, room.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRoom(int id) {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ROOM_SQL)) {
            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setRoomAvailability(int id, boolean available) {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ROOM_AVAILABILITY_SQL)) {
            preparedStatement.setBoolean(1, available);
            preparedStatement.setInt(2, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateRoomPrice(int id, double newPrice) {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ROOM_PRICE_SQL)) {
            preparedStatement.setDouble(1, newPrice);
            preparedStatement.setInt(2, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Room> getAvailableRooms(int hotelId, LocalDate checkIn, LocalDate checkOut, int numberOfGuests, int numberOfRooms) throws SQLException {
        List<Room> availableRooms = new ArrayList<>();

        String sql = """
            SELECT * FROM rooms r
            WHERE r.hotel_id = ?
              AND r.room_id NOT IN (
                  SELECT room_id FROM bookings
                  WHERE (check_in_date < ? AND check_out_date > ?)
                  AND status != 'cancelled'
              )
              AND r.max_occupancy >= ?
            ORDER BY r.max_occupancy ASC
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, hotelId);
            stmt.setDate(2, Date.valueOf(checkOut));  // check_in < checkOut
            stmt.setDate(3, Date.valueOf(checkIn));   // check_out > checkIn
            stmt.setInt(4, numberOfGuests / numberOfRooms); // rough average occupancy per room

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("room_id"));
                room.setHotelId(rs.getInt("hotel_id"));
                room.setRoomNumber(rs.getString("room_number"));
                room.setFloor(rs.getInt("floor"));
                room.setCategory(rs.getString("type"));
                room.setSize(rs.getString("size"));
                room.setBasePrice(rs.getDouble("base_price"));
                room.setAvailable(rs.getBoolean("available"));
                room.setMaxOccupancy(rs.getInt("max_occupancy"));
                room.setAmenities(rs.getString("amenities"));
                room.setImage(rs.getString("image")); 
                availableRooms.add(room);
            }
        }

        return availableRooms;
    }

    public List<RoomAvailabilityDTO> getAvailableRoomCountsByCategory(int hotelId, LocalDate checkIn, LocalDate checkOut) {
        List<RoomAvailabilityDTO> result = new ArrayList<>();

        String sql = """
            SELECT r.type, COUNT(*) AS count, r.base_price, r.max_occupancy
            FROM rooms r
            WHERE r.hotel_id = ?
              AND r.room_id NOT IN (
                  SELECT br.room_id
                  FROM booking_rooms br
                  JOIN booking b ON br.booking_id = b.id
                  WHERE b.status != 'cancelled'
                    AND b.check_in_date < ?
                    AND b.check_out_date > ?
              )
            GROUP BY r.type, r.base_price, r.max_occupancy
            ORDER BY r.type
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, hotelId);
            stmt.setDate(2, Date.valueOf(checkOut)); // Exclude rooms where booking overlaps
            stmt.setDate(3, Date.valueOf(checkIn));  // with the desired stay

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                RoomAvailabilityDTO dto = new RoomAvailabilityDTO();
                dto.setCategory(rs.getString("type"));
                dto.setAvailable(rs.getInt("count"));
                dto.setBasePrice(rs.getDouble("base_price"));
                dto.setMaxOccupancy(rs.getInt("max_occupancy"));
                result.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<Integer> getAvailableRoomIdsByCategory(int hotelId, String category, LocalDate checkIn, LocalDate checkOut, int count) throws SQLException {
        List<Integer> roomIds = new ArrayList<>();

        String sql = """
            SELECT r.room_id FROM rooms r
            WHERE r.hotel_id = ?
              AND r.type = ?
              AND r.room_id NOT IN (
                  SELECT br.room_id
                  FROM booking_rooms br
                  JOIN booking b ON br.booking_id = b.id
                  WHERE b.status != 'cancelled'
                    AND b.check_in_date < ?
                    AND b.check_out_date > ?
              )
            LIMIT ?
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, hotelId);
            ps.setString(2, category);
            ps.setDate(3, Date.valueOf(checkOut)); 
            ps.setDate(4, Date.valueOf(checkIn));  
            ps.setInt(5, count);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                roomIds.add(rs.getInt("room_id"));
            }
        }

        return roomIds;
    }

    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getInt("room_id"));
        room.setHotelId(rs.getInt("hotel_id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setFloor(rs.getInt("floor"));
        room.setCategory(rs.getString("type"));
        room.setSize(rs.getString("size"));
        room.setBasePrice(rs.getDouble("base_price"));
        room.setAvailable(rs.getBoolean("available"));
        room.setMaxOccupancy(rs.getInt("max_occupancy"));
        room.setAmenities(rs.getString("amenities"));
        room.setImage(rs.getString("image")); 
        return room;
    }
}
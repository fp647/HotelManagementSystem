package dao;

import model.Booking;
import model.RoomSelection;
import util.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public int addBooking(Booking booking) throws SQLException {
        String insertBookingSQL = """
            INSERT INTO booking (customer_id, check_in_date, check_out_date, status)
            VALUES (?, ?, ?, ?)
        """;
        String insertRoomsSQL = "INSERT INTO booking_rooms (booking_id, room_id) VALUES (?, ?)";

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            int bookingId;

            try (PreparedStatement stmt = conn.prepareStatement(insertBookingSQL, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, booking.getCustomerId());
                stmt.setDate(2, Date.valueOf(booking.getCheckInDate()));
                stmt.setDate(3, Date.valueOf(booking.getCheckOutDate()));
                stmt.setString(4, booking.getStatus());

                stmt.executeUpdate();
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    bookingId = rs.getInt(1);
                } else {
                    throw new SQLException("Creating booking failed, no ID obtained.");
                }
            }

            try (PreparedStatement roomStmt = conn.prepareStatement(insertRoomsSQL)) {
                for (int roomId : booking.getRoomIds()) {
                    roomStmt.setInt(1, bookingId);
                    roomStmt.setInt(2, roomId);
                    roomStmt.addBatch();
                }
                roomStmt.executeBatch();
            }

            conn.commit();
            return bookingId;
        }
    }

    public Booking getBookingById(int bookingId) throws SQLException {
        String sql = "SELECT b.*, c.first_name, c.last_name FROM booking b JOIN customers c ON b.customer_id = c.id WHERE b.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Booking booking = new Booking();
                booking.setId(rs.getInt("id"));
                booking.setCustomerId(rs.getInt("customer_id"));
                booking.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
                booking.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
                booking.setStatus(rs.getString("status"));
                booking.setCustomerFirstName(rs.getString("first_name"));
                booking.setCustomerLastName(rs.getString("last_name"));

                // Get associated room IDs and numbers
                booking.setRoomIds(getRoomIdsForBooking(bookingId));
                booking.setRoomNumbers(getRoomNumbersForBooking(bookingId)); // Populate room numbers

                return booking;
            }

            return null;
        }
    }

    public List<Booking> getAllBookings() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, c.first_name, c.last_name FROM booking b JOIN customers c ON b.customer_id = c.id";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int bookingId = rs.getInt("id");

                Booking booking = new Booking();
                booking.setId(bookingId);
                booking.setCustomerId(rs.getInt("customer_id"));
                booking.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
                booking.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
                booking.setStatus(rs.getString("status"));
                booking.setCustomerFirstName(rs.getString("first_name"));
                booking.setCustomerLastName(rs.getString("last_name"));
                booking.setRoomIds(getRoomIdsForBooking(bookingId));
                booking.setRoomNumbers(getRoomNumbersForBooking(bookingId)); // Populate room numbers

                bookings.add(booking);
            }
        }
        return bookings;
    }

    public void updateBooking(Booking booking) throws SQLException {
        String updateBookingSQL = """
            UPDATE booking SET customer_id=?, check_in_date=?, check_out_date=?, status=? WHERE id=?
        """;
        String deleteRoomsSQL = "DELETE FROM booking_rooms WHERE booking_id = ?";
        String insertRoomsSQL = "INSERT INTO booking_rooms (booking_id, room_id) VALUES (?, ?)";

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(updateBookingSQL)) {
                stmt.setInt(1, booking.getCustomerId());
                stmt.setDate(2, Date.valueOf(booking.getCheckInDate()));
                stmt.setDate(3, Date.valueOf(booking.getCheckOutDate()));
                stmt.setString(4, booking.getStatus());
                stmt.setInt(5, booking.getId());
                stmt.executeUpdate();
            }

            try (PreparedStatement delStmt = conn.prepareStatement(deleteRoomsSQL)) {
                delStmt.setInt(1, booking.getId());
                delStmt.executeUpdate();
            }

            try (PreparedStatement insStmt = conn.prepareStatement(insertRoomsSQL)) {
                for (int roomId : booking.getRoomIds()) {
                    insStmt.setInt(1, booking.getId());
                    insStmt.setInt(2, roomId);
                    insStmt.addBatch();
                }
                insStmt.executeBatch();
            }

            conn.commit();
        }
    }

    public void deleteBooking(int bookingId) throws SQLException {
        String deleteRoomsSQL = "DELETE FROM booking_rooms WHERE booking_id = ?";
        String deleteBookingSQL = "DELETE FROM booking WHERE id = ?";

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(deleteRoomsSQL)) {
                stmt1.setInt(1, bookingId);
                stmt1.executeUpdate();
            }

            try (PreparedStatement stmt2 = conn.prepareStatement(deleteBookingSQL)) {
                stmt2.setInt(1, bookingId);
                stmt2.executeUpdate();
            }

            conn.commit();
        }
    }

    private List<Integer> getRoomIdsForBooking(int bookingId) throws SQLException {
        String sql = "SELECT room_id FROM booking_rooms WHERE booking_id = ?";
        List<Integer> roomIds = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                roomIds.add(rs.getInt("room_id"));
            }
        }

        return roomIds;
    }

    private List<Integer> getRoomNumbersForBooking(int bookingId) throws SQLException {
        String sql = "SELECT r.room_number FROM booking_rooms br JOIN rooms r ON br.room_id = r.room_id WHERE br.booking_id = ?";
        List<Integer> roomNumbers = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                roomNumbers.add(rs.getInt("room_number"));
            }
        }
        return roomNumbers;
    }


    public int createBooking(int customerId, int hotelId, LocalDate checkIn, LocalDate checkOut, String status) throws SQLException {
        String sql = """
            INSERT INTO booking (customer_id, check_in_date, check_out_date, status)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, customerId);
            stmt.setDate(2, Date.valueOf(checkIn));
            stmt.setDate(3, Date.valueOf(checkOut));
            stmt.setString(4, status);

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Failed to retrieve booking ID.");
            }
        }
    }

    public void addRoomsToBooking(int bookingId, List<RoomSelection> roomSelections) throws SQLException {
        String insertSql = "INSERT INTO booking_rooms (booking_id, room_id) VALUES (?, ?)";
        String findRoomsSql = "SELECT room_id FROM rooms WHERE type = ? AND hotel_id = (SELECT hotel_id FROM booking WHERE id = ?) AND room_id NOT IN (SELECT room_id FROM booking_rooms WHERE booking_id = ?) LIMIT ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement findRoomsStmt = conn.prepareStatement(findRoomsSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            for (RoomSelection selection : roomSelections) {
                // Find available rooms of the category for the booking's hotel
                findRoomsStmt.setString(1, selection.getCategory());
                findRoomsStmt.setInt(2, bookingId);
                findRoomsStmt.setInt(3, bookingId);
                findRoomsStmt.setInt(4, selection.getCount());

                try (ResultSet rs = findRoomsStmt.executeQuery()) {
                    int countInserted = 0;
                    while (rs.next() && countInserted < selection.getCount()) {
                        int roomId = rs.getInt("room_id");

                        insertStmt.setInt(1, bookingId);
                        insertStmt.setInt(2, roomId);
                        insertStmt.addBatch();

                        countInserted++;
                    }
                }
            }

            insertStmt.executeBatch();
        }
    }

    public List<Booking> getCurrentBookings(Integer hotelId) throws SQLException {
        List<Booking> currentBookings = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT b.*, c.first_name, c.last_name FROM booking b JOIN customers c ON b.customer_id = c.id ");
        sql.append("JOIN booking_rooms br ON b.id = br.booking_id JOIN rooms r ON br.room_id = r.room_id ");

        sql.append("WHERE ? BETWEEN b.check_in_date AND b.check_out_date");

        if (hotelId != null) {
            sql.append(" AND r.hotel_id = ?");
        }
        sql.append(" GROUP BY b.id");


        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            LocalDate today = LocalDate.now();
            int paramIndex = 1;
            stmt.setDate(paramIndex++, Date.valueOf(today));

            if (hotelId != null) {
                stmt.setInt(paramIndex++, hotelId);
            }
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int bookingId = rs.getInt("id");
                Booking booking = new Booking();
                booking.setId(bookingId);
                booking.setCustomerId(rs.getInt("customer_id"));
                booking.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
                booking.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
                booking.setStatus(rs.getString("status"));
                booking.setCustomerFirstName(rs.getString("first_name"));
                booking.setCustomerLastName(rs.getString("last_name"));
                booking.setRoomIds(getRoomIdsForBooking(bookingId));
                booking.setRoomNumbers(getRoomNumbersForBooking(bookingId)); // Populate room numbers
                currentBookings.add(booking);
            }
        }
        return currentBookings;
    }

    public List<Booking> getCurrentBookings() throws SQLException {
        return getCurrentBookings(null);
    }


    //  method without hotel filter
    public List<Booking> getBookingsByCustomerId(int customerId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, c.first_name, c.last_name FROM booking b JOIN customers c ON b.customer_id = c.id WHERE b.customer_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int bookingId = rs.getInt("id");
                Booking booking = new Booking();
                booking.setId(bookingId);
                booking.setCustomerId(rs.getInt("customer_id"));
                booking.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
                booking.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
                booking.setStatus(rs.getString("status"));
                booking.setCustomerFirstName(rs.getString("first_name"));
                booking.setCustomerLastName(rs.getString("last_name"));
                booking.setRoomIds(getRoomIdsForBooking(bookingId));
                booking.setRoomNumbers(getRoomNumbersForBooking(bookingId));
                bookings.add(booking);
            }
        }
        return bookings;
    }

    // method to get bookings by customer ID and optionally filter by hotel ID
    public List<Booking> getBookingsByCustomerId(int customerId, Integer hotelId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT b.*, c.first_name, c.last_name FROM booking b JOIN customers c ON b.customer_id = c.id ");
        sql.append("JOIN booking_rooms br ON b.id = br.booking_id JOIN rooms r ON br.room_id = r.room_id "); // Join rooms to filter by hotel
        sql.append("WHERE b.customer_id = ?");

        if (hotelId != null) {
            sql.append(" AND r.hotel_id = ?"); // Add hotel filter
        }
        sql.append(" GROUP BY b.id"); // Group to avoid duplicate bookings if a booking has multiple rooms in the same hotel

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            stmt.setInt(1, customerId);
            if (hotelId != null) {
                stmt.setInt(2, hotelId);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int bookingId = rs.getInt("id");
                Booking booking = new Booking();
                booking.setId(bookingId);
                booking.setCustomerId(rs.getInt("customer_id"));
                booking.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
                booking.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
                booking.setStatus(rs.getString("status"));
                booking.setCustomerFirstName(rs.getString("first_name"));
                booking.setCustomerLastName(rs.getString("last_name"));
                booking.setRoomIds(getRoomIdsForBooking(bookingId));
                booking.setRoomNumbers(getRoomNumbersForBooking(bookingId));
                bookings.add(booking);
            }
        }
        return bookings;
    }

    //  method without hotel filter
    public List<Booking> getBookingsByCustomerLastName(String customerLastName) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, c.first_name, c.last_name FROM booking b JOIN customers c ON b.customer_id = c.id WHERE c.last_name LIKE ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + customerLastName + "%"); 
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int bookingId = rs.getInt("id");
                Booking booking = new Booking();
                booking.setId(bookingId);
                booking.setCustomerId(rs.getInt("customer_id"));
                booking.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
                booking.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
                booking.setStatus(rs.getString("status"));
                booking.setCustomerFirstName(rs.getString("first_name"));
                booking.setCustomerLastName(rs.getString("last_name"));
                booking.setRoomIds(getRoomIdsForBooking(bookingId));
                booking.setRoomNumbers(getRoomNumbersForBooking(bookingId));
                bookings.add(booking);
            }
        }
        return bookings;
    }

    // method to get bookings by customer last name and optionally filter by hotel ID
    public List<Booking> getBookingsByCustomerLastName(String customerLastName, Integer hotelId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT b.*, c.first_name, c.last_name FROM booking b JOIN customers c ON b.customer_id = c.id ");
        sql.append("JOIN booking_rooms br ON b.id = br.booking_id JOIN rooms r ON br.room_id = r.room_id "); // Join rooms to filter by hotel
        sql.append("WHERE c.last_name LIKE ?");

        if (hotelId != null) {
            sql.append(" AND r.hotel_id = ?"); // Add hotel filter
        }
        sql.append(" GROUP BY b.id"); // Group to avoid duplicate bookings

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            stmt.setString(1, "%" + customerLastName + "%");
            if (hotelId != null) {
                stmt.setInt(2, hotelId);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int bookingId = rs.getInt("id");
                Booking booking = new Booking();
                booking.setId(bookingId);
                booking.setCustomerId(rs.getInt("customer_id"));
                booking.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
                booking.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
                booking.setStatus(rs.getString("status"));
                booking.setCustomerFirstName(rs.getString("first_name"));
                booking.setCustomerLastName(rs.getString("last_name"));
                booking.setRoomIds(getRoomIdsForBooking(bookingId));
                booking.setRoomNumbers(getRoomNumbersForBooking(bookingId));
                bookings.add(booking);
            }
        }
        return bookings;
    }

    // method to update booking status
    public void updateBookingStatus(int bookingId, String newStatus) throws SQLException {
        String sql = "UPDATE booking SET status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, bookingId);
            stmt.executeUpdate();
        }
    }
}
package dao;

import model.BillDetails;
import model.BookingDetailsForBill;
import model.CustomerForBill;
import model.ServiceItemForBill;
import util.DBUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {

    public BillDetails getBillDetailsByBookingId(int bookingId) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        CustomerForBill customer = null;
        BookingDetailsForBill bookingDetails = null;
        List<ServiceItemForBill> servicesUsed = new ArrayList<>();
        BigDecimal totalRoomCharge = BigDecimal.ZERO;
        BigDecimal totalServiceCharge = BigDecimal.ZERO;
        BigDecimal grandTotal = BigDecimal.ZERO;

        try {
            conn = DBUtil.getConnection();

            // Get Customer and Booking details (including calculated nights)

            String bookingAndCustomerSQL = "SELECT " +
                                           "b.id AS booking_id, c.id AS customer_id, c.first_name, c.last_name, c.email, c.phone_number, c.address, " +
                                           "b.check_in_date, b.check_out_date, " +
                                           "r.room_number, r.type AS room_type, r.base_price " +
                                           "FROM booking b " +
                                           "JOIN hotel.customers c ON b.customer_id = c.id " +
                                           "JOIN hotel.booking_rooms br ON b.id = br.booking_id " + // <--- MODIFICATION HERE
                                           "JOIN rooms r ON br.room_id = r.room_id " +
                                           "WHERE b.id = ?";

            ps = conn.prepareStatement(bookingAndCustomerSQL);
            ps.setInt(1, bookingId);
            rs = ps.executeQuery();

            if (rs.next()) {
                customer = new CustomerForBill(
                    rs.getInt("customer_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("phone_number"),
                    rs.getString("address")
                );

                LocalDate checkInDate = rs.getDate("check_in_date").toLocalDate();
                LocalDate checkOutDate = rs.getDate("check_out_date").toLocalDate();
                String roomNumber = rs.getString("room_number");
                String roomType = rs.getString("room_type");
                BigDecimal roomPricePerNight = rs.getBigDecimal("base_price");

                bookingDetails = new BookingDetailsForBill(
                    bookingId, checkInDate, checkOutDate, roomNumber, roomType, roomPricePerNight
                );
                totalRoomCharge = bookingDetails.getTotalRoomCharge();
            } else {
                return null; // Booking not found
            }
            DBUtil.closeQuietly(rs);
            DBUtil.closeQuietly(ps);

            // Get Service details
            String servicesSQL = "SELECT " +
                                 "s.name AS service_name, s.price AS service_price_per_unit, bs.quantity " +
                                 "FROM booking_services bs " +
                                 "JOIN services s ON bs.service_id = s.service_id " +
                                 "WHERE bs.booking_id = ?";

            ps = conn.prepareStatement(servicesSQL);
            ps.setInt(1, bookingId);
            rs = ps.executeQuery();

            while (rs.next()) {
                String serviceName = rs.getString("service_name");
                BigDecimal servicePrice = rs.getBigDecimal("service_price_per_unit");
                int quantity = rs.getInt("quantity");

                ServiceItemForBill serviceItem = new ServiceItemForBill(
                    serviceName, servicePrice, quantity
                );
                servicesUsed.add(serviceItem);
                totalServiceCharge = totalServiceCharge.add(serviceItem.getTotalServiceCost());
            }

            grandTotal = totalRoomCharge.add(totalServiceCharge);

            return new BillDetails(customer, bookingDetails, servicesUsed, grandTotal);

        } finally {
            DBUtil.closeQuietly(rs);
            DBUtil.closeQuietly(ps);
            DBUtil.closeQuietly(conn);
        }
    }
}
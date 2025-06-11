package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject; 
import com.google.gson.JsonParser; 

import dao.BookingDAO;
import model.Booking;
import model.BookingRequest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import util.LocalDateAdapter;
import java.time.LocalDate;


@WebServlet("/api/bookings")
public class BookingServlet extends HttpServlet {

    private final BookingDAO bookingDAO = new BookingDAO();
    private final Gson gson = new GsonBuilder()
    	    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
    	    .create();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            String idParam = request.getParameter("id");
            String customerIdParam = request.getParameter("customer_id");
            String customerLastNameParam = request.getParameter("customer_last_name");
            String statusParam = request.getParameter("status");
            String hotelIdParam = request.getParameter("hotel_id"); // Get hotel_id parameter

            Integer hotelId = null; // Initialize hotelId as null
            if (hotelIdParam != null && !hotelIdParam.trim().isEmpty()) {
                try {
                    hotelId = Integer.parseInt(hotelIdParam);
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"Invalid hotel_id format\"}");
                    return;
                }
            }

            if (idParam != null) {
                // Search by Booking ID  
                int id = Integer.parseInt(idParam);
                Booking booking = bookingDAO.getBookingById(id);
                if (booking != null) {
                    out.print(gson.toJson(booking));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\": \"Booking not found\"}");
                }
            } else if (customerIdParam != null) {
                // Search by Customer ID,  with optional hotel_id filter
                try {
                    int customerId = Integer.parseInt(customerIdParam);
                    List<Booking> bookings = bookingDAO.getBookingsByCustomerId(customerId, hotelId); // Pass hotelId
                    out.print(gson.toJson(bookings));
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"Invalid customer_id format\"}");
                }
            } else if (customerLastNameParam != null) {
                // Search by Customer Last Name,  with optional hotel_id filter
                List<Booking> bookings = bookingDAO.getBookingsByCustomerLastName(customerLastNameParam, hotelId); // Pass hotelId
                out.print(gson.toJson(bookings));
            } else if (statusParam != null && statusParam.equals("current")) {
                // Handle current bookings, potentially by hotel ID
                List<Booking> currentBookings = bookingDAO.getCurrentBookings(hotelId); // Pass hotelId
                out.print(gson.toJson(currentBookings));
            } else {
                // Default: Get all bookings if no specific search/filter parameter is provided
                List<Booking> bookings = bookingDAO.getAllBookings(); 
                out.print(gson.toJson(bookings));
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Failed to retrieve booking(s)\"}");
        }
        out.flush();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            BookingRequest bookingRequest = gson.fromJson(request.getReader(), BookingRequest.class);

            int bookingId = bookingDAO.createBooking(
                bookingRequest.getCustomerId(),
                bookingRequest.getHotelId(),
                bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(),
                bookingRequest.getStatus()
            );

            bookingDAO.addRoomsToBooking(bookingId, bookingRequest.getRooms());

            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print("{\"message\": \"Booking created successfully\", \"bookingId\": " + bookingId + "}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Failed to create booking\"}");
        }

        out.flush();
    }
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        BufferedReader reader = request.getReader();
        
        try {
            //Use JsonParser to parse the incoming JSON
            JsonObject jsonRequest = JsonParser.parseReader(reader).getAsJsonObject();

            if (!jsonRequest.has("id") || !jsonRequest.has("status")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Missing booking ID or status in request body\"}");
                return;
            }

            int bookingId = jsonRequest.get("id").getAsInt();
            String newStatus = jsonRequest.get("status").getAsString();

            if (!newStatus.equalsIgnoreCase("confirmed") && 
                !newStatus.equalsIgnoreCase("cancelled") &&
                !newStatus.equalsIgnoreCase("pending") &&
                !newStatus.equalsIgnoreCase("checked_in") &&
                !newStatus.equalsIgnoreCase("checked_out")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Invalid status value. Must be 'confirmed', 'cancelled', 'pending', 'check_in', or 'check_out'\"}");
                return;
            }

            bookingDAO.updateBookingStatus(bookingId, newStatus);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print("{\"message\": \"Booking status updated successfully\"}");

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Database error: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"An unexpected error occurred\"}");
        } finally {
            if (reader != null) reader.close();
            out.flush();
        }
    }


    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            String idParam = request.getParameter("id");
            if (idParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Missing booking id\"}");
                return;
            }
            int id = Integer.parseInt(idParam);
            bookingDAO.deleteBooking(id);
            out.print("{\"message\": \"Booking deleted successfully\"}");
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Internal server error\"}");
        }
        out.flush();
    }
}

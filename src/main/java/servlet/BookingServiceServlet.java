package servlet;

import com.google.gson.Gson;
import dao.BookingServiceDAO;
import model.BookingService;


import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/booking-services")
public class BookingServiceServlet extends HttpServlet {
    private final BookingServiceDAO bookingServiceDAO = new BookingServiceDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int bookingId = Integer.parseInt(req.getParameter("bookingId"));
            List<BookingService> services = bookingServiceDAO.getServicesByBookingId(bookingId);
            String json = gson.toJson(services);
            resp.setContentType("application/json");
            resp.getWriter().write(json);
        } catch (SQLException | NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Failed to fetch services\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            BookingService bs = gson.fromJson(req.getReader(), BookingService.class);
            //bookingServiceDAO.addBookingService(bs.getBookingId(), bs.getServiceId());
            bookingServiceDAO.addBookingService(bs.getBookingId(), bs.getServiceId(), bs.getQuantity());
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"message\":\"Service added to booking\"}");
        } catch (SQLException e) {
        	
            e.printStackTrace(); //  print the detailed error to server console

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to add service\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int bookingId = Integer.parseInt(req.getParameter("bookingId"));
            int serviceId = Integer.parseInt(req.getParameter("serviceId"));
            bookingServiceDAO.deleteBookingService(bookingId, serviceId);
            resp.getWriter().write("{\"message\":\"Service removed from booking\"}");
        } catch (SQLException | NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Failed to delete service\"}");
        }
    }
}

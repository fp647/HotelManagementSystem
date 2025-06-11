package servlet;

import dao.RoomDAO;
import model.Room;
import model.RoomAvailabilityDTO;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@WebServlet("/api/rooms/availability")
public class RoomAvailabilityServlet extends HttpServlet {

    private RoomDAO roomDAO = new RoomDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	String hotelIdStr = req.getParameter("hotelId");
    	String dateFromStr = req.getParameter("dateFrom");
        String dateToStr = req.getParameter("dateTo");
        String guestsStr = req.getParameter("guests");
        String roomsStr = req.getParameter("rooms");

        resp.setContentType("application/json");
        
        if (hotelIdStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Missing hotelId parameter.\"}");
            return;
        }
        
        // Basic validation
        if (dateFromStr == null || dateToStr == null || guestsStr == null || roomsStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Missing required parameters.\"}");
            return;
        }

        try {
            LocalDate dateFrom = LocalDate.parse(dateFromStr);
            LocalDate dateTo = LocalDate.parse(dateToStr);
            int guests = Integer.parseInt(guestsStr);
            int roomsRequested = Integer.parseInt(roomsStr);
            int hotelId = Integer.parseInt(hotelIdStr);

            //List<Room> availableRooms = roomDAO.getAvailableRooms(hotelId, dateFrom, dateTo, guests, roomsRequested);
            List<RoomAvailabilityDTO> availableRooms = roomDAO.getAvailableRoomCountsByCategory(hotelId, dateFrom, dateTo);
            String json = gson.toJson(availableRooms);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(json);

        } catch (DateTimeParseException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid date format. Use yyyy-MM-dd.\"}");
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Guests and rooms must be integers.\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Server error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
}

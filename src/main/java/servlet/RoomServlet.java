package servlet;

import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;
import model.Room;
import dao.RoomDAO;

@WebServlet("/api/rooms")
public class RoomServlet extends HttpServlet {
    private RoomDAO roomDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        roomDAO = new RoomDAO();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String roomIdParam = req.getParameter("id"); // Parameter for a single room by room_id
        String hotelIdParam = req.getParameter("hotelId"); // Parameter for rooms by hotel_id
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8"); // character encoding

        try {
            if (roomIdParam != null && !roomIdParam.isEmpty()) {
                // If 'id' parameter is present, fetch a single room by its ID
                int roomId = Integer.parseInt(roomIdParam);
                Room room = roomDAO.getRoomById(roomId);
                String json = gson.toJson(room);
                resp.getWriter().write(json);
            } else if (hotelIdParam != null && !hotelIdParam.isEmpty()) {
                // If 'hotelId' parameter is present, fetch rooms belonging to that hotel
                int hotelId = Integer.parseInt(hotelIdParam);
                List<Room> rooms = roomDAO.getRoomsByHotel(hotelId);
                String json = gson.toJson(rooms);
                resp.getWriter().write(json);
            } else {
                // If neither 'id' nor 'hotelId' is present, return all rooms 
                List<Room> rooms = roomDAO.getAllRooms();
                String json = gson.toJson(rooms);
                resp.getWriter().write(json);
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid ID format\"}");
            e.printStackTrace();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Failed to retrieve rooms: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            Room room = gson.fromJson(req.getReader(), Room.class);
            roomDAO.addRoom(room);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"message\": \"Room created successfully\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Failed to create room: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            Room room = gson.fromJson(req.getReader(), Room.class);
            roomDAO.updateRoom(room);
            resp.getWriter().write("{\"message\": \"Room updated successfully\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Failed to update room: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                int id = Integer.parseInt(idParam);
                roomDAO.deleteRoom(id);
                resp.getWriter().write("{\"message\": \"Room deleted successfully\"}");
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Invalid room ID format\"}");
                e.printStackTrace();
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\": \"Failed to delete room: " + e.getMessage() + "\"}");
                e.printStackTrace();
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Missing room ID\"}");
        }
    }
}
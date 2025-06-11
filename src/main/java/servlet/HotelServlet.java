package servlet;
import dao.HotelDAO;
import model.Hotel;

import com.google.gson.Gson;


import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.List;



@WebServlet("/api/hotels")
public class HotelServlet extends HttpServlet {

    private HotelDAO hotelDAO = new HotelDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Hotel hotel = new Gson().fromJson(request.getReader(), Hotel.class);
            hotelDAO.addHotel(hotel);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            new Gson().toJson(hotel, out);
            out.flush();
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Failed to create hotel");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Hotel hotel = new Gson().fromJson(request.getReader(), Hotel.class);
            hotelDAO.updateHotel(hotel);
            response.setStatus(HttpServletResponse.SC_OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Failed to update hotel");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            hotelDAO.deleteHotel(id);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Failed to delete hotel");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String idParam = request.getParameter("id");

        try {
            if (idParam != null) {
                int id = Integer.parseInt(idParam);
                Hotel hotel = hotelDAO.getHotelById(id); 
                if (hotel != null) {
                    new Gson().toJson(hotel, response.getWriter());
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Hotel not found");
                }
            } else {
                List<Hotel> hotels = hotelDAO.getAllHotels();
                new Gson().toJson(hotels, response.getWriter());
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Failed to get hotel(s)");
        }
    }
}

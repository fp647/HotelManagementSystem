package servlet;

import com.google.gson.Gson;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;


import java.io.IOException;
import java.util.List;
import java.sql.SQLException;


import dao.ServiceDAO;
import model.Service;

@WebServlet("/api/services")
public class ServiceServlet extends HttpServlet {
    private ServiceDAO serviceDAO = new ServiceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        try {
            String hotelIdParam = request.getParameter("hotelId");
            List<Service> services;
            
            if (hotelIdParam != null) {
                int hotelId = Integer.parseInt(hotelIdParam);
                services = serviceDAO.getServicesByHotelId(hotelId);
            } else {
                // optional for admin use
                services = serviceDAO.getAllServices();
            }

            String json = new Gson().toJson(services);
            response.getWriter().write(json);

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Failed to retrieve services\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Service service = new Gson().fromJson(req.getReader(), Service.class);
            serviceDAO.addService(service);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"message\":\"Service added successfully\"}");
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to add service\"}");
        }
    }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Service service = new Gson().fromJson(req.getReader(), Service.class);
            serviceDAO.updateService(service);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"message\":\"Service updated successfully\"}");
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to update service\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String serviceIdParam = req.getParameter("id");
        if (serviceIdParam == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Service ID is required\"}");
            return;
        }

        try {
            int serviceId = Integer.parseInt(serviceIdParam);
            serviceDAO.deleteService(serviceId);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"message\":\"Service deleted successfully\"}");
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid Service ID format\"}");
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to delete service\"}");
        }
    }
}


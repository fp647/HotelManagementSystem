package servlet;

import dao.BillDAO;
import model.BillDetails;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import util.LocalDateAdapter; // Import your custom adapter

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate; // Make sure LocalDate is imported for the adapter registration

@WebServlet("/api/bill")
public class BillServlet extends HttpServlet {

    private BillDAO billDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        billDAO = new BillDAO();
        
        // Register your custom LocalDateAdapter with Gson
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter()) // <--- MODIFICATION HERE
                .setPrettyPrinting()
                .create();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String bookingIdStr = request.getParameter("bookingId");
        if (bookingIdStr == null || bookingIdStr.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Missing bookingId parameter.\"}");
            return;
        }

        try {
            int bookingId = Integer.parseInt(bookingIdStr);
            BillDetails billDetails = billDAO.getBillDetailsByBookingId(bookingId);

            if (billDetails != null) {
                String jsonResponse = gson.toJson(billDetails);
                out.print(jsonResponse);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Bill details not found for bookingId: " + bookingId + "\"}");
            }

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid bookingId format. Must be an integer.\"}");
            System.err.println("NumberFormatException in BillServlet: " + e.getMessage());
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Database error while fetching bill details.\"}");
            System.err.println("SQLException in BillServlet: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"An unexpected error occurred.\"}");
            System.err.println("Unexpected error in BillServlet: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
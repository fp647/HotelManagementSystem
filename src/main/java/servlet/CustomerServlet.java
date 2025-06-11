package servlet;

import com.google.gson.Gson;
import dao.CustomerDAO;
import model.Customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.List;

@WebServlet("/api/customers")
public class CustomerServlet extends HttpServlet {
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String email = req.getParameter("email"); 
        String bookingId = req.getParameter("bookingId");

        resp.setContentType("application/json");

        if (id != null) {
            Customer customer = customerDAO.getCustomerById(Integer.parseInt(id));
            resp.getWriter().write(gson.toJson(customer));
        } else if (firstName != null) {
            List<Customer> customers = customerDAO.searchByFirstName(firstName);
            resp.getWriter().write(gson.toJson(customers));
        } else if (lastName != null) {
            List<Customer> customers = customerDAO.searchByLastName(lastName);
            resp.getWriter().write(gson.toJson(customers));
        } else if (email != null) { // email search
            List<Customer> customers = customerDAO.searchByEmail(email);
            resp.getWriter().write(gson.toJson(customers));
        }
        else if (bookingId != null) {
            Customer customer = customerDAO.searchByBookingId(Integer.parseInt(bookingId));
            resp.getWriter().write(gson.toJson(customer));
        }
        else {
            List<Customer> customers = customerDAO.getAllCustomers();
            resp.getWriter().write(gson.toJson(customers));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Customer customer = gson.fromJson(req.getReader(), Customer.class);

        try {
            int id = customerDAO.addCustomer(customer); // returns generated ID
            customer.setId(id);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(customer)); // return saved customer with ID
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"Failed to create customer\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Customer customer = gson.fromJson(req.getReader(), Customer.class);
        customerDAO.updateCustomer(customer);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        String id = req.getParameter("id");
        if (id != null) {
            customerDAO.deleteCustomer(Integer.parseInt(id));
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}

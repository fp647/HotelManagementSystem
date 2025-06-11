package model;

import java.math.BigDecimal;
import java.util.List;

public class BillDetails {
    private CustomerForBill customer;
    private BookingDetailsForBill bookingDetails;
    private List<ServiceItemForBill> servicesUsed;
    private BigDecimal grandTotal;

    // Constructor
    public BillDetails(CustomerForBill customer, BookingDetailsForBill bookingDetails, List<ServiceItemForBill> servicesUsed, BigDecimal grandTotal) {
        this.customer = customer;
        this.bookingDetails = bookingDetails;
        this.servicesUsed = servicesUsed;
        this.grandTotal = grandTotal;
    }

    // Getters
    public CustomerForBill getCustomer() { return customer; }
    public BookingDetailsForBill getBookingDetails() { return bookingDetails; }
    public List<ServiceItemForBill> getServicesUsed() { return servicesUsed; }
    public BigDecimal getGrandTotal() { return grandTotal; }

    @Override
    public String toString() {
        return "BillDetails{" +
               "customer=" + customer +
               ", bookingDetails=" + bookingDetails +
               ", servicesUsed=" + servicesUsed +
               ", grandTotal=" + grandTotal +
               '}';
    }
}
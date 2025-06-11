package model;

public class BookingService {
    private int bookingId;
    private int serviceId;
    private int quantity;
    private Service service; 

    public BookingService() {}

    public BookingService(int bookingId, int serviceId, int quantity) {
        this.bookingId = bookingId;
        this.serviceId = serviceId;
        this.quantity = quantity;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }
    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
    
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
    	this.quantity=quantity;
    }
}

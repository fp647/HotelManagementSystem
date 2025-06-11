package model;

import java.time.LocalDateTime;

public class Bill {
    private int billId;
    private int bookingId;
    private double roomCharge;
    private double serviceCharge;
    private double totalAmount;
    private LocalDateTime generatedDate;

    public Bill() {}

    public Bill(int billId, int bookingId, double roomCharge, double serviceCharge, double totalAmount, LocalDateTime generatedDate) {
        this.billId = billId;
        this.bookingId = bookingId;
        this.roomCharge = roomCharge;
        this.serviceCharge = serviceCharge;
        this.totalAmount = totalAmount;
        this.generatedDate = generatedDate;
    }

    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public double getRoomCharge() { return roomCharge; }
    public void setRoomCharge(double roomCharge) { this.roomCharge = roomCharge; }

    public double getServiceCharge() { return serviceCharge; }
    public void setServiceCharge(double serviceCharge) { this.serviceCharge = serviceCharge; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(LocalDateTime generatedDate) { this.generatedDate = generatedDate; }
}

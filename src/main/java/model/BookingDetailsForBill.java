package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BookingDetailsForBill {
    private int bookingId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private long numberOfNights;
    private String roomNumber;
    private String roomType;
    private BigDecimal roomPricePerNight;
    private BigDecimal totalRoomCharge;

    // Constructor
    public BookingDetailsForBill(int bookingId, LocalDate checkInDate, LocalDate checkOutDate, String roomNumber, String roomType, BigDecimal roomPricePerNight) {
        this.bookingId = bookingId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.roomPricePerNight = roomPricePerNight;
        this.numberOfNights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        this.totalRoomCharge = roomPricePerNight.multiply(BigDecimal.valueOf(this.numberOfNights));
    }

    // Getters
    public int getBookingId() { return bookingId; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public long getNumberOfNights() { return numberOfNights; }
    public String getRoomNumber() { return roomNumber; }
    public String getRoomType() { return roomType; }
    public BigDecimal getRoomPricePerNight() { return roomPricePerNight; }
    public BigDecimal getTotalRoomCharge() { return totalRoomCharge; }

    @Override
    public String toString() {
        return "BookingDetailsForBill{" +
               "bookingId=" + bookingId +
               ", checkInDate=" + checkInDate +
               ", checkOutDate=" + checkOutDate +
               ", numberOfNights=" + numberOfNights +
               ", roomNumber='" + roomNumber + '\'' +
               ", roomType='" + roomType + '\'' +
               ", roomPricePerNight=" + roomPricePerNight +
               ", totalRoomCharge=" + totalRoomCharge +
               '}';
    }
}
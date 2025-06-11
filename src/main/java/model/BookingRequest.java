package model;

import java.time.LocalDate;
import java.util.List;

public class BookingRequest {
    private int customerId;
    private int hotelId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status;
    public List<RoomSelection> rooms;

    // Getters and setters

    public int getCustomerId() {
        return customerId;
    }
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getHotelId() {
        return hotelId;
    }
    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }
    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }
    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public List<RoomSelection> getRooms() {
        return rooms;
    }
    public void setRooms(List<RoomSelection> rooms) {
        this.rooms = rooms;
    }
}

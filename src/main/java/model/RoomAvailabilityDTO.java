package model;

public class RoomAvailabilityDTO {
    private String category;
    private int available;
    private double basePrice;
    private int maxOccupancy;

    public RoomAvailabilityDTO() {}

    public RoomAvailabilityDTO(String category, int available, double basePrice, int maxOccupancy) {
        this.category = category;
        this.available = available;
        this.basePrice = basePrice;
        this.maxOccupancy = maxOccupancy;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public int getMaxOccupancy() {
        return maxOccupancy;
    }

    public void setMaxOccupancy(int maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }
}

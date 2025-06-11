package model;

public class Room {
    private int id;
    private int hotelId;
    private String roomNumber;
    private int floor;
    private String category;
    private String size;
    private double basePrice;
    private boolean isAvailable;
    private int maxOccupancy;
    private String amenities;
    private String image;

    public Room() {}

    public Room(int id, int hotelId, String roomNumber, int floor, String category, String size, double basePrice, boolean isAvailable, int maxOccupancy, String amenities, String image) {
        this.id = id;
        this.hotelId = hotelId;
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.category = category;
        this.size = size;
        this.basePrice = basePrice;
        this.isAvailable = isAvailable;
        this.maxOccupancy = maxOccupancy;
        this.amenities = amenities;
        this.image = image; 
    }

    // Getters and setters 
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getHotelId() { return hotelId; }
    public void setHotelId(int hotelId) { this.hotelId = hotelId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public int getMaxOccupancy() { return maxOccupancy; }
    public void setMaxOccupancy(int maxOccupancy) { this.maxOccupancy = maxOccupancy; }

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }

    public String getImage() { return image; } 
    public void setImage(String image) { this.image = image; } 


    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", hotelId=" + hotelId +
                ", roomNumber='" + roomNumber + '\'' +   
                ", floor=" + floor +
                ", category='" + category + '\'' +     
                ", size='" + size + '\'' +             
                ", basePrice=" + basePrice +
                ", isAvailable=" + isAvailable +
                ", maxOccupancy=" + maxOccupancy +
                ", amenities='" + amenities + '\'' +   
                ", image='" + image + '\'' +           
                '}';
    }
}
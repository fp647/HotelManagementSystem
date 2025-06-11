package model;

public class Staff {
    private int id;
    private int hotelId;
    private String name;
    private String role;
    private String contactInfo;

    // Constructors
    public Staff() {
    }

    public Staff(int id, int hotelId, String name, String role, String contactInfo) {
        this.id = id;
        this.hotelId = hotelId;
        this.name = name;
        this.role = role;
        this.contactInfo = contactInfo;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}
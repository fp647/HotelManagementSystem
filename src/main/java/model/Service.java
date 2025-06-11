package model;

public class Service {
    private int id;
    private int hotelId;
    private String name;
    private double price;

    public Service() {}

    public Service(int id, int hotelId, String name, double price) {
        this.id = id;
        this.hotelId = hotelId;
        this.name = name;
        this.price = price;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getHotelId() { return hotelId; }
    public void setHotelId(int hotelId) { this.hotelId = hotelId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}

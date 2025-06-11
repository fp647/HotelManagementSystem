package model;

public class RoomCount {
    private String category;
    private int count;

    public RoomCount() {}

    public RoomCount(String category, int count) {
        this.category = category;
        this.count = count;
    }

    // Getters and setters
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
}

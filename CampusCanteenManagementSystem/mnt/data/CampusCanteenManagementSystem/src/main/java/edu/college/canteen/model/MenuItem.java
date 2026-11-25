package edu.college.canteen.model;

public class MenuItem {
    private final int id;
    private final String name, category;
    private final double price;
    private final boolean available;

    public MenuItem(int id, String name, String category, double price, boolean available) {
        this.id=id; this.name=name; this.category=category; this.price=price; this.available=available;
    }
    public int getId(){return id;}
    public String getName(){return name;}
    public String getCategory(){return category;}
    public double getPrice(){return price;}
    public boolean isAvailable(){return available;}
}

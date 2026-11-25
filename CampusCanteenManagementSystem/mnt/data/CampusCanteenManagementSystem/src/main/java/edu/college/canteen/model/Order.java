package edu.college.canteen.model;

public class Order {
    private final int id, quantity;
    private final String studentName, itemName, status, createdAt;
    private final double total;
    public Order(int id, String studentName, String itemName, int quantity, double total, String status, String createdAt) {
        this.id=id; this.studentName=studentName; this.itemName=itemName; this.quantity=quantity;
        this.total=total; this.status=status; this.createdAt=createdAt;
    }
    public int getId(){return id;} public int getQuantity(){return quantity;}
    public String getStudentName(){return studentName;} public String getItemName(){return itemName;}
    public double getTotal(){return total;} public String getStatus(){return status;}
    public String getCreatedAt(){return createdAt;}
}

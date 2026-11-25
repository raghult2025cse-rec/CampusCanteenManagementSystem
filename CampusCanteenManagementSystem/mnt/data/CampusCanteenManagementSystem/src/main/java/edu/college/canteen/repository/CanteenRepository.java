package edu.college.canteen.repository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import edu.college.canteen.model.*;
import java.sql.*;
import java.util.*;

public class CanteenRepository {
    public Student registerStudent(String name, String number, String dept) throws SQLException {
        String sql="INSERT INTO students(name,student_number,department) VALUES(?,?,?)";
        try(Connection c=Database.connect(); PreparedStatement p=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            p.setString(1,name); p.setString(2,number); p.setString(3,dept); p.executeUpdate();
            try(ResultSet k=p.getGeneratedKeys()){return new Student(k.next()?k.getInt(1):findStudentId(number),name,number,dept);}
        }
    }
    private int findStudentId(String number) throws SQLException {
        try(Connection c=Database.connect(); PreparedStatement p=c.prepareStatement("SELECT id FROM students WHERE student_number=?")){
            p.setString(1,number); try(ResultSet r=p.executeQuery()){return r.next()?r.getInt(1):0;}
        }
    }
    public Student findStudent(String number) throws SQLException {
        try(Connection c=Database.connect(); PreparedStatement p=c.prepareStatement("SELECT * FROM students WHERE student_number=?")){
            p.setString(1,number); try(ResultSet r=p.executeQuery()){
                return r.next()?new Student(r.getInt("id"),r.getString("name"),r.getString("student_number"),r.getString("department")):null;
            }
        }
    }
    public List<MenuItem> getMenu(boolean availableOnly) throws SQLException {
        List<MenuItem> out=new ArrayList<>();
        String sql="SELECT * FROM menu_items "+(availableOnly?"WHERE available=1 ":"")+"ORDER BY category,name";
        try(Connection c=Database.connect(); Statement s=c.createStatement(); ResultSet r=s.executeQuery(sql)){
            while(r.next()) out.add(new MenuItem(r.getInt("id"),r.getString("name"),r.getString("category"),r.getDouble("price"),r.getInt("available")==1));
        } return out;
    }
    public void addMenuItem(String name,String category,double price) throws SQLException {
        try(Connection c=Database.connect(); PreparedStatement p=c.prepareStatement("INSERT INTO menu_items(name,category,price) VALUES(?,?,?)")){
            p.setString(1,name);p.setString(2,category);p.setDouble(3,price);p.executeUpdate();
        }
    }
    public boolean toggleAvailability(int id,boolean available) throws SQLException {
        try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement("UPDATE menu_items SET available=? WHERE id=?")){
            p.setInt(1,available?1:0);p.setInt(2,id);return p.executeUpdate()>0;
        }
    }
   public int placeOrder(int studentId, int itemId, int qty, double total) throws SQLException {

    String currentDateTime = LocalDateTime.now().format(
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    );

    String sql = "INSERT INTO orders(student_id,item_id,quantity,total,status,created_at) " +
                 "SELECT ?,id,?,?, 'PENDING',? FROM menu_items WHERE id=? AND available=1";

    try (Connection c = Database.connect();
         PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        p.setInt(1, studentId);
        p.setInt(2, qty);
        p.setDouble(3, total);
        p.setString(4, currentDateTime);
        p.setInt(5, itemId);

        if (p.executeUpdate() == 0) {
            throw new SQLException("Item is unavailable or does not exist.");
        }

        try (ResultSet k = p.getGeneratedKeys()) {
            return k.next() ? k.getInt(1) : 0;
        }
    }
}
    public List<Order> getOrders(Integer studentId) throws SQLException {
        List<Order> out=new ArrayList<>();
        String sql="SELECT o.id,s.name student_name,m.name item_name,o.quantity,o.total,o.status,o.created_at FROM orders o JOIN students s ON s.id=o.student_id JOIN menu_items m ON m.id=o.item_id "+(studentId==null?"":"WHERE o.student_id=? ")+"ORDER BY o.id DESC";
        try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement(sql)){
            if(studentId!=null)p.setInt(1,studentId);
            try(ResultSet r=p.executeQuery()){while(r.next())out.add(new Order(r.getInt("id"),r.getString("student_name"),r.getString("item_name"),r.getInt("quantity"),r.getDouble("total"),r.getString("status"),r.getString("created_at")));}
        } return out;
    }
    public boolean updateOrderStatus(int id,String status) throws SQLException {
        try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement("UPDATE orders SET status=? WHERE id=?")){
            p.setString(1,status);p.setInt(2,id);return p.executeUpdate()>0;
        }
    }
}

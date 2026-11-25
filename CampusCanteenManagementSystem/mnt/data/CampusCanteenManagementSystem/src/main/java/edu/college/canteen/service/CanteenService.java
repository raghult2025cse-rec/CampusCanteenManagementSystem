package edu.college.canteen.service;

import edu.college.canteen.model.*;
import edu.college.canteen.repository.CanteenRepository;
import java.sql.SQLException;
import java.util.List;

public class CanteenService {
    private final CanteenRepository repository;
    public CanteenService(CanteenRepository repository){this.repository=repository;}
    public Student register(String name,String number,String dept) throws SQLException {
        require(name,"Name"); require(number,"Student number"); require(dept,"Department");
        if(number.length()<4) throw new IllegalArgumentException("Student number must contain at least 4 characters.");
        return repository.registerStudent(name.trim(),number.trim(),dept.trim());
    }
    public Student signIn(String number) throws SQLException {
        require(number,"Student number"); return repository.findStudent(number.trim());
    }
    public List<MenuItem> menu(boolean availableOnly) throws SQLException{return repository.getMenu(availableOnly);}
    public int order(Student student,int itemId,int quantity) throws SQLException {
        if(student==null)throw new IllegalArgumentException("Please sign in first.");
        if(itemId<=0||quantity<=0)throw new IllegalArgumentException("Item ID and quantity must be positive.");
        MenuItem item=repository.getMenu(false).stream().filter(x->x.getId()==itemId).findFirst()
          .orElseThrow(()->new IllegalArgumentException("Food item not found."));
        if(!item.isAvailable())throw new IllegalArgumentException("This food item is currently unavailable.");
        return repository.placeOrder(student.getId(),itemId,quantity,item.getPrice()*quantity);
    }
    public void addItem(String name,String category,double price) throws SQLException {
        require(name,"Food name");require(category,"Category");
        if(price<0)throw new IllegalArgumentException("Price cannot be negative.");
        repository.addMenuItem(name.trim(),category.trim(),price);
    }
    public boolean setAvailability(int id,boolean available)throws SQLException{return repository.toggleAvailability(id,available);}
    public List<Order> orders(Integer studentId)throws SQLException{return repository.getOrders(studentId);}
    public boolean updateStatus(int id,String status)throws SQLException {
        if(!List.of("PENDING","DELIVERED","CANCELLED").contains(status))throw new IllegalArgumentException("Invalid status.");
        return repository.updateOrderStatus(id,status);
    }
    private void require(String v,String label){if(v==null||v.trim().isEmpty())throw new IllegalArgumentException(label+" is required.");}
}

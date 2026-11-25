package edu.college.canteen;

import edu.college.canteen.model.*;
import edu.college.canteen.repository.*;
import edu.college.canteen.service.CanteenService;
import java.sql.SQLException;
import java.util.*;

public class CanteenApp {
    private static final String STAFF_PIN="1234";
    private final Scanner in=new Scanner(System.in);
    private final CanteenService service=new CanteenService(new CanteenRepository());

    public static void main(String[] args) {
        try { Database.initialize(); new CanteenApp().run(); }
        catch(SQLException e){System.out.println("Database initialization failed: "+e.getMessage());}
    }
    private void run(){
        System.out.println("\n==========================================");
        System.out.println("       CAMPUS CANTEEN MANAGEMENT SYSTEM");
        System.out.println("==========================================");
        while(true){
            System.out.println("\n1. Student\n2. Canteen staff\n0. Exit");
            int choice=readInt("Choose: ");
            try{
                if(choice==1) studentEntry();
                else if(choice==2) staffEntry();
                else if(choice==0){System.out.println("Thank you. Goodbye!");return;}
                else System.out.println("Please choose 0, 1 or 2.");
            }catch(SQLException e){System.out.println("Database error: "+e.getMessage());}
             catch(IllegalArgumentException e){System.out.println("Error: "+e.getMessage());}
        }
    }
    private void studentEntry() throws SQLException {
        System.out.println("\n--- Student ---\n1. Register\n2. Sign in\n0. Back");
        int c=readInt("Choose: "); Student student=null;
        if(c==1){
            String name=readLine("Full name: "), number=readLine("Student number: "), dept=readLine("Department/course: ");
            try{student=service.register(name,number,dept);System.out.println("Registration successful. Welcome, "+student.getName()+"!");}
            catch(SQLException e){if(e.getMessage()!=null&&e.getMessage().toLowerCase().contains("unique"))System.out.println("This student number is already registered. Please sign in.");else throw e;}
        } else if(c==2){
            student=service.signIn(readLine("Student number: "));
            if(student==null){System.out.println("Student not found. Please register first.");return;}
            System.out.println("Welcome back, "+student.getName()+"!");
        } else return;
        if(student!=null) studentDashboard(student);
    }
    private void studentDashboard(Student student)throws SQLException{
        while(true){
            System.out.println("\n--- Student: "+student.getName()+" ---");
            System.out.println("1. View food menu\n2. Place an order\n3. My order history\n0. Back");
            int c=readInt("Choose: ");
            if(c==1) printMenu(service.menu(true));
            else if(c==2){
                printMenu(service.menu(true));
                int id=readInt("Enter food item ID: "), qty=readInt("Quantity: ");
                int orderId=service.order(student,id,qty);
                System.out.println("Order #"+orderId+" placed successfully. Please collect it at the canteen counter.");
            } else if(c==3) printOrders(service.orders(student.getId()));
            else if(c==0)return;
            else System.out.println("Invalid choice.");
        }
    }
    private void staffEntry()throws SQLException{
        String pin=readLine("Staff PIN: ");
        if(!STAFF_PIN.equals(pin)){System.out.println("Access denied.");return;}
        Staff staff=new Staff("Canteen Staff");
        System.out.println("Welcome, "+staff.getRole()+".");
        while(true){
            System.out.println("\n--- Staff Dashboard ---");
            System.out.println("1. View full menu\n2. Add menu item\n3. Change item availability\n4. View all orders\n5. Update order status\n0. Back");
            int c=readInt("Choose: ");
            if(c==1)printMenu(service.menu(false));
            else if(c==2){
                String name=readLine("Food name: "),cat=readLine("Category: ");double price=readDouble("Price (Rs.): ");
                service.addItem(name,cat,price);System.out.println("Food item added successfully.");
            } else if(c==3){
                printMenu(service.menu(false));int id=readInt("Item ID: ");
                int a=readInt("Availability (1 = Available, 0 = Unavailable): ");
                if(a!=0&&a!=1)System.out.println("Enter only 1 or 0.");
                else System.out.println(service.setAvailability(id,a==1)?"Availability updated.":"Item ID not found.");
            } else if(c==4)printOrders(service.orders(null));
            else if(c==5){
                printOrders(service.orders(null));int id=readInt("Order ID to update: ");
                System.out.println("1. Pending\n2. Delivered\n3. Cancelled");
                int s=readInt("Choose new status: ");
                String status=s==1?"PENDING":s==2?"DELIVERED":s==3?"CANCELLED":null;
                if(status==null)System.out.println("Invalid status choice.");
                else System.out.println(service.updateStatus(id,status)?"Order status updated.":"Order ID not found.");
            } else if(c==0)return;
            else System.out.println("Invalid choice.");
        }
    }
    private void printMenu(List<MenuItem> items){
        System.out.println("\nID  FOOD ITEM                 CATEGORY          PRICE     STATUS");
        System.out.println("----------------------------------------------------------------");
        if(items.isEmpty()){System.out.println("No menu items found.");return;}
        for(MenuItem m:items)System.out.printf("%-3d %-25s %-17s Rs.%-7.2f %s%n",
          m.getId(),m.getName(),m.getCategory(),m.getPrice(),m.isAvailable()?"Available":"Unavailable");
    }
    private void printOrders(List<Order> orders){
        System.out.println("\nORDER  STUDENT              ITEM                 QTY   TOTAL      STATUS       TIME");
        System.out.println("------------------------------------------------------------------------------------");
        if(orders.isEmpty()){System.out.println("No orders found.");return;}
        for(Order o:orders)System.out.printf("#%-5d %-20s %-20s %-5d Rs.%-8.2f %-12s %s%n",
          o.getId(),o.getStudentName(),o.getItemName(),o.getQuantity(),o.getTotal(),o.getStatus(),o.getCreatedAt());
    }
    private String readLine(String prompt){System.out.print(prompt);return in.nextLine().trim();}
    private int readInt(String prompt){
        while(true){try{return Integer.parseInt(readLine(prompt));}catch(NumberFormatException e){System.out.println("Please enter a valid whole number.");}}
    }
    private double readDouble(String prompt){
        while(true){try{return Double.parseDouble(readLine(prompt));}catch(NumberFormatException e){System.out.println("Please enter a valid amount.");}}
    }
}

package edu.college.canteen.model;

public class Staff extends User {
    public Staff(String name) { super(0, name); }
    @Override public String getRole() { return "CANTEEN STAFF"; }
}

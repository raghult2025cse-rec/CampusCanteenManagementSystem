package edu.college.canteen.model;

public class Student extends User {
    private final String studentNumber;
    private final String department;

    public Student(int id, String name, String studentNumber, String department) {
        super(id, name);
        this.studentNumber = studentNumber;
        this.department = department;
    }
    public String getStudentNumber() { return studentNumber; }
    public String getDepartment() { return department; }
    @Override public String getRole() { return "STUDENT"; }
}

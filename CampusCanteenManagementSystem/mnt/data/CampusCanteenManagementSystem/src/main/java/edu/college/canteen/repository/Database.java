package edu.college.canteen.repository;

import java.sql.*;

public final class Database {
    private static final String URL = "jdbc:sqlite:canteen.db";
    private Database() {}
    public static Connection connect() throws SQLException {
        Connection c = DriverManager.getConnection(URL);
        try (Statement s=c.createStatement()) { s.execute("PRAGMA foreign_keys = ON"); }
        return c;
    }
    public static void initialize() throws SQLException {
        try (Connection c=connect(); Statement s=c.createStatement()) {
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS students(
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  name TEXT NOT NULL,
                  student_number TEXT NOT NULL UNIQUE,
                  department TEXT NOT NULL
                )""");
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS menu_items(
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  name TEXT NOT NULL UNIQUE,
                  category TEXT NOT NULL,
                  price REAL NOT NULL CHECK(price >= 0),
                  available INTEGER NOT NULL DEFAULT 1 CHECK(available IN (0,1))
                )""");
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS orders(
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  student_id INTEGER NOT NULL REFERENCES students(id),
                  item_id INTEGER NOT NULL REFERENCES menu_items(id),
                  quantity INTEGER NOT NULL CHECK(quantity > 0),
                  total REAL NOT NULL CHECK(total >= 0),
                  status TEXT NOT NULL DEFAULT 'PENDING' CHECK(status IN ('PENDING','DELIVERED','CANCELLED')),
                  created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
                )""");
            s.executeUpdate("CREATE INDEX IF NOT EXISTS idx_orders_student ON orders(student_id)");
            try (ResultSet rs=s.executeQuery("SELECT COUNT(*) FROM menu_items")) {
                if (rs.next() && rs.getInt(1)==0) {
                    String[][] seed={{"Fresh Lime","Beverages","30"},{"Tea","Beverages","15"},
                      {"Masala Dosa","Breakfast","65"},{"Veg Thali","Meals","100"},
                      {"Paalada","Snacks","35"},{"Veg Sandwich","Snacks","45"},
                      {"Biryani","Lunch","120"},{"Tomato Rice","Lunch","60"},
                      {"Non Veg Thali","Meals","999"}};
                    try (PreparedStatement p=c.prepareStatement(
                      "INSERT INTO menu_items(name,category,price,available) VALUES(?,?,?,1)")) {
                        for(String[] row:seed){p.setString(1,row[0]);p.setString(2,row[1]);p.setDouble(3,Double.parseDouble(row[2]));p.addBatch();}
                        p.executeBatch();
                    }
                }
            }
        }
    }
}

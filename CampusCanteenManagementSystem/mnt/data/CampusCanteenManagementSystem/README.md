# Campus Canteen Management System

Java console application matching the submitted project report: Java, JDBC, SQLite and Maven.

## Requirements
- JDK 15 or later
- Maven 3.9+ (or use the included `run.bat` after Maven is installed)

## Run in VS Code / Windows
1. Extract the ZIP.
2. Open the extracted `CampusCanteenManagementSystem` folder in VS Code.
3. Open Terminal → New Terminal.
4. Run `mvn clean compile exec:java`.

Windows alternative: double-click `run.bat`.

The SQLite database `canteen.db` is created automatically in the project folder on first run. Do not delete it if you want to keep saved records.

## Demo staff PIN
`1234` (classroom demonstration only; change `STAFF_PIN` in `CanteenApp.java` if required).

## Features
- Student registration and student-number sign-in
- Available food menu, single-item ordering and order history
- Staff dashboard: full menu, add item, toggle availability, view orders, update status
- SQLite persistence, prepared statements, validation and foreign keys

## Java/OOP concepts
- Encapsulation: private fields and getters
- Abstraction: abstract `User` class
- Inheritance: `Student` and `Staff` extend `User`
- Runtime polymorphism / overriding: `getRole()`
- Exception handling, collections, JDBC and CRUD

This is an academic demonstration system, not production authentication or payment software.

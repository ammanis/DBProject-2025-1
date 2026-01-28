# Camping Car Rental System
The **Camping Car Rental System** is a Java-based desktop application designed to manage the operations of a camper van rental business.
It provides role-based access for administrators and members, enabling efficient management of vehicles, rentals, maintenance, employees, and customers through a MySQL database.

The system is built using Java Swing for the user interface and MySQL for persistent data storage.

## Features
The main features are:
* Role-based login system (Admin / Member)
* GUI-based desktop application (Java Swing)
* Full CRUD operations for major entities
* Maintenance tracking (internal / external)
* Rental status management
* Database initialization and reset feature
* Structured relational database design
* Error handling for invalid operations

## System Functionality
### Authentication & User Roles
* Secure login system with role-based access
* Two user roles:
  * Admin: Full system control
  * Member: Rental and viewing privileges
* Login validation using database-stored credentials

### Admin Functions
Admins can manage all core system data, including:
* Database Initialization
  * Create and reset all database tables
  * Insert preset sample data
* User Management
  * View registered members
* Camper Van Management
  * Add, update, delete camper van records
  * View camper van availability and rental status
* Rental Management
  * View rental history
  * Track ongoing and completed rentals
* Employee & Garage Management
  * Manage employee information
  * Manage repair garages
* Maintenance Records
  * Record internal and external maintenance
  * Track maintenance cost and responsible employees
* Parts & Supplier Management
  * Manage spare parts
  * Track part usage and suppliers

### Member Functions
Members have limited access focused on rental usage:
* Camper Van Browsing
  * View available camper vans
  * Check capacity, fuel type, and daily cost
* Rental Operations
  * Rent available camper vans
  * View personal rental history
* Profile Information
  * View personal account details

### Database Features
* Relational database design with:
  * Primary keys and foreign keys
  * Referential integrity
* Tables include:
  * Users, Customers, Companies
  * CamperVans, Rentals
  * Employees, Garages
  * Maintenance, Parts, Suppliers
* Built with InnoDB engine for transaction support

## Preview (As Admin)
### Login Page
<img width="500" height="500" alt="image" src="https://github.com/user-attachments/assets/14eb83e2-4d4b-414c-ba99-39d743a618d6" />

### View Table (shows all preset database)
<img width="500" height="500" alt="image" src="https://github.com/user-attachments/assets/fca3357a-83bf-48b1-87a6-95ebf1ebc898" />

### Initialized DB, and its result

<img width="500" height="500" alt="image" src="https://github.com/user-attachments/assets/6adf279a-8897-49df-b2ce-5ff453eb2a2d" />
<img width="500" height="500" alt="image" src="https://github.com/user-attachments/assets/5da3c17e-468f-409d-8f75-0bf1030a100a" />

### Insert new DB

<img width="500" height="500" alt="image" src="https://github.com/user-attachments/assets/6c836d27-4bba-49e5-ba68-3872c6240948" />
<img width="500" height="500" alt="image" src="https://github.com/user-attachments/assets/046a49da-0bd4-4641-bbe2-e66ea3b4e2ee" />

### Edit existing DB

<img width="500" height="500" alt="image" src="https://github.com/user-attachments/assets/1441760c-9b14-4c99-a372-3336bdf059a5" />
<img width="500" height="500" alt="image" src="https://github.com/user-attachments/assets/1be183e5-39b9-4bf8-8487-2bd8d7885aa5" />

### Delete existing DB

<img width="500" height="500" alt="image" src="https://github.com/user-attachments/assets/89d71424-eb9e-49f6-a5e1-2a574247a4e9" />
<img width="500" height="500" alt="image" src="https://github.com/user-attachments/assets/db775b96-f0b2-4e00-82e4-474cf467af32" />

## Preview (As Users)
<img width="500" height="500" alt="image" src="https://github.com/user-attachments/assets/e893b801-d994-4389-8ac4-c79ff5251a4c" />
<img width="500" height="500" alt="image" src="https://github.com/user-attachments/assets/a1200388-c811-4f1a-8145-e75c7e2ddb7a" />
<img width="500" height="500" alt="image" src="https://github.com/user-attachments/assets/f3aa22c1-36ec-4ade-8668-188c588e4d8e" />


## Technologies Used
**Language**: Java\
**GUI**: Java Swing\
**Database**: MySQL\
**JDBC**: MySQL Connector/J\
**IDE**: Eclipse

## How to Run

1. Start MySQL server
2. Create database:
```
CREATE DATABASE newmadangdb;
```
3. Update DB credentials in DBConnection.java
4. Run Main.java
5. Login as Admin
6. Click Initialize DB
7. Login as Member after registering user as admin

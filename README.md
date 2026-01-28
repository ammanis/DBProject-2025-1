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
🔐 Authentication & User Roles

* Secure login system with role-based access
* Two user roles:
  * Admin: Full system control
  * Member: Rental and viewing privileges
* Login validation using database-stored credentials

👤 Admin Functions
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

🙋 Member Functions
Members have limited access focused on rental usage:
* Camper Van Browsing
  * View available camper vans
  * Check capacity, fuel type, and daily cost
* Rental Operations
  * Rent available camper vans
  * View personal rental history
* Profile Information
  * View personal account details

🗄️ Database Features
* Relational database design with:
  * Primary keys and foreign keys
  * Referential integrity
* Tables include:
  * Users, Customers, Companies
  * CamperVans, Rentals
  * Employees, Garages
  * Maintenance, Parts, Suppliers
* Built with InnoDB engine for transaction support

## Preview

## System Architecture
<img width="984" height="735" alt="image" src="https://github.com/user-attachments/assets/fca3357a-83bf-48b1-87a6-95ebf1ebc898" />

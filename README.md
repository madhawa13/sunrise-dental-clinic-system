# Sunrise Dental Clinic System

A Java web application developed for the CIS6003 Advanced Programming assignment.

The system computerizes patient registration, appointment scheduling, treatment records, treatment charges, billing, payments, reporting and staff authentication at Sunrise Dental Clinic.

## Project Information

- Module: CIS6003 Advanced Programming
- Application: Sunrise Dental Clinic System
- Architecture: Three-tier MVC architecture
- Build type: Maven WAR
- Server: Apache Tomcat 10.1
- Java version: Java 21 LTS
- Database: MySQL 8
- Development method: Test-Driven Development

## Important Restriction

This project does not use Spring Boot.

The web application is implemented using Jakarta Servlets, JSP, JSTL, JDBC and Maven.

## Main Features

### Authentication and Security

- Secure staff login
- PBKDF2-HMAC-SHA256 password hashing
- Receptionist and Dentist roles
- Session-based authentication
- Automatic session timeout
- Logout and session invalidation
- Authentication filter
- Role-based authorization filter
- Protected pages cannot be accessed after logout
- Browser cache prevention for protected pages

### Patient Management

- Register patients
- Generate unique patient numbers
- Display patient records
- Search using patient number, name, NIC or phone
- Update patient information
- Soft-delete or deactivate patient records
- Input validation

### Appointment Management

- Generate unique appointment numbers
- Schedule appointments
- Assign patients and dentists
- Search appointments
- Edit appointments
- Cancel appointments
- Update appointment status
- Prevent dentist double-booking
- Validate appointment dates and times

### Treatment Management

- Record diagnoses
- Record treatment notes
- Record prescriptions
- Connect treatments with appointments
- Assign standard treatment charges
- Calculate treatment totals
- Update and delete treatment records

### Billing and Payments

- Calculate bill subtotal from treatment charges
- Apply discounts
- Generate unique bill numbers
- Prevent duplicate bills
- Record cash, card and bank-transfer payments
- Support partial payments
- Automatically update payment status
- Calculate outstanding balances
- Produce printable patient bills

### Reports

- Appointment and billing summary
- Date-based report filtering
- Total billed amount
- Total paid amount
- Outstanding balance
- Printable report layout
- MySQL reporting view

### Help

- Staff login and logout guidance
- Patient-management instructions
- Appointment-management instructions
- Dentist treatment guidance
- Receptionist billing guidance
- Security guidance

### REST-Style JSON Web Service

The application provides an appointment JSON API.

```text
GET /api/appointments
GET /api/appointments?id=4
GET /api/appointments?search=Dental
```

The API returns JSON data and uses appropriate HTTP status codes:

- `200 OK`
- `400 Bad Request`
- `404 Not Found`
- `500 Internal Server Error`

An authenticated application session is required to access the API.

## User Roles

| Function | Receptionist | Dentist |
|---|---:|---:|
| Dashboard | Yes | Yes |
| Help | Yes | Yes |
| View patients | Yes | Yes |
| Register/update patients | Yes | No |
| View appointments | Yes | Yes |
| Schedule appointments | Yes | No |
| Treatment management | No | Yes |
| Treatment charges | No | Yes |
| Billing and payments | Yes | No |
| Financial reports | Yes | No |

## Technologies

- Java SE 21
- Jakarta Servlet API
- JSP
- JSTL
- JDBC
- MySQL 8
- Apache Tomcat 10.1
- Maven
- JUnit 5
- Mockito
- H2 in-memory database
- Git
- GitHub
- GitHub Actions
- Draw.io

## Architecture

The application follows a three-tier architecture.

```text
Presentation Layer
JSP pages, Controllers and Filters

Business Layer
Service interfaces and implementations

Data Access Layer
DAO interfaces, JDBC implementations and MySQL
```

Dependency injection constructors are included to support isolated automated testing.

## Design Patterns

The project demonstrates the following patterns:

- Model-View-Controller
- Data Access Object
- Service Layer
- Dependency Injection
- Front Controller using Servlets
- Filter pattern for authentication and authorization
- Singleton-style database connection configuration

## Project Structure

```text
sunrise-dental-clinic-system
├── database
│   ├── sunrise_dental_clinic.sql
│   └── advanced_database_objects.sql
├── docs
│   └── diagrams
├── src
│   ├── main
│   │   ├── java
│   │   │   └── lk.icbt.dental
│   │   │       ├── controller
│   │   │       ├── dao
│   │   │       ├── dao.impl
│   │   │       ├── exception
│   │   │       ├── filter
│   │   │       ├── model
│   │   │       ├── service
│   │   │       └── util
│   │   ├── resources
│   │   └── webapp
│   │       ├── assets
│   │       │   └── css
│   │       ├── WEB-INF
│   │       │   └── views
│   │       └── index.jsp
│   └── test
│       ├── java
│       └── resources
├── pom.xml
└── README.md
```

## Database Setup

### 1. Create the main database

Open MySQL Workbench and execute:

```text
database/sunrise_dental_clinic.sql
```

This creates:

- users
- patients
- appointments
- treatment_charges
- treatments
- treatment_details
- bills
- payments

It also inserts the demonstration staff accounts and standard treatment charges.

### 2. Create advanced database objects

Execute:

```text
database/advanced_database_objects.sql
```

This creates:

- `fn_calculate_bill_balance`
- `sp_daily_appointment_report`
- `trg_payment_update_bill_status`
- `vw_appointment_billing_summary`

## Local Database Configuration

Copy:

```text
src/main/resources/db.properties.example
```

as:

```text
src/main/resources/db.properties
```

Update it with the local MySQL credentials:

```properties
db.url=jdbc:mysql://localhost:3306/sunrise_dental_clinic?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Colombo
db.username=root
db.password=YOUR_MYSQL_PASSWORD
db.driver=com.mysql.cj.jdbc.Driver
```

The real `db.properties` file is excluded from Git to protect database credentials.

## Demonstration Accounts

### Receptionist

```text
Username: reception01
Password: Reception@123
```

### Dentist 1

```text
Username: dentist.amara
Password: Dentist@123
```

### Dentist 2

```text
Username: dentist.kasun
Password: Dentist@456
```

These passwords are stored in MySQL as PBKDF2 hashes and not as plain text.

## Running the Project in Eclipse

1. Install Java 21.
2. Install Eclipse IDE for Enterprise Java and Web Developers.
3. Install or extract Apache Tomcat 10.1.
4. Import the project as an Existing Maven Project.
5. Configure JavaSE-21.
6. Add Apache Tomcat 10.1 as the Targeted Runtime.
7. Create `db.properties`.
8. Run both database scripts.
9. Add the project to Tomcat.
10. Start the server.
11. Open:

```text
http://localhost:8080/sunrise-dental-clinic-system/login
```

## Automated Testing

The project uses TDD for DAO, Service and Controller layers.

### Frameworks

- JUnit 5
- Mockito
- H2 database

### TDD cycle

```text
RED
Write a failing automated test.

GREEN
Implement the minimum correct production code.

REFACTOR
Improve the code while retaining green tests.
```

### Run all tests

```powershell
mvn clean test
```

Expected:

```text
Failures: 0
Errors: 0
BUILD SUCCESS
```

## Build the WAR File

Run:

```powershell
mvn clean package
```

The deployable file is generated in:

```text
target/sunrise-dental-clinic-system.war
```

The WAR file can be deployed to Apache Tomcat 10.1.

## GitHub Actions CI/CD

Every push to the `main` branch automatically:

1. Checks out the source code.
2. Configures Java 21.
3. Runs all automated tests.
4. Builds the Maven WAR package.
5. Uploads the WAR as a workflow artifact.

The WAR artifact is available from the completed GitHub Actions workflow run.

## Application URLs

```text
/login
/patients
/appointments
/treatments
/treatment-details
/bills
/payments
/reports
/help
/api/appointments
/logout
```

The complete URL begins with:

```text
http://localhost:8080/sunrise-dental-clinic-system
```

## Advanced Database Features

### Function

```text
fn_calculate_bill_balance
```

Calculates the outstanding balance using bill and payment information.

### Stored Procedure

```text
sp_daily_appointment_report
```

Produces appointment information for a selected date.

### Trigger

```text
trg_payment_update_bill_status
```

Automatically updates a bill to `UNPAID`, `PARTIALLY_PAID` or `PAID` after a payment is inserted.

### Report View

```text
vw_appointment_billing_summary
```

Combines appointment, patient, dentist, bill and payment information.

## Security Notes

- Database credentials are not committed.
- Passwords are stored as secure hashes.
- Prepared statements are used for database operations.
- JSP output uses JSTL escaping.
- Sessions are invalidated during logout.
- Role-based access is enforced by Servlet filters.
- Protected responses include no-cache headers.
- JSON responses include `X-Content-Type-Options: nosniff`.
- Internal database errors are not exposed to users.

## GitHub Repository

```text
https://github.com/madhawa13/sunrise-dental-clinic-system
```

## Author

Developed for the CIS6003 Advanced Programming assessment.

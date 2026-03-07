# HRMS (Spring Boot + Angular + SQLite)

This project is a simple HR management system built with a Java Spring Boot backend, Angular frontend, and SQLite as the database. It supports core HR flows: employees, departments, leave requests, attendance tracking, and basic payroll.

### Backend (Spring Boot)

- **Location**: `backend`
- **Stack**: Spring Boot 3, Spring Web, Spring Data JPA, Spring Security, SQLite JDBC, Lombok.
- **Main features**:
  - REST APIs under `/api/v1/**` for employees, departments, leave, attendance, and payroll.
  - SQLite database stored in `hrms.db` in the backend working directory.
  - Simple HTTP Basic authentication with in-memory users:
    - `admin` / `admin123` (ROLE_ADMIN)
    - `hr` / `hr123` (ROLE_HR)
    - `employee` / `emp123` (ROLE_EMPLOYEE)
  - Payroll endpoints require `ADMIN` or `HR` role; other API endpoints require authentication.

#### Running the backend

1. Install Java 17+ and Maven.
2. From the `backend` folder:

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080/api/v1`.

### Frontend (Angular)

- **Location**: `frontend`
- **Stack**: Angular, Angular Material, RxJS.
- **Main features**:
  - Login screen using HTTP Basic credentials (same as backend users).
  - Modules/pages for Employees, Departments, Leave, Attendance, and Payroll.
  - Responsive layout with a persistent side navigation.

#### Running the frontend

1. Install Node.js and the Angular CLI.
2. From the `frontend` folder:

```bash
npm install
npx ng serve
```

The app will run at `http://localhost:4200` and talk to the backend at `http://localhost:8080/api/v1`.

### Notes

- To reset data, stop the backend, delete the `hrms.db` file in the `backend` directory, and restart `spring-boot:run`.
- The project includes a few basic service unit tests in `backend/src/test/java/com/example/hrms/service`.


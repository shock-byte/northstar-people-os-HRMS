package com.example.hrms.config;

import com.example.hrms.model.AttendanceRecord;
import com.example.hrms.model.Department;
import com.example.hrms.model.Employee;
import com.example.hrms.model.LeaveRequest;
import com.example.hrms.model.PayrollRecord;
import com.example.hrms.repository.AttendanceRecordRepository;
import com.example.hrms.repository.DepartmentRepository;
import com.example.hrms.repository.EmployeeRepository;
import com.example.hrms.repository.LeaveRequestRepository;
import com.example.hrms.repository.PayrollRecordRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
public class DemoDataInitializer {

    @Bean
    CommandLineRunner seedDemoData(@Value("${app.demo.seed.enabled:true}") boolean seedEnabled,
                                   DepartmentRepository departmentRepository,
                                   EmployeeRepository employeeRepository,
                                   LeaveRequestRepository leaveRequestRepository,
                                   AttendanceRecordRepository attendanceRecordRepository,
                                   PayrollRecordRepository payrollRecordRepository) {
        return args -> {
            if (!seedEnabled || employeeRepository.count() > 0 || departmentRepository.count() > 0) {
                return;
            }

            LocalDate today = LocalDate.now();
            YearMonth currentMonth = YearMonth.now();

            Department people = department("People Operations", "PEOPLE", "Owns talent, culture, compliance, and employee experience.", "Nina Verma");
            Department engineering = department("Engineering", "ENG", "Builds internal systems and employee-facing digital tools.", "Rahul Sharma");
            Department finance = department("Finance", "FIN", "Runs budgeting, payroll controls, and financial reporting.", "Maria Lewis");
            Department operations = department("Operations", "OPS", "Supports onboarding, workplace logistics, and vendor operations.", "Arjun Patel");
            Department sales = department("Revenue", "REV", "Owns account growth, pipelines, and revenue forecasting.", "Leah Brooks");

            departmentRepository.saveAll(List.of(people, engineering, finance, operations, sales));

            List<Employee> employees = new ArrayList<>();
            employees.add(employee("Aisha", "Khan", "aisha.khan@northstarhr.demo", "Senior HR Manager", people, today.minusYears(5).minusDays(12), "99000", Employee.EmploymentStatus.ACTIVE));
            employees.add(employee("Rahul", "Sharma", "rahul.sharma@northstarhr.demo", "Engineering Director", engineering, today.minusYears(6).minusDays(20), "148000", Employee.EmploymentStatus.ACTIVE));
            employees.add(employee("Maria", "Lewis", "maria.lewis@northstarhr.demo", "Finance Controller", finance, today.minusYears(4).minusDays(3), "132000", Employee.EmploymentStatus.ACTIVE));
            employees.add(employee("Priya", "Nair", "priya.nair@northstarhr.demo", "People Analyst", people, today.minusMonths(7), "68000", Employee.EmploymentStatus.ACTIVE));
            employees.add(employee("Daniel", "Cho", "daniel.cho@northstarhr.demo", "Platform Engineer", engineering, today.minusYears(2).minusMonths(2), "94000", Employee.EmploymentStatus.ACTIVE));
            employees.add(employee("Fatima", "Ali", "fatima.ali@northstarhr.demo", "Payroll Specialist", finance, today.minusYears(1).minusMonths(8), "72000", Employee.EmploymentStatus.ACTIVE));
            employees.add(employee("Owen", "Taylor", "owen.taylor@northstarhr.demo", "Operations Lead", operations, today.minusYears(3).minusMonths(4), "86000", Employee.EmploymentStatus.ACTIVE));
            employees.add(employee("Meera", "Joshi", "meera.joshi@northstarhr.demo", "Implementation Manager", operations, today.minusMonths(11), "79000", Employee.EmploymentStatus.ACTIVE));
            employees.add(employee("Leah", "Brooks", "leah.brooks@northstarhr.demo", "Revenue Manager", sales, today.minusYears(2).minusMonths(6), "101000", Employee.EmploymentStatus.ACTIVE));
            employees.add(employee("Sanjay", "Menon", "sanjay.menon@northstarhr.demo", "Account Executive", sales, today.minusMonths(3), "64000", Employee.EmploymentStatus.ACTIVE));
            employees.add(employee("Eva", "Nguyen", "eva.nguyen@northstarhr.demo", "People Coordinator", people, today.minusDays(18), "52000", Employee.EmploymentStatus.ACTIVE));
            employees.add(employee("Jon", "Mills", "jon.mills@northstarhr.demo", "QA Analyst", engineering, today.minusYears(1).minusMonths(1), "61000", Employee.EmploymentStatus.INACTIVE));
            employeeRepository.saveAll(employees);

            Map<String, Employee> employeeByEmail = employees.stream()
                    .collect(java.util.stream.Collectors.toMap(Employee::getEmail, employee -> employee));

            leaveRequestRepository.saveAll(List.of(
                    leave(employeeByEmail.get("priya.nair@northstarhr.demo"), today.plusDays(3), today.plusDays(5), LeaveRequest.LeaveType.ANNUAL, LeaveRequest.LeaveStatus.PENDING, null, "Family travel"),
                    leave(employeeByEmail.get("daniel.cho@northstarhr.demo"), today.minusDays(1), today.plusDays(1), LeaveRequest.LeaveType.SICK, LeaveRequest.LeaveStatus.APPROVED, "Aisha Khan", "Doctor-advised rest"),
                    leave(employeeByEmail.get("sanjay.menon@northstarhr.demo"), today.plusDays(10), today.plusDays(12), LeaveRequest.LeaveType.ANNUAL, LeaveRequest.LeaveStatus.APPROVED, "Leah Brooks", "Planned vacation"),
                    leave(employeeByEmail.get("eva.nguyen@northstarhr.demo"), today.plusDays(15), today.plusDays(15), LeaveRequest.LeaveType.OTHER, LeaveRequest.LeaveStatus.PENDING, null, "University convocation"),
                    leave(employeeByEmail.get("fatima.ali@northstarhr.demo"), today.minusDays(7), today.minusDays(6), LeaveRequest.LeaveType.SICK, LeaveRequest.LeaveStatus.APPROVED, "Maria Lewis", "Recovery leave")
            ));

            attendanceRecordRepository.saveAll(List.of(
                    attendance(employeeByEmail.get("aisha.khan@northstarhr.demo"), today, AttendanceRecord.AttendanceStatus.PRESENT, "09:03", "18:04"),
                    attendance(employeeByEmail.get("rahul.sharma@northstarhr.demo"), today, AttendanceRecord.AttendanceStatus.PRESENT, "08:52", "18:12"),
                    attendance(employeeByEmail.get("maria.lewis@northstarhr.demo"), today, AttendanceRecord.AttendanceStatus.PRESENT, "09:07", "17:58"),
                    attendance(employeeByEmail.get("priya.nair@northstarhr.demo"), today, AttendanceRecord.AttendanceStatus.LATE, "09:38", "18:11"),
                    attendance(employeeByEmail.get("daniel.cho@northstarhr.demo"), today, AttendanceRecord.AttendanceStatus.ABSENT, null, null),
                    attendance(employeeByEmail.get("fatima.ali@northstarhr.demo"), today, AttendanceRecord.AttendanceStatus.PRESENT, "09:15", "17:49"),
                    attendance(employeeByEmail.get("owen.taylor@northstarhr.demo"), today, AttendanceRecord.AttendanceStatus.PRESENT, "08:59", "18:00"),
                    attendance(employeeByEmail.get("meera.joshi@northstarhr.demo"), today, AttendanceRecord.AttendanceStatus.HALF_DAY, "09:10", "13:02"),
                    attendance(employeeByEmail.get("leah.brooks@northstarhr.demo"), today, AttendanceRecord.AttendanceStatus.PRESENT, "08:57", "18:21"),
                    attendance(employeeByEmail.get("sanjay.menon@northstarhr.demo"), today, AttendanceRecord.AttendanceStatus.PRESENT, "09:14", "18:18"),
                    attendance(employeeByEmail.get("eva.nguyen@northstarhr.demo"), today, AttendanceRecord.AttendanceStatus.PRESENT, "09:05", "17:45"),
                    attendance(employeeByEmail.get("jon.mills@northstarhr.demo"), today.minusDays(1), AttendanceRecord.AttendanceStatus.PRESENT, "09:11", "17:50")
            ));

            payrollRecordRepository.saveAll(buildPayroll(employees, currentMonth));
            payrollRecordRepository.saveAll(buildPayroll(employees.stream()
                    .filter(employee -> employee.getStatus() == Employee.EmploymentStatus.ACTIVE)
                    .toList(), currentMonth.minusMonths(1)));
        };
    }

    private Department department(String name, String code, String description, String managerName) {
        Department department = new Department();
        department.setName(name);
        department.setCode(code);
        department.setDescription(description);
        department.setManagerName(managerName);
        return department;
    }

    private Employee employee(String firstName,
                              String lastName,
                              String email,
                              String jobTitle,
                              Department department,
                              LocalDate hireDate,
                              String salary,
                              Employee.EmploymentStatus status) {
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setEmail(email);
        employee.setPhone("+91-9000-" + Math.abs(email.hashCode() % 10000));
        employee.setHireDate(hireDate);
        employee.setJobTitle(jobTitle);
        employee.setDepartment(department);
        employee.setMonthlySalary(new BigDecimal(salary));
        employee.setStatus(status);
        return employee;
    }

    private LeaveRequest leave(Employee employee,
                               LocalDate startDate,
                               LocalDate endDate,
                               LeaveRequest.LeaveType type,
                               LeaveRequest.LeaveStatus status,
                               String approverName,
                               String comment) {
        LeaveRequest request = new LeaveRequest();
        request.setEmployee(employee);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setType(type);
        request.setStatus(status);
        request.setApproverName(approverName);
        request.setComment(comment);
        return request;
    }

    private AttendanceRecord attendance(Employee employee,
                                        LocalDate date,
                                        AttendanceRecord.AttendanceStatus status,
                                        String checkIn,
                                        String checkOut) {
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployee(employee);
        record.setWorkDate(date);
        record.setStatus(status);
        record.setCheckInTime(checkIn != null ? LocalTime.parse(checkIn) : null);
        record.setCheckOutTime(checkOut != null ? LocalTime.parse(checkOut) : null);
        return record;
    }

    private List<PayrollRecord> buildPayroll(List<Employee> employees, YearMonth month) {
        List<PayrollRecord> records = new ArrayList<>();
        for (Employee employee : employees) {
            PayrollRecord payrollRecord = new PayrollRecord();
            payrollRecord.setEmployee(employee);
            payrollRecord.setYear(month.getYear());
            payrollRecord.setMonth(month.getMonthValue());
            BigDecimal base = employee.getMonthlySalary() != null ? employee.getMonthlySalary() : BigDecimal.ZERO;
            BigDecimal allowances = base.multiply(new BigDecimal("0.08")).setScale(2, java.math.RoundingMode.HALF_UP);
            BigDecimal deductions = base.multiply(new BigDecimal("0.03")).setScale(2, java.math.RoundingMode.HALF_UP);
            payrollRecord.setBaseSalary(base);
            payrollRecord.setAllowances(allowances);
            payrollRecord.setDeductions(deductions);
            payrollRecord.setNetPay(base.add(allowances).subtract(deductions));
            records.add(payrollRecord);
        }
        return records;
    }
}

package com.bluetree.employeedetails.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

@Entity
@Table(name = "employees")
public class Employees {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Column(name = "email")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Column(name = "dob")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Date of birth must be in the past")
    @NotNull(message = "Date of birth is required")  // Ensure DOB is provided
    private LocalDate dob;

    @Column(name = "age")
    private Integer age;

    @Column(name = "salary")
    @Positive(message = "Salary must be a positive number")
    @NotNull(message = "Salary is required")  // Ensure salary is provided
    private Double salary;

    @Column(name = "status")
    private boolean status;
    
    // Add a transient field for dobAsDate (converted from LocalDate to java.util.Date)
    @Transient
    private Date dobAsDate;

    // Constructor
    public Employees() {
    }

    // Constructor with parameters (optional)
    public Employees(String name, String email, LocalDate dob, Double salary, boolean status) {
        this.name = name;
        this.email = email;
        this.dob = dob;
        this.salary = salary;
        this.status = status;
        calculateAge();
    }

    public void calculateAge() {
        if (this.dob != null) {
            this.age = Period.between(this.dob, LocalDate.now()).getYears();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
        calculateAge(); // Recalculate age when date of birth changes
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
    
    // Getters for dobAsDate (converted LocalDate -> java.util.Date)
    public Date getDobAsDate() {
        if (dob != null) {
            return Date.from(dob.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        return null;
    }

    // This setter is not strictly necessary but can be included for completeness
    public void setDobAsDate(Date dobAsDate) {
        this.dobAsDate = dobAsDate;
    }
}

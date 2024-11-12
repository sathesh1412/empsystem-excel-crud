package com.bluetree.employeedetails.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bluetree.employeedetails.Exception.EmployeeValidationException;
import com.bluetree.employeedetails.entity.EmployeeUploadResult;
import com.bluetree.employeedetails.entity.Employees;
import com.bluetree.employeedetails.repository.EmployeeRepository;

import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    // Get all employees
    public List<Employees> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // Save a single employee (create or update)
    public Employees saveEmployees(Employees employee) {
        employee.calculateAge();  // Assuming there's a method to calculate age based on DOB
        return employeeRepository.save(employee);
    }
    
    public Employees updateEmployee(Long id) {
		return employeeRepository.findById(id).get();
	}
    
    public Employees updatedEmployee(Employees updatedEmployee) {
		Employees existingEmployee = employeeRepository.findById(updatedEmployee.getId()).orElse(null);
		if(existingEmployee!=null) {
			existingEmployee.setId(updatedEmployee.getId());
			existingEmployee.setName(updatedEmployee.getName());
			existingEmployee.setEmail(updatedEmployee.getEmail());
			existingEmployee.setDob(updatedEmployee.getDob());
			existingEmployee.setAge(updatedEmployee.getAge());
			existingEmployee.setSalary(updatedEmployee.getSalary());
			existingEmployee.setStatus(updatedEmployee.isStatus());
			return employeeRepository.save(existingEmployee);
		}else {
			return null;
		}
			
	}

    // Delete an employee by ID
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    // Upload Employees from Excel file
    public EmployeeUploadResult saveEmployeesFromExcel(MultipartFile file) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        List<String> allErrors = new ArrayList<>(); // Collect errors for the entire file
        List<Integer> successfulRows = new ArrayList<>(); // List to store successful row numbers
        int successCount = 0;
        int failureCount = 0;

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Skip header row

            Employees employee = new Employees();
            List<String> errors = new ArrayList<>(); // Collect errors for the current row

            // Read Name (String)
            String name = getStringCellValue(row.getCell(0));
            if (name.isEmpty()) {
                errors.add("Name cannot be empty at row " + (row.getRowNum() + 1));
            } else if (!name.matches("[A-Za-z\\s]+")) { // Regular expression to check for letters only
                errors.add("Invalid name format at row " + (row.getRowNum() + 1) + ". Name must contain only letters and spaces.");
            } else {
                employee.setName(name);
            }

            // Read Email (String)
            String email = getStringCellValue(row.getCell(1));
            if (email.isEmpty() || !email.matches("[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}")) {
                errors.add("Invalid email format at row " + (row.getRowNum() + 1));
            } else {
                employee.setEmail(email);
            }

            // Read Date of Birth (DOB) - Handle Date cells
            LocalDate dob = getDateCellValue(row.getCell(2));
            if (dob == null) {
                errors.add("Invalid date of birth at row " + (row.getRowNum() + 1));
            } else {
                employee.setDob(dob);
            }

            // Read Salary (Double) - Handle Numeric cells
            Double salary = getNumericCellValue(row.getCell(3));
            if (salary == null || salary <= 0) {
                errors.add("Invalid salary at row " + (row.getRowNum() + 1));
            } else {
                employee.setSalary(salary);
            }

            // Check for non-numeric input (Handle string input for numeric fields)
            if (!isNumeric(getStringCellValue(row.getCell(3)))) {
                errors.add("Salary should be a valid number at row " + (row.getRowNum() + 1));
            }

            // Read Status (Boolean) - Handle Boolean and Numeric cells
            boolean status = getBooleanCellValue(row.getCell(4));
            if (!status && row.getCell(4) != null && row.getCell(4).getCellType() != CellType.BLANK) {
                // If the status is invalid (not true or false), add an error
                errors.add("Status should be 'active' or 'inactive' at row " + (row.getRowNum() + 1));
            }
            employee.setStatus(status);

            // If there are errors for this row, add them to the overall errors list
            if (!errors.isEmpty()) {
                allErrors.add(String.join("; ", errors)); // Join all errors for the current row
                failureCount++; // Increment failure count
            } else {
                // If no errors, calculate the age and save the employee to the repository
                employee.calculateAge();
                employeeRepository.save(employee);
                successCount++; // Increment success count
                successfulRows.add(row.getRowNum() + 1);  // Add the row number to the list (1-based index)
            }
        }

        workbook.close();

        // Return result with counts, errors, and successful row numbers
        return new EmployeeUploadResult(successCount, failureCount, allErrors, successfulRows);
    }


        
     // Helper function to check if a string is numeric
        private boolean isNumeric(String str) {
            try {
                Double.parseDouble(str); // Tries to convert string to a number
                return true;
            } catch (NumberFormatException e) {
                return false; // If conversion fails, it's not numeric
            }
      
        }


        private String getStringCellValue(Cell cell) {
            if (cell == null) {
                return "";
            }

            if (cell.getCellType() == CellType.STRING) {
                return cell.getStringCellValue().trim(); // Trimming to handle any extra whitespace
            } else if (cell.getCellType() == CellType.NUMERIC) {
                // If a numeric value is entered where a string is expected, you can return an empty string or log an error
                return String.valueOf(cell.getNumericCellValue()); // Optionally return "" or throw an error/collect an error message
            } else if (cell.getCellType() == CellType.BLANK) {
                return "";
            }
        return "";
    }

    private LocalDate getDateCellValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.STRING) {
            try {
                return LocalDate.parse(cell.getStringCellValue());
            } catch (Exception e) {
                // Handle exception, e.g., log and return null
                return null;
            }
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getDateCellValue().toInstant()
                       .atZone(java.time.ZoneId.systemDefault())
                       .toLocalDate();
        }
        return null;
    }

    private Double getNumericCellValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Double.parseDouble(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                // Handle exception, e.g., log and return null
                return null;
            }
        }
        return null;
    }

    private boolean getBooleanCellValue(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            // Return false to signal missing value and handle it as an error later
            return false;
        }

        if (cell.getCellType() == CellType.BOOLEAN) {
            return cell.getBooleanCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            // If the cell contains numeric (0 or 1), convert it to boolean
            return cell.getNumericCellValue() == 1;
        } else if (cell.getCellType() == CellType.STRING) {
            String cellValue = cell.getStringCellValue().trim().toLowerCase();
            // If it is active or inactive (or their valid equivalents), return true or false
            if (cellValue.equals("active") || cellValue.equals("1") || cellValue.equals("true")) {
                return true;
            } else if (cellValue.equals("inactive") || cellValue.equals("0") || cellValue.equals("false")) {
                return false;
            }
        }

        // If the value is invalid, return false and handle it as an error later
        return false;
    }



 // Export Employees to Excel file
    public File exportEmployeesToExcel() throws IOException {
        // Create a new workbook and a sheet called "Employees"
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Employees");
        
        // Create a list of values for the dropdown (only for "Status" column)
        String[] statusOptions = {"Active", "Inactive"};

        // Create the header row (without any data)
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Name");
        headerRow.createCell(1).setCellValue("Email");
        headerRow.createCell(2).setCellValue("Date of Birth");
        headerRow.createCell(3).setCellValue("Salary");
        headerRow.createCell(4).setCellValue("Status");  // "Status" will have a dropdown
        
        // Create a DataValidationHelper to set data validation for the "Status" column
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        
        // Define the data validation constraint for the "Status" column (Dropdown values)
        DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(statusOptions);

        // Define the range for the "Status" column (for example, from row 2 to row 100, column 5)
        CellRangeAddressList addressList = new CellRangeAddressList(1, 99, 4, 4);  // Rows 2-100, Column 5 (Status)

        // Create the data validation object
        DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);

        // Apply the data validation to the sheet (only the "Status" column)
        sheet.addValidationData(dataValidation);

        // Create a temporary file to save the Excel file (no employee data will be written)
        File tempFile = File.createTempFile("employees-", ".xlsx");
        try (FileOutputStream fileOut = new FileOutputStream(tempFile)) {
            // Write the workbook content (header only) to the output file
            workbook.write(fileOut);
        }

        // Close the workbook to release resources
        workbook.close();

        // Return the temporary file containing the Excel data (just headers)
        return tempFile;
    }
    
    public File exportExisitingEmployeesToExcel() throws IOException {
        // Create a new workbook and a sheet called "Employees"
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Employees");

        // List of employees from the database
        List<Employees> employees = employeeRepository.findAll();

        // Create a list of values for the dropdown (only for "Status" column)
        String[] statusOptions = {"Active", "Inactive"};

        // Create the header row (without any data)
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Name");
        headerRow.createCell(1).setCellValue("Email");
        headerRow.createCell(2).setCellValue("Date of Birth");
        headerRow.createCell(3).setCellValue("Salary");
        headerRow.createCell(4).setCellValue("Status");  // "Status" will have a dropdown

        // Create a DataValidationHelper to set data validation for the "Status" column
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        
        // Define the data validation constraint for the "Status" column (Dropdown values)
        DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(statusOptions);

        // Define the range for the "Status" column (for example, from row 2 to row 100, column 5)
        CellRangeAddressList addressList = new CellRangeAddressList(1, employees.size(), 4, 4);  // Rows 2-100, Column 5 (Status)

        // Create the data validation object
        DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);

        // Apply the data validation to the sheet (only the "Status" column)
        sheet.addValidationData(dataValidation);

        // Fill the data rows with employee details
        int rowNum = 1;  // Start from row 1 (since row 0 is for headers)
        for (Employees employee : employees) {
            Row row = sheet.createRow(rowNum++);

            // Add employee data to each row
            row.createCell(0).setCellValue(employee.getName());  // Name
            row.createCell(1).setCellValue(employee.getEmail());  // Email
            row.createCell(2).setCellValue(employee.getDob() != null ? employee.getDob().toString() : ""); // Date of Birth
            row.createCell(3).setCellValue(employee.getSalary() != null ? employee.getSalary() : 0.0); // Salary
            row.createCell(4).setCellValue(employee.isStatus() ? "Active" : "Inactive"); // Status (will have dropdown)
        }

        // Create a temporary file to save the Excel file (with data)
        File tempFile = File.createTempFile("employees-data-", ".xlsx");
        try (FileOutputStream fileOut = new FileOutputStream(tempFile)) {
            // Write the workbook content (with data) to the output file
            workbook.write(fileOut);
        }

        // Close the workbook to release resources
        workbook.close();

        // Return the temporary file containing the Excel data (with employee information)
        return tempFile;
    }


}



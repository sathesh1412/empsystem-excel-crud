package com.bluetree.employeedetails.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.bluetree.employeedetails.Exception.EmployeeValidationException;
import com.bluetree.employeedetails.entity.EmployeeUploadResult;
import com.bluetree.employeedetails.entity.Employees;
import com.bluetree.employeedetails.service.EmployeeService;

//import jakarta.servlet.http.HttpServletResponse;

@Controller
public class EmployeesController {

    @Autowired
    private EmployeeService employeeService;

    // Display the employee management page with a list of all employees
    @GetMapping("/")
    public String home(Model model) {
        List<Employees> employeeList = employeeService.getAllEmployees();

        // Convert LocalDate to java.util.Date for each employee
        for (Employees employee : employeeList) {
            if (employee.getDob() != null) {
                // Convert LocalDate to Date
                Date dobAsDate = Date.from(employee.getDob().atStartOfDay(ZoneId.systemDefault()).toInstant());
                model.addAttribute("dobAsDate", dobAsDate);
            }
        }

        model.addAttribute("AllEmployees", employeeList);
        model.addAttribute("employee", new Employees()); // For adding a new employee
        return "employeeManagement"; // JSP page for CRUD operations
    }


    // Create or Update Employee
    @PostMapping("/saveEmployee")
    public String saveEmployee(@Valid @ModelAttribute("employee") Employees employee,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            // Collect all error messages and add them to the model as 'errors'
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());

            model.addAttribute("errors", errorMessages);

            // Return to the same form view (replace 'employeeManagement' with your JSP's view name)
            return "employeeManagement";
        }

        // Save the employee if valid
        employeeService.saveEmployees(employee);

        // Add success message to the model
        model.addAttribute("successMessage", "Employee saved successfully!");
        
     // Retrieve the list of all employees to display in the table
        List<Employees> employeeList = employeeService.getAllEmployees();
        model.addAttribute("AllEmployees", employeeList);

        // Redirect to the employee form or another page, if needed
        return "employeeManagement"; // or another page, like redirect:/employees
    }


    
    //update employee
    @GetMapping("/updateEmployee/{id}")
	public String updateEmployee(@PathVariable Long id,Model model) {
		Employees employee = employeeService.updateEmployee(id);
		if(employee!=null) {
			model.addAttribute("employee",employee);
			return "updateEmployeeForm";
		}else {
			return "errorPage";
		}
	}
    
    @PostMapping("/updatedEmployee/{id}")
	public String updatedEmployee(Employees employee,@PathVariable Long id,RedirectAttributes redirectAttributes) {
		employee.setId(id);
		employeeService.updatedEmployee(employee);
		redirectAttributes.addFlashAttribute("message","Employee Updated Successfully");
		return "redirect:/";
		
	}

    // Delete Employee
    @GetMapping("/deleteEmployee/{id}")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        employeeService.deleteEmployee(id);
        redirectAttributes.addFlashAttribute("message", "Employee deleted successfully!");
        return "redirect:/";
    }

    // Upload Employees from Excel
    @PostMapping("/uploadEmployees")
    public String uploadEmployees(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            // Call service to process the file and get the result
            EmployeeUploadResult result = employeeService.saveEmployeesFromExcel(file);

            // Pass the result to the frontend via redirect attributes
            redirectAttributes.addFlashAttribute("successCount", result.getSuccessCount());
            redirectAttributes.addFlashAttribute("failureCount", result.getFailureCount());
            redirectAttributes.addFlashAttribute("errorMessages", result.getErrorMessages());
            redirectAttributes.addFlashAttribute("successfulRows", result.getSuccessfulRows());  // Pass the successful rows list

        } catch (EmployeeValidationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error uploading employees: " + e.getMessage());
        }

        // Redirect to the homepage after processing
        return "redirect:/";
    }





    // Download Employees as Excel
    @GetMapping("/downloadEmployees")
    public void downloadEmployees(HttpServletResponse response) throws IOException {
        // Prepare the Excel file
        File file = employeeService.exportEmployeesToExcel();

        // Set response headers to indicate an Excel file download
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=employees.xlsx");

        // Write the file content to the response output stream
        try (InputStream inputStream = new FileInputStream(file);
             OutputStream outputStream = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush(); // Ensure all content is sent
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error while downloading the Excel file.", e);
        }
    }
    
 // Endpoint to download existing employee data as Excel
    @GetMapping("/downloadExistingEmployees")
    public void downloadExistingEmployees(HttpServletResponse response) throws IOException {
        // Get the generated Excel file from the service
        File file = employeeService.exportExisitingEmployeesToExcel();

        // Set the response headers for Excel file download
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=exisitingemployees.xlsx");

        // Write the file content to the response output stream
        try (InputStream inputStream = new FileInputStream(file);
             OutputStream outputStream = response.getOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();  // Ensure all content is written to the response
        }
    }

}

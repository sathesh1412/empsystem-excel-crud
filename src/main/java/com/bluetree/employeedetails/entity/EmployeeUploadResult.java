package com.bluetree.employeedetails.entity;

import java.util.List;

public class EmployeeUploadResult {
    private int successCount;
    private int failureCount;
    private List<String> errorMessages;
    private List<Integer> successfulRows;  // List to store successful row numbers

    // Constructor
    public EmployeeUploadResult(int successCount, int failureCount, List<String> errorMessages, List<Integer> successfulRows) {
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.errorMessages = errorMessages;
        this.successfulRows = successfulRows;
    }

    // Getters and Setters
    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public List<Integer> getSuccessfulRows() {
        return successfulRows;
    }

    public void setSuccessfulRows(List<Integer> successfulRows) {
        this.successfulRows = successfulRows;
    }
}




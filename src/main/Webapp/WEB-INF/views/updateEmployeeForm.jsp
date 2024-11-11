<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Update Employee</title>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container">
        <h2 class="mt-4">Update Employee</h2>

        <form:form action="/updatedEmployee/${employee.id}" method="post" modelAttribute="employee" class="mt-4">
            <form:hidden id="id" name="id" path="id" value="${employee.id}" />

            <div class="form-group row">
                <label for="id" class="col-sm-2 col-form-label">ID:</label>
                <div class="col-sm-10 col-md-8">
                    <input type="text" class="form-control" id="id" name="id" value="${employee.id}" readonly />
                </div>
            </div>

            <div class="form-group row">
                <label for="name" class="col-sm-2 col-form-label">Name:</label>
                <div class="col-sm-10 col-md-8">
                    <input type="text" class="form-control" id="name" name="name" value="${employee.name}">
                </div>
            </div>

             <div class="form-group row">
                <label for="email" class="col-sm-2 col-form-label">Email:</label>
                <div class="col-sm-10 col-md-8">
                    <input type="email" class="form-control" id="email" name="email" value="${employee.email}">
                </div>
            </div>

            <div class="form-group row">
                <label for="dob" class="col-sm-2 col-form-label">DOB:</label>
                <div class="col-sm-10 col-md-8">
                    <input type="date" class="form-control" id="dob" name="dob" value="${employee.dob}">
                </div>
            </div>

            
            <div class="form-group row">
                <label for="age" class="col-sm-2 col-form-label">Age:</label>
                <div class="col-sm-10 col-md-8">
                    <input type="number" class="form-control" id="age" name="age" value="${employee.age}">
                </div>
            </div>

            
            <div class="form-group row">
                <label for="salary" class="col-sm-2 col-form-label">Salary:</label>
                <div class="col-sm-10 col-md-8">
                    <input type="number" class="form-control" id="salary" name="salary" value="${employee.salary}">
                </div>
            </div>

            
            <div class="form-group row">
                <label for="status" class="col-sm-2 col-form-label">Status:</label>
                <div class="col-sm-10 col-md-8">
                   <select class="form-control" id="status" name="status">
                     <option value="true" ${employee.status == 'Active' ? 'selected' : ''}>Active</option>
                     <option value="false" ${employee.status == 'Inactive' ? 'selected' : ''}>Inactive</option>
                   </select>
                </div>
            </div>

            <div class="form-group row">
                <div class="col-sm-10 offset-sm-2">
                    <button type="submit" class="btn btn-primary btn-block">Update</button>
                </div>
            </div>
        </form:form>
    </div>
</body>
</html>
	
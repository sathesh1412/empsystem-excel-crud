<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Employee Management</title>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<div class="container">
    <h2 class="mt-4">Employee Management</h2>

    <!-- Display success or error message -->
    <c:if test="${not empty message}">
        <div class="alert alert-info">${message}</div>
    </c:if>
    
  <%--   <c:if test="${not empty errors}">
    <ul>
        <c:forEach var="error" items="${errors}">
            <li>${error}</li>
        </c:forEach>
    </ul>
</c:if>  --%>

<c:if test="${not empty successMessage}">
    <div class="alert alert-success">
        ${successMessage}
    </div>
</c:if>

<%-- Display error message --%>
 <c:if test="${not empty error}">
    <div class="alert alert-danger">
        <strong>Error!</strong> ${error}<br>
    </div>
</c:if>  

<%-- Display success message --%>
 <c:if test="${not empty message}">
    <div class="alert alert-success">
        <strong>Success!</strong> ${message}
    </div>
</c:if> 

    <!-- If there are error messages, display them -->
<%-- Display the error messages if present --%>
<c:if test="${not empty error}">
    <div class="alert alert-danger">
        <ul>
            <c:forEach var="err" items="${fn:split(error, ';')}">
                <li>${err}</li>
            </c:forEach>
        </ul>
    </div>
</c:if>


    <!-- Form for Adding/Editing Employee -->
    <form:form action="/saveEmployee" method="post" modelAttribute="employee" >
        
        <!-- Name Field -->
        <div class="form-row">
            <div class="form-group col-md-6">
                <label for="name">Name</label>
                <form:input path="name" class="form-control" id="name" />
                <form:errors path="name" cssClass="text-danger"/>
            </div>

            <!-- Email Field -->
            <div class="form-group col-md-6">
                <label for="email">Email</label>
                <form:input path="email" class="form-control" id="email" />
                <form:errors path="email" cssClass="text-danger"/>
            </div>
        </div>

        <!-- Date of Birth Field -->
        <div class="form-row">
            <div class="form-group col-md-6">
                <label for="dob">Date of Birth</label>
                <form:input path="dob" type="date" class="form-control" id="dob" />
                <form:errors path="dob" cssClass="text-danger"/>
            </div>

            <!-- Salary Field -->
            <div class="form-group col-md-6">
                <label for="salary">Salary</label>
                <form:input type="number" path="salary" class="form-control" id="salary" />
                <form:errors path="salary" cssClass="text-danger"/>
            </div>
        </div>

        <!-- Status Field -->
        <div class="form-row">
            <div class="form-group col-md-6">
                <label for="status">Status</label>
                <form:select path="status" class="form-control" id="status">
                    <form:option value="true">Active</form:option>
                    <form:option value="false">Inactive</form:option>
                </form:select>
                <form:errors path="status" cssClass="text-danger"/>
            </div>
        </div>

        <button type="submit" class="btn btn-primary">Save Employee</button>
    </form:form>

    <!-- Employee List -->
    <h3 class="mt-4">Employee List</h3>
    <table class="table table-bordered table-hover">
        <thead>
            <tr>
                <th>Name</th>
                <th>Email</th>
                <th>DOB</th>
                <th>Age</th> <!-- Added Age column -->
                <th>Salary</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="employee" items="${AllEmployees}">
                <tr>
                    <td>${employee.name}</td>
                    <td>${employee.email}</td>
                    <td><fmt:formatDate value="${employee.dobAsDate}" pattern="yyyy-MM-dd" /></td>
                    <td>${employee.age}</td> <!-- Displaying Age -->
                    <td>${employee.salary}</td>
                    <td>${employee.status ? 'Active' : 'Inactive'}</td>
                    <td>
                        <a href="/deleteEmployee/${employee.id}" class="btn btn-danger btn-sm">Delete</a>
                        <a href="/updateEmployee/${employee.id}" class="btn btn-warning btn-sm">Edit</a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <!-- Excel Upload Form -->
    <h4 class="mt-4">Upload Employees from Excel</h4>
    <form action="/uploadEmployees" method="post" enctype="multipart/form-data">
        <input type="file" name="file" accept=".xlsx" required />
        <button type="submit" class="btn btn-success">Upload</button>
    </form>

    <!-- Excel Download Links -->
    <h4 class="mt-4">Download Employees Data as Excel</h4>
    <a href="/downloadEmployees" class="btn btn-info" download>Download Excel</a>

    <h4 class="mt-4">Download Existing Employees Data as Excel</h4>
    <a href="/downloadExistingEmployees" class="btn btn-info" download>Download Existing Employees Excel</a>
</div>

</body>
<script>
        function validateEmail() {
            var email = document.getElementById("email").value;
            var regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
            if (regex.test(email)) {
                return true;
            } else {
                alert("Invalid Email Address. Please enter a valid one.");
                return false;
            }
        }
    </script>
</html>

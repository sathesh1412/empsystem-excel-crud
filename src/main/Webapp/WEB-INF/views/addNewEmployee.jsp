<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add New Employee</title>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
    <style>
        .custom-background {
            background-color: #f0f0f0; 
            padding: 20px;  
            border-radius: 10px;
        }

        h3.custom-h1 {
            color: #ffffff;
            background-color: #333333;
            padding: 10px;  
            border-radius: 5px; 
        }
    </style>
</head>
<body class="bg-light">
    <div class="container-fluid custom-background">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card">
                    <div class="card-body">
                        <h3 class="custom-h1 text-center">Add New Employee</h3>
                        <form:form method="post" action="/savedEmployees" onsubmit="return validateEmail()">
                            <div class="form-group">
                                <label for="name">Name</label>
                                <input type="text" class="form-control" id="name" name="name" required>
                            </div>
                            <div class="form-group">
                                <label for="email">Email</label>
                                <input type="text" class="form-control" id="email" name="email" required>
                            </div>
                            <div class="form-group">
                                <label for="dob">Date of Birth</label>
                                <input type="date" class="form-control" id="dob" name="dob" required>
                            </div>
                             <div class="form-group">
                                 <label for="age">Age</label>
                                 <input type="text" class="form-control" id="age" name="age" value="${employee.age}" readonly>
                              </div>


                            <div class="form-group">
                                <label for="salary">Salary</label>
                                <input type="number" class="form-control" id="salary" name="salary" step="0.01" required>
                            </div>
                            <div class="form-group">
                                <label for="status">Status</label>
                                <select class="form-control" id="status" name="status">
                                    <option value="true">Active</option>
                                    <option value="false">Inactive</option>
                                </select>
                            </div>
                            <button class="btn btn-primary btn-block" type="submit">Add Employee</button>
                            <spring:url value='/getAllEmployees' var="allEmployees" />
                            <a class="btn btn-secondary btn-block" href="${allEmployees}">View Employees</a>
                        </form:form>
                    </div>
                </div>
            </div>
        </div>
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

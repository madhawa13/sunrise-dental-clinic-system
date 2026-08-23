<%@ page language="java"
         contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ taglib prefix="c"
           uri="jakarta.tags.core" %>

<!DOCTYPE html>

<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>
        Login - Sunrise Dental Clinic
    </title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/style.css">

</head>

<body class="login-page">

<main class="login-container">

    <section class="login-card">

        <div class="login-brand">

            <div class="login-icon">
                &#129463;
            </div>

            <h1>Sunrise Dental Clinic</h1>

            <p>
                Appointment and Patient
                Management System
            </p>

        </div>

        <div class="login-heading">

            <h2>Staff Login</h2>

            <p>
                Enter your authorized staff
                account information.
            </p>

        </div>

        <c:if test="${not empty errorMessage}">

            <div class="alert alert-error">

                <c:out value="${errorMessage}"/>

            </div>

        </c:if>

        <c:if test="${param.logout == 'success'}">

            <div class="alert alert-success">

                You have logged out successfully.

            </div>

        </c:if>

        <c:if test="${param.session == 'expired'}">

            <div class="alert alert-warning">

                Your session has expired.
                Please log in again.

            </div>

        </c:if>

        <form method="post"
              action="${pageContext.request.contextPath}/login"
              class="login-form">

            <div class="form-group">

                <label for="username">

                    Username
                    <span class="required">*</span>

                </label>

                <input type="text"
                       id="username"
                       name="username"
                       value="<c:out value='${submittedUsername}'/>"
                       placeholder="Enter your username"
                       maxlength="50"
                       autocomplete="username"
                       required
                       autofocus>

            </div>

            <div class="form-group">

                <label for="password">

                    Password
                    <span class="required">*</span>

                </label>

                <input type="password"
                       id="password"
                       name="password"
                       placeholder="Enter your password"
                       autocomplete="current-password"
                       required>

            </div>

            <button type="submit"
                    class="btn btn-primary login-button">

                Log In

            </button>

        </form>

        <div class="login-information">

            <p>
                This system is restricted to
                authorized clinic staff.
            </p>

            <p>
                Receptionists and dentists must use
                their assigned login accounts.
            </p>

        </div>

    </section>

</main>

<footer class="login-footer">

    <p>
        &copy; 2026 Sunrise Dental Clinic.
        CIS6003 Advanced Programming Assignment.
    </p>

</footer>

</body>

</html>
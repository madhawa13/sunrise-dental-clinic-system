<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    isErrorPage="true" %>

<%@ taglib prefix="c"
    uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">

    <meta name="viewport"
        content="width=device-width, initial-scale=1.0">

    <title>
        Error | Sunrise Dental Clinic
    </title>

    <link rel="stylesheet"
        href="${pageContext.request.contextPath}/assets/css/style.css">
</head>

<body>

    <!-- Navigation Bar -->
    <header class="navbar">

        <div class="navbar-container">

            <a class="brand"
                href="${pageContext.request.contextPath}/">

                <span class="brand-icon">
                    🦷
                </span>

                <span class="brand-text">

                    <span class="brand-title">
                        Sunrise Dental Clinic
                    </span>

                    <span class="brand-subtitle">
                        Appointment and Patient Management
                    </span>

                </span>

            </a>

            <nav class="nav-links">

                <a class="nav-link"
                    href="${pageContext.request.contextPath}/">
                    Dashboard
                </a>

                <a class="nav-link"
                    href="${pageContext.request.contextPath}/patients">
                    Patients
                </a>

            </nav>

        </div>

    </header>


    <!-- Error Information -->
    <main class="error-container">

        <section class="card">

            <div class="card-body">

                <p class="error-code">
                    !
                </p>

                <h1 class="error-title">
                    Something went wrong
                </h1>

                <p class="error-message">

                    <c:choose>

                        <c:when test="${not empty errorMessage}">

                            <c:out value="${errorMessage}" />

                        </c:when>

                        <c:when test="${not empty requestScope['jakarta.servlet.error.message']}">

                            <c:out
                                value="${requestScope['jakarta.servlet.error.message']}" />

                        </c:when>

                        <c:otherwise>

                            The requested operation could not
                            be completed. Please try again.

                        </c:otherwise>

                    </c:choose>

                </p>

                <div class="form-actions"
                    style="justify-content: center;
                           border-top: 0;
                           padding-top: 0;">

                    <a class="btn btn-outline"
                        href="javascript:history.back();">

                        Go Back

                    </a>

                    <a class="btn btn-primary"
                        href="${pageContext.request.contextPath}/">

                        Return to Dashboard

                    </a>

                </div>

            </div>

        </section>

    </main>


    <!-- Footer -->
    <footer class="footer">

        Sunrise Dental Clinic
        &copy; 2026 |
        CIS6003 Advanced Programming Assignment

    </footer>

</body>

</html>
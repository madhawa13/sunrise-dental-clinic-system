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

    <title>Sunrise Dental Clinic System</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/style.css">
</head>

<body>

<header class="main-header">
    <div class="container">

        <div class="header-top">

            <div>
                <h1>Sunrise Dental Clinic</h1>

                <p>
                    Appointment and Patient Management System
                </p>
            </div>

            <div class="user-panel">

                <div class="user-information">

                    <span class="user-welcome">
                        Welcome,
                        <strong>
                            <c:out value="${sessionScope.username}"/>
                        </strong>
                    </span>

                    <span class="user-role">
                        <c:out value="${sessionScope.userRole}"/>
                    </span>

                </div>

                <form method="post"
                      action="${pageContext.request.contextPath}/logout"
                      class="logout-form">

                    <button type="submit"
                            class="btn logout-button">
                        Logout
                    </button>

                </form>

            </div>
        </div>

        <nav class="main-nav">

            <a href="${pageContext.request.contextPath}/"
               class="active">
                Dashboard
            </a>

            <a href="${pageContext.request.contextPath}/patients">
                Patients
            </a>

            <a href="${pageContext.request.contextPath}/appointments">
                Appointments
            </a>

            <c:if test="${sessionScope.userRole == 'DENTIST'}">

                <a href="${pageContext.request.contextPath}/treatments">
                    Treatments
                </a>

            </c:if>

            <c:if test="${sessionScope.userRole == 'RECEPTIONIST'}">

                <a href="${pageContext.request.contextPath}/bills">
                    Billing
                </a>

                <a href="${pageContext.request.contextPath}/reports">
                    Reports
                </a>

            </c:if>

            <a href="${pageContext.request.contextPath}/help">
                Help
            </a>

        </nav>

    </div>
</header>

<main class="container">

    <div class="alert alert-success">
        Login successful. Welcome to the Sunrise Dental Clinic
        management system,
        <c:out value="${sessionScope.username}"/>.
    </div>

    <section class="page-header">

        <div>
            <h2>System Dashboard</h2>

            <p>
                Manage clinic information using your
                authorized staff account.
            </p>
        </div>

    </section>

    <section class="dashboard-grid">

        <!-- Patient Management -->
        <article class="card dashboard-card">

            <div class="dashboard-icon">
                &#128100;
            </div>

            <h3>Patient Management</h3>

            <p>
                Register new patients and view, search,
                update or deactivate existing patient records.
            </p>

            <div class="dashboard-actions">

                <a class="btn btn-primary"
                   href="${pageContext.request.contextPath}/patients">
                    Manage Patients
                </a>

                <c:if test="${sessionScope.userRole == 'RECEPTIONIST'}">

                    <a class="btn btn-secondary"
                       href="${pageContext.request.contextPath}/patients?action=new">
                        Register Patient
                    </a>

                </c:if>

            </div>

        </article>

        <!-- Appointment Management -->
        <article class="card dashboard-card">

            <div class="dashboard-icon">
                &#128197;
            </div>

            <h3>Appointment Management</h3>

            <p>
                View clinic appointments, assigned dentists,
                dates, times and statuses.
            </p>

            <div class="dashboard-actions">

                <a class="btn btn-primary"
                   href="${pageContext.request.contextPath}/appointments">
                    Manage Appointments
                </a>

                <c:if test="${sessionScope.userRole == 'RECEPTIONIST'}">

                    <a class="btn btn-secondary"
                       href="${pageContext.request.contextPath}/appointments?action=new">
                        Schedule Appointment
                    </a>

                </c:if>

            </div>

        </article>

        <!-- Dentist-only Treatment Management -->
        <c:if test="${sessionScope.userRole == 'DENTIST'}">

            <article class="card dashboard-card">

                <div class="dashboard-icon">
                    &#129463;
                </div>

                <h3>Treatment Records</h3>

                <p>
                    Record diagnoses, dental treatments,
                    prescriptions and standard treatment
                    charge information.
                </p>

                <div class="dashboard-actions">

                    <a class="btn btn-primary"
                       href="${pageContext.request.contextPath}/treatments">
                        Manage Treatments
                    </a>

                    <a class="btn btn-secondary"
                       href="${pageContext.request.contextPath}/treatments?action=new">
                        Add Treatment
                    </a>

                </div>

            </article>

        </c:if>

        <!-- Receptionist-only Billing Management -->
        <c:if test="${sessionScope.userRole == 'RECEPTIONIST'}">

            <article class="card dashboard-card">

                <div class="dashboard-icon">
                    &#128179;
                </div>

                <h3>Billing and Payments</h3>

                <p>
                    Calculate treatment charges, prepare
                    printable bills and record patient payments.
                </p>

                <div class="dashboard-actions">

                    <a class="btn btn-primary"
                       href="${pageContext.request.contextPath}/bills">
                        Manage Bills
                    </a>

                    <a class="btn btn-secondary"
                       href="${pageContext.request.contextPath}/bills?action=new">
                        Create Bill
                    </a>

                </div>

            </article>

            <!-- Receptionist-only Reports -->
            <article class="card dashboard-card">

                <div class="dashboard-icon">
                    &#128202;
                </div>

                <h3>Appointment Billing Reports</h3>

                <p>
                    View appointment, patient, dentist,
                    treatment, billing and payment information
                    in a combined report.
                </p>

                <div class="dashboard-actions">

                    <a class="btn btn-primary"
                       href="${pageContext.request.contextPath}/reports">
                        View Reports
                    </a>

                </div>

            </article>

        </c:if>

        <!-- Help -->
        <article class="card dashboard-card">

            <div class="dashboard-icon">
                &#10067;
            </div>

            <h3>Help and Guidance</h3>

            <p>
                View instructions for using patient,
                appointment, treatment, billing and
                payment functions.
            </p>

            <div class="dashboard-actions">

                <a class="btn btn-primary"
                   href="${pageContext.request.contextPath}/help">
                    Open Help
                </a>

            </div>

        </article>

    </section>

</main>

<footer class="main-footer">

    <div class="container">

        <p>
            &copy; 2026 Sunrise Dental Clinic.
            CIS6003 Advanced Programming Assignment.
        </p>

    </div>

</footer>

</body>
</html>
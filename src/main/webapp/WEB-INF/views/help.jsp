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
        Help - Sunrise Dental Clinic
    </title>

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
                    Appointment and Patient
                    Management System
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

            <a href="${pageContext.request.contextPath}/">
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

            </c:if>

            <a href="${pageContext.request.contextPath}/help"
               class="active">
                Help
            </a>

        </nav>

    </div>

</header>

<main class="container">

    <section class="page-header">

        <div>

            <h2>System Help and Guidance</h2>

            <p>
                Instructions for using the
                Sunrise Dental Clinic system.
            </p>

        </div>

        <a class="btn btn-secondary"
           href="${pageContext.request.contextPath}/">

            Return to Dashboard

        </a>

    </section>

    <section class="help-grid">

        <article class="card help-card">

            <div class="help-number">1</div>

            <h3>Logging In and Out</h3>

            <ul class="help-list">

                <li>
                    Open the staff login page.
                </li>

                <li>
                    Enter the username and password
                    assigned to your account.
                </li>

                <li>
                    Your available modules depend
                    on your staff role.
                </li>

                <li>
                    Always use the Logout button
                    after completing your work.
                </li>

            </ul>

        </article>

        <article class="card help-card">

            <div class="help-number">2</div>

            <h3>Patient Management</h3>

            <ul class="help-list">

                <li>
                    Open Patients from the
                    navigation menu.
                </li>

                <li>
                    Search using patient name,
                    patient number, NIC or phone.
                </li>

                <li>
                    Receptionists can register and
                    update patient information.
                </li>

                <li>
                    Patient records are deactivated
                    instead of permanently removed.
                </li>

            </ul>

        </article>

        <article class="card help-card">

            <div class="help-number">3</div>

            <h3>Appointment Management</h3>

            <ul class="help-list">

                <li>
                    Select a registered patient
                    and an active dentist.
                </li>

                <li>
                    Choose the appointment date,
                    time and reason.
                </li>

                <li>
                    The system prevents the same
                    dentist from being double-booked.
                </li>

                <li>
                    Appointments may be scheduled,
                    completed, cancelled or marked
                    as no-show.
                </li>

            </ul>

        </article>

        <c:if test="${sessionScope.userRole == 'DENTIST'}">

            <article class="card help-card">

                <div class="help-number">4</div>

                <h3>Treatment Records</h3>

                <ul class="help-list">

                    <li>
                        Open Treatments from
                        the navigation menu.
                    </li>

                    <li>
                        Select the related completed
                        appointment.
                    </li>

                    <li>
                        Record diagnosis, treatment
                        notes and prescription.
                    </li>

                    <li>
                        Use Manage Charges to assign
                        standard treatment charges.
                    </li>

                </ul>

            </article>

        </c:if>

        <c:if test="${sessionScope.userRole == 'RECEPTIONIST'}">

            <article class="card help-card">

                <div class="help-number">4</div>

                <h3>Billing and Payments</h3>

                <ul class="help-list">

                    <li>
                        Create a bill for an appointment
                        with treatment charge records.
                    </li>

                    <li>
                        Confirm the calculated subtotal
                        and enter any permitted discount.
                    </li>

                    <li>
                        Record partial or complete
                        patient payments.
                    </li>

                    <li>
                        Open the printable bill page
                        to prepare the patient invoice.
                    </li>

                </ul>

            </article>

        </c:if>

        <article class="card help-card">

            <div class="help-number">5</div>

            <h3>Security Guidance</h3>

            <ul class="help-list">

                <li>
                    Never share your password
                    with another person.
                </li>

                <li>
                    Do not leave the application
                    open on an unattended computer.
                </li>

                <li>
                    Unauthorized modules are blocked
                    according to your staff role.
                </li>

                <li>
                    Contact the system administrator
                    if your account is inactive.
                </li>

            </ul>

        </article>

    </section>

    <section class="card help-contact">

        <h3>Need Additional Assistance?</h3>

        <p>
            Contact the Sunrise Dental Clinic
            system administrator or clinic manager.
        </p>

        <div class="information-grid">

            <div>

                <strong>System</strong>

                <span>
                    Sunrise Dental Clinic System
                </span>

            </div>

            <div>

                <strong>Current Role</strong>

                <span>
                    <c:out value="${sessionScope.userRole}"/>
                </span>

            </div>

        </div>

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
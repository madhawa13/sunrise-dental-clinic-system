<%@ page language="java"
         contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

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

        <h1>Sunrise Dental Clinic</h1>

        <p>Appointment and Patient Management System</p>

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

            <a href="${pageContext.request.contextPath}/treatments">
                Treatments
            </a>

        </nav>

    </div>
</header>

<main class="container">

    <section class="page-header">

        <div>
            <h2>System Dashboard</h2>

            <p>
                Welcome to the Sunrise Dental Clinic
                management system.
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

                <a class="btn btn-secondary"
                   href="${pageContext.request.contextPath}/patients?action=new">

                    Register Patient

                </a>

            </div>

        </article>

        <!-- Appointment Management -->

        <article class="card dashboard-card">

            <div class="dashboard-icon">
                &#128197;
            </div>

            <h3>Appointment Management</h3>

            <p>
                Schedule patient appointments, assign dentists,
                prevent double bookings and manage statuses.
            </p>

            <div class="dashboard-actions">

                <a class="btn btn-primary"
                   href="${pageContext.request.contextPath}/appointments">

                    Manage Appointments

                </a>

                <a class="btn btn-secondary"
                   href="${pageContext.request.contextPath}/appointments?action=new">

                    Schedule Appointment

                </a>

            </div>

        </article>

        <!-- Treatment Management -->

        <article class="card dashboard-card">

            <div class="dashboard-icon">
                &#129463;
            </div>

            <h3>Treatment Records</h3>

            <p>
                Record diagnoses, dental treatments,
                prescriptions and treatment information
                for completed appointments.
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

        <!-- Billing Management -->

        <article class="card dashboard-card">

            <div class="dashboard-icon">
                &#128179;
            </div>

            <h3>Billing and Payments</h3>

            <p>
                Calculate treatment charges, prepare bills
                and record payments received from patients.
            </p>

            <div class="dashboard-actions">

                <button class="btn btn-disabled"
                        type="button"
                        disabled>

                    Coming Soon

                </button>

            </div>

        </article>

    </section>

    <section class="card system-information">

        <h3>System Information</h3>

        <div class="information-grid">

            <div>
                <strong>Module</strong>
                <span>CIS6003 Advanced Programming</span>
            </div>

            <div>
                <strong>Application</strong>
                <span>Sunrise Dental Clinic System</span>
            </div>

            <div>
                <strong>Technology</strong>
                <span>Java, JSP, Servlets, JDBC and MySQL</span>
            </div>

            <div>
                <strong>Server</strong>
                <span>Apache Tomcat 10.1</span>
            </div>

            <div>
                <strong>Testing</strong>
                <span>JUnit 5, Mockito, H2 and TDD</span>
            </div>

            <div>
                <strong>Completed Modules</strong>
                <span>
                    Patients, Appointments and Treatments
                </span>
            </div>

            <div>
                <strong>Current Development</strong>
                <span>Billing and Payments</span>
            </div>

            <div>
                <strong>Application Status</strong>

                <span class="status-badge status-completed">
                    Running
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
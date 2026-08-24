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
        Appointment Management | Sunrise Dental Clinic
    </title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/style.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/modern.css?v=1">
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

            <a href="${pageContext.request.contextPath}/">
                Dashboard
            </a>

            <a href="${pageContext.request.contextPath}/patients">
                Patients
            </a>

            <a href="${pageContext.request.contextPath}/appointments"
               class="active">
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

    <section class="page-header">

        <div>
            <h2>Appointment Management</h2>

            <p>
                View, search and manage patient
                appointments and assigned dentists.
            </p>
        </div>

        <c:if test="${sessionScope.userRole == 'RECEPTIONIST'}">

            <a class="btn btn-primary"
               href="${pageContext.request.contextPath}/appointments?action=new">
                Schedule Appointment
            </a>

        </c:if>

    </section>

    <!-- Success messages -->
    <c:choose>

        <c:when test="${param.success == 'scheduled'}">

            <div class="alert alert-success">
                Appointment scheduled successfully.
            </div>

        </c:when>

        <c:when test="${param.success == 'updated'}">

            <div class="alert alert-success">
                Appointment updated successfully.
            </div>

        </c:when>

        <c:when test="${param.success == 'cancelled'}">

            <div class="alert alert-success">
                Appointment cancelled successfully.
            </div>

        </c:when>

    </c:choose>

    <!-- Error message -->
    <c:if test="${not empty errorMessage}">

        <div class="alert alert-danger">
            <c:out value="${errorMessage}"/>
        </div>

    </c:if>

    <!-- Search -->
    <section class="card">

        <form method="get"
              action="${pageContext.request.contextPath}/appointments"
              class="search-form">

            <input type="hidden"
                   name="action"
                   value="search">

            <div class="form-group">

                <label for="searchTerm">
                    Search Appointments
                </label>

                <input type="search"
                       id="searchTerm"
                       name="searchTerm"
                       value="<c:out value='${searchTerm}'/>"
                       placeholder="Search appointment number, patient, dentist or status">

            </div>

            <div class="form-actions">

                <button type="submit"
                        class="btn btn-primary">
                    Search
                </button>

                <a class="btn btn-secondary"
                   href="${pageContext.request.contextPath}/appointments">
                    Clear
                </a>

            </div>

        </form>

    </section>

    <!-- Appointment table -->
    <section class="card">

        <c:choose>

            <c:when test="${empty appointments}">

                <div class="empty-message">

                    <h3>No appointments found</h3>

                    <p>
                        There are currently no appointments
                        to display.
                    </p>

                    <c:if test="${sessionScope.userRole == 'RECEPTIONIST'}">

                        <a class="btn btn-primary"
                           href="${pageContext.request.contextPath}/appointments?action=new">
                            Schedule First Appointment
                        </a>

                    </c:if>

                </div>

            </c:when>

            <c:otherwise>

                <div class="table-responsive">

                    <table class="data-table">

                        <thead>
                        <tr>
                            <th>Appointment No.</th>
                            <th>Patient</th>
                            <th>Dentist</th>
                            <th>Date</th>
                            <th>Time</th>
                            <th>Reason</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                        </thead>

                        <tbody>

                        <c:forEach var="appointment"
                                   items="${appointments}">

                            <tr>

                                <td>
                                    <span class="patient-number">
                                        <c:out value="${appointment.appointmentNumber}"/>
                                    </span>
                                </td>

                                <td>
                                    <strong class="patient-name">
                                        <c:out value="${appointment.patientName}"/>
                                    </strong>

                                    <small class="form-help">
                                        Patient ID:
                                        <c:out value="${appointment.patientId}"/>
                                    </small>
                                </td>

                                <td>
                                    <strong>
                                        <c:out value="${appointment.dentistName}"/>
                                    </strong>

                                    <small class="form-help">
                                        Dentist ID:
                                        <c:out value="${appointment.dentistId}"/>
                                    </small>
                                </td>

                                <td>
                                    <c:out value="${appointment.appointmentDate}"/>
                                </td>

                                <td>
                                    <c:out value="${appointment.appointmentTime}"/>
                                </td>

                                <td>
                                    <c:out value="${appointment.reason}"/>
                                </td>

                                <td>

                                    <c:choose>

                                        <c:when test="${appointment.status == 'SCHEDULED'}">

                                            <span class="status-badge status-scheduled">
                                                Scheduled
                                            </span>

                                        </c:when>

                                        <c:when test="${appointment.status == 'COMPLETED'}">

                                            <span class="status-badge status-completed">
                                                Completed
                                            </span>

                                        </c:when>

                                        <c:when test="${appointment.status == 'CANCELLED'}">

                                            <span class="status-badge status-cancelled">
                                                Cancelled
                                            </span>

                                        </c:when>

                                        <c:when test="${appointment.status == 'NO_SHOW'}">

                                            <span class="status-badge status-no_show">
                                                No Show
                                            </span>

                                        </c:when>

                                        <c:otherwise>

                                            <span class="status-badge">
                                                <c:out value="${appointment.status}"/>
                                            </span>

                                        </c:otherwise>

                                    </c:choose>

                                </td>

                                <td>

                                    <div class="action-buttons">

                                        <c:choose>

                                            <c:when test="${sessionScope.userRole == 'RECEPTIONIST'}">

                                                <a class="btn btn-small btn-secondary"
                                                   href="${pageContext.request.contextPath}/appointments?action=edit&id=${appointment.appointmentId}">
                                                    Edit
                                                </a>

                                                <c:if test="${appointment.status == 'SCHEDULED'}">

                                                    <form method="post"
                                                          action="${pageContext.request.contextPath}/appointments"
                                                          class="inline-form"
                                                          onsubmit="return confirmAppointmentCancellation();">

                                                        <input type="hidden"
                                                               name="action"
                                                               value="cancel">

                                                        <input type="hidden"
                                                               name="appointmentId"
                                                               value="${appointment.appointmentId}">

                                                        <button type="submit"
                                                                class="btn btn-small btn-danger">
                                                            Cancel
                                                        </button>

                                                    </form>

                                                </c:if>

                                            </c:when>

                                            <c:otherwise>

                                                <span class="form-help">
                                                    View only
                                                </span>

                                            </c:otherwise>

                                        </c:choose>

                                    </div>

                                </td>

                            </tr>

                        </c:forEach>

                        </tbody>

                    </table>

                </div>

            </c:otherwise>

        </c:choose>

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

<script>
    function confirmAppointmentCancellation() {
        return window.confirm(
                "Are you sure you want to cancel this appointment?"
        );
    }
</script>

</body>
</html>
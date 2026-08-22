<%@ page language="java"
         contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Appointments - Sunrise Dental Clinic</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/style.css">
</head>

<body>

<header class="main-header">
    <div class="container">

        <h1>Sunrise Dental Clinic</h1>

        <p>Appointment and Patient Management System</p>

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

        </nav>

    </div>
</header>

<main class="container">

    <section class="page-header">

        <div>
            <h2>Appointment Management</h2>

            <p>
                View, search, schedule and manage patient appointments.
            </p>
        </div>

        <a class="btn btn-primary"
           href="${pageContext.request.contextPath}/appointments?action=new">

            Schedule New Appointment

        </a>

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

        <c:when test="${not empty successMessage}">

            <div class="alert alert-success">
                <c:out value="${successMessage}"/>
            </div>

        </c:when>

    </c:choose>

    <!-- Error message -->

    <c:if test="${not empty errorMessage}">

        <div class="alert alert-danger">
            <c:out value="${errorMessage}"/>
        </div>

    </c:if>

    <!-- Search section -->

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

                <input type="text"
                       id="searchTerm"
                       name="searchTerm"
                       value="<c:out value='${param.searchTerm}'/>"
                       placeholder="Enter appointment number or reason">

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

    <!-- Appointment list -->

    <section class="card">

        <c:choose>

            <c:when test="${empty appointments}">

                <div class="empty-message">

                    <h3>No appointments found</h3>

                    <p>
                        There are currently no appointments to display.
                    </p>

                    <a class="btn btn-primary"
                       href="${pageContext.request.contextPath}/appointments?action=new">

                        Schedule First Appointment

                    </a>

                </div>

            </c:when>

            <c:otherwise>

                <div class="table-responsive">

                    <table class="data-table">

                        <thead>
                        <tr>
                            <th>Appointment No.</th>
                            <th>Patient ID</th>
                            <th>Dentist ID</th>
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
                                    <strong>
                                        <c:out value="${appointment.appointmentNumber}"/>
                                    </strong>
                                </td>

                                <td>
                                    <c:out value="${appointment.patientId}"/>
                                </td>

                                <td>
                                    <c:out value="${appointment.dentistId}"/>
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
                                    <span class="status-badge status-${fn:toLowerCase(appointment.status)}">

                                        <c:out value="${appointment.status}"/>

                                    </span>
                                </td>

                                <td>
                                    <div class="action-buttons">

                                        <a class="btn btn-small btn-secondary"
                                           href="${pageContext.request.contextPath}/appointments?action=edit&id=${appointment.appointmentId}">

                                            Edit

                                        </a>

                                        <c:if test="${appointment.status == 'SCHEDULED'}">

                                            <form method="post"
                                                  action="${pageContext.request.contextPath}/appointments"
                                                  class="inline-form">

                                                <input type="hidden"
                                                       name="action"
                                                       value="cancel">

                                                <input type="hidden"
                                                       name="appointmentId"
                                                       value="${appointment.appointmentId}">

                                                <button type="submit"
                                                        class="btn btn-small btn-danger"
                                                        onclick="return confirm('Are you sure you want to cancel this appointment?');">

                                                    Cancel

                                                </button>

                                            </form>

                                        </c:if>

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

</body>
</html>
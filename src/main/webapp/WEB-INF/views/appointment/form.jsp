<%@ page language="java"
         contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>
        ${empty appointment.appointmentId
            ? 'Schedule Appointment'
            : 'Edit Appointment'}
        - Sunrise Dental Clinic
    </title>

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

            <c:choose>
                <c:when test="${empty appointment.appointmentId}">
                    <h2>Schedule New Appointment</h2>

                    <p>
                        Select a patient, dentist, date and available time.
                    </p>
                </c:when>

                <c:otherwise>
                    <h2>Edit Appointment</h2>

                    <p>
                        Update the selected appointment information.
                    </p>
                </c:otherwise>
            </c:choose>

        </div>

        <a class="btn btn-secondary"
           href="${pageContext.request.contextPath}/appointments">
            Back to Appointments
        </a>
    </section>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">
            <c:out value="${errorMessage}"/>
        </div>
    </c:if>

    <section class="card">

        <form method="post"
              action="${pageContext.request.contextPath}/appointments">

            <c:choose>

                <c:when test="${empty appointment.appointmentId}">

                    <!-- Controller expects action=schedule -->

                    <input type="hidden"
                           name="action"
                           value="schedule">

                </c:when>

                <c:otherwise>

                    <!-- Controller expects action=update -->

                    <input type="hidden"
                           name="action"
                           value="update">

                    <input type="hidden"
                           name="appointmentId"
                           value="${appointment.appointmentId}">

                    <input type="hidden"
                           name="appointmentNumber"
                           value="${appointment.appointmentNumber}">

                </c:otherwise>

            </c:choose>

            <div class="form-grid">

                <div class="form-group">
                    <label for="patientId">
                        Patient
                        <span class="required">*</span>
                    </label>

                    <select id="patientId"
                            name="patientId"
                            required>

                        <option value="">
                            -- Select Patient --
                        </option>

                        <c:forEach var="patient"
                                   items="${patients}">

                            <option value="${patient.patientId}"
                                ${appointment.patientId == patient.patientId
                                    ? 'selected'
                                    : ''}>

                                <c:out value="${patient.patientNumber}"/>
                                -
                                <c:out value="${patient.firstName}"/>
                                <c:out value="${patient.lastName}"/>

                            </option>

                        </c:forEach>

                    </select>
                </div>

                <div class="form-group">
                    <label for="dentistId">
                        Dentist
                        <span class="required">*</span>
                    </label>

                    <select id="dentistId"
                            name="dentistId"
                            required>

                        <option value="">
                            -- Select Dentist --
                        </option>

                        <c:forEach var="dentist"
                                   items="${dentists}">

                            <option value="${dentist.userId}"
                                ${appointment.dentistId == dentist.userId
                                    ? 'selected'
                                    : ''}>

                                <c:out value="${dentist.fullName}"/>

                            </option>

                        </c:forEach>

                    </select>
                </div>

                <div class="form-group">
                    <label for="appointmentDate">
                        Appointment Date
                        <span class="required">*</span>
                    </label>

                    <input type="date"
                           id="appointmentDate"
                           name="appointmentDate"
                           value="${appointment.appointmentDate}"
                           required>
                </div>

                <div class="form-group">
                    <label for="appointmentTime">
                        Appointment Time
                        <span class="required">*</span>
                    </label>

                    <input type="time"
                           id="appointmentTime"
                           name="appointmentTime"
                           value="${appointment.appointmentTime}"
                           required>
                </div>

                <div class="form-group form-group-full">
                    <label for="reason">
                        Reason for Appointment
                        <span class="required">*</span>
                    </label>

                    <input type="text"
                           id="reason"
                           name="reason"
                           maxlength="255"
                           value="<c:out value='${appointment.reason}'/>"
                           placeholder="Example: Dental consultation"
                           required>
                </div>

                <div class="form-group form-group-full">
                    <label for="notes">
                        Additional Notes
                    </label>

                    <textarea id="notes"
                              name="notes"
                              rows="4"
                              maxlength="1000"
                              placeholder="Enter any additional appointment information"><c:out value="${appointment.notes}"/></textarea>
                </div>

                <c:if test="${not empty appointment.appointmentId}">
                    <div class="form-group">

                        <label for="status">
                            Appointment Status
                            <span class="required">*</span>
                        </label>

                        <select id="status"
                                name="status"
                                required>

                            <option value="SCHEDULED"
                                ${appointment.status == 'SCHEDULED'
                                    ? 'selected'
                                    : ''}>
                                Scheduled
                            </option>

                            <option value="COMPLETED"
                                ${appointment.status == 'COMPLETED'
                                    ? 'selected'
                                    : ''}>
                                Completed
                            </option>

                            <option value="CANCELLED"
                                ${appointment.status == 'CANCELLED'
                                    ? 'selected'
                                    : ''}>
                                Cancelled
                            </option>

                            <option value="NO_SHOW"
                                ${appointment.status == 'NO_SHOW'
                                    ? 'selected'
                                    : ''}>
                                No Show
                            </option>

                        </select>
                    </div>
                </c:if>

            </div>

            <div class="form-actions">

                <button type="submit"
                        class="btn btn-primary">

                    <c:choose>
                        <c:when test="${empty appointment.appointmentId}">
                            Schedule Appointment
                        </c:when>

                        <c:otherwise>
                            Update Appointment
                        </c:otherwise>
                    </c:choose>

                </button>

                <a class="btn btn-secondary"
                   href="${pageContext.request.contextPath}/appointments">
                    Cancel
                </a>

            </div>

        </form>

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
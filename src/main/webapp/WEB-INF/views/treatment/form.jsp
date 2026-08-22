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
        ${empty treatment.treatmentId
            ? 'Add Treatment Record'
            : 'Edit Treatment Record'}
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

            <a href="${pageContext.request.contextPath}/appointments">
                Appointments
            </a>

            <a href="${pageContext.request.contextPath}/treatments"
               class="active">
                Treatments
            </a>

        </nav>

    </div>
</header>

<main class="container">

    <section class="page-header">

        <div>

            <c:choose>

                <c:when test="${empty treatment.treatmentId}">

                    <h2>Add Treatment Record</h2>

                    <p>
                        Record diagnosis, treatment details
                        and prescription for a completed appointment.
                    </p>

                </c:when>

                <c:otherwise>

                    <h2>Edit Treatment Record</h2>

                    <p>
                        Update the selected patient's
                        treatment information.
                    </p>

                </c:otherwise>

            </c:choose>

        </div>

        <a class="btn btn-secondary"
           href="${pageContext.request.contextPath}/treatments">

            Back to Treatments

        </a>

    </section>

    <!-- Error message -->

    <c:if test="${not empty errorMessage}">

        <div class="alert alert-danger">
            <c:out value="${errorMessage}"/>
        </div>

    </c:if>

    <section class="card">

        <form method="post"
              action="${pageContext.request.contextPath}/treatments">

            <c:choose>

                <c:when test="${empty treatment.treatmentId}">

                    <input type="hidden"
                           name="action"
                           value="create">

                </c:when>

                <c:otherwise>

                    <input type="hidden"
                           name="action"
                           value="update">

                    <input type="hidden"
                           name="treatmentId"
                           value="${treatment.treatmentId}">

                </c:otherwise>

            </c:choose>

            <div class="form-grid">

                <!-- Completed appointment -->

                <div class="form-group form-group-full">

                    <label for="appointmentId">

                        Completed Appointment
                        <span class="required">*</span>

                    </label>

                    <select id="appointmentId"
                            name="appointmentId"
                            required>

                        <option value="">
                            -- Select Completed Appointment --
                        </option>

                        <c:forEach var="appointment"
                                   items="${appointments}">

                            <option value="${appointment.appointmentId}"
                                ${treatment.appointmentId
                                        == appointment.appointmentId
                                    ? 'selected'
                                    : ''}>

                                <c:out value="${appointment.appointmentNumber}"/>

                                -

                                <c:out value="${appointment.patientName}"/>

                                -

                                <c:out value="${appointment.appointmentDate}"/>

                            </option>

                        </c:forEach>

                    </select>

                    <c:if test="${empty appointments}">

                        <small class="form-help">

                            No completed appointments are available.
                            Mark an appointment as COMPLETED first.

                        </small>

                    </c:if>

                </div>

                <!-- Dentist -->

                <div class="form-group">

                    <label for="dentistId">

                        Treating Dentist
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
                                ${treatment.dentistId
                                        == dentist.userId
                                    ? 'selected'
                                    : ''}>

                                <c:out value="${dentist.fullName}"/>

                            </option>

                        </c:forEach>

                    </select>

                </div>

                <!-- Treatment date -->

                <div class="form-group">

                    <label for="treatmentDate">

                        Treatment Date
                        <span class="required">*</span>

                    </label>

                    <input type="date"
                           id="treatmentDate"
                           name="treatmentDate"
                           value="${treatment.treatmentDate}"
                           required>

                </div>

                <!-- Diagnosis -->

                <div class="form-group form-group-full">

                    <label for="diagnosis">
                        Diagnosis
                    </label>

                    <textarea id="diagnosis"
                              name="diagnosis"
                              rows="3"
                              maxlength="500"
                              placeholder="Enter the dentist's diagnosis"><c:out value="${treatment.diagnosis}"/></textarea>

                    <small class="form-help">
                        Diagnosis is optional and cannot exceed
                        500 characters.
                    </small>

                </div>

                <!-- Treatment notes -->

                <div class="form-group form-group-full">

                    <label for="treatmentNotes">

                        Treatment Notes
                        <span class="required">*</span>

                    </label>

                    <textarea id="treatmentNotes"
                              name="treatmentNotes"
                              rows="5"
                              placeholder="Describe examinations, procedures and treatments performed"
                              required><c:out value="${treatment.treatmentNotes}"/></textarea>

                </div>

                <!-- Prescription -->

                <div class="form-group form-group-full">

                    <label for="prescription">
                        Prescription
                    </label>

                    <textarea id="prescription"
                              name="prescription"
                              rows="4"
                              placeholder="Enter prescribed medicines and instructions"><c:out value="${treatment.prescription}"/></textarea>

                    <small class="form-help">
                        Leave blank when no medicine was prescribed.
                    </small>

                </div>

            </div>

            <div class="form-actions">

                <button type="submit"
                        class="btn btn-primary"
                        ${empty appointments
                            && empty treatment.treatmentId
                            ? 'disabled'
                            : ''}>

                    <c:choose>

                        <c:when test="${empty treatment.treatmentId}">
                            Save Treatment Record
                        </c:when>

                        <c:otherwise>
                            Update Treatment Record
                        </c:otherwise>

                    </c:choose>

                </button>

                <a class="btn btn-secondary"
                   href="${pageContext.request.contextPath}/treatments">

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
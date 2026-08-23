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
        Treatment Records - Sunrise Dental Clinic
    </title>

    <link rel="stylesheet"
        href="${pageContext.request.contextPath}/assets/css/style.css">
</head>

<body>

<header class="main-header">
    <div class="container">

        <h1>Sunrise Dental Clinic</h1>

        <p>
            Appointment and Patient Management System
        </p>

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

            <a class="active"
                href="${pageContext.request.contextPath}/treatments">
                Treatments
            </a>

            <a href="${pageContext.request.contextPath}/bills">
                Billing
            </a>

        </nav>
    </div>
</header>

<main class="container">

    <section class="page-header">
        <div>
            <h2>Treatment Records</h2>

            <p>
                Record diagnoses, treatments,
                prescriptions and standard charges
                for completed patient appointments.
            </p>
        </div>

        <a class="btn btn-primary"
            href="${pageContext.request.contextPath}/treatments?action=new">
            Add Treatment Record
        </a>
    </section>

    <c:choose>

        <c:when test="${param.success == 'created'}">
            <div class="alert alert-success">
                Treatment record created successfully.
                You can now assign its standard charges.
            </div>
        </c:when>

        <c:when test="${param.success == 'updated'}">
            <div class="alert alert-success">
                Treatment record updated successfully.
            </div>
        </c:when>

        <c:when test="${param.success == 'deleted'}">
            <div class="alert alert-success">
                Treatment record deleted successfully.
            </div>
        </c:when>

    </c:choose>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">
            <c:out value="${errorMessage}" />
        </div>
    </c:if>

    <section class="card">

        <form method="get"
            action="${pageContext.request.contextPath}/treatments"
            class="search-form">

            <input type="hidden"
                name="action"
                value="search">

            <div class="form-group">
                <label for="searchTerm">
                    Search Treatment Records
                </label>

                <input type="text"
                    id="searchTerm"
                    name="searchTerm"
                    value="<c:out value='${param.searchTerm}' />"
                    placeholder="Search patient, dentist, diagnosis or appointment number">
            </div>

            <div class="form-actions">

                <button class="btn btn-primary"
                    type="submit">
                    Search
                </button>

                <a class="btn btn-secondary"
                    href="${pageContext.request.contextPath}/treatments">
                    Clear
                </a>

            </div>

        </form>

    </section>

    <section class="card">

        <c:choose>

            <c:when test="${empty treatments}">

                <div class="empty-message">
                    <h3>No treatment records found</h3>

                    <p>
                        There are currently no treatment
                        records to display.
                    </p>

                    <a class="btn btn-primary"
                        href="${pageContext.request.contextPath}/treatments?action=new">
                        Add First Treatment
                    </a>
                </div>

            </c:when>

            <c:otherwise>

                <div class="table-responsive">

                    <table class="data-table">

                        <thead>
                            <tr>
                                <th>Appointment</th>
                                <th>Patient</th>
                                <th>Dentist</th>
                                <th>Date</th>
                                <th>Diagnosis</th>
                                <th>Treatment Notes</th>
                                <th>Prescription</th>
                                <th>Actions</th>
                            </tr>
                        </thead>

                        <tbody>

                            <c:forEach var="treatment"
                                items="${treatments}">

                                <tr>
                                    <td>
                                        <strong>
                                            <c:out
                                                value="${treatment.appointmentNumber}" />
                                        </strong>
                                    </td>

                                    <td>
                                        <c:out
                                            value="${treatment.patientName}" />
                                    </td>

                                    <td>
                                        <c:out
                                            value="${treatment.dentistName}" />
                                    </td>

                                    <td>
                                        <c:out
                                            value="${treatment.treatmentDate}" />
                                    </td>

                                    <td>
                                        <c:choose>

                                            <c:when test="${empty treatment.diagnosis}">
                                                Not specified
                                            </c:when>

                                            <c:otherwise>
                                                <c:out
                                                    value="${treatment.diagnosis}" />
                                            </c:otherwise>

                                        </c:choose>
                                    </td>

                                    <td>
                                        <c:out
                                            value="${treatment.treatmentNotes}" />
                                    </td>

                                    <td>
                                        <c:choose>

                                            <c:when test="${empty treatment.prescription}">
                                                No prescription
                                            </c:when>

                                            <c:otherwise>
                                                <c:out
                                                    value="${treatment.prescription}" />
                                            </c:otherwise>

                                        </c:choose>
                                    </td>

                                    <td>
                                        <div class="action-buttons">

                                            <a class="btn btn-small btn-primary"
                                                href="${pageContext.request.contextPath}/treatment-details?treatmentId=${treatment.treatmentId}">
                                                Manage Charges
                                            </a>

                                            <a class="btn btn-small btn-secondary"
                                                href="${pageContext.request.contextPath}/treatments?action=edit&id=${treatment.treatmentId}">
                                                Edit
                                            </a>

                                            <form method="post"
                                                action="${pageContext.request.contextPath}/treatments"
                                                class="inline-form"
                                                onsubmit="return confirm('Are you sure you want to delete this treatment record?');">

                                                <input type="hidden"
                                                    name="action"
                                                    value="delete">

                                                <input type="hidden"
                                                    name="treatmentId"
                                                    value="${treatment.treatmentId}">

                                                <button class="btn btn-small btn-danger"
                                                    type="submit">
                                                    Delete
                                                </button>

                                            </form>

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
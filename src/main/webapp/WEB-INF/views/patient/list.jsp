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
        Patient Management | Sunrise Dental Clinic
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

                <a class="nav-link active"
                    href="${pageContext.request.contextPath}/patients">
                    Patients
                </a>

                <a class="nav-link"
                    href="#">
                    Appointments
                </a>

                <a class="nav-link"
                    href="#">
                    Treatments
                </a>

                <a class="nav-link"
                    href="#">
                    Billing
                </a>

            </nav>

        </div>

    </header>


    <!-- Main Page -->
    <main class="page-container">

        <section class="page-header">

            <div>

                <h1 class="page-title">
                    Patient Management
                </h1>

                <p class="page-description">
                    Register, search, update and manage
                    dental clinic patient records.
                </p>

            </div>

            <a class="btn btn-primary"
                href="${pageContext.request.contextPath}/patients?action=new">

                <span>＋</span>
                Register New Patient

            </a>

        </section>


        <!-- Success Messages -->
        <c:if test="${param.success == 'registered'}">

            <div class="alert alert-success">
                Patient registered successfully.
            </div>

        </c:if>

        <c:if test="${param.success == 'updated'}">

            <div class="alert alert-success">
                Patient information updated successfully.
            </div>

        </c:if>

        <c:if test="${param.success == 'deleted'}">

            <div class="alert alert-success">
                Patient record deleted successfully.
            </div>

        </c:if>


        <!-- Error Message -->
        <c:if test="${not empty errorMessage}">

            <div class="alert alert-error">
                <c:out value="${errorMessage}" />
            </div>

        </c:if>


        <!-- Search and Patient Table -->
        <section class="card">

            <div class="card-body">

                <div class="search-toolbar">

                    <form class="search-form"
                        method="get"
                        action="${pageContext.request.contextPath}/patients">

                        <input type="hidden"
                            name="action"
                            value="search">

                        <input class="search-input"
                            type="search"
                            name="searchTerm"
                            value="<c:out value='${searchTerm}' />"
                            placeholder="Search by patient number, name, NIC or phone">

                        <button class="btn btn-secondary"
                            type="submit">

                            Search

                        </button>

                        <c:if test="${not empty searchTerm}">

                            <a class="btn btn-outline"
                                href="${pageContext.request.contextPath}/patients">

                                Clear

                            </a>

                        </c:if>

                    </form>

                </div>


                <c:choose>

                    <c:when test="${not empty patients}">

                        <div class="table-responsive">

                            <table class="data-table">

                                <thead>

                                    <tr>
                                        <th>Patient Number</th>
                                        <th>Patient Name</th>
                                        <th>Date of Birth</th>
                                        <th>Gender</th>
                                        <th>Phone</th>
                                        <th>Status</th>
                                        <th>Actions</th>
                                    </tr>

                                </thead>

                                <tbody>

                                    <c:forEach var="patient"
                                        items="${patients}">

                                        <tr>

                                            <td>
                                                <span class="patient-number">

                                                    <c:out
                                                        value="${patient.patientNumber}" />

                                                </span>
                                            </td>

                                            <td>
                                                <span class="patient-name">

                                                    <c:out
                                                        value="${patient.fullName}" />

                                                </span>

                                                <c:if test="${not empty patient.nicNumber}">

                                                    <div class="form-help">

                                                        NIC:
                                                        <c:out
                                                            value="${patient.nicNumber}" />

                                                    </div>

                                                </c:if>
                                            </td>

                                            <td>
                                                <c:out
                                                    value="${patient.dateOfBirth}" />
                                            </td>

                                            <td>
                                                <c:out
                                                    value="${patient.gender}" />
                                            </td>

                                            <td>
                                                <c:out
                                                    value="${patient.phone}" />
                                            </td>

                                            <td>

                                                <c:choose>

                                                    <c:when test="${patient.active}">

                                                        <span class="badge badge-active">
                                                            Active
                                                        </span>

                                                    </c:when>

                                                    <c:otherwise>

                                                        <span class="badge badge-inactive">
                                                            Inactive
                                                        </span>

                                                    </c:otherwise>

                                                </c:choose>

                                            </td>

                                            <td>

                                                <div class="table-actions">

                                                    <a class="btn btn-outline btn-small"
                                                        href="${pageContext.request.contextPath}/patients?action=edit&id=${patient.patientId}">

                                                        Edit

                                                    </a>

                                                    <form class="inline-form"
                                                        method="post"
                                                        action="${pageContext.request.contextPath}/patients"
                                                        onsubmit="return confirmPatientDelete();">

                                                        <input type="hidden"
                                                            name="action"
                                                            value="delete">

                                                        <input type="hidden"
                                                            name="patientId"
                                                            value="${patient.patientId}">

                                                        <button class="btn btn-danger btn-small"
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

                    </c:when>

                    <c:otherwise>

                        <div class="empty-state">

                            <div class="empty-state-icon">
                                🗂️
                            </div>

                            <h2 class="empty-state-title">
                                No patients found
                            </h2>

                            <p class="empty-state-text">

                                <c:choose>

                                    <c:when test="${not empty searchTerm}">
                                        No patient records matched
                                        your search term.
                                    </c:when>

                                    <c:otherwise>
                                        Register the first patient
                                        to begin managing clinic records.
                                    </c:otherwise>

                                </c:choose>

                            </p>

                            <a class="btn btn-primary"
                                href="${pageContext.request.contextPath}/patients?action=new">

                                Register New Patient

                            </a>

                        </div>

                    </c:otherwise>

                </c:choose>

            </div>

        </section>

    </main>


    <!-- Footer -->
    <footer class="footer">

        Sunrise Dental Clinic
        &copy; 2026 |
        CIS6003 Advanced Programming Assignment

    </footer>


    <script>

        function confirmPatientDelete() {

            return window.confirm(
                "Are you sure you want to delete this patient record?"
            );
        }

    </script>

</body>

</html>
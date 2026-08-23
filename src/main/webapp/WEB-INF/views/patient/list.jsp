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
        Patient Management - Sunrise Dental Clinic
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

            <a href="${pageContext.request.contextPath}/patients"
               class="active">
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

            <a href="${pageContext.request.contextPath}/help">
                Help
            </a>

        </nav>

    </div>

</header>

<main class="container">

    <section class="page-header">

        <div>

            <h2>Patient Management</h2>

            <p>
                Register, search, update and manage
                dental clinic patient records.
            </p>

        </div>

        <c:if test="${sessionScope.userRole == 'RECEPTIONIST'}">

            <a class="btn btn-primary"
               href="${pageContext.request.contextPath}/patients?action=new">

                Register New Patient

            </a>

        </c:if>

    </section>

    <!-- Success messages -->

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

            Patient record deactivated successfully.

        </div>

    </c:if>

    <!-- Error message -->

    <c:if test="${not empty errorMessage}">

        <div class="alert alert-error">

            <c:out value="${errorMessage}"/>

        </div>

    </c:if>

    <!-- Search form -->

    <section class="card">

        <form method="get"
              action="${pageContext.request.contextPath}/patients"
              class="search-form">

            <input type="hidden"
                   name="action"
                   value="search">

            <div class="form-group">

                <label for="searchTerm">

                    Search Patients

                </label>

                <input type="search"
                       id="searchTerm"
                       name="searchTerm"
                       value="<c:out value='${searchTerm}'/>"
                       placeholder="Patient number, name, NIC or phone">

            </div>

            <div class="form-actions">

                <button type="submit"
                        class="btn btn-primary">

                    Search

                </button>

                <a class="btn btn-secondary"
                   href="${pageContext.request.contextPath}/patients">

                    Clear

                </a>

            </div>

        </form>

    </section>

    <!-- Patient records -->

    <section class="card">

        <c:choose>

            <c:when test="${empty patients}">

                <div class="empty-message">

                    <h3>No patients found</h3>

                    <p>

                        <c:choose>

                            <c:when test="${not empty searchTerm}">

                                No patient records matched
                                your search term.

                            </c:when>

                            <c:otherwise>

                                There are currently no patient
                                records to display.

                            </c:otherwise>

                        </c:choose>

                    </p>

                    <c:if test="${sessionScope.userRole == 'RECEPTIONIST'}">

                        <a class="btn btn-primary"
                           href="${pageContext.request.contextPath}/patients?action=new">

                            Register First Patient

                        </a>

                    </c:if>

                </div>

            </c:when>

            <c:otherwise>

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

                                        <c:out value="${patient.patientNumber}"/>

                                    </span>

                                </td>

                                <td>

                                    <span class="patient-name">

                                        <c:out value="${patient.fullName}"/>

                                    </span>

                                    <c:if test="${not empty patient.nicNumber}">

                                        <span class="form-help">

                                            NIC:
                                            <c:out value="${patient.nicNumber}"/>

                                        </span>

                                    </c:if>

                                </td>

                                <td>

                                    <c:out value="${patient.dateOfBirth}"/>

                                </td>

                                <td>

                                    <c:out value="${patient.gender}"/>

                                </td>

                                <td>

                                    <c:out value="${patient.phone}"/>

                                </td>

                                <td>

                                    <c:choose>

                                        <c:when test="${patient.active}">

                                            <span class="status-badge status-active">
                                                Active
                                            </span>

                                        </c:when>

                                        <c:otherwise>

                                            <span class="status-badge status-inactive">
                                                Inactive
                                            </span>

                                        </c:otherwise>

                                    </c:choose>

                                </td>

                                <td>

                                    <div class="action-buttons">

                                        <c:choose>

                                            <c:when test="${sessionScope.userRole == 'RECEPTIONIST'}">

                                                <a class="btn btn-small btn-secondary"
                                                   href="${pageContext.request.contextPath}/patients?action=edit&id=${patient.patientId}">

                                                    Edit

                                                </a>

                                                <form method="post"
                                                      action="${pageContext.request.contextPath}/patients"
                                                      class="inline-form"
                                                      onsubmit="return confirmPatientDelete();">

                                                    <input type="hidden"
                                                           name="action"
                                                           value="delete">

                                                    <input type="hidden"
                                                           name="patientId"
                                                           value="${patient.patientId}">

                                                    <button type="submit"
                                                            class="btn btn-small btn-danger">

                                                        Deactivate

                                                    </button>

                                                </form>

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

    function confirmPatientDelete() {

        return window.confirm(
            "Are you sure you want to deactivate "
            + "this patient record?"
        );
    }

</script>

</body>

</html>
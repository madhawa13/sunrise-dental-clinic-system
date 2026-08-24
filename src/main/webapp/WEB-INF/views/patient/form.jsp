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

    <c:choose>
        <c:when test="${not empty patient.patientId}">
            <title>
                Edit Patient | Sunrise Dental Clinic
            </title>
        </c:when>

        <c:otherwise>
            <title>
                Register Patient | Sunrise Dental Clinic
            </title>
        </c:otherwise>
    </c:choose>

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

            <c:choose>
                <c:when test="${not empty patient.patientId}">

                    <h2>Edit Patient</h2>

                    <p>
                        Update the patient's personal, contact
                        and medical information.
                    </p>

                </c:when>

                <c:otherwise>

                    <h2>Register New Patient</h2>

                    <p>
                        Enter the patient's personal, contact
                        and medical information.
                    </p>

                </c:otherwise>
            </c:choose>

        </div>

        <a class="btn btn-secondary"
           href="${pageContext.request.contextPath}/patients">
            Back to Patient List
        </a>

    </section>

    <c:if test="${not empty errorMessage}">

        <div class="alert alert-danger">
            <c:out value="${errorMessage}"/>
        </div>

    </c:if>

    <section class="card form-card">

        <c:choose>
            <c:when test="${not empty patient.patientId}">
                <h3>Edit Patient Information</h3>
            </c:when>

            <c:otherwise>
                <h3>New Patient Information</h3>
            </c:otherwise>
        </c:choose>

        <form method="post"
              action="${pageContext.request.contextPath}/patients">

            <c:choose>
                <c:when test="${not empty patient.patientId}">

                    <input type="hidden"
                           name="action"
                           value="update">

                    <input type="hidden"
                           name="patientId"
                           value="${patient.patientId}">

                    <input type="hidden"
                           name="patientNumber"
                           value="<c:out value='${patient.patientNumber}'/>">

                </c:when>

                <c:otherwise>

                    <input type="hidden"
                           name="action"
                           value="register">

                </c:otherwise>
            </c:choose>

            <div class="form-grid">

                <div class="form-group">

                    <label for="firstName">
                        First Name
                        <span class="required">*</span>
                    </label>

                    <input type="text"
                           id="firstName"
                           name="firstName"
                           maxlength="50"
                           value="<c:out value='${patient.firstName}'/>"
                           required>

                </div>

                <div class="form-group">

                    <label for="lastName">
                        Last Name
                        <span class="required">*</span>
                    </label>

                    <input type="text"
                           id="lastName"
                           name="lastName"
                           maxlength="50"
                           value="<c:out value='${patient.lastName}'/>"
                           required>

                </div>

                <div class="form-group">

                    <label for="dateOfBirth">
                        Date of Birth
                        <span class="required">*</span>
                    </label>

                    <input type="date"
                           id="dateOfBirth"
                           name="dateOfBirth"
                           value="${patient.dateOfBirth}"
                           required>

                </div>

                <div class="form-group">

                    <label for="gender">
                        Gender
                        <span class="required">*</span>
                    </label>

                    <select id="gender"
                            name="gender"
                            required>

                        <option value="">
                            Select gender
                        </option>

                        <option value="MALE"
                            <c:if test="${patient.gender == 'MALE'}">
                                selected
                            </c:if>>
                            Male
                        </option>

                        <option value="FEMALE"
                            <c:if test="${patient.gender == 'FEMALE'}">
                                selected
                            </c:if>>
                            Female
                        </option>

                        <option value="OTHER"
                            <c:if test="${patient.gender == 'OTHER'}">
                                selected
                            </c:if>>
                            Other
                        </option>

                    </select>

                </div>

                <div class="form-group">

                    <label for="nicNumber">
                        NIC Number
                    </label>

                    <input type="text"
                           id="nicNumber"
                           name="nicNumber"
                           maxlength="20"
                           value="<c:out value='${patient.nicNumber}'/>"
                           placeholder="Example: 901234567V">

                    <small class="form-help">
                        Enter the patient's NIC number when available.
                    </small>

                </div>

                <div class="form-group">

                    <label for="phone">
                        Phone Number
                        <span class="required">*</span>
                    </label>

                    <input type="tel"
                           id="phone"
                           name="phone"
                           maxlength="20"
                           value="<c:out value='${patient.phone}'/>"
                           placeholder="Example: 0771234567"
                           required>

                </div>

                <div class="form-group form-group-full">

                    <label for="email">
                        Email Address
                    </label>

                    <input type="email"
                           id="email"
                           name="email"
                           maxlength="100"
                           value="<c:out value='${patient.email}'/>"
                           placeholder="Example: patient@example.com">

                </div>

                <div class="form-group form-group-full">

                    <label for="address">
                        Residential Address
                    </label>

                    <textarea id="address"
                              name="address"
                              maxlength="255"
                              placeholder="Enter the patient's address"><c:out value="${patient.address}"/></textarea>

                </div>

                <div class="form-group form-group-full">

                    <label for="medicalNotes">
                        Medical Notes
                    </label>

                    <textarea id="medicalNotes"
                              name="medicalNotes"
                              placeholder="Enter allergies, health conditions or other important notes"><c:out value="${patient.medicalNotes}"/></textarea>

                    <small class="form-help">
                        Record allergies and medical conditions
                        that may affect dental treatment.
                    </small>

                </div>

                <c:if test="${not empty patient.patientId}">

                    <div class="form-group form-group-full">

                        <label for="active">
                            Patient Status
                        </label>

                        <select id="active"
                                name="active">

                            <option value="true"
                                <c:if test="${patient.active}">
                                    selected
                                </c:if>>
                                Active
                            </option>

                            <option value="false"
                                <c:if test="${not patient.active}">
                                    selected
                                </c:if>>
                                Inactive
                            </option>

                        </select>

                    </div>

                </c:if>

            </div>

            <div class="form-actions">

                <a class="btn btn-secondary"
                   href="${pageContext.request.contextPath}/patients">
                    Cancel
                </a>

                <c:choose>
                    <c:when test="${not empty patient.patientId}">

                        <button type="submit"
                                class="btn btn-primary">
                            Update Patient
                        </button>

                    </c:when>

                    <c:otherwise>

                        <button type="submit"
                                class="btn btn-primary">
                            Register Patient
                        </button>

                    </c:otherwise>
                </c:choose>

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
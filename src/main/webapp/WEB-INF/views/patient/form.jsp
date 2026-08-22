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
                Update Patient | Sunrise Dental Clinic
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

                <c:choose>

                    <c:when test="${not empty patient.patientId}">

                        <h1 class="page-title">
                            Update Patient
                        </h1>

                        <p class="page-description">
                            Edit the selected patient's
                            personal and medical information.
                        </p>

                    </c:when>

                    <c:otherwise>

                        <h1 class="page-title">
                            Register New Patient
                        </h1>

                        <p class="page-description">
                            Enter the patient's personal,
                            contact and medical information.
                        </p>

                    </c:otherwise>

                </c:choose>

            </div>

            <a class="btn btn-outline"
                href="${pageContext.request.contextPath}/patients">

                Back to Patient List

            </a>

        </section>


        <!-- Validation Error -->
        <c:if test="${not empty errorMessage}">

            <div class="alert alert-error">

                <c:out value="${errorMessage}" />

            </div>

        </c:if>


        <!-- Patient Form -->
        <section class="card">

            <div class="card-header">

                <h2 class="card-title">

                    <c:choose>

                        <c:when test="${not empty patient.patientId}">
                            Patient Information
                        </c:when>

                        <c:otherwise>
                            New Patient Information
                        </c:otherwise>

                    </c:choose>

                </h2>

            </div>


            <div class="card-body">

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
                                value="<c:out value='${patient.patientNumber}' />">

                        </c:when>

                        <c:otherwise>

                            <input type="hidden"
                                name="action"
                                value="register">

                        </c:otherwise>

                    </c:choose>


                    <div class="form-grid">

                        <!-- Patient Number -->
                        <c:if test="${not empty patient.patientId}">

                            <div class="form-group form-group-full">

                                <label class="form-label">
                                    Patient Number
                                </label>

                                <input class="form-control"
                                    type="text"
                                    value="<c:out value='${patient.patientNumber}' />"
                                    readonly>

                                <span class="form-help">
                                    The patient number is generated
                                    automatically and cannot be changed.
                                </span>

                            </div>

                        </c:if>


                        <!-- First Name -->
                        <div class="form-group">

                            <label class="form-label"
                                for="firstName">

                                First Name
                                <span class="required">*</span>

                            </label>

                            <input class="form-control"
                                id="firstName"
                                type="text"
                                name="firstName"
                                value="<c:out value='${patient.firstName}' />"
                                maxlength="50"
                                autocomplete="given-name"
                                required>

                        </div>


                        <!-- Last Name -->
                        <div class="form-group">

                            <label class="form-label"
                                for="lastName">

                                Last Name
                                <span class="required">*</span>

                            </label>

                            <input class="form-control"
                                id="lastName"
                                type="text"
                                name="lastName"
                                value="<c:out value='${patient.lastName}' />"
                                maxlength="50"
                                autocomplete="family-name"
                                required>

                        </div>


                        <!-- Date of Birth -->
                        <div class="form-group">

                            <label class="form-label"
                                for="dateOfBirth">

                                Date of Birth
                                <span class="required">*</span>

                            </label>

                            <input class="form-control"
                                id="dateOfBirth"
                                type="date"
                                name="dateOfBirth"
                                value="${patient.dateOfBirth}"
                                required>

                        </div>


                        <!-- Gender -->
                        <div class="form-group">

                            <label class="form-label"
                                for="gender">

                                Gender
                                <span class="required">*</span>

                            </label>

                            <select class="form-control"
                                id="gender"
                                name="gender"
                                required>

                                <option value="">
                                    Select gender
                                </option>

                                <option value="MALE"
                                    ${patient.gender == 'MALE'
                                        ? 'selected' : ''}>
                                    Male
                                </option>

                                <option value="FEMALE"
                                    ${patient.gender == 'FEMALE'
                                        ? 'selected' : ''}>
                                    Female
                                </option>

                                <option value="OTHER"
                                    ${patient.gender == 'OTHER'
                                        ? 'selected' : ''}>
                                    Other
                                </option>

                            </select>

                        </div>


                        <!-- NIC Number -->
                        <div class="form-group">

                            <label class="form-label"
                                for="nicNumber">

                                NIC Number

                            </label>

                            <input class="form-control"
                                id="nicNumber"
                                type="text"
                                name="nicNumber"
                                value="<c:out value='${patient.nicNumber}' />"
                                maxlength="20"
                                placeholder="Example: 901234567V">

                        </div>


                        <!-- Phone -->
                        <div class="form-group">

                            <label class="form-label"
                                for="phone">

                                Phone Number
                                <span class="required">*</span>

                            </label>

                            <input class="form-control"
                                id="phone"
                                type="tel"
                                name="phone"
                                value="<c:out value='${patient.phone}' />"
                                maxlength="15"
                                autocomplete="tel"
                                placeholder="Example: 0771234567"
                                required>

                        </div>


                        <!-- Email -->
                        <div class="form-group form-group-full">

                            <label class="form-label"
                                for="email">

                                Email Address

                            </label>

                            <input class="form-control"
                                id="email"
                                type="email"
                                name="email"
                                value="<c:out value='${patient.email}' />"
                                maxlength="100"
                                autocomplete="email"
                                placeholder="Example: patient@example.com">

                        </div>


                        <!-- Address -->
                        <div class="form-group form-group-full">

                            <label class="form-label"
                                for="address">

                                Residential Address

                            </label>

                            <textarea class="form-control"
                                id="address"
                                name="address"
                                maxlength="255"
                                autocomplete="street-address"
                                placeholder="Enter the patient's address"><c:out value="${patient.address}" /></textarea>

                        </div>


                        <!-- Medical Notes -->
                        <div class="form-group form-group-full">

                            <label class="form-label"
                                for="medicalNotes">

                                Medical Notes

                            </label>

                            <textarea class="form-control"
                                id="medicalNotes"
                                name="medicalNotes"
                                placeholder="Enter allergies, health conditions or other important notes"><c:out value="${patient.medicalNotes}" /></textarea>

                            <span class="form-help">
                                Record allergies and medical conditions
                                that may affect dental treatment.
                            </span>

                        </div>

                    </div>


                    <!-- Form Buttons -->
                    <div class="form-actions">

                        <a class="btn btn-outline"
                            href="${pageContext.request.contextPath}/patients">

                            Cancel

                        </a>

                        <button class="btn btn-primary"
                            type="submit">

                            <c:choose>

                                <c:when test="${not empty patient.patientId}">
                                    Update Patient
                                </c:when>

                                <c:otherwise>
                                    Register Patient
                                </c:otherwise>

                            </c:choose>

                        </button>

                    </div>

                </form>

            </div>

        </section>

    </main>


    <!-- Footer -->
    <footer class="footer">

        Sunrise Dental Clinic
        &copy; 2026 |
        CIS6003 Advanced Programming Assignment

    </footer>

</body>

</html>
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
        Create Bill - Sunrise Dental Clinic
    </title>

    <link rel="stylesheet"
        href="${pageContext.request.contextPath}/assets/css/style.css">
</head>

<body>

<header class="main-header">
    <div class="container">

        <h1>Sunrise Dental Clinic</h1>

        <p>
            Create Patient Treatment Bill
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

            <a href="${pageContext.request.contextPath}/treatments">
                Treatments
            </a>

            <a class="active"
                href="${pageContext.request.contextPath}/bills">
                Billing
            </a>

        </nav>
    </div>
</header>

<main class="container">

    <section class="page-header">

        <div>
            <h2>Create Treatment Bill</h2>

            <p>
                Select the completed appointment using
                its unique appointment number.
            </p>
        </div>

        <a class="btn btn-secondary"
            href="${pageContext.request.contextPath}/bills">
            Back to Bills
        </a>

    </section>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error">
            <c:out value="${errorMessage}" />
        </div>
    </c:if>

    <section class="card form-card">

        <form method="post"
            action="${pageContext.request.contextPath}/bills">

            <input type="hidden"
                name="action"
                value="create">

            <div class="form-group">

                <label for="appointmentId">
                    Completed Appointment
                    <span class="required">*</span>
                </label>

                <select id="appointmentId"
                    name="appointmentId"
                    required>

                    <option value="">
                        Select appointment number
                    </option>

                    <c:forEach var="appointment"
                        items="${appointments}">

                        <c:if test="${appointment.status == 'COMPLETED'}">

                            <option
                                value="${appointment.appointmentId}"
                                <c:if test="${selectedAppointmentId == appointment.appointmentId}">
                                    selected
                                </c:if>>

                                <c:out
                                    value="${appointment.appointmentNumber}" />

                                -

                                <c:out
                                    value="${appointment.patientName}" />

                                -

                                <c:out
                                    value="${appointment.appointmentDate}" />

                                (${appointment.status})

                            </option>

                        </c:if>

                    </c:forEach>

                </select>

                <small class="form-help">
                    The full appointment number is displayed.
                    The internal database ID is selected
                    automatically.
                </small>

            </div>

            <div class="form-group">

                <label for="discount">
                    Discount Amount (Rs.)
                </label>

                <input type="number"
                    id="discount"
                    name="discount"
                    min="0"
                    step="0.01"
                    value="0.00">

                <small class="form-help">
                    Leave this as 0.00 when no
                    discount is provided.
                </small>

            </div>

            <div class="information-box">

                <strong>
                    Automatic bill calculation
                </strong>

                <p>
                    The selected appointment's treatment
                    charges are calculated using
                    quantity × standard unit price.
                    The discount is then deducted
                    from the subtotal.
                </p>

            </div>

            <div class="form-actions">

                <button class="btn btn-primary"
                    type="submit">
                    Calculate and Create Bill
                </button>

                <a class="btn btn-secondary"
                    href="${pageContext.request.contextPath}/bills">
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
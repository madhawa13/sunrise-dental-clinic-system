<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>

<%@ taglib prefix="c"
    uri="jakarta.tags.core" %>

<%@ taglib prefix="fmt"
    uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">

    <meta name="viewport"
        content="width=device-width, initial-scale=1.0">

    <title>
        Treatment Charges - Sunrise Dental Clinic
    </title>

    <link rel="stylesheet"
        href="${pageContext.request.contextPath}/assets/css/style.css">
</head>

<body>

<header class="main-header">
    <div class="container">

        <h1>Sunrise Dental Clinic</h1>

        <p>
            Treatment Charge Management
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
            <h2>Manage Treatment Charges</h2>

            <p>
                Treatment ID:
                <strong>
                    <c:out value="${treatment.treatmentId}" />
                </strong>

                &nbsp; | &nbsp;

                Appointment ID:
                <strong>
                    <c:out value="${treatment.appointmentId}" />
                </strong>
            </p>
        </div>

        <a class="btn btn-secondary"
            href="${pageContext.request.contextPath}/treatments">
            Back to Treatments
        </a>
    </section>

    <c:if test="${param.success == 'added'}">
        <div class="alert alert-success">
            Treatment charge added successfully.
        </div>
    </c:if>

    <c:if test="${param.success == 'deleted'}">
        <div class="alert alert-success">
            Treatment charge removed successfully.
        </div>
    </c:if>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error">
            <c:out value="${errorMessage}" />
        </div>
    </c:if>

    <section class="billing-summary-grid">

        <article class="card summary-card">
            <span class="summary-label">
                Treatment ID
            </span>

            <strong class="summary-value">
                <c:out value="${treatment.treatmentId}" />
            </strong>
        </article>

        <article class="card summary-card">
            <span class="summary-label">
                Appointment ID
            </span>

            <strong class="summary-value">
                <c:out value="${treatment.appointmentId}" />
            </strong>
        </article>

        <article class="card summary-card">
            <span class="summary-label">
                Current Treatment Total
            </span>

            <strong class="summary-value">
                Rs.
                <fmt:formatNumber
                    value="${treatmentTotal}"
                    minFractionDigits="2"
                    maxFractionDigits="2" />
            </strong>
        </article>

    </section>

    <section class="card form-card">

        <h3>Add Standard Treatment Charge</h3>

        <p>
            Select a standard treatment type.
            Its current standard price will be
            added automatically.
        </p>

        <form method="post"
            action="${pageContext.request.contextPath}/treatment-details">

            <input type="hidden"
                name="action"
                value="add">

            <input type="hidden"
                name="treatmentId"
                value="${treatment.treatmentId}">

            <div class="form-grid">

                <div class="form-group">
                    <label for="chargeId">
                        Treatment Charge
                        <span class="required">*</span>
                    </label>

                    <select id="chargeId"
                        name="chargeId"
                        required>

                        <option value="">
                            Select a treatment charge
                        </option>

                        <c:forEach var="charge"
                            items="${charges}">

                            <option value="${charge.chargeId}">

                                <c:out
                                    value="${charge.treatmentCode}" />

                                -

                                <c:out
                                    value="${charge.treatmentName}" />

                                (Rs.
                                <fmt:formatNumber
                                    value="${charge.standardCharge}"
                                    minFractionDigits="2"
                                    maxFractionDigits="2" />)

                            </option>

                        </c:forEach>

                    </select>
                </div>

                <div class="form-group">
                    <label for="quantity">
                        Quantity
                        <span class="required">*</span>
                    </label>

                    <input type="number"
                        id="quantity"
                        name="quantity"
                        min="1"
                        value="1"
                        required>
                </div>

                <div class="form-group form-group-full">
                    <label for="notes">
                        Charge Notes
                    </label>

                    <textarea id="notes"
                        name="notes"
                        rows="3"
                        maxlength="255"
                        placeholder="Optional charge information"></textarea>
                </div>

            </div>

            <div class="form-actions">

                <button class="btn btn-primary"
                    type="submit">
                    Add Treatment Charge
                </button>

                <a class="btn btn-secondary"
                    href="${pageContext.request.contextPath}/treatments">
                    Cancel
                </a>

            </div>

        </form>

    </section>

    <section class="card">

        <div class="section-header">

            <div>
                <h3>Assigned Charge Items</h3>

                <p>
                    These items will be used to
                    calculate the patient bill.
                </p>
            </div>

        </div>

        <c:choose>

            <c:when test="${empty details}">

                <div class="empty-state">
                    <h3>No treatment charges added</h3>

                    <p>
                        Select a standard treatment
                        charge using the form above.
                    </p>
                </div>

            </c:when>

            <c:otherwise>

                <div class="table-responsive">

                    <table class="data-table">

                        <thead>
                            <tr>
                                <th>Detail ID</th>
                                <th>Charge ID</th>
                                <th>Quantity</th>
                                <th>Unit Price</th>
                                <th>Line Total</th>
                                <th>Notes</th>
                                <th>Action</th>
                            </tr>
                        </thead>

                        <tbody>

                            <c:forEach var="detail"
                                items="${details}">

                                <tr>
                                    <td>
                                        <c:out
                                            value="${detail.treatmentDetailId}" />
                                    </td>

                                    <td>
                                        <c:out
                                            value="${detail.chargeId}" />
                                    </td>

                                    <td>
                                        <c:out
                                            value="${detail.quantity}" />
                                    </td>

                                    <td>
                                        Rs.
                                        <fmt:formatNumber
                                            value="${detail.unitPrice}"
                                            minFractionDigits="2"
                                            maxFractionDigits="2" />
                                    </td>

                                    <td>
                                        Rs.
                                        <fmt:formatNumber
                                            value="${detail.unitPrice * detail.quantity}"
                                            minFractionDigits="2"
                                            maxFractionDigits="2" />
                                    </td>

                                    <td>
                                        <c:out
                                            value="${detail.notes}" />
                                    </td>

                                    <td>
                                        <form method="post"
                                            action="${pageContext.request.contextPath}/treatment-details"
                                            onsubmit="return confirm('Remove this treatment charge?');">

                                            <input type="hidden"
                                                name="action"
                                                value="delete">

                                            <input type="hidden"
                                                name="treatmentDetailId"
                                                value="${detail.treatmentDetailId}">

                                            <input type="hidden"
                                                name="treatmentId"
                                                value="${treatment.treatmentId}">

                                            <button class="btn btn-danger btn-small"
                                                type="submit">
                                                Remove
                                            </button>

                                        </form>
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
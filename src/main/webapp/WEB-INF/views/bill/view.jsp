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
        Bill Details - Sunrise Dental Clinic
    </title>

    <link rel="stylesheet"
        href="${pageContext.request.contextPath}/assets/css/style.css">
</head>

<body>

<header class="site-header">
    <div class="container">
        <h1>Sunrise Dental Clinic</h1>

        <p>Patient Billing and Payments</p>

        <nav class="main-navigation">
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

<main class="container page-content">

    <div class="page-header">
        <div>
            <h2>Bill Details</h2>

            <p>
                Bill:
                <strong>
                    <c:out value="${bill.billNumber}" />
                </strong>
            </p>
        </div>

        <div class="action-buttons">

            <a class="button button-secondary"
                href="${pageContext.request.contextPath}/bills">
                Back to Bills
            </a>

            <a class="button button-primary"
                href="${pageContext.request.contextPath}/bills?action=print&id=${bill.billId}"
                target="_blank">
                Print Bill
            </a>

        </div>
    </div>

    <c:if test="${param.success == 'created'}">
        <div class="alert alert-success">
            Bill created successfully.
        </div>
    </c:if>

    <c:if test="${param.success == 'recalculated'}">
        <div class="alert alert-success">
            Bill recalculated successfully.
        </div>
    </c:if>

    <c:if test="${param.success == 'payment'}">
        <div class="alert alert-success">
            Payment recorded successfully.
        </div>
    </c:if>

    <section class="billing-summary-grid">

        <article class="card summary-card">
            <span class="summary-label">
                Subtotal
            </span>

            <strong class="summary-value">
                Rs.
                <fmt:formatNumber
                    value="${bill.subtotal}"
                    minFractionDigits="2"
                    maxFractionDigits="2" />
            </strong>
        </article>

        <article class="card summary-card">
            <span class="summary-label">
                Discount
            </span>

            <strong class="summary-value">
                Rs.
                <fmt:formatNumber
                    value="${bill.discount}"
                    minFractionDigits="2"
                    maxFractionDigits="2" />
            </strong>
        </article>

        <article class="card summary-card">
            <span class="summary-label">
                Total Amount
            </span>

            <strong class="summary-value">
                Rs.
                <fmt:formatNumber
                    value="${bill.totalAmount}"
                    minFractionDigits="2"
                    maxFractionDigits="2" />
            </strong>
        </article>

        <article class="card summary-card">
            <span class="summary-label">
                Total Paid
            </span>

            <strong class="summary-value">
                Rs.
                <fmt:formatNumber
                    value="${totalPaid}"
                    minFractionDigits="2"
                    maxFractionDigits="2" />
            </strong>
        </article>

        <article class="card summary-card">
            <span class="summary-label">
                Balance
            </span>

            <strong class="summary-value">
                Rs.
                <fmt:formatNumber
                    value="${balance}"
                    minFractionDigits="2"
                    maxFractionDigits="2" />
            </strong>
        </article>

        <article class="card summary-card">
            <span class="summary-label">
                Payment Status
            </span>

            <strong class="status-badge">
                <c:out value="${bill.paymentStatus}" />
            </strong>
        </article>

    </section>

    <section class="card">
        <div class="section-header">
            <div>
                <h3>Bill Information</h3>
            </div>

            <c:if test="${bill.paymentStatus != 'PAID'}">
                <a class="button button-primary"
                    href="${pageContext.request.contextPath}/payments?action=new&billId=${bill.billId}">
                    Record Payment
                </a>
            </c:if>
        </div>

        <div class="details-grid">

            <div class="detail-item">
                <span>Bill ID</span>
                <strong>
                    <c:out value="${bill.billId}" />
                </strong>
            </div>

            <div class="detail-item">
                <span>Appointment ID</span>
                <strong>
                    <c:out value="${bill.appointmentId}" />
                </strong>
            </div>

            <div class="detail-item">
                <span>Bill Number</span>
                <strong>
                    <c:out value="${bill.billNumber}" />
                </strong>
            </div>

            <div class="detail-item">
                <span>Created At</span>
                <strong>
                    <c:out value="${bill.createdAt}" />
                </strong>
            </div>

        </div>
    </section>

    <section class="card">
        <div class="section-header">
            <h3>Payment History</h3>
        </div>

        <c:choose>

            <c:when test="${empty payments}">
                <div class="empty-state">
                    <p>
                        No payments have been recorded
                        for this bill.
                    </p>
                </div>
            </c:when>

            <c:otherwise>
                <div class="table-responsive">
                    <table class="data-table">

                        <thead>
                            <tr>
                                <th>Payment Number</th>
                                <th>Date</th>
                                <th>Method</th>
                                <th>Reference</th>
                                <th>Received By</th>
                                <th>Amount</th>
                            </tr>
                        </thead>

                        <tbody>
                            <c:forEach var="payment"
                                items="${payments}">

                                <tr>
                                    <td>
                                        <c:out
                                            value="${payment.paymentNumber}" />
                                    </td>

                                    <td>
                                        <c:out
                                            value="${payment.paymentDate}" />
                                    </td>

                                    <td>
                                        <c:out
                                            value="${payment.paymentMethod}" />
                                    </td>

                                    <td>
                                        <c:out
                                            value="${payment.referenceNumber}" />
                                    </td>

                                    <td>
                                        <c:out
                                            value="${payment.receivedBy}" />
                                    </td>

                                    <td>
                                        Rs.
                                        <fmt:formatNumber
                                            value="${payment.amount}"
                                            minFractionDigits="2"
                                            maxFractionDigits="2" />
                                    </td>
                                </tr>

                            </c:forEach>
                        </tbody>

                    </table>
                </div>
            </c:otherwise>

        </c:choose>
    </section>

    <section class="card">
        <h3>Recalculate Bill</h3>

        <form method="post"
            action="${pageContext.request.contextPath}/bills"
            class="inline-form">

            <input type="hidden"
                name="action"
                value="recalculate">

            <input type="hidden"
                name="billId"
                value="${bill.billId}">

            <div class="form-group">
                <label for="discount">
                    Discount (Rs.)
                </label>

                <input type="number"
                    id="discount"
                    name="discount"
                    min="0"
                    step="0.01"
                    value="${bill.discount}"
                    required>
            </div>

            <button class="button button-secondary"
                type="submit">
                Recalculate
            </button>

        </form>
    </section>

</main>

<footer class="site-footer">
    <div class="container">
        <p>
            Sunrise Dental Clinic Management System
        </p>
    </div>
</footer>

</body>
</html>
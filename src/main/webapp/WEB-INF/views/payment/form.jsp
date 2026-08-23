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
        Record Payment - Sunrise Dental Clinic
    </title>

    <link rel="stylesheet"
        href="${pageContext.request.contextPath}/assets/css/style.css">
</head>

<body>

<header class="site-header">
    <div class="container">
        <h1>Sunrise Dental Clinic</h1>

        <p>Record Patient Payment</p>

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
            <h2>Record Payment</h2>

            <p>
                Bill:
                <strong>
                    <c:out value="${bill.billNumber}" />
                </strong>
            </p>
        </div>

        <a class="button button-secondary"
            href="${pageContext.request.contextPath}/bills?action=view&id=${bill.billId}">
            Back to Bill
        </a>
    </div>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error">
            <c:out value="${errorMessage}" />
        </div>
    </c:if>

    <section class="billing-summary-grid">

        <article class="card summary-card">
            <span class="summary-label">
                Bill Total
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
                Already Paid
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
                Remaining Balance
            </span>

            <strong class="summary-value">
                Rs.
                <fmt:formatNumber
                    value="${balance}"
                    minFractionDigits="2"
                    maxFractionDigits="2" />
            </strong>
        </article>

    </section>

    <section class="card form-card">

        <form method="post"
            action="${pageContext.request.contextPath}/payments">

            <input type="hidden"
                name="action"
                value="record">

            <input type="hidden"
                name="billId"
                value="${bill.billId}">

            <div class="form-grid">

                <div class="form-group">
                    <label for="amount">
                        Payment Amount (Rs.)
                        <span class="required">*</span>
                    </label>

                    <input type="number"
                        id="amount"
                        name="amount"
                        min="0.01"
                        max="${balance}"
                        step="0.01"
                        required>

                    <small>
                        Maximum payment:
                        Rs.
                        <fmt:formatNumber
                            value="${balance}"
                            minFractionDigits="2"
                            maxFractionDigits="2" />
                    </small>
                </div>

                <div class="form-group">
                    <label for="paymentMethod">
                        Payment Method
                        <span class="required">*</span>
                    </label>

                    <select id="paymentMethod"
                        name="paymentMethod"
                        required>

                        <option value="">
                            Select payment method
                        </option>

                        <option value="CASH">
                            Cash
                        </option>

                        <option value="CARD">
                            Card
                        </option>

                        <option value="BANK_TRANSFER">
                            Bank Transfer
                        </option>

                    </select>
                </div>

                <div class="form-group">
                    <label for="receivedBy">
                        Received-by User ID
                        <span class="required">*</span>
                    </label>

                    <input type="number"
                        id="receivedBy"
                        name="receivedBy"
                        min="1"
                        required>

                    <small>
                        Enter the receptionist user ID.
                        Login integration will later supply
                        this automatically.
                    </small>
                </div>

                <div class="form-group">
                    <label for="referenceNumber">
                        Reference Number
                    </label>

                    <input type="text"
                        id="referenceNumber"
                        name="referenceNumber"
                        maxlength="100"
                        placeholder="Card or bank reference">
                </div>

                <div class="form-group form-group-full">
                    <label for="notes">
                        Payment Notes
                    </label>

                    <textarea id="notes"
                        name="notes"
                        rows="4"
                        maxlength="255"
                        placeholder="Optional payment information"></textarea>
                </div>

            </div>

            <div class="information-box">
                <strong>Automatic status update</strong>

                <p>
                    After saving this payment, the bill
                    will automatically change to
                    PARTIALLY_PAID or PAID.
                </p>
            </div>

            <div class="form-actions">

                <button class="button button-primary"
                    type="submit">
                    Record Payment
                </button>

                <a class="button button-secondary"
                    href="${pageContext.request.contextPath}/bills?action=view&id=${bill.billId}">
                    Cancel
                </a>

            </div>

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
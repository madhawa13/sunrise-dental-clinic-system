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
        Bills - Sunrise Dental Clinic
    </title>

    <link rel="stylesheet"
        href="${pageContext.request.contextPath}/assets/css/style.css">
</head>

<body>

<header class="site-header">
    <div class="container">
        <h1>Sunrise Dental Clinic</h1>

        <p>
            Appointment and Patient Management System
        </p>

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
            <h2>Patient Bills</h2>

            <p>
                View calculated treatment bills,
                payment status and printable invoices.
            </p>
        </div>

        <a class="button button-primary"
            href="${pageContext.request.contextPath}/bills?action=new">
            Create Bill
        </a>
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

    <section class="card">

        <c:choose>

            <c:when test="${empty bills}">
                <div class="empty-state">
                    <h3>No bills found</h3>

                    <p>
                        There are currently no patient
                        bills to display.
                    </p>

                    <a class="button button-primary"
                        href="${pageContext.request.contextPath}/bills?action=new">
                        Create First Bill
                    </a>
                </div>
            </c:when>

            <c:otherwise>

                <div class="table-responsive">
                    <table class="data-table">

                        <thead>
                            <tr>
                                <th>Bill Number</th>
                                <th>Appointment ID</th>
                                <th>Subtotal</th>
                                <th>Discount</th>
                                <th>Total</th>
                                <th>Payment Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>

                        <tbody>

                            <c:forEach var="bill"
                                items="${bills}">

                                <tr>
                                    <td>
                                        <strong>
                                            <c:out value="${bill.billNumber}" />
                                        </strong>
                                    </td>

                                    <td>
                                        <c:out value="${bill.appointmentId}" />
                                    </td>

                                    <td>
                                        Rs.
                                        <fmt:formatNumber
                                            value="${bill.subtotal}"
                                            minFractionDigits="2"
                                            maxFractionDigits="2" />
                                    </td>

                                    <td>
                                        Rs.
                                        <fmt:formatNumber
                                            value="${bill.discount}"
                                            minFractionDigits="2"
                                            maxFractionDigits="2" />
                                    </td>

                                    <td>
                                        <strong>
                                            Rs.
                                            <fmt:formatNumber
                                                value="${bill.totalAmount}"
                                                minFractionDigits="2"
                                                maxFractionDigits="2" />
                                        </strong>
                                    </td>

                                    <td>
                                        <span class="status-badge">
                                            <c:out
                                                value="${bill.paymentStatus}" />
                                        </span>
                                    </td>

                                    <td class="action-buttons">

                                        <a class="button button-small"
                                            href="${pageContext.request.contextPath}/bills?action=view&id=${bill.billId}">
                                            View
                                        </a>

                                        <a class="button button-small button-secondary"
                                            href="${pageContext.request.contextPath}/bills?action=print&id=${bill.billId}"
                                            target="_blank">
                                            Print
                                        </a>

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

<footer class="site-footer">
    <div class="container">
        <p>
            Sunrise Dental Clinic Management System
        </p>
    </div>
</footer>

</body>
</html>
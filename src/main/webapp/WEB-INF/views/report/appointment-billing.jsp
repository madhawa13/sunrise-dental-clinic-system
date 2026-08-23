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
        Appointment Billing Report
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

            <a href="${pageContext.request.contextPath}/patients">
                Patients
            </a>

            <a href="${pageContext.request.contextPath}/appointments">
                Appointments
            </a>

            <a href="${pageContext.request.contextPath}/bills">
                Billing
            </a>

            <a href="${pageContext.request.contextPath}/reports"
               class="active">
                Reports
            </a>

            <a href="${pageContext.request.contextPath}/help">
                Help
            </a>

        </nav>

    </div>

</header>

<main class="container">

    <section class="page-header">

        <div>

            <h2>
                Appointment and Billing Report
            </h2>

            <p>
                Combined appointment, patient,
                billing and payment summary.
            </p>

        </div>

        <button type="button"
                class="btn btn-secondary"
                onclick="window.print();">

            Print Report

        </button>

    </section>

    <c:if test="${not empty errorMessage}">

        <div class="alert alert-error">

            <c:out value="${errorMessage}"/>

        </div>

    </c:if>

    <!-- Date filter -->

    <section class="card report-filter-card">

        <form method="get"
              action="${pageContext.request.contextPath}/reports"
              class="search-form">

            <div class="form-group">

                <label for="date">

                    Appointment Date

                </label>

                <input type="date"
                       id="date"
                       name="date"
                       value="<c:out value='${selectedDate}'/>">

            </div>

            <div class="form-actions">

                <button type="submit"
                        class="btn btn-primary">

                    Generate Report

                </button>

                <a class="btn btn-secondary"
                   href="${pageContext.request.contextPath}/reports">

                    Show All

                </a>

            </div>

        </form>

    </section>

    <!-- Financial summary -->

    <section class="billing-summary-grid">

        <article class="card summary-card">

            <span class="summary-label">
                Report Records
            </span>

            <strong class="summary-value">

                <c:out value="${reports.size()}"/>

            </strong>

        </article>

        <article class="card summary-card">

            <span class="summary-label">
                Total Billed
            </span>

            <strong class="summary-value">

                Rs.
                <fmt:formatNumber
                        value="${totalBilled}"
                        minFractionDigits="2"
                        maxFractionDigits="2"/>

            </strong>

        </article>

        <article class="card summary-card">

            <span class="summary-label">
                Total Paid
            </span>

            <strong class="summary-value report-paid">

                Rs.
                <fmt:formatNumber
                        value="${totalPaid}"
                        minFractionDigits="2"
                        maxFractionDigits="2"/>

            </strong>

        </article>

        <article class="card summary-card">

            <span class="summary-label">
                Outstanding
            </span>

            <strong class="summary-value report-outstanding">

                Rs.
                <fmt:formatNumber
                        value="${totalOutstanding}"
                        minFractionDigits="2"
                        maxFractionDigits="2"/>

            </strong>

        </article>

    </section>

    <!-- Report table -->

    <section class="card">

        <div class="section-header">

            <div>

                <h3>Report Details</h3>

                <p class="form-help">

                    Data source:
                    vw_appointment_billing_summary

                </p>

            </div>

        </div>

        <c:choose>

            <c:when test="${empty reports}">

                <div class="empty-message">

                    <h3>No report records found</h3>

                    <p>
                        No appointments matched the
                        selected report criteria.
                    </p>

                </div>

            </c:when>

            <c:otherwise>

                <div class="table-responsive">

                    <table class="data-table">

                        <thead>

                        <tr>

                            <th>Appointment</th>

                            <th>Date</th>

                            <th>Patient</th>

                            <th>Dentist</th>

                            <th>Appointment Status</th>

                            <th>Bill Number</th>

                            <th>Bill Total</th>

                            <th>Paid</th>

                            <th>Balance</th>

                            <th>Payment Status</th>

                        </tr>

                        </thead>

                        <tbody>

                        <c:forEach var="report"
                                   items="${reports}">

                            <tr>

                                <td>

                                    <strong>

                                        <c:out value="${report.appointmentNumber}"/>

                                    </strong>

                                </td>

                                <td>

                                    <c:out value="${report.appointmentDate}"/>

                                </td>

                                <td>

                                    <c:out value="${report.patientName}"/>

                                </td>

                                <td>

                                    <c:out value="${report.dentistName}"/>

                                </td>

                                <td>

                                    <span class="status-badge status-${report.appointmentStatus.toLowerCase()}">

                                        <c:out value="${report.appointmentStatus}"/>

                                    </span>

                                </td>

                                <td>

                                    <c:choose>

                                        <c:when test="${empty report.billNumber}">
                                            Not billed
                                        </c:when>

                                        <c:otherwise>

                                            <c:out value="${report.billNumber}"/>

                                        </c:otherwise>

                                    </c:choose>

                                </td>

                                <td>

                                    Rs.
                                    <fmt:formatNumber
                                            value="${report.billTotal}"
                                            minFractionDigits="2"
                                            maxFractionDigits="2"/>

                                </td>

                                <td>

                                    Rs.
                                    <fmt:formatNumber
                                            value="${report.amountPaid}"
                                            minFractionDigits="2"
                                            maxFractionDigits="2"/>

                                </td>

                                <td>

                                    Rs.
                                    <fmt:formatNumber
                                            value="${report.outstandingBalance}"
                                            minFractionDigits="2"
                                            maxFractionDigits="2"/>

                                </td>

                                <td>

                                    <span class="status-badge status-${report.paymentStatus.toLowerCase()}">

                                        <c:out value="${report.paymentStatus}"/>

                                    </span>

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
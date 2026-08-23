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
        ${bill.billNumber} - Printable Bill
    </title>

    <link rel="stylesheet"
        href="${pageContext.request.contextPath}/assets/css/style.css">

    <style>
        .print-invoice {
            max-width: 850px;
            margin: 30px auto;
            padding: 35px;
            background: #ffffff;
            border: 1px solid #d9e2ec;
            border-radius: 12px;
        }

        .invoice-heading {
            display: flex;
            justify-content: space-between;
            gap: 20px;
            border-bottom: 2px solid #0f766e;
            padding-bottom: 20px;
            margin-bottom: 25px;
        }

        .invoice-totals {
            width: 100%;
            max-width: 380px;
            margin-left: auto;
            margin-top: 25px;
        }

        .invoice-totals div {
            display: flex;
            justify-content: space-between;
            padding: 9px 0;
            border-bottom: 1px solid #e5e7eb;
        }

        .invoice-total-final {
            font-size: 1.15rem;
            font-weight: bold;
        }

        .print-actions {
            max-width: 850px;
            margin: 20px auto;
            display: flex;
            justify-content: flex-end;
            gap: 10px;
        }

        @media print {
            body {
                background: #ffffff;
            }

            .print-actions {
                display: none;
            }

            .print-invoice {
                max-width: none;
                margin: 0;
                padding: 0;
                border: none;
                box-shadow: none;
            }
        }
    </style>
</head>

<body>

<div class="print-actions">

    <button class="button button-primary"
        type="button"
        onclick="window.print()">
        Print Invoice
    </button>

    <button class="button button-secondary"
        type="button"
        onclick="window.close()">
        Close
    </button>

</div>

<main class="print-invoice">

    <div class="invoice-heading">

        <div>
            <h1>Sunrise Dental Clinic</h1>

            <p>
                Colombo, Sri Lanka
            </p>

            <p>
                Appointment and Patient
                Management System
            </p>
        </div>

        <div>
            <h2>Patient Bill</h2>

            <p>
                <strong>Bill Number:</strong>
                <c:out value="${bill.billNumber}" />
            </p>

            <p>
                <strong>Appointment ID:</strong>
                <c:out value="${bill.appointmentId}" />
            </p>

            <p>
                <strong>Payment Status:</strong>
                <c:out value="${bill.paymentStatus}" />
            </p>
        </div>

    </div>

    <section>
        <h3>Payment History</h3>

        <c:choose>

            <c:when test="${empty payments}">
                <p>No payments recorded.</p>
            </c:when>

            <c:otherwise>
                <table class="data-table">

                    <thead>
                        <tr>
                            <th>Payment Number</th>
                            <th>Date</th>
                            <th>Method</th>
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
            </c:otherwise>

        </c:choose>
    </section>

    <section class="invoice-totals">

        <div>
            <span>Subtotal</span>

            <strong>
                Rs.
                <fmt:formatNumber
                    value="${bill.subtotal}"
                    minFractionDigits="2"
                    maxFractionDigits="2" />
            </strong>
        </div>

        <div>
            <span>Discount</span>

            <strong>
                Rs.
                <fmt:formatNumber
                    value="${bill.discount}"
                    minFractionDigits="2"
                    maxFractionDigits="2" />
            </strong>
        </div>

        <div class="invoice-total-final">
            <span>Total Amount</span>

            <strong>
                Rs.
                <fmt:formatNumber
                    value="${bill.totalAmount}"
                    minFractionDigits="2"
                    maxFractionDigits="2" />
            </strong>
        </div>

        <div>
            <span>Total Paid</span>

            <strong>
                Rs.
                <fmt:formatNumber
                    value="${totalPaid}"
                    minFractionDigits="2"
                    maxFractionDigits="2" />
            </strong>
        </div>

        <div class="invoice-total-final">
            <span>Balance</span>

            <strong>
                Rs.
                <fmt:formatNumber
                    value="${balance}"
                    minFractionDigits="2"
                    maxFractionDigits="2" />
            </strong>
        </div>

    </section>

    <p style="margin-top: 45px; text-align: center;">
        Thank you for choosing Sunrise Dental Clinic.
    </p>

</main>

</body>
</html>
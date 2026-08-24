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

    <title>Login | Sunrise Dental Clinic</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/style.css">
          
    <link rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/modern.css?v=1">
</head>

<body class="login-page">

<main class="login-layout">

    <section class="login-brand-panel">

        <div class="login-brand-content">

            <div class="login-logo">
                <span class="login-logo-icon">
                    &#129463;
                </span>

                <div>
                    <h1>Sunrise Dental Clinic</h1>

                    <p>
                        Appointment and Patient
                        Management System
                    </p>
                </div>
            </div>

            <div class="login-introduction">

                <span class="login-eyebrow">
                    Secure Clinic Management
                </span>

                <h2>
                    Better patient care starts with
                    better information.
                </h2>

                <p>
                    Manage patients, appointments,
                    treatments, bills and payments using
                    one secure and reliable system.
                </p>

                <div class="login-feature-list">

                    <div class="login-feature">
                        <span class="login-feature-icon">
                            &#10003;
                        </span>

                        <span>
                            Secure role-based staff access
                        </span>
                    </div>

                    <div class="login-feature">
                        <span class="login-feature-icon">
                            &#10003;
                        </span>

                        <span>
                            Accurate appointment management
                        </span>
                    </div>

                    <div class="login-feature">
                        <span class="login-feature-icon">
                            &#10003;
                        </span>

                        <span>
                            Treatment, billing and reporting
                        </span>
                    </div>

                </div>

            </div>

        </div>

    </section>

    <section class="login-form-panel">

        <div class="login-card">

            <div class="login-card-header">

                <div class="login-mobile-logo">
                    &#129463;
                </div>

                <span class="login-card-label">
                    Staff Portal
                </span>

                <h2>Welcome back</h2>

                <p>
                    Sign in using your authorized
                    clinic staff account.
                </p>

            </div>

            <c:if test="${not empty errorMessage}">

                <div class="alert alert-error login-alert">
                    <span class="alert-icon">
                        !
                    </span>

                    <c:out value="${errorMessage}"/>
                </div>

            </c:if>

            <c:if test="${param.logout == 'success'}">

                <div class="alert alert-success login-alert">
                    You have logged out successfully.
                </div>

            </c:if>

            <c:if test="${param.session == 'expired'}">

                <div class="alert alert-warning login-alert">
                    Your session has expired.
                    Please sign in again.
                </div>

            </c:if>

            <form method="post"
                  action="${pageContext.request.contextPath}/login"
                  class="login-form">

                <div class="form-group">

                    <label for="username">
                        Username
                    </label>

                    <div class="input-with-icon">

                        <span class="input-icon">
                            &#128100;
                        </span>

                        <input type="text"
                               id="username"
                               name="username"
                               maxlength="50"
                               value="<c:out value='${username}'/>"
                               placeholder="Enter your username"
                               autocomplete="username"
                               autofocus
                               required>

                    </div>

                </div>

                <div class="form-group">

                    <div class="password-label-row">

                        <label for="password">
                            Password
                        </label>

                        <span class="secure-label">
                            Secure login
                        </span>

                    </div>

                    <div class="input-with-icon">

                        <span class="input-icon">
                            &#128274;
                        </span>

                        <input type="password"
                               id="password"
                               name="password"
                               placeholder="Enter your password"
                               autocomplete="current-password"
                               required>

                        <button type="button"
                                class="password-toggle"
                                id="passwordToggle"
                                aria-label="Show password">
                            Show
                        </button>

                    </div>

                </div>

                <button type="submit"
                        class="btn btn-primary login-submit">
                    Sign In
                    <span aria-hidden="true">
                        &#8594;
                    </span>
                </button>

            </form>

            <div class="login-security-note">

                <span class="login-security-icon">
                    &#128737;
                </span>

                <p>
                    This system is restricted to authorized
                    Sunrise Dental Clinic staff members.
                </p>

            </div>

            <div class="login-help">

                Need assistance?

                <a href="${pageContext.request.contextPath}/help">
                    Open the help guide
                </a>

            </div>

        </div>

        <p class="login-copyright">
            &copy; 2026 Sunrise Dental Clinic.
            CIS6003 Advanced Programming Assignment.
        </p>

    </section>

</main>

<script>
    const passwordInput =
            document.getElementById("password");

    const passwordToggle =
            document.getElementById("passwordToggle");

    passwordToggle.addEventListener(
            "click",
            function () {

                const passwordIsHidden =
                        passwordInput.type === "password";

                passwordInput.type =
                        passwordIsHidden
                                ? "text"
                                : "password";

                passwordToggle.textContent =
                        passwordIsHidden
                                ? "Hide"
                                : "Show";

                passwordToggle.setAttribute(
                        "aria-label",
                        passwordIsHidden
                                ? "Hide password"
                                : "Show password");
            });
</script>

</body>
</html>
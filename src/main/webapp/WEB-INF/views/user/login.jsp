<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Sign In" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="row justify-content-center my-4">
    <div class="col-md-7 col-lg-5">
        <div class="card card-custom shadow-lg">
            <div class="card-header card-header-navy text-center py-4">
                <i class="bi bi-shield-lock fs-1 text-warning mb-2 d-block"></i>
                <h3 class="mb-1 text-white">Sign In to StayEase</h3>
                <p class="text-white-50 mb-0 small">Access your bookings or administrative dashboard</p>
            </div>
            <div class="card-body p-4 p-md-5">
                <form action="${pageContext.request.contextPath}/user" method="POST" id="loginForm" novalidate>
                    <input type="hidden" name="action" value="login">

                    <div class="mb-3">
                        <label for="email" class="form-label fw-semibold">Email Address</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                            <input type="email" class="form-control" id="email" name="email" required
                                   placeholder="guest@example.com"
                                   value="<c:out value='${inputEmail}'/>">
                        </div>
                    </div>

                    <div class="mb-4">
                        <label for="password" class="form-label fw-semibold">Password</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-key"></i></span>
                            <input type="password" class="form-control" id="password" name="password" required
                                   placeholder="Enter your password">
                        </div>
                    </div>

                    <div class="d-grid mb-3">
                        <button type="submit" class="btn btn-navy py-2 fs-6">
                            <i class="bi bi-box-arrow-in-right me-1"></i> Sign In
                        </button>
                    </div>

                    <div class="text-center text-muted small mb-4">
                        Don't have an account yet?
                        <a href="${pageContext.request.contextPath}/user?action=register" class="text-navy fw-semibold">Register here</a>
                    </div>

                    <!-- Quick Demo Accounts Helper -->
                    <div class="card bg-light border-0 p-3 rounded">
                        <div class="small fw-bold text-muted mb-2"><i class="bi bi-info-circle me-1"></i> Quick Viva Demo Logins:</div>
                        <div class="small text-muted">
                            <div><strong>Admin:</strong> <code>admin@stayease.com</code> / <code>Password@123</code></div>
                            <div><strong>Regular Guest:</strong> <code>nimal@gmail.com</code> / <code>Password@123</code></div>
                            <div><strong>Premium Guest:</strong> <code>sunil@gmail.com</code> / <code>Password@123</code></div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>

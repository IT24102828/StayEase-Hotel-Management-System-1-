<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Guest Registration" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="row justify-content-center my-4">
    <div class="col-md-8 col-lg-6">
        <div class="card card-custom shadow-lg">
            <div class="card-header card-header-navy text-center py-4">
                <i class="bi bi-person-plus fs-1 text-warning mb-2 d-block"></i>
                <h3 class="mb-1 text-white">Create Your Account</h3>
                <p class="text-white-50 mb-0 small">Join StayEase to reserve boutique hotel rooms and unlock VIP privileges</p>
            </div>
            <div class="card-body p-4 p-md-5">
                <form action="${pageContext.request.contextPath}/user" method="POST" id="registerForm" novalidate>
                    <input type="hidden" name="action" value="register">

                    <div class="mb-3">
                        <label for="fullName" class="form-label fw-semibold">Full Name <span class="text-danger">*</span></label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-person"></i></span>
                            <input type="text" class="form-control" id="fullName" name="fullName"
                                   maxlength="100" required placeholder="e.g. Johnathan Doe"
                                   value="<c:out value='${inputFullName}'/>">
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="email" class="form-label fw-semibold">Email Address <span class="text-danger">*</span></label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                            <input type="email" class="form-control" id="email" name="email"
                                   maxlength="100" required placeholder="e.g. guest@example.com"
                                   value="<c:out value='${inputEmail}'/>">
                        </div>
                        <div class="form-text">We'll use this for booking confirmations and login.</div>
                    </div>

                    <div class="mb-3">
                        <label for="phone" class="form-label fw-semibold">Phone Number</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-telephone"></i></span>
                            <input type="tel" class="form-control" id="phone" name="phone"
                                   maxlength="20" placeholder="e.g. +94 77 123 4567"
                                   value="<c:out value='${inputPhone}'/>">
                        </div>
                        <div class="form-text">Optional. 7–20 digits (allows +, -, spaces).</div>
                    </div>

                    <div class="mb-3">
                        <label for="password" class="form-label fw-semibold">Password <span class="text-danger">*</span></label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-key"></i></span>
                            <input type="password" class="form-control" id="password" name="password"
                                   minlength="6" required placeholder="At least 6 characters">
                        </div>
                        <div class="form-text">Must be at least 6 characters long.</div>
                    </div>

                    <div class="mb-4">
                        <label class="form-label fw-semibold d-block">Membership Tier</label>
                        <div class="row g-3">
                            <div class="col-sm-6">
                                <div class="form-check card p-3 h-100 ${inputMembership != 'PREMIUM' ? 'border-primary' : ''}">
                                    <input class="form-check-input" type="radio" name="membership" id="roleRegular"
                                           value="REGULAR" ${inputMembership != 'PREMIUM' ? 'checked' : ''}>
                                    <label class="form-check-label fw-semibold" for="roleRegular">
                                        Regular Guest
                                    </label>
                                    <div class="small text-muted mt-1">Standard booking access with standard room rates.</div>
                                </div>
                            </div>
                            <div class="col-sm-6">
                                <div class="form-check card p-3 h-100 ${inputMembership == 'PREMIUM' ? 'border-warning bg-light' : ''}">
                                    <input class="form-check-input" type="radio" name="membership" id="rolePremium"
                                           value="PREMIUM" ${inputMembership == 'PREMIUM' ? 'checked' : ''}>
                                    <label class="form-check-label fw-semibold text-navy" for="rolePremium">
                                        <i class="bi bi-star-fill text-warning me-1"></i> Premium VIP
                                    </label>
                                    <div class="small text-success fw-medium mt-1">Save 10% on every room booking automatically!</div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="d-grid mb-3">
                        <button type="submit" class="btn btn-gold py-2 fs-6">
                            <i class="bi bi-person-check me-1"></i> Complete Registration
                        </button>
                    </div>

                    <div class="text-center text-muted small">
                        Already have an account?
                        <a href="${pageContext.request.contextPath}/user?action=login" class="text-navy fw-semibold">Sign in here</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>

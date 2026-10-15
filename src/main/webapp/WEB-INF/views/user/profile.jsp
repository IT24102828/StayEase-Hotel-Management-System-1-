<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="My Profile" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="row justify-content-center">
    <div class="col-lg-8">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h1 class="h2 mb-1 fw-bold text-navy">Account Settings</h1>
                <p class="text-muted mb-0">Manage your profile information and membership tier.</p>
            </div>
            <div>
                <c:choose>
                    <c:when test="${user.admin}">
                        <span class="badge badge-admin fs-6 px-3 py-2">System Administrator</span>
                    </c:when>
                    <c:when test="${user.premium}">
                        <span class="badge badge-premium fs-6 px-3 py-2"><i class="bi bi-star-fill text-dark me-1"></i> VIP Member (10% Discount)</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge badge-regular fs-6 px-3 py-2">Regular Guest</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="card card-custom shadow-sm mb-4">
            <div class="card-header card-header-navy">
                <h5 class="mb-0 text-white"><i class="bi bi-person-gear me-2"></i> Update Profile Details</h5>
            </div>
            <div class="card-body p-4">
                <form action="${pageContext.request.contextPath}/user" method="POST" id="profileForm" novalidate>
                    <input type="hidden" name="action" value="update">

                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label for="fullName" class="form-label fw-semibold">Full Name <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="fullName" name="fullName"
                                   maxlength="100" required value="<c:out value='${user.fullName}'/>">
                        </div>

                        <div class="col-md-6">
                            <label for="email" class="form-label fw-semibold">Email Address (Read-only)</label>
                            <input type="email" class="form-control bg-light" id="email" name="email"
                                   readonly value="<c:out value='${user.email}'/>">
                            <div class="form-text">Email address is permanently bound to your account and cannot be modified.</div>
                        </div>
                    </div>

                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label for="phone" class="form-label fw-semibold">Phone Number</label>
                            <input type="tel" class="form-control" id="phone" name="phone"
                                   maxlength="20" placeholder="+94 77 123 4567"
                                   value="<c:out value='${user.phone}'/>">
                            <div class="form-text">7–20 digits (allows +, -, spaces).</div>
                        </div>

                        <div class="col-md-6">
                            <label for="password" class="form-label fw-semibold">New Password</label>
                            <input type="password" class="form-control" id="password" name="password"
                                   placeholder="Leave blank to keep existing password">
                            <div class="form-text">Leave blank to retain current password (TC-U-05). Minimum 6 characters if changed.</div>
                        </div>
                    </div>

                    <c:if test="${!user.admin}">
                        <div class="mb-4">
                            <label class="form-label fw-semibold d-block">Membership Tier</label>
                            <div class="row g-3">
                                <div class="col-sm-6">
                                    <div class="form-check card p-3 h-100 ${!user.premium ? 'border-primary' : ''}">
                                        <input class="form-check-input" type="radio" name="membership" id="roleRegular"
                                               value="REGULAR" ${!user.premium ? 'checked' : ''}>
                                        <label class="form-check-label fw-semibold" for="roleRegular">
                                            Regular Guest
                                        </label>
                                        <div class="small text-muted mt-1">Standard booking rates.</div>
                                    </div>
                                </div>
                                <div class="col-sm-6">
                                    <div class="form-check card p-3 h-100 ${user.premium ? 'border-warning bg-light' : ''}">
                                        <input class="form-check-input" type="radio" name="membership" id="rolePremium"
                                               value="PREMIUM" ${user.premium ? 'checked' : ''}>
                                        <label class="form-check-label fw-semibold text-navy" for="rolePremium">
                                            <i class="bi bi-star-fill text-warning me-1"></i> Premium VIP
                                        </label>
                                        <div class="small text-success fw-medium mt-1">10% discount on all stays!</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:if>

                    <div class="d-flex justify-content-end">
                        <button type="submit" class="btn btn-navy px-4">
                            <i class="bi bi-check2-circle me-1"></i> Save Changes
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Danger Zone: Account Deletion (U-05) -->
        <div class="card border-danger shadow-sm mb-4">
            <div class="card-header bg-danger text-white">
                <h5 class="mb-0"><i class="bi bi-exclamation-octagon me-2"></i> Account Deletion</h5>
            </div>
            <div class="card-body p-4 d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3">
                <div>
                    <h6 class="fw-bold text-danger mb-1">Delete this account permanently</h6>
                    <p class="text-muted small mb-0">
                        <c:choose>
                            <c:when test="${user.admin}">
                                Administrators cannot delete their own accounts.
                            </c:when>
                            <c:otherwise>
                                Permanently removes your personal profile and all associated reservations from StayEase.
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>
                <c:choose>
                    <c:when test="${user.admin}">
                        <button class="btn btn-outline-secondary" disabled>Account Protected</button>
                    </c:when>
                    <c:otherwise>
                        <button type="button" class="btn btn-outline-danger" data-bs-toggle="modal" data-bs-target="#deleteAccountModal">
                            <i class="bi bi-trash3 me-1"></i> Delete My Account
                        </button>

                        <!-- Self-Delete Confirmation Modal (TC-U-07) -->
                        <div class="modal fade" id="deleteAccountModal" tabindex="-1" aria-hidden="true">
                            <div class="modal-dialog modal-dialog-centered">
                                <div class="modal-content">
                                    <div class="modal-header bg-danger text-white">
                                        <h5 class="modal-title"><i class="bi bi-exclamation-triangle-fill me-2"></i> Confirm Account Deletion</h5>
                                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                                    </div>
                                    <div class="modal-body">
                                        <p>Are you sure you want to delete your account, <strong><c:out value="${user.fullName}"/></strong>?</p>
                                        <p class="small text-danger mb-0">This action is irreversible. You will be immediately logged out and all your room reservations will be removed.</p>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                        <form action="${pageContext.request.contextPath}/user" method="POST" class="d-inline">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="id" value="${user.id}">
                                            <button type="submit" class="btn btn-danger">Confirm Delete</button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>

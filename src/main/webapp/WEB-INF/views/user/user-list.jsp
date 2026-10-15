<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Guest Management" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3 mb-4">
    <div>
        <h1 class="h2 mb-1 fw-bold text-navy">Guest & User Directory</h1>
        <p class="text-muted mb-0">Search and manage hotel guest accounts and system administrators.</p>
    </div>
</div>

<!-- Search Bar (U-04 / TC-U-06) -->
<div class="card card-custom p-3 mb-4 shadow-sm">
    <form action="${pageContext.request.contextPath}/user" method="GET" class="row g-2 align-items-center">
        <input type="hidden" name="action" value="list">
        <div class="col-md-9 col-sm-8">
            <div class="input-group">
                <span class="input-group-text bg-white border-end-0 text-muted"><i class="bi bi-search"></i></span>
                <input type="text" name="keyword" class="form-control border-start-0 ps-0"
                       placeholder="Search by name, email, or guest ID (e.g. nimal)..."
                       value="<c:out value='${keyword}'/>">
            </div>
        </div>
        <div class="col-md-3 col-sm-4 d-flex gap-2">
            <button type="submit" class="btn btn-navy flex-grow-1">
                <i class="bi bi-filter me-1"></i> Search Guests
            </button>
            <c:if test="${not empty keyword}">
                <a href="${pageContext.request.contextPath}/user?action=list" class="btn btn-outline-secondary" title="Reset Search">
                    <i class="bi bi-x-lg"></i>
                </a>
            </c:if>
        </div>
    </form>
</div>

<c:choose>
    <c:when test="${empty users}">
        <div class="empty-state">
            <div class="empty-state-icon">
                <i class="bi bi-person-x"></i>
            </div>
            <h4 class="fw-semibold">No users found.</h4>
            <p class="text-muted mb-3">No account matched your search query.</p>
            <a href="${pageContext.request.contextPath}/user?action=list" class="btn btn-outline-gold">
                <i class="bi bi-arrow-repeat me-1"></i> View All Users
            </a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="table-responsive table-custom mb-4">
            <table class="table table-hover align-middle mb-0">
                <thead>
                    <tr>
                        <th>User ID</th>
                        <th>Full Name</th>
                        <th>Email Address</th>
                        <th>Phone</th>
                        <th>Membership / Role</th>
                        <th>Discount Rate</th>
                        <th class="text-center">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="u" items="${users}">
                        <tr>
                            <td class="fw-bold text-muted">#<c:out value="${u.id}"/></td>
                            <td class="fw-semibold">
                                <div class="d-flex align-items-center gap-2">
                                    <i class="bi bi-person-circle fs-5 text-secondary"></i>
                                    <span><c:out value="${u.fullName}"/></span>
                                </div>
                            </td>
                            <td><c:out value="${u.email}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty u.phone}">
                                        <c:out value="${u.phone}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-muted fst-italic">—</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${u.admin}">
                                        <span class="badge badge-admin">ADMIN</span>
                                    </c:when>
                                    <c:when test="${u.premium}">
                                        <span class="badge badge-premium"><i class="bi bi-star-fill text-dark me-1"></i> PREMIUM</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-regular">REGULAR</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <span class="fw-semibold ${u.premium ? 'text-success' : 'text-muted'}">
                                    <c:choose>
                                        <c:when test="${u.premium}">10% Off</c:when>
                                        <c:otherwise>0%</c:otherwise>
                                    </c:choose>
                                </span>
                            </td>
                            <td class="text-center">
                                <c:choose>
                                    <c:when test="${u.id == sessionScope.currentUser.id}">
                                        <span class="badge bg-secondary-subtle text-secondary border">Your Account</span>
                                    </c:when>
                                    <c:otherwise>
                                        <button type="button" class="btn btn-sm btn-outline-danger"
                                                data-bs-toggle="modal" data-bs-target="#deleteUserModal${u.id}"
                                                title="Delete Account">
                                            <i class="bi bi-trash"></i> Delete
                                        </button>

                                        <!-- Admin Delete Guest Modal (U-05) -->
                                        <div class="modal fade" id="deleteUserModal${u.id}" tabindex="-1" aria-hidden="true">
                                            <div class="modal-dialog modal-dialog-centered">
                                                <div class="modal-content">
                                                    <div class="modal-header bg-danger text-white">
                                                        <h5 class="modal-title"><i class="bi bi-exclamation-triangle-fill me-2"></i> Confirm Account Removal</h5>
                                                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                                                    </div>
                                                    <div class="modal-body text-start">
                                                        <p>Are you sure you want to delete user <strong><c:out value="${u.fullName}"/></strong> (<c:out value="${u.email}"/>)?</p>
                                                        <p class="small text-danger mb-0"><strong>Warning:</strong> All active and historical reservations for this user will also be removed.</p>
                                                    </div>
                                                    <div class="modal-footer">
                                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                                        <form action="${pageContext.request.contextPath}/user" method="POST" class="d-inline">
                                                            <input type="hidden" name="action" value="delete">
                                                            <input type="hidden" name="id" value="${u.id}">
                                                            <button type="submit" class="btn btn-danger">Confirm Delete</button>
                                                        </form>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>

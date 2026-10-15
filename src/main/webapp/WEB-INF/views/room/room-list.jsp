<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Rooms & Suites" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3 mb-4">
    <div>
        <h1 class="h2 mb-1 fw-bold text-navy">Hotel Rooms & Suites</h1>
        <p class="text-muted mb-0">Explore our curated collection of comfortable rooms and luxurious suites.</p>
    </div>
    <c:if test="${sessionScope.currentUser != null && sessionScope.currentUser.admin}">
        <div>
            <a href="${pageContext.request.contextPath}/rooms?action=new" class="btn btn-gold shadow-sm">
                <i class="bi bi-plus-lg me-1"></i> Add New Room
            </a>
        </div>
    </c:if>
</div>

<!-- Search & Filter Bar (R-02) -->
<div class="card card-custom p-3 mb-4 shadow-sm">
    <form action="${pageContext.request.contextPath}/rooms" method="GET" class="row g-2 align-items-center">
        <input type="hidden" name="action" value="list">
        <div class="col-md-9 col-sm-8">
            <div class="input-group">
                <span class="input-group-text bg-white border-end-0 text-muted"><i class="bi bi-search"></i></span>
                <input type="text" name="keyword" class="form-control border-start-0 ps-0"
                       placeholder="Search by room number, type (Single, Double, Suite), or status..."
                       value="<c:out value='${keyword}'/>">
            </div>
        </div>
        <div class="col-md-3 col-sm-4 d-flex gap-2">
            <button type="submit" class="btn btn-navy flex-grow-1">
                <i class="bi bi-funnel me-1"></i> Filter
            </button>
            <c:if test="${not empty keyword}">
                <a href="${pageContext.request.contextPath}/rooms" class="btn btn-outline-secondary" title="Reset Search">
                    <i class="bi bi-x-lg"></i>
                </a>
            </c:if>
        </div>
    </form>
</div>

<!-- Rooms Table & Display -->
<c:choose>
    <c:when test="${empty rooms}">
        <div class="empty-state">
            <div class="empty-state-icon">
                <i class="bi bi-door-closed"></i>
            </div>
            <h4 class="fw-semibold">No rooms match your search.</h4>
            <p class="text-muted mb-3">Try adjusting your search criteria or resetting filters.</p>
            <a href="${pageContext.request.contextPath}/rooms" class="btn btn-outline-gold">
                <i class="bi bi-arrow-repeat me-1"></i> View All Rooms
            </a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="table-responsive table-custom mb-4">
            <table class="table table-hover align-middle mb-0">
                <thead>
                    <tr>
                        <th>Room No</th>
                        <th>Type</th>
                        <th>Capacity</th>
                        <th>Features & Amenities</th>
                        <th>Base Price</th>
                        <th>Nightly Rate</th>
                        <th>Status</th>
                        <c:if test="${sessionScope.currentUser != null && sessionScope.currentUser.admin}">
                            <th class="text-center" style="min-width: 140px;">Actions</th>
                        </c:if>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="room" items="${rooms}">
                        <tr>
                            <td class="fw-bold fs-5 text-primary-navy">
                                <i class="bi bi-door-open text-muted me-1 fs-6"></i>
                                <c:out value="${room.roomNumber}"/>
                            </td>
                            <td>
                                <span class="badge room-badge-type">
                                    <c:out value="${room.type}"/>
                                </span>
                            </td>
                            <td>
                                <span class="d-inline-flex align-items-center text-secondary small">
                                    <i class="bi bi-people-fill text-muted me-1"></i>
                                    Up to <c:out value="${room.capacity}"/> guest(s)
                                </span>
                            </td>
                            <td>
                                <div class="small text-dark fw-medium mb-1"><c:out value="${room.features}"/></div>
                                <c:if test="${not empty room.description}">
                                    <div class="small text-muted fst-italic"><c:out value="${room.description}"/></div>
                                </c:if>
                            </td>
                            <td class="text-secondary small">
                                $<fmt:formatNumber value="${room.basePrice}" pattern="0.00"/>
                            </td>
                            <td>
                                <span class="fw-bold text-success fs-6">
                                    $<fmt:formatNumber value="${room.nightlyRate}" pattern="0.00"/>
                                </span>
                                <span class="small text-muted">/ night</span>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${room.available}">
                                        <span class="badge room-badge-avail">
                                            <i class="bi bi-check-circle me-1"></i> Available
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge room-badge-maint">
                                            <i class="bi bi-tools me-1"></i> Maintenance
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <c:if test="${sessionScope.currentUser != null && sessionScope.currentUser.admin}">
                                <td class="text-center">
                                    <div class="btn-group btn-group-sm">
                                        <a href="${pageContext.request.contextPath}/rooms?action=edit&id=${room.id}"
                                           class="btn btn-outline-primary" title="Edit Room">
                                            <i class="bi bi-pencil-square"></i>
                                        </a>
                                        <button type="button" class="btn btn-outline-danger" title="Delete Room"
                                                data-bs-toggle="modal" data-bs-target="#deleteRoomModal${room.id}">
                                            <i class="bi bi-trash"></i>
                                        </button>
                                    </div>

                                    <!-- Delete Confirmation Modal (R-04) -->
                                    <div class="modal fade" id="deleteRoomModal${room.id}" tabindex="-1" aria-hidden="true">
                                        <div class="modal-dialog modal-dialog-centered">
                                            <div class="modal-content">
                                                <div class="modal-header bg-danger text-white">
                                                    <h5 class="modal-title"><i class="bi bi-exclamation-triangle-fill me-2"></i> Confirm Room Deletion</h5>
                                                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                                                </div>
                                                <div class="modal-body text-start">
                                                    <p>Are you sure you want to permanently delete <strong>Room <c:out value="${room.roomNumber}"/></strong> (<c:out value="${room.type}"/>)?</p>
                                                    <p class="small text-danger mb-0"><strong>Warning:</strong> Any associated reservations for this room will also be removed.</p>
                                                </div>
                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                                    <form action="${pageContext.request.contextPath}/rooms" method="POST" class="d-inline">
                                                        <input type="hidden" name="action" value="delete">
                                                        <input type="hidden" name="id" value="${room.id}">
                                                        <button type="submit" class="btn btn-danger">Confirm Delete</button>
                                                    </form>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </td>
                            </c:if>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>

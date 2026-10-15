<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Reservations" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3 mb-4">
    <div>
        <h1 class="h2 mb-1 fw-bold text-navy">
            <c:choose>
                <c:when test="${isAdminView}">Master Reservation Records</c:when>
                <c:otherwise>My Room Bookings</c:otherwise>
            </c:choose>
        </h1>
        <p class="text-muted mb-0">
            <c:choose>
                <c:when test="${isAdminView}">Complete hotel reservation history across all registered guests.</c:when>
                <c:otherwise>Review, manage, or cancel your upcoming and past hotel reservations.</c:otherwise>
            </c:choose>
        </p>
    </div>
    <div>
        <a href="${pageContext.request.contextPath}/reservations?action=search" class="btn btn-gold shadow-sm">
            <i class="bi bi-plus-circle me-1"></i> Book Another Room
        </a>
    </div>
</div>

<c:choose>
    <c:when test="${empty reservations}">
        <div class="empty-state">
            <div class="empty-state-icon">
                <i class="bi bi-journal-x"></i>
            </div>
            <h4 class="fw-semibold">No reservations found.</h4>
            <p class="text-muted mb-3">
                <c:choose>
                    <c:when test="${isAdminView}">There are no bookings recorded in the system yet.</c:when>
                    <c:otherwise>You haven't made any hotel reservations yet.</c:otherwise>
                </c:choose>
            </p>
            <a href="${pageContext.request.contextPath}/reservations?action=search" class="btn btn-outline-gold">
                <i class="bi bi-search me-1"></i> Check Available Rooms
            </a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="table-responsive table-custom mb-4">
            <table class="table table-hover align-middle mb-0">
                <thead>
                    <tr>
                        <th>Booking ID</th>
                        <c:if test="${isAdminView}">
                            <th>Guest Details</th>
                        </c:if>
                        <th>Room</th>
                        <th>Dates (Check-in &rarr; Out)</th>
                        <th>Duration</th>
                        <th>Guests</th>
                        <th>Total Price</th>
                        <th>Status</th>
                        <th class="text-center" style="min-width: 140px;">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="res" items="${reservations}">
                        <tr>
                            <td class="fw-bold text-muted">#RES-<c:out value="${res.id}"/></td>
                            <c:if test="${isAdminView}">
                                <td>
                                    <div class="fw-semibold text-navy"><c:out value="${res.guestName}"/></div>
                                    <div class="small text-muted"><c:out value="${res.guestEmail}"/></div>
                                </td>
                            </c:if>
                            <td>
                                <div class="fw-bold text-navy">Room <c:out value="${res.roomNumber}"/></div>
                                <span class="badge room-badge-type small"><c:out value="${res.roomType}"/></span>
                            </td>
                            <td>
                                <div class="small fw-semibold text-dark">
                                    <i class="bi bi-calendar-event me-1 text-muted"></i>
                                    <c:out value="${res.checkIn}"/>
                                </div>
                                <div class="small text-muted">
                                    &rarr; <c:out value="${res.checkOut}"/>
                                </div>
                            </td>
                            <td>
                                <span class="small badge bg-light text-dark border">
                                    <c:out value="${res.numberOfNights}"/> night(s)
                                </span>
                            </td>
                            <td>
                                <span class="small text-secondary">
                                    <i class="bi bi-people me-1"></i> <c:out value="${res.guests}"/>
                                </span>
                            </td>
                            <td>
                                <span class="fw-bold text-success fs-6">
                                    $<fmt:formatNumber value="${res.totalPrice}" pattern="0.00"/>
                                </span>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${res.confirmed}">
                                        <span class="badge bg-success-subtle text-success border border-success-subtle px-2 py-1">
                                            <i class="bi bi-check-circle-fill me-1"></i> CONFIRMED
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-secondary-subtle text-secondary border border-secondary-subtle px-2 py-1">
                                            <i class="bi bi-x-circle me-1"></i> CANCELLED
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-center">
                                <div class="d-flex justify-content-center gap-1">
                                    <!-- Cancel Button (B-04) -->
                                    <c:if test="${res.confirmed}">
                                        <button type="button" class="btn btn-sm btn-outline-warning"
                                                data-bs-toggle="modal" data-bs-target="#cancelModal${res.id}"
                                                title="Cancel Reservation">
                                            <i class="bi bi-slash-circle me-1"></i> Cancel
                                        </button>

                                        <!-- Cancel Confirmation Modal -->
                                        <div class="modal fade" id="cancelModal${res.id}" tabindex="-1" aria-hidden="true">
                                            <div class="modal-dialog modal-dialog-centered">
                                                <div class="modal-content">
                                                    <div class="modal-header bg-warning text-dark">
                                                        <h5 class="modal-title"><i class="bi bi-exclamation-triangle-fill me-2"></i> Confirm Cancellation</h5>
                                                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                    </div>
                                                    <div class="modal-body text-start">
                                                        <p>Are you sure you want to cancel booking <strong>#RES-<c:out value="${res.id}"/></strong> for <strong>Room <c:out value="${res.roomNumber}"/></strong>?</p>
                                                        <p class="small text-muted mb-0">The room will immediately become available again for other guests on those dates (TC-B-06).</p>
                                                    </div>
                                                    <div class="modal-footer">
                                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Keep Booking</button>
                                                        <form action="${pageContext.request.contextPath}/reservations" method="POST" class="d-inline">
                                                            <input type="hidden" name="action" value="cancel">
                                                            <input type="hidden" name="id" value="${res.id}">
                                                            <button type="submit" class="btn btn-warning">Confirm Cancellation</button>
                                                        </form>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </c:if>

                                    <!-- Admin Delete Button (B-05 / TC-B-08) -->
                                    <c:if test="${isAdminView}">
                                        <button type="button" class="btn btn-sm btn-outline-danger"
                                                data-bs-toggle="modal" data-bs-target="#deleteResModal${res.id}"
                                                title="Permanently Delete Record">
                                            <i class="bi bi-trash"></i>
                                        </button>

                                        <!-- Permanent Deletion Modal -->
                                        <div class="modal fade" id="deleteResModal${res.id}" tabindex="-1" aria-hidden="true">
                                            <div class="modal-dialog modal-dialog-centered">
                                                <div class="modal-content">
                                                    <div class="modal-header bg-danger text-white">
                                                        <h5 class="modal-title"><i class="bi bi-trash3-fill me-2"></i> Permanent Record Deletion</h5>
                                                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                                                    </div>
                                                    <div class="modal-body text-start">
                                                        <p>Are you sure you want to permanently delete reservation record <strong>#RES-<c:out value="${res.id}"/></strong>?</p>
                                                        <p class="small text-danger mb-0">This record will be permanently purged from the database.</p>
                                                    </div>
                                                    <div class="modal-footer">
                                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                                        <form action="${pageContext.request.contextPath}/reservations" method="POST" class="d-inline">
                                                            <input type="hidden" name="action" value="delete">
                                                            <input type="hidden" name="id" value="${res.id}">
                                                            <button type="submit" class="btn btn-danger">Confirm Delete</button>
                                                        </form>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </c:if>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>

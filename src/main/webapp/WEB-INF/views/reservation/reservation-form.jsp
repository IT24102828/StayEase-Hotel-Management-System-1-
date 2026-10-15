<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Check Room Availability" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="mb-4">
    <h1 class="h2 mb-1 fw-bold text-navy">Find & Reserve Your Room</h1>
    <p class="text-muted mb-0">Select your travel dates and party size to discover available rooms with instant price quotes.</p>
</div>

<!-- Search Widget (B-01) -->
<div class="search-widget-card mb-5">
    <form action="${pageContext.request.contextPath}/reservations" method="GET" class="row g-3 align-items-end" id="availabilityForm">
        <input type="hidden" name="action" value="search">

        <div class="col-md-4 col-sm-6">
            <label for="checkIn" class="form-label fw-semibold text-navy">
                <i class="bi bi-calendar-event me-1 text-gold"></i> Check-in Date <span class="text-danger">*</span>
            </label>
            <input type="date" class="form-control form-control-lg" id="checkIn" name="checkIn" required
                   value="<c:out value='${checkIn != null ? checkIn : inputCheckIn}'/>">
            <div class="form-text">Must be today or in the future (TC-B-02).</div>
        </div>

        <div class="col-md-4 col-sm-6">
            <label for="checkOut" class="form-label fw-semibold text-navy">
                <i class="bi bi-calendar-check me-1 text-gold"></i> Check-out Date <span class="text-danger">*</span>
            </label>
            <input type="date" class="form-control form-control-lg" id="checkOut" name="checkOut" required
                   value="<c:out value='${checkOut != null ? checkOut : inputCheckOut}'/>">
            <div class="form-text">Check-out day is exclusive (TC-B-01).</div>
        </div>

        <div class="col-md-2 col-sm-6">
            <label for="guests" class="form-label fw-semibold text-navy">
                <i class="bi bi-people me-1 text-gold"></i> Guests <span class="text-danger">*</span>
            </label>
            <input type="number" min="1" max="10" class="form-control form-control-lg" id="guests" name="guests" required
                   value="<c:out value='${guests != null ? guests : (inputGuests != null ? inputGuests : 1)}'/>">
            <div class="form-text">1 to 10 guests.</div>
        </div>

        <div class="col-md-2 col-sm-6 d-grid">
            <button type="submit" class="btn btn-gold btn-lg">
                <i class="bi bi-search me-1"></i> Search
            </button>
        </div>
    </form>
</div>

<!-- Search Results -->
<c:if test="${searched}">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h3 class="h4 mb-0 fw-bold text-navy">
                Available Rooms (${availableRooms.size()} found)
            </h3>
            <p class="text-muted small mb-0">
                For stay from <strong><c:out value="${checkIn}"/></strong> to <strong><c:out value="${checkOut}"/></strong>
                (${nights} night<c:if test="${nights > 1}">s</c:if>, ${guests} guest<c:if test="${guests > 1}">s</c:if>)
            </p>
        </div>
        <c:if test="${sessionScope.currentUser != null && sessionScope.currentUser.premium}">
            <div class="alert alert-warning py-1 px-3 mb-0 small rounded-pill d-inline-flex align-items-center">
                <i class="bi bi-star-fill me-1"></i> 10% VIP Discount Applied
            </div>
        </c:if>
    </div>

    <c:choose>
        <c:when test="${empty availableRooms}">
            <div class="empty-state">
                <div class="empty-state-icon">
                    <i class="bi bi-calendar-x"></i>
                </div>
                <h4 class="fw-semibold">No rooms available for the selected dates.</h4>
                <p class="text-muted mb-3">All rooms of sufficient capacity are either fully booked or scheduled for maintenance during this time.</p>
                <p class="small text-muted mb-0">Try selecting different dates or fewer guests.</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="row g-4 mb-4">
                <c:forEach var="room" items="${availableRooms}">
                    <div class="col-lg-4 col-md-6">
                        <div class="card card-custom h-100 d-flex flex-column">
                            <div class="card-header bg-white border-bottom pt-3 pb-2 d-flex justify-content-between align-items-center">
                                <div>
                                    <span class="badge room-badge-type me-1"><c:out value="${room.type}"/></span>
                                    <span class="fw-bold fs-5 text-primary-navy">Room <c:out value="${room.roomNumber}"/></span>
                                </div>
                                <span class="badge room-badge-avail">Available</span>
                            </div>
                            <div class="card-body d-flex flex-column justify-content-between">
                                <div>
                                    <div class="d-flex align-items-center text-muted small mb-3">
                                        <i class="bi bi-person-fill me-1"></i>
                                        Sleeps up to <strong>&nbsp;<c:out value="${room.capacity}"/> guests</strong>
                                    </div>
                                    <div class="small text-secondary mb-3">
                                        <strong>Amenities:</strong> <c:out value="${room.features}"/>
                                    </div>
                                    <c:if test="${not empty room.description}">
                                        <p class="small text-muted fst-italic mb-3"><c:out value="${room.description}"/></p>
                                    </c:if>
                                </div>

                                <div class="border-top pt-3 mt-3">
                                    <div class="d-flex justify-content-between align-items-baseline mb-1">
                                        <span class="small text-muted">Nightly Rate:</span>
                                        <span class="fw-semibold text-secondary">
                                            $<fmt:formatNumber value="${room.nightlyRate}" pattern="0.00"/>
                                        </span>
                                    </div>
                                    <div class="d-flex justify-content-between align-items-baseline mb-3">
                                        <span class="fw-bold text-navy">Total Stay (${nights} nights):</span>
                                        <span class="fs-4 fw-bold text-success">
                                            $<fmt:formatNumber value="${priceMap[room.id]}" pattern="0.00"/>
                                        </span>
                                    </div>

                                    <c:choose>
                                        <c:when test="${sessionScope.currentUser != null}">
                                            <button type="button" class="btn btn-navy w-100 py-2"
                                                    data-bs-toggle="modal" data-bs-target="#confirmBookingModal${room.id}">
                                                <i class="bi bi-bookmark-check me-1"></i> Book Room ${room.roomNumber}
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="${pageContext.request.contextPath}/user?action=login" class="btn btn-outline-gold w-100 py-2">
                                                <i class="bi bi-box-arrow-in-right me-1"></i> Sign In to Book
                                            </a>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>

                        <!-- Booking Confirmation Modal (B-02) -->
                        <c:if test="${sessionScope.currentUser != null}">
                            <div class="modal fade" id="confirmBookingModal${room.id}" tabindex="-1" aria-hidden="true">
                                <div class="modal-dialog modal-dialog-centered">
                                    <div class="modal-content">
                                        <div class="modal-header card-header-navy text-white">
                                            <h5 class="modal-title"><i class="bi bi-check2-circle me-2"></i> Confirm Room Reservation</h5>
                                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                                        </div>
                                        <div class="modal-body">
                                            <div class="p-3 bg-light rounded mb-3">
                                                <div class="d-flex justify-content-between mb-1">
                                                    <span class="text-muted">Room:</span>
                                                    <span class="fw-bold">Room <c:out value="${room.roomNumber}"/> (<c:out value="${room.type}"/>)</span>
                                                </div>
                                                <div class="d-flex justify-content-between mb-1">
                                                    <span class="text-muted">Dates:</span>
                                                    <span><c:out value="${checkIn}"/> &rarr; <c:out value="${checkOut}"/> (${nights} nights)</span>
                                                </div>
                                                <div class="d-flex justify-content-between mb-1">
                                                    <span class="text-muted">Guests:</span>
                                                    <span><c:out value="${guests}"/> guest(s)</span>
                                                </div>
                                                <div class="d-flex justify-content-between mb-1">
                                                    <span class="text-muted">Guest Name:</span>
                                                    <span class="fw-semibold"><c:out value="${sessionScope.currentUser.fullName}"/></span>
                                                </div>
                                                <hr class="my-2">
                                                <div class="d-flex justify-content-between align-items-center">
                                                    <span class="fw-bold text-navy">Total Confirmed Price:</span>
                                                    <span class="fs-4 fw-bold text-success">$<fmt:formatNumber value="${priceMap[room.id]}" pattern="0.00"/></span>
                                                </div>
                                                <c:if test="${sessionScope.currentUser.premium}">
                                                    <div class="small text-success text-end mt-1">Includes 10% VIP Discount!</div>
                                                </c:if>
                                            </div>
                                            <p class="small text-muted mb-0">
                                                <i class="bi bi-shield-check me-1 text-success"></i>
                                                This reservation will be locked and saved inside a secure atomic database transaction.
                                            </p>
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                            <form action="${pageContext.request.contextPath}/reservations" method="POST" class="d-inline">
                                                <input type="hidden" name="action" value="book">
                                                <input type="hidden" name="roomId" value="${room.id}">
                                                <input type="hidden" name="checkIn" value="${checkIn}">
                                                <input type="hidden" name="checkOut" value="${checkOut}">
                                                <input type="hidden" name="guests" value="${guests}">
                                                <button type="submit" class="btn btn-gold px-4">Confirm & Book</button>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>

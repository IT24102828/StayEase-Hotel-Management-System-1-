<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Luxury Boutique Hotel" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<!-- Hero Section -->
<div class="hero-section rounded-3 p-4 p-md-5 mb-5 shadow-lg">
    <div class="row align-items-center">
        <div class="col-lg-7">
            <span class="badge bg-warning text-dark mb-3 px-3 py-2 fw-semibold">
                <i class="bi bi-stars me-1"></i> Welcome to StayEase
            </span>
            <h1 class="hero-title text-white">Experience Boutique Luxury & Refined Living</h1>
            <p class="hero-subtitle">
                Discover exceptional comfort in our thoughtfully designed rooms and executive suites.
                Reserve directly online with instant availability confirmation and no double bookings.
            </p>
            <div class="d-flex flex-wrap gap-3">
                <a href="${pageContext.request.contextPath}/reservations?action=search" class="btn btn-gold btn-lg px-4">
                    <i class="bi bi-calendar2-check me-2"></i> Book Your Stay
                </a>
                <a href="${pageContext.request.contextPath}/rooms" class="btn btn-outline-light btn-lg px-4">
                    <i class="bi bi-door-open me-2"></i> View Rooms & Rates
                </a>
            </div>
        </div>
        <div class="col-lg-5 mt-4 mt-lg-0">
            <!-- Quick Availability Search Widget -->
            <div class="card card-custom p-4 shadow-lg border-top border-4 border-warning text-dark">
                <h4 class="fw-bold mb-3 text-navy">
                    <i class="bi bi-search me-2 text-gold"></i> Quick Availability
                </h4>
                <form action="${pageContext.request.contextPath}/reservations" method="GET">
                    <input type="hidden" name="action" value="search">
                    <div class="mb-3">
                        <label class="form-label small fw-semibold text-muted">Check-in Date</label>
                        <input type="date" name="checkIn" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-semibold text-muted">Check-out Date</label>
                        <input type="date" name="checkOut" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-semibold text-muted">Number of Guests</label>
                        <select name="guests" class="form-select">
                            <option value="1">1 Guest</option>
                            <option value="2" selected>2 Guests</option>
                            <option value="3">3 Guests</option>
                            <option value="4">4 Guests</option>
                        </select>
                    </div>
                    <button type="submit" class="btn btn-navy w-100 py-2">
                        <i class="bi bi-search me-1"></i> Check Free Rooms
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Key Features / Highlights -->
<div class="row g-4 mb-5">
    <div class="col-md-4">
        <div class="card card-custom p-4 text-center h-100">
            <div class="mb-3">
                <i class="bi bi-shield-check fs-1 text-success"></i>
            </div>
            <h5 class="fw-bold text-navy">Guaranteed Booking</h5>
            <p class="text-muted small mb-0">
                Atomic database row-locking guarantees zero double bookings even during concurrent peak check-outs.
            </p>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card card-custom p-4 text-center h-100">
            <div class="mb-3">
                <i class="bi bi-gem fs-1 text-warning"></i>
            </div>
            <h5 class="fw-bold text-navy">VIP Membership Perk</h5>
            <p class="text-muted small mb-0">
                Register as a Premium VIP guest to automatically receive <strong>10% off</strong> all room reservations.
            </p>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card card-custom p-4 text-center h-100">
            <div class="mb-3">
                <i class="bi bi-arrow-repeat fs-1 text-primary"></i>
            </div>
            <h5 class="fw-bold text-navy">Flexible Cancellation</h5>
            <p class="text-muted small mb-0">
                Cancel your reservation anytime with one click. Cancelled dates are instantly restored to availability.
            </p>
        </div>
    </div>
</div>

<!-- Featured Rooms Preview -->
<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h2 class="h3 fw-bold text-navy mb-1">Our Signature Accommodations</h2>
        <p class="text-muted mb-0">From cozy single rooms to expansive executive suites</p>
    </div>
    <a href="${pageContext.request.contextPath}/rooms" class="btn btn-outline-gold">
        View All <i class="bi bi-arrow-right ms-1"></i>
    </a>
</div>

<div class="row g-4 mb-5">
    <c:forEach var="room" items="${featuredRooms}">
        <div class="col-lg-4 col-md-6">
            <div class="card card-custom h-100 d-flex flex-column">
                <div class="card-header bg-white pt-3 pb-2 d-flex justify-content-between align-items-center border-0">
                    <span class="badge room-badge-type"><c:out value="${room.type}"/> Room</span>
                    <span class="badge room-badge-avail"><i class="bi bi-check-circle me-1"></i> Available</span>
                </div>
                <div class="card-body d-flex flex-column justify-content-between">
                    <div>
                        <h4 class="fw-bold text-navy mb-2">Room <c:out value="${room.roomNumber}"/></h4>
                        <div class="small text-muted mb-2">
                            <i class="bi bi-people-fill me-1"></i> Up to <c:out value="${room.capacity}"/> guest(s)
                        </div>
                        <div class="small text-secondary mb-3">
                            <c:out value="${room.features}"/>
                        </div>
                        <c:if test="${not empty room.description}">
                            <p class="small text-muted fst-italic mb-3"><c:out value="${room.description}"/></p>
                        </c:if>
                    </div>

                    <div class="border-top pt-3 d-flex justify-content-between align-items-center">
                        <div>
                            <span class="fs-4 fw-bold text-success">$<fmt:formatNumber value="${room.nightlyRate}" pattern="0.00"/></span>
                            <span class="small text-muted">/ night</span>
                        </div>
                        <a href="${pageContext.request.contextPath}/reservations?action=search" class="btn btn-sm btn-navy px-3">
                            Check Dates
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </c:forEach>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>

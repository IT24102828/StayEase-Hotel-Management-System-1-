<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Add New Room" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="row justify-content-center">
    <div class="col-lg-8">
        <nav aria-label="breadcrumb" class="mb-3">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/rooms">Rooms</a></li>
                <li class="breadcrumb-item active" aria-current="page">Add New Room</li>
            </ol>
        </nav>

        <div class="card card-custom shadow-sm mb-4">
            <div class="card-header card-header-navy">
                <h4 class="mb-0 text-white"><i class="bi bi-plus-circle me-2"></i> Add New Hotel Room</h4>
            </div>
            <div class="card-body p-4">
                <form action="${pageContext.request.contextPath}/rooms" method="POST" id="addRoomForm" novalidate>
                    <input type="hidden" name="action" value="insert">

                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label for="roomNumber" class="form-label fw-semibold">
                                Room Number <span class="text-danger">*</span>
                            </label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-hash"></i></span>
                                <input type="text" class="form-control" id="roomNumber" name="roomNumber"
                                       maxlength="10" required placeholder="e.g. 401"
                                       value="<c:out value='${inputRoomNumber}'/>">
                            </div>
                            <div class="form-text">Unique room identifier (maximum 10 characters).</div>
                        </div>

                        <div class="col-md-6">
                            <label for="type" class="form-label fw-semibold">
                                Room Type <span class="text-danger">*</span>
                            </label>
                            <select class="form-select" id="type" name="type" required>
                                <option value="">-- Select Room Type --</option>
                                <option value="Single" ${inputType == 'Single' ? 'selected' : ''}>Single (Capacity: 1 Guest)</option>
                                <option value="Double" ${inputType == 'Double' ? 'selected' : ''}>Double (Capacity: 2 Guests)</option>
                                <option value="Suite" ${inputType == 'Suite' ? 'selected' : ''}>Suite (Capacity: 4 Guests)</option>
                            </select>
                            <div class="form-text">Determines capacity, features, and nightly formula.</div>
                        </div>
                    </div>

                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label for="basePrice" class="form-label fw-semibold">
                                Base Price ($/night) <span class="text-danger">*</span>
                            </label>
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" step="0.01" min="0.01" class="form-control" id="basePrice"
                                       name="basePrice" required placeholder="e.g. 95.00"
                                       value="<c:out value='${inputBasePrice}'/>">
                            </div>
                            <div class="form-text">Must be greater than $0.00.</div>
                        </div>

                        <div class="col-md-6">
                            <label for="status" class="form-label fw-semibold">
                                Operational Status <span class="text-danger">*</span>
                            </label>
                            <select class="form-select" id="status" name="status" required>
                                <option value="Available" ${inputStatus != 'Maintenance' ? 'selected' : ''}>Available (Bookable)</option>
                                <option value="Maintenance" ${inputStatus == 'Maintenance' ? 'selected' : ''}>Maintenance (Blocked from Booking)</option>
                            </select>
                            <div class="form-text">Maintenance rooms are automatically hidden from guest searches.</div>
                        </div>
                    </div>

                    <div class="mb-4">
                        <label for="description" class="form-label fw-semibold">Description & Amenities</label>
                        <textarea class="form-control" id="description" name="description" rows="3"
                                  maxlength="255" placeholder="Optional notes, view orientation, or special features (up to 255 chars)"><c:out value='${inputDescription}'/></textarea>
                    </div>

                    <!-- Pricing Info Notice -->
                    <div class="alert alert-light border p-3 mb-4 rounded">
                        <div class="fw-semibold text-primary-navy mb-2"><i class="bi bi-info-circle me-1"></i> Polymorphic Room Pricing Rules:</div>
                        <ul class="small text-muted mb-0 ps-3">
                            <li><strong>Single:</strong> Nightly Rate = Base Price (Capacity: 1)</li>
                            <li><strong>Double:</strong> Nightly Rate = Base Price &times; 1.10 (+10% markup, Capacity: 2)</li>
                            <li><strong>Suite:</strong> Nightly Rate = (Base Price &times; 1.25) + $20.00 service surcharge (Capacity: 4)</li>
                        </ul>
                    </div>

                    <div class="d-flex justify-content-between align-items-center">
                        <a href="${pageContext.request.contextPath}/rooms" class="btn btn-outline-secondary">
                            <i class="bi bi-arrow-left me-1"></i> Cancel
                        </a>
                        <button type="submit" class="btn btn-gold px-4">
                            <i class="bi bi-check2-circle me-1"></i> Save Room
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>

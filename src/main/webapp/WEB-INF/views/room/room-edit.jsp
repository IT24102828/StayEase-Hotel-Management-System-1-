<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Edit Room ${room.roomNumber}" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="row justify-content-center">
    <div class="col-lg-8">
        <nav aria-label="breadcrumb" class="mb-3">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/rooms">Rooms</a></li>
                <li class="breadcrumb-item active" aria-current="page">Edit Room <c:out value="${room.roomNumber}"/></li>
            </ol>
        </nav>

        <div class="card card-custom shadow-sm mb-4">
            <div class="card-header card-header-navy d-flex justify-content-between align-items-center">
                <h4 class="mb-0 text-white"><i class="bi bi-pencil-square me-2"></i> Edit Hotel Room <c:out value="${room.roomNumber}"/></h4>
                <span class="badge bg-warning text-dark">ID: <c:out value="${room.id}"/></span>
            </div>
            <div class="card-body p-4">
                <form action="${pageContext.request.contextPath}/rooms" method="POST" id="editRoomForm" novalidate>
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="id" value="${room.id}">

                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label for="roomNumber" class="form-label fw-semibold">
                                Room Number <span class="text-danger">*</span>
                            </label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-hash"></i></span>
                                <input type="text" class="form-control" id="roomNumber" name="roomNumber"
                                       maxlength="10" required placeholder="e.g. 102"
                                       value="<c:out value='${room.roomNumber}'/>">
                            </div>
                        </div>

                        <div class="col-md-6">
                            <label for="type" class="form-label fw-semibold">
                                Room Type <span class="text-danger">*</span>
                            </label>
                            <select class="form-select" id="type" name="type" required>
                                <option value="Single" ${room.type == 'Single' ? 'selected' : ''}>Single (Capacity: 1 Guest)</option>
                                <option value="Double" ${room.type == 'Double' ? 'selected' : ''}>Double (Capacity: 2 Guests)</option>
                                <option value="Suite" ${room.type == 'Suite' ? 'selected' : ''}>Suite (Capacity: 4 Guests)</option>
                            </select>
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
                                       name="basePrice" required placeholder="e.g. 70.00"
                                       value="${room.basePrice}">
                            </div>
                            <div class="form-text">
                                Current calculated nightly rate:
                                <strong class="text-success">$<fmt:formatNumber value="${room.nightlyRate}" pattern="0.00"/></strong>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <label for="status" class="form-label fw-semibold">
                                Operational Status <span class="text-danger">*</span>
                            </label>
                            <select class="form-select" id="status" name="status" required>
                                <option value="Available" ${room.status == 'Available' ? 'selected' : ''}>Available (Bookable)</option>
                                <option value="Maintenance" ${room.status == 'Maintenance' ? 'selected' : ''}>Maintenance (Blocked from Booking)</option>
                            </select>
                        </div>
                    </div>

                    <div class="mb-4">
                        <label for="description" class="form-label fw-semibold">Description & Amenities</label>
                        <textarea class="form-control" id="description" name="description" rows="3"
                                  maxlength="255"><c:out value='${room.description}'/></textarea>
                    </div>

                    <div class="d-flex justify-content-between align-items-center">
                        <a href="${pageContext.request.contextPath}/rooms" class="btn btn-outline-secondary">
                            <i class="bi bi-arrow-left me-1"></i> Cancel
                        </a>
                        <button type="submit" class="btn btn-gold px-4">
                            <i class="bi bi-check2-circle me-1"></i> Update Room
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>

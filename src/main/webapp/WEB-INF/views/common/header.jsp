<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle != null ? pageTitle : 'StayEase Hotel'}"/> | StayEase Hotel Reservation</title>
    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=Playfair+Display:ital,wght@0,600;0,700;1,600&display=swap" rel="stylesheet">
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <!-- Custom StayEase CSS -->
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>
<body>

<nav class="navbar navbar-expand-lg stayease-navbar sticky-top">
    <div class="container">
        <a class="navbar-brand stayease-brand d-flex align-items-center" href="${pageContext.request.contextPath}/">
            <i class="bi bi-building-check me-2" style="color: var(--luxury-gold);"></i>
            StayEase<span>.</span>
        </a>
        <button class="navbar-toggler text-white border-0" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent"
                aria-controls="navbarContent" aria-expanded="false" aria-label="Toggle navigation">
            <i class="bi bi-list fs-2 text-white"></i>
        </button>

        <div class="collapse navbar-collapse" id="navbarContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-3">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/">
                        <i class="bi bi-house-door me-1"></i> Home
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/rooms">
                        <i class="bi bi-door-open me-1"></i> Browse Rooms
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/reservations?action=search">
                        <i class="bi bi-calendar-check me-1"></i> Check Availability
                    </a>
                </li>

                <!-- Admin Only Menus -->
                <c:if test="${sessionScope.currentUser != null && sessionScope.currentUser.admin}">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle text-warning" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                            <i class="bi bi-shield-lock me-1"></i> Admin Console
                        </a>
                        <ul class="dropdown-menu dropdown-menu-dark shadow">
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/rooms"><i class="bi bi-grid me-2"></i>Manage Rooms</a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/rooms?action=new"><i class="bi bi-plus-circle me-2"></i>Add New Room</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/reservations?action=list"><i class="bi bi-journal-text me-2"></i>All Reservations</a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user?action=list"><i class="bi bi-people me-2"></i>Manage Guests</a></li>
                        </ul>
                    </li>
                </c:if>

                <!-- Guest Only Menu -->
                <c:if test="${sessionScope.currentUser != null && !sessionScope.currentUser.admin}">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/reservations?action=list">
                            <i class="bi bi-journal-bookmark me-1"></i> My Bookings
                        </a>
                    </li>
                </c:if>
            </ul>

            <div class="d-flex align-items-center gap-2">
                <c:choose>
                    <c:when test="${sessionScope.currentUser != null}">
                        <div class="dropdown">
                            <button class="btn btn-sm btn-outline-light dropdown-toggle d-flex align-items-center gap-2 px-3 py-2 rounded-pill"
                                    type="button" data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="bi bi-person-circle fs-6"></i>
                                <span><c:out value="${sessionScope.currentUser.fullName}"/></span>
                                <c:choose>
                                    <c:when test="${sessionScope.currentUser.admin}">
                                        <span class="badge badge-admin ms-1">Admin</span>
                                    </c:when>
                                    <c:when test="${sessionScope.currentUser.premium}">
                                        <span class="badge badge-premium ms-1"><i class="bi bi-star-fill text-dark me-1"></i>VIP</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-regular ms-1">Guest</span>
                                    </c:otherwise>
                                </c:choose>
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end shadow">
                                <li class="px-3 py-2 border-bottom">
                                    <div class="small text-muted">Signed in as</div>
                                    <div class="fw-bold"><c:out value="${sessionScope.currentUser.email}"/></div>
                                </li>
                                <li>
                                    <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/user?action=profile">
                                        <i class="bi bi-gear me-2"></i> Profile Settings
                                    </a>
                                </li>
                                <c:if test="${!sessionScope.currentUser.admin}">
                                    <li>
                                        <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/reservations?action=list">
                                            <i class="bi bi-calendar3 me-2"></i> My Reservations
                                        </a>
                                    </li>
                                </c:if>
                                <li><hr class="dropdown-divider"></li>
                                <li>
                                    <a class="dropdown-item py-2 text-danger" href="${pageContext.request.contextPath}/user?action=logout">
                                        <i class="bi bi-box-arrow-right me-2"></i> Log Out
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/user?action=login" class="btn btn-outline-gold btn-sm px-3 py-2 rounded-pill">
                            <i class="bi bi-box-arrow-in-right me-1"></i> Log In
                        </a>
                        <a href="${pageContext.request.contextPath}/user?action=register" class="btn btn-gold btn-sm px-3 py-2 rounded-pill">
                            <i class="bi bi-person-plus me-1"></i> Register
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</nav>

<main class="py-4">
    <div class="container">
        <!-- Reusable Alert Messages -->
        <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>

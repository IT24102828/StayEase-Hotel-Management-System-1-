<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    </div><!-- container -->
</main>

<footer class="stayease-footer">
    <div class="container">
        <div class="row g-4 mb-4">
            <div class="col-lg-4 col-md-6">
                <h5 class="brand-font fs-4 text-white">
                    <i class="bi bi-building-check me-2" style="color: var(--luxury-gold);"></i>StayEase Hotel
                </h5>
                <p class="small text-secondary">
                    Providing refined comfort, boutique hospitality, and seamless room booking experiences. Built for SE1020 Object Oriented Programming.
                </p>
                <div class="d-flex gap-3 text-secondary">
                    <span class="badge bg-secondary">Java 17</span>
                    <span class="badge bg-secondary">Tomcat 10.1</span>
                    <span class="badge bg-secondary">MySQL 8</span>
                    <span class="badge bg-secondary">Jakarta EE</span>
                </div>
            </div>
            <div class="col-lg-2 col-md-6">
                <h5>Navigation</h5>
                <ul class="list-unstyled small d-flex flex-column gap-2">
                    <li><a href="${pageContext.request.contextPath}/"><i class="bi bi-chevron-right me-1"></i> Home</a></li>
                    <li><a href="${pageContext.request.contextPath}/rooms"><i class="bi bi-chevron-right me-1"></i> Rooms & Suites</a></li>
                    <li><a href="${pageContext.request.contextPath}/reservations?action=search"><i class="bi bi-chevron-right me-1"></i> Check Availability</a></li>
                    <li><a href="${pageContext.request.contextPath}/user?action=login"><i class="bi bi-chevron-right me-1"></i> Account Portal</a></li>
                </ul>
            </div>
            <div class="col-lg-3 col-md-6">
                <h5>VIP Membership</h5>
                <p class="small text-secondary">
                    Unlock exclusive benefits: Enjoy an automatic <strong>10% polymorphic discount</strong> on all room reservations with StayEase Premium VIP membership.
                </p>
            </div>
            <div class="col-lg-3 col-md-6">
                <h5>Contact & Location</h5>
                <p class="small text-secondary mb-1"><i class="bi bi-geo-alt me-2" style="color: var(--luxury-gold);"></i> 100 Galle Road, Colombo 03, Sri Lanka</p>
                <p class="small text-secondary mb-1"><i class="bi bi-telephone me-2" style="color: var(--luxury-gold);"></i> +94 (11) 234-5678</p>
                <p class="small text-secondary mb-1"><i class="bi bi-envelope me-2" style="color: var(--luxury-gold);"></i> reservations@stayease.com</p>
            </div>
        </div>

        <hr class="border-secondary opacity-25">

        <div class="d-flex flex-column flex-sm-row justify-content-between align-items-center small text-secondary">
            <div>&copy; 2026 StayEase Hotel Reservation System. All rights reserved.</div>
            <div class="mt-2 mt-sm-0">SE1020 OOP University Project &bull; Developed by Two-Member Team</div>
        </div>
    </div>
</footer>

<!-- Bootstrap 5 Bundle JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

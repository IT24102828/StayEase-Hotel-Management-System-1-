# StayEase Hotel Reservation System - Local Setup & Deployment Guide

This guide provides step-by-step instructions for setting up, building, and deploying the **StayEase** system on macOS, Windows, or Linux.

---

## 1. Prerequisites

Ensure the following tools are installed on your workstation:

| Tool | Minimum Version | Verification Command |
|---|---|---|
| **Java Development Kit (JDK)** | OpenJDK 17 or 21 | `java -version` |
| **Apache Maven** | 3.8.0 or newer | `mvn -version` |
| **MySQL Database Server** | 8.0 or newer | `mysql --version` |
| **Apache Tomcat** | 10.1.x (Jakarta EE 10) | `catalina.sh version` |
| **IntelliJ IDEA** | Community or Ultimate | — |

> **IMPORTANT:** You must use **Apache Tomcat 10.1**, not Tomcat 9 or older. Tomcat 10 uses the modern `jakarta.servlet.*` package namespace, whereas Tomcat 9 uses legacy `javax.servlet.*`.

---

## 2. Database Initialization (MySQL 8)

1. Start your local MySQL service:
   - **macOS (Homebrew):** `brew services start mysql`
   - **Windows:** Start MySQL from Windows Services (`services.msc`)
   - **Linux:** `sudo systemctl start mysql`

2. Log into MySQL and create the database schema:
   ```bash
   mysql -u root -p < sql/schema.sql
   ```

3. Verify the tables and sample data were created:
   ```sql
   USE stayease_db;
   SHOW TABLES;
   -- Should display: reservations, rooms, users
   SELECT id, full_name, role FROM users;
   SELECT id, room_number, type, base_price, status FROM rooms;
   SELECT id, user_id, room_id, total_price, status FROM reservations;
   ```

---

## 3. Configure Database Credentials

Open `src/main/resources/db.properties` and verify your local connection parameters:

```properties
# StayEase Database Configuration
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/stayease_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
db.user=root
db.password=your_mysql_password
```

*(If you set a root password in MySQL, enter it here. If no password was set, leave `db.password=` empty.)*

---

## 4. Run Automated Tests

Execute the full automated test suite using Maven:

```bash
mvn clean test
```

**Expected output:**
```
[INFO] Running com.stayease.dao.ReservationTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.stayease.model.RoomTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.stayease.model.UserTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] Tests run: 24, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 5. Build Deployable WAR Artifact

Package the application:

```bash
mvn package
```

The output WAR file will be generated at:
```
target/stayease.war
```

---

## 6. Deployment to Apache Tomcat 10.1

### Option A: Standalone Tomcat Deployment (Recommended for Viva Demo)

1. Locate your Apache Tomcat 10.1 installation directory.
2. Copy the generated WAR file into Tomcat's `webapps/` folder:
   ```bash
   cp target/stayease.war /path/to/apache-tomcat-10.1.xx/webapps/
   ```
3. Start Tomcat:
   - **macOS / Linux:**
     ```bash
     /path/to/apache-tomcat-10.1.xx/bin/startup.sh
     ```
   - **Windows:**
     ```cmd
     C:\path\to\apache-tomcat-10.1.xx\bin\startup.bat
     ```
4. Open your web browser and navigate to:  
   **`http://localhost:8080/stayease/`**

### Option B: IntelliJ IDEA Ultimate Deployment

1. Open IntelliJ IDEA and open the project directory (`Hotel_managemnt System`).
2. Go to **Run &rarr; Edit Configurations...**
3. Click **+ &rarr; Tomcat Server &rarr; Local**.
4. Set Application server to your Tomcat 10.1 directory.
5. In the **Deployment** tab, click **+ &rarr; Artifact...** and select `hotel-room-reservation:war`.
6. Set the Application context to `/stayease`.
7. Click **Apply** and **Run**.

---

## 7. Sample Accounts for Testing

| Persona | Email | Password | Role & Permissions |
|---|---|---|---|
| **Admin** | `admin@stayease.com` | `Password@123` | Can manage rooms, view all reservations, search & delete guests |
| **Regular Guest** | `nimal@gmail.com` | `Password@123` | Can search rooms, book at standard rates, cancel own stays |
| **VIP Premium Guest** | `sunil@gmail.com` | `Password@123` | Receives 10% discount on all bookings, cancel own stays |

---

## 8. Troubleshooting

* **Problem: `java.lang.ClassNotFoundException: jakarta.servlet.http.HttpServlet`**  
  *Cause:* Application deployed to Tomcat 9 or older.  
  *Fix:* Install and deploy to **Tomcat 10.1**, which implements Jakarta EE 10.

* **Problem: `Communications link failure` on database connection**  
  *Cause:* MySQL service is stopped or port is not 3306.  
  *Fix:* Start MySQL with `brew services start mysql` or check credentials in `src/main/resources/db.properties`.

* **Problem: Port 8080 already in use**  
  *Cause:* Another process is listening on port 8080.  
  *Fix:* Change port in Tomcat's `conf/server.xml` to `8081` (e.g., `<Connector port="8081" protocol="HTTP/1.1" ... />`).

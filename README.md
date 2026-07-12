# CareHub Version 2.0 — AI-Based Hospital Inventory & Management System

A full-stack hospital inventory & management system built with
**Spring Boot 3 + Spring Security + Spring Data JPA + MySQL** on the backend,
and a plain **HTML / CSS / JavaScript** dashboard on the frontend — served
directly by Spring Boot, so there's nothing extra to install or run
separately.

This is the "Version 2.0" build: on top of full CRUD for every module it adds
**BCrypt + Spring Security role-based access control**, an **AI inventory
engine** (stock prediction, smart reorder suggestions, fast/slow movers),
**low-stock & expiry automation** (including supplier emails and purchase
orders), **PDF/Excel reports**, and **dashboard charts**.

---

## 1. What's inside

| Layer      | Tech                                                              |
|------------|--------------------------------------------------------------------|
| Backend    | Spring Boot 3.3, Spring Security, Spring Data JPA, Bean Validation  |
| Database   | MySQL (auto-creates the schema via `ddl-auto=update`)              |
| Security   | Spring Security sessions + BCrypt password hashing                 |
| AI         | Java heuristic engine over real dispensing history (no external ML)|
| Reports    | OpenPDF (PDF) + Apache POI (Excel)                                  |
| Email      | Spring Boot Mail (SMTP) — optional, degrades gracefully if unset    |
| Frontend   | Static HTML/CSS/JS in `src/main/resources/static`, Chart.js for charts |
| Build tool | Maven                                                               |

All 11 tables are implemented end-to-end (entity → repository → REST
controller → UI screen):

`Users, Doctors, Patients, Suppliers, Medicines, Inventory, Prescriptions,
Prescription_Items, Bills, Bill_Items, Purchase_Orders`

---

## 2. Import into STS (Spring Tool Suite)

1. Unzip this project anywhere on disk.
2. Open STS → **File → Import… → Maven → Existing Maven Projects**.
3. Browse to the unzipped `carehub` folder → Finish.
4. Wait for Maven to download dependencies (bottom-right progress bar).

## 3. Set up MySQL

1. Make sure MySQL Server is running locally.
2. You don't need to create the database by hand — the connection string
   `?createDatabaseIfNotExist=true` in `application.properties` does it for
   you the first time the app starts. You only need MySQL itself running
   with a user that can create databases.
3. Open `src/main/resources/application.properties` and update the
   username/password if your local MySQL isn't `root` / `root`:

   ```properties
   spring.datasource.username=root
   spring.datasource.password=root
   ```

## 4. (Optional) Set up email for supplier notifications

The AI/automation module emails suppliers when stock drops below minimum.
This is entirely optional — if you leave the mail settings blank, the app
runs fine and simply logs "could not send email" instead of crashing.

To enable it (e.g. with Gmail SMTP + an
[app password](https://myaccount.google.com/apppasswords)):

```properties
spring.mail.username=your-address@gmail.com
spring.mail.password=your-16-char-app-password
```

## 5. Run the app

In STS: right-click **CarehubApplication.java → Run As → Spring Boot App**.

Or from a terminal:

```bash
cd carehub
mvn spring-boot:run
```

The app starts on **http://localhost:8080**. Hibernate creates all tables
automatically on first launch (`spring.jpa.hibernate.ddl-auto=update`).

## 6. First login

On first startup, if the `users` table is empty, CareHub automatically seeds
a default Admin account (see the console log):

```
username: admin
password: Admin@123
```

**Sign in and change this password immediately** (Users page → Reset
Password action). From there, use the **Users** page to create Doctor,
Pharmacist, and Receptionist accounts — self-registration is disabled once
at least one account exists, so role assignment can't be self-granted.

## 7. Role-based access control

Every module is protected server-side by Spring Security, matching the
CareHub role matrix:

| Module              | Admin | Doctor | Receptionist | Pharmacist |
|----------------------|:---:|:---:|:---:|:---:|
| Dashboard             | ✅ | ✅ | ✅ | ✅ |
| Patients               | ✅ | view only | ✅ | — |
| Doctors                | ✅ | view only | view only | — |
| Medicines              | ✅ | view only | — | ✅ |
| Inventory              | ✅ | — | — | ✅ |
| Suppliers              | ✅ | — | — | ✅ |
| Purchase Orders        | ✅ (approve) | — | — | ✅ (create) |
| AI Insights            | ✅ | — | — | ✅ |
| Prescriptions          | ✅ | ✅ | — | view only |
| Billing                | ✅ | — | ✅ | — |
| Reports                | ✅ | — | — | — |
| Users / Staff          | ✅ | — | — | — |

The sidebar only shows links a signed-in user can actually use, but the real
enforcement happens in `SecurityConfig.java` — hitting a restricted API
directly returns `403 Forbidden`.

## 8. AI & automation features

- **AI Stock Prediction** (`/api/ai/stock-predictions`) — average daily usage
  is computed from real `Bill_Items` over the last 30 days; days-until-empty
  is `current_stock / avg_daily_usage`.
- **Smart Reorder Recommendation** (`/api/ai/reorder-recommendations`) —
  suggests a quantity that covers ~30 more days of demand for every
  below-minimum medicine, with a one-click "Create Purchase Order" button.
- **Fast / Slow Moving Medicines** (`/api/ai/fast-moving`, `/api/ai/slow-moving`).
- **Low Stock / Expiry Alerts** — dashboard panels + `/api/dashboard/low-stock`
  and `/api/dashboard/expiring?days=30|60|90`.
- **Supplier Email automation** — fired automatically when a Purchase Order
  is approved, available on-demand from the Suppliers page, and swept once
  daily by a scheduled job (`ScheduledTasks.java`, runs at 00:15).
- **Purchase Orders** — Pharmacist creates (manually or from an AI
  recommendation), Admin approves/rejects, either can mark as Received.
- **Automatic stock decrement** — creating a Bill Item decrements the
  earliest-expiring Inventory batch (FIFO) for that medicine, which is what
  feeds the AI usage numbers.

## 9. Reports

The **Reports** page (Admin only) generates PDF and Excel downloads for
Inventory, Patients, Doctors, and Revenue/Billing — `/api/reports/...`.

## 10. REST API

Every table has a full REST CRUD API under `/api/...`, e.g.:

```
GET    /api/patients
GET    /api/patients/{id}
POST   /api/patients
PUT    /api/patients/{id}
DELETE /api/patients/{id}
```

Same pattern for `/api/users`, `/api/doctors`, `/api/suppliers`,
`/api/medicines`, `/api/inventory`, `/api/prescriptions`,
`/api/prescription-items`, `/api/bills`, `/api/bill-items`,
`/api/purchase-orders`, plus:

```
POST   /api/auth/login             { "username": "...", "password": "..." }
POST   /api/auth/logout
GET    /api/auth/me
GET    /api/dashboard/summary
GET    /api/dashboard/low-stock
GET    /api/dashboard/expiring?days=30
GET    /api/dashboard/charts
GET    /api/ai/stock-predictions
GET    /api/ai/reorder-recommendations
GET    /api/ai/fast-moving?limit=10
GET    /api/ai/slow-moving
PATCH  /api/purchase-orders/{id}/approve
PATCH  /api/purchase-orders/{id}/reject
PATCH  /api/purchase-orders/{id}/received
POST   /api/suppliers/{id}/notify/{inventoryId}
PATCH  /api/users/{id}/reset-password   { "newPassword": "..." }
PATCH  /api/users/{id}/role             { "role": "Pharmacist" }
PATCH  /api/users/{id}/activate
PATCH  /api/users/{id}/deactivate
GET    /api/reports/{inventory|patients|doctors|revenue}/{pdf|excel}
```

For entities with foreign keys (e.g. Inventory → Medicine), send the related
id nested, e.g.:

```json
{
  "medicine": { "medicineId": 3 },
  "supplier": { "supplierId": 1 },
  "batchNo": "B-2026-001",
  "expiryDate": "2027-01-01",
  "unitPrice": 12.50,
  "availableStock": 100,
  "minimumStock": 20
}
```

Authentication is session-based: `/api/auth/login` sets a session cookie,
and the browser sends it automatically on every subsequent request — no
token handling needed on the frontend.

## 11. Project structure

```
carehub/
├── pom.xml
├── src/main/java/com/carehub/carehub/
│   ├── CarehubApplication.java
│   ├── entity/         (11 JPA entities)
│   ├── repository/     (11 Spring Data repositories, with AI/report queries)
│   ├── service/         (UserService, AiService, AlertService, EmailService,
│   │                      DashboardService, PurchaseOrderService, ReportService)
│   ├── controller/      (CRUD REST controllers, AuthController, AiController,
│   │                      DashboardController, PurchaseOrderController, ReportController)
│   └── config/          (SecurityConfig, CustomUserDetailsService, DataSeeder,
│                          ScheduledTasks, error handling)
└── src/main/resources/
    ├── application.properties
    └── static/           (login, dashboard, AI insights, purchase orders,
                            reports, and CRUD screens + css/js)
```

## 12. Security notes

- Passwords are hashed with BCrypt (`spring-boot-starter-security` default
  strength) — never stored or logged in plain text.
- Sessions expire after 30 minutes of inactivity (`server.servlet.session.timeout`).
- CSRF protection is disabled for simplicity (typical for a JSON API consumed
  by a same-origin SPA-style frontend); if you deploy this behind a different
  origin, re-enable CSRF or switch to a token-based auth scheme.
- Deactivated users (`isActive = false`) cannot log in even with the correct
  password.

## 13. Future enhancements

Appointment Management, Laboratory Module, Nurse Module, SMS/WhatsApp
alerts, QR code prescriptions, barcode medicine scanning, voice assistant,
facial recognition login, cloud backup, mobile app, multi-hospital support,
and a full AI chatbot for medicine suggestions.

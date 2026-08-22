# Dayflow - Human Resource Management System (HRMS)

> Every workday, perfectly aligned.

## 🚀 Overview
Dayflow is a modern, enterprise-grade Human Resource Management System built to digitize and streamline workforce operations including employee onboarding, role-based dashboards, live attendance tracking, automated leave approval workflows, transparent salary structures, and company analytics.

## 👥 User Roles & Access
- **Admin / HR Officer:** Workforce directory, attendance override adjustments, leave approval queue with remarks, payroll configuration & batch disbursement, intelligence analytics.
- **Employee:** Workday check-in/check-out with live stopwatch timer, daily/weekly/monthly attendance calendar, apply for leave (Paid, Sick, Unpaid) with working-day calculator, self-service profile editing, read-only salary structure & printable payslips.

## 🛠️ Core Modules
- **Authentication & RBAC:** Email & OTP verification, password strength security analyzer, 1-click persona demo accounts.
- **Workday & Attendance Tracker:** Real-time punch terminal, daily timeline spread, weekly 7-day cards, interactive monthly calendar.
- **Leave & Time-Off:** Multi-category quotas (Paid, Sick, Casual, Unpaid), half-day options, document attachments, HR approval pipeline.
- **Payroll & Compensation:** Full transparent compensation breakdown, monthly payslip generator with printable and downloadable PDFs.
- **Employee Portal:** Dedicated employee self-service dashboard (`employee-dashboard/`).

## 💻 Tech Stack
- **Frontend:** React 19, Vite, Lucide Icons, Canvas-Confetti, Modern CSS Design System (Dark/Light mode).
- **Backend:** Spring Boot (Java 17), Spring Security, JWT, JPA / Hibernate, Flyway Migrations, PostgreSQL.

## ⚙️ Getting Started

### Run the Frontend
```bash
npm install
npm run dev
```
Open [http://localhost:5173](http://localhost:5173) in your browser.

### Run the Employee Dashboard Standalone
```bash
cd employee-dashboard
npm install
npm run dev
```
Open [http://localhost:5175](http://localhost:5175) in your browser.

### Run the Backend
```bash
cd backend
./mvnw spring-boot:run
```

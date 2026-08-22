# DAYFLOW — HUMAN RESOURCE MANAGEMENT SYSTEM

## Backend Implementation Specification

> **This document is ONLY for implementing the backend.**
>
> Do not create or modify any frontend application.
>
> Backend technology: **Java + Spring Boot**
>
> Database: **Supabase PostgreSQL**

---

# 1. Objective

Build the complete backend for the **Dayflow Human Resource Management System**.

The backend must expose secure REST APIs that can later be consumed by a separate frontend application.

The backend must handle:

* Authentication integration
* Authorization
* Employee management
* Employee profiles
* Attendance
* Leave management
* Leave approval/rejection
* Payroll
* Documents
* Notifications
* Dashboard data
* Reports
* Validation
* Error handling
* Auditing
* Database persistence
* Supabase integration

Do NOT implement frontend pages, React components, Node.js frontend code, HTML pages, CSS, or UI components.

---

# 2. Technology Stack

Use:

```text
Java 21
Spring Boot 3.x
Maven
Spring Web
Spring Data JPA
Hibernate
Spring Security
Jakarta Bean Validation
PostgreSQL Driver
Lombok
Flyway
Springdoc OpenAPI
JUnit 5
Mockito
MockMvc
```

External services:

```text
Supabase PostgreSQL
Supabase Auth
Supabase Storage
```

---

# 3. Architecture

Use a layered architecture.

```text
HTTP Request
     |
     v
Controller
     |
     v
Service
     |
     v
Repository
     |
     v
Supabase PostgreSQL
```

Security:

```text
HTTP Request
     |
     v
Spring Security
     |
     v
JWT Validation
     |
     v
Role / Permission Check
     |
     v
Controller
```

Do not put business logic inside controllers.

Do not access repositories directly from controllers.

---

# 4. Project Structure

Create:

```text
dayflow-backend/
│
├── pom.xml
├── README.md
├── .gitignore
├── .env.example
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── dayflow/
│   │   │           └── hrms/
│   │   │               │
│   │   │               ├── DayflowApplication.java
│   │   │               │
│   │   │               ├── config/
│   │   │               ├── controller/
│   │   │               ├── dto/
│   │   │               ├── entity/
│   │   │               ├── exception/
│   │   │               ├── mapper/
│   │   │               ├── repository/
│   │   │               ├── security/
│   │   │               ├── service/
│   │   │               └── util/
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/
│   │           └── migration/
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── dayflow/
│                   └── hrms/
```

---

# 5. Package Responsibilities

## controller

Contains REST controllers only.

Examples:

```text
EmployeeController
ProfileController
AttendanceController
LeaveController
PayrollController
DocumentController
NotificationController
DashboardController
ReportController
```

Controllers must:

* Receive HTTP requests
* Validate DTOs
* Call services
* Return HTTP responses

Controllers must NOT contain business logic.

---

## service

Contains all business logic.

Examples:

```text
EmployeeService
ProfileService
AttendanceService
LeaveService
PayrollService
DocumentService
NotificationService
DashboardService
ReportService
```

---

## repository

Contains Spring Data JPA repositories.

---

## entity

Contains JPA entities.

---

## dto

Contains request and response DTOs.

Never expose JPA entities directly through REST APIs.

---

## security

Contains:

* JWT authentication
* authenticated-user handling
* role handling
* security utilities
* authorization support

---

## exception

Contains:

* custom exceptions
* global exception handler
* API error response

---

# 6. Supabase Architecture

Use Supabase as the backend infrastructure.

```text
                  Supabase
                     |
       +-------------+-------------+
       |             |             |
       v             v             v
 PostgreSQL       Auth          Storage
       |
       |
 Spring Boot Backend
```

Spring Boot is responsible for application business logic.

Supabase PostgreSQL is responsible for persistent relational data.

Supabase Storage is responsible for uploaded files.

Supabase Auth is responsible for user authentication.

---

# 7. Database Configuration

Use PostgreSQL.

Do not use MySQL.

Do not create an H2 database as the actual application database.

Development and production database credentials must come from environment variables.

Example:

```yaml
spring:
  datasource:
    url: ${SUPABASE_DB_URL}
    username: ${SUPABASE_DB_USERNAME}
    password: ${SUPABASE_DB_PASSWORD}

  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
```

Never hardcode database credentials.

---

# 8. Environment Variables

Create:

```text
.env.example
```

Include placeholders such as:

```env
SUPABASE_URL=
SUPABASE_ANON_KEY=
SUPABASE_SERVICE_ROLE_KEY=

SUPABASE_DB_URL=
SUPABASE_DB_USERNAME=
SUPABASE_DB_PASSWORD=

SUPABASE_JWT_ISSUER=

FRONTEND_URL=http://localhost:5173
```

The real `.env` file must never be committed.

Add `.env` to `.gitignore`.

---

# 9. Authentication

Use **Supabase Auth** for authentication.

Do not build a second password authentication system.

Do not store passwords inside the application's `users` table.

Authentication flow:

```text
User
 |
 v
Supabase Auth
 |
 v
JWT Access Token
 |
 v
Spring Boot API
 |
 v
JWT Validation
 |
 v
Authenticated User
```

Every protected API must require a valid authentication token.

Expected header:

```http
Authorization: Bearer <access-token>
```

---

# 10. JWT Security

Spring Security must validate the Supabase JWT.

The backend must:

1. Read the Bearer token.
2. Validate the token.
3. Extract the authenticated Supabase user ID.
4. Load the corresponding application user.
5. Determine the user's application role.
6. Create an authenticated Spring Security principal.
7. Continue to the requested controller.

Invalid or missing token:

```text
HTTP 401 Unauthorized
```

Authenticated user without sufficient permission:

```text
HTTP 403 Forbidden
```

Never trust a role sent by the frontend request body.

---

# 11. Application Roles

Support:

```text
EMPLOYEE
HR
ADMIN
```

## EMPLOYEE

Can access only permitted employee functionality.

Can:

* View own profile
* Update permitted profile fields
* Check in
* Check out
* View own attendance
* Apply for leave
* View own leave requests
* View own payroll
* View own documents
* View own notifications
* View own dashboard

Cannot:

* Manage employees
* Approve leave
* Reject leave
* Modify payroll
* Access another employee's private information

---

## HR

Can:

* View employees
* Manage employees
* View attendance
* Manage leave requests
* Approve leave
* Reject leave
* Add leave comments
* View payroll
* Update salary information
* View reports

---

## ADMIN

Has full application access.

Can:

* Manage employees
* Manage HR users
* Manage roles
* Manage attendance
* Manage leave
* Manage payroll
* Manage documents
* View reports
* Manage system-level data

---

# 12. Database Entities

Create these main entities:

```text
User
Employee
Role
Attendance
LeaveType
LeaveRequest
Payroll
Document
Notification
AuditLog
```

---

# 13. User Entity

Application-level user information:

```text
User
----
id
supabaseUserId
email
role
isActive
createdAt
updatedAt
```

Important:

```text
supabaseUserId
```

must identify the corresponding Supabase Auth user.

Use UUID where appropriate.

---

# 14. Employee Entity

Fields:

```text
Employee
--------
id
userId
employeeCode
firstName
lastName
phone
address
dateOfBirth
gender
profilePictureUrl
department
designation
joiningDate
employmentStatus
createdAt
updatedAt
```

Employee code must be unique.

Example:

```text
EMP001
EMP002
EMP003
```

---

# 15. Role Entity

If roles are stored as a separate database table:

```text
Role
----
id
name
description
```

Allowed roles:

```text
EMPLOYEE
HR
ADMIN
```

If using a Java enum for roles, ensure database persistence is clean and consistent.

Do not allow arbitrary role strings from client requests.

---

# 16. Attendance Entity

Fields:

```text
Attendance
----------
id
employeeId
attendanceDate
checkIn
checkOut
status
workingHours
remarks
createdAt
updatedAt
```

Statuses:

```text
PRESENT
ABSENT
HALF_DAY
LEAVE
```

Rules:

* One attendance record per employee per date.
* Employee cannot check in twice.
* Employee cannot check out without checking in.
* Employee cannot check out twice.
* Check-out must be after check-in.
* Working hours are calculated by backend.
* Employee cannot directly modify attendance history.

---

# 17. Leave Type Entity

Supported leave types:

```text
PAID
SICK
UNPAID
```

Entity:

```text
LeaveType
---------
id
name
description
createdAt
updatedAt
```

---

# 18. Leave Request Entity

Fields:

```text
LeaveRequest
------------
id
employeeId
leaveTypeId
startDate
endDate
reason
status
adminComment
approvedBy
appliedAt
reviewedAt
createdAt
updatedAt
```

Statuses:

```text
PENDING
APPROVED
REJECTED
```

---

# 19. Leave Workflow

```text
Employee
   |
   | Submit Leave
   v
PENDING
   |
   +----------------+
   |                |
   v                v
APPROVED          REJECTED
```

Only HR/Admin can approve or reject.

After a leave has already been approved or rejected, it must not be processed again.

When rejecting a request, store the HR/Admin comment.

---

# 20. Payroll Entity

Fields:

```text
Payroll
-------
id
employeeId
basicSalary
hra
allowances
deductions
grossSalary
netSalary
effectiveFrom
createdAt
updatedAt
```

Calculation:

```text
grossSalary =
basicSalary + hra + allowances

netSalary =
grossSalary - deductions
```

The calculation must be performed on the backend.

Employee access:

```text
READ ONLY
```

HR/Admin:

```text
READ + UPDATE
```

---

# 21. Document Entity

Do not store uploaded files directly in PostgreSQL.

Use Supabase Storage.

Database stores metadata:

```text
Document
--------
id
employeeId
documentName
documentType
storagePath
fileUrl
fileSize
mimeType
uploadedBy
createdAt
updatedAt
```

Document types can include:

```text
ID_PROOF
OFFER_LETTER
CONTRACT
SALARY_SLIP
CERTIFICATE
OTHER
```

---

# 22. Notification Entity

Fields:

```text
Notification
------------
id
userId
title
message
type
isRead
createdAt
readAt
```

Types:

```text
LEAVE_SUBMITTED
LEAVE_APPROVED
LEAVE_REJECTED
ATTENDANCE
PAYROLL
SYSTEM
```

---

# 23. Audit Log Entity

Fields:

```text
AuditLog
--------
id
userId
action
entityType
entityId
description
ipAddress
createdAt
```

Log important administrative operations.

Examples:

```text
EMPLOYEE_CREATED
EMPLOYEE_UPDATED
EMPLOYEE_DEACTIVATED
LEAVE_APPROVED
LEAVE_REJECTED
PAYROLL_UPDATED
ATTENDANCE_UPDATED
DOCUMENT_UPLOADED
```

Never log:

```text
passwords
JWT tokens
database passwords
Supabase service keys
```

---

# 24. REST API Base Path

All APIs must use:

```text
/api/v1
```

Example:

```text
/api/v1/profile
/api/v1/employees
/api/v1/attendance
/api/v1/leaves
```

---

# 25. Profile APIs

## Get Current Profile

```http
GET /api/v1/profile
```

Authentication:

```text
EMPLOYEE
HR
ADMIN
```

The backend must identify the current user from the JWT.

Do not accept employee ID from the client for this endpoint.

---

## Update Current Profile

```http
PUT /api/v1/profile
```

Employee may update permitted fields:

```text
phone
address
profile picture
```

Employee must NOT modify:

```text
role
salary
department
designation
employeeCode
joiningDate
employmentStatus
```

HR/Admin may have broader modification privileges.

---

# 26. Employee APIs

## Get Employees

```http
GET /api/v1/employees
```

Roles:

```text
HR
ADMIN
```

Support pagination:

```text
page
size
sort
```

Support filtering:

```text
search
department
employmentStatus
```

---

## Get Employee

```http
GET /api/v1/employees/{employeeId}
```

---

## Create Employee

```http
POST /api/v1/employees
```

Roles:

```text
HR
ADMIN
```

---

## Update Employee

```http
PUT /api/v1/employees/{employeeId}
```

Roles:

```text
HR
ADMIN
```

---

## Change Employee Status

```http
PATCH /api/v1/employees/{employeeId}/status
```

Prefer deactivation over physical deletion.

---

# 27. Attendance APIs

## Check In

```http
POST /api/v1/attendance/check-in
```

The backend determines:

```text
authenticated employee
current date
current timestamp
```

Do not trust these values from the frontend.

---

## Check Out

```http
POST /api/v1/attendance/check-out
```

Backend determines checkout time.

Calculate working hours on the backend.

---

## Today's Attendance

```http
GET /api/v1/attendance/today
```

---

## My Attendance

```http
GET /api/v1/attendance/me
```

Optional filters:

```text
startDate
endDate
status
```

---

## Weekly Attendance

```http
GET /api/v1/attendance/me/weekly
```

---

## All Attendance

```http
GET /api/v1/attendance
```

Roles:

```text
HR
ADMIN
```

Filters:

```text
employeeId
department
startDate
endDate
status
```

---

# 28. Leave APIs

## Apply Leave

```http
POST /api/v1/leaves
```

Request:

```json
{
  "leaveTypeId": 1,
  "startDate": "2026-08-25",
  "endDate": "2026-08-27",
  "reason": "Medical leave"
}
```

Backend automatically determines:

```text
employee
status = PENDING
appliedAt
```

---

## My Leave Requests

```http
GET /api/v1/leaves/me
```

---

## Get Leave Request

```http
GET /api/v1/leaves/{leaveId}
```

Employees may only access their own requests.

HR/Admin may access permitted requests.

---

## Pending Leave Requests

```http
GET /api/v1/leaves/pending
```

Roles:

```text
HR
ADMIN
```

---

## Approve Leave

```http
PATCH /api/v1/leaves/{leaveId}/approve
```

Roles:

```text
HR
ADMIN
```

---

## Reject Leave

```http
PATCH /api/v1/leaves/{leaveId}/reject
```

Request:

```json
{
  "comment": "Leave cannot be approved for this period."
}
```

Roles:

```text
HR
ADMIN
```

---

# 29. Payroll APIs

## My Payroll

```http
GET /api/v1/payroll/me
```

Employee can only read their own payroll.

---

## Get Employee Payroll

```http
GET /api/v1/payroll/employee/{employeeId}
```

Roles:

```text
HR
ADMIN
```

---

## Update Employee Payroll

```http
PUT /api/v1/payroll/employee/{employeeId}
```

Roles:

```text
HR
ADMIN
```

Employee must never be allowed to modify salary.

---

# 30. Document APIs

## Upload Document

```http
POST /api/v1/documents
```

Use:

```text
multipart/form-data
```

Flow:

```text
Request
   |
   v
Validate file
   |
   v
Upload to Supabase Storage
   |
   v
Save metadata in PostgreSQL
   |
   v
Return document response
```

---

## Get My Documents

```http
GET /api/v1/documents/me
```

---

## Get Employee Documents

```http
GET /api/v1/documents/employee/{employeeId}
```

Roles:

```text
HR
ADMIN
```

---

## Delete Document

```http
DELETE /api/v1/documents/{documentId}
```

Verify ownership/authorization before deletion.

---

# 31. Notification APIs

## Get Notifications

```http
GET /api/v1/notifications
```

---

## Mark Notification Read

```http
PATCH /api/v1/notifications/{notificationId}/read
```

The user may only modify their own notifications.

---

## Mark All Notifications Read

```http
PATCH /api/v1/notifications/read-all
```

---

# 32. Dashboard APIs

## Employee Dashboard

```http
GET /api/v1/dashboard/employee
```

Return backend data such as:

```text
employee information
today's attendance
check-in status
check-out status
leave summary
pending leaves
approved leaves
recent notifications
payroll summary
```

---

## Admin Dashboard

```http
GET /api/v1/dashboard/admin
```

Return:

```text
total employees
active employees
present today
absent today
employees on leave
pending leave requests
department statistics
recent activities
payroll summary
```

---

# 33. Report APIs

## Attendance Report

```http
GET /api/v1/reports/attendance
```

Filters:

```text
startDate
endDate
employeeId
department
status
```

---

## Leave Report

```http
GET /api/v1/reports/leaves
```

---

## Payroll Report

```http
GET /api/v1/reports/payroll
```

Roles:

```text
HR
ADMIN
```

---

# 34. DTO Design

Never return entities directly.

Example:

```text
CreateEmployeeRequest
UpdateEmployeeRequest
EmployeeResponse
EmployeeSummaryResponse
```

Attendance:

```text
CheckInResponse
CheckOutResponse
AttendanceResponse
AttendanceSummaryResponse
```

Leave:

```text
CreateLeaveRequest
RejectLeaveRequest
LeaveResponse
```

Payroll:

```text
PayrollResponse
UpdatePayrollRequest
```

Document:

```text
DocumentResponse
```

Notification:

```text
NotificationResponse
```

---

# 35. Validation

Use Jakarta Validation.

Examples:

```java
@NotBlank
@NotNull
@NotEmpty
@Email
@Size
@Past
@Positive
```

Validate:

```text
employee information
phone numbers
email
dates
leave dates
salary values
document metadata
```

Frontend validation is not sufficient.

The backend must always validate incoming data.

---

# 36. Leave Validation

Before creating a leave request, verify:

```text
leave type exists
start date exists
end date exists
start date <= end date
reason is valid
employee is active
```

Prevent invalid overlapping leave requests according to the application's leave policy.

Do not approve a request that is already:

```text
APPROVED
REJECTED
```

---

# 37. Attendance Validation

Check-in:

```text
authenticated employee exists
employee is active
employee has not already checked in today
```

Check-out:

```text
attendance exists
check-in exists
check-out does not already exist
checkout > checkin
```

---

# 38. Authorization and Ownership

This is mandatory.

Never rely only on frontend restrictions.

For example, if an employee requests:

```http
GET /api/v1/payroll/employee/25
```

the backend must verify whether employee `25` belongs to the authenticated user.

An employee must not be able to access another employee's:

```text
profile
attendance
leave
payroll
documents
notifications
```

Use service-layer ownership checks where necessary.

---

# 39. Global Exception Handling

Create:

```text
GlobalExceptionHandler
```

Return consistent JSON responses.

Example:

```json
{
  "success": false,
  "message": "Employee not found",
  "errorCode": "EMPLOYEE_NOT_FOUND",
  "timestamp": "2026-08-22T10:30:00Z"
}
```

Handle:

```text
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
409 Conflict
422 Unprocessable Entity
500 Internal Server Error
```

Never expose Java stack traces to API clients.

---

# 40. Transactions

Use `@Transactional` for operations involving multiple database changes.

Examples:

```text
Employee creation
Leave approval
Leave rejection
Payroll update
Employee status change
Document metadata creation
Notification creation
```

A business operation must not leave the database in a partially updated state.

---

# 41. Pagination

Use Spring Data:

```text
Pageable
Page<T>
```

Pagination is required for large collections.

Use it for:

```text
employees
attendance
leave requests
payroll
notifications
audit logs
```

---

# 42. Supabase Storage

Use Supabase Storage for:

```text
profile pictures
employee documents
salary slips
```

Store only metadata/path information in PostgreSQL.

Sensitive buckets should not be publicly accessible.

Never expose the Supabase service-role key to the frontend.

The service-role key must exist only on the backend when required.

---

# 43. CORS

Configure CORS using an environment variable.

Development frontend:

```text
http://localhost:5173
```

Do not use unrestricted CORS in production.

Do not use:

```text
Access-Control-Allow-Origin: *
```

for authenticated production APIs.

---

# 44. Database Migration

Use Flyway.

Example migrations:

```text
V1__create_users.sql
V2__create_roles.sql
V3__create_employees.sql
V4__create_attendance.sql
V5__create_leave_types.sql
V6__create_leave_requests.sql
V7__create_payroll.sql
V8__create_documents.sql
V9__create_notifications.sql
V10__create_audit_logs.sql
```

Use:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
```

Do not use:

```text
create
create-drop
```

for production.

---

# 45. Seed Data

Seed the following roles:

```text
EMPLOYEE
HR
ADMIN
```

Seed leave types:

```text
PAID
SICK
UNPAID
```

Do not seed real passwords.

---

# 46. API Documentation

Add Swagger/OpenAPI.

Document:

```text
Profiles
Employees
Attendance
Leaves
Payroll
Documents
Notifications
Dashboard
Reports
```

For each endpoint document:

```text
HTTP method
URL
authentication requirement
required role
request body
response
possible errors
```

---

# 47. Testing

Create unit tests for:

```text
EmployeeService
ProfileService
AttendanceService
LeaveService
PayrollService
DocumentService
NotificationService
DashboardService
ReportService
```

Test important business rules.

Examples:

```text
employee cannot check in twice
employee cannot check out without check-in
employee cannot access another employee's payroll
employee cannot approve leave
HR can approve leave
admin can approve leave
approved leave cannot be approved again
rejected leave cannot be approved
employee cannot update salary
```

---

# 48. Integration Tests

Test:

```text
database connectivity
repositories
controllers
security
authorization
validation
transactions
```

Use:

```text
JUnit 5
Spring Boot Test
MockMvc
Mockito
```

---

# 49. Logging

Use SLF4J.

Log:

```text
application startup
important business operations
errors
security failures
database errors
external service errors
```

Never log:

```text
passwords
JWT tokens
database passwords
Supabase service-role keys
```

---

# 50. Implementation Order

Antigravity must implement the backend in this exact sequence.

## Step 1

Create Spring Boot project.

Verify:

```text
mvn test
```

works.

---

## Step 2

Configure:

```text
PostgreSQL
Supabase
JPA
Flyway
environment variables
```

Verify database connectivity.

---

## Step 3

Create:

```text
User
Role
Employee
```

Implement repositories and migrations.

---

## Step 4

Implement Supabase JWT authentication.

Verify:

```text
valid token -> authenticated
invalid token -> 401
missing token -> 401
```

---

## Step 5

Implement roles:

```text
EMPLOYEE
HR
ADMIN
```

Verify authorization.

---

## Step 6

Implement Employee and Profile modules.

---

## Step 7

Implement Attendance.

---

## Step 8

Implement Leave Management.

---

## Step 9

Implement Payroll.

---

## Step 10

Implement Supabase Storage and Documents.

---

## Step 11

Implement Notifications.

---

## Step 12

Implement Dashboard APIs.

---

## Step 13

Implement Reports.

---

## Step 14

Implement Audit Logs.

---

## Step 15

Add comprehensive validation and exception handling.

---

## Step 16

Add unit and integration tests.

---

## Step 17

Add Swagger/OpenAPI documentation.

---

## Step 18

Perform final security testing.

---

# 51. Antigravity Rules

When using Antigravity to implement this backend, follow these rules.

### Rule 1

Build incrementally.

Do not generate the entire application in one operation.

### Rule 2

Before implementing a module:

```text
Requirement
    ↓
Entity
    ↓
Repository
    ↓
DTO
    ↓
Service
    ↓
Controller
    ↓
Validation
    ↓
Security
    ↓
Tests
```

### Rule 3

Do not implement frontend code.

The scope is:

```text
BACKEND ONLY
```

### Rule 4

Do not create:

```text
React
Angular
Vue
HTML
CSS
Node.js frontend
```

### Rule 5

Do not use MySQL.

Use:

```text
Supabase PostgreSQL
```

### Rule 6

Do not create a duplicate password authentication system.

Use:

```text
Supabase Auth
```

### Rule 7

Never hardcode secrets.

### Rule 8

Never expose JPA entities directly.

Use DTOs.

### Rule 9

Never trust frontend authorization.

Enforce authorization on the backend.

### Rule 10

Do not modify unrelated modules while implementing a feature.

### Rule 11

After every module:

```text
Compile
    ↓
Run tests
    ↓
Start application
    ↓
Test APIs
    ↓
Fix errors
    ↓
Commit
```

---

# 52. Backend Definition of Done

The backend is complete only when all of the following work:

```text
[ ] Spring Boot application starts
[ ] Maven build succeeds
[ ] Supabase PostgreSQL connection works
[ ] Flyway migrations work
[ ] Supabase authentication integration works
[ ] JWT validation works
[ ] EMPLOYEE role works
[ ] HR role works
[ ] ADMIN role works
[ ] Employee management works
[ ] Profile management works
[ ] Attendance works
[ ] Check-in works
[ ] Check-out works
[ ] Leave application works
[ ] Leave approval works
[ ] Leave rejection works
[ ] Payroll works
[ ] Employee payroll is read-only
[ ] Document upload works
[ ] Supabase Storage works
[ ] Notifications work
[ ] Dashboard APIs work
[ ] Reports work
[ ] Audit logs work
[ ] Validation works
[ ] Global exception handling works
[ ] Ownership checks work
[ ] Pagination works
[ ] Swagger works
[ ] Unit tests pass
[ ] Integration tests pass
[ ] No secrets are hardcoded
[ ] Production configuration is secure
```

---

# 53. Final Backend Architecture

```text
                         SUPABASE
                            |
              +-------------+-------------+
              |             |             |
              v             v             v
         PostgreSQL      Auth          Storage
              ^             |
              |             |
              |             v
              |        JWT Access Token
              |             |
              +-------------+
                            |
                            v
                  SPRING BOOT BACKEND
                            |
                 +----------+----------+
                 |                     |
                 v                     v
          Spring Security        REST Controllers
                 |                     |
                 v                     v
          Authorization           Services
                                       |
                                       v
                                  Repositories
                                       |
                                       v
                                  PostgreSQL
```

The backend must remain independent from the frontend and expose clean, documented REST APIs.

**Primary implementation goal:**

```text
Java
  +
Spring Boot
  +
Spring Security
  +
Supabase Auth
  +
Supabase PostgreSQL
  +
Supabase Storage
  =
Dayflow Backend
```

Do not implement anything outside this backend scope.

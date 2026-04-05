# 🚀 Finance Data Processing and Access Control Backend

A secure, scalable backend system for managing financial records with 
**Role-Based Access Control (RBAC)** and **dashboard-level analytics**.

This project was built as part of the **Zorvyn Backend Developer Intern assessment**.

🔗 **GitHub Repository:**
https://github.com/vivek9589/finance-data-processing-Zoryvn.git

---

## 📌 Overview

This system simulates a **finance dashboard backend** where users interact with 
financial data based on their roles.

### ✨ Key Capabilities

* Clean backend architecture
* Secure authentication & authorization
* Financial data processing
* Aggregated dashboard insights

---

## 🛠️ Tech Stack

* **Java 17**
* **Spring Boot 3**
* Spring Web, JPA, Validation
* Spring Security + JWT
* MySQL + Flyway
* Swagger (SpringDoc OpenAPI)
* Docker + Docker Compose
* JUnit + Mockito
* Lombok

---

## 🏗️ Architecture

```
Controller → Service → Repository → Database
```

### 🔑 Highlights

* DTO-based request/response design
* Layered architecture (separation of concerns)
* Global exception handling
* Structured logging

---

## 🧩 Core Modules

### 👤 User Management

* Create users
* Assign roles
* Manage active/inactive status

### 💰 Financial Records

* Create, update, delete records
* Filtering (date, category, type)
* Pagination & sorting
* Soft delete support

### 📊 Dashboard

* Total income
* Total expenses
* Net balance
* Category-wise insights
* Recent activity

---

## 🔐 Authentication & RBAC

### 🔑 Authentication

* JWT-based authentication
* Stateless sessions
* Token expiry: 1 hour
* No refresh token (simplified for assessment)

### 🛡️ Role-Based Access Control

| Role    | Access                   |
| ------- | ------------------------ |
| Viewer  | Read-only dashboard      |
| Analyst | Read records + analytics |
| Admin   | Full system access       |

### 🔒 Enforcement

* Spring Security filters
* JWT validation
* Role-based endpoint protection

---

## 📂 Project Structure

```
finance-data-processing/
├── src/
│   ├── main/java/com/zorvyn/finance_data_processing/
│   │   ├── auth/                  # Authentication module
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   └── service/
│   │   │
│   │   ├── controller/            # REST Controllers
│   │   ├── dto/                   # Request/Response DTOs
│   │   ├── service/               # Business logic
│   │   ├── model/                 # Entities + enums
│   │   ├── repository/            # JPA Repositories
│   │   ├── util/                  # Utility classes
│   │   └── FinanceDataProcessingApplication.java
│   │
│   ├── resources/
│   │   ├── application.yml
│   │   └── db/migration/          # Flyway scripts
│
│   └── test/                      # Unit tests
│
├── pom.xml
└── README.md
```

---

## 🧠 Design Decisions

* Separation of **User** and **Credential** entities
* DTO pattern to avoid exposing internal models
* Flyway for schema versioning
* JWT for stateless authentication
* Layered architecture for maintainability and scalability 

---

## 🗄️ Database Design

### Entities

* User
* UserCredential
* FinancialRecord

### Features

* Normalized schema
* Version-controlled migrations using Flyway

---

## 🌐 Live API

* **Base URL:**
  http://161.118.173.77:8080

* **Swagger UI:**
  http://161.118.173.77:8080/swagger-ui/index.html

---

## 📡 API Endpoints

### 🔐 Auth APIs (`/api/auth`)

| Method | Endpoint  | Description       |
| ------ | --------- | ----------------- |
| POST   | /register | Register new user |
| POST   | /login    | Login user        |

---

### 📊 Dashboard APIs (`/api/dashboard`)

| Method | Endpoint          | Roles                  |
| ------ | ----------------- | ---------------------- |
| GET    | /{userId}/summary | ADMIN, ANALYST, VIEWER |

---

### 💰 Financial Records (`/api/records`)

| Method | Endpoint       | Roles                  |
| ------ | -------------- | ---------------------- |
| POST   | /              | ADMIN                  |
| GET    | /user/{userId} | ADMIN, ANALYST, VIEWER |
| PUT    | /{id}          | ADMIN                  |
| DELETE | /{id}          | ADMIN                  |
| GET    | /filter        | ADMIN, ANALYST, VIEWER |

---

### 👥 User APIs (`/api/users`)

| Method | Endpoint     | Roles          |
| ------ | ------------ | -------------- |
| POST   | /            | ADMIN          |
| GET    | /active      | ADMIN, ANALYST |
| PUT    | /{id}/role   | ADMIN          |
| PUT    | /{id}/status | ADMIN          |
| DELETE | /{id}        | ADMIN          |
| GET    | /            | ADMIN, ANALYST |

---

## ⚙️ Configuration

### Profiles

* `dev`
* `prod`

### Environment Variables

* Database credentials
* JWT secret

---

## 🐳 Deployment

* Dockerized application
* Multi-architecture image (ARM + AMD64)
* Docker Compose setup:

    * App container
    * MySQL container

**Hosted on:** Oracle VM (Ubuntu)

---

## 🧪 Testing

* JUnit-based unit testing
* Mockito for mocking

### Coverage Includes:

* Service layer logic
* Edge cases
* Validation scenarios

---

---

## ✨ Additional Enhancements

* Swagger API documentation
* Global exception handling
* Logging
* Soft delete implementation
* Pagination & filtering
* Reusable authentication module from previous project 

---

## 🚀 Run Locally

### Using Docker

```bash
docker-compose up --build
```

### Manual Setup

```bash
mvn spring-boot:run
```

---

## 📌 Final Note

This project demonstrates:

* Backend system design
* Clean architecture
* Secure access control
* Real-world engineering practices



 **About the Developer (Vivek Rathore)** 

In addition to this assignment,I have worked on backend systems in both monolithic
and microservice-based architectures, where I independently handled backend development
and collaborated  with frontend teams.

I am currently **building a SaaS-based gym management system**, 
which is actively being used by a local gym. 
In this project, I have independently designed and developed the complete backend, 
including features such as:

**Role-Based Access Control (RBAC) for gym owners and staff
Member management and attendance tracking
Notification system for member engagement**

I continuously improve this system by enhancing backend capabilities and
expanding into frontend development to better understand full-stack system design.

🔗 GitHub Profile:
https://github.com/vivek9589

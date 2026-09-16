
# 🟣 Schema Architecture

## 🟣 Section 1 - Architecture summary

This spring boot application uses both MVC and REST controllers.
Thymeleaf templates are used for the Admin and Doctor dashboards,
while REST APIs serve all other modules. The application interacts 
with two databases - MySql (for patient, doctor, appointment, 
and admin data) and MongoDB (for prescriptions). All controllers
rout requests through a common service layer, which in turn delegates
to the appropriated repositories. MySQL uses JPA (Java Persistence API)
entities while MongoDB uses document models.

## 🟣 Section 2 - Numbered flow of data and control

### 🟣 1. User Interface Layer

The system supports multiple user types and interaction patterns. Users
can interact with the application through:
- **Thymeleaf-based web dashboards** such as AdminDashboard or DoctorDashboard these are traditional HTML pages rendered on the server and delivered to the browser.
- **REST API clients** - like mobile apps or frontend modules (e.g., Appointments, PatientDashboards, Patient record) that interact with the backend via HTTP and receive JSON responses.

This separation allows the system to support both interactive browser views and scalable API-based integrations.

### 🟣 2. Controller Layer

When a user interacts with the application (e.g., clicking a button or submitting a form) the request is routed to the backend **controller** based on the URL path and the HTTP method.

- Request for server-rendered views are handled by Thymeleaf Controllers, which return .html templates that will be filled with dynamic data and rendered in the browser.
- Request from API consumers are handled by REST Controllers, which process the input, call backend logic, and return responses in JSON format.

The controllers serve as entry points into the backend application logic, enforcing request validation and coordinating the request response flow.

### 🟣 3. Service Layer

All controllers delegate business logic to the **Service Layer**, which act has the heart of the backend system. 
This layer:
- Applies business rules and validations
- Coordinates workflows across multiple entities (e.g., checking doctor availability before scheduling an appointment)
- Ensures clean separation between controller logic and data access
By isolating business logic here, the application becomes more maintainable, testable, and easier to scale.

### 🟣 4. Repository Layer

The service layer communicates with the **Repository Layer** to perform data access operations. This layer includes two types of repositories:

- **MySQL Repositories**, which use Spring Data JPA to manage structured relational data like patients, doctors, appointments, and admin records.
- **MongoDB Repository**, which uses Spring Data MongoDB to manage document-based records like prescriptions.

Repositories abstract the database access logic and expose simple declarative interface for fetching and persisting data.

### 🟣 5. Database Access

Each repository interfaces directly with the underlying database engine:

- **MySQL** stores all core entities that benefit from normalized relational schema and constraints - such as users, roles, and appointments.
- **MongoDB** stores flexible and nested data structures, such as prescriptions, which may vary in format and allow for rapid schema evolution.

This dual-database setup leverages the strengths of both structured and unstructured data storage.

### 🟣 6. Model binding

Once data is retrieved from the database, it is mapped into Java model classes that the application can work with. This process is known as **model biding**.

- In the case of MySQL, data is converted into **JPA Entities**, which represent rows in relational tables and annotated with @Entity.
- From MongoDB, data is loaded into **document objects**, typically annotated with @Document, which map to BSON/JSON structures in collections.

These model classes provide a consistent, object-oriented representation of the data across the application layers.

### 🟣 7. Application models in use

Finally, the bound models are used in the response layer:

- In **MVC flows**, models are passed from the controller to Thymeleaf templates, where they are rendered as dynamic HTML for the browser.
- In **REST flows**, the same models (or transformed DTOs) are serialized into **JSON** and send back to the client as part of an HTTP response.

This mark the end of the request-response cycle, delivering either a full web page os a structure API data, depending on the consumer. 
 

# CivicDesk

A robust, enterprise-ready desktop application built with Java Swing and Oracle Database. This system empowers citizens to securely lodge and track civic complaints while providing municipal officials with a comprehensive suite of tools to manage, update, and analyze grievance resolutions.

## 🚀 Key Features

* **Secure Authentication:** Passwords are cryptographically secured using `PBKDF2WithHmacSHA256` hashing and per-user random salting.
* **Role-Based Architecture:** Distinct workflows and interfaces designed specifically for Citizens (lodging and tracking) and Officials (managing and analyzing).
* **Asynchronous Operations:** All database interactions are offloaded to background threads using `SwingWorker`, ensuring a perfectly fluid and responsive user interface even during network latency.
* **Resilient Error Handling:** Intercepts low-level `SQLException`s (like DB outages or duplicate emails) and translates them into friendly, non-crashing dialogs for the user.
* **Live Analytics & Visualization:** Features a dynamic dashboard with real-time metrics and a custom Java2D pie chart detailing complaint distribution.

## 🖥️ Screens & Workflows

1. **Login & Registration (`LoginFrame` / `CitizenFrame`)**
   - Entry point for all users. Allows secure role-based login or new citizen registration.
2. **Citizen Portal (`CitizenPortalFrame`)**
   - **Lodge Complaint Tab:** Submit new grievances with category tags and live character-counted descriptions.
   - **My Complaints Tab:** A historical ledger of the citizen's submitted complaints and current statuses.
3. **Official Workspace (`OfficialFrame`)**
   - A unified grid displaying all system complaints with instant text search and dropdown filters.
   - Includes a dynamic Detail Panel to claim complaints, update statuses (Pending, In Progress, Resolved), and append official remarks.
4. **Analytics Dashboard (`DashboardFrame`)**
   - A reporting overlay accessible from the Official Workspace. Displays aggregated metrics, dynamic database counts, and a purely native Java2D pie chart.

## 🗂️ Folder Structure

```
Complaint-Management-System/
├── bin/                       # Compiled .class files (generated automatically)
├── database/                  
│   ├── schema.sql             # Table creation and structural constraints
│   ├── sample_data.sql        # Seed data (hashed passwords included)
│   ├── database_queries.sql   # Core SQL queries used by the DAOs
│   └── normalization.md       # Detailed 3NF normalization analysis
├── lib/                       
│   └── ojdbc11.jar            # Oracle JDBC Driver dependency
├── src/                       
│   ├── dao/                   # Data Access Objects (DB transactions)
│   ├── db/                    # DB connection utilities
│   ├── model/                 # Data transfer objects (Citizen, Official, Complaint)
│   ├── ui/                    # Swing GUI frames and components
│   └── util/                  # Helpers (Password hashing, Session, Error handling)
├── build.bat / build.sh       # Compilation scripts
├── run.bat / run.sh           # Execution scripts
├── README.md                  # Project documentation
└── TESTING.md                 # Manual quality assurance testing checklist
```

## 🗄️ Database Normalization

This application is powered by a strictly normalized **Third Normal Form (3NF)** relational database schema. The design completely eradicates insertion, update, and deletion anomalies by cleanly separating entities (Citizens, Officials, Complaints) and resolving all transitive dependencies. 

For an in-depth breakdown of the schema evolution from Unnormalized Form to 3NF, including the formal ER Diagram, please read: **[Database Normalization Report](database/normalization.md)**.

## ⚙️ Setup & Installation

### 1. Prerequisites
- **Java Development Kit (JDK 17+)**
- **Oracle Database Free 26ai** (or any modern Oracle DB instance)
- Ensure the listener is running on port `1521` and the service is accessible.

### 2. Database Initialization
1. Connect to your Oracle database using SQL*Plus or Oracle SQL Developer.
2. Execute the schema generation script:
   ```sql
   @database/schema.sql
   ```
3. (Optional) Load the pre-hashed seed data to quickly test the application:
   ```sql
   @database/sample_data.sql
   ```

### 3. Environment Configuration
The application securely injects database credentials via environment variables rather than hardcoding them in source control. You must set the following variables before running:

**Windows (Command Prompt):**
```cmd
set DB_USER=your_username
set DB_PASSWORD=your_password
```

**Linux / macOS (Bash):**
```bash
export DB_USER=your_username
export DB_PASSWORD=your_password
```

### 4. Build and Run
We have provided automated scripts to handle classpath configurations and compilation.

**On Windows:**
```cmd
.\build.bat
.\run.bat
```

**On Linux / macOS:**
```bash
chmod +x build.sh run.sh
./build.sh
./run.sh
```

## 🧪 Testing

A rigorous manual testing checklist is provided in **[TESTING.md](TESTING.md)**. Follow the scenarios inside to verify security, database constraints, input validation, and asynchronous behavior.

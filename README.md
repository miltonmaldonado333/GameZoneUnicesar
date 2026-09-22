# GameZone Unicesar - Information System

GameZone Unicesar is a layered Java application designed for managing products, sales, customers, and staff for a video game store located in Valledupar, Colombia. The system provides persistent file-based data management, strict domain validation rules, and a interactive console interface[cite: 1, 2].

---

## Architecture & Design Principles

The application is built using a **4-Layer Architecture** to enforce separation of concerns, maintainability, and clean code principles[cite: 1, 2]:

1. **Model Layer (`com.gamezone.model`):** Core domain entities (`Person`, `Client`, `Seller`, `Product`, `VideoGame`, `Console`, `Accessory`, `Sale`)[cite: 1, 2].
2. **Persistence Layer (`com.gamezone.persistence`):** Handles file-based data reading and writing (`data/*.csv`)[cite: 1, 2].
3. **Service Layer (`com.gamezone.service`):** Contains business logic, inventory updates, and transaction rules[cite: 1, 2].
4. **UI Layer (`com.gamezone.ui`):** Console-based user interface (`GameZoneUI`)[cite: 2, 3].

---

## Main Features

### Core Store Management (Base System)

- **Product Management:** Register and list Video Games and Consoles[cite: 2].
- **People & Roles:** Register and list Customers and Salespeople with their respective roles[cite: 2].
- **Sales System:** Process sales involving multiple products, automatic stock reduction, total calculations, and sales history queries[cite: 2].

### New Feature: Accessory Module (Requirement 1)

The system has been extended to manage video game accessories directly integrated into the existing product hierarchy (`Product`):

- **Accessory Types:**
  - **Controllers (`Controller`):** Supports connection type (Wireless or Wired)[cite: 1].
  - **Cables (`Cable`):** Supports length (in meters) and connector type (HDMI, USB, Optical, etc.)[cite: 1].
  - **Memories (`Memory`):** Supports storage capacity (GB) and card type (SD, microSD, internal)[cite: 1].
- **Console Compatibility:** Accessories track compatible console IDs, allowing users to query which accessories work with specific consoles before completing a transaction[cite: 1].
- **Unified Sales:** Sales transactions seamlessly combine Video Games, Consoles, and Accessories within a single purchase order while ensuring unified stock updates[cite: 1].

---

## Technical Specifications & Stack

- **Language:** Java 17+[cite: 2]
- **Build Tool:** Apache Maven (`pom.xml`)[cite: 2]
- **Version Control:** Git Flow (`main`, `develop`, `feature/*`)[cite: 1, 2]
- **Commit Standard:** Conventional Commits[cite: 1, 2]
- **Data Storage:** Delimited text/CSV files in `src/main/data/` (`accessories.csv`, `products.txt`, `persons.txt`)[cite: 1, 3]

---

## Project Structure

```text
GameZoneUnicesar/
├── pom.xml
├── README.md
├── TEAM.md
├── docs/
│   ├── analysis.md
│   ├── hierarchy-diagram.md
│   ├── accessory-class-diagram.md
│   └── layers-diagram.md
└── src/
    └── main/
        ├── data/
        │   ├── accessories.csv
        │   ├── persons.txt
        │   └── products.txt
        └── java/
            └── com/
                └── gamezone/
                    ├── Main.java
                    ├── model/
                    ├── persistence/
                    ├── service/
                    └── ui/
```

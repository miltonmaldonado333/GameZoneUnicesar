# GameZone Unicesar - Information System

GameZone Unicesar is a layered Java application designed for managing products, sales, customers, staff, and commercial promotions for a video game store located in Valledupar, Colombia[cite: 1, 2, 4]. The system provides persistent file-based data management, strict domain validation rules, and an interactive console interface[cite: 1, 2, 4].

---

## Architecture & Design Principles

The application is built using a **4-Layer Architecture** to enforce separation of concerns, maintainability, and clean code principles[cite: 1, 2, 4]:

1. **Model Layer (`com.gamezone.model`):** Core domain entities (`Person`, `Client`, `Seller`, `Product`, `VideoGame`, `Console`, `Accessory`, `Sale`, `Promotion`, `PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount`)[cite: 1, 2, 4].
2. **Persistence Layer (`com.gamezone.persistence`):** Handles file-based data reading and writing (`data/*.csv`, `data/*.txt`)[cite: 1, 2, 4].
3. **Service Layer (`com.gamezone.service`):** Contains business logic, inventory updates, promotion/discount calculations, and transaction rules[cite: 1, 2, 4].
4. **UI Layer (`com.gamezone.ui`):** Console-based user interface (`GameZoneUI`)[cite: 2, 3, 4].

---

## Main Features

### Core Store Management (Base System)

- **Product Management:** Register and list Video Games and Consoles[cite: 2].
- **People & Roles:** Register and list Customers and Salespeople with their respective roles[cite: 2].
- **Sales System:** Process sales involving multiple products, automatic stock reduction, total calculations, and sales history queries[cite: 2].

### Feature 1: Accessory Module (Requirement 1)

The system manages video game accessories integrated into the existing product hierarchy (`Product`)[cite: 1]:

- **Accessory Types:**
  - **Controllers (`Controller`):** Supports connection type (Wireless or Wired)[cite: 1].
  - **Cables (`Cable`):** Supports length (in meters) and connector type (HDMI, USB, Optical, etc.)[cite: 1].
  - **Memories (`Memory`):** Supports storage capacity (GB) and card type (SD, microSD, internal)[cite: 1].
- **Console Compatibility:** Accessories track compatible console IDs, allowing users to query compatible accessories for specific consoles[cite: 1].
- **Unified Sales:** Sales transactions seamlessly combine Video Games, Consoles, and Accessories within a single purchase order with unified inventory deduction[cite: 1].

### Feature 2: Promotion & Discount Module (Requirement 2)

The system supports automated marketing campaigns and discount calculations applied during checkout[cite: 4]:

- **Promotion Types:**
  - **Percentage Discount (`PercentageDiscount`):** Applies a global percentage discount across the entire sale total[cite: 4].
  - **Category Discount (`CategoryDiscount`):** Applies a percentage discount exclusively to items belonging to a specific product category (e.g., `VIDEOGAME` or `CONSOLE`)[cite: 4].
  - **Bulk Purchase Discount (`BulkPurchaseDiscount`):** Applies a percentage discount when the total item count in a sale meets or exceeds a minimum threshold[cite: 4].
- **Validity Check:** Promotions feature start and end dates (`LocalDate`), ensuring discounts are applied only when active on the transaction date[cite: 4].
- **Automatic Best-Discount Engine:** When processing a sale, the system evaluates all active promotions and automatically applies the single campaign that yields the highest monetary savings for the customer (promotions are non-cumulative)[cite: 4].
- **Itemized Receipts:** Generated sale receipts display the subtotal, applied promotion name, discount amount saved, and final net total[cite: 4].

---

## Technical Specifications & Stack

- **Language:** Java 17+[cite: 2]
- **Build Tool:** Apache Maven (`pom.xml`)[cite: 2, 4]
- **Version Control:** Git Flow (`main`, `develop`, `feature/*`)[cite: 1, 2, 4]
- **Commit Standard:** Conventional Commits[cite: 1, 2, 4]
- **Data Storage:** Delimited CSV/text files in `data/` (`accessories.csv`, `promotions.csv`, `persons.txt`, `products.txt`)[cite: 1, 3, 4]

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
│   ├── promotion-analysis.md
│   ├── promotion-class-diagram.md
│   └── layers-diagram.md
└── src/
    └── main/
        ├── data/
        │   ├── accessories.csv
        │   ├── promotions.csv
        │   ├── persons.txt
        │   └── products.txt
        └── java/
            └── com/
                └── gamezone/
                    ├── Main.java
                    ├── model/
                    │   ├── Person.java
                    │   ├── Client.java
                    │   ├── Seller.java
                    │   ├── Product.java
                    │   ├── VideoGame.java
                    │   ├── Console.java
                    │   ├── Accessory.java
                    │   ├── Controller.java
                    │   ├── Cable.java
                    │   ├── Memory.java
                    │   ├── Sale.java
                    │   ├── Promotion.java
                    │   ├── PercentageDiscount.java
                    │   ├── CategoryDiscount.java
                    │   └── BulkPurchaseDiscount.java
                    ├── persistence/
                    │   ├── PersonRepository.java
                    │   ├── ProductRepository.java
                    │   ├── AccessoryRepository.java
                    │   ├── SaleRepository.java
                    │   └── PromotionRepository.java
                    ├── service/
                    │   ├── PersonService.java
                    │   ├── ProductService.java
                    │   ├── AccessoryService.java
                    │   ├── SaleService.java
                    │   └── PromotionService.java
                    └── ui/
                        └── GameZoneUI.java
```

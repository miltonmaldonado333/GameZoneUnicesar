# SOLID Refactor Log — Developer 1

## Non-Regression Strategy

To guarantee that the refactoring preserves external system behavior across all catalog, warranty, return, and promotion menu operations, a baseline check was performed on `develop` prior to applying code modifications.

### Verification Plan
1. Compile the project using `mvn clean compile` before and after each refactoring activity.
2. Execute verification workflows for product registration, category discount calculations, return processing, warranty issuance, and sale validation.
3. Ensure that identical inputs produce the exact same outputs, exception types, user messages, and persisted data structures as before the refactor.

---

## Refactor Change Log - Developer 1

| Class / Interface | Change Made | Violation ID | Applied Pattern / Principle | Commit / PR / Author |
| :--- | :--- | :---: | :--- | :--- |
| `IProductService`, `ProductService` | Extracted the `IProductService` interface (extending `IStockService`) to segregate product catalog operations and decouple caller components from the concrete service. | V-DEV1-01 | **ISP** (Interface Segregation Principle) | `refactor: extract IProductService interface to apply ISP (V-DEV1-01)` / Jesús Martínez |
| `CategoryDiscount` | Removed console debug statements (`System.out.println`) from `calculateDiscount()` to prevent side-effect UI operations within domain entities. | V-DEV1-02 | **SRP** (Single Responsibility Principle) | `refactor: remove console print debug logic from CategoryDiscount to preserve SRP (V-DEV1-02)` / Jesús Martínez |
| `SaleValidator` | Evaluated parameter types (`Client`, `Seller`). Preserved concrete sub-roles to avoid **LSP** violations and maintain access to role-specific contracts (`getEmployeeCode()`). | V-DEV1-03 | **DIP** (Justified Domain Coupling) | *Dismissed/Justified in Audit Report* / Jesús Martínez |
| `ReturnReceiptPrinter`, `Return`, Menu Handlers | Extracted `generateReturnReceipt()` out of the `Return` domain entity into `ReturnReceiptPrinter` (in presentation layer) and updated menu invocation points. | V-DEV1-04 | **SRP** (Single Responsibility Principle) | `refactor: extract ReturnReceiptPrinter to decouple UI formatting from Return entity (V-DEV1-04)` / Jesús Martínez |
| `WarrantyCertificatePrinter`, `Warranty`, Menu Handlers | Extracted `generateWarrantyCertificate()` out of the abstract `Warranty` domain entity into `WarrantyCertificatePrinter` (in presentation layer) and updated menu invocation points. | V-DEV1-05 | **SRP** (Single Responsibility Principle) | `refactor


 SOLID REFACTORING IN GAMEZONE UNICESAR

**Project:** GameZone Unicesar  
**Git Branch:** `feature/solid-refactor`  
**Author:** Milton Andrés Maldonado Salcedo [Technical leader] 
**Consolidation Date:** October 22, 2026  
**Main Objective:** Strict application of SOLID principles, GRASP patterns, and decoupling of the presentation, service, and persistence layers.

---

## 1. EXECUTIVE SUMMARY OF CHANGES

During this work and refactoring session, we identified and resolved a series of architectural inconsistencies, compilation errors, and domain logic flaws. The three transformed pillars are detailed below:

1. **UI Layer Specialization (SRP / GRASP):** The logic for formatting and printing documents (ReturnReceiptPrinter and WarrantyCertificatePrinter) was extracted out of the main command console (GameZoneUI).
2. **Harmonization of Service Contracts and Interfaces (DIP / OCP):** The registerConsole method was standardized in IProductService and ProductService to resolve parameter signature issues and type imprecision (@Override).
3. **Polymorphic Management of Extended Warranties and Categories:** The domain models (Console, VideoGame) and the category promotion evaluation (CategoryDiscount) were adjusted, fixing the erroneous application of video game promotions to consoles.

---

## 2. CHANGES IN THE PRESENTATION LAYER (com.gamezone.ui)

### 2.1. Creation of Specialized Printing Components

To comply with the **Single Responsibility Principle (SRP)**, the textual generation of return receipts and warranty certificates was extracted to independent classes:

#### A. ReturnReceiptPrinter.java

    package com.gamezone.ui;

    import com.gamezone.model.Return;

    /**
     * Single Responsibility: Generate formatted presentation string for return receipts.
     */
    public class ReturnReceiptPrinter {

        public static String generateReturnReceipt(Return returnModel) {
            if (returnModel == null) {
                return "--- RETURN NOT FOUND ---";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("\n=========================================\n");
            sb.append("         GAMEZONE UNICESAR               \n");
            sb.append("            RETURN RECEIPT               \n");
            sb.append("=========================================\n");
            sb.append(String.format("Return ID     : %s%n", returnModel.getId()));
            sb.append(String.format("Sale ID       : %s%n", returnModel.getSaleId()));
            sb.append(String.format("Date          : %s%n", returnModel.getReturnDate()));
            sb.append(String.format("Reason        : %s%n", returnModel.getReason()));
            sb.append("-----------------------------------------\n");
            sb.append(String.format("Refund Amount : $%.2f%n", returnModel.getRefundAmount()));
            sb.append("=========================================\n");
            return sb.toString();
        }
    }

#### B. WarrantyCertificatePrinter.java

    package com.gamezone.ui;

    import com.gamezone.model.Warranty;

    /**
     * Single Responsibility: Format and present warranty certificates.
     */
    public class WarrantyCertificatePrinter {

        public static String generateWarrantyCertificate(Warranty warranty) {
            if (warranty == null) {
                return "--- WARRANTY CERTIFICATE NOT FOUND ---";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("==================================================================\n");
            sb.append("                 GAMEZONE WARRANTY CERTIFICATE                   \n");
            sb.append("==================================================================\n");
            sb.append(String.format("Warranty ID     : %s%n", warranty.getId()));
            sb.append(String.format("Sale ID         : %s%n", warranty.getSaleId()));
            sb.append(String.format("Product ID      : %s%n", warranty.getProductId()));
            sb.append(String.format("Type            : %s%n", warranty.getWarrantyType()));
            sb.append(String.format("Coverage Date   : %s to %s%n", 
                    warranty.getStartDate(), warranty.getEndDate()));
            sb.append(String.format("Status          : %s%n", 
                    warranty.isActive() ? "ACTIVE" : "EXPIRED/INACTIVE"));
            sb.append("==================================================================\n");
            return sb.toString();
        }
    }

---

### 2.2. Refactoring of GameZoneUI.java

The user interface methods were updated to directly use the formatters and ensure the polymorphic evaluation of the extended warranty via item.hasWarrantyCoverage():

    // Key fragment within the sales registration flow in GameZoneUI
    if (item.hasWarrantyCoverage()) {
        String extendedChoice = promptString("Do you want to add an extended warranty (+10% of the cost) to the product '"
                + item.getTitle() + "'? (y/n): ");
        if (extendedChoice.equalsIgnoreCase("y")) {
            extendedWarrantyProductIds.add(String.valueOf(item.getId()));
        }
    }

    // Clean delegation to specialized formatter classes
    System.out.println(ReturnReceiptPrinter.generateReturnReceipt(processedReturn));
    System.out.println(WarrantyCertificatePrinter.generateWarrantyCertificate(warranty));

---

## 3. CHANGES IN THE SERVICE LAYER (com.gamezone.service)

### 3.1. Correction of the IProductService.java Interface

The console registration method was added to the contract to allow dependency inversion and prevent @Override errors:

    package com.gamezone.service;

    import com.gamezone.model.Product;
    import java.util.List;

    public interface IProductService {

        boolean registerVideoGame(String platform, String genre, String ageRating,
                                  String id, String title, double price, int stock);

        // Integrated standardized signature
        boolean registerConsole(String brand, String model, String generation,
                                String id, String title, double price, int stock);

        List<Product> getAllProducts();

        Product findProductById(String id);

        List<String> getAvailableCategories();
    }

### 3.2. Implementation in ProductService.java

The method was implemented ensuring proper persistence through ProductRepository:

    @Override
    public boolean registerConsole(String brand, String model, String generation,
                                   String id, String title, double price, int stock) {
        List<Product> products = productRepository.loadProducts();
        if (products == null) {
            products = new ArrayList<>();
        }

        // Duplicate ID validation
        for (Product p : products) {
            if (p.getId().equalsIgnoreCase(id)) {
                return false;
            }
        }

        Console console = new Console(brand, model, generation, id, title, price, stock);
        products.add(console);

        productRepository.saveProducts(products);
        return true;
    }

---

## 4. CORRECTION IN THE DOMAIN MODEL AND CATEGORY EVALUATION

### 4.1. Adjustment in Console.java

To correct the mistaken application of video game promotions to consoles (such as PROMO002), the value returned by getCategory() was normalized to uppercase ("CONSOLE"):

    package com.gamezone.model;

    public class Console extends Product {

        private String brand;
        private String model;
        private String generation;

        public Console(String brand, String model, String generation, String id, String title, double price, int stock) {
            super(id, title, price, stock);
            this.brand = brand;
            this.model = model;
            this.generation = generation;
        }

        @Override
        public String getCategory() {
            return "CONSOLE"; // Normalized to avoid false positives in CategoryDiscount
        }

        @Override
        public boolean hasWarrantyCoverage() {
            return true; // Polymorphism that activates the warranty offer in the UI
        }
    }

---

## 5. PERSISTENCE DIAGNOSIS AND FILE LOADING (products.txt)

### 5.1. Root Cause of the Reading Inconsistency

The error where the PS5 (110011) received the video game discount and skipped the warranty was due to a mapping error in the flat file. The record was stored with the VideoGame tag:

    // ERRONEOUS RECORD IN THE FLAT FILE:
    VideoGame;SONY;5;5;110011;PS5;2009.00;8

    // CORRECTED RECORD FOR CONSOLES:
    Console;SONY;PS5 Standard;9th;110011;PS5;2009.00;8

### 5.2. Correct Mapping in Concrete Persistence (FileProductRepository.java)

    package com.gamezone.persistence;

    import com.gamezone.model.Console;
    import com.gamezone.model.Product;
    import com.gamezone.model.VideoGame;
    import java.io.*;
    import java.util.ArrayList;
    import java.util.List;

    public class FileProductRepository implements ProductRepository {

        private final String filePath = "products.txt";

        @Override
        public List<Product> loadProducts() {
            List<Product> products = new ArrayList<>();
            File file = new File(filePath);

            if (!file.exists()) return products;

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;

                    String[] parts = line.split(";");
                    if (parts.length < 8) continue;

                    String type = parts[0].trim();

                    if (type.equalsIgnoreCase("Console")) {
                        // Explicit Console mapping: brand, model, generation, id, title, price, stock
                        products.add(new Console(parts[1].trim(), parts[2].trim(), parts[3].trim(),
                                parts[4].trim(), parts[5].trim(),
                                Double.parseDouble(parts[6].trim()),
                                Integer.parseInt(parts[7].trim())));
                    } else if (type.equalsIgnoreCase("VideoGame")) {
                        // Explicit VideoGame mapping: platform, genre, ageRating, id, title, price, stock
                        products.add(new VideoGame(parts[1].trim(), parts[2].trim(), parts[3].trim(),
                                parts[4].trim(), parts[5].trim(),
                                Double.parseDouble(parts[6].trim()),
                                Integer.parseInt(parts[7].trim())));
                    }
                }
            } catch (IOException | NumberFormatException e) {
                System.err.println("Error processing products: " + e.getMessage());
            }
            return products;
        }

        @Override
        public void saveProducts(List<Product> products) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
                for (Product p : products) {
                    if (p instanceof Console c) {
                        pw.printf("Console;%s;%s;%s;%s;%s;%.2f;%d%n",
                                c.getBrand(), c.getModel(), c.getGeneration(),
                                c.getId(), c.getTitle(), c.getPrice(), c.getStock());
                    } else if (p instanceof VideoGame g) {
                        pw.printf("VideoGame;%s;%s;%s;%s;%s;%.2f;%d%n",
                                g.getPlatform(), g.getGenre(), g.getAgeRating(),
                                g.getId(), g.getTitle(), g.getPrice(), g.getStock());
                    }
                }
            } catch (IOException e) {
                System.err.println("Error saving products: " + e.getMessage());
            }
        }
    }

---

## 6. GUIDE FOR INDIVIDUAL STUDY OF PRINCIPLES

1. **SRP (Single Responsibility Principle):**
   - *Before:* GameZoneUI mixed menu capture logic with complex string formatting for receipts.
   - *Now:* ReturnReceiptPrinter and WarrantyCertificatePrinter assume the single responsibility of presentation.

2. **OCP (Open/Closed Principle):**
   - *Demonstration:* If a new product type is added (e.g., BoardGame), Product is simply extended by overriding getCategory() and hasWarrantyCoverage() without modifying the main billing or sales logic in GameZoneUI.

3. **LSP (Liskov Substitution Principle):**
   - *Demonstration:* Console and VideoGame can be used interchangeably wherever a Product reference is expected. The item.hasWarrantyCoverage() call behaves correctly according to the actual instantiated subclass.

4. **ISP (Interface Segregation Principle):**
   - *Demonstration:* IProductService contains only the operations required for product catalog management, avoiding forcing unused dependencies.

5. **DIP (Dependency Inversion Principle):**
   - *Demonstration:* GameZoneUI depends on the IProductService abstraction (or injected services), allowing internal logic changes without breaking the user interface.

---

## 📌 GIT COMMANDS FOR FINAL COMMIT CONSOLIDATION

    # 1. Verify clean full compilation with Maven
    mvn clean compile

    # 2. Add modified files to the staging area
    git add src/main/java/com/gamezone/ui/
    git add src/main/java/com/gamezone/service/
    git add src/main/java/com/gamezone/model/
    git add src/main/java/com/gamezone/persistence/

    # 3. Create commit in Git
    git commit -m "refactor(solid): finalize UI printers, fix console registration contract and persistence parser"

    # 4. Push changes to the remote branch
    git push origin feature/solid-refactor

---
*Document prepared for technical study and architectural review of the GameZone Unicesar system.*

## Refactor Change Log - Developer 2

| Class / Interface | Change Made | Violation ID | Applied Principle | Commit / PR / Author |
| :--- | :--- | :---: | :--- | :--- |
| `WarrantyService`, `Main` | Removed the unused sale repository field and constructor; the matching `new` call in `Main` was updated. | V-DEV2-05 | ISP | `d5b64d8` / Luis Corzo |
| `AccessoryService`, `Main` | Removed the unused product repository field and constructor; the matching `new` call in `Main` was updated. | V-DEV2-05 | ISP | `8c27dfa` / Luis Corzo |
| `StockHolder`, `AccessoryService` | `AccessoryService` fulfils the common stock contract through the new `StockHolder` interface. | V-DEV2-04 | OCP | `7d63944` / Luis Corzo |
| `ItemStockService`, `ReturnService`, `Main` | `ReturnService` restores stock through a resolver instead of testing `instanceof Accessory`. This first version routed by identifier. | V-DEV2-04 | OCP | `cef4f24`, `65595bc` / Luis Corzo |
| `ItemStock`, `ItemStockService`, `StockHolder`, `AccessoryService`, `ReturnService`, `Main` | Fix of the previous change: product and accessory identifiers collide in the data files (1, 11 and c1), so the owner of the stock is decided by asking the accessory service whether it holds the very item. | V-DEV2-04 | OCP | `8ae3123` / Luis Corzo |
| `SaleLookup`, `RepositorySaleLookup`, `WarrantyService`, `Main` | Added a narrow sale lookup over the repository abstraction and injected it into `WarrantyService` by constructor; the setter and the cycle with `SaleService` were removed. | V-DEV2-07 | DIP | `882427b`, `afa33cd` / Luis Corzo |
| `WarrantyCanceller`, `MonthlySales`, `AccessoryLookup`, `SaleService`, `WarrantyService`, `AccessoryService` | Added three narrow interfaces for what the return workflow uses and made the services implement them. `SaleService` (Technical Leader's class) only received the `implements` clause and `@Override` annotations, with no change of logic. | V-DEV2-06 | DIP | `400a432` / Luis Corzo |
| `ReturnRecordMapper`, `MonthlyBalanceCalculator` | Extracted the rebuilding of the stored returns and the monthly balance out of `ReturnService`. | V-DEV2-01 | SRP | `3ce1de6` / Luis Corzo |
| `ReturnService`, `Main` | `ReturnService` depends only on abstractions and delegates to the two helpers; its public methods are unchanged. | V-DEV2-06, V-DEV2-01 | DIP, SRP | `32265d8` / Luis Corzo |
| `WarrantyRecordMapper`, `WarrantyBackfill` | Extracted the rebuilding of the stored warranties and the historical backfill out of `WarrantyService`. | V-DEV2-02 | SRP | `5da1235` / Luis Corzo |
| `WarrantyService` | `WarrantyService` delegates to the two helpers and keeps its constructor and public methods. | V-DEV2-02 | SRP | `a6bad3c` / Luis Corzo |
| `SubtypeCodec`, `CodecRegistry` | Added the generic codec and registry that let a repository write and read each subtype without a chain of type tests. | V-DEV2-03 | OCP | `ceb3be8` / Luis Corzo |
| `ClientCodec`, `SellerCodec`, `FilePersonRepository` | One codec per person role. | V-DEV2-03 | OCP | `2ec405d` / Luis Corzo |
| `AccessoryCodec`, `ControllerCodec`, `CableCodec`, `MemoryCodec`, `FileAccessoryRepository` | One codec per accessory type. | V-DEV2-03 | OCP | `04e2f2f` / Luis Corzo |
| `PromotionCodec` and the three promotion codecs, `FilePromotionRepository` | One codec per promotion type; the dates are still checked before the type. | V-DEV2-03 | OCP | `9cd7368` / Luis Corzo |
| `WarrantyTypeRegistry`, `FileWarrantyRepository` | The stored type of a warranty is written through a registry. | V-DEV2-03 | OCP | `6dbab07` / Luis Corzo |
| `WarrantyCreator`, `WarrantyRecordMapper` | The warranty subtype is rebuilt through registered creators. | V-DEV2-03 | OCP | `12937b6` / Luis Corzo |

### Verification of Developer 2

`mvn clean compile` passed after every commit and the menu was not run, so the equivalence of behaviour was argued by reading the code. Known differences, which only appear with damaged data: a line of an unknown type in the person and accessory files is skipped instead of failing on a later field, and writing a warranty class that has no registered type raises an error instead of storing it as BASIC. The routing of the stock relies on the accessories of a sale being the same objects that `AccessoryService` holds.
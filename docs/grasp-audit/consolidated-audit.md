# Consolidated GRASP Audit Report — GameZone Unicesar

* **Audit Date:** 2026-10-17  
* **Technical Leader:** Milton Andrés Maldonado Salcedo  
* **Developer 1:** Jesús Manuel Martínez Baute  
* **Developer 2:** Luis Manuel Corzo Castro  
* **Audited Baseline:** `origin/develop` at commit `336790c`  
* **Scope:** Full system audit covering responsibility assignment, coupling, cohesion, polymorphism, persistence boundaries, and protected variations.

---

## 1. Purpose and Criteria

The purpose of this audit is to identify GRASP design violations across all modules of GameZone Unicesar and propose precise refactoring actions while strictly preserving the system's externally observable behavior, Spanish user messages, menu flows, and persisted file formats. 

A finding is recorded once even when it affects multiple GRASP patterns. The pattern most directly guiding the correction is identified, and related patterns are listed. Boundary findings involving two developers from opposite sides of an architectural barrier are unified into single rows.

---

## 2. Scope and Module Responsibility

* **Technical Leader:** `GameZoneUI`, `Sale`, `SaleRepository`, `SaleService`, `ProductService.restoreStock`, and `Main.main`.
* **Developer 1:** Product hierarchy (`Product`, `VideoGame`, `Console`, `ProductRepository`, and related `ProductService` methods), Accessory hierarchy, Promotion hierarchy, Warranty hierarchy, Returns (`Return`, `Sale.canBeReturned()`), Exception hierarchy, and Validators (`PersonValidator`, `ProductValidator`, `SaleValidator`).
* **Developer 2:** Persons module (`Person`, `Client`, `Seller`, `PersonRepository`, `FilePersonRepository`, `PersonService`), Accessories module (`AccessoryRepository`, `AccessoryService`), Promotions module, Returns module (`ReturnRepository`, `ReturnService`, monthly balances), Warranties module (`WarrantyRepository`, `WarrantyService`, `cancelWarranties`), and exception/validator usage within these specific services.

---

## 3. Consolidated GRASP Evaluation Matrix

| ID | Class / Method | Main GRASP Pattern | Status | Evidence and Issue | Proposed Correction | Responsible Owner |
|---|---|---|---|---|---|---|
| **V-01** | `GameZoneUI.registerCategoryDiscount()` | Controller / High Cohesion | Violation | The UI derives categories by iterating through products and inspecting `getClass().getSimpleName()`. Domain-query logic is embedded in the presentation layer. | Add `ProductService.getAvailableCategories()` and have the UI consume its result. | Technical Leader (UI); coordinate with Developer 1 if service API changes. |
| **V-02** | `GameZoneUI.registerSale()` | Indirection / Low Coupling | Violation | The UI performs sequential lookup attempts by catching `ResourceNotFoundException` from `ProductService` and `AccessoryService`. | Expose a unified service-layer `findSaleableItemById(String id)` lookup. | Technical Leader (UI); coordinate with service owners. |
| **V-03** | `GameZoneUI` | High Cohesion | Violation | Combines input parsing, business workflow orchestration, exception handling, and output formatting. | Delegate business workflows and validation to services or controllers; keep UI focused on console I/O. | Technical Leader |
| **V-04** | `SaleRepository.findAll()`, `ReturnRepository`, `WarrantyRepository` | Low Coupling / Indirection | Violation | Repositories depend on services (`PersonService`, `ProductService`, `SaleService`) to reconstruct relationships during reads. | Make repositories load/persist identifiers or raw records and rebuild objects in the service layer. | Technical Leader (`SaleRepository`, `Main`); Developer 2 (`ReturnRepository`, `WarrantyRepository`). |
| **V-05** | `SaleService`, `ReturnService`, `WarrantyService` | Polymorphism / Low Coupling | Violation | Explicit `instanceof Console` and `instanceof Accessory` checks select inventory and warranty behavior. | Replace type checks with a polymorphic query on `Product` for warranty coverage and a common stock-restoring contract. | Technical Leader (`SaleService`, `ProductService`); Developer 1 (`Product`, `Console`); Developer 2 (`ReturnService`, `WarrantyService`). |
| **V-06** | `ProductService.restoreStock()` | Information Expert | Violation | The service increments stock using `product.setStock(product.getStock() + quantity)`, manipulating entity state externally. | Add `Product.addStock(int quantity)` and delegate state change to the product. | Technical Leader |
| **V-07** | `Main.main()` | Low Coupling / Indirection | Violation | Manual construction of the full dependency graph creates fragile initialization ordering and documents circular dependencies. | Encapsulate object-graph construction in a composition root or factory. | Technical Leader |
| **V-08** | `CategoryDiscount.calculateDiscount()` | Polymorphism | Violation | Uses `instanceof` checks for product types instead of polymorphic category identification. | Use `product.getCategory()` or another stable polymorphic contract. | Developer 1 |
| **V-09** | `CategoryDiscount.calculateDiscount()` | Information Expert | Violation | Traverses `sale.getProducts()` and calculates category subtotals externally. | Add/use a method on `Sale` to calculate the subtotal for a category. | Developer 1; coordinate with Technical Leader (`Sale`). |
| **V-10** | `ProductService.updateStock()` | Information Expert | Violation | Calculates remaining stock with `product.getStock() - quantitySold` instead of delegating to `Product`. | Add `Product.reduceStock(int quantity)` and delegate mutation. | Developer 1 |
| **V-11** | `ProductService.registerVideoGame()` / `registerConsole()` | High Cohesion / Pure Fabrication | Violation | Duplicate-ID checks are performed inline instead of being delegated to the validation component. | Move uniqueness/existence checks to `ProductValidator`. | Developer 1 |
| **V-12** | `Return.calculateRefundAmount()` / `generateReturnReceipt()` | Information Expert / Low Coupling | Violation | Iterates over `originalSale.getProducts()` directly to inspect subtotals and discounts. | Ask `Sale` for relevant subtotal/discount information through high-level methods. | Developer 1 |
| **V-13** | `Sale.canBeReturned()` | High Cohesion | Violation | Parses a string date using `LocalDate.parse(this.date)` inside policy evaluation. | Store date as `LocalDate` where feasible or isolate parsing in a conversion/validation layer. | Developer 1 |
| **V-14** | `Sale.canBeReturned()` | Protected Variations | Violation | Calls `LocalDate.now()` directly, coupling the rule to the system clock and hindering deterministic tests. | Provide an overload `canBeReturned(LocalDate evaluationDate)` with a default fallback. | Developer 1 |
| **V-15** | `AccessoryService.updateStock()` / `restoreStock()` | Information Expert | Violation | Computes stock mutations externally via `setStock(...)` (matching V-06 and V-10). | Use `Product.addStock` and `Product.reduceStock` operations created for V-06 and V-10. | Developer 2 |
| **V-16** | `AccessoryService.listAccessoriesByType()` | Polymorphism | Violation | Selects by concrete class (`accessory instanceof Controller`, etc.) instead of answering `getCategory()`. | Compare requested type with `accessory.getCategory()` ignoring case. | Developer 2 |
| **V-17** | `PromotionService.registerCategoryDiscount()` | Pure Fabrication / High Cohesion | Violation | Validates target category inline (`VIDEOGAME`, `CONSOLE`, `ACCESSORY`) and throws `IllegalArgumentException`. | Create `PromotionValidator` in the `validation` package and call it from the service. | Developer 2 |
| **V-18** | `ReturnService.registerReturn()` | High Cohesion | Violation | Mixes validation, 30-day window checks, counting loops, ID generation, warranty cancellation, stock restoration, and persistence in one 77-line method. | Extract checks into a dedicated `ReturnValidator` in the `validation` package. | Developer 2 |
| **V-19** | `ReturnService.calculateMonthlySales()` | Information Expert | Violation | Loops over `saleService.getAllSales()`, parses dates, and computes totals externally. | Add a monthly total method to `SaleService` and delegate to it. | Technical Leader (`SaleService`); Developer 2 (`ReturnService`). |
| **V-20** | `findSaleById` in `ReturnService`, `ReturnRepository`, `WarrantyRepository` | Information Expert / Low Coupling | Violation | Private lookup method comparing string IDs is duplicated across three separate classes. | Add centralized lookup by ID to `SaleService` and `SaleRepository`; delete private copies. | Technical Leader adds lookups; Developer 2 removes duplicates. |
| **V-21** | `AccessoryService`, `PromotionService`, `ReturnService`, `WarrantyService` | Indirection / Protected Variations | Violation | Declare concrete repository classes directly instead of depending on abstractions. | Extract repository interfaces (`AccessoryRepository`, etc.), rename current classes to `File...Repository`, and inject interfaces. | Developer 2; Technical Leader updates `Main`. |
| **V-22** | `WarrantyService` | Low Coupling | Violation | Declares and receives an unused `ProductService` dependency in its constructor. | Remove the field and constructor parameter. | Developer 2; Technical Leader updates `Main`. |

---

## 4. Main Corrective Actions & Coordination Order

1. **Product State Encapsulation (V-06, V-10, V-15):** Implement `Product.addStock(int quantity)` and `Product.reduceStock(int quantity)`. Technical Leader handles `restoreStock()`; Developer 1 handles `updateStock()`; Developer 2 updates `AccessoryService`.
2. **Sale Lookups & Monthly Totals (V-19, V-20):** Technical Leader adds centralized ID lookups and monthly totals to `SaleService`/`SaleRepository`; Developer 2 removes duplicated private lookups.
3. **Repository Interfaces (V-04, V-21):** Refactor repositories to persist/load raw IDs first (V-04), then extract interfaces and rename file implementations (V-21). Technical Leader updates `Main` composition root.
4. **Polymorphic Handlers (V-05):** Remove concrete-type branching (`instanceof`) from services by establishing proper inventory/warranty abstractions across `Product`, `Console`, and services.

---

## 5. Out-of-Scope Functional Observations (F-01 to F-06)

*Note: These defects are documented for future iterations and are left uncorrected in this refactoring cycle to avoid breaking expected user-facing behavior:*

* **F-01:** Warranty IDs can repeat if older warranties are canceled and removed.
* **F-02:** Canceling warranties during a return removes and refunds all matching product warranties in a sale batch rather than individual units.
* **F-03:** Non-standard error handling in accessory/promotion/return/warranty repositories (`System.out.println` vs. `PersistenceException`).
* **F-04:** Services throwing generic `IllegalArgumentException` instead of domain-specific validation exceptions.
* **F-05:** Corrupted/unresolvable file lines being silently dropped upon saving.
* **F-06:** Accessories omitted during historical sales reloading inside `SaleRepository.findAll`.
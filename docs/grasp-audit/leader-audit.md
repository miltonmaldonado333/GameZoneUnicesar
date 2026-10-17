# GRASP Audit Report - Technical Leader

**Auditor:** Milton Andrés Maldonado Salcedo  
**Role:** Technical Leader  
**Date:** 2026-10-17

---

## Assigned Modules & Classes

- `com.gamezone.ui.GameZoneUI`
- `com.gamezone.model.Sale`
- `com.gamezone.persistence.SaleRepository`
- `com.gamezone.service.SaleService`
- `com.gamezone.Main.main`

---

## GRASP Evaluation Matrix

| Class / Method                        | Pattern Evaluated                  |    Status    | Violation / Compliance Evidence                                                                                                                                                      | Corrective Action Proposal                                                                                              |
| :------------------------------------ | :--------------------------------- | :----------: | :----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :---------------------------------------------------------------------------------------------------------------------- |
| `GameZoneUI.registerCategoryDiscount` | Controller / High Cohesion         | ⚠️ Violation | Infers available categories inside the view by iterating through products and inspecting class names (`p.getClass().getSimpleName()`).                                               | Move category extraction logic to `ProductService.getAvailableCategories()` and consume it directly in the UI.          |
| `GameZoneUI.registerSale`             | Indirection / Low Coupling         | ⚠️ Violation | Handles item lookup fallback logic by catching `ResourceNotFoundException` sequentially between `ProductService` and `AccessoryService` from the UI layer.                           | Provide a unified facade method `findSaleableItemById(id)` in the service layer.                                        |
| `GameZoneUI`                          | High Cohesion                      | ⚠️ Violation | Accumulates input parsing, business rule execution, service orchestration, and exception handling within a single presentation class.                                                | Restrict `GameZoneUI` strictly to console input parsing and menu display, delegating workflows to services/controllers. |
| `Sale.getSubtotal`                    | Information Expert                 | ✅ Compliant | Encapsulates the calculation of item subtotals using its internal list of products (`products`).                                                                                     | None required.                                                                                                          |
| `Sale.calculateTotal`                 | Information Expert / High Cohesion | ✅ Compliant | Computes the final total using its own internal state (`getSubtotal()`, `discountAmount`, `extendedWarrantyCost`).                                                                   | None required.                                                                                                          |
| `Sale.generateReceipt`                | Information Expert                 | ✅ Compliant | Formats and constructs the formatted transaction receipt using its own structural fields.                                                                                            | None required.                                                                                                          |
| `Sale.canBeReturned`                  | Information Expert                 | ✅ Compliant | Evaluates return eligibility (30-day policy rule) internally using its transaction date.                                                                                             | None required.                                                                                                          |
| `SaleRepository`                      | Pure Fabrication                   | ✅ Compliant | Handles flat-file persistence (`sales.txt`), preventing the `Sale` domain entity from bearing file I/O responsibilities.                                                             | None required.                                                                                                          |
| `SaleRepository.findAll`              | Low Coupling / Indirection         | ⚠️ Violation | Direct dependency on services (`PersonService`, `ProductService`) to reconstruct domain objects during file reading, tightly coupling persistence to services.                       | Use a mapper or pass primitive IDs to resolve dependencies outside the repository layer.                                |
| `SaleService.registerSale`            | Controller / High Cohesion         | ⚠️ Violation | Orchestrates the sale transaction workflow but relies on explicit type checking (`instanceof Console`, `instanceof Accessory`) to handle inventory updates and warranty assignments. | Remove `instanceof` blocks using polymorphic dispatching or dedicated inventory/warranty handlers.                      |
| `SaleService`                         | Low Coupling                       | ✅ Compliant | Delegates domain validations to `SaleValidator` and `ProductValidator`, cleanly separating concerns.                                                                                 | None required.                                                                                                          |
| `main.main`                           | Pure Fabrication / Creator         | ✅ Compliant | Functions as the application bootstrapper, instantiating and connecting the persistence, service, and UI layers.                                                                     | None required.                                                                                                          |
| `main.main`                           | Low Coupling                       | ⚠️ Violation | Exhibits complex initialization ordering (code comments document circular dependencies previously resolved between `WarrantyRepository`, `SaleRepository`, and `SaleService`).       | Apply Dependency Injection (DI) using a Factory pattern or DI container to isolate the object graph assembly.           |

---

## Detailed Findings & Justifications

### 1. `com.gamezone.ui.GameZoneUI`

- **Evaluated Patterns:** Controller, High Cohesion, Indirection, Low Coupling.
- **Analysis:**
  - _Current State:_ Serves as the console user interface class, managing menu navigation, user input collection, and result display.
  - _Identified Issues:_
    1. **Controller & High Cohesion Violation (`registerCategoryDiscount`):** Dynamically extracts product categories via type reflection (`getClass().getSimpleName()`), embedding domain query logic in the view.
    2. **Indirection & Low Coupling Violation (`registerSale`):** Executes chained `ResourceNotFoundException` catches between two distinct services directly within the UI flow.
    3. **Low Cohesion:** Combines input validation, UI formatting, exception handling, and multi-service delegation into a single class.
  - _Proposed Refactoring:_
    1. Implement `ProductService.getAvailableCategories()`.
    2. Create a unified `findSaleableItemById(id)` facade method in the service layer.
    3. Limit `GameZoneUI` purely to console input/output handling.

---

### 2. `com.gamezone.model.Sale`

- **Evaluated Patterns:** Information Expert, High Cohesion, Low Coupling, Creator.
- **Analysis:**
  - _Current State:_ Domain entity containing transaction state (client, seller, items, discounts, warranties) and calculating subtotals, totals, receipts, and return eligibility.
  - _Identified Issues:_ None. Operates as an **Information Expert** by processing its own state without exposing internal calculation logic to external classes.
  - _Proposed Refactoring:_ None required. Fully complies with GRASP guidelines.

---

### 3. `com.gamezone.persistence.SaleRepository`

- **Evaluated Patterns:** Pure Fabrication, Low Coupling, Indirection, High Cohesion.
- **Analysis:**
  - _Current State:_ Persistence component responsible for storing and parsing sales records in `sales.txt`.
  - _Identified Issues:_
    1. **Pure Fabrication Compliance:** Decouples file storage and reading concerns from domain entities (`Sale`).
    2. **Low Coupling Violation (`findAll`):** Injects `PersonService` and `ProductService` into its constructor to resolve entity relationships while reading, inverting clean architectural boundaries.
  - _Proposed Refactoring:_
    1. Extract relationship resolution logic to a dedicated mapper or service layer so `SaleRepository` returns raw domain data or primitive IDs.

---

### 4. `com.gamezone.service.SaleService`

- **Evaluated Patterns:** Controller, High Cohesion, Low Coupling, Polymorphism, Indirection.
- **Analysis:**
  - _Current State:_ Core service centralizing sale transaction processing (calculations, promotion evaluation, warranty assignment, stock updates, and persistence).
  - _Identified Issues:_
    1. **Polymorphism Violation:** Uses `instanceof` branching (`instanceof Console`, `instanceof Accessory`) to determine specific stock deduction and warranty execution behaviors.
  - _Proposed Refactoring:_
    1. Replace `instanceof` conditions with polymorphic method calls on product abstractions or delegate to specialized inventory strategies.

---

### 5. `com.gamezone.Main.main`

- **Evaluated Patterns:** Pure Fabrication, Creator, Low Coupling, Controller.
- **Analysis:**
  - _Current State:_ Application entry point (Bootstrapper) responsible for building the object graph (repositories, services, UI) and launching the execution loop.
  - _Identified Issues:_
    1. **Pure Fabrication & Creator Compliance:** Acts as a clean global creator to assemble application layers without cluttering business objects.
    2. **Coupling & Maintenance Risk:** Constructing the entire dependency tree manually makes the startup sequence fragile to constructor changes across layers.
  - _Proposed Refactoring:_
    1. Implement a **Factory** pattern or introduce a lightweight **Dependency Injection (DI)** container to handle layer instantiation and configuration modularly.

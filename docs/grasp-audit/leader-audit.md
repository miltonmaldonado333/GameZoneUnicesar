# GRASP Audit Report - Technical Leader

**Auditor:** Milton Andrés Maldonado Salcedo  
**Role:** Technical Leader  
**Date:** 2026-10-17  

---

## Assigned Modules & Classes

- `com.gamezone.ui.GameZoneUI`
- `com.gamezone.model.Sale`

---

## GRASP Evaluation Matrix

| Class / Method | Pattern Evaluated | Status | Violation / Compliance Evidence | Corrective Action Proposal |
| :--- | :--- | :---: | :--- | :--- |
| `GameZoneUI.registerCategoryDiscount` | Controller / High Cohesion | ⚠️ Violation | Extracts and infers available product categories dynamically inside the UI layer by iterating over products and checking class names (`p.getClass().getSimpleName()`). | Move category discovery and extraction logic to `ProductService.getAvailableCategories()` and consume it directly in the UI. |
| `GameZoneUI.registerSale` | Indirection / Low Coupling | ⚠️ Violation | Manages item search fallback logic by catching `ResourceNotFoundException` sequentially between `ProductService` and `AccessoryService` directly within the UI flow. | Provide a unified lookup facade method `findSaleableItemById(id)` in the service layer to eliminate cross-service exception catching in the UI. |
| `GameZoneUI` | High Cohesion | ⚠️ Violation | Accumulates input validation, domain data formatting, multi-service orchestrations, and exception handlings within a single presentation class. | Keep `GameZoneUI` strictly focused on user input collection and output presentation, delegating all coordination and processing to service classes. |
| `Sale.getSubtotal` | Information Expert | ✅ Compliant | Encapsulates the calculation of item subtotals directly within the entity that owns the list of products (`products`). | None required. Maintains proper encapsulation of financial calculations. |
| `Sale.calculateTotal` | Information Expert / High Cohesion | ✅ Compliant | Computes final pricing using internal state (`getSubtotal()`, `discountAmount`, `extendedWarrantyCost`), avoiding external calculation logic. | None required. |
| `Sale.generateReceipt` | Information Expert | ✅ Compliant | Formats and constructs the transaction receipt using its own structural fields and line items. | None required. |
| `Sale.canBeReturned` | Information Expert | ✅ Compliant | Evaluates return eligibility internally using its transaction date relative to business rules (30-day window). | None required. |

---

## Detailed Findings & Justifications

### 1. `com.gamezone.ui.GameZoneUI`
- **Evaluated Patterns:** Controller, High Cohesion, Indirection, Low Coupling.
- **Analysis:**
  - *Current State:* Serves as the primary console interaction class, responsible for reading user choices, displaying menus, requesting inputs, and presenting execution results.
  - *Identified Issues:*
    1. **Controller & High Cohesion Violation (`registerCategoryDiscount`):** The UI inspects product lists directly and determines available product categories using reflection/type inspection (`p.getClass().getSimpleName()`). This embeds domain query logic within the view.
    2. **Indirection & Low Coupling Violation (`registerSale`):** When looking up saleable items by ID, the UI attempts to query `ProductService` first, catches `ResourceNotFoundException`, and then queries `AccessoryService`. This couples the UI directly to two separate domain services and their fallback flow.
    3. **High Cohesion Violation:** The class acts as a large controller/view hybrid, mixing UI formatting, input parsing, error handling, and business flow orchestration.
  - *Proposed Refactoring:*
    1. Add a `getAvailableCategories()` method in `ProductService` that returns a clean list/set of category names for the UI to display.
    2. Add a unified `findSaleableItemById(String id)` method in the service layer that handles internal lookup across products and accessories, returning a single result or throwing a single domain exception.
    3. Restrict `GameZoneUI` to purely collecting strings/numbers from `Scanner` and passing them to service methods.

---

### 2. `com.gamezone.model.Sale`
- **Evaluated Patterns:** Information Expert, High Cohesion, Low Coupling, Creator.
- **Analysis:**
  - *Current State:* Domain entity representing a transaction, encapsulating attributes for client, seller, products/accessories, promotion discounts, extended warranties, total calculation, receipt generation, and return eligibility.
  - *Identified Issues:* None. The class acts as an **Information Expert** by performing calculations on its own data (subtotal, total, receipts, return eligibility) rather than relying on external services to manipulate its state. It exhibits **High Cohesion** by focusing strictly on sale domain responsibilities and maintains **Low Coupling** by referencing base abstractions/models (`Client`, `Seller`, `Product`).
  - *Proposed Refactoring:* None required. The class fully complies with all applicable GRASP principles.
# GRASP Audit Report - Technical Leader (UI Scope)

**Auditor:** Milton Andrés Maldonado Salcedo  
**Role:** Technical Leader  
**Date:** 2026-10-17

---

## Assigned Modules & Classes

- `com.gamezone.ui.GameZoneUI`

---

## GRASP Evaluation Matrix

| Class / Method                        | Pattern Evaluated          |    Status    | Violation / Compliance Evidence                                                                                                                                        | Corrective Action Proposal                                                                                                                          |
| :------------------------------------ | :------------------------- | :----------: | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :-------------------------------------------------------------------------------------------------------------------------------------------------- |
| `GameZoneUI.registerCategoryDiscount` | Controller / High Cohesion | ⚠️ Violation | Extracts and infers available product categories dynamically inside the UI layer by iterating over products and checking class names (`p.getClass().getSimpleName()`). | Move category discovery and extraction logic to `ProductService.getAvailableCategories()` and consume it directly in the UI.                        |
| `GameZoneUI.registerSale`             | Indirection / Low Coupling | ⚠️ Violation | Manages item search fallback logic by catching `ResourceNotFoundException` sequentially between `ProductService` and `AccessoryService` directly within the UI flow.   | Provide a unified lookup facade method `findSaleableItemById(id)` in the service layer to eliminate cross-service exception catching in the UI.     |
| `GameZoneUI`                          | High Cohesion              | ⚠️ Violation | Accumulates input validation, domain data formatting, multi-service orchestrations, and exception handlings within a single presentation class.                        | Keep `GameZoneUI` strictly focused on user input collection and output presentation, delegating all coordination and processing to service classes. |

---

## Detailed Findings & Justifications

### 1. `com.gamezone.ui.GameZoneUI`

- **Evaluated Patterns:** Controller, High Cohesion, Indirection, Low Coupling.
- **Analysis:**
  - _Current State:_ Serves as the primary console interaction class, responsible for reading user choices, displaying menus, requesting inputs, and presenting execution results.
  - _Identified Issues:_
    1. **Controller & High Cohesion Violation (`registerCategoryDiscount`):** The UI inspects product lists directly and determines available product categories using reflection/type inspection (`p.getClass().getSimpleName()`). This embeds domain query logic within the view.
    2. **Indirection & Low Coupling Violation (`registerSale`):** When looking up saleable items by ID, the UI attempts to query `ProductService` first, catches `ResourceNotFoundException`, and then queries `AccessoryService`. This couples the UI directly to two separate domain services and their fallback flow.
    3. **High Cohesion Violation:** The class acts as a large controller/view hybrid, mixing UI formatting, input parsing, error handling, and business flow orchestration.
  - _Proposed Refactoring:_
    1. Add a `getAvailableCategories()` method in `ProductService` that returns a clean list/set of category names for the UI to display.
    2. Add a unified `findSaleableItemById(String id)` method in the service layer that handles internal lookup across products and accessories, returning a single result or throwing a single domain exception.
    3. Restrict `GameZoneUI` to purely collecting strings/numbers from `Scanner` and passing them to service methods.

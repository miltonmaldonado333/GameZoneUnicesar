
# Consolidated GRASP Audit Report — GameZone Unicesar

**Audit date:** 2026-10-17  
**Technical Leader:** Milton Andrés Maldonado Salcedo  
**Developer 1:** Jesús Manuel Martínez Baute  
**Scope:** Consolidation of the Technical Leader and Developer 1 reports supplied for this task.

> **Consolidation status:** This document consolidates the two reports provided. The assignment specifies that the Technical Leader must consolidate all three individual reports; therefore, the findings and coverage must be reviewed and completed with Developer 2's report before this document is treated as the final team-wide audit.

---

## 1. Purpose and criteria

The purpose of this audit is to identify GRASP design violations in GameZone Unicesar and propose refactoring actions while preserving the system's externally observable behavior. The audit evaluates responsibility assignment, coupling, cohesion, polymorphism, persistence boundaries, and protection against foreseeable changes.

A finding is recorded once even when it affects more than one GRASP pattern. The pattern most directly guiding the correction is identified, and related patterns are listed where appropriate. The final violation IDs below are provisional consolidation IDs and should be checked against Developer 2's report before being finalized.

## 2. Scope and responsibility

### Technical Leader

- `com.gamezone.ui.GameZoneUI` (including category discounts and sale registration)
- `com.gamezone.model.Sale`
- `com.gamezone.persistence.SaleRepository`
- `com.gamezone.service.SaleService`
- `com.gamezone.service.ProductService.restoreStock`
- `com.gamezone.Main.main`

### Developer 1

- Product hierarchy: `Product`, `VideoGame`, `Console`, `ProductRepository`, and relevant `ProductService` methods
- Accessory hierarchy: `Accessory`, `Cable`, `Controller`, `Memory`
- Promotion hierarchy: `Promotion`, `PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount`
- Warranty hierarchy: `Warranty`, `BasicWarranty`, `ExtendedWarranty`
- Returns: `Return` and `Sale.canBeReturned()`
- Exceptions: `GameZoneException`, `BusinessRuleException`, `InvalidDataException`, `PersistenceException`, `ResourceNotFoundException`
- Validation: `PersonValidator`, `ProductValidator`, `SaleValidator`

The supplied materials do not include Developer 2's individual audit report. The following coverage and findings therefore represent only the two reports listed above.

## 3. Consolidated GRASP evaluation matrix

| ID | Class / method | Main GRASP pattern | Status | Evidence and issue | Proposed correction | Responsible owner |
|---|---|---|---|---|---|---|
| V-01 | `GameZoneUI.registerCategoryDiscount()` | Controller / High Cohesion | Violation | The UI derives categories by iterating through products and inspecting `getClass().getSimpleName()`. Domain-query logic is embedded in the presentation layer. | Add `ProductService.getAvailableCategories()` and have the UI consume its result. | Technical Leader for UI integration; coordinate with Developer 1 if the service API changes. |
| V-02 | `GameZoneUI.registerSale()` | Indirection / Low Coupling | Violation | The UI performs sequential lookup attempts by catching `ResourceNotFoundException` from `ProductService` and `AccessoryService`. | Expose a unified service-layer `findSaleableItemById(String id)` lookup and delegate the search to it. | Technical Leader for UI integration; coordinate with the owner of the relevant service classes. |
| V-03 | `GameZoneUI` | High Cohesion | Violation | The class combines input parsing, business workflow orchestration, exception handling, and output formatting. | Keep the UI focused on console input/output and delegate business workflows and validation to services or controllers. | Technical Leader |
| V-04 | `SaleRepository.findAll()` | Low Coupling / Indirection | Violation | The repository depends on `PersonService` and `ProductService` to reconstruct relationships while reading persisted sales. | Use a data mapper or return persisted IDs/raw records so relationship resolution occurs outside the persistence repository. | Technical Leader |
| V-05 | `SaleService.registerSale()` | Polymorphism / Low Coupling | Violation | Explicit `instanceof Console` and `instanceof Accessory` checks select inventory and warranty behavior. | Replace type checks with polymorphic operations on product abstractions or specialized inventory/warranty handlers. | Technical Leader |
| V-06 | `ProductService.restoreStock()` | Information Expert | Violation | The service increments stock using `product.setStock(product.getStock() + quantity)`, manipulating entity state externally. | Add `Product.addStock(int quantity)` and delegate the stock change to the product. | Technical Leader, for this method as assigned in the requirement. |
| V-07 | `Main.main()` | Low Coupling / Indirection | Violation | Manual construction of the full dependency graph creates fragile initialization ordering and documents circular dependencies among repositories and services. | Encapsulate object-graph construction in a factory or lightweight dependency-injection composition root. | Technical Leader |
| V-08 | `CategoryDiscount.calculateDiscount()` | Polymorphism | Violation | The method uses `instanceof` checks for product types instead of polymorphic category identification. | Use `product.getCategory()` or another stable polymorphic contract rather than concrete-type checks. | Developer 1 |
| V-09 | `CategoryDiscount.calculateDiscount()` | Information Expert | Violation | The discount class traverses `sale.getProducts()` and calculates category subtotals externally. | Add/use a method on `Sale` to calculate the subtotal for a category, keeping sale-item calculations with the Information Expert. | Developer 1; coordinate with the Technical Leader because `Sale` is in the leader's scope. |
| V-10 | `ProductService.updateStock()` | Information Expert | Violation | The service calculates remaining stock with `product.getStock() - quantitySold` instead of delegating the state change to `Product`. | Add a domain operation such as `Product.reduceStock(int quantity)` and delegate the mutation. | Developer 1 |
| V-11 | `ProductService.registerVideoGame()` and `registerConsole()` | High Cohesion / Pure Fabrication | Violation | Duplicate-ID checks are performed inline in registration methods instead of being delegated to the validation component. | Move the uniqueness/existence checks to `ProductValidator` and reuse them from the registration methods. | Developer 1 |
| V-12 | `Return.calculateRefundAmount()` and `Return.generateReturnReceipt()` | Information Expert / Low Coupling | Violation | `Return` iterates over `originalSale.getProducts()` to calculate sale subtotals and inspect discount information. | Ask `Sale` for the relevant subtotal and discount information through high-level methods, avoiding direct traversal of its internal collection. | Developer 1 |
| V-13 | `Sale.canBeReturned()` | High Cohesion | Violation | The method parses a string date using `LocalDate.parse(this.date)` while evaluating the return policy. | Store the date as `LocalDate` where feasible or isolate parsing in a dedicated conversion/validation layer. | Developer 1 for the method identified in the individual report; coordinate with the Technical Leader if the field or constructors are shared. |
| V-14 | `Sale.canBeReturned()` | Protected Variations | Violation | The method calls `LocalDate.now()` directly, coupling the rule to the system clock and making date-based tests less deterministic. | Provide an overload such as `canBeReturned(LocalDate evaluationDate)` and keep the existing user-facing behavior through a compatible default call. | Developer 1 for the method identified in the individual report; coordinate with the Technical Leader if needed. |

## 4. Findings that are compliant in the supplied reports

The following findings were assessed as compliant in the two reports and do not require corrective action based on the evidence supplied:

- **`Sale` calculations and receipt:** `getSubtotal()`, `calculateTotal()`, and `generateReceipt()` use the sale's own state to calculate and format transaction information.
- **Return eligibility ownership:** `Sale.canBeReturned()` has the transaction date needed to evaluate the 30-day policy. The date parsing and system-clock dependencies are separate concerns recorded in V-13 and V-14.
- **`SaleRepository` as Pure Fabrication:** the repository isolates flat-file persistence from the domain entity. Its service dependencies are a separate Low Coupling/Indirection issue (V-04).
- **Product and accessory domain hierarchy:** `Accessory`, `Cable`, `Controller`, `Memory`, `Product`, `VideoGame`, and `Console` encapsulate their domain-specific data and descriptions and use polymorphic category/description methods.
- **Warranty hierarchy:** `Warranty`, `BasicWarranty`, and `ExtendedWarranty` encapsulate warranty data, duration, type, validity, and additional-cost behavior through polymorphism.
- **Promotion hierarchy:** `Promotion`, `PercentageDiscount`, and `BulkPurchaseDiscount` encapsulate promotion metadata and their respective discount rules. `CategoryDiscount` has the specific violations listed in V-08 and V-09.
- **`ProductRepository`:** the repository contract provides indirection between service logic and persistence implementation.
- **Exception hierarchy:** `GameZoneException`, `BusinessRuleException`, `InvalidDataException`, `PersistenceException`, and `ResourceNotFoundException` encapsulate application error information without direct UI/persistence coupling.
- **Validators:** `PersonValidator`, `ProductValidator`, and `SaleValidator` centralize validation responsibilities and depend on domain objects and application exceptions.
- **`ProductService` low coupling:** the Developer 1 report identifies its use of the `ProductRepository` interface and its role as a service intermediary as compliant. This does not negate the separate violations in V-10 and V-11.
- **`SaleService` validation separation:** the Technical Leader report identifies delegation to `SaleValidator` and `ProductValidator` as compliant, independently of the `instanceof` issue in V-05.
- **`Main.main()` as Creator/Pure Fabrication:** the entry point appropriately assembles the application layers; V-07 concerns the fragility of manual dependency construction, not the need for a composition root itself.

## 5. Main corrective actions and coordination

1. **Encapsulation of product state:** implement `Product.addStock(int quantity)` and `Product.reduceStock(int quantity)`. The Technical Leader handles `restoreStock()`; Developer 1 handles `updateStock()`. Both changes must agree on validation and stock invariants.

2. **Category and sale lookup APIs:** Developer 1 and the Technical Leader must agree on stable service/domain contracts before changing the UI or `CategoryDiscount`. Avoid introducing new dependencies from the UI into persistence or model internals.

3. **Sale calculations and returns:** add the required high-level subtotal/discount queries to `Sale`, then update `CategoryDiscount` and `Return` to use them. Because `Sale` and its consuming classes are assigned to different people in parts of the project, the Technical Leader coordinates the interface and commit order.

4. **Polymorphic inventory/warranty behavior:** remove concrete-type branching from `SaleService.registerSale()` by agreeing on product operations or dedicated handlers. Keep warranty and stock behavior unchanged from the user's perspective.

5. **Persistence boundaries:** remove `PersonService` and `ProductService` dependencies from `SaleRepository`; resolve IDs in an appropriate service or mapper layer.

6. **Deterministic return-policy testing:** separate date parsing from the return rule and make the evaluation date injectable while preserving existing menu behavior.

7. **Application initialization:** centralize object creation in a factory/composition root and remove fragile circular initialization dependencies without changing the startup flow seen by the user.

## 6. Boundary findings

The following findings require coordination across assigned areas and should be recorded once in the final team audit:

- **V-01 / V-02:** UI changes depend on service-layer APIs. The Technical Leader owns the UI changes; the relevant service owner must implement or confirm the API.
- **V-06 / V-10:** both affect product stock mutation. The Technical Leader owns `ProductService.restoreStock()` and Developer 1 owns `ProductService.updateStock()` and the `Product` domain operation. They must use consistent validation and stock invariants.
- **V-09 / V-12:** both need `Sale` to expose subtotal/discount information. Developer 1 owns `CategoryDiscount` and `Return`; the Technical Leader owns `Sale`. Coordinate the method contract and sequence the commits.
- **V-13 / V-14:** the date-related refactor touches `Sale`, which is in the Technical Leader's overall module scope but the supplied Developer 1 report assigns `Sale.canBeReturned()` to Developer 1. Confirm the actual method ownership in `TEAM.md` and Git history before implementation, as required by the assignment.
- **V-05:** if specialized inventory or warranty handlers are introduced, assign their creation to the owner of the violation and coordinate any required service-interface changes.

## 7. Out-of-scope findings

No independently verified functional defect has been established from the supplied reports. The reported issues concern design and responsibility assignment. If testing reveals a defect that would change outputs, messages, menu behavior, or persisted file contents, document it separately as an out-of-scope functional finding instead of silently changing behavior during this refactor.

## 8. Preservation of behavior and verification plan

The refactoring must preserve the existing menu operations, input/output behavior, Spanish user-facing messages, and persisted file formats.

Before and after the changes:

1. Run `mvn clean compile`.
2. Exercise the same menu operations with the same inputs and compare outputs.
3. Verify product registration, stock deduction/restoration, category discounts, sale registration, warranty assignment, returns, receipts, and persistence/reloading.
4. Record test evidence and the relevant violation ID in `docs/grasp-refactor-log.md`.
5. Keep each refactoring commit focused and include its violation ID in the commit message.
6. Review the before/after Mermaid class diagrams and ensure each changed class references its violation ID.

## 9. GRASP patterns: consolidated preliminary verdict

The supplied reports identify violations in the following patterns:

- **Information Expert:** V-06, V-09, V-10, V-12
- **Creator:** no confirmed violation in the supplied findings; `Main.main()` is considered an appropriate application composition point.
- **Controller:** V-03 concerns excessive responsibilities in the UI; the final team verdict should distinguish event coordination from presentation-layer cohesion.
- **Low Coupling:** V-02, V-04, V-05, V-12
- **High Cohesion:** V-01, V-03, V-11, V-13
- **Polymorphism:** V-05, V-08
- **Pure Fabrication:** V-11
- **Indirection:** V-02, V-04, V-07
- **Protected Variations:** V-14

These are preliminary pattern groupings based only on the two reports provided. The final team verdict for all nine patterns must be updated after Developer 2's audit is incorporated.

## 10. Items to complete before submitting the final consolidated audit

- Add Developer 2's audited modules and findings.
- Verify the full class-coverage table against `TEAM.md` and the repository's commit history.
- Confirm the commit hash of `develop` that was audited.
- Validate evidence against the actual source code and add exact line references or code excerpts where available.
- Unify any overlapping findings with Developer 2's report and assign one final ID per violation.
- Confirm the responsible owner for cross-boundary methods before beginning Phase 2.
- Ensure the final document is saved as `docs/grasp-audit/consolidated-audit.md`.

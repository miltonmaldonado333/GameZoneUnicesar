
# Consolidated GRASP Audit Report — GameZone Unicesar

**Audit date:** 2026-10-17  
**Technical Leader:** Milton Andrés Maldonado Salcedo  
**Developer 1:** Jesús Manuel Martínez Baute  
**Developer 2:** Luis Manuel Corzo Castro  
**Audited baseline:** `origin/develop` at commit `336790c` (Developer 2 evidence)  
**Scope:** Consolidation of the Technical Leader, Developer 1 and Developer 2 reports.

> **Consolidation status:** This document consolidates the three individual reports. Developer 2's findings are incorporated as V-15 to V-22 and as extensions of V-04 and V-05, which Developer 2 reported from the persistence and service side of the same problems. The IDs V-01 to V-14 are kept as already referenced by the Technical Leader and Developer 1.

---

## 1. Purpose and criteria

The purpose of this audit is to identify GRASP design violations in GameZone Unicesar and propose refactoring actions while preserving the system's externally observable behavior. The audit evaluates responsibility assignment, coupling, cohesion, polymorphism, persistence boundaries, and protection against foreseeable changes.

A finding is recorded once even when it affects more than one GRASP pattern. The pattern most directly guiding the correction is identified, and related patterns are listed where appropriate. When two integrantes report the same problem from both sides of a boundary, it is unified in one row (V-04 and V-05).

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

### Developer 2

- Persons: `Person`, `Client`, `Seller`, `PersonRepository`, `FilePersonRepository`, `PersonService`
- Accessories: `AccessoryRepository`, `AccessoryService`
- Promotions: `PromotionRepository`, `PromotionService`
- Returns: `ReturnRepository`, `ReturnService`, including the monthly balance
- Warranties: `WarrantyRepository`, `WarrantyService`
- Exceptions and validators: only their use in `PersonService`, `ProductService`, `SaleService` and in the repositories changed in Requirement 6

The three scope lists above cover the classes of the responsibility table. Classes shared by two integrantes (`Sale`, `ProductService`, `SaleService`) were audited method by method by the person who developed each method. The coverage must still be checked against `TEAM.md` and the commit history before this document is final.

## 3. Consolidated GRASP evaluation matrix

| ID | Class / method | Main GRASP pattern | Status | Evidence and issue | Proposed correction | Responsible owner |
|---|---|---|---|---|---|---|
| V-01 | `GameZoneUI.registerCategoryDiscount()` | Controller / High Cohesion | Violation | The UI derives categories by iterating through products and inspecting `getClass().getSimpleName()`. Domain-query logic is embedded in the presentation layer. | Add `ProductService.getAvailableCategories()` and have the UI consume its result. | Technical Leader for UI integration; coordinate with Developer 1 if the service API changes. |
| V-02 | `GameZoneUI.registerSale()` | Indirection / Low Coupling | Violation | The UI performs sequential lookup attempts by catching `ResourceNotFoundException` from `ProductService` and `AccessoryService`. | Expose a unified service-layer `findSaleableItemById(String id)` lookup and delegate the search to it. | Technical Leader for UI integration; coordinate with the owner of the relevant service classes. |
| V-03 | `GameZoneUI` | High Cohesion | Violation | The class combines input parsing, business workflow orchestration, exception handling, and output formatting. | Keep the UI focused on console input/output and delegate business workflows and validation to services or controllers. | Technical Leader |
| V-04 | `SaleRepository.findAll()`, `ReturnRepository` and `WarrantyRepository` | Low Coupling / Indirection | Violation | `SaleRepository` depends on `PersonService` and `ProductService` to reconstruct relationships while reading persisted sales. The same happens in `ReturnRepository` (imports `AccessoryService`, `ProductService` and `SaleService`, lines 16-18) and `WarrantyRepository` (imports `ProductService`, line 18), which use them in `parseLine` (developer2-audit V-08). | Make the three repositories load and persist identifiers or raw records and rebuild the objects in the service layer, with one mechanism for all three. | Technical Leader for `SaleRepository` and `Main`; Developer 2 for `ReturnRepository` and `WarrantyRepository`. Boundary finding. |
| V-05 | `SaleService.registerSale()`, `ReturnService.registerReturn()` and `WarrantyService.syncPastConsoleWarranties()` | Polymorphism / Low Coupling | Violation | Explicit `instanceof Console` and `instanceof Accessory` checks select inventory and warranty behavior. The same tests exist in `ReturnService.java` lines 120 and 129 and in `WarrantyService.syncPastConsoleWarranties` (developer2-audit V-05). | Replace type checks with one shared abstraction: a polymorphic query on `Product` for warranty coverage (false by default, true in `Console`) and a common stock-restoring contract fulfilled by `ProductService` and `AccessoryService`. | Technical Leader for `SaleService` and `ProductService`; Developer 1 for `Product` and `Console`; Developer 2 for `ReturnService` and `WarrantyService`. Boundary finding. |
| V-06 | `ProductService.restoreStock()` | Information Expert | Violation | The service increments stock using `product.setStock(product.getStock() + quantity)`, manipulating entity state externally. | Add `Product.addStock(int quantity)` and delegate the stock change to the product. | Technical Leader, for this method as assigned in the requirement. |
| V-07 | `Main.main()` | Low Coupling / Indirection | Violation | Manual construction of the full dependency graph creates fragile initialization ordering and documents circular dependencies among repositories and services. | Encapsulate object-graph construction in a factory or lightweight dependency-injection composition root. | Technical Leader |
| V-08 | `CategoryDiscount.calculateDiscount()` | Polymorphism | Violation | The method uses `instanceof` checks for product types instead of polymorphic category identification. | Use `product.getCategory()` or another stable polymorphic contract rather than concrete-type checks. | Developer 1 |
| V-09 | `CategoryDiscount.calculateDiscount()` | Information Expert | Violation | The discount class traverses `sale.getProducts()` and calculates category subtotals externally. | Add/use a method on `Sale` to calculate the subtotal for a category, keeping sale-item calculations with the Information Expert. | Developer 1; coordinate with the Technical Leader because `Sale` is in the leader's scope. |
| V-10 | `ProductService.updateStock()` | Information Expert | Violation | The service calculates remaining stock with `product.getStock() - quantitySold` instead of delegating the state change to `Product`. | Add a domain operation such as `Product.reduceStock(int quantity)` and delegate the mutation. | Developer 1 |
| V-11 | `ProductService.registerVideoGame()` and `registerConsole()` | High Cohesion / Pure Fabrication | Violation | Duplicate-ID checks are performed inline in registration methods instead of being delegated to the validation component. | Move the uniqueness/existence checks to `ProductValidator` and reuse them from the registration methods. | Developer 1 |
| V-12 | `Return.calculateRefundAmount()` and `Return.generateReturnReceipt()` | Information Expert / Low Coupling | Violation | `Return` iterates over `originalSale.getProducts()` to calculate sale subtotals and inspect discount information. | Ask `Sale` for the relevant subtotal and discount information through high-level methods, avoiding direct traversal of its internal collection. | Developer 1 |
| V-13 | `Sale.canBeReturned()` | High Cohesion | Violation | The method parses a string date using `LocalDate.parse(this.date)` while evaluating the return policy. | Store the date as `LocalDate` where feasible or isolate parsing in a dedicated conversion/validation layer. | Developer 1 for the method identified in the individual report; coordinate with the Technical Leader if the field or constructors are shared. |
| V-14 | `Sale.canBeReturned()` | Protected Variations | Violation | The method calls `LocalDate.now()` directly, coupling the rule to the system clock and making date-based tests less deterministic. | Provide an overload such as `canBeReturned(LocalDate evaluationDate)` and keep the existing user-facing behavior through a compatible default call. | Developer 1 for the method identified in the individual report; coordinate with the Technical Leader if needed. |
| V-15 | `AccessoryService.updateStock()` and `restoreStock()` | Information Expert | Violation | Lines 190 and 207: `accessory.setStock(accessory.getStock() - quantity)` and `accessory.setStock(accessory.getStock() + quantity)`. The service computes the new stock outside the entity, as in V-06 and V-10 (developer2-audit V-01). | Use the `Product.addStock` and `Product.reduceStock` operations created for V-06 and V-10, keeping the boolean result of `updateStock` and the exception of `restoreStock`. | Developer 2, after V-06 and V-10. |
| V-16 | `AccessoryService.listAccessoriesByType()` | Polymorphism | Violation | Lines 116, 118 and 120 select by concrete class (`type.equalsIgnoreCase("CONTROLLER") && accessory instanceof Controller`, and the same for `Cable` and `Memory`) although every accessory answers `getCategory()` (developer2-audit V-02). | Compare the requested type with `accessory.getCategory()` ignoring case. Accepted values and results stay the same. | Developer 2 |
| V-17 | `PromotionService.registerCategoryDiscount()` | Pure Fabrication / High Cohesion | Violation | Lines 35-36 validate inline that the target category is VIDEOGAME, CONSOLE or ACCESSORY and throw `IllegalArgumentException`, while the project has a `validation` package used by the other services (developer2-audit V-03). | Create `PromotionValidator` in the `validation` package with the same check, exception type and message, and call it from the service. | Developer 2 |
| V-18 | `ReturnService.registerReturn()` | High Cohesion (related: Controller, Pure Fabrication) | Violation | Lines 65-141 (77 lines) validate the sale and the 30-day window, run three counting loops, build the id, cancel warranties, restore stock and persist (developer2-audit V-04). | Extract the checks into a `ReturnValidator` in the `validation` package with the same exception type and messages, and leave `registerReturn` coordinating the steps. | Developer 2 |
| V-19 | `ReturnService.calculateMonthlySales()` | Information Expert | Violation | Lines 201-206 loop over `saleService.getAllSales()`, parse `LocalDate.parse(sale.getDate())` and add `sale.getTotal()`, so the returns service computes a sales total and interprets the date format of `Sale` (developer2-audit V-06). | Add a monthly total method to `SaleService` and make `calculateMonthlySales` delegate to it, keeping its signature. | Technical Leader adds the method in `SaleService`; Developer 2 delegates. Boundary finding. |
| V-20 | `findSaleById` in `ReturnService`, `ReturnRepository` and `WarrantyRepository` | Information Expert / Low Coupling | Violation | The same private method is copied in `ReturnService.java` line 143 (called at line 66), `ReturnRepository.java` line 170 (line 139) and `WarrantyRepository.java` line 153 (line 138); each compares `String.valueOf(sale.getId()).equals(saleId)` (developer2-audit V-07). | Add a lookup by id to `SaleService` and `SaleRepository` and make the three classes use it, deleting their private copies. | Technical Leader adds the lookups; Developer 2 removes the copies. Boundary finding. |
| V-21 | `AccessoryService`, `PromotionService`, `ReturnService` and `WarrantyService` | Indirection / Protected Variations | Violation | They declare concrete repository classes (`AccessoryService.java` line 30, `PromotionService.java` line 20, `ReturnService.java` line 21, `WarrantyService.java` line 23), while `PersonService` and `ProductService` use the interfaces `PersonRepository` and `ProductRepository` (developer2-audit V-09). | Extract the four interfaces, rename the current classes to `FileAccessoryRepository`, `FilePromotionRepository`, `FileReturnRepository` and `FileWarrantyRepository`, and make the services depend on the interfaces. Done after V-04. | Developer 2; Technical Leader updates `Main` (V-07). Boundary finding. |
| V-22 | `WarrantyService` | Low Coupling | Violation | Line 25 declares `private ProductService productService;`, line 34 receives it in the constructor and line 37 assigns it, and no method uses it (developer2-audit V-10). | Remove the field and the constructor parameter. | Developer 2; Technical Leader updates the call in `Main`. Boundary finding. |

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
- **Developer 2 modules, creation:** `PersonService`, `AccessoryService`, `PromotionService`, `ReturnService` and `WarrantyService` create the objects they register, and the repositories only rebuild stored records in `parseLine`.
- **`PromotionService.findBestPromotionFor` and `WarrantyService.cancelWarranties`:** they ask each promotion or warranty for its own discount or cost through polymorphic calls, without checking the type.
- **Persons module:** `PersonService` depends on the `PersonRepository` interface and on the static `PersonValidator`.
- **Requirement 6 wrapping:** `FilePersonRepository`, `FileProductRepository` and `SaleRepository` wrap `IOException` in `PersistenceException`.
- **`ReturnService.registerReturn` as Controller:** it coordinates the return use case through other services, so the pattern is correct; the excess of work inside it is V-18.
- **`instanceof` in `toLine` of the file repositories:** accepted as serialization that must stay in persistence, so it is not reported.

## 5. Main corrective actions and coordination

1. **Encapsulation of product state:** implement `Product.addStock(int quantity)` and `Product.reduceStock(int quantity)`. The Technical Leader handles `restoreStock()`; Developer 1 handles `updateStock()`. Both changes must agree on validation and stock invariants.

2. **Category and sale lookup APIs:** Developer 1 and the Technical Leader must agree on stable service/domain contracts before changing the UI or `CategoryDiscount`. Avoid introducing new dependencies from the UI into persistence or model internals.

3. **Sale calculations and returns:** add the required high-level subtotal/discount queries to `Sale`, then update `CategoryDiscount` and `Return` to use them. Because `Sale` and its consuming classes are assigned to different people in parts of the project, the Technical Leader coordinates the interface and commit order.

4. **Polymorphic inventory/warranty behavior:** remove concrete-type branching from `SaleService.registerSale()` by agreeing on product operations or dedicated handlers. Keep warranty and stock behavior unchanged from the user's perspective.

5. **Persistence boundaries:** remove `PersonService` and `ProductService` dependencies from `SaleRepository`; resolve IDs in an appropriate service or mapper layer.

6. **Deterministic return-policy testing:** separate date parsing from the return rule and make the evaluation date injectable while preserving existing menu behavior.

7. **Application initialization:** centralize object creation in a factory/composition root and remove fragile circular initialization dependencies without changing the startup flow seen by the user.
8. **Persistence interfaces:** V-04 is done before V-21, because the interfaces take the signatures that V-04 leaves. Developer 2 extracts the four interfaces and the Technical Leader creates the file implementations in `Main` (V-07).

9. **Sale lookup and monthly totals:** the Technical Leader adds the lookup by id (`SaleService` and `SaleRepository`) and the monthly total (`SaleService`); Developer 2 then removes the copies (V-20) and delegates (V-19). Suggested order: V-04, V-21, V-20, V-19.

10. **Stock and item-type contracts:** V-15 is done after `Product.addStock` and `Product.reduceStock` exist (V-06 and V-10). V-05 uses one shared hook agreed by the Technical Leader, Developer 1 and Developer 2.

## 6. Boundary findings

The following findings require coordination across assigned areas and should be recorded once in the final team audit:

- **V-01 / V-02:** UI changes depend on service-layer APIs. The Technical Leader owns the UI changes; the relevant service owner must implement or confirm the API.
- **V-06 / V-10:** both affect product stock mutation. The Technical Leader owns `ProductService.restoreStock()` and Developer 1 owns `ProductService.updateStock()` and the `Product` domain operation. They must use consistent validation and stock invariants.
- **V-09 / V-12:** both need `Sale` to expose subtotal/discount information. Developer 1 owns `CategoryDiscount` and `Return`; the Technical Leader owns `Sale`. Coordinate the method contract and sequence the commits.
- **V-13 / V-14:** the date-related refactor touches `Sale`, which is in the Technical Leader's overall module scope but the supplied Developer 1 report assigns `Sale.canBeReturned()` to Developer 1. Confirm the actual method ownership in `TEAM.md` and Git history before implementation, as required by the assignment.
- **V-05:** if specialized inventory or warranty handlers are introduced, assign their creation to the owner of the violation and coordinate any required service-interface changes.
- **V-04 (extended):** `SaleRepository` belongs to the Technical Leader, and `ReturnRepository` and `WarrantyRepository` to Developer 2. All three must use the same decoupling mechanism; the Technical Leader coordinates the order of the commits.
- **V-05 (extended):** the item-type tests are in `SaleService` (Technical Leader), `ReturnService` and `WarrantyService` (Developer 2). The warranty hook goes in `Product` and `Console` (Developer 1) and the stock contract in `ProductService` (Technical Leader and Developer 1) and `AccessoryService` (Developer 2).
- **V-15 / V-06 / V-10:** the same stock arithmetic is written in `AccessoryService`, `ProductService.restoreStock` and `ProductService.updateStock`. All three use the `Product` operations and must keep the same invariants.
- **V-19 / V-13:** if `Sale` changes the type of its date (V-13), `calculateMonthlySales` stops compiling. V-19 delegates the total to `SaleService` first, so it must be corrected before or together with V-13.
- **V-20:** the lookups are added by the Technical Leader and the copies removed by Developer 2.
- **V-21 / V-22:** they need `Main` (V-07) updated by the Technical Leader to create the file implementations and to call the new `WarrantyService` constructor.

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

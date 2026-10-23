# SOLID Audit Report - Developer 2

**Auditor:** Luis Manuel Corzo Castro (1066870055)
**Role:** Developer 2
**Date:** 2026-10-21
**Audited baseline:** `origin/develop` at commit `5f20ee1`. Every line number in this report refers to that commit.

## Scope

| Module (requirement) | Audited classes and methods |
| --- | --- |
| Persons (Taller 1) | `Person`, `Client`, `Seller`, `PersonRepository`, `FilePersonRepository`, `PersonService` |
| Accessories (Requirement 1) | `AccessoryRepository`, `FileAccessoryRepository`, `AccessoryService` |
| Promotions (Requirement 2) | `PromotionRepository`, `FilePromotionRepository`, `PromotionService`, including `findBestPromotionFor` |
| Returns (Requirement 3) | `ReturnRepository`, `FileReturnRepository`, `ReturnService`, including the monthly balance |
| Warranties (Requirement 4) | `WarrantyRepository`, `FileWarrantyRepository`, `WarrantyService`, including `cancelWarranties` |
| Exceptions and validators (Requirement 6) | Use of exceptions and validators in the services and repositories listed above |
| GRASP correction (Requirement 7) | `ReturnValidator`, `PromotionValidator`, `ReturnRecord`, `WarrantyRecord`, the repository interfaces and the `File...Repository` classes, and the methods `SaleService.calculateMonthlyTotal` and `SaleService.findSaleById`, added to the Technical Leader's class with his authorization |

The assignment names the class `Customer`; in this repository it is `Client`.

Not audited here, because other integrantes audit them: the exception classes and the `PersonValidator`, `ProductValidator` and `SaleValidator` classes (Developer 1), the rest of `SaleService`, `SaleRepository`, `Sale`, `GameZoneUI` and `Main` (Technical Leader), and `IStockService`, created by another integrante in the GRASP correction.

## Summary of findings

| ID | Principle | Location | Involves another integrante's code |
| --- | --- | --- | --- |
| V-01 | SRP | `ReturnService` | No |
| V-02 | SRP | `WarrantyService` | No |
| V-03 | OCP | `FilePersonRepository`, `FileAccessoryRepository`, `FilePromotionRepository`, `FileWarrantyRepository` and `WarrantyService.loadWarranties` | No |
| V-04 | OCP | `ReturnService.registerReturn` | Yes (`ProductService`, `Main`) |
| V-05 | ISP | `AccessoryService` and `WarrantyService` | Yes (`Main`) |
| V-06 | DIP | `ReturnService` | Yes (`SaleService`, `ProductService`, `Main`) |
| V-07 | DIP | `WarrantyService` | Yes (`SaleService`, `Main`) |

## Verdict per principle

| Principle | Verdict | Findings |
| --- | --- | --- |
| Single Responsibility (SRP) | violation | V-01, V-02 |
| Open/Closed (OCP) | violation | V-03, V-04 |
| Liskov Substitution (LSP) | correct | - |
| Interface Segregation (ISP) | violation | V-05 |
| Dependency Inversion (DIP) | violation | V-06, V-07 |

A finding that breaks more than one principle is registered once, under the principle that guides its correction; the related principle is named inside the finding.

## 1. Single Responsibility Principle (SRP)

**Verdict: violation** (V-01, V-02).

Where the principle is correctly applied:
- `PersonService` (class at line 20) only registers and queries persons; its checks are delegated to `PersonValidator` and its storage to `PersonRepository`.
- `AccessoryService` (class at line 18) only manages the accessory catalog, its compatibility queries and its stock. `PromotionService` (class at line 19) only registers and selects promotions; `findBestPromotionFor` is at line 67.
- `ReturnValidator` (line 14) has one reason to change, the return rules, and `PromotionValidator` (line 6) the category rule. Both were extracted from services by the GRASP correction (V-17 and V-18 of Requirement 7).
- `ReturnRecord` (line 11) and `WarrantyRecord` (line 10) only carry the stored values.
- `Person` (line 9), `Client` (line 8) and `Seller` (line 8) only hold the data of a person and of each role.
- The five `File...Repository` classes only read and write their own file.

### V-01 - ReturnService mixes the return workflow, the rebuilding of records and the monthly report

- **Principle:** SRP
- **Module:** Returns (Requirement 3), extended in A4 and A7 (Requirement 5) and in the GRASP correction V-04 (Requirement 7)
- **Evidence:** `ReturnService.java`. `registerReturn` (line 66) validates the sale (line 68), builds the identifier (line 80), cancels warranties (line 85), restores stock (lines 94 and 96) and persists (line 102). `loadReturns` (line 114) rebuilds every `Return` from its `ReturnRecord`, resolving the sale, the products and the accessories (line 133). `calculateMonthlyReturns` (line 200) and `generateMonthlyBalance` (line 219) compute the financial report.
- **Explanation:** the class has three reasons to change: the return policy and workflow, the representation of a stored return (what `ReturnRecord` holds and how its items are resolved), and the rules of the monthly report. Rebuilding the objects was moved into the service by the GRASP correction V-04, which created this extra responsibility.
- **Consequence:** adding a column to the stored return, or changing how its items are resolved, forces edits in the class that also holds the refund workflow, and the balance cannot be reused or checked without the whole service.
- **Proposed solution:** extract the rebuilding into a `ReturnRecordMapper` and the report into a `MonthlyBalanceCalculator`, both in the service package. `ReturnService` keeps its public methods and delegates to them, so its callers do not change.
- **Other integrante's code:** No.

### V-02 - WarrantyService mixes the warranty rules, the rebuilding of records and the historical synchronization

- **Principle:** SRP
- **Module:** Warranties (Requirement 4), extended in A7 (Requirement 5) and in the GRASP correction V-04 (Requirement 7)
- **Evidence:** `WarrantyService.java`. `assignBasicWarranty` (line 102), `assignExtendedWarranty` (line 124) and `cancelWarranties` (line 262) apply the warranty rules. `loadWarranties` (line 63) rebuilds each warranty from its `WarrantyRecord`, creating the subtype from the stored discriminator (lines 73 and 75). `syncPastConsoleWarranties` (line 219) scans every historical sale to generate the missing warranties.
- **Explanation:** the class has three reasons to change: the warranty rules, the stored representation, and the migration of historical sales. Rebuilding was moved here by the GRASP correction V-04.
- **Consequence:** a change in the warranty record, or in the way old sales are backfilled, edits the class that also assigns and cancels warranties.
- **Proposed solution:** extract the rebuilding into a `WarrantyRecordMapper` and the historical synchronization into a `WarrantyBackfill`, both in the service package. `WarrantyService` keeps its public methods, including `syncPastConsoleWarranties`, and delegates to them.
- **Other integrante's code:** No.

## 2. Open/Closed Principle (OCP)

**Verdict: violation** (V-03, V-04).

Where the principle is correctly applied:
- `PromotionService.findBestPromotionFor` asks each promotion for its discount (`promotion.calculateDiscount(sale)`, line 72) without testing its type.
- `AccessoryService.listAccessoriesByType` compares the requested type with `accessory.getCategory()` (line 132), so a new accessory subtype needs no change there.
- `ReturnService.registerReturn` (line 84) and `WarrantyService.syncPastConsoleWarranties` (line 231) ask each product `hasWarrantyCoverage()` instead of testing for `Console`.

Considered and not reported: the three `register...` methods of `AccessoryService` and of `PromotionService` take different parameters per type, and adding a type adds a method without changing the existing ones; `PromotionValidator.validateTargetCategory` closes the list of categories to VIDEOGAME, CONSOLE and ACCESSORY, which is a rule fixed by Requirement 2 (A1) and not an extension point; `PersonService.getAllClients` and `getAllSellers` are explicit queries by role.

### V-03 - The file repositories choose the stored format with a chain of type tests

- **Principle:** OCP
- **Module:** Persons (Taller 1), Accessories (Requirement 1), Promotions (Requirement 2), Warranties (Requirement 4) and the GRASP correction V-04 (Requirement 7)
- **Evidence:** `FilePersonRepository.java` tests `person instanceof Client` (line 101) and `person instanceof Seller` (line 108), falls back to `return "";` (line 117) and rebuilds from the stored text in `parseLine` (line 129). `FileAccessoryRepository.java` tests the three subtypes (lines 110, 119 and 129), falls back to `return "";` (line 140) and reads the discriminator at line 160. `FilePromotionRepository.java` tests the three promotion subtypes (lines 105, 114 and 124) and falls back to `return "";` (line 135). `FileWarrantyRepository.java` line 102 stores every warranty that is not extended as "BASIC". `WarrantyService.loadWarranties` rebuilds the subtype from the stored text (lines 72 and 74).
- **Explanation:** a new subtype (a person role, an accessory, a promotion or a warranty) cannot be stored without editing the repository where it is written and where it is read and, for warranties, also the code that rebuilds it.
- **Consequence:** a subtype that is not listed is not rejected: a person, an accessory or a promotion of an unknown subtype is written as an empty line and lost on the next load, and a new kind of warranty would be stored as "BASIC" and reloaded as a basic warranty.
- **Proposed solution:** one codec per subtype, in the persistence package, registered in a map of each repository (by class to write, by discriminator to read). A new subtype adds a codec and its registration and leaves the repository code unchanged. The dispatch of `WarrantyService.loadWarranties` moves with the mapper of V-02. The stored lines and the data files do not change.
- **Other integrante's code:** No.
- **Note:** the GRASP audit of Requirement 7 accepted the type tests because file logic cannot live in the model; OCP shows that the repository can still be closed for modification.

### V-04 - ReturnService chooses the stock service with instanceof

- **Principle:** OCP (related: DIP)
- **Module:** Returns (Requirement 3), adjusted in A4 (Requirement 5); left partially corrected by the GRASP correction V-05 (Requirement 7)
- **Evidence:** `ReturnService.java` line 93 tests `product instanceof Accessory` and calls `accessoryService.restoreStock(...)` (line 94); the other branch calls `productService.restoreStock(...)` (line 96). `IStockService.java` declares the stock contract at line 7, but `ReturnService` does not reference it and `AccessoryService` does not implement it (class at line 18).
- **Explanation:** a new kind of item stored by a different service requires editing `registerReturn`. The common stock contract already exists and is not used here.
- **Consequence:** the return workflow, which is stable, changes whenever the inventory of a new kind of item is added.
- **Proposed solution:** `ReturnService` restores stock through the `IStockService` contract, with an implementation that resolves the item and delegates to the right service, so the type test disappears.
- **Other integrante's code:** Yes. The implementation that resolves the item belongs to the owners of `ProductService` and `Main`; making `AccessoryService` implement the contract is my part.

## 3. Liskov Substitution Principle (LSP)

**Verdict: correct.**

- `Client` (class at line 8) and `Seller` (line 8) extend `Person` (line 9) only by adding attributes and calling `super(...)` (lines 21 and 23); they override nothing, so they can be used wherever a `Person` is expected, as `PersonService` does when it keeps both kinds in one list (line 23).
- Each `File...Repository` implements its interface, marks its operations with `@Override` and keeps the documented behavior of returning an empty result when the file does not exist:

| Implementation | Declared at line | Empty result when the file does not exist, at line |
| --- | --- | --- |
| `FilePersonRepository` | 22 | 50 |
| `FileAccessoryRepository` | 23 | 53 |
| `FilePromotionRepository` | 25 | 59 |
| `FileReturnRepository` | 25 | 58 |
| `FileWarrantyRepository` | 22 | 55 |

Each interface has a single implementation, so substitution between implementations is not exercised yet.

## 4. Interface Segregation Principle (ISP)

**Verdict: violation** (V-05).

Where the principle is correctly applied: each repository interface is small and its client uses all its operations.

| Interface (declared at line) | Client | Operations used (first use at line) |
| --- | --- | --- |
| `PersonRepository` (12) | `PersonService` | `loadPersons` (33), `savePersons` (52) |
| `AccessoryRepository` (11) | `AccessoryService` | `loadAll` (32), `saveAll` (74) |
| `PromotionRepository` (11) | `PromotionService` | `loadAll` (29), `saveAll` (32) |
| `ReturnRepository` (12) | `ReturnService` | `loadAll` (116), `saveAll` (102) |
| `WarrantyRepository` (12) | `WarrantyService` | `loadAll` (65), `saveAll` (109) |

### V-05 - Constructor dependencies that the class never uses

- **Principle:** ISP (related: Low Coupling)
- **Module:** Accessories (Requirement 1) and Warranties (Requirement 4)
- **Evidence:** `AccessoryService.java` declares `ProductRepository productRepository` (line 21) and assigns it (line 44); no operation is ever called on it. `WarrantyService.java` declares `SaleRepository saleRepository` (line 26) and assigns it (line 48); no operation is ever called on it, because the sale lookup goes through `saleService`.
- **Explanation:** each class depends on a whole contract (`ProductRepository`, `SaleRepository`) and uses none of its operations.
- **Consequence:** the code that builds these services has to create and pass objects that they never use, and a change in those contracts reaches classes that do not need them.
- **Proposed solution:** remove the field, the assignment and the constructor parameter in each class, keeping the one-argument constructors.
- **Other integrante's code:** Yes. `Main` (Technical Leader) builds both services and must be updated if it still passes those arguments.
- **Note:** this is the same smell that the GRASP correction V-22 removed from `WarrantyService` (an unused `ProductService`); it came back with an unused `SaleRepository` after the sale lookup changed.

## 5. Dependency Inversion Principle (DIP)

**Verdict: violation** (V-06, V-07).

Where the principle is correctly applied: each service depends on the interface of its repository, after the GRASP correction V-21 of Requirement 7: `PersonService` (line 22), `AccessoryService` (line 20), `PromotionService` (line 21), `ReturnService` (line 22) and `WarrantyService` (line 25). The validators are stateless static classes that hold rules and no collaborators, so they are not treated as dependencies on implementations.

### V-06 - ReturnService depends on four concrete services

- **Principle:** DIP (related: ISP)
- **Module:** Returns (Requirement 3), adjusted in A4 and A7 (Requirement 5)
- **Evidence:** `ReturnService.java` declares `SaleService` (line 23), `ProductService` (line 24), `AccessoryService` (line 25) and `WarrantyService` (line 26), all concrete classes, and uses a few operations of each: `findSaleById` (lines 67, 117) and `calculateMonthlyTotal` (line 189) of `SaleService`, `restoreStock` of `ProductService` and of `AccessoryService` (lines 96 and 94), and `cancelWarranties` of `WarrantyService` (line 85).
- **Explanation:** the return workflow, a high-level policy, depends on whole implementations when it needs two or three operations from each.
- **Consequence:** it cannot be checked or reused without building the whole service graph, and a change in the signature of any of those services reaches `ReturnService`.
- **Proposed solution:** narrow interfaces owned by the consumer: one for the sale lookup and the monthly total (`SaleLookup`), one for cancelling warranties, and the `IStockService` contract of V-04 for restoring stock. The services implement them and `ReturnService` receives the interfaces.
- **Other integrante's code:** Yes: `SaleService`, `ProductService` and `Main`.

### V-07 - WarrantyService and SaleService depend on each other as concrete classes

- **Principle:** DIP
- **Module:** Warranties (Requirement 4), A2 (Requirement 5) and the change of the sale lookup made after the GRASP correction
- **Evidence:** `WarrantyService.java` declares the concrete `SaleService` (line 27), receives it through `setSaleService` (line 51), and uses it to rebuild warranties (line 66) and to scan the sales (line 227). `SaleService.java` declares `WarrantyService` (line 29). The setter was added in commit `aa019bd` by Milton Andrés Maldonado Salcedo.
- **Explanation:** two concrete high-level classes depend on each other, and the cycle is closed with a setter. Until the setter is called, `loadWarranties` skips every stored record (line 66) and `syncPastConsoleWarranties` returns without doing anything (line 220).
- **Consequence:** the order of the wiring decides whether the warranties exist; a missing call silently hides them, and a later save would write the file without them.
- **Proposed solution:** `WarrantyService` receives, at construction, a narrow `SaleLookup` abstraction implemented by a class that does not depend on `WarrantyService`, so the setter and the cycle disappear. It is the same abstraction proposed in V-06.
- **Other integrante's code:** Yes: `SaleService` and `Main` (Technical Leader).

## Findings involving other integrantes' code

Registered here so that the Technical Leader can unify them in the consolidated audit and agree the order of the corrections.

| ID | Other integrante | What the correction needs |
| --- | --- | --- |
| V-04 | Owner of `ProductService`, Technical Leader | An implementation of `IStockService` that resolves the item and delegates to the right service, and its construction in `Main`; making `AccessoryService` implement the contract is my part |
| V-05 | Technical Leader | `Main` updated if it still passes the unused arguments |
| V-06 | Technical Leader | `SaleService` implementing the new sale lookup interface, `ProductService` taking part in the stock contract, and `Main` |
| V-07 | Technical Leader | The `SaleLookup` abstraction without a cycle, `SaleService`, and `Main` replacing the setter |

Suggested order: the abstractions that belong to other integrantes first (V-04 and V-06), then my correction of the services that use them (V-04, V-06 and V-07), then the independent ones (V-01, V-02, V-03 and V-05).

## Observations for the consolidation

- The report of Developer 1 (`V-DEV1-03`, `SaleValidator`) and the report of the Technical Leader (`Sale`, DIP) propose replacing `Client` and `Seller` by `Person`. The person hierarchy audited here needs the role-specific data: the seller is found by `getEmployeeCode()` (`PersonService.java` line 127) and only `Seller` has it (`Seller.java` line 33). I recommend that the Leader reviews whether those are violations before assigning a correction in my hierarchy.
- The report of Developer 1 uses the identifier `V-DEV1-03` for two different findings (`Return` and `SaleValidator`).

## Out-of-scope functional observations

Defects found while auditing that are not corrected in this requirement because fixing them would change what the user observes. The defects already registered in the GRASP audit (Requirement 7) are not repeated.

- **F-01 - A corrupted line is handled differently by each repository.** `FilePromotionRepository.parseLine` catches every exception and skips the line (line 169). `FileReturnRepository` (line 117), `FileAccessoryRepository` (line 155), `FileWarrantyRepository` (line 118) and `FilePersonRepository` (line 130) parse without protection and only catch `IOException`, so a malformed line raises a runtime exception while the file is being loaded, for example when `AccessoryService` or `PersonService` is created.
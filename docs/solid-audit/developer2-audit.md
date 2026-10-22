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
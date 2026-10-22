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
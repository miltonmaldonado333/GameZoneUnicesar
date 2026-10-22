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
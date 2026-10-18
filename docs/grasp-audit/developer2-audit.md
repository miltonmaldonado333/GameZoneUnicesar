# GRASP Audit Report - Developer 2

**Auditor:** Luis Manuel Corzo Castro - 1066870055
**Role:** Developer 2
**Date:** 2026-10-18
**Audited state:** `origin/develop` at commit `336790c`

All evidence was read from `origin/develop`, not from the working tree of the audit branch.

---

## 1. Scope

The classes and methods below are audited by module and requirement, following the responsibility table of the requirement. The table calls `Customer` the class that this project names `Client`.

| Module (requirement) | Audited classes and methods |
| :--- | :--- |
| Persons (Taller 1) | `Person`, `Client`, `Seller`, `PersonRepository`, `FilePersonRepository`, `PersonService` |
| Accessories (Requirement 1) | `AccessoryRepository`, `AccessoryService` |
| Promotions (Requirement 2) | `PromotionRepository`, `PromotionService`, including best-promotion selection |
| Returns (Requirement 3, A4, A6) | `ReturnRepository`, `ReturnService`, including the monthly balance |
| Warranties (Requirement 4, A2, A7) | `WarrantyRepository`, `WarrantyService`, including `cancelWarranties` |
| Exceptions and validators (Requirement 6) | Use of `PersistenceException` in `FilePersonRepository`, `FileProductRepository` and `SaleRepository`, and use of validators in `PersonService`, `ProductService` and `SaleService` (only the lines I changed) |

Problems whose origin is in code owned by another team member are not audited here; they are reported to the Technical Leader for the consolidation.

---

## 2. Module audit

### 2.1 Persons (Taller 1)

| Pattern | Verdict | Evidence |
| :--- | :---: | :--- |
| Information Expert | Correct | `Person` keeps `name`, `identification` and `phone`; `Client` keeps `email`; `Seller` keeps `employeeCode` and `workShift`. `PersonService` holds the `persons` list, so `findClientById` and `findSellerByCode` search there and compare through `getIdentification()` and `getEmployeeCode()`. |
| Creator | Correct | `PersonService.registerClient` and `registerSeller` call `new Client(...)` and `new Seller(...)`: the service holds the list and has the data to initialise them. `FilePersonRepository.parseLine` also creates them, only to rebuild stored records. |
| Controller | Not applicable | No class in this module receives system events. `GameZoneUI` (Technical Leader) does, and `PersonService` is called from it. |
| Low Coupling | Correct | `PersonService` depends on the `PersonRepository` interface, on `PersonValidator` and on model classes. It does not know `FilePersonRepository`. |
| High Cohesion | Correct | `Person`, `Client` and `Seller` only hold person data, `PersonService` only applies person rules, and `FilePersonRepository` only reads and writes the file. |
| Polymorphism | Correct, justified | Nothing in `Person` varies by subtype in the domain, so it declares no abstract method. The only branching by type is in `FilePersonRepository.toLine` (`instanceof Client`, `instanceof Seller`) and `parseLine` (discriminator `CLIENT` or `SELLER`). It stays in persistence on purpose: moving it into the subclasses would put file-format logic in model classes, which the project rules forbid. `getAllClients` and `getAllSellers` filter by type but do not vary behaviour. |
| Pure Fabrication | Correct | `FilePersonRepository` and `PersonValidator` exist only to carry persistence and validation, so the domain classes stay free of them. |
| Indirection | Correct | `PersonService` talks to the `PersonRepository` interface, implemented by `FilePersonRepository`. |
| Protected Variations | Correct | Changing the storage affects only a new `PersonRepository` implementation and its construction in `Main`. |

**Violations in this module:** none.

**Observation (not a violation):** `PersonService.registerSeller` only checks uniqueness, because `PersonValidator.validatePersonData` requires an email and sellers have none.
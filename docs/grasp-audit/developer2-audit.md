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

### 2.2 Accessories (Requirement 1, adjustment A4)

Audited: `AccessoryRepository` and `AccessoryService`, including `restoreStock`, which adjustment A4 added.

| Pattern | Verdict | Evidence |
| :--- | :---: | :--- |
| Information Expert | Violation (V-01) | `AccessoryService.updateStock` (line 190) runs `accessory.setStock(accessory.getStock() - quantity)` and `restoreStock` (line 207) runs `accessory.setStock(accessory.getStock() + quantity)`: the service reads the stock, computes the new value and writes it back, although the stock belongs to `Product`. Correct elsewhere: `findAccessoriesCompatibleWith` asks each accessory for `getCompatibleConsoleIds()`, and `findById` searches the list that the service itself holds. |
| Creator | Correct | `registerController`, `registerCable` and `registerMemory` call `new Controller(...)`, `new Cable(...)` and `new Memory(...)`: the service holds the `accessories` list and has the data to initialise them. `AccessoryRepository.parseLine` also creates them, only to rebuild stored records. |
| Controller | Not applicable | `AccessoryService` does not receive system events: the console menu (`GameZoneUI`, Technical Leader) does, and other services call this one. |
| Low Coupling | Correct | `AccessoryRepository` depends only on the model classes. `AccessoryService` depends on its repository and on the model, and on no other service. |
| High Cohesion | Correct | The service only registers, lists, finds and changes the stock of accessories, and the repository only reads and writes `accessories.csv`. |
| Polymorphism | Violation (V-02) | `AccessoryService.listAccessoriesByType` (lines 116, 118 and 120) selects by concrete class, for example `type.equalsIgnoreCase("CONTROLLER") && accessory instanceof Controller`, and the same for `Cable` and `Memory`, although every subclass already answers `getCategory()`. The `instanceof` in `AccessoryRepository.toLine` and the discriminator in `parseLine` choose the stored format of each subtype, which has to stay in persistence (see 2.1), so they are not reported. |
| Pure Fabrication | Correct | `AccessoryRepository` exists only to carry file access, so `Accessory` and its subclasses hold none. |
| Indirection | Violation (V-09) | `AccessoryService` line 30 declares `private AccessoryRepository accessoryRepository;`, a concrete class that reads and writes the file. `PersonService` depends on the interface `PersonRepository` instead. |
| Protected Variations | Violation (V-09) | Changing where accessories are stored would force editing `AccessoryService`, because no interface sits between them. It is the same finding as Indirection. |

**Violations in this module:** V-01, V-02, V-09.

### 2.3 Promotions (Requirement 2)

Audited: `PromotionRepository` and `PromotionService`, including the selection of the best promotion.

| Pattern | Verdict | Evidence |
| :--- | :---: | :--- |
| Information Expert | Correct | `PromotionService.findBestPromotionFor` asks every active promotion for its own discount through `promotion.calculateDiscount(sale)` and keeps the largest, and `listActivePromotions` asks each one `promotion.isActive(today)`. The service never computes a discount itself. |
| Creator | Correct | `registerPercentageDiscount`, `registerCategoryDiscount` and `registerBulkPurchaseDiscount` create the promotion they register and persist it. `PromotionRepository.parseLine` rebuilds promotions from stored records. |
| Controller | Not applicable | `PromotionService` does not receive system events: the console menu (`GameZoneUI`, Technical Leader) does. |
| Low Coupling | Correct | `PromotionService` depends on its repository and on the model (`Promotion`, `Sale` and the three promotion types), and `PromotionRepository` only on the model. |
| High Cohesion | Correct | The service only registers, lists and selects promotions, and the repository only reads and writes `promotions.csv`. |
| Polymorphism | Correct | The best-promotion selection never asks for the type of a promotion. `PromotionRepository.toLine` uses `instanceof` to pick the columns of each subtype, which stays in persistence (see 2.1) and is not reported. |
| Pure Fabrication | Violation (V-03) | `PromotionService.registerCategoryDiscount` (lines 35-36) checks inline that `targetCategory` is VIDEOGAME, CONSOLE or ACCESSORY and throws `IllegalArgumentException`, although the project has a `validation` package and `PersonService` delegates its checks to `PersonValidator`. |
| Indirection | Violation (V-09) | `PromotionService` line 20 declares `private final PromotionRepository promotionRepository;`, a concrete class that reads and writes the file. |
| Protected Variations | Violation (V-09) | Same finding as Indirection: changing where promotions are stored would force editing `PromotionService`. |

**Violations in this module:** V-03, V-09.

### 2.4 Returns (Requirement 3, adjustments A4 and A6)

Audited: `ReturnRepository` and `ReturnService`, including `calculateMonthlySales`, `calculateMonthlyReturns` and `generateMonthlyBalance`.

| Pattern | Verdict | Evidence |
| :--- | :---: | :--- |
| Information Expert | Violation (V-06, V-07) | V-06: `ReturnService.calculateMonthlySales` (lines 201-206) loops over `saleService.getAllSales()`, parses `LocalDate.parse(sale.getDate())` (line 204) and adds `sale.getTotal()` (line 206), so the returns service computes a sales total and interprets the date format of `Sale`. V-07: `findSaleById` is copied in `ReturnService` (line 143, called at line 66) and in `ReturnRepository` (line 170, called at line 139). Correct elsewhere: `Return` computes its own refund and `calculateMonthlyReturns` only adds `getRefundAmount()`. |
| Creator | Correct | `registerReturn` creates the `Return` it records and persists it. `ReturnRepository.parseLine` rebuilds stored returns. |
| Controller | Correct | `registerReturn` is the use-case operation of a return: it delegates to `WarrantyService`, to `AccessoryService` or `ProductService` and to `ReturnRepository`. The event itself enters through `GameZoneUI` (Technical Leader). The excess of work that remains inside it is registered under High Cohesion. |
| Low Coupling | Violation (V-08) | `ReturnRepository` imports `AccessoryService`, `ProductService` and `SaleService` (lines 16-18) and uses them in `parseLine` and `resolveItem` to rebuild sales and returned items, so persistence depends on the service layer above it. |
| High Cohesion | Violation (V-04) | `ReturnService.registerReturn` (lines 65-141, 77 lines) checks the sale and the 30-day window (66-73), runs three counting loops (84-105), builds the id (116), cancels warranties (117-123), restores stock (127-134) and persists (136-138). |
| Polymorphism | Violation (V-05) | `registerReturn` decides with `product instanceof Console` (line 120) which items get their warranties cancelled and with `product instanceof Accessory` (line 129) which service restores the stock. |
| Pure Fabrication | Correct | `ReturnRepository` exists only to carry file access. The validation written inline in `registerReturn` is handled inside V-04. |
| Indirection | Violation (V-09) | `ReturnService` line 21 declares `private ReturnRepository returnRepository;`, a concrete class that reads and writes the file. |
| Protected Variations | Violation (V-09) | Same finding as Indirection: changing where returns are stored would force editing `ReturnService`. |

**Violations in this module:** V-04, V-05, V-06, V-07, V-08, V-09.

### 2.5 Warranties (Requirement 4, adjustments A2 and A7)

Audited: `WarrantyRepository` and `WarrantyService`, including `cancelWarranties` and `syncPastConsoleWarranties`.

| Pattern | Verdict | Evidence |
| :--- | :---: | :--- |
| Information Expert | Violation (V-07) | `WarrantyRepository.findSaleById` (line 153, called at line 138) repeats the sale lookup that `ReturnService` and `ReturnRepository` also copy. Correct elsewhere: `cancelWarranties` asks each warranty for `getAdditionalCost()` and `listActiveWarranties` asks `warranty.isActive(today)`. |
| Creator | Correct | `assignBasicWarranty`, `assignExtendedWarranty` and `syncPastConsoleWarranties` create the `BasicWarranty` and `ExtendedWarranty` objects they store. `WarrantyRepository.parseLine` rebuilds them from stored records. |
| Controller | Not applicable | `WarrantyService` does not receive system events: the console menu (`GameZoneUI`, Technical Leader) does, and other services call it, such as `ReturnService.registerReturn`. |
| Low Coupling | Violation (V-08, V-10) | V-08: `WarrantyRepository` imports `ProductService` (line 18) and uses it in `parseLine` through `productService.findProductById(productId)`. V-10: `WarrantyService` declares `productService` (line 25), receives it in the constructor (line 34) and assigns it (line 37), and no method uses it. After adjustment A2 sales are resolved through `SaleRepository` and not through `SaleService`, which is correct. |
| High Cohesion | Correct | The service only creates, cancels and queries warranties, and the repository only reads and writes `warranties.csv`. |
| Polymorphism | Violation (V-05) | `WarrantyService.syncPastConsoleWarranties` (line 174) uses `item instanceof com.gamezone.model.Console` to decide which sold items receive a basic warranty. Correct elsewhere: `cancelWarranties` never asks whether a warranty is basic or extended. `WarrantyRepository.toLine` uses `instanceof ExtendedWarranty` to choose the stored discriminator, which stays in persistence (see 2.1) and is not reported. |
| Pure Fabrication | Correct | `WarrantyRepository` exists only to carry file access. |
| Indirection | Violation (V-09) | `WarrantyService` line 23 declares `private WarrantyRepository warrantyRepository;`, a concrete class that reads and writes the file. |
| Protected Variations | Violation (V-09) | Same finding as Indirection: changing where warranties are stored would force editing `WarrantyService`. |

**Violations in this module:** V-05, V-07, V-08, V-09, V-10.

### 2.6 Exceptions and validators (Requirement 6)

Audited: only the lines changed in Requirement 6. They are the wrapping of `IOException` in `PersistenceException` in `FilePersonRepository`, `FileProductRepository` and `SaleRepository`, and the calls to `PersonValidator`, `ProductValidator` and `SaleValidator` from `PersonService`, `ProductService` and `SaleService`. The validator and exception classes are audited by Developer 1, and the rest of `ProductService`, `SaleService` and `SaleRepository` by their owners.

| Pattern | Verdict | Evidence |
| :--- | :---: | :--- |
| Pure Fabrication | Correct | The checks live in stateless classes of the `validation` package. `PersonService` calls `PersonValidator.validatePersonData` (line 47) and `validateUniquePerson` (lines 48, 67), `ProductService` calls `ProductValidator.validateProductData` (lines 61, 85) and `validateProductExists` (line 130), and `SaleService` calls `SaleValidator.validateSaleData` (line 117) and `ProductValidator.validateStockAvailability` (line 121). The inline check in `PromotionService` is V-03, and the inline duplicate-id check in `ProductService.registerVideoGame` and `registerConsole` is reported in developer1-audit. |
| Protected Variations | Correct | `throw new PersistenceException(...)` wraps the `IOException` in `FilePersonRepository` (lines 67, 89), `FileProductRepository` (lines 76, 107) and `SaleRepository` (lines 66, 119), so the callers depend on the project exception and not on the I/O type of the storage. The repositories of the other modules do not do this, and changing it would change what the user sees (F-03). |
| Low Coupling | Correct | The services reach the validators only through static calls and do not know how each check is done. |
| Other patterns | Not applicable | These lines contain no object creation, event handling or type-based branching beyond what sections 2.1 to 2.5 already cover. |

**Violations in these lines:** none.
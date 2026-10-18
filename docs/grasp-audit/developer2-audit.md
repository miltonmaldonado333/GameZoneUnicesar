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

## 3. Verdict per pattern

Verdict of this report for each GRASP pattern over the code audited here. A finding that breaks more than one pattern is registered once, under the pattern that guides its correction, and the related patterns are named in the finding.

| Pattern | Verdict | Findings and where it is correct |
| :--- | :---: | :--- |
| Information Expert | Violation | V-01, V-06, V-07. Correct in `PromotionService.findBestPromotionFor`, in `WarrantyService.cancelWarranties` and in the persons module. |
| Creator | Correct | In the five modules the services create what they register and the repositories only rebuild stored records. |
| Controller | Correct | `ReturnService.registerReturn` coordinates the return use case through other services and its repository. In the other classes it does not apply, because the events enter through `GameZoneUI` (Technical Leader). The excess of work inside `registerReturn` is V-04. |
| Low Coupling | Violation | V-08, V-10. Correct in the persons module and in the use of validators. |
| High Cohesion | Violation | V-04. Correct in the other services and repositories audited. |
| Polymorphism | Violation | V-02, V-05. Correct in `PromotionService.findBestPromotionFor` and in `WarrantyService.cancelWarranties`. The `instanceof` that choose the stored format in the repositories are accepted (see 2.1). |
| Pure Fabrication | Violation | V-03. Correct in the repositories and in the use of validators. |
| Indirection | Violation | V-09. Correct in the persons module. |
| Protected Variations | Violation | V-09, the same finding as Indirection. Correct in the persons module and in the wrapping of `IOException`. |

## 4. Violations

Identifiers belong to this report. The Technical Leader assigns the definitive ones in the consolidated audit.

#### V-01 - Stock arithmetic done outside the entity

- **Pattern:** Information Expert
- **Module:** Accessories (Requirement 1); `restoreStock` was added in Requirement 5 (A4)
- **Evidence:** `AccessoryService.java` line 190, in `updateStock`: `accessory.setStock(accessory.getStock() - quantity);` and line 207, in `restoreStock`: `accessory.setStock(accessory.getStock() + quantity);`.
- **Explanation:** The service reads the stock, computes the new value and writes it back, although the stock belongs to `Product`. The same pattern is reported for `ProductService.updateStock` (developer1-audit) and for `ProductService.restoreStock` (leader-audit).
- **Consequence:** Any stock rule, such as never going below zero, must be repeated in each service that changes stock, and two services already duplicate the arithmetic.
- **Proposed solution:** Call the `addStock` and `reduceStock` operations that `Product` receives for the `ProductService` findings. `updateStock` keeps its boolean result and `restoreStock` keeps its exception. Pattern applied: Information Expert.
- **Other integrante's code:** Yes. The new `Product` operations belong to the owners of the `ProductService` findings (Developer 1 and Technical Leader).

#### V-02 - Accessory type chosen with instanceof

- **Pattern:** Polymorphism
- **Module:** Accessories (Requirement 1)
- **Evidence:** `AccessoryService.listAccessoriesByType`, lines 116, 118 and 120: `type.equalsIgnoreCase("CONTROLLER") && accessory instanceof Controller`, and the same test with `"CABLE"` and `Cable`, and with `"MEMORY"` and `Memory`.
- **Explanation:** The method decides by concrete class although each subclass already returns its own name through `getCategory()`: `"controller"`, `"Cable"` and `"Memory"`.
- **Consequence:** A new accessory type would also need an edit here, besides its class and its line in the repository.
- **Proposed solution:** Compare the requested type with `accessory.getCategory()` ignoring case. The accepted values and the lists returned stay the same, so the menu behaves as before. Pattern applied: Polymorphism.
- **Other integrante's code:** No. `getCategory()` already exists in the accessory classes (Developer 1) and is not modified.

#### V-03 - Validation rule written inline in a service

- **Pattern:** Pure Fabrication (related: High Cohesion)
- **Module:** Promotions (Requirement 2); `ACCESSORY` was added in A1
- **Evidence:** `PromotionService.registerCategoryDiscount`, lines 35-36: the condition `!"VIDEOGAME".equalsIgnoreCase(targetCategory) && !"CONSOLE".equalsIgnoreCase(targetCategory) && !"ACCESSORY".equalsIgnoreCase(targetCategory)` followed by `throw new IllegalArgumentException(...)` with the Spanish message about the target category.
- **Explanation:** The rule is written inside the service although the project has a `validation` package, and `PersonService`, `ProductService` and `SaleService` already delegate their checks to it.
- **Consequence:** The rule cannot be reused or checked apart from the service, and the registration method mixes validation with creating and saving.
- **Proposed solution:** Create a `PromotionValidator` in the `validation` package with the same check, the same exception type (`IllegalArgumentException`) and the same message, and call it from the service. Pattern applied: Pure Fabrication.
- **Other integrante's code:** No. The new class is created by the owner of the finding.

#### V-04 - One method validates, counts, coordinates and persists

- **Pattern:** High Cohesion (related: Controller and Pure Fabrication)
- **Module:** Returns (Requirement 3), extended in A4 and A7
- **Evidence:** `ReturnService.registerReturn`, lines 65-141 (77 lines). It checks the sale and the 30-day window (lines 66-73), runs three counting loops for `originalQuantity`, `alreadyReturnedQuantity` and `requestedInCurrentBatch` (lines 84-105), builds the id with `"RET" + String.format("%04d", returnRepository.loadAll().size() + 1)` (line 116), cancels warranties (lines 117-123), restores stock (lines 127-134) and persists (lines 136-138).
- **Explanation:** One method mixes validation, quantity counting, coordination of two other modules and persistence. The validation rules sit inline although the project has a validation layer for that purpose.
- **Consequence:** Any new return rule means editing this method, and the rules cannot be checked without the file repository and the other services.
- **Proposed solution:** Extract the checks of the sale, the 30-day window, the ownership of each product and the available quantity into a `ReturnValidator` in the `validation` package. It receives the sale and the previous returns as parameters, so it has no file access, and it throws the same exception type with the same messages. `registerReturn` is left coordinating the steps. Pattern applied: High Cohesion, guided by Pure Fabrication.
- **Other integrante's code:** No. The new class is created by the owner of the finding.

#### V-05 - Item type chosen with instanceof

- **Pattern:** Polymorphism
- **Module:** Returns (Requirement 3, A4 and A7) and Warranties (Requirement 4)
- **Evidence:** `ReturnService.java` line 120, `if (product instanceof Console)`, decides which returned items get their warranties cancelled, and line 129, `if (product instanceof Accessory)`, decides whether `AccessoryService` or `ProductService` restores the stock. `WarrantyService.syncPastConsoleWarranties`, line 174: `item instanceof com.gamezone.model.Console`. The same tests exist in `SaleService.registerSale`, lines 153 and 179 (leader-audit).
- **Explanation:** Behaviour that varies by item type is selected with type tests inside the methods instead of by the items or by an abstraction.
- **Consequence:** A new kind of item with its own stock service or warranty rule means editing `ReturnService`, `WarrantyService` and `SaleService`.
- **Proposed solution:** `Product` gets a hook that tells whether the item carries a warranty (false by default, true in `Console`), and `ProductService` and `AccessoryService` fulfil one common stock-restoring interface that `ReturnService` calls without testing the class of the item. The exception of `ProductService.restoreStock` for an unknown product is kept. The same hook serves `SaleService`. Pattern applied: Polymorphism.
- **Other integrante's code:** Yes. `Product` and `Console` (Developer 1), and `SaleService` and `ProductService` (Technical Leader).

#### V-06 - Monthly sales total computed from the internals of Sale

- **Pattern:** Information Expert
- **Module:** Returns (Requirement 3); monthly balance changed in Requirement 5 (A6)
- **Evidence:** `ReturnService.calculateMonthlySales`, lines 201-206: it loops over `saleService.getAllSales()`, parses `LocalDate.parse(sale.getDate())` (line 204) and adds `sale.getTotal()` (line 206).
- **Explanation:** The sales, their totals and the format of their date belong to the sales module. The returns service aggregates them and interprets the string date of `Sale` by itself.
- **Consequence:** If `Sale` changes the type of its date, as developer1-audit proposes for `Sale.canBeReturned`, `calculateMonthlySales` stops compiling, and the sales total lives apart from the classes that know about sales.
- **Proposed solution:** `SaleService` gets a method that returns the total of the sales of a month and year. `ReturnService.calculateMonthlySales` keeps its signature and delegates to it, so `generateMonthlyBalance` and the menu behave the same. Pattern applied: Information Expert.
- **Other integrante's code:** Yes. The new method goes in `SaleService` (Technical Leader).

#### V-07 - Sale lookup by id copied in three classes

- **Pattern:** Information Expert (related: Low Coupling)
- **Module:** Returns (Requirement 3) and Warranties (Requirement 4)
- **Evidence:** `private Sale findSaleById(String saleId)` is defined in `ReturnService.java` line 143 (called at line 66), in `ReturnRepository.java` line 170 (called at line 139) and in `WarrantyRepository.java` line 153 (called at line 138). Each one iterates over the sales and compares `String.valueOf(sale.getId()).equals(saleId)`.
- **Explanation:** Knowing the sales and how they are identified belongs to `SaleService` and `SaleRepository`. Three other classes repeat that knowledge, including that the id is an `int` compared as text.
- **Consequence:** If the type of the sale id changes, three classes must change. Each lookup goes through `SaleService.getAllSales()` or `SaleRepository.findAll()`, which read and rebuild every sale from `sales.txt`, and `ReturnRepository.parseLine` does it for every stored return.
- **Proposed solution:** Add a lookup by id to `SaleService` and `SaleRepository`, keeping the same comparison, and make the three classes use it, deleting their private copies. Pattern applied: Information Expert.
- **Other integrante's code:** Yes. The lookup goes in `SaleService` and `SaleRepository` (Technical Leader).

#### V-08 - Persistence classes depend on services

- **Pattern:** Low Coupling (related: Indirection)
- **Module:** Returns (Requirement 3, adjusted in A4) and Warranties (Requirement 4)
- **Evidence:** `ReturnRepository.java` lines 16-18: `import com.gamezone.service.AccessoryService;`, `ProductService` and `SaleService`, injected through the constructors and used in `parseLine` and `resolveItem` to rebuild sales and returned items. `WarrantyRepository.java` line 18: `import com.gamezone.service.ProductService;`, used in `parseLine` through `productService.findProductById(productId)`.
- **Explanation:** The allowed direction is ui, service, persistence, model. These repositories depend on the layer above them. Adjustment A2 removed `SaleService` from `WarrantyRepository` but left `ProductService`.
- **Consequence:** The repositories cannot be built without the services, so the construction order in `Main` has to be arranged by hand, the cycle that A2 had to break can appear again, and any change to a service constructor reaches the repositories.
- **Proposed solution:** Make the repositories load and persist identifiers only, as adjustment A2 describes for `WarrantyRepository`, and rebuild the objects in the services, which already have the sale, product and accessory lookups. It is the same mechanism that the consolidated audit proposes for `SaleRepository.findAll`, so persistence is decoupled in one consistent way. Pattern applied: Low Coupling, with Indirection.
- **Other integrante's code:** Yes. `SaleRepository` and `Main` (Technical Leader).

#### V-09 - Services depend on concrete repository classes

- **Pattern:** Indirection (related: Protected Variations)
- **Module:** Accessories (Requirement 1), Promotions (Requirement 2), Returns (Requirement 3) and Warranties (Requirement 4)
- **Evidence:** `AccessoryService.java` line 30 `private AccessoryRepository accessoryRepository;`, `PromotionService.java` line 20 `private final PromotionRepository promotionRepository;`, `ReturnService.java` line 21 `private ReturnRepository returnRepository;` and `WarrantyService.java` line 23 `private WarrantyRepository warrantyRepository;`. These four are classes that read and write files. In contrast, `PersonService.java` line 22 uses the interface `PersonRepository` and `ProductService.java` line 19 uses the interface `ProductRepository`.
- **Explanation:** The project already uses an interface plus a file implementation for persons and products. Accessories, promotions, returns and warranties skip that indirection, so their services are tied to file storage.
- **Consequence:** Changing the storage, or testing a service with a fake repository, requires editing the four services.
- **Proposed solution:** Extract the interfaces `AccessoryRepository`, `PromotionRepository`, `ReturnRepository` and `WarrantyRepository`, rename the current classes to `FileAccessoryRepository`, `FilePromotionRepository`, `FileReturnRepository` and `FileWarrantyRepository`, and make the services depend on the interfaces. Behaviour and file formats do not change. The interfaces take the signatures that V-08 leaves, so V-08 is done first. Pattern applied: Indirection.
- **Other integrante's code:** Yes. `Main` (Technical Leader) must create the file implementations.

#### V-10 - Constructor dependency that is never used

- **Pattern:** Low Coupling
- **Module:** Warranties (Requirement 4)
- **Evidence:** `WarrantyService.java` line 25 `private ProductService productService;`, line 34 the constructor parameter `ProductService productService` and line 37 `this.productService = productService;`. No method of the class uses the field.
- **Explanation:** The class depends on `ProductService` without using it, and that dependency has no relation to the warranty rules.
- **Consequence:** `Main` has to build `ProductService` before `WarrantyService` for no reason, and a change in `ProductService` reaches a class that does not need it.
- **Proposed solution:** Remove the field and the constructor parameter. Pattern applied: Low Coupling.
- **Other integrante's code:** Yes. `Main` (Technical Leader) passes that argument when it creates the service and must be updated in the same step.
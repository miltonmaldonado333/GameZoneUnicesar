# GRASP Audit Report - Developer 1

**Auditor:** Jesús Manuel Martínez Baute  
**Role:** Developer 1  
**Date:** 2026-10-17  

---

## Assigned Modules & Classes

- `com.gamezone.model.Accessory`
- `com.gamezone.model.Cable`
- `com.gamezone.model.Controller`
- `com.gamezone.model.Memory`
- `com.gamezone.model.Warranty`
- `com.gamezone.model.BasicWarranty`
- `com.gamezone.model.ExtendedWarranty`
- `com.gamezone.model.Promotion`
- `com.gamezone.model.PercentageDiscount`
- `com.gamezone.model.CategoryDiscount`
- `com.gamezone.model.BulkPurchaseDiscount`
- `com.gamezone.model.Product`
- `com.gamezone.model.VideoGame`
- `com.gamezone.model.Console`
- `com.gamezone.persistence.ProductRepository`
- `com.gamezone.model.Return`
- `com.gamezone.exceptions.GameZoneException`
- `com.gamezone.exceptions.BusinessRuleException`
- `com.gamezone.exceptions.InvalidDataException`
- `com.gamezone.exceptions.PersistenceException`
- `com.gamezone.exceptions.ResourceNotFoundException`





---

## GRASP Evaluation Matrix

| Class / Method | Pattern Evaluated | Status | Violation / Compliance Evidence | Corrective Action Proposal |
| :--- | :--- | :---: | :--- | :--- |
| `Accessory` | Information Expert | ✅ Compliant | Encapsulates its compatible console list and category name (`"Accessory"`). | None required. |
| `Accessory` | Low Coupling | ✅ Compliant | Inherits from `Product` and uses standard Java collections without external dependencies. | None required. |
| `Accessory` | High Cohesion | ✅ Compliant | Focuses exclusively on accessory domain properties and compatibility contract. | None required. |
| `Accessory` | Polymorphism | ✅ Compliant | Overrides `getCategory()` and relies on the abstract `getDescription()` contract. | None required. |
| `Accessory` | Protected Variations | ✅ Compliant | Abstract class protects callers from changes in specific accessory implementations. | None required. |
| `Cable` | Information Expert | ✅ Compliant | Manages `length` and `connectorType` and formats its own description in `getFullDescription()`. | None required. |
| `Cable` | Polymorphism | ✅ Compliant | Polymorphically overrides `getFullDescription()` and `getCategory()` returning `"Cable"`. | None required. |
| `Cable` | High Cohesion | ✅ Compliant | Dedicated strictly to holding cable-specific state and formatting. | None required. |
| `Controller` | Information Expert | ✅ Compliant | Manages `connectionType` and formats its complete string representation in `getFullDescription()`. | None required. |
| `Controller` | Polymorphism | ✅ Compliant | Polymorphically overrides `getFullDescription()` and `getCategory()` returning `"controller"`. | None required. |
| `Controller` | High Cohesion | ✅ Compliant | Focuses solely on controller-specific domain features. | None required. |
| `Memory` | Information Expert | ✅ Compliant | Encapsulates `capacityGB` and `storageType`, generating its formatted state via `getFullDescription()`. | None required. |
| `Memory` | Polymorphism | ✅ Compliant | Overrides `getFullDescription()` and `getCategory()` returning `"Memory"`. | None required. |
| `Memory` | High Cohesion | ✅ Compliant | Holds state and behavior strictly related to memory accessories. | None required. |
| `Warranty` | Information Expert | ✅ Compliant | Encapsulates warranty period (`startDate`, `endDate`) and checks validity in `isActive()`. | None required. |
| `Warranty` | Polymorphism | ✅ Compliant | Defines abstract methods (`getDurationInMonths()`, `getWarrantyType()`, `getAdditionalCost()`) for concrete subclasses. | None required. |
| `Warranty` | High Cohesion | ✅ Compliant | Focuses strictly on warranty metadata and certificate generation logic. | None required. |
| `Warranty` | Protected Variations | ✅ Compliant | Abstract class isolates callers from variations in basic vs extended warranties. | None required. |
| `BasicWarranty` | Information Expert | ✅ Compliant | Encapsulates basic warranty defaults (6-month duration, $0.0 additional cost). | None required. |
| `BasicWarranty` | Polymorphism | ✅ Compliant | Implements `getDurationInMonths()`, `getWarrantyType()`, and `getAdditionalCost()` without type-checking conditionals. | None required. |
| `BasicWarranty` | High Cohesion | ✅ Compliant | Focused solely on basic warranty specific rules and parameters. | None required. |
| `BasicWarranty` | Creator | ✅ Compliant | Provides a parameterized constructor delegating state initialization to `super(...)`. | None required. |
| `ExtendedWarranty` | Information Expert | ✅ Compliant | Encapsulates extended warranty logic (12-month duration, calculates 10% additional cost based on product price). | None required. |
| `ExtendedWarranty` | Polymorphism | ✅ Compliant | Polymorphically implements `getDurationInMonths()`, `getWarrantyType()`, and `getAdditionalCost()` without type conditional checks. | None required. |
| `ExtendedWarranty` | High Cohesion | ✅ Compliant | Focused exclusively on rules, pricing calculations, and parameters for extended warranties. | None required. |
| `ExtendedWarranty` | Creator | ✅ Compliant | Provides a parameterized constructor delegating state initialization to `super(...)`. | None required. |
| `Promotion` | Information Expert | ✅ Compliant | Encapsulates promotion validity dates (`startDate`, `endDate`) and checks active state in `isActive()`. | None required. |
| `Promotion` | Polymorphism | ✅ Compliant | Defines abstract contracts (`calculateDiscount()`, `getDetails()`) to enforce polymorphic discount evaluations. | None required. |
| `Promotion` | High Cohesion | ✅ Compliant | Focused exclusively on promotion metadata and core date-validity rules. | None required. |
| `Promotion` | Protected Variations | ✅ Compliant | Abstract base class protects caller services from concrete promotion type implementations. | None required. |
| `PercentageDiscount` | Information Expert | ✅ Compliant | Encapsulates the discount percentage and calculates monetary savings on a sale in `calculateDiscount()`. | None required. |
| `PercentageDiscount` | Polymorphism | ✅ Compliant | Polymorphically overrides `calculateDiscount()` and `getDetails()` without using conditional logic. | None required. |
| `PercentageDiscount` | High Cohesion | ✅ Compliant | Focuses exclusively on flat percentage discount rules and calculations. | None required. |
| `PercentageDiscount` | Creator | ✅ Compliant | Provides a parameterized constructor delegating parent initialization to `super(...)`. | None required. |
| `CategoryDiscount` | Polymorphism | ⚠️ Violation | Uses `instanceof` checks (`product instanceof VideoGame`, etc.) inside `calculateDiscount()` instead of polymorphic category evaluation. | Refactor `calculateDiscount()` to rely on `product.getCategory()` polymorphically instead of type checking. |
| `CategoryDiscount` | Information Expert | ⚠️ Violation | Manually extracts and iterates over `sale.getProducts()` to sum prices instead of requesting category subtotals from `Sale`. | Delegate product iteration and category subtotal calculations to `Sale`. |
| `CategoryDiscount` | High Cohesion | ✅ Compliant | Focused strictly on category-specific promotional discount rules. | None required. |
| `CategoryDiscount` | Creator | ✅ Compliant | Provides a constructor delegating parent initialization and validating category input. | None required. |
| `BulkPurchaseDiscount` | Information Expert | ✅ Compliant | Encapsulates `minQuantity` and `percentage` to evaluate volume-based discounts in `calculateDiscount()`. | None required. |
| `BulkPurchaseDiscount` | Polymorphism | ✅ Compliant | Polymorphically overrides `calculateDiscount()` and `getDetails()` without using type checks. | None required. |
| `BulkPurchaseDiscount` | High Cohesion | ✅ Compliant | Focused exclusively on bulk purchase discount rules and minimum item count conditions. | None required. |
| `BulkPurchaseDiscount` | Creator | ✅ Compliant | Provides a parameterized constructor delegating parent initialization to `super(...)`. | None required. |
| `Product` | Information Expert | ✅ Compliant | Encapsulates fundamental product state (`id`, `title`, `price`, `stock`). | None required. |
| `Product` | Polymorphism | ✅ Compliant | Defines abstract contracts (`getFullDescription()`, `getCategory()`) to eliminate type-checking conditionals across product types. | None required. |
| `Product` | High Cohesion | ✅ Compliant | Focuses strictly on core inventory product state and common domain operations. | None required. |
| `Product` | Protected Variations | ✅ Compliant | Abstract superclass protects callers from variations in specialized product implementations. | None required. |
| `VideoGame` | Information Expert | ✅ Compliant | Encapsulates game-specific attributes (`platform`, `genre`, `ageRating`) and formats its details in `getFullDescription()`. | None required. |
| `VideoGame` | Polymorphism | ✅ Compliant | Polymorphically implements `getCategory()` returning `"VideoGame"` and `getFullDescription()` without conditional logic. | None required. |
| `VideoGame` | High Cohesion | ✅ Compliant | Focused exclusively on video game product properties and description formatting. | None required. |
| `VideoGame` | Creator | ✅ Compliant | Provides default and parameterized constructors delegating state initialization to `super(...)`. | None required. |
| `Console` | Information Expert | ✅ Compliant | Encapsulates hardware-specific attributes (`brand`, `model`, `generation`) and formats its details in `getFullDescription()`. | None required. |
| `Console` | Polymorphism | ✅ Compliant | Polymorphically implements `getCategory()` returning `"Console"` and `getFullDescription()` without type checking conditionals. | None required. |
| `Console` | High Cohesion | ✅ Compliant | Focused exclusively on console product hardware state and description formatting. | None required. |
| `Console` | Creator | ✅ Compliant | Provides default and parameterized constructors delegating state initialization to `super(...)`. | None required. |
| `ProductRepository` | Indirection | ✅ Compliant | Acts as an interface decoupling service logic from concrete file persistence implementations. | None required. |
| `ProductRepository` | Protected Variations | ✅ Compliant | Protects domain services from changes in storage mechanisms (e.g., swapping file storage for a database). | None required. |
| `ProductRepository` | Low Coupling | ✅ Compliant | Defines a lean contract depending only on domain entities (`Product`) and standard Java collections. | None required. |
| `ProductRepository` | High Cohesion | ✅ Compliant | Dedicated strictly to declaring product persistence contracts (`loadProducts`, `saveProducts`). | None required. |
| `ProductService` | Information Expert | ⚠️ Violation | `updateStock()` manually extracts and calculates stock (`product.getStock() - quantitySold`) instead of delegating stock reduction to `Product`. | Delegate stock reduction logic to a method inside `Product` (e.g., `product.reduceStock()`). |
| `ProductService` | High Cohesion / Pure Fabrication | ⚠️ Violation | `registerVideoGame()` and `registerConsole()` perform inline duplicate ID checks (`findProductById(id) != null`) instead of delegating to `ProductValidator`. | Delegate unique ID and existence validations to `ProductValidator`. |
| `ProductService` | Low Coupling | ✅ Compliant | Depends on `ProductRepository` interface rather than concrete file persistence classes. | None required. |
| `ProductService` | Indirection | ✅ Compliant | Acts as an intermediate service layer between UI components and persistence repositories. | None required. |
| `Return` | Information Expert | ⚠️ Violation | `calculateRefundAmount()` and `generateReturnReceipt()` manually iterate over `originalSale.getProducts()` to calculate sale subtotals instead of asking `Sale`. | Delegate sale subtotal calculations to a method inside `Sale` (e.g., `originalSale.getSubtotal()`). |
| `Return` | Low Coupling | ⚠️ Violation | Directly inspects the internal product collection of `originalSale` to compute discount rates. | Query `Sale` high-level methods to retrieve subtotals and discount rates. |
| `Return` | High Cohesion | ✅ Compliant | Dedicated strictly to encapsulating return metadata, refund calculations, and receipt formatting. | None required. |
| `Return` | Creator | ✅ Compliant | Provides overloaded constructors to initialize return instances with or without warranty refunds. | None required. |
| `Sale.canBeReturned()` | Information Expert | ✅ Compliant | `Sale` holds its own transaction date and legitimately evaluates its 30-day return window. | None required. |
| `Sale.canBeReturned()` | High Cohesion | ⚠️ Violation | Manually parses String dates (`LocalDate.parse(this.date)`) inside domain logic instead of encapsulating date formats. | Refactor `Sale.date` attribute type or extract date parsing to a dedicated utility / validator. |
| `Sale.canBeReturned()` | Protected Variations | ⚠️ Violation | Couples return deadline evaluation to the system clock via `LocalDate.now()`, hindering testability. | Overload `canBeReturned(LocalDate evaluationDate)` to pass the target date explicitly. |
| `GameZoneException` | Information Expert | ✅ Compliant | Encapsulates exception details (`message`, `errorCode`) as the root domain exception. | None required. |
| `GameZoneException` | Protected Variations | ✅ Compliant | Unchecked root exception protecting method signatures across services and repositories from checked throws. | None required. |
| `GameZoneException` | Low Coupling | ✅ Compliant | Extends standard `RuntimeException` with no dependencies on UI or persistence layers. | None required. |
| `GameZoneException` | High Cohesion | ✅ Compliant | Dedicated strictly to encapsulating domain error state and classification codes. | None required. |
| `BusinessRuleException` | Information Expert | ✅ Compliant | Encapsulates the `"BUSINESS_RULE_VIOLATION"` error code and detailed message for domain invariant breaches. | None required. |
| `BusinessRuleException` | Creator | ✅ Compliant | Provides a specialized constructor delegating message and error code to `GameZoneException`. | None required. |
| `BusinessRuleException` | Low Coupling | ✅ Compliant | Extends `GameZoneException` without introducing coupling to UI or persistence layers. | None required. |
| `BusinessRuleException` | High Cohesion | ✅ Compliant | Dedicated strictly to representing domain business rule violation failures. | None required. |
| `InvalidDataException` | Information Expert | ✅ Compliant | Encapsulates field name, issue detail, and the `"INVALID_DATA"` error code. | None required. |
| `InvalidDataException` | Creator | ✅ Compliant | Formats user-friendly Spanish error messages upon construction and delegates to `GameZoneException`. | None required. |
| `InvalidDataException` | Low Coupling | ✅ Compliant | Extends `GameZoneException` without introducing coupling to UI or persistence layers. | None required. |
| `InvalidDataException` | High Cohesion | ✅ Compliant | Dedicated strictly to encapsulating input formatting and range validation errors. | None required. |
| `PersistenceException` | Information Expert | ✅ Compliant | Encapsulates the `"PERSISTENCE_ERROR"` error code, contextual error message, and original root cause. | None required. |
| `PersistenceException` | Creator | ✅ Compliant | Specialized constructor setting error code and preserving cause trace via `initCause()`. | None required. |
| `PersistenceException` | Low Coupling | ✅ Compliant | Wraps low-level I/O exceptions into domain-specific exceptions, decoupling caller layers from file/storage technicalities. | None required. |
| `PersistenceException` | High Cohesion | ✅ Compliant | Dedicated strictly to encapsulating file and storage persistence operational failures. | None required. |
| `ResourceNotFoundException` | Information Expert | ✅ Compliant | Encapsulates resource type, missing identifier, and the `"NOT_FOUND"` error code. | None required. |
| `ResourceNotFoundException` | Creator | ✅ Compliant | Formats user-friendly Spanish messages upon instantiation and delegates state to `GameZoneException`. | None required. |
| `ResourceNotFoundException` | Low Coupling | ✅ Compliant | Extends `GameZoneException` without coupling domain entities to UI or persistence packages. | None required. |
| `ResourceNotFoundException` | High Cohesion | ✅ Compliant | Dedicated strictly to encapsulating missing entity search failures across all system modules. | None required. |
---

## Detailed Findings & Justifications

### 1. `com.gamezone.model.Accessory`
- **Evaluated Patterns:** Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations.
- **Analysis:**
  - *Current State:* Abstract domain model class extending `Product` to hold a list of compatible console IDs (`compatibleConsoleIds`) and provide common category identification (`"Accessory"`).
  - *Identified Issues:* None. Creator and Controller do not apply as this is an abstract domain entity that cannot be directly instantiated or receive system events. Pure Fabrication does not apply as it represents a real domain concept.
  - *Proposed Refactoring:* None required. The class fully complies with GRASP principles.

---

### 2. `com.gamezone.model.Cable`
- **Evaluated Patterns:** Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations.
- **Analysis:**
  - *Current State:* Concrete subclass extending `Accessory` with cable-specific attributes (`length`, `connectorType`) and implementing polymorphic methods (`getFullDescription()`, `getCategory()`).
  - *Identified Issues:* None. The class maintains low coupling by relying only on its parent class and standard Java types, avoiding direct UI or persistence references. Controller and Pure Fabrication do not apply.
  - *Proposed Refactoring:* None required. Complies with all applicable GRASP principles.

---

### 3. `com.gamezone.model.Controller`
- **Evaluated Patterns:** Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations.
- **Analysis:**
  - *Current State:* Concrete domain entity extending `Accessory` to manage controller connectivity (`connectionType`) and produce formatted descriptions.
  - *Identified Issues:* None. Controller and Pure Fabrication patterns are not applicable. Polymorphism and Information Expert are correctly applied.
  - *Proposed Refactoring:* None required. Complies with all applicable GRASP principles.

---

### 4. `com.gamezone.model.Memory`
- **Evaluated Patterns:** Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations.
- **Analysis:**
  - *Current State:* Concrete domain entity extending `Accessory` to manage storage capacity (`capacityGB`) and type (`storageType`).
  - *Identified Issues:* None. The class is highly cohesive, encapsulates its state as an Information Expert, and exhibits low coupling.
  - *Proposed Refactoring:* None required. Complies with all applicable GRASP principles.

  ### 5. `com.gamezone.model.Warranty`
- **Evaluated Patterns:** Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations.
- **Analysis:**
  - *Current State:* Abstract base class representing a product warranty. It automatically computes its expiration date (`endDate`) using the polymorphic `getDurationInMonths()` method upon construction, encapsulates validity checks via `isActive(LocalDate)`, and generates a formatted warranty certificate in `generateWarrantyCertificate()`.
  - *Identified Issues:* None. Creator and Controller do not apply as this is an abstract domain class. Pure Fabrication is not applicable as it represents a core business entity.
  - *Proposed Refactoring:* None required. Fully complies with all applicable GRASP principles.

  ### 6. `com.gamezone.model.BasicWarranty`
- **Evaluated Patterns:** Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations.
- **Analysis:**
  - *Current State:* Concrete implementation of `Warranty` representing a standard 6-month free warranty. It polymorphically overrides `getDurationInMonths()`, `getWarrantyType()`, and `getAdditionalCost()` to supply default values.
  - *Identified Issues:* None. Controller and Pure Fabrication patterns are not applicable. Low coupling is maintained by depending strictly on its abstract parent class `Warranty`.
  - *Proposed Refactoring:* None required. Fully complies with all applicable GRASP principles.

  ### 7. `com.gamezone.model.ExtendedWarranty`
- **Evaluated Patterns:** Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations.
- **Analysis:**
  - *Current State:* Concrete subclass extending `Warranty` to represent a premium 12-month extended warranty. It acts as an Information Expert by computing its additional cost (10% of the associated product price) directly in `getAdditionalCost()`, and polymorphically overrides `getDurationInMonths()` and `getWarrantyType()`.
  - *Identified Issues:* None. Controller and Pure Fabrication patterns are not applicable. Low coupling is maintained by interacting strictly with its inherited `Warranty` state and `Product` interface methods.
  - *Proposed Refactoring:* None required. Fully complies with all applicable GRASP principles.

  ### 8. `com.gamezone.model.Promotion`
- **Evaluated Patterns:** Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations.
- **Analysis:**
  - *Current State:* Abstract base class representing a general promotion. It encapsulates promotion identity (`id`, `name`) and validity period (`startDate`, `endDate`), serving as an Information Expert in `isActive(LocalDate)`. It defines abstract contracts for `calculateDiscount(Sale)` and `getDetails()`.
  - *Identified Issues:* None. Creator and Controller do not apply as this is an abstract domain class that cannot receive UI events directly. Pure Fabrication is not applicable as it represents a core domain model concept.
  - *Proposed Refactoring:* None required. Fully complies with all applicable GRASP principles.

  ### 9. `com.gamezone.model.PercentageDiscount`
- **Evaluated Patterns:** Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations.
- **Analysis:**
  - *Current State:* Concrete implementation of `Promotion` applying a flat percentage discount to the entire total of a sale. It acts as an Information Expert in `calculateDiscount(Sale)` by using its `percentage` attribute alongside the sale total.
  - *Identified Issues:* None. Controller and Pure Fabrication patterns are not applicable. Low coupling is maintained by depending strictly on abstract interfaces and model objects.
  - *Proposed Refactoring:* None required. Fully complies with all applicable GRASP principles.

### 10. `com.gamezone.model.CategoryDiscount`
- **Evaluated Patterns:** Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations.
- **Analysis:**
  - *Current State:* Concrete implementation of `Promotion` applying discounts to specific product categories (`VIDEOGAME`, `CONSOLE`, `ACCESSORY`).
  - *Identified Issues:* 
    1. **Polymorphism Violation:** The `calculateDiscount(Sale)` method uses `instanceof` checks (`product instanceof VideoGame`, etc.) instead of calling `product.getCategory()` polymorphically.
    2. **Information Expert Violation:** It directly extracts `sale.getProducts()` and calculates subtotals externally instead of querying `Sale` for category subtotals.
  - *Proposed Refactoring:* Refactor `calculateDiscount()` to use `product.getCategory()` polymorphically and delegate sale subtotal calculations to `Sale`.

  ### 11. `com.gamezone.model.BulkPurchaseDiscount`
- **Evaluated Patterns:** Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations.
- **Analysis:**
  - *Current State:* Concrete implementation of `Promotion` applying a percentage discount when the total quantity of items in a sale meets or exceeds `minQuantity`. It acts as an Information Expert in `calculateDiscount(Sale)` by checking the product count against `minQuantity`.
  - *Identified Issues:* None. Controller and Pure Fabrication patterns are not applicable. Low coupling is maintained by interacting strictly with domain interfaces.
  - *Proposed Refactoring:* None required. Fully complies with all applicable GRASP principles.

  ### 12. `com.gamezone.model.Product`
- **Evaluated Patterns:** Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations.
- **Analysis:**
  - *Current State:* Abstract base class representing a generic product in the GameZone inventory. It manages core attributes (`id`, `title`, `price`, `stock`) as an Information Expert and enforces polymorphic behavior through abstract contracts (`getFullDescription()`, `getCategory()`).
  - *Identified Issues:* None. Creator and Controller do not apply as this is an abstract domain class. Pure Fabrication is not applicable as it represents a core business entity.
  - *Proposed Refactoring:* None required. Fully complies with all applicable GRASP principles.

  ### 13. `com.gamezone.model.VideoGame`
- **Evaluated Patterns:** Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations.
- **Analysis:**
  - *Current State:* Concrete subclass extending `Product` to represent video game items in the inventory. It manages game-specific metadata (`platform`, `genre`, `ageRating`) as an Information Expert, overriding `getCategory()` and `getFullDescription()` polymorphically.
  - *Identified Issues:* None. Controller and Pure Fabrication patterns are not applicable. Low coupling is maintained by depending strictly on its abstract parent class `Product`.
  - *Proposed Refactoring:* None required. Fully complies with all applicable GRASP principles.

  ### 14. `com.gamezone.model.Console`
- **Evaluated Patterns:** Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations.
- **Analysis:**
  - *Current State:* Concrete subclass extending `Product` to represent gaming consoles in the store inventory. It manages hardware properties (`brand`, `model`, `generation`) as an Information Expert, polymorphically overriding `getCategory()` and `getFullDescription()`.
  - *Identified Issues:* None. Controller and Pure Fabrication patterns are not applicable. Low coupling is maintained by depending exclusively on its parent class `Product`.
  - *Proposed Refactoring:* None required. Fully complies with all applicable GRASP principles.

  ### 15. `com.gamezone.persistence.ProductRepository`
- **Evaluated Patterns:** Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations.
- **Analysis:**
  - *Current State:* Domain persistence interface defining contracts to load and save product collections (`loadProducts()`, `saveProducts()`). It applies Indirection and Protected Variations by decoupling `ProductService` from underlying storage mechanisms.
  - *Identified Issues:* None. Creator, Controller, and Pure Fabrication do not apply directly to interface definitions.
  - *Proposed Refactoring:* None required. Fully complies with all applicable GRASP principles.

### 16. `com.gamezone.service.ProductService`
- **Evaluated Patterns:** Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations.
- **Analysis:**
  - *Current State:* Service class orchestrating product business logic (registration, stock updates, lookups).
  - *Identified Issues:*
    1. **Information Expert Violation in `updateStock()`:** Manually calculates `product.getStock() - quantitySold` outside the entity instead of delegating stock reduction to `Product`.
    2. **High Cohesion / Pure Fabrication Violation in Registration:** Performs inline duplicate checks (`findProductById(id) != null`) directly in registration methods instead of delegating all existence checks to `ProductValidator`.
  - *Proposed Refactoring:* Delegate stock reduction to `Product` and move all product existence and uniqueness checks to `ProductValidator`.

  ### 17. `com.gamezone.model.Return`
- **Evaluated Patterns:** Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations.
- **Analysis:**
  - *Current State:* Domain model class representing product returns, computing proportional refunds based on original sale discounts, and generating return receipts.
  - *Identified Issues:* 
    1. **Information Expert & Low Coupling Violation:** In both `calculateRefundAmount()` and `generateReturnReceipt()`, the `Return` class extracts `originalSale.getProducts()` and loops over it to calculate the raw subtotal of the original sale. Since `Sale` is the Information Expert regarding its own items and pricing, `Return` should query `Sale` directly for its subtotal instead of performing external iteration over `Sale`'s internal data.
  - *Proposed Refactoring:* Add a getter or helper method in `Sale` to expose the gross subtotal, allowing `Return` to request the value without directly traversing `Sale`'s internal product list.

  ### 18. `com.gamezone.model.Sale.canBeReturned()`
- **Evaluated Patterns:** Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations.
- **Analysis:**
  - *Current State:* Domain method in `Sale` checking if the transaction occurred within the allowable 30-day return policy.
  - *Identified Issues:* 
    1. **High Cohesion Violation:** It performs string parsing (`LocalDate.parse(this.date)`) directly inside the domain method, mixing string manipulation with return policy evaluation.
    2. **Protected Variations Violation:** Direct coupling to system time through `LocalDate.now()` makes return policy checks non-deterministic and harder to test for historical or simulated dates.
  - *Proposed Refactoring:* Overload the method to accept a reference date parameter `canBeReturned(LocalDate returnDate)` and delegate String parsing to input validation layers or standard date formatters.

  ### 19. `com.gamezone.exceptions.GameZoneException`
- **Evaluated Patterns:** Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations.
- **Analysis:**
  - *Current State:* Abstract base exception extending `RuntimeException`. It encapsulates the descriptive error message and an optional domain `errorCode`. It serves as the root class for all application-specific exceptions.
  - *Identified Issues:* None. Creator and Controller patterns do not apply as this is an exception class. Pure Fabrication is not applicable.
  - *Proposed Refactoring:* None required. Fully complies with all applicable GRASP principles.

  ### 20. `com.gamezone.exceptions.BusinessRuleException`
- **Evaluated Patterns:** Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations.
- **Analysis:**
  - *Current State:* Concrete exception extending `GameZoneException`. It encapsulates the `"BUSINESS_RULE_VIOLATION"` error code to signal domain rule failures (e.g., insufficient stock, duplicate entity registration).
  - *Identified Issues:* None. Controller and Pure Fabrication patterns do not apply.
  - *Proposed Refactoring:* None required. Fully complies with all applicable GRASP principles.

  ### 21. `com.gamezone.exceptions.InvalidDataException`
- **Evaluated Patterns:** Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations.
- **Analysis:**
  - *Current State:* Concrete domain exception extending `GameZoneException`. It formats descriptive Spanish validation messages (e.g., `"El campo 'X' es inválido: Y"`) and sets the `"INVALID_DATA"` error code for input format or range failures.
  - *Identified Issues:* None. Controller and Pure Fabrication patterns do not apply.
  - *Proposed Refactoring:* None required. Fully complies with all applicable GRASP principles.

  ### 22. `com.gamezone.exceptions.PersistenceException`
- **Evaluated Patterns:** Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations.
- **Analysis:**
  - *Current State:* Concrete unchecked exception extending `GameZoneException`. It wraps lower-level I/O exceptions (like `IOException`) by preserving the original root cause via `initCause()` and assigning the `"PERSISTENCE_ERROR"` error code.
  - *Identified Issues:* None. Controller and Pure Fabrication patterns do not apply.
  - *Proposed Refactoring:* None required. Fully complies with all applicable GRASP principles.

  ### 23. `com.gamezone.exceptions.ResourceNotFoundException`
- **Evaluated Patterns:** Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations.
- **Analysis:**
  - *Current State:* Concrete domain exception extending `GameZoneException`. It generates formatted Spanish error messages (e.g., `"El/La producto con identificador 'X' no fue encontrado."`) and assigns the `"NOT_FOUND"` error code when a queried entity does not exist in memory or storage.
  - *Identified Issues:* None. Controller and Pure Fabrication patterns do not apply.
  - *Proposed Refactoring:* None required. Fully complies with all applicable GRASP principles.
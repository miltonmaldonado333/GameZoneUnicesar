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
| `CategoryDiscount` | Information Expert | ✅ Compliant | Encapsulates `targetCategory` and `percentage` to compute savings on matching products. | None required. |
| `CategoryDiscount` | High Cohesion | ✅ Compliant | Focused strictly on category-specific promotional discount rules. | None required. |
| `CategoryDiscount` | Creator | ✅ Compliant | Provides a constructor delegating parent initialization and validating category input. | None required. |
| `BulkPurchaseDiscount` | Information Expert | ✅ Compliant | Encapsulates `minQuantity` and `percentage` to evaluate volume-based discounts in `calculateDiscount()`. | None required. |
| `BulkPurchaseDiscount` | Polymorphism | ✅ Compliant | Polymorphically overrides `calculateDiscount()` and `getDetails()` without using type checks. | None required. |
| `BulkPurchaseDiscount` | High Cohesion | ✅ Compliant | Focused exclusively on bulk purchase discount rules and minimum item count conditions. | None required. |
| `BulkPurchaseDiscount` | Creator | ✅ Compliant | Provides a parameterized constructor delegating parent initialization to `super(...)`. | None required. |

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
  - *Identified Issues:* **Polymorphism Violation.** The `calculateDiscount(Sale)` method uses `if-else` branching combined with `instanceof` checks (`product instanceof VideoGame`, `product instanceof Console`, `product instanceof Accessory`) to identify eligible items. This violates GRASP Polymorphism by inspecting concrete runtime types instead of relying on polymorphic dispatch (e.g., calling `product.getCategory()`).
  - *Proposed Refactoring:* Replace the `instanceof` conditional cascade with a polymorphic call to `product.getCategory()` to evaluate eligibility cleanly without type inspection.

  ### 11. `com.gamezone.model.BulkPurchaseDiscount`
- **Evaluated Patterns:** Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations.
- **Analysis:**
  - *Current State:* Concrete implementation of `Promotion` applying a percentage discount when the total quantity of items in a sale meets or exceeds `minQuantity`. It acts as an Information Expert in `calculateDiscount(Sale)` by checking the product count against `minQuantity`.
  - *Identified Issues:* None. Controller and Pure Fabrication patterns are not applicable. Low coupling is maintained by interacting strictly with domain interfaces.
  - *Proposed Refactoring:* None required. Fully complies with all applicable GRASP principles.
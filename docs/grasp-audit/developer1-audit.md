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
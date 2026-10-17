# GRASP Audit Report - Technical Leader

**Auditor:** Milton Andrés Maldonado Salcedo  
**Role:** Technical Leader  
**Date:** 2026-10-17

---

## Assigned Modules & Classes

- `com.gamezone.ui.GameZoneUI`
- `com.gamezone.model.Sale`
- `com.gamezone.persistence.SaleRepository`
- `com.gamezone.service.SaleService`

---

## GRASP Evaluation Matrix

| Class / Method                        | Pattern Evaluated                  |    Status    | Violation / Compliance Evidence                                                                                                                                                                                                                     | Corrective Action Proposal                                                                                                         |
| :------------------------------------ | :--------------------------------- | :----------: | :-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :--------------------------------------------------------------------------------------------------------------------------------- |
| `GameZoneUI.registerCategoryDiscount` | Controller / High Cohesion         | ⚠️ Violation | Reflection ya class name checks (`p.getClass().getSimpleName()`) ke zariye UI layer mein categories infer karta hai.                                                                                                                                | Category discovery logic ko `ProductService.getAvailableCategories()` mein move karein aur UI se call karein.                      |
| `GameZoneUI.registerSale`             | Indirection / Low Coupling         | ⚠️ Violation | `ProductService` aur `AccessoryService` ke beech exceptions (`ResourceNotFoundException`) catch karke fallback lookup handle karta hai.                                                                                                             | Service layer mein unified facade method `findSaleableItemById(id)` provide karein.                                                |
| `GameZoneUI`                          | High Cohesion                      | ⚠️ Violation | Input parsing, domain logic validation, multi-service delegation, aur exception handling ek hi UI class mein mixed hai.                                                                                                                             | `GameZoneUI` ko purely user interaction tak limited rakhein, business workflows ko controllers/services ko delegate karein.        |
| `Sale.getSubtotal`                    | Information Expert                 | ✅ Compliant | Products list ka internal state use karke price calculation subtotal compute karta hai.                                                                                                                                                             | Koi action zaroori nahi hai.                                                                                                       |
| `Sale.calculateTotal`                 | Information Expert / High Cohesion | ✅ Compliant | Subtotal, discounts, aur extended warranty costs ka internal state manage aur calculate karta hai.                                                                                                                                                  | Koi action zaroori nahi hai.                                                                                                       |
| `Sale.generateReceipt`                | Information Expert                 | ✅ Compliant | Formatted receipt text generate karne ke liye saare required internal attributes direct use karta hai.                                                                                                                                              | Koi action zaroori nahi hai.                                                                                                       |
| `Sale.canBeReturned`                  | Information Expert                 | ✅ Compliant | Internal date attribute use karke 30-day return policy rule validate karta hai.                                                                                                                                                                     | Koi action zaroori nahi hai.                                                                                                       |
| `SaleRepository`                      | Pure Fabrication                   | ✅ Compliant | File-based persistence operations handle karta hai taaki `Sale` entity file storage logic se decoupled rahe.                                                                                                                                        | Koi action zaroori nahi hai.                                                                                                       |
| `SaleRepository.findAll`              | Low Coupling / Indirection         | ⚠️ Violation | File read karte samay objects rebuild karne ke liye `PersonService` aur `ProductService` par direct dependent hai.                                                                                                                                  | Data conversion layer/mapper create karein ya ID references pass karein taaki persistence layer service layer par dependent na ho. |
| `SaleService.registerSale`            | Controller / High Cohesion         | ⚠️ Violation | Transaction workflow (validation, stock checks, promotion evaluation, warranties, inventory update, persistence) orchestrate karta hai lekin item type (`instanceof Console`, `instanceof Accessory`) check karke branching logic handle karta hai. | Type checks (`instanceof`) hatayein aur polymorphic delegation ya specialized handlers update flow ke liye use karein.             |
| `SaleService`                         | Low Coupling                       | ✅ Compliant | Domain logic validation `SaleValidator` aur `ProductValidator` ko delegate karke concern separation balance karta hai.                                                                                                                              | Internal type checking/branching refactor karein coupling further clean karne ke liye.                                             |

---

## Detailed Findings & Justifications

### 1. `com.gamezone.ui.GameZoneUI`

- **Evaluated Patterns:** Controller, High Cohesion, Indirection, Low Coupling.
- **Analysis:**
  - _Current State:_ Console interface class jo user menu navigation, input reading, aur overall user workflow control karti hai.
  - _Identified Issues:_
    1. **Controller & High Cohesion Violation (`registerCategoryDiscount`):** Direct list iteration aur reflection (`getClass().getSimpleName()`) se domain categories identify karta hai, jo presentation layer ka concern nahi hai.
    2. **Indirection & Low Coupling Violation (`registerSale`):** UI direct multi-service lookup and exception-catching chain manage karta hai (`ProductService` then `AccessoryService`).
    3. **High Cohesion Violation:** Multi-concern hybrid class ban gayi hai (UI display + logic routing + fallback exception control).
  - _Proposed Refactoring:_
    1. `ProductService.getAvailableCategories()` expose karein.
    2. Shared lookup method `findSaleableItemById(id)` implement karein.
    3. `GameZoneUI` ko purely user input/output operations tak constraint karein.

---

### 2. `com.gamezone.model.Sale`

- **Evaluated Patterns:** Information Expert, High Cohesion, Low Coupling, Creator.
- **Analysis:**
  - _Current State:_ Domain entity jo sale transaction data, discount attributes, warranty cost, subtotal/total calculations, receipt generation, aur return policy checks hold karti hai.
  - _Identified Issues:_ None. Class pure **Information Expert** ke tor par react karti hai. Complete state encapsulation mainted hai.
  - _Proposed Refactoring:_ None required.

---

### 3. `com.gamezone.persistence.SaleRepository`

- **Evaluated Patterns:** Pure Fabrication, Low Coupling, Indirection, High Cohesion.
- **Analysis:**
  - _Current State:_ Class storage file (`sales.txt`) operations aur parsing clear karti hai.
  - _Identified Issues:_
    1. **Pure Fabrication Compliance:** Correct implementation jo domain entities ko direct file I/O se insulate karti hai.
    2. **Low Coupling & Indirection Violation (`findAll`):** Constructor ke through `PersonService` aur `ProductService` accept karti hai, jisse persistence layer application service layer par depend ho jaati hai.
  - _Proposed Refactoring:_
    1. Mapper/Builder introduce karein jo IDs parse karne ke baad higher service layer mein domain objects build kare.

---

### 4. `com.gamezone.service.SaleService`

- **Evaluated Patterns:** Controller, High Cohesion, Low Coupling, Polymorphism, Indirection.
- **Analysis:**
  - _Current State:_ Transaction processing centralize karta hai, including pricing adjustments, promotion rules, warranty generation, inventory deductions, aur sale persistence.
  - _Identified Issues:_
    1. **Controller Pattern Application:** Overall transaction control balance karta hai, lekin high cohesion partially break hoti hai jab implementation type-checking (`item instanceof Console`, `item instanceof Accessory`) ka use karti hai stock updates aur warranty assignment rules decide karne ke liye.
    2. **Polymorphism Violation:** Specific behaviors (jaise stock deduction rules ya warranty logic) domain abstraction par delegate karne ki jagah service layer mein `if-else`/`instanceof` checking se split ki gayi hain.
  - _Proposed Refactoring:_
    1. Polymorphic dispatching (`item.updateStock(...)` ya dedicated inventory updater strategies) use karke `instanceof` blocks remove karein.
    2. Extended warranty resolution strategies extract karein taaki `SaleService.registerSale` method ka length aur internal responsibilities reduce ho sakein.

# GRASP Refactor Log

## Non-Regression Strategy

To guarantee that the refactoring preserves external system behavior across all 10 menu operations, a baseline check was performed on `develop` prior to applying code modifications.

### Verification Plan
1. Compile the project using `mvn clean compile` before and after each refactoring activity.
2. Execute test workflows for product registration, listings, stock updates, sale processing, returns, and warranty assignments.
3. Ensure that identical inputs produce the exact same outputs, messages, and persisted data structure as before the refactor.

---

## Refactor Change Log - Developer 1

| Class | Change Made | Violation ID | Applied Pattern | Commit / PR / Author |
| :--- | :--- | :---: | :--- | :--- |
| `Product` | Added `reduceStock()` and `addStock()` methods to encapsulate stock mutation state. | V-10 | Information Expert | `9086f2c` / Jesús Martínez |
| `ProductValidator` | Added `validateUniqueProduct()` method to encapsulate product existence and duplicate assertions. | V-11 | High Cohesion / Pure Fabrication | `8d0224c` / Jesús Martínez |
| `CategoryDiscount` | Refactored `calculateDiscount()` to use polymorphic `product.getCategory()` calls. | V-08, V-09 | Polymorphism / Information Expert | `3ec52a6` / Jesús Martínez |
| `Return` | Refactored `calculateRefundAmount()` and `generateReturnReceipt()` to consume `originalSale.getSubtotal()`. | V-12 | Information Expert / Low Coupling | `6213435` / Jesús Martínez |
| `ProductService` | Updated `updateStock()` and product registration to delegate to `Product` and `ProductValidator`. | V-10, V-11 | Information Expert / Pure Fabrication | `18cc37f` / Jesús Martínez |
| `Sale` | Overloaded `canBeReturned(LocalDate evaluationDate)` to decouple policy checks from system clock reliance and preserved Javadocs. | V-14 | Protected Variations | `7c3ed9f` / Jesús Martínez |

## Refactor Change Log - Developer 2

| Class | Change Made | Violation ID | Applied Pattern | Commit / PR / Author |
| :--- | :--- | :---: | :--- | :--- |
| `WarrantyService` | Removed the unused `ProductService` field and constructor parameter. The matching call in `Main` was updated in the same commit (one line). | V-22 | Low Coupling | `0471c4d` / Luis Corzo |
| `PromotionValidator`, `PromotionService` | Extracted the target category check into `PromotionValidator`, keeping the exception type and the Spanish message. | V-17 | Pure Fabrication / High Cohesion | `dea802a` / Luis Corzo |
| `AccessoryService` | `updateStock` and `restoreStock` now call `Product.reduceStock` and `Product.addStock` instead of computing the stock outside the entity. | V-15 | Information Expert | `75571df` / Luis Corzo |
| `AccessoryService` | `listAccessoriesByType` compares the requested type with `getCategory()` ignoring case instead of testing the concrete class. | V-16 | Polymorphism | `f822d12` / Luis Corzo |
| `ReturnValidator` | New validator holding the return checks (sale exists and is inside the 30-day window, product belongs to the sale, units left to return), with the same exception type and messages. | V-18 | High Cohesion / Pure Fabrication | `7745bb0` / Luis Corzo |
| `ReturnService` | `registerReturn` delegates its checks to `ReturnValidator` and keeps only the orchestration. | V-18 | High Cohesion / Controller | `d56cde0` / Luis Corzo |
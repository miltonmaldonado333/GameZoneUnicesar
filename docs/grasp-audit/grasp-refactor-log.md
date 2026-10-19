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
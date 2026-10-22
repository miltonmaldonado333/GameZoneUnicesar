# SOLID Refactor Log — Developer 1

## Non-Regression Strategy

To guarantee that the refactoring preserves external system behavior across all catalog, warranty, return, and promotion menu operations, a baseline check was performed on `develop` prior to applying code modifications.

### Verification Plan
1. Compile the project using `mvn clean compile` before and after each refactoring activity.
2. Execute verification workflows for product registration, category discount calculations, return processing, warranty issuance, and sale validation.
3. Ensure that identical inputs produce the exact same outputs, exception types, user messages, and persisted data structures as before the refactor.

---

## Refactor Change Log - Developer 1

| Class / Interface | Change Made | Violation ID | Applied Pattern / Principle | Commit / PR / Author |
| :--- | :--- | :---: | :--- | :--- |
| `IProductService`, `ProductService` | Extracted the `IProductService` interface (extending `IStockService`) to segregate product catalog operations and decouple caller components from the concrete service. | V-DEV1-01 | **ISP** (Interface Segregation Principle) | `refactor: extract IProductService interface to apply ISP (V-DEV1-01)` / Jesús Martínez |
| `CategoryDiscount` | Removed console debug statements (`System.out.println`) from `calculateDiscount()` to prevent side-effect UI operations within domain entities. | V-DEV1-02 | **SRP** (Single Responsibility Principle) | `refactor: remove console print debug logic from CategoryDiscount to preserve SRP (V-DEV1-02)` / Jesús Martínez |
| `SaleValidator` | Evaluated parameter types (`Client`, `Seller`). Preserved concrete sub-roles to avoid **LSP** violations and maintain access to role-specific contracts (`getEmployeeCode()`). | V-DEV1-03 | **DIP** (Justified Domain Coupling) | *Dismissed/Justified in Audit Report* / Jesús Martínez |
| `ReturnReceiptPrinter`, `Return`, Menu Handlers | Extracted `generateReturnReceipt()` out of the `Return` domain entity into `ReturnReceiptPrinter` (in presentation layer) and updated menu invocation points. | V-DEV1-04 | **SRP** (Single Responsibility Principle) | `refactor: extract ReturnReceiptPrinter to decouple UI formatting from Return entity (V-DEV1-04)` / Jesús Martínez |
| `WarrantyCertificatePrinter`, `Warranty`, Menu Handlers | Extracted `generateWarrantyCertificate()` out of the abstract `Warranty` domain entity into `WarrantyCertificatePrinter` (in presentation layer) and updated menu invocation points. | V-DEV1-05 | **SRP** (Single Responsibility Principle) | `refactor
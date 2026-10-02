# Warranty Module Analysis

## 1. Class Hierarchy and Polymorphism

Common attributes (such as `id`, `product`, `sale`, `startDate`, and `endDate`) and common behavior (checking if a warranty is active via `isActive`) are encapsulated in the abstract base class `Warranty`[cite: 11]. Polymorphism allows each concrete subclass (`BasicWarranty` and `ExtendedWarranty`) to define its specific duration through the abstract method `getDurationInMonths()`, name via `getWarrantyType()`, and cost via `getAdditionalCost()`[cite: 11]. The base class constructor automatically calculates the `endDate` by invoking `getDurationInMonths()`, eliminating duplicate date calculation logic across the hierarchy[cite: 11].

## 2. Automatic Warranty Eligibility and Product Verification

This business rule is located in the **Service Layer** within the `SaleService.registerSale` method[cite: 11]. During sale registration, Java's `instanceof` operator (or pattern matching with `instanceof`) is used to evaluate each item in the sale (e.g., `if (product instanceof Console)`). Placing this decision in the Service layer prevents domain model pollution with flow control logic while ensuring that warranties are generated automatically only for valid product types[cite: 11].

## 3. Expiration Date Calculation

The expiration date (`endDate`) is calculated inside the base class `Warranty` constructor by executing `this.startDate.plusMonths(getDurationInMonths())`[cite: 11]. Performing this calculation in the constructor guarantees that every `Warranty` instance is created in a valid and consistent state without needing explicit manual calls to external calculation methods[cite: 11].

## 4. Extended Warranty Cost Calculation and Flow Integration

The additional cost is calculated during the sale registration flow in `SaleService.registerSale`[cite: 11]. For every console requested with an extended warranty, `WarrantyService.assignExtendedWarranty` is invoked[cite: 11]. The method returns an `ExtendedWarranty` instance, and its `getAdditionalCost()` value (10% of the console's price) is added to the sale's final total[cite: 11]. The method signature of `SaleService.registerSale` must be updated to receive a list of product IDs requiring extended warranties (e.g., `List<String> productIdsWithExtendedWarranty`)[cite: 11].

## 5. Expiring Soon Query Location and Dependencies

The `listWarrantiesExpiringSoon(int daysAhead)` method is located in the `WarrantyService` class within the **Service Layer**[cite: 11]. This placement is consistent with the 4-layer architecture, as processing and filtering business metrics belong in the Service layer rather than the Persistence layer[cite: 11]. `WarrantyService` depends on `WarrantyRepository` to fetch the complete list of persisted warranties before filtering items whose `endDate` falls within the specified range[cite: 11].

# Integrated System Class Diagram

```mermaid
classDiagram
    %% Layer 1: Model
    namespace Model {
        class Product {
            -String id
            -String title
            -double price
            -int stock
        }
        class Accessory {
            -String id
            -String name
            -double price
            -int stock
            -String type
        }
        class Sale {
            -String id
            -List items
            -double subtotal
            -double discount
            -double total
            +generateReceipt() String
        }
        class Promotion {
            <<abstract>>
            -String id
            +calculateDiscount(Sale sale)* double
        }
        class Warranty {
            -String id
            -String productId
            -String saleId
            -boolean isExtended
            -double cost
        }
        class Return {
            -String id
            -String saleId
            -List returnedItems
            +calculateRefundAmount() double
            +generateReturnReceipt() String
        }
    }

    %% Layer 2: Persistence
    namespace Persistence {
        class ProductRepository
        class AccessoryRepository
        class SaleRepository
        class PromotionRepository
        class WarrantyRepository
        class ReturnRepository
    }

    %% Layer 3: Service
    namespace Service {
        class ProductService
        class AccessoryService
        class SaleService {
            +registerSale()
        }
        class PromotionService {
            +findBestPromotionFor(Sale sale)
        }
        class WarrantyService {
            +cancelWarranties(productId, saleId) double
        }
        class ReturnService {
            +registerReturn()
            +calculateMonthlySales() double
            +calculateMonthlyReturns() double
            +generateMonthlyBalance() double
        }
    }

    %% Layer 4: UI
    namespace UI {
        class ConsoleMenu
    }

    %% Relationships
    ConsoleMenu --> SaleService
    ConsoleMenu --> ReturnService
    SaleService --> ProductService
    SaleService --> AccessoryService
    SaleService --> PromotionService
    SaleService --> WarrantyService
    ReturnService --> WarrantyService
    ReturnService --> AccessoryService
    ReturnService --> ProductService
Set-Content -Path "docs/integration-analysis.md" -Value @"
# Integration Analysis - GameZone Unicesar

This document details the root causes, applied solutions, and verification steps for all integration adjustments (A1 - A7) required to unify the GameZone system modules (Accessories, Promotions, Warranties, and Returns).

---

## A1 - Accessory Category Discount
* **Type:** Feature (eature/accessory-category-discount)
* **Cause:** CategoryDiscount was previously restricted to VIDEOGAME and CONSOLE targets.
* **Solution:** Extended CategoryDiscount to support ACCESSORY target category. Updated PromotionService.registerCategoryDiscount to validate all three categories, enabled the option in ConsoleMenu, and added an active accessory promotion in data/promotions.csv.

## A2 - Warranty Circular Dependency
* **Type:** Fix (ix/warranty-circular-dependency)
* **Cause:** A circular dependency existed (SaleService ? WarrantyService ? WarrantyRepository ? SaleService) due to resolving sale references during repository loading.
* **Solution:** Refactored WarrantyRepository to persist and load only primitive identifiers (saleId, productId). Updated WarrantyService constructor to receive WarrantyRepository, SaleRepository, and ProductService to resolve object references dynamically. Adjusted instantiation order in Main.

## A3 - Unified Sale Registration Flow
* **Type:** Refactor (
efactor/unified-sale-registration)
* **Cause:** Independent sale execution logic in previous requirements caused inconsistencies in discount calculation vs. warranty additions.
* **Solution:** Reordered SaleService.registerSale into a sequential pipeline:
  1. Validate non-empty items.
  2. Validate item stock (products/accessories).
  3. Calculate base subtotal.
  4. Query and apply best promotion only on item subtotal.
  5. Generate basic/extended warranties and add warranty cost.
  6. Calculate final total (subtotal - discount + extendedWarrantyCost).
  7. Deduct inventory via respective services.
  8. Persist sale and warranties.

## A4 - Return Accessory Stock Restoration
* **Type:** Fix (ix/return-accessory-stock)
* **Cause:** ReturnService was hardcoded to call ProductService.restoreStock, causing accessory stock to remain unrestored upon returns.
* **Solution:** Added
estoreStock method to AccessoryService. Injected AccessoryService into ReturnService to delegate stock restoration based on the instance type of the returned item.

## A5 - Discounted Sale Refund Calculation
* **Type:** Fix (ix/return-discounted-refund)
* **Cause:** Return.calculateRefundAmount summed list prices, over-refunding items originally bought under promotional discounts.
* **Solution:** Updated formula in Return.calculateRefundAmount to calculate proportional refunds:
  Refund = List Price * (1 - (Sale Discount / Sale Subtotal))

## A6 - Monthly Balance Report
* **Type:** Fix (ix/monthly-balance-report)
* **Cause:** generateMonthlyBalance only returned net balance, omitting gross sales and returns, and did not factor in extended warranties/discounts.
* **Solution:** Updated ReturnService with explicit calculateMonthlySales and calculateMonthlyReturns methods using final sale totals. Refactored generateMonthlyBalance to calculate the net difference and updated ConsoleMenu to present all three values.

## A7 - Warranty Cancellation on Returns
* **Type:** Feature (eature/return-warranty-cancellation)
* **Cause:** Returned consoles retained active warranties in the system without refunding extended warranty fees.
* **Solution:** Added WarrantyService.cancelWarranties(productId, saleId) to invalidate active warranties and return refundable extended warranty costs. Integrated this into ReturnService.registerReturn.
```

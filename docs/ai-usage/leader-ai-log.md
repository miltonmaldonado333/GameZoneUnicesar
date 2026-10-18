# AI Usage Log - Technical Leader

This log records technical consultations, logic verifications, and syntax checks with Gemini AI during the development and integration of the GameZone Unicesar system.

---

### Entry 1

- **Date:** 2026-09-10
- **Tool:** Gemini 1.5 Pro
- **Phase & Branch:** Phase 1 / `feature/accessory-module`
- **Objective:** Verify if extending `Product` for the `Accessory` class violates any fundamental OOP principles in our architecture.
- **Query:** "Is it recommended to have `Accessory` extend `Product` in a 4-layer architecture if accessories share price and stock attributes?"
- **Response:** Confirmed that inheritance is appropriate here since accessories are sellable store items with stock, price, and ID.
- **Decision:** Proceeded with `Accessory extends Product`.
- **Related Commit:** `feat: create accessory class hierarchy extending product`

---

### Entry 2

- **Date:** 2026-09-11
- **Tool:** Gemini 1.5 Pro
- **Phase & Branch:** Phase 1 / `feature/accessory-module`
- **Objective:** Double-check CSV parsing logic for accessory sub-types.
- **Query:** "Is checking a string discriminator column in a `switch` statement efficient for parsing 100+ CSV lines in Java?"
- **Response:** Confirmed it is lightweight and sufficient for file-based persistence without external libraries.
- **Decision:** Kept the `switch(type)` parsing logic in `AccessoryRepository`.
- **Related Commit:** `feat: implement accessory repository csv parsing`

---

### Entry 3

- **Date:** 2026-09-12
- **Tool:** Gemini 1.5 Pro
- **Phase & Branch:** Phase 1 / `feature/accessory-module`
- **Objective:** Quick syntax check for Stream API vs traditional loop for filtering compatible accessories.
- **Query:** "What is the cleanest way to filter a List<Accessory> by checking if `compatibleConsoleIds` contains a specific consoleId?"
- **Response:** Showed both `stream().filter()` and standard `for` loop examples.
- **Decision:** Used traditional `for` loop to keep execution consistent with team codebase guidelines.
- **Related Commit:** `feat: add accessory console compatibility filtering`

---

### Entry 4

- **Date:** 2026-09-13
- **Tool:** Gemini 1.5 Pro
- **Phase & Branch:** Phase 2 / `feature/promotion-module`
- **Objective:** Verify promotion selection behavior when two discounts offer the exact same amount.
- **Query:** "If two active promotions result in the same monetary discount, is it better to pick the first one or throw an exception?"
- **Response:** Advised picking the first encountered promotion to avoid breaking the checkout flow.
- **Decision:** Implemented `>=` comparison retaining the first maximum promotion.
- **Related Commit:** `feat: implement best promotion evaluation algorithm`

---

### Entry 5

- **Date:** 2026-09-14
- **Tool:** Gemini 1.5 Pro
- **Phase & Branch:** Phase 2 / `feature/accessory-category-discount` (A1)
- **Objective:** Review requirements for adjustment A1.
- **Query:** "Does extending CategoryDiscount to ACCESSORY require changing existing promotion model methods or just service validation?"
- **Response:** Clarified that updating validation in `PromotionService` and admitting `ACCESSORY` in `CategoryDiscount` is enough.
- **Decision:** Updated validation without modifying abstract `Promotion` parent class.
- **Related Commit:** `feat: extend category discount support to accessories`

---

### Entry 6

- **Date:** 2026-09-15
- **Tool:** Gemini 1.5 Pro
- **Phase & Branch:** Phase 3 / `feature/warranty-module`
- **Objective:** Validate basic warranty duration calculation using `LocalDate`.
- **Query:** "How to add 6 months to current date using Java `LocalDate`?"
- **Response:** Provided `saleDate.plusMonths(6)`.
- **Decision:** Applied `plusMonths(6)` for basic warranty and `plusMonths(12)` for extended warranty.
- **Related Commit:** `feat: add basic and extended warranty calculations`

---

### Entry 7

- **Date:** 2026-09-16
- **Tool:** Gemini 1.5 Pro
- **Phase & Branch:** Phase 3 / `fix/warranty-circular-dependency` (A2)
- **Objective:** Diagnose circular dependency error during application startup.
- **Query:** "Why does `SaleService` -> `WarrantyService` -> `WarrantyRepository` -> `SaleService` cause a StackOverflow or null reference in Main?"
- **Response:** Explained that loading full domain objects inside repositories creates circular construction loops. Recommended saving IDs instead.
- **Decision:** Refactored `WarrantyRepository` to persist primitive string IDs.
- **Related Commit:** `fix: resolve circular dependency in warranty module`

---

### Entry 8

- **Date:** 2026-09-17
- **Tool:** Gemini 1.5 Pro
- **Phase & Branch:** Phase 3 / `refactor/unified-sale-registration` (A3)
- **Objective:** Verify order of operations in unified sale processing.
- **Query:** "Should promotional discounts be calculated before or after adding extended warranty fees?"
- **Response:** Clarified that store discounts apply to item subtotals, while warranties are optional add-ons calculated on product base prices.
- **Decision:** Structured pipeline: Subtotal -> Promotion -> Warranties -> Final Total.
- **Related Commit:** `refactor: unify sale registration execution pipeline`

---

### Entry 9

- **Date:** 2026-09-18
- **Tool:** Gemini 1.5 Pro
- **Phase & Branch:** Phase 4 / `feature/return-module`
- **Objective:** Check `ChronoUnit` method syntax for date comparison.
- **Query:** "What is the exact syntax for `ChronoUnit.DAYS.between` in Java 17?"
- **Response:** Provided `ChronoUnit.DAYS.between(startDate, endDate)`.
- **Decision:** Used in `Sale.canBeReturned()` to enforce 30-day policy.
- **Related Commit:** `feat: implement sale return eligibility rules`

---

### Entry 10

- **Date:** 2026-09-19
- **Tool:** Gemini 1.5 Pro
- **Phase & Branch:** Phase 4 / `fix/return-accessory-stock` (A4)
- **Objective:** Consult clean delegation for restoring stock on returned items.
- **Query:** "Is using `instanceof` acceptable in service classes to delegate stock restoration to `ProductService` or `AccessoryService`?"
- **Response:** Confirmed `instanceof` is standard here when handling mixed item lists in Java without double dispatch.
- **Decision:** Implemented `instanceof` check inside `ReturnService`.
- **Related Commit:** `fix: restore accessory stock on item return`

---

### Entry 11

- **Date:** 2026-09-20
- **Tool:** Gemini 2.0 Flash
- **Phase & Branch:** Phase 4 / `fix/return-discounted-refund` (A5)
- **Objective:** Validate mathematical formula for partial return refunds.
- **Query:** "Is `ListPrice * (1 - (Discount / Subtotal))` mathematically correct to reimburse discounted items proportionally?"
- **Response:** Confirmed the formula accurately computes the effective price paid per item.
- **Decision:** Implemented formula in `Return.calculateRefundAmount`.
- **Related Commit:** `fix: calculate proportional refund amount for discounted sales`

---

### Entry 12

- **Date:** 2026-09-21
- **Tool:** Gemini 2.0 Flash
- **Phase & Branch:** Phase 4 / `fix/monthly-balance-report` (A6)
- **Objective:** Review method signatures for monthly balance calculations.
- **Query:** "Should monthly balance return a formatted String or double value in Service layer?"
- **Response:** Recommended returning `double` from Service layer and formatting currency in UI layer (`ConsoleMenu`).
- **Decision:** Kept numeric calculations in `ReturnService` and UI formatting in `ConsoleMenu`.
- **Related Commit:** `fix: enhance monthly balance calculations and report`

---

### Entry 13

- **Date:** 2026-09-22
- **Tool:** Gemini 2.0 Flash
- **Phase & Branch:** Phase 4 / `feature/return-warranty-cancellation` (A7)
- **Objective:** Verify warranty cancellation side-effects.
- **Query:** "When a console is returned, should basic warranty ($0) produce any refund value?"
- **Response:** Confirmed basic warranty refund is $0; only extended warranty cost should be refunded.
- **Decision:** Logic set to return $0 for basic warranty and exact paid cost for extended warranty.
- **Related Commit:** `feat: invalidate warranties and refund extended coverage on return`

---

### Entry 14

- **Date:** 2026-09-23
- **Tool:** Gemini 2.5 Flash
- **Phase & Branch:** Phase 5 / `docs/integration-documentation` (A8)
- **Objective:** English grammar review for technical document.
- **Query:** "Review this draft of integration-analysis.md for technical clarity and grammar errors in English."
- **Response:** Provided minor punctuation and phrasing corrections for technical terms.
- **Decision:** Applied corrections and saved `docs/integration-analysis.md`.
- **Related Commit:** `docs: add integration analysis documentation for A8`

---

### Entry 15

- **Date:** 2026-09-24
- **Tool:** Gemini 2.5 Flash
- **Phase & Branch:** Phase 5 / `docs/integration-documentation` (A8)
- **Objective:** Check Mermaid diagram syntax for namespace grouping.
- **Query:** "How to define namespaces inside `classDiagram` in Mermaid?"
- **Response:** Showed `namespace Name { class A }` syntax.
- **Decision:** Applied namespace syntax in `docs/integrated-class-diagram.md`.
- **Related Commit:** `docs: add unified class diagram in mermaid for A8`

---

### Entry 16

- **Date:** 2026-09-25
- **Tool:** Gemini 2.5 Flash
- **Phase & Branch:** Phase 5 / `docs/integration-documentation` (A8)
- **Objective:** Verify README directory tree formatting.
- **Query:** "Check if this text tree layout matches standard markdown code block formatting for project structure."
- **Response:** Verified tree structure and suggested adding `returns.csv` and `return-analysis.md`.
- **Decision:** Updated file list in `README.md`.
- **Related Commit:** `docs: update main readme with integrated system features`

---

### Entry 17

- **Date:** 2026-09-26
- **Tool:** Gemini 2.5 Flash
- **Phase & Branch:** Phase 5 / `docs/integration-documentation` (A8)
- **Objective:** Check Mermaid flowchart syntax for architecture layers.
- **Query:** "How to set unidirectional top-down arrows between subgraphs in Mermaid `graph TD`?"
- **Response:** Showed syntax linking elements across subgraphs (`ConsoleMenu --> SaleService`).
- **Decision:** Saved updated diagram in `docs/layers-diagram.md`.
- **Related Commit:** `docs: update layers diagram mermaid structure`

---

### Entry 18

- **Date:** 2026-09-27
- **Tool:** Gemini 2.5 Flash
- **Phase & Branch:** Phase 5 / `docs/integration-documentation` (A8)
- **Objective:** Confirm Git Flow pull request checklist.
- **Query:** "What details are required in PR descriptions for fix/ and refactor/ branches according to standard guidelines?"
- **Response:** Problem detected, root cause, applied solution, and verification method.
- **Decision:** Included all 4 items in Pull Request descriptions on GitHub.
- **Related Commit:** `docs: review and finalize documentation structure`

---

### Entry 19

- **Date:** 2026-09-28
- **Tool:** Gemini 2.5 Flash
- **Phase & Branch:** Phase 5 / `docs/integration-documentation` (A8)
- **Objective:** Terminal command check for file generation.
- **Query:** "What is the PowerShell command to create a file with multi-line string content?"
- **Response:** Showed `Set-Content -Path "file" -Value @" ... "@`.
- **Decision:** Used PowerShell here-strings for local documentation creation.
- **Related Commit:** `docs: automate documentation file creation scripts`

---

### Entry 20

- **Date:** 2026-09-30
- **Tool:** Gemini 2.5 Flash
- **Phase & Branch:** Phase 5 / `docs/integration-documentation` (A8)
- **Objective:** Format and finalize technical leader AI log for course submission.
- **Query:** "Format my AI usage notes into a clean Markdown log following the required course fields."
- **Response:** Formatted all 20 entries into the final Markdown log layout.
- **Decision:** Saved directly to `docs/ai-usage/leader-ai-log.md`.
- **Related Commit:** `docs: complete leader ai usage log for integration requirement`

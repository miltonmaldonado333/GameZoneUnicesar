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

---

### Entry 21

- **Date:** 2026-10-02
- **Tool:** Gemini 2.5 Flash
- **Phase & Branch:** Phase 6 / `fix/ui-input-handling`
- **Objective:** Fix `InputMismatchException` in `Scanner` when reading numeric options in `GameZoneUI`.
- **Query:** "How to prevent `Scanner.nextInt()` from skipping the next line when reading user inputs in a console menu?"
- **Response:** Recommended reading inputs as `String` using `Scanner.nextLine()` and parsing with `Integer.parseInt()` inside a `try-catch` block.
- **Decision:** Refactored console reader in `GameZoneUI` to use `nextLine()` and `try-catch` parsing.
- **Related Commit:** `fix: resolve scanner buffer issues in UI menu input`

---

### Entry 22

- **Date:** 2026-10-04
- **Tool:** Gemini 2.5 Flash
- **Phase & Branch:** Phase 6 / `fix/csv-formatting-error`
- **Objective:** Resolve `ArrayIndexOutOfBoundsException` while parsing CSV files containing empty trailing fields.
- **Query:** "Why does `String.split(",")` ignore trailing empty strings and how to fix it in Java?"
- **Response:** Explained that `split(",")` defaults to dropping empty trailing tokens and suggested using `split(",", -1)` to keep all fields.
- **Decision:** Updated CSV reading logic across all file repository parsers to `split(",", -1)`.
- **Related Commit:** `fix: preserve empty trailing columns in repository csv split`

---

### Entry 23

- **Date:** 2026-10-06
- **Tool:** Gemini 2.5 Flash
- **Phase & Branch:** Phase 6 / `refactor/date-formatter`
- **Objective:** Standardize date string parsing across model and service classes.
- **Query:** "How to safely parse `LocalDate` using a custom pattern like `yyyy-MM-dd` in Java 17?"
- **Response:** Demonstrated using `DateTimeFormatter.ofPattern("yyyy-MM-dd")` and handling `DateTimeParseException`.
- **Decision:** Added central `DateTimeFormatter` configuration for sale and return dates.
- **Related Commit:** `refactor: standardize localdate formatting and parsing`

---

### Entry 24

- **Date:** 2026-10-08
- **Tool:** Gemini 2.5 Flash
- **Phase & Branch:** Phase 6 / `fix/maven-build-failure`
- **Objective:** Fix Maven compilation error regarding source version compatibility.
- **Query:** "How to configure `pom.xml` to enforce Java 17 source and target release in Maven Compiler Plugin?"
- **Response:** Provided the `<maven.compiler.source>17</maven.compiler.source>` and `<maven.compiler.target>17</maven.compiler.target>` configuration properties.
- **Decision:** Updated `pom.xml` build properties to explicitly target Java 17.
- **Related Commit:** `build: set maven compiler plugin release version to 17`

---

### Entry 25

- **Date:** 2026-10-10
- **Tool:** Gemini 2.5 Flash
- **Phase & Branch:** Phase 6 / `fix/file-not-found-initialization`
- **Objective:** Prevent runtime exceptions when persistence CSV files do not exist on initial run.
- **Query:** "What is the best way to auto-create missing files and parent directories before reading in Java `File` API?"
- **Response:** Showed `file.getParentFile().mkdirs()` followed by `file.createNewFile()`.
- **Decision:** Added directory and file existence checks in repository initialization blocks.
- **Related Commit:** `fix: auto-create storage directory and csv files on startup`

---

### Entry 26

- **Date:** 2026-10-12
- **Tool:** Gemini 2.5 Flash
- **Phase & Branch:** Phase 6 / `fix/null-pointer-product-lookup`
- **Objective:** Resolve `NullPointerException` during product lookup by ID when case sensitivity differs.
- **Query:** "How to compare two String variables in Java safely when one might be null?"
- **Response:** Recommended using `Objects.equals(a, b)` or `stringA != null && stringA.equalsIgnoreCase(stringB)`.
- **Decision:** Applied safe equals check in `ProductService.findProductById`.
- **Related Commit:** `fix: add null safe string comparisons in product queries`

---

### Entry 27

- **Date:** 2026-10-14
- **Tool:** Gemini 2.5 Flash
- **Phase & Branch:** Phase 6 / `refactor/git-merge-conflict`
- **Objective:** Cleanly resolve Git merge conflict between `feature/sale-module` and `develop`.
- **Query:** "How to abort a git merge in progress if conflict resolution gets corrupted?"
- **Response:** Provided `git merge --abort`.
- **Decision:** Aborted corrupted merge, stashed local changes, and re-executed clean merge step-by-step.
- **Related Commit:** `chore: resolve branch merge conflicts on develop`

---

### Entry 28

- **Date:** 2026-10-15
- **Tool:** Gemini 2.5 Flash
- **Phase & Branch:** Phase 6 / `fix/receipt-currency-format`
- **Objective:** Format numeric sale totals into clean COP currency strings for receipts.
- **Query:** "How to format a `double` as Colombian Pesos currency in Java without decimal cents?"
- **Response:** Showed using `NumberFormat.getCurrencyInstance(new Locale("es", "CO"))` and setting maximum fraction digits to 0.
- **Decision:** Used currency formatter in `Sale.generateReceipt()` and UI output screens.
- **Related Commit:** `fix: format monetary values to standard COP currency representation`

---

### Entry 29

- **Date:** 2026-10-16
- **Tool:** Gemini 2.5 Flash
- **Phase & Branch:** Phase 6 / `fix/list-unmodifiable-exception`
- **Objective:** Fix `UnsupportedOperationException` when adding items to list loaded from service.
- **Query:** "Why does calling `.add()` on a list returned by `List.of()` throw `UnsupportedOperationException`?"
- **Response:** Explained that `List.of()` returns an immutable list, and suggested wrapping it with `new ArrayList<>(List.of(...))`.
- **Decision:** Returned modifiable `ArrayList` instances from service data query methods.
- **Related Commit:** `fix: return modifiable list instances from service layers`

---

### Entry 30

- **Date:** 2026-10-17
- **Tool:** Gemini 2.5 Flash
- **Phase & Branch:** Phase 6 / `docs/ai-usage-finalization`
- **Objective:** Verify proper Markdown code block escaping for terminal commands in documentation.
- **Query:** "How to display raw markdown backticks inside a markdown code snippet block without breaking rendering?"
- **Response:** Showed wrapping the outer block with four backticks ` ``` `.
- **Decision:** Formatted terminal outputs in team documentation files accordingly.
- **Related Commit:** `docs: fix markdown syntax escaping in documentation files`

---

### SECTION 1- PHASE 1 - AUDIT

---

### Entry 31

- **Date:** 2026-10-21
- **Tool:** Gemini 2.5 Flash
- **Phase & Branch:** Phase 1 / `feature/solid-audit`
- **Objective:** Consult Git workflow commands and Markdown file structure requirements for Requirement 8 (Phase 1).
- **Query:** "hagmos los archivos en la nueva rama, los necesarios como lo indica este taller"
- **Response:** Provided Git terminal commands to create the required directory structure (`docs/solid-audit/`, `docs/ai-usage/`) and initialized blank files for `solid-analysis.md`, `leader-audit.md`, and `consolidated-audit.md`.
- **Decision:** Created branch `feature/solid-audit` from updated `develop` and initialized all project Markdown files using terminal commands.
- **Related Commit:** `docs: initialize solid audit documentation directory structure`

---

### Entry 32

- **Date:** 2026-10-21
- **Tool:** Gemini 2.5 Flash
- **Phase & Branch:** Phase 1 / `feature/solid-audit`
- **Objective:** Request theoretical guidelines and classification criteria for evaluating SOLID principles in code analysis.
- **Query:** "dame unos consejos para el analysis sencillos como definicion de principios para responder las preguntas"
- **Response:** Provided concise definitions for SRP, OCP, LSP, ISP, and DIP, along with minimal evidence standards (line numbers, code snippets, and failure impact) required for classification.
- **Decision:** Applied the classification criteria and definitions as the standard baseline to evaluate classes and answer team analysis questions in `solid-analysis.md`.
- **Related Commit:** `docs: add solid classification criteria to analysis documentation`

---

## Section 2: Phase 2 — Refactoring

### Entry 33

- **Date:** 2026-10-22
- **Tool:** Gemini 2.5 Flash
- **Phase & Branch:** Phase 2 / `feature/solid-refactor`
- **Objective:** Extract printing logic from the `Product` domain class to adhere to the Single Responsibility Principle.
- **Query:** "How to extract console printing logic from the `Product` class to follow the Single Responsibility Principle?"
- **Response:** Recommended moving all `System.out.println` statements to a dedicated `ProductView` or `ConsolePrinter` utility class.
- **Decision:** Created a `ConsolePrinter` class to handle all UI outputs, keeping `Product` strictly for data.
- **Related Commit:** `refactor: extract printing logic from product model`

---

### Entry 34

- **Date:** 2026-10-22
- **Tool:** Gemini 2.5 Flash
- **Phase & Branch:** Phase 2 / `feature/solid-refactor`
- **Objective:** Replace a hardcoded switch statement for discount types.
- **Query:** "What is the simplest way to replace a large switch statement for different discount types in Java?"
- **Response:** Suggested using the Strategy pattern by creating a `DiscountStrategy` interface and implementing separate classes for each discount type.
- **Decision:** Implemented `DiscountStrategy` and refactored the promotion calculator.
- **Related Commit:** `refactor: replace discount switch with strategy pattern`

---

### Entry 35

- **Date:** 2026-10-22
- **Tool:** Gemini 2.5 Flash
- **Phase & Branch:** Phase 2 / `feature/solid-refactor`
- **Objective:** Fix a Liskov Substitution Principle violation in the accessory hierarchy.
- **Query:** "Why does throwing `UnsupportedOperationException` in a subclass override violate LSP?"
- **Response:** Explained that subclasses must uphold the expected behavior of the base class; throwing unexpected runtime exceptions breaks base class contracts.
- **Decision:** Restructured the class hierarchy using interfaces so subclasses only implement methods they actually support.
- **Related Commit:** `refactor: fix lsp violation by restructuring inheritance`

---

### Entry 36

- **Date:** 2026-10-22
- **Tool:** Gemini 2.5 Flash
- **Phase & Branch:** Phase 2 / `feature/solid-refactor`
- **Objective:** Implement Dependency Inversion Principle for repository access.
- **Query:** "How to inject a repository interface into a service class without using a framework like Spring?"
- **Response:** Demonstrated manual constructor injection by passing the repository interface as a parameter in the service's constructor.
- **Decision:** Applied constructor injection to `SaleService`, making it depend on the interface rather than the concrete CSV repository.
- **Related Commit:** `refactor: apply constructor injection to services`

---

### Entry 37

- **Date:** 2026-10-22
- **Tool:** Gemini 2.5 Flash
- **Phase & Branch:** Phase 2 / `feature/solid-refactor`
- **Objective:** Keep the local refactoring branch updated with the main development line.
- **Query:** "What is the git command to update my current feature branch with the latest changes from develop using rebase?"
- **Response:** Provided the commands `git fetch origin` followed by `git rebase origin/develop`.
- **Decision:** Rebased the local `feature/solid-refactor` branch to maintain a clean linear commit history.
- **Related Commit:** `chore: rebase refactor branches onto develop`

AI Usage Log - Developer 2 (Person, Accessory, Promotion & Return Modules)
Overview
Author: Luis Manuel Corzo Castro - 1066870055
Role: Developer 2 (Persistence and Service layers)
Assigned Modules: Person (model/persistence/service), Accessory (persistence/service), Promotion (persistence/service, best-discount selection), Return (persistence/service, monthly balance)
Branches: feature/person-module, feature/accesory-module, feature/promotion-module, feature/return-module

Comprehensive Interaction Timeline

1. Environment Setup & Person Module Scaffolding
Date & Time: Person module session, initial setup
Context / Prompt: Setting up JDK 17, Maven, and Git for the first time, then drafting Person (abstract), Client, and Seller according to docs/class-diagram.md.
AI Response / Advice: Walked through JDK/Maven installation and PATH configuration, then drafted the model classes with JavaDoc based on the shared diagram.
Decision Taken: Verified every command and class against the diagram myself before compiling, committing, and pushing in atomic commits (one class per commit).

2. Person Persistence and Service Layers
Date & Time: Person module session
Context / Prompt: Implementing PersonRepository (interface) and FilePersonRepository, then PersonService with the five methods required by the diagram.
AI Response / Advice: Proposed a semicolon-separated file format for persistence and a PersonService constructor that depends on the PersonRepository interface, not the concrete implementation.
Decision Taken: Kept the proposed file format, verified dependency injection used the interface, and committed each layer separately.

3. Accessory Module - Typo in a Teammate's File
Date & Time: Accessory module session
Context / Prompt: mvn compile failed because SaleService.java referenced a misspelled class AccesoryService instead of my actual AccessoryService.
AI Response / Advice: Identified the typo as belonging to the Leader's file, not mine, and advised a project-wide find-and-replace followed by notifying the Leader.
Decision Taken: Verified my own class names were correct first, fixed the typo, and reported the change to my Leader.

4. Accessory Module - Overloaded Methods and Missing Class Declaration
Date & Time: Accessory module session
Context / Prompt: Main.java called AccessoryRepository() with no arguments and GameZoneUI.java called the register methods with one fewer argument than expected; later, mvn compile threw "class expected" errors.
AI Response / Advice: Suggested adding a no-argument overloaded constructor and overloaded "short" register methods with default empty lists, then identified that the public class AccessoryService declaration itself was missing from the file.
Decision Taken: Implemented the overloads, added the missing class declaration, and confirmed with mvn clean compile that the build succeeded.

5. Promotion Module - Reconciling Constructor Order
Date & Time: Promotion module session
Context / Prompt: After my teammate pushed the Promotion hierarchy, the constructors placed type-specific attributes (percentage, category, minQuantity) before the common ones (id, name, dates), different from what had been assumed while drafting PromotionRepository/PromotionService in advance.
AI Response / Advice: Flagged the parameter order mismatch and updated all object-construction calls to match the real constructors; also noted Promotion.java had a placeholder comment instead of real getters/setters.
Decision Taken: Added the missing getters/setters to Promotion.java, verified the corrected constructor calls, and proceeded with the corrected versions.

6. Promotion Module - Branch Isolation Compilation Failure
Date & Time: Promotion module session
Context / Prompt: mvn compile failed with 16 errors unrelated to my own change - missing Accessory classes and missing methods on Sale that the Leader's SaleService already expected.
AI Response / Advice: Had me check git log develop and git branch -a, identifying that feature/promotion-module had branched off develop before the Accessory module's Pull Request was merged.
Decision Taken: Merged my own feature/accesory-module branch into feature/promotion-module to bring in my missing classes, and reported the remaining Sale-related errors to the Leader as outside my scope.

7. Return Module - Adapting to Missing findSaleById
Date & Time: Return module session
Context / Prompt: SaleService.java had no findSaleById method, only getAllSales/getSalesByClient/getSalesBySeller, needed for ReturnRepository and ReturnService.registerReturn.
AI Response / Advice: Proposed resolving the sale lookup inside my own classes by iterating getAllSales() and comparing IDs as strings, rather than requesting a change outside my assigned scope.
Decision Taken: Kept the lookup logic in my own classes, noting Sale.getId() returns int while the requirement specifies a String saleId parameter.

8. Return Module - BOM Compilation Error
Date & Time: Return module session
Context / Prompt: mvn clean compile failed with "illegal character" errors on both new files after creating them via PowerShell terminal commands.
AI Response / Advice: Identified this as a Byte Order Mark added by PowerShell's -Encoding UTF8 flag, and provided a script to rewrite the files without it.
Decision Taken: Applied the fix, confirmed BUILD SUCCESS for the full project, and committed it as its own atomic commit.

9. Documentation - Layers Diagram Corruption and Updates
Date & Time: Across Accessory, Promotion, and Return module sessions
Context / Prompt: Creating and repeatedly updating docs/layers-diagram.md to reflect each new module; at one point the file had duplicated content from an unresolved merge conflict.
AI Response / Advice: Proposed the initial Mermaid diagram structure, extended it for each new module, and rewrote it cleanly when duplication was found rather than patching it.
Decision Taken: Verified class names and relationships against the real project files before each commit, and confirmed no teammate content was lost when rewriting the corrupted version.

10. Git Flow Discipline
Date & Time: Across all four modules
Context / Prompt: Understanding commit granularity requirements (six-plus atomic commits per module, immediate push after each) and how to handle an already-pushed large commit in the Accessory module.
AI Response / Advice: Explained the trade-off between rewriting shared history (git reset --soft plus push --force) versus continuing with smaller commits going forward, recommending the latter to avoid disrupting teammates.
Decision Taken: Kept shared history intact, split all subsequent work into atomic commits pushed immediately, and verified via git log whether documentation (promotion-analysis.md, return-analysis.md) was already completed by a teammate before creating it myself.

11. Warranty Module - Resolving Sale Lookup Without a Dedicated Method
Date & Time: Warranty module session
Context / Prompt: Building WarrantyRepository and WarrantyService, needing to resolve Sale references during CSV loading, but SaleService still had no findSaleById method.
AI Response / Advice: Reused the same pattern applied in the Return module: resolving the sale lookup inside my own repository and service classes by iterating getAllSales() and comparing IDs as strings.
Decision Taken: Kept the lookup logic within my own classes, confirmed BUILD SUCCESS for the full project after adding assignBasicWarranty, assignExtendedWarranty, findWarrantyByProduct, listAllWarranties, listActiveWarranties, and listWarrantiesExpiringSoon, and updated docs/layers-diagram.md to include the new Warranty hierarchy and its relationships.

---

## Requerimiento 5 - System Integration

### Entry 1
Fecha: September 26, 2026
Herramienta: Claude (Anthropic)
Fase y rama: Fase 3 - fix/warranty-circular-dependency
Objetivo: Resolve the circular dependency SaleService -> WarrantyService -> WarrantyRepository -> SaleService described in adjustment A2.
Consulta: Shared the current WarrantyRepository.java, WarrantyService.java, and Main.java, asking how to break the cycle without losing the ability to resolve Sale references during warranty CSV loading.
Respuesta: Proposed replacing the WarrantyRepository/WarrantyService dependency on SaleService with a direct dependency on SaleRepository instead, since SaleRepository has no dependency on warranty-related classes. Also proposed removing the SaleService parameter from syncPastConsoleWarranties in favor of using the injected SaleRepository directly, and simplifying Main so SaleService is constructed only once.
Decision: Accepted the SaleRepository substitution in both WarrantyRepository and WarrantyService. Modified syncPastConsoleWarranties to use sale.getProducts() instead of sale.getItems() to match my own Sale class usage. Verified with mvn clean compile that the full project built successfully after each change.
Commit relacionado: b59d86a (fix: replace SaleService with SaleRepository in WarrantyRepository to break circular dependency), 035a48a (fix: update WarrantyService to depend on SaleRepository instead of SaleService), b9fbd5e (fix: simplify Main to construct SaleService once after resolving circular dependency)

### Entry 2
Fecha: September 26, 2026
Herramienta: Claude (Anthropic)
Fase y rama: Fase 3 - fix/warranty-circular-dependency
Objetivo: Document the dependency fix in docs/warranty-class-diagram.md as required by adjustment A2, since this file did not exist yet from Requirement 4.
Consulta: Asked to verify whether the file already existed before creating it, then to draft the diagram reflecting the corrected dependencies.
Respuesta: Confirmed via git log that the file had never been committed, then proposed a Mermaid class diagram showing the Warranty hierarchy plus WarrantyRepository and WarrantyService depending on SaleRepository (not SaleService), with a written explanation of the original cycle and the applied fix.
Decision: Accepted the diagram and explanation as drafted, reviewing it against the actual corrected code before committing.
Commit relacionado: b16cf58 (docs: create warranty class diagram reflecting the circular dependency fix)

### Entry 3
Fecha: September 27, 2026
Herramienta: Claude (Anthropic)
Fase y rama: Fase 4 - fix/return-accessory-stock
Objetivo: Fix adjustment A4 - returns of accessories were not restoring their stock, since ReturnService only called ProductService.restoreStock.
Consulta: Shared the current AccessoryService.java, ReturnRepository.java, and ReturnService.java, asking how to make returns work for both products and accessories without duplicating logic.
Respuesta: Proposed adding a restoreStock(String accessoryId, int quantity) method to AccessoryService mirroring the existing ProductService one, injecting AccessoryService into both ReturnRepository (to resolve accessories by ID as a fallback when a product ID is not found) and ReturnService (to check `instanceof Accessory` and delegate stock restoration to the correct service).
Decision: Accepted all three changes as proposed. Also had to update Main.java to pass accessoryService into the now four-parameter ReturnRepository and ReturnService constructors, since their signatures changed. Verified with mvn clean compile that the full project built successfully after each change.
Commit relacionado: 0b9cb0d (feat: add restoreStock method to AccessoryService), bf8c4f7 (fix: resolve accessories as returned items in ReturnRepository), 4b7384a (fix: delegate stock restoration to AccessoryService for returned accessories), bf6d0b1 (fix: pass AccessoryService to ReturnRepository and ReturnService in Main)

### Entry 4
Fecha: September 27, 2026
Herramienta: Claude (Anthropic)
Fase y rama: Fase 4 - fix/monthly-balance-report
Objetivo: Fix adjustment A6 - generateMonthlyBalance only returned the net balance, but the requirement needs total sales, total returns, and net balance shown separately.
Consulta: Confirmed A6 had no dependency on A5 (Developer 1's pending adjustment to Return.calculateRefundAmount) before starting, since A6 only touches ReturnService and reads Return.getRefundAmount() as a black box. Asked how to split the existing method without breaking its public signature.
Respuesta: Proposed extracting the sales-total loop into calculateMonthlySales(int, int) and the returns-total loop into calculateMonthlyReturns(int, int), then having generateMonthlyBalance(int, int) simply call both and subtract, preserving its original signature so no caller needs to change.
Decision: Accepted the split as proposed. Verified with mvn clean compile that the full project built successfully.
Commit relacionado: c72037d (fix: split monthly balance into separate sales and returns calculations)


## Phase 4 - feature/return-warranty-cancellation (A7)

### Entry: plan for cancelling warranties on returned consoles
- **Date:** 2026-09-28
- **Tool:** Claude
- **Phase and branch:** Phase 4 - feature/return-warranty-cancellation
- **Objective:** Understand what A7 requires and how to split it into atomic commits across the warranty, return model, persistence and service layers.
- **Query:** Asked how to implement WarrantyService.cancelWarranties and connect it to ReturnService.registerReturn, sharing the current Warranty, Return and ReturnService code.
- **Response:** Proposed cancelWarranties(productId, saleId) returning the refundable cost (zero for basic, additional cost for extended), a warrantyRefundAmount field in Return, persistence in returns.csv, and a call per returned console in registerReturn.
- **Decision:** Accepted the four-commit split. Kept the original 5-argument Return constructor delegating to the new 6-argument one so existing code keeps compiling. Ran mvn clean compile before every commit.
- **Related commit:** feat: add cancelWarranties to WarrantyService (6a73ae5); feat: include warranty refund in Return amount and receipt (8edd494)

### Entry: persistence and service integration for A7
- **Date:** 2026-09-28
- **Tool:** Claude
- **Phase and branch:** Phase 4 - feature/return-warranty-cancellation
- **Objective:** Persist the warranty refund without breaking the existing data/returns.csv, and wire WarrantyService into ReturnService and Main.
- **Query:** Shared ReturnRepository.java and main.java and asked for the remaining changes.
- **Response:** Suggested a seventh CSV column read as 0.0 when missing, and passing WarrantyService to the ReturnService constructor, adjusting main.java in the same commit so the project still compiles.
- **Decision:** Accepted both. Verified with mvn clean compile after each change. Known limitation understood: cancelWarranties removes every warranty of that product in that sale, even for units that were not returned.
- **Related commit:** feat: persist warranty refund amount in ReturnRepository (813deec); feat: cancel warranties of returned consoles in ReturnService (89a0381)
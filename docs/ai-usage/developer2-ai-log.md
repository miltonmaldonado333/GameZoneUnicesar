# AI Usage Log - Developer 2

**Author:** Luis Manuel Corzo Castro - 1066870055
**Role:** Developer 2 (persistence and service layers)
**Tool used in every entry:** Claude (Anthropic)
**Modules:** Person (model, persistence, service), Accessory, Promotion, Return and Warranty (persistence, service), plus the Requirement 5 adjustments and the Requirement 6 refactor.

---

## Taller 1 and Requirements 1-4

Each entry: problem, how the AI helped, and my decision.

1. **Environment and Person model.** Problem: first-time setup of JDK 17, Maven and Git, and drafting Person, Client and Seller from docs/class-diagram.md. AI: guided the installation and drafted the classes with JavaDoc. Decision: checked every command and class against the diagram before compiling; one class per commit.
2. **Person persistence and service.** Problem: implement PersonRepository, FilePersonRepository and PersonService. AI: proposed a semicolon-separated file format and a service that depends on the repository interface. Decision: kept the format, verified the interface injection, committed each layer separately.
3. **Accessory: typo in a teammate's file.** Problem: SaleService referenced AccesoryService. AI: identified the typo as the Leader's and advised a find-and-replace plus notifying him. Decision: confirmed my own names were correct, fixed it and reported it.
4. **Accessory: overloads and missing declaration.** Problem: Main and GameZoneUI called my classes with different signatures, then "class expected" errors. AI: suggested a no-argument constructor and short register overloads, and found the missing class declaration. Decision: applied both and confirmed mvn clean compile.
5. **Promotion: constructor order.** Problem: the teammate's constructors put the specific attributes first, unlike what I had assumed. AI: flagged the mismatch and noted Promotion.java lacked real getters and setters. Decision: added them and corrected all constructor calls.
6. **Promotion: branch isolation.** Problem: 16 compilation errors from classes not yet in my branch. AI: had me inspect git log and branches; the branch predated the Accessory merge. Decision: merged my Accessory branch into it and reported the remaining Sale errors as outside my scope.
7. **Return: missing findSaleById.** Problem: SaleService had no lookup by id. AI: proposed resolving it inside my own classes by iterating getAllSales(). Decision: kept the lookup in my classes instead of asking for changes outside my scope.
8. **Return: BOM error.** Problem: illegal character errors after creating files from PowerShell. AI: identified a Byte Order Mark and gave a script to rewrite the files without it. Decision: applied it, confirmed BUILD SUCCESS, committed it separately.
9. **Layers diagram.** Problem: creating and updating docs/layers-diagram.md for each module, once with duplicated merge content. AI: drafted the Mermaid diagram and rewrote it cleanly when duplication appeared. Decision: verified class names against the real files before each commit.
10. **Git flow.** Problem: commit granularity and an oversized pushed commit. AI: explained the risk of rewriting shared history and recommended smaller commits from then on. Decision: kept the shared history and pushed atomic commits immediately.
11. **Warranty: sale lookup.** Problem: resolving Sale references while loading warranties. AI: reused the Return pattern. Decision: kept the lookup in my own classes and confirmed BUILD SUCCESS after the six service methods; updated the layers diagram.

---

## Requirement 5 - System Integration

### A2 - fix/warranty-circular-dependency
- Fecha: 2026-09-26
- Herramienta: Claude (Anthropic)
- Fase y rama: Fase 3 - fix/warranty-circular-dependency
- Objetivo: break the cycle SaleService -> WarrantyService -> WarrantyRepository -> SaleService and document it.
- Consulta: shared WarrantyRepository, WarrantyService and Main; later asked whether docs/warranty-class-diagram.md already existed.
- Respuesta: replace the dependency on SaleService with SaleRepository, drop the SaleService parameter of syncPastConsoleWarranties, build SaleService once in Main; confirmed the diagram file had never been committed and drafted it.
- Decision: accepted, using sale.getProducts() to match my Sale class; verified with mvn clean compile after each change.
- Commit relacionado: b59d86a, 035a48a, b9fbd5e, b16cf58

### A4 - fix/return-accessory-stock
- Fecha: 2026-09-27
- Herramienta: Claude (Anthropic)
- Fase y rama: Fase 4 - fix/return-accessory-stock
- Objetivo: restore the stock of returned accessories.
- Consulta: shared AccessoryService, ReturnRepository and ReturnService.
- Respuesta: add restoreStock to AccessoryService and inject AccessoryService into ReturnRepository and ReturnService, delegating by item type.
- Decision: accepted all three changes and updated Main for the new constructors; mvn clean compile after each.
- Commit relacionado: 0b9cb0d, bf8c4f7, 4b7384a, bf6d0b1

### A6 - fix/monthly-balance-report
- Fecha: 2026-09-27
- Herramienta: Claude (Anthropic)
- Fase y rama: Fase 4 - fix/monthly-balance-report
- Objetivo: show total sales, total returns and net balance separately.
- Consulta: confirmed A6 does not depend on A5 and asked how to split the method without changing its signature.
- Respuesta: extract calculateMonthlySales and calculateMonthlyReturns; generateMonthlyBalance calls both and subtracts.
- Decision: accepted; mvn clean compile passed.
- Commit relacionado: c72037d

### A7 - feature/return-warranty-cancellation
- Fecha: 2026-09-28
- Herramienta: Claude (Anthropic)
- Fase y rama: Fase 4 - feature/return-warranty-cancellation
- Objetivo: cancel the warranties of returned consoles and include their refundable cost in the refund.
- Consulta: shared the Warranty, Return and ReturnService code, then ReturnRepository and Main.
- Respuesta: cancelWarranties(productId, saleId) returns 0 for basic and the additional cost for extended; Return gets a warrantyRefundAmount field; a seventh CSV column read as 0.0 when missing; WarrantyService passed to ReturnService and Main in the same commit.
- Decision: accepted, keeping the 5-argument Return constructor. Tested in the app: the receipt showed the warranty refund, the warranty disappeared from the list and returns.csv stored 200.0. Known limitation: it removes every warranty of that product in that sale, even for units not returned.
- Commit relacionado: 6a73ae5, 8edd494, 813deec, 89a0381, b7cd207

---

## Requirement 6 - Exceptions and Validations

### feature/exception-handling
- Fecha: 2026-10-16
- Herramienta: Claude (Anthropic)
- Fase y rama: Requirement 6 - feature/exception-handling
- Objetivo: rethrow IOException as PersistenceException in the repositories and delegate the service validations to the new validators.
- Consulta: shared Developer 1's exceptions and validators and the current three repositories and three services.
- Respuesta: wrap each load and save in PersistenceException with the file name and operation; call the validators from the services; keep the find methods returning null and the boolean result of registerVideoGame and registerConsole.
- Decision: accepted that criterion, replacing whole files and running mvn clean compile before each commit. Understood the behaviour changes: read errors now propagate instead of printing a message, and invalid client, product or sale data now fails with typed exceptions.
- Commit relacionado: f62518a, 0b9f699, 5906ba3, 814bfcd, 702d863, 091ae94
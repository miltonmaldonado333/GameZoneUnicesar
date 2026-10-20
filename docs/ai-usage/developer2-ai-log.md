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

---

## Requirement 7 - GRASP Audit and Correction

### Phase 1 - Audit (feature/grasp-audit)
- Fecha: 2026-10-18
- Herramienta: Claude (Anthropic)
- Fase y rama: Requirement 7, Phase 1 - feature/grasp-audit
- Objetivo: audit my modules (persons, accessories, promotions, returns, warranties and the Requirement 6 usage of exceptions and validators) against the nine GRASP patterns, and add my findings to the consolidated audit.
- Consulta: shared the assignment, the other two reports and my classes read from origin/develop.
- Respuesta: a verdict per pattern, ten violations (V-01 to V-10) and six out-of-scope observations (F-01 to F-06). In the consolidated audit they became V-15 to V-22 plus extensions of V-04 and V-05, keeping the IDs already used by the other two reports.
- Decision: audited origin/develop at 336790c, not the branch, and checked every cited line with git grep before each commit. Did not report the instanceof in toLine/parseLine, because serialization must stay in persistence. The branch also carried code commits of the Technical Leader and had emptied the AI logs, so I restored the logs from develop and he reverted his two commits without force push.
- Commit relacionado: 8facee7, 7c6dc0a, 4cfd9b4, 8214835, 0a68d7c, bfe14dd, eeff611, 28cfe7a, 6438fe6, 27849ef, c6c147a, dcb8c43, 2034b67, f80c1b8, add328a, 9e8753a, 0f254e4

### Phase 2 - Correction (feature/grasp-refactor)

#### Entry 1 - Corrections that did not need other integrantes
- Fecha: 2026-10-18 to 2026-10-19
- Herramienta: Claude (Anthropic)
- Fase y rama: Requirement 7, Phase 2 - feature/grasp-refactor
- Objetivo: correct my violations of the consolidated audit that did not depend on other integrantes.
- Consulta: shared my classes, the consolidated rows and the branch log, and asked for each correction.
- Respuesta: remove the unused ProductService from WarrantyService (V-22); PromotionValidator for the category check (V-17); Product.reduceStock and addStock in AccessoryService (V-15); getCategory() instead of instanceof (V-16); ReturnValidator for the return checks (V-18); SaleService.calculateMonthlyTotal (V-19); one shared sale lookup replacing three copies (V-20).
- Decision: kept the exception types, messages and results of each method; counted only refactor commits as corrections; edited SaleService and SaleRepository with the Technical Leader's authorization; ran mvn clean compile before each commit.
- Commit relacionado: 0471c4d, dea802a, 75571df, f822d12, 7745bb0, d56cde0, fcf0a50, 28b14ce, 013f9da

#### Entry 2 - V-05, warranty part
- Fecha: 2026-10-19
- Herramienta: Claude (Anthropic)
- Fase y rama: Requirement 7, Phase 2 - feature/grasp-refactor
- Objetivo: remove the instanceof Console tests from ReturnService and WarrantyService.
- Consulta: shared Developer 1's commit fa51543 with Product.hasWarrantyCoverage().
- Respuesta: replace both tests with the polymorphic query, which is false by default and true only in Console.
- Decision: behaviour unchanged because Console is the only class that answers true. The instanceof Accessory that chooses the stock service was not touched, because the common stock contract does not exist yet, so V-05 stays partially corrected.
- Commit relacionado: e3a3db2, 50eff64

#### Entry 3 - V-04 and V-21
- Fecha: 2026-10-19 to 2026-10-20
- Herramienta: Claude (Anthropic)
- Fase y rama: Requirement 7, Phase 2 - feature/grasp-refactor
- Objetivo: stop ReturnRepository and WarrantyRepository from depending on services, and extract the repository interfaces.
- Consulta: shared the repositories, the services and Main, and asked for a mechanism that rebuilds objects in the service layer.
- Respuesta: raw ReturnRecord and WarrantyRecord loaded by the repositories and rebuilt by the services (V-04); an interface per repository with the implementation renamed to File...Repository, including PromotionService because the consolidated audit lists four services (V-21).
- Decision: the warranty product is taken from its sale, so V-22 stays valid; records without a sale are still skipped; each commit changes one line of Main. Every commit compiled, but the menu was not run, so the equivalence of behaviour was argued by reading the code.
- Commit relacionado: a0d84b8, 4577aa1, 54657a0, 590d72c, 677abad, 93af52f, b435f9c
# AI Usage Log - Developer 2

**Author:** Luis Manuel Corzo Castro - 1066870055
**Role:** Developer 2 (persistence and service layers)
**Tool:** Claude (Anthropic), unless an entry says otherwise.
**Modules:** Person (model, persistence, service), Accessory, Promotion, Return and Warranty (persistence, service), the Requirement 5 adjustments, the Requirement 6 refactor and the Requirement 7 and 8 audits and corrections.

Entry format: date, phase and branch, what I consulted, what the AI answered or what I understood, my decision, and related commits. Dates of entries 1 to 11 are taken from the dates of the related commits.

---

## Taller 1 and Requirements 1-4

### 1. Environment and Person model
- **Date:** 2026-09-09
- **Phase / branch:** Taller 1 - feature/person-module
- **Consulted:** first-time setup of JDK 17, Maven and Git, and drafting Person, Client and Seller from docs/class-diagram.md.
- **AI response:** guided the installation and drafted the classes with JavaDoc.
- **Decision:** I checked every command and class against the diagram before compiling; one class per commit.
- **Commits:** ae2d22f, 8480650, cc0023f

### 2. Person persistence and service
- **Date:** 2026-09-09
- **Phase / branch:** Taller 1 - feature/person-module
- **Consulted:** how to implement PersonRepository, FilePersonRepository and PersonService.
- **AI response:** proposed a semicolon-separated file format and a service that depends on the repository interface.
- **Decision:** I kept the format, verified the interface injection and committed each layer separately.
- **Commits:** 07b2e68, 3e38b3d, 9dd03ee

### 3. Accessory: typo in a teammate's file
- **Date:** 2026-09-23
- **Phase / branch:** Requirement 1 - feature/accesory-module
- **Consulted:** SaleService referenced AccesoryService and the build failed.
- **AI response:** identified the typo as the Leader's and advised a find-and-replace plus notifying him.
- **Decision:** I confirmed my own class names were correct, fixed it and reported it to the Leader.
- **Commits:** 60fbc9c

### 4. Accessory: overloads and missing class declaration
- **Date:** 2026-09-23
- **Phase / branch:** Requirement 1 - feature/accesory-module
- **Consulted:** Main and GameZoneUI called my classes with different signatures, then "class expected" errors appeared.
- **AI response:** suggested a no-argument constructor and short register overloads, and found the missing class declaration.
- **Decision:** I applied both and confirmed mvn clean compile.
- **Commits:** 60fbc9c

### 5. Promotion: constructor order
- **Date:** 2026-09-23
- **Phase / branch:** Requirement 2 - feature/promotion-module
- **Consulted:** my teammate's constructors put the specific attributes first, unlike what I had assumed.
- **AI response:** flagged the mismatch and noted Promotion.java lacked real getters and setters.
- **Decision:** I added them and corrected all constructor calls.
- **Commits:** aaf85c6

### 6. Promotion: branch isolation
- **Date:** 2026-09-23
- **Phase / branch:** Requirement 2 - feature/promotion-module
- **Consulted:** 16 compilation errors from classes not yet in my branch.
- **AI response:** had me inspect git log and the branches; the branch predated the Accessory merge.
- **Decision:** I merged my Accessory branch into it and reported the remaining Sale errors as outside my scope.

### 7. Return: missing findSaleById
- **Date:** 2026-09-24
- **Phase / branch:** Requirement 3 - feature/return-module
- **Consulted:** SaleService had no lookup by id.
- **AI response:** proposed resolving it inside my own classes by iterating getAllSales().
- **Decision:** I kept the lookup in my classes instead of asking for changes outside my scope.
- **Commits:** aac3c86, 82663ae, b15efa1, e7fcf6b

### 8. Return: BOM error
- **Date:** 2026-09-24
- **Phase / branch:** Requirement 3 - feature/return-module
- **Consulted:** illegal character errors after creating files from PowerShell.
- **AI response:** identified a Byte Order Mark and gave a script to rewrite the files without it.
- **Decision:** I applied it, confirmed BUILD SUCCESS and committed it separately.
- **Commits:** 6c7626a

### 9. Layers diagram
- **Date:** 2026-09-23 to 2026-09-25
- **Phase / branch:** Requirements 1-4
- **Consulted:** creating and updating docs/layers-diagram.md for each module, once with duplicated merge content.
- **AI response:** drafted the Mermaid diagram and rewrote it cleanly when the duplication appeared.
- **Decision:** I verified class names against the real files before each commit.
- **Commits:** 49a8239, 26ce08d, cb72d25, ecf3dd5

### 10. Git flow
- **Date:** 2026-09-23
- **Phase / branch:** Requirement 1
- **Consulted:** commit granularity and an oversized commit already pushed.
- **AI response:** explained the risk of rewriting shared history and recommended smaller commits from then on.
- **Decision:** I kept the shared history and pushed atomic commits immediately.

### 11. Warranty: sale lookup
- **Date:** 2026-09-25
- **Phase / branch:** Requirement 4 - feature/warranty-module
- **Consulted:** resolving Sale references while loading warranties.
- **AI response:** reused the pattern of the Return module.
- **Decision:** I kept the lookup in my own classes, confirmed BUILD SUCCESS after the six service methods and updated the layers diagram.
- **Commits:** f5e2975, e298845, 5f2a15d, f67f138, a84889d, e227b51

---

## Requirement 5 - System Integration

### A2 - fix/warranty-circular-dependency
- **Date:** 2026-09-26
- **Phase / branch:** Phase 3 - fix/warranty-circular-dependency
- **Consulted:** how to break the cycle SaleService -> WarrantyService -> WarrantyRepository -> SaleService, sharing WarrantyRepository, WarrantyService and Main; later whether docs/warranty-class-diagram.md already existed.
- **AI response:** replace the dependency on SaleService with SaleRepository, drop the SaleService parameter of syncPastConsoleWarranties and build SaleService once in Main; confirmed the diagram file had never been committed and drafted it.
- **Decision:** I accepted it, using sale.getProducts() to match my Sale class, and verified with mvn clean compile after each change.
- **Commits:** b59d86a, 035a48a, b9fbd5e, b16cf58

### A4 - fix/return-accessory-stock
- **Date:** 2026-09-27
- **Phase / branch:** Phase 4 - fix/return-accessory-stock
- **Consulted:** how to restore the stock of returned accessories, sharing AccessoryService, ReturnRepository and ReturnService.
- **AI response:** add restoreStock to AccessoryService and inject AccessoryService into ReturnRepository and ReturnService, delegating by item type.
- **Decision:** I accepted all three changes and updated Main for the new constructors; mvn clean compile after each.
- **Commits:** 0b9cb0d, bf8c4f7, 4b7384a, bf6d0b1

### A6 - fix/monthly-balance-report
- **Date:** 2026-09-27
- **Phase / branch:** Phase 4 - fix/monthly-balance-report
- **Consulted:** whether A6 depends on A5, and how to show total sales, total returns and net balance separately without changing the method signature.
- **AI response:** A6 does not depend on A5; extract calculateMonthlySales and calculateMonthlyReturns, and let generateMonthlyBalance call both and subtract.
- **Decision:** I accepted it; mvn clean compile passed.
- **Commits:** c72037d

### A7 - feature/return-warranty-cancellation
- **Date:** 2026-09-28
- **Phase / branch:** Phase 4 - feature/return-warranty-cancellation
- **Consulted:** how to cancel the warranties of returned consoles and include their refundable cost in the refund, sharing the Warranty, Return, ReturnService, ReturnRepository and Main code.
- **AI response:** cancelWarranties(productId, saleId) returns 0 for basic and the additional cost for extended; Return gets a warrantyRefundAmount field; a seventh CSV column read as 0.0 when missing; WarrantyService passed to ReturnService and Main in the same commit.
- **Decision:** I accepted it, keeping the 5-argument Return constructor. I tested it in the application: the receipt showed the warranty refund, the warranty disappeared from the list and returns.csv stored 200.0. Known limitation: it removes every warranty of that product in that sale, even for units not returned.
- **Commits:** 6a73ae5, 8edd494, 813deec, 89a0381, b7cd207

---

## Requirement 6 - Exceptions and Validations

### feature/exception-handling
- **Date:** 2026-10-16
- **Phase / branch:** Requirement 6 - feature/exception-handling
- **Consulted:** how to rethrow IOException as PersistenceException in the repositories and delegate the service validations to the new validators, sharing Developer 1's exceptions and validators and the current repositories and services.
- **AI response:** wrap each load and save in PersistenceException with the file name and operation; call the validators from the services; keep the find methods returning null and the boolean result of registerVideoGame and registerConsole. The AI wrote the replacement files.
- **Decision:** I accepted that criterion, replaced whole files and ran mvn clean compile before each commit. I understood the behaviour changes: read errors now propagate instead of printing a message, and invalid client, product or sale data now fails with typed exceptions.
- **Commits:** f62518a, 0b9f699, 5906ba3, 814bfcd, 702d863, 091ae94

---

## Requirement 7 - GRASP Audit and Correction

### Phase 1 - Audit (feature/grasp-audit)
- **Date:** 2026-10-18
- **Phase / branch:** Requirement 7, Phase 1 - feature/grasp-audit
- **Consulted:** how to audit my modules (persons, accessories, promotions, returns, warranties and the Requirement 6 usage of exceptions and validators) against the nine GRASP patterns, and how to add my findings to the consolidated audit, sharing the assignment, the other two reports and my classes read from origin/develop.
- **AI response:** a verdict per pattern, ten violations (V-01 to V-10) and six out-of-scope observations (F-01 to F-06). In the consolidated audit they became V-15 to V-22 plus extensions of V-04 and V-05, keeping the IDs already used by the other two reports.
- **Decision:** I audited origin/develop at 336790c, not the branch, and checked every cited line with git grep before each commit. I did not report the instanceof in toLine and parseLine, because serialization must stay in persistence. The branch also carried code commits of the Technical Leader and had emptied the AI logs, so I restored the logs from develop and he reverted his two commits without force push.
- **Commits:** 8facee7, 7c6dc0a, 4cfd9b4, 8214835, 0a68d7c, bfe14dd, eeff611, 28cfe7a, 6438fe6, 27849ef, c6c147a, dcb8c43, 2034b67, f80c1b8, add328a, 9e8753a, 0f254e4

### Phase 2 - Correction (feature/grasp-refactor)

#### Entry 1 - Corrections that did not need other integrantes
- **Date:** 2026-10-18 to 2026-10-19
- **Phase / branch:** Requirement 7, Phase 2 - feature/grasp-refactor
- **Consulted:** how to correct my violations of the consolidated audit that did not depend on other integrantes, sharing my classes, the consolidated rows and the branch log.
- **AI response:** remove the unused ProductService from WarrantyService (V-22); PromotionValidator for the category check (V-17); Product.reduceStock and addStock in AccessoryService (V-15); getCategory() instead of instanceof (V-16); ReturnValidator for the return checks (V-18); SaleService.calculateMonthlyTotal (V-19); one shared sale lookup replacing three copies (V-20).
- **Decision:** I kept the exception types, messages and results of each method; counted only refactor commits as corrections; edited SaleService and SaleRepository with the Technical Leader's authorization; ran mvn clean compile before each commit.
- **Commits:** 0471c4d, dea802a, 75571df, f822d12, 7745bb0, d56cde0, fcf0a50, 28b14ce, 013f9da

#### Entry 2 - V-05, warranty part
- **Date:** 2026-10-19
- **Phase / branch:** Requirement 7, Phase 2 - feature/grasp-refactor
- **Consulted:** how to remove the instanceof Console tests from ReturnService and WarrantyService, sharing Developer 1's commit fa51543 with Product.hasWarrantyCoverage().
- **AI response:** replace both tests with the polymorphic query, which is false by default and true only in Console.
- **Decision:** behaviour is unchanged because Console is the only class that answers true. The instanceof Accessory that chooses the stock service was not touched, because the common stock contract does not exist yet, so V-05 stays partially corrected.
- **Commits:** e3a3db2, 50eff64

#### Entry 3 - V-04 and V-21
- **Date:** 2026-10-19 to 2026-10-20
- **Phase / branch:** Requirement 7, Phase 2 - feature/grasp-refactor
- **Consulted:** how to stop ReturnRepository and WarrantyRepository from depending on services and how to extract the repository interfaces, sharing the repositories, the services and Main.
- **AI response:** raw ReturnRecord and WarrantyRecord loaded by the repositories and rebuilt by the services (V-04); an interface per repository with the implementation renamed to File...Repository, including PromotionService because the consolidated audit lists four services (V-21).
- **Decision:** the warranty product is taken from its sale, so V-22 stays valid; records without a sale are still skipped; each commit changes one line of Main. Every commit compiled, but the menu was not run, so the equivalence of behaviour was argued by reading the code.
- **Commits:** a0d84b8, 4577aa1, 54657a0, 590d72c, 677abad, 93af52f, b435f9c

---

## Requirement 8 - SOLID Audit and Correction

### Phase 1 - Audit (feature/solid-audit)

#### Entry 1 - Scope of the AI policy and state of the branches
- **Phase / branch:** Requirement 8, Phase 1 - feature/solid-audit
- **Consulted:** what the AI policy of the new requirement allows, after sharing the requirement text, and whether Requirements 6 and 7 were closed before starting.
- **AI response:** explained that in Phase 1 it would not identify SOLID violations, write the audit report or answer the guiding questions, and offered conceptual explanations, Git help and compiler errors instead. It also read the output of git fetch --prune, git branch -r and git log origin/develop --merges, which showed the pull requests of Requirements 6 and 7 merged and only develop, main and feature/solid-audit left in the remote.
- **Decision:** I did not ask the AI for violations or for the report, and I started the audit from the state of develop.

#### Entry 2 - Individual audit
The analysis of my assigned modules was carried out individually, taking the five SOLID principles and the code in origin/develop, at commit 5f20ee1, as the reference. Identifying the possible violations, evaluating the principles and deciding the content of the audit report were my own responsibility. I took into account the requirements of each module and the limits of the assigned scope. No code was modified during this phase.
- **Commits:** dd56a96, ec0c98b, b7dd342, d16f150, d0c90d7, 180aa7c

### Phase 2 - Correction (feature/solid-refactor)

#### Entry 1 - Corrections of the consolidated audit
During this phase I used AI tools as support to understand the technical corrections, resolve implementation doubts and evaluate refactoring alternatives. The modifications were applied according to the findings of the consolidated audit, reviewing the proposals and checking the compilation after the changes. The final decisions about the modifications, the tests performed and the results obtained were documented together with the corresponding commits.
- **Commits:** 8c27dfa to 0e91f02 (violations V-DEV2-01 to V-DEV2-07 and their entries in the refactor log)
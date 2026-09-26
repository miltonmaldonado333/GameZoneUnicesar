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

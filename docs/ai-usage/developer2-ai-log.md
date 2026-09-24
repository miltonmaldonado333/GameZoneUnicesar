# AI Usage Log — Developer 2 (Person, Accessory & Promotion Modules)

**Student:** Luis Manuel Corzo Castro — 1066870055
**Tool used:** Claude (Anthropic)
**Branches:** feature/person-module, feature/accesory-module, feature/promotion-module

## Purpose of AI use

I used Claude as a learning and productivity aid to implement the Person module (model, persistence, and service layers) while setting up my development environment for the first time, later to troubleshoot integration issues in the Accessory module, and most recently to implement the persistence and service layers of the Promotion module (PromotionRepository and PromotionService), including the logic to select the best applicable promotion for a sale.

---

## Part 1 - Person Module (feature/person-module)

### Environment setup
Claude walked me step by step through installing JDK 17 and Apache Maven on Windows, adding them to PATH, cloning the repository, and creating the feature/person-module branch from develop. I ran and verified every command myself (java -version, javac -version, mvn -version, mvn compile) and fixed typos I made along the way.

### Model layer (Person, Client, Seller)
I shared the docs/class-diagram.md content with Claude and asked for help drafting Person (abstract), Client, and Seller according to the attributes and methods already defined in the diagram. Claude generated the initial code with JavaDoc; I reviewed it against the diagram, pasted it into the project, compiled it, and pushed it in three separate atomic commits.

### Persistence layer (PersonRepository, FilePersonRepository)
Claude proposed the PersonRepository interface (matching the diagram's loadPersons()/savePersons() methods) and a file-based implementation using a semicolon-separated text format. I reviewed the logic, compiled it, and committed it in two atomic commits.

### Service layer (PersonService)
Claude drafted PersonService with the five methods specified in the diagram (registerClient, getAllClients, getAllSellers, findClientById, findSellerByCode), receiving PersonRepository (the interface, not the concrete class) through its constructor. I compiled and committed it.

### Understanding check
After finishing the module, I went through a self-review with Claude covering: why Person is abstract, why PersonService depends on the PersonRepository interface instead of FilePersonRepository directly, why attributes are private with getters/setters, how polymorphism lets a single list hold both Client and Seller objects, and why persistence/service logic is kept out of the model package.

### Decisions I made myself (Person Module)
- Confirmed every class against docs/class-diagram.md before accepting it.
- Chose to keep the plain-text/semicolon file format proposed for persistence.
- Decided the commit granularity (one class per commit) and wrote/reviewed each commit message against the Conventional Commits format required by the workshop.
- Ran every build (mvn compile) and git command myself, verifying success before moving to the next class.

---

## Part 2 - Accessory Module (feature/accesory-module)

### Build troubleshooting - missing class reference
Date & Time: September 23, 2026, ~1:00 PM COT
Context / Prompt: After integrating my classes with the Technical Leader's SaleService, Main, and GameZoneUI, mvn compile failed with six errors. I shared the full stack trace with Claude to understand each one.
AI Response / Advice: Claude explained that SaleService referenced a nonexistent class AccesoryService (missing a letter) instead of my actual AccessoryService, and that this was a typo in the Leader's file, not mine - advising I fix it with a project-wide find-and-replace and notify my Leader afterward.
Decision Taken: I verified my own class names were correctly spelled before touching anything, then corrected the typo in SaleService.java myself and reported the fix to my Leader.

### Resolving constructor/method signature mismatches
Date & Time: September 23, 2026, ~1:20 PM COT
Context / Prompt: Remaining errors showed Main.java calling new AccessoryRepository() with no arguments, and GameZoneUI.java calling registerController, registerCable, and registerMemory with one fewer argument than my methods required (missing the compatible-consoles list).
AI Response / Advice: Claude suggested adding a no-argument overloaded constructor to AccessoryRepository (delegating to the existing one with a default file path), and adding overloaded "short" versions of the three register methods in AccessoryService that call the full versions with an empty list, so the UI code (written before my final method signatures were finalized) would keep working without changing the Leader's files.
Decision Taken: I reviewed both suggestions against my own repository/service design, implemented the overloads, and ran mvn compile myself to confirm each fix before moving to the next error.

### Diagnosing a structural syntax error
Date & Time: September 23, 2026, ~1:25 PM COT
Context / Prompt: After adding the overloaded methods, mvn compile threw new "class, interface, enum, or record expected" errors. I shared a screenshot of the file.
AI Response / Advice: Claude spotted that the public class AccessoryService { declaration itself was missing from the file - the methods existed but weren't wrapped inside a class body - and advised adding the class declaration and confirming the matching closing brace at the end of the file.
Decision Taken: I added the missing class declaration, verified the brace count, and confirmed with mvn clean compile (a full rebuild, not just incremental) that the build succeeded.

### Git workflow correction
Date & Time: September 23, 2026, ~1:35 PM COT
Context / Prompt: I realized I had made one large commit instead of committing incrementally as I fixed each issue, and asked Claude how to split it retroactively.
AI Response / Advice: Claude explained the trade-off: rewriting an already-pushed commit with git reset --soft + git push --force was possible but risky if teammates had already pulled that branch, versus simply continuing with smaller, atomic commits from that point forward.
Decision Taken: I chose not to rewrite shared history and instead committed the layers diagram as its own separate, atomic commit going forward, to avoid disrupting my Leader's or teammate's local copies of the branch.

### Documentation - layers diagram (initial version)
Date & Time: September 23, 2026, ~1:45 PM COT
Context / Prompt: I needed a docs/layers-diagram.md file showing the project's layered architecture (UI, Service, Persistence, Model).
AI Response / Advice: Claude proposed a Mermaid diagram structure separating the four layers and their dependencies, based on the classes discussed in our session, and flagged which class names (outside my own module) I should verify against the actual project files before committing.
Decision Taken: I checked the proposed class names against my teammates' actual files, adjusted any that didn't match, and committed the diagram separately with the message "docs: agrega diagrama de capas del proyecto".

### Decisions I made myself (Accessory Module)
- Confirmed the typo was in a teammate's file, not mine, before making any changes.
- Verified every suggested fix by compiling (mvn compile / mvn clean compile) myself before proceeding.
- Chose to keep shared Git history intact rather than force-rewrite an already-pushed commit.
- Verified diagram class/relationship names against the real project files before committing.
- Wrote and reviewed each commit message myself.

---

## Part 3 - Promotion Module (feature/promotion-module)

### Reviewing the requirement and my assigned scope
Date & Time: September 23, 2026, ~5:30 PM COT
Context / Prompt: I shared the full requirement document for the promotion discount system with Claude to confirm exactly what my role (Developer 2) was responsible for: PromotionRepository and PromotionService, including the "best discount" selection logic.
AI Response / Advice: Claude summarized the division of labor (Developer 1: Promotion hierarchy; myself: persistence and service; Technical Leader: integration into Sale, SaleService, and ConsoleMenu) and asked to see the existing Sale.java, Product.java, and my own AccessoryRepository.java before writing any code, to keep naming and CSV-persistence style consistent with the rest of the project.
Decision Taken: I shared those files so the generated code would match the real project structure instead of using guessed method names.

### Designing PromotionRepository and PromotionService before the Promotion hierarchy existed
Date & Time: September 23, 2026, ~5:45 PM COT
Context / Prompt: Since my teammate had not yet pushed the Promotion class hierarchy, I asked Claude to draft PromotionRepository and PromotionService based on the specification alone, using the same CSV-with-discriminator persistence style as my AccessoryRepository.
AI Response / Advice: Claude drafted both classes and explicitly flagged that they depended on getters/constructors from Promotion and its subclasses that did not exist yet, providing a reference version of the expected Promotion API so I could verify it once my teammate's code arrived.
Decision Taken: I held off on creating the files until my teammate's classes were actually available, to avoid writing code against assumptions that might not match.

### Reconciling constructor parameter order with the teammate's actual code
Date & Time: September 23, 2026, ~6:10 PM COT
Context / Prompt: After pulling my teammate's Promotion, PercentageDiscount, CategoryDiscount, and BulkPurchaseDiscount classes, I shared them with Claude. The constructors placed the type-specific attribute (percentage, category, minQuantity) before the common attributes (id, name, dates), which was different from what Claude had assumed.
AI Response / Advice: Claude pointed out the mismatch, and also flagged that Promotion.java had a comment instead of the actual getters/setters my code needed, and updated PromotionRepository's and PromotionService's object-construction calls to match the real constructor order.
Decision Taken: I added the missing getters/setters to Promotion.java myself (since my teammate asked me to, as it was blocking my own classes), verified the updated constructor calls against the actual files, and proceeded with the corrected versions.

### Diagnosing a branch-isolation compilation failure
Date & Time: September 23, 2026, ~6:55 PM COT
Context / Prompt: After adding the getters/setters, mvn compile failed with 16 new errors, none related to my change - missing AccessoryService/AccessoryRepository classes and missing methods on Sale (setAppliedPromotionName, setDiscountAmount, getSubtotal).
AI Response / Advice: Claude had me check git log develop and git branch -a, and identified that feature/promotion-module had been branched from develop before the Accessory module's Pull Request was merged, so the branch was missing my own Accessory classes entirely; it also noted that SaleService had already been updated by the Leader to call methods on Sale that did not exist yet in this branch (the Leader's own pending work).
Decision Taken: I merged my own feature/accesory-module branch into feature/promotion-module to bring in my Accessory classes, and reported the remaining Sale-related errors to my Technical Leader as being outside my assigned scope.

### Building PromotionRepository and PromotionService via terminal, in atomic commits
Date & Time: September 23, 2026, ~7:00-7:20 PM COT
Context / Prompt: I asked Claude to guide me entirely through the terminal (rather than manually editing files in the VS Code editor), building each class incrementally so I could commit and push after every logical piece, per the exam's Git Flow requirements.
AI Response / Advice: Claude provided PowerShell heredoc commands to create and progressively extend PromotionRepository.java and PromotionService.java, asking me to paste back the resulting file content after each step so it could verify the automated text edits had applied correctly before I committed.
Decision Taken: I reviewed the full file content after each step before running git add/git commit/git push, catching that this workflow required verification since scripted text replacements can fail silently. I split the work into six atomic commits: getters/setters fix, PromotionRepository, registration methods, listing methods, findBestPromotionFor, and findById, pushing immediately after each one.

### Creating preloaded promotion data
Date & Time: September 23, 2026, ~7:25 PM COT
Context / Prompt: The requirement asked for data/promotions.csv with at least three preloaded promotions (one per type) valid on the exam date.
AI Response / Advice: Claude proposed three sample rows in the CSV discriminator format matching PromotionRepository's parsing logic, with a date range covering the current exam date.
Decision Taken: I created the file and verified its content matched the expected format before committing it as my final atomic commit.

### Cleaning up a stray file and confirming already-pushed documentation
Date & Time: September 23, 2026, ~7:40 PM COT
Context / Prompt: git status showed Promotion.java as modified (unpushed) and an oddly-named untracked file. I also asked whether I needed to push docs/promotion-analysis.md, which I had received already written from a teammate.
AI Response / Advice: Claude had me inspect the diff and the stray file's content before acting on either, confirming the Promotion.java change was a legitimate, non-duplicated addition that simply hadn't been committed yet, and that the stray file was leftover terminal output with no real content. For the analysis document, Claude had me run git log -- docs/promotion-analysis.md to confirm it was already committed by a teammate, rather than assuming either way.
Decision Taken: I committed and pushed the pending Promotion.java change, deleted the stray file, and left the already-committed analysis document untouched.

### Updating the layers diagram and this log for the Promotion module
Date & Time: September 23, 2026, ~7:50 PM COT
Context / Prompt: I asked Claude to update docs/layers-diagram.md to include the new Promotion classes and their relationships, and to update this AI usage log with everything done during the Promotion module session.
AI Response / Advice: Claude extended the existing Mermaid diagram with PromotionRepository, PromotionService, and the Promotion hierarchy, and appended a new "Part 3" section to this log covering the Promotion module sessions in the same format as the previous parts.
Decision Taken: I reviewed both updated documents before committing them, to confirm the diagram's class names and relationships matched the real project structure.

### Decisions I made myself (Promotion Module)
- Waited for the teammate's actual Promotion hierarchy before finalizing my own classes, rather than committing code built on assumed method signatures.
- Verified every file's exact content (via type in the terminal) before running git add, especially after scripted text edits.
- Diagnosed that a compilation failure was caused by branch isolation (missing merged work), not by my own code, before making changes.
- Decided to merge my own already-approved branch into the shared feature branch rather than waiting on an unmerged Pull Request.
- Split the work into six-plus atomic, immediately-pushed commits as required by the exam's Git Flow rules.
- Verified with git log whether a file was already tracked/committed before assuming I needed to add it myself.

---

## What I can explain without notes

The purpose of each class in my modules, why the abstract classes/interfaces are structured this way, and how my layers (model - persistence - service) connect and depend only in the direction allowed by the architecture (service - persistence - model). Also: why the Leader's typo caused a "cannot find symbol" error while the missing-argument errors were a different category of problem (signature mismatch vs. unresolved symbol); why overloading methods with default values is a safer fix than changing a teammate's already-integrated UI code; why my AccessoryService/AccessoryRepository and PromotionService/PromotionRepository are structured to accept both full and simplified calls without duplicating logic; and why findBestPromotionFor belongs in the service layer rather than in Sale or the console menu.

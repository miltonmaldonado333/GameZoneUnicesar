AI Usage Log — Developer 2 (Person & Accessory Modules)

Student: Luis Manuel Corzo Castro — 1066870055 Tool used: Claude (Anthropic) Branches: feature/person-module, feature/accesory-module

Purpose of AI use

I used Claude as a learning and productivity aid to implement the Person module (model, persistence, and service layers) while setting up my development environment for the first time, and later to troubleshoot integration issues in the Accessory module (model, persistence, and service layers) once my code was merged with the Technical Leader's SaleService, Main, and GameZoneUI.

Part 1 — Person Module (feature/person-module)
Environment setup

Claude walked me step by step through installing JDK 17 and Apache Maven on Windows, adding them to PATH, cloning the repository, and creating the feature/person-module branch from develop. I ran and verified every command myself (java -version, javac -version, mvn -version, mvn compile) and fixed typos I made along the way.

Model layer (Person, Client, Seller)

I shared the docs/class-diagram.md content with Claude and asked for help drafting Person (abstract), Client, and Seller according to the attributes and methods already defined in the diagram. Claude generated the initial code with JavaDoc; I reviewed it against the diagram, pasted it into the project, compiled it, and pushed it in three separate atomic commits.

Persistence layer (PersonRepository, FilePersonRepository)

Claude proposed the PersonRepository interface (matching the diagram's loadPersons()/savePersons() methods) and a file-based implementation using a semicolon-separated text format. I reviewed the logic, compiled it, and committed it in two atomic commits.

Service layer (PersonService)

Claude drafted PersonService with the five methods specified in the diagram (registerClient, getAllClients, getAllSellers, findClientById, findSellerByCode), receiving PersonRepository (the interface, not the concrete class) through its constructor. I compiled and committed it.

Understanding check

After finishing the module, I went through a self-review with Claude covering: why Person is abstract, why PersonService depends on the PersonRepository interface instead of FilePersonRepository directly, why attributes are private with getters/setters, how polymorphism lets a single list hold both Client and Seller objects, and why persistence/service logic is kept out of the model package.

Decisions I made myself (Person Module)
Confirmed every class against docs/class-diagram.md before accepting it.
Chose to keep the plain-text/semicolon file format proposed for persistence.
Decided the commit granularity (one class per commit) and wrote/reviewed each commit message against the Conventional Commits format required by the workshop.
Ran every build (mvn compile) and git command myself, verifying success before moving to the next class.
Part 2 — Accessory Module (feature/accesory-module)
Build troubleshooting — missing class reference

Date & Time: September 23, 2026, ~1:00 PM COT Context / Prompt: After integrating my classes with the Technical Leader's SaleService, Main, and GameZoneUI, mvn compile failed with six errors. I shared the full stack trace with Claude to understand each one. AI Response / Advice: Claude explained that SaleService referenced a nonexistent class AccesoryService (missing a letter) instead of my actual AccessoryService, and that this was a typo in the Leader's file, not mine — advising I fix it with a project-wide find-and-replace and notify my Leader afterward. Decision Taken: I verified my own class names were correctly spelled before touching anything, then corrected the typo in SaleService.java myself and reported the fix to my Leader.

Resolving constructor/method signature mismatches

Date & Time: September 23, 2026, ~1:20 PM COT Context / Prompt: Remaining errors showed Main.java calling new AccessoryRepository() with no arguments, and GameZoneUI.java calling registerController, registerCable, and registerMemory with one fewer argument than my methods required (missing the compatible-consoles list). AI Response / Advice: Claude suggested adding a no-argument overloaded constructor to AccessoryRepository (delegating to the existing one with a default file path), and adding overloaded "short" versions of the three register methods in AccessoryService that call the full versions with an empty list, so the UI code (written before my final method signatures were finalized) would keep working without changing the Leader's files. Decision Taken: I reviewed both suggestions against my own repository/service design, implemented the overloads, and ran mvn compile myself to confirm each fix before moving to the next error.

Diagnosing a structural syntax error

Date & Time: September 23, 2026, ~1:25 PM COT Context / Prompt: After adding the overloaded methods, mvn compile threw new "class, interface, enum, or record expected" errors. I shared a screenshot of the file. AI Response / Advice: Claude spotted that the public class AccessoryService { declaration itself was missing from the file — the methods existed but weren't wrapped inside a class body — and advised adding the class declaration and confirming the matching closing brace at the end of the file. Decision Taken: I added the missing class declaration, verified the brace count, and confirmed with mvn clean compile (a full rebuild, not just incremental) that the build succeeded.

Git workflow correction

Date & Time: September 23, 2026, ~1:35 PM COT Context / Prompt: I realized I had made one large commit instead of committing incrementally as I fixed each issue, and asked Claude how to split it retroactively. AI Response / Advice: Claude explained the trade-off: rewriting an already-pushed commit with git reset --soft + git push --force was possible but risky if teammates had already pulled that branch, versus simply continuing with smaller, atomic commits from that point forward. Decision Taken: I chose not to rewrite shared history and instead committed the layers diagram as its own separate, atomic commit going forward, to avoid disrupting my Leader's or teammate's local copies of the branch.

Documentation — layers diagram

Date & Time: September 23, 2026, ~1:45 PM COT Context / Prompt: I needed a docs/layers-diagram.md file showing the project's layered architecture (UI, Service, Persistence, Model). AI Response / Advice: Claude proposed a Mermaid diagram structure separating the four layers and their dependencies, based on the classes discussed in our session, and flagged which class names (outside my own module) I should verify against the actual project files before committing. Decision Taken: I checked the proposed class names against my teammates' actual files, adjusted any that didn't match, and committed the diagram separately with the message docs: agrega diagrama de capas del proyecto.

Decisions I made myself (Accessory Module)
Confirmed the typo was in a teammate's file, not mine, before making any changes.
Verified every suggested fix by compiling (mvn compile / mvn clean compile) myself before proceeding.
Chose to keep shared Git history intact rather than force-rewrite an already-pushed commit.
Verified diagram class/relationship names against the real project files before committing.
Wrote and reviewed each commit message myself.
What I can explain without notes

The purpose of each class in my modules, why the abstract classes/interfaces are structured this way, and how my layers (model → persistence → service) connect and depend only in the direction allowed by the architecture (service → persistence → model). Also: why the Leader's typo caused a "cannot find symbol" error while the missing-argument errors were a different category of problem (signature mismatch vs. unresolved symbol); why overloading methods with default values is a safer fix than changing a teammate's already-integrated UI code; and why my AccessoryService and AccessoryRepository are structured to accept both full and simplified calls without duplicating logic.
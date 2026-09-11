AI Usage Log — Developer 2 (Person Module)

Student: Luis Manuel Corzo Castro — 1066870055 Tool used: Claude (Anthropic)

Purpose of AI use

I used Claude as a learning and productivity aid to implement the Person module (model, persistence, and service layers) while I was setting up my development environment (JDK, Maven, Git) for the first time.

Summary of sessions

Environment setup Claude walked me step by step through installing JDK 17 and Apache Maven on Windows, adding them to PATH, cloning the repository, and creating the feature/person-module branch from develop. I ran and verified every command myself (java -version, javac -version, mvn -version, mvn compile) and fixed typos I made along the way.

Model layer (Person, Client, Seller) I shared the docs/class-diagram.md content with Claude and asked for help drafting Person (abstract), Client, and Seller according to the attributes and methods already defined in the diagram. Claude generated the initial code with JavaDoc; I reviewed it against the diagram, pasted it into the project, compiled it, and pushed it in three separate atomic commits.

Persistence layer (PersonRepository, FilePersonRepository) Claude proposed the PersonRepository interface (matching the diagram's loadPersons()/savePersons() methods) and a file-based implementation using a semicolon-separated text format. I reviewed the logic, compiled it, and committed it in two atomic commits.

Service layer (PersonService) Claude drafted PersonService with the five methods specified in the diagram (registerClient, getAllClients, getAllSellers, findClientById, findSellerByCode), receiving PersonRepository (the interface, not the concrete class) through its constructor. I compiled and committed it.

Understanding check After finishing the module, I went through a self-review with Claude covering: why Person is abstract, why PersonService depends on the PersonRepository interface instead of FilePersonRepository directly, why attributes are private with getters/setters, how polymorphism lets a single list hold both Client and Seller objects, and why persistence/service logic is kept out of the model package.

Decisions I made myself
Confirmed every class against docs/class-diagram.md before accepting it.
Chose to keep the plain-text/semicolon file format proposed for persistence.
Decided the commit granularity (one class per commit) and wrote/reviewed each commit message against the Conventional Commits format required by the workshop.
Ran every build (mvn compile) and git command myself, verifying success before moving to the next class.
What I can explain without notes

The purpose of each class in my module, why the abstract classes/interfaces are structured this way, and how my three layers (model → persistence → service) connect and depend only in the direction allowed by the architecture (service → persistence → model).
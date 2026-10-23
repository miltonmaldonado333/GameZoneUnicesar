### Class: `com.gamezone.model.Sale`

#### Single Responsibility Principle (SRP)

- **Status:** Violation
- **Evidence:** Lines 82-129 (`public String generateReceipt()`)
- **Explanation:** The `Sale` class combines domain model responsibilities (managing sale state and calculating totals) with UI presentation concerns (formatting a text-based physical receipt for console output). Changes to the receipt presentation layout force modifications onto the core domain entity.
- **Impact:** High coupling between domain model and UI layout formatting.
- **Proposed Solution:** Extract receipt string formatting into a dedicated utility or presentation service (e.g., `SaleReceiptPrinter`).
- **Other Member Code Involved:** No

#### Open/Closed Principle (OCP)

- **Status:** Satisfied
- **Evidence:** Lines 57-68 (`getSubtotal()`) and 76-79 (`calculateTotal()`)
- **Explanation:** Subtotal calculations process polymorphic `Product` instances through `product.getPrice()` without type checks or branching logic.
- **Proposed Solution:** None required.

#### Liskov Substitution Principle (LSP)

- **Status:** Not Applicable
- **Explanation:** `Sale` is a concrete domain class and does not participate in an inheritance hierarchy.

#### Interface Segregation Principle (ISP)

- **Status:** Not Applicable
- **Explanation:** `Sale` does not implement any interface.

#### Dependency Inversion Principle (DIP)

- **Status:** Violation
- **Evidence:** Lines 17-18 (`private Client client;`, `private Seller seller;`)
- **Explanation:** Direct dependency on concrete classes `Client` and `Seller` instead of generic person abstractions where applicable.
- **Proposed Solution:** Evaluate substituting with generic `Person` references if specific subclass features are not strictly required within the sale entity.

### Class: `com.gamezone.persistence.SaleRepository`

#### Single Responsibility Principle (SRP)

- **Status:** Satisfied
- **Evidence:** Lines 33-57 (`save()`) and 64-88 (`readAllRawLines()`)
- **Explanation:** The class strictly handles I/O file persistence operations for sales data without mixing domain logic or presentation concerns.
- **Proposed Solution:** None strictly required, though extracting CSV string mapping to a `SaleRecord` helper class aligns with Reqs 7 patterns.

#### Open/Closed Principle (OCP)

- **Status:** Violation
- **Evidence:** Line 18 (`FILE_NAME = "sales.txt"`) and hardcoded file-writing behavior.
- **Explanation:** Changing storage strategies (e.g., to SQL or JSON) requires modifying this class directly instead of extending an abstraction.
- **Impact:** Medium. Inflexible persistence boundary.
- **Proposed Solution:** Extract an interface `ISaleRepository` and implement `FileSaleRepository`.

#### Liskov Substitution Principle (LSP)

- **Status:** Not Applicable
- **Explanation:** Does not inherit from any superclass.

#### Interface Segregation Principle (ISP)

- **Status:** Violation
- **Evidence:** Entire class declaration.
- **Explanation:** Clients are forced to depend on the concrete class directly due to the lack of an explicit repository interface contract.
- **Proposed Solution:** Define an explicit `ISaleRepository` interface.

#### Dependency Inversion Principle (DIP)

- **Status:** Violation
- **Evidence:** Class lacks interface abstraction.
- **Explanation:** High-level modules (`SaleService`) must depend directly on this concrete persistence class instead of an interface.
- **Proposed Solution:** Introduce `ISaleRepository` interface and inject it into `SaleService`.

### Class: `com.gamezone.service.SaleService`

#### Single Responsibility Principle (SRP)

- **Status:** Violation
- **Evidence:** Lines 141-207 (`getAllSales()`)
- **Explanation:** `SaleService` mixes orchestration of domain sales business logic with low-level string parsing and CSV format structure interpretation across multiple legacy file versions.
- **Impact:** High. Persistence schema changes force modifications on core business logic.
- **Proposed Solution:** Delegate raw string parsing and record reconstruction to the persistence layer (`SaleRepository` / `SaleRecord`).

#### Open/Closed Principle (OCP)

- **Status:** Violation
- **Evidence:** Lines 160-187 (`getAllSales()`)
- **Explanation:** Handcrafted conditional branching (`if (parts.length >= 8)`) for handling legacy file formats requires modifying service code when new attributes or persistence fields are introduced.
- **Proposed Solution:** Encapsulate record parsing strategies inside persistence mappers.

#### Liskov Substitution Principle (LSP)

- **Status:** Not Applicable
- **Explanation:** Class does not belong to an inheritance hierarchy.

#### Interface Segregation Principle (ISP)

- **Status:** Violation
- **Evidence:** Class declaration lacks a contracts interface.
- **Explanation:** Higher-level callers (UI/Controllers) are tightly coupled to the entire concrete class implementation rather than a segregated interface contract.
- **Proposed Solution:** Extract an `ISaleService` interface.

#### Dependency Inversion Principle (DIP)

- **Status:** Violation
- **Evidence:** Lines 23, 40 (`private final SaleRepository saleRepository;`)
- **Explanation:** Direct dependency on the concrete `SaleRepository` class instead of depending on an abstraction/interface contract.
- **Proposed Solution:** Inject an `ISaleRepository` interface abstraction via constructor.

### Class: `com.gamezone.ui.GameZoneUI`

#### Single Responsibility Principle (SRP)

- **Status:** Violation
- **Evidence:** Entire class declaration (928 lines) and line 390 (`item instanceof Console`)
- **Explanation:** Monolithic presentation class that manages all UI menus while embedding domain-level decision rules (checking `Console` instance for warranty eligibility in line 390).
- **Impact:** Very High. Class is brittle and overly complex.
- **Proposed Solution:** Extract sub-menu handling into dedicated views/controllers and move domain logic into services.

#### Open/Closed Principle (OCP)

- **Status:** Violation
- **Evidence:** Lines 180-192 (`displayMainMenu()`) and 218-230 (`processMainMenuOption()`)
- **Explanation:** Adding new subsystem features or menu items requires modifying existing switch-case handlers directly.
- **Proposed Solution:** Apply the Command pattern for menu actions.

#### Liskov Substitution Principle (LSP)

- **Status:** Not Applicable
- **Explanation:** Class does not extend any base class or interface.

#### Interface Segregation Principle (ISP)

- **Status:** Not Applicable
- **Explanation:** Class implements no interfaces.

#### Dependency Inversion Principle (DIP)

- **Status:** Violation
- **Evidence:** Lines 44-51 and 69-76 (Constructor parameters)
- **Explanation:** Injects and couples directly to 7 concrete service implementations instead of service interfaces.
- **Proposed Solution:** Refactor fields and constructor parameters to rely on service interface contracts (`ISaleService`, etc.).

### Class: `com.gamezone.Main.main`

#### Single Responsibility Principle (SRP)

- **Status:** Satisfied
- **Evidence:** Entire class (`main.java`)
- **Explanation:** Acts strictly as the application Composition Root (Bootstrap), instantiating layers and injecting dependencies before launching the UI.
- **Note:** Class identifier `main` violates standard Java PascalCase naming conventions and should be renamed to `Main`.

#### Open/Closed Principle (OCP)

- **Status:** Satisfied
- **Explanation:** As the application bootstrap entry point, static wiring is expected.

#### Liskov Substitution Principle (LSP)

- **Status:** Not Applicable
- **Explanation:** Does not inherit from any superclass.

#### Interface Segregation Principle (ISP)

- **Status:** Not Applicable
- **Explanation:** Does not implement any interface.

#### Dependency Inversion Principle (DIP)

- **Status:** Violation
- **Evidence:** Line 47 (`SaleRepository saleRepository = new SaleRepository();`)
- **Explanation:** Instantiates concrete `SaleRepository` type directly instead of referencing a repository interface abstraction like other persistence units.
- **Proposed Solution:** Declare type as `ISaleRepository` once extracted.

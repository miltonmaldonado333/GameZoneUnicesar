### Class: `com.gamezone.model.Product`

#### 1. Single Responsibility Principle (SRP)
- **Status**: Correct
- **Evidence**: Lines 8-100 (entire class definition)
- **Justification**: The `Product` class strictly acts as an abstract domain model encapsulating core product state (`id`, `title`, `price`, `stock`) and state modification logic (`addStock`, `reduceStock`). It does not contain persistence, file I/O, or user interface responsibilities.

#### 2. Open/Closed Principle (OCP)
- **Status**: Correct
- **Evidence**: Lines 77 and 84 (`getFullDescription()` and `getCategory()`)
- **Justification**: Defines abstract methods for behavior that varies by product type, allowing new product variants to extend `Product` without modifying existing core logic.

#### 3. Liskov Substitution Principle (LSP)
- **Status**: Correct
- **Evidence**: Line 106 (`hasWarrantyCoverage()`)
- **Justification**: Provides a non-breaking default implementation (`return false;`) that allows subclasses to substitute `Product` cleanly without altering system expectations.

#### 4. Interface Segregation Principle (ISP)
- **Status**: Not Applicable
- **Justification**: `Product` is an abstract base class and does not implement Java interfaces.

#### 5. Dependency Inversion Principle (DIP)
- **Status**: Not Applicable
- **Justification**: Being a domain entity, `Product` has no external dependencies on low-level or high-level modules.

### Class: `com.gamezone.model.VideoGame`

#### 1. Single Responsibility Principle (SRP)
- **Status**: Correct
- **Evidence**: Lines 7-49 (entire class definition)
- **Justification**: The `VideoGame` class strictly acts as a concrete domain model encapsulating video game-specific attributes (`platform`, `genre`, `ageRating`) alongside inherited product properties. It contains no persistence or UI presentation logic.

#### 2. Open/Closed Principle (OCP)
- **Status**: Correct
- **Evidence**: Lines 37-48 (`getCategory()` and `getFullDescription()`)
- **Justification**: Implements abstract base methods from `Product` to extend product behavior without modifying the superclass.

#### 3. Liskov Substitution Principle (LSP)
- **Status**: Correct
- **Evidence**: Lines 7-49
- **Justification**: `VideoGame` can substitute `Product` anywhere in the application without violating system invariants or unexpected side effects. It inherits `hasWarrantyCoverage()` (returning `false`) which aligns with the game domain logic.

#### 4. Interface Segregation Principle (ISP)
- **Status**: Not Applicable
- **Justification**: `VideoGame` does not implement any Java interfaces.

#### 5. Dependency Inversion Principle (DIP)
- **Status**: Not Applicable
- **Justification**: It is a pure domain entity with no dependencies on high-level or low-level components.

### Class: `com.gamezone.model.Console`

#### 1. Single Responsibility Principle (SRP)
- **Status**: Correct
- **Evidence**: Lines 7-88 (entire class definition)
- **Justification**: The `Console` class acts strictly as a domain model encapsulating hardware console attributes (`brand`, `model`, `generation`) along with inherited product state. It maintains no persistence or user interface behavior.

#### 2. Open/Closed Principle (OCP)
- **Status**: Correct
- **Evidence**: Lines 65-81 (`getCategory()` and `getFullDescription()`)
- **Justification**: Fulfills the abstract contract of `Product` to extend domain functionality without altering superclass code.

#### 3. Liskov Substitution Principle (LSP)
- **Status**: Correct
- **Evidence**: Line 85 (`hasWarrantyCoverage()`)
- **Justification**: Properly overrides `hasWarrantyCoverage()` to return `true`, extending base behavior predictably without breaking polymorphism or altering base contract assumptions.

#### 4. Interface Segregation Principle (ISP)
- **Status**: Not Applicable
- **Justification**: `Console` does not implement Java interfaces.

#### 5. Dependency Inversion Principle (DIP)
- **Status**: Not Applicable
- **Justification**: Pure domain model entity with no external module dependencies.

### Class/Interface: `com.gamezone.persistence.ProductRepository`

#### 1. Single Responsibility Principle (SRP)
- **Status**: Correct
- **Evidence**: Lines 12-29 (entire interface contract)
- **Justification**: The interface strictly defines contract boundaries for product persistence operations (`loadProducts`, `saveProducts`, `findById`, `findAll`). It contains no concrete I/O file manipulation or business logic.

#### 2. Open/Closed Principle (OCP)
- **Status**: Correct
- **Evidence**: Lines 12-29
- **Justification**: Serves as an abstraction that enables adding new persistence strategies (e.g., CSV file storage, database storage, in-memory storage) without altering existing caller logic.

#### 3. Liskov Substitution Principle (LSP)
- **Status**: Correct
- **Evidence**: Lines 19-25 (`findById` default implementation)
- **Justification**: Provides consistent default lookup behavior that works reliably across any concrete repository implementation that fulfills `loadProducts()`.

#### 4. Interface Segregation Principle (ISP)
- **Status**: Correct
- **Evidence**: Lines 12-29
- **Justification**: Exposes a lean, cohesive set of operations dedicated purely to product persistence, avoiding broad or unused method signatures for client components.

#### 5. Dependency Inversion Principle (DIP)
- **Status**: Correct
- **Evidence**: Line 9 (`public interface ProductRepository`)
- **Justification**: Acts directly as the high-level abstraction required by DIP, allowing domain and service layers (`ProductService`) to depend on an interface rather than low-level concrete storage implementations.

### Class: `com.gamezone.service.ProductService` (excluding `restoreStock`)

#### 1. Single Responsibility Principle (SRP)
- **Status**: Correct
- **Evidence**: Lines 33-145 (excluding lines 71-92 `restoreStock`)
- **Justification**: The service acts purely as a business domain orchestrator handling product operations (stock validation, product retrieval, video game registration). It delegates persistence to `ProductRepository` and contains no UI formatting or direct file I/O operations.

#### 2. Open/Closed Principle (OCP)
- **Status**: Correct
- **Evidence**: Lines 94-106 (`getAvailableCategories()`)
- **Justification**: Processes categories polymorphically by invoking `p.getCategory()` on `Product` instances, allowing new product types to seamlessly provide their own category without requiring modifications to service iteration logic.

#### 3. Liskov Substitution Principle (LSP)
- **Status**: Not Applicable
- **Justification**: `ProductService` does not extend a base class.

#### 4. Interface Segregation Principle (ISP)
- **Status**: Violation
- **ID**: V-DEV1-01
- **Module**: Product Service / Workshop 1 & Requisitions
- **Evidence**: Lines 16-146 (entire class declaration)
- **Explanation**: The class implements `IStockService` for stock operations, but lacks a dedicated business interface contract for core catalog operations (`registerVideoGame`, `getAllProducts`, `findProductById`, `getAvailableCategories`). Calling UI components are forced to depend on the concrete service implementation rather than a segregated interface contract.
- **Consequence**: High coupling between presentation/UI components and the concrete service class, limiting mockability and contract abstraction.
- **Proposed Solution**: Extract an `IProductService` interface that defines catalog operations.
- **Other Member Code Involved**: Technical Leader (`GameZoneUI`)

#### 5. Dependency Inversion Principle (DIP)
- **Status**: Correct
- **Evidence**: Lines 18, 27 (`private final ProductRepository productRepository;`)
- **Justification**: Depends on the abstraction `ProductRepository` (interface) rather than a concrete low-level persistence implementation, permitting flexible injection of repository strategy instances.

### Class: `com.gamezone.model.Accessory`

#### 1. Single Responsibility Principle (SRP)
- **Status**: Correct
- **Evidence**: Lines 8-39 (entire class definition)
- **Justification**: The `Accessory` class acts purely as an abstract domain entity encapsulating compatibility attributes (`compatibleConsoleIds`) along with inherited product attributes. It contains no file persistence or presentation logic.

#### 2. Open/Closed Principle (OCP)
- **Status**: Correct
- **Evidence**: Lines 36-38 (`getCategory()`)
- **Justification**: Overrides `getCategory()` from `Product` to define a unified accessory category while remaining abstract, allowing concrete subclasses (`Controller`, `Cable`, `Memory`) to extend specific behavior without modifying `Accessory`.

#### 3. Liskov Substitution Principle (LSP)
- **Status**: Correct
- **Evidence**: Lines 8-39
- **Justification**: `Accessory` cleanly substitutes `Product` across system operations without breaking base class invariants or behavior expectations.

#### 4. Interface Segregation Principle (ISP)
- **Status**: Not Applicable
- **Justification**: `Accessory` does not implement Java interfaces.

#### 5. Dependency Inversion Principle (DIP)
- **Status**: Not Applicable
- **Justification**: Pure domain model entity with no external module dependencies.

### Class: `com.gamezone.model.Controller`

#### 1. Single Responsibility Principle (SRP)
- **Status**: Correct
- **Evidence**: Lines 9-52 (entire class definition)
- **Justification**: The `Controller` class acts strictly as a concrete domain entity encapsulating controller-specific properties (`connectionType`) alongside inherited accessory and product attributes. It contains no persistence, file I/O, or user interface logic.

#### 2. Open/Closed Principle (OCP)
- **Status**: Correct
- **Evidence**: Lines 43-51 (`getCategory()` and `getFullDescription()`)
- **Justification**: Fulfills the abstract contracts of `Accessory` and `Product` to extend domain behavior without modifying superclass code.

#### 3. Liskov Substitution Principle (LSP)
- **Status**: Correct
- **Evidence**: Lines 9-52
- **Justification**: `Controller` can substitute `Accessory` or `Product` cleanly in any system operation without breaking invariants or expected behaviors.

#### 4. Interface Segregation Principle (ISP)
- **Status**: Not Applicable
- **Justification**: `Controller` does not implement Java interfaces.

#### 5. Dependency Inversion Principle (DIP)
- **Status**: Not Applicable
- **Justification**: Pure domain model entity with no external module dependencies.

### Class: `com.gamezone.model.Cable`

#### 1. Single Responsibility Principle (SRP)
- **Status**: Correct
- **Evidence**: Lines 9-61 (entire class definition)
- **Justification**: The `Cable` class strictly acts as a concrete domain entity encapsulating cable-specific properties (`length`, `connectorType`) along with inherited accessory and product attributes. It contains no persistence, file I/O, or presentation logic.

#### 2. Open/Closed Principle (OCP)
- **Status**: Correct
- **Evidence**: Lines 52-60 (`getFullDescription()` and `getCategory()`)
- **Justification**: Extends domain behavior by overriding abstract/base methods from `Accessory` and `Product` without modifying superclass implementation details.

#### 3. Liskov Substitution Principle (LSP)
- **Status**: Correct
- **Evidence**: Lines 9-61
- **Justification**: `Cable` can substitute `Accessory` or `Product` seamlessly in any polymorphic operation without breaking system expectations or contract invariants.

#### 4. Interface Segregation Principle (ISP)
- **Status**: Not Applicable
- **Justification**: `Cable` does not implement Java interfaces.

#### 5. Dependency Inversion Principle (DIP)
- **Status**: Not Applicable
- **Justification**: Pure domain model entity with no external module dependencies.

### Class: `com.gamezone.model.Memory`

#### 1. Single Responsibility Principle (SRP)
- **Status**: Correct
- **Evidence**: Lines 9-47 (entire class definition)
- **Justification**: The `Memory` class strictly acts as a concrete domain entity encapsulating storage-specific properties (`capacityGB`, `storageType`) along with inherited accessory and product state. It contains no persistence, file I/O, or presentation logic.

#### 2. Open/Closed Principle (OCP)
- **Status**: Correct
- **Evidence**: Lines 37-46 (`getCategory()` and `getFullDescription()`)
- **Justification**: Fulfills abstract and base methods from `Accessory` and `Product` to extend domain functionality without altering superclass code.

#### 3. Liskov Substitution Principle (LSP)
- **Status**: Correct
- **Evidence**: Lines 9-47
- **Justification**: `Memory` cleanly substitutes `Accessory` or `Product` instances across all system operations without violating contracts or breaking expectations.

#### 4. Interface Segregation Principle (ISP)
- **Status**: Not Applicable
- **Justification**: `Memory` does not implement Java interfaces.

#### 5. Dependency Inversion Principle (DIP)
- **Status**: Not Applicable
- **Justification**: Pure domain model entity with no external module dependencies.

### Class: `com.gamezone.model.Promotion`

#### 1. Single Responsibility Principle (SRP)
- **Status**: Correct
- **Evidence**: Lines 9-112 (entire class definition)
- **Justification**: The `Promotion` class strictly acts as an abstract domain model encapsulating promotion metadata (`id`, `name`, `startDate`, `endDate`) and date validation logic (`isActive`). It contains no persistence, file I/O, or user interface presentation code.

#### 2. Open/Closed Principle (OCP)
- **Status**: Correct
- **Evidence**: Lines 65 and 111 (`calculateDiscount(Sale sale)` and `getDetails()`)
- **Justification**: Defines abstract contracts for calculating discounts and retrieving promotion details, allowing concrete promotion types (`PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount`) to extend behavior without modifying `Promotion`.

#### 3. Liskov Substitution Principle (LSP)
- **Status**: Correct
- **Evidence**: Lines 33-49 (`isActive`)
- **Justification**: Provides non-breaking, consistent date range validation that sub-types rely on, ensuring concrete promotions substitute `Promotion` predictably across system calculations.

#### 4. Interface Segregation Principle (ISP)
- **Status**: Not Applicable
- **Justification**: `Promotion` is an abstract base class and does not implement Java interfaces.

#### 5. Dependency Inversion Principle (DIP)
- **Status**: Not Applicable
- **Justification**: Pure domain model abstraction with no external module dependencies.

### Class: `com.gamezone.model.PercentageDiscount`

#### 1. Single Responsibility Principle (SRP)
- **Status**: Correct
- **Evidence**: Lines 8-36 (entire class definition)
- **Justification**: The `PercentageDiscount` class strictly acts as a concrete domain model encapsulating flat percentage discount state (`percentage`) and discount calculation logic. It contains no persistence, file I/O, or presentation code.

#### 2. Open/Closed Principle (OCP)
- **Status**: Correct
- **Evidence**: Lines 26-34 (`calculateDiscount` and `getDetails`)
- **Justification**: Implements the abstract contracts defined in `Promotion` to extend discount strategy behavior without modifying the superclass.

#### 3. Liskov Substitution Principle (LSP)
- **Status**: Correct
- **Evidence**: Lines 8-36
- **Justification**: `PercentageDiscount` substitutes `Promotion` seamlessly across system calculations, returning predicted discount amounts based on the sale total without altering expected behavior.

#### 4. Interface Segregation Principle (ISP)
- **Status**: Not Applicable
- **Justification**: `PercentageDiscount` does not implement Java interfaces.

#### 5. Dependency Inversion Principle (DIP)
- **Status**: Not Applicable
- **Justification**: Pure domain model entity with no external module dependencies.

### Class: `com.gamezone.model.CategoryDiscount`

#### 1. Single Responsibility Principle (SRP)
- **Status**: Violation
- **ID**: V-DEV1-02
- **Module**: Promotion / Requisition 2
- **Evidence**: Lines 48-50 (`System.out.println("DEBUG - ID Producto: " ... )`)
- **Explanation**: The class embeds console output debug code (`System.out.println`) inside domain business calculation logic. Presentation/console I/O concerns should not leak into domain model calculations.
- **Consequence**: Pollutes system standard output during routine business operations and violates separation of UI/logging concerns from domain logic.
- **Proposed Solution**: Remove `System.out.println` debug lines or replace with a proper logging abstraction.
- **Other Member Code Involved**: No

#### 2. Open/Closed Principle (OCP)
- **Status**: Correct
- **Evidence**: Lines 40-61 (`calculateDiscount`)
- **Justification**: Calculates category-specific discounts polymorphically by invoking `product.getCategory()` rather than using type checking (`instanceof`) or conditional branching per concrete product class.

#### 3. Liskov Substitution Principle (LSP)
- **Status**: Correct
- **Evidence**: Lines 5-66
- **Justification**: Substitutes `Promotion` without breaking expected calculation outcomes or throwing unexpected unchecked exceptions during standard evaluations.

#### 4. Interface Segregation Principle (ISP)
- **Status**: Not Applicable
- **Justification**: `CategoryDiscount` does not implement Java interfaces.

#### 5. Dependency Inversion Principle (DIP)
- **Status**: Not Applicable
- **Justification**: Pure domain model entity with no external module dependencies.

### Class: `com.gamezone.model.BulkPurchaseDiscount`

#### 1. Single Responsibility Principle (SRP)
- **Status**: Correct
- **Evidence**: Lines 9-47 (entire class definition)
- **Justification**: The `BulkPurchaseDiscount` class strictly acts as a concrete domain entity encapsulating bulk purchase conditions (`minQuantity`, `percentage`) and calculating discounts based on product volume. It contains no file persistence or UI presentation logic.

#### 2. Open/Closed Principle (OCP)
- **Status**: Correct
- **Evidence**: Lines 37-46 (`calculateDiscount` and `getDetails`)
- **Justification**: Implements the abstract methods of `Promotion` to extend discount rule strategies without modifying superclass implementation details.

#### 3. Liskov Substitution Principle (LSP)
- **Status**: Correct
- **Evidence**: Lines 9-47
- **Justification**: Substitutes `Promotion` seamlessly across system calculations, returning expected discount values when minimum quantity thresholds are met or zero when not.

#### 4. Interface Segregation Principle (ISP)
- **Status**: Not Applicable
- **Justification**: `BulkPurchaseDiscount` does not implement Java interfaces.

#### 5. Dependency Inversion Principle (DIP)
- **Status**: Not Applicable
- **Justification**: Pure domain model entity with no external module dependencies.
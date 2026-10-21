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
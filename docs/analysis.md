# Domain Analysis and Architectural Decisions

## About People in the System

### 1. Common and Specific Attributes of People

- **Common Attributes:** Every person interacting with the store shares basic identifying information, which includes full name (`name`), national identification number (`id`), and contact phone number (`phone`)[cite: 2].
- **Specific Attributes:**
  - Customers (`Customer`) require an email address (`email`) and a purchase history record (`purchaseHistory`)[cite: 2].
  - Salespeople (`Seller`) require an employee ID (`employeeId`) and an assigned work shift (`shift`)[cite: 2].
- **Class Hierarchy Reflection:** This distinction is implemented using generalisation/specialisation[cite: 2]. A base class `Person` encapsulates all shared attributes and methods, while `Customer` and `Seller` inherit from `Person` and declare their own specific fields[cite: 2].

### 2. Generic Person Class and Instantiation

- **Design Decision:** The system should **not** allow creating a generic `Person` object; the `Person` class must be declared as `abstract`[cite: 2].
- **Justification:** In the context of GameZone Unicesar, every person entering a business transaction performs a well-defined role (either buying as a customer or processing sales as an employee)[cite: 2]. A generic person without a role has no operational purpose in the system domain[cite: 2]. Declaring `Person` as abstract prevents direct instantiation (`new Person()`) and forces the system to instantiate concrete subtypes (`Customer` or `Seller`)[cite: 2].

---

## About Products in the System

### 3. Common and Specific Attributes of Products

- **Common Attributes:** All sellable items in the store share four basic attributes: unique identifier (`id`), title or name (`title`), unit price (`price`), and available stock quantity (`quantity`)[cite: 1, 2].
- **Specific Attributes:**
  - Video Games (`VideoGame`): Target platform (`platform`), genre (`genre`), and age rating (`ageRating`)[cite: 2].
  - Consoles (`Console`): Brand (`brand`), model (`model`), and generation (`generation`)[cite: 2].

### 4. Custom Description Behavior and Object-Oriented Mechanism

- **Declaration:** The base class `Product` declares an abstract method `public abstract String getDescription();`[cite: 1, 2].
- **Implementation:** Each concrete subclass (`VideoGame` and `Console`) provides its own specific implementation of `getDescription()`, formatting its custom attributes into a human-readable summary[cite: 1, 2].
- **OO Mechanism:** This is achieved through **Polymorphism** and **Method Overriding** using the `@Override` annotation[cite: 1, 2]. It allows higher-level services to display item details without needing to know the specific subclass type at compile time[cite: 1, 2].

---

## About Sales and Relationships Between Entities

### 5. Entity Relationships in Sales

- **Relationships:**
  - `Sale` to `Customer`: **Association / Aggregation** (1 to 1). A sale is associated with the customer who made the purchase[cite: 2].
  - `Sale` to `Seller`: **Association / Aggregation** (1 to 1). A sale is associated with the salesperson who handled the transaction[cite: 2].
  - `Sale` to `Product`: **Aggregation** (1 to Many). A sale contains a collection of products[cite: 2].
- **Justification:** These are association and aggregation relationships rather than inheritance, because a `Sale` is an operational transaction entity that links independent domain entities (`Customer`, `Seller`, `Product`) without sharing an "is-a" relationship with them[cite: 2].

### 6. Responsibility for Total Calculation

- **Design Decision:** The `Sale` class itself is responsible for calculating its own total amount[cite: 2].
- **Argument:** According to the **Information Expert** principle in GRASP, the class that holds the information necessary to fulfill a responsibility should be assigned that responsibility. Since `Sale` maintains the collection of purchased items and their quantities, it is best positioned to iterate over them and calculate the sum[cite: 2].

---

## About Business Rules and Constraints

### 7. Minimum Product Validation Rule

- **Design Guarantee:** The rule stating that a sale must contain at least one product is enforced in the **Service Layer** (`SaleService`) before persisting the transaction[cite: 2].
- **Validation Point:** When `SaleService.registerSale(...)` is invoked, the service checks whether the product list is `null` or empty (`items.isEmpty()`)[cite: 2]. If empty, the service throws a domain exception (e.g., `IllegalArgumentException` or custom `EmptySaleException`) and aborts processing[cite: 2].

### 8. Automatic Inventory Update

- **Involved Classes:** `SaleService`, `ProductService`, `Product`, and `ProductRepository`[cite: 1, 2].
- **Workflow:** Upon validating a sale, `SaleService` iterates through each product in the purchase order and calls `ProductService.updateStock(productId, quantity)`. `ProductService` retrieves the product, decrements its stock, and instructs `ProductRepository` to save the updated inventory back to persistent storage[cite: 1, 2].

---

## About Layered Architecture

### 9. Four-Layer Architecture Responsibilities and Criteria

- **Model Layer (`com.gamezone.model`):** Domain entities (`Person`, `Product`, `Sale`, etc.)[cite: 2]. Contains data structures, private attributes, getters/setters, and core business formulas[cite: 2].
- **Persistence Layer (`com.gamezone.persistence`):** Repositories (`ProductRepository`, `PersonRepository`, `SaleRepository`)[cite: 2]. Responsible strictly for reading from and writing to external storage (files/CSV)[cite: 1, 2].
- **Service Layer (`com.gamezone.service`):** Business logic services (`ProductService`, `PersonService`, `SaleService`)[cite: 2]. Enforces business constraints, manages orchestrations, validates rules, and acts as a bridge between UI and persistence[cite: 2].
- **UI Layer (`com.gamezone.ui`):** User interface menus (`ConsoleMenu`)[cite: 1, 2]. Handles console input/output, user option menus, and formats data for display[cite: 2].

### 10. Decoupling Persistence from Domain Classes

- **Problems Avoided:** Placing file access logic directly inside domain model classes violates the **Single Responsibility Principle (SRP)**[cite: 2].
- **Specific Issues:**
  1. Mixing I/O exceptions, file paths, and parsing logic with business entities[cite: 2].
  2. Making unit testing difficult, as domain objects cannot be tested without reading/writing physical files[cite: 2].
  3. High coupling: changing the storage format (e.g., switching from CSV to a relational database) would require modifying every domain model class[cite: 2].

### 11. Allowed and Forbidden Layer Dependencies

- **Allowed Dependencies:**
  $$\text{UI Layer} \longrightarrow \text{Service Layer} \longrightarrow \text{Persistence Layer} \longrightarrow \text{Model Layer}$$
  $$\text{Service Layer} \longrightarrow \text{Model Layer}$$
- **Forbidden Dependencies:**
  - Direct dependency from UI to Persistence (bypassing Services)[cite: 2].
  - Dependencies pointing upwards or backwards (e.g., Model referencing Persistence or UI)[cite: 2].
- **Justification:** Unidirectional downward dependencies ensure loose coupling, high cohesion, and strict separation of concerns, allowing layers to be tested, maintained, or replaced independently without cascading side effects[cite: 2].

===============================================================================

# Requirement 1: Accessories Module Analysis

===============================================================================

## 1. Hierarchy Integration

- **Decision:** The accessory classes must extend the existing abstract `Product` class rather than forming an independent hierarchy[cite: 1].
- **Justification:** Accessories are commercial items sold by the store and naturally share basic domain attributes with video games and consoles: a unique identifier (`id`), a title (`title`), a price (`price`), and available stock (`quantity`)[cite: 1]. Extending `Product` promotes code reuse and maintains domain model consistency[cite: 1]. Furthermore, it leverages **polymorphism**, allowing accessories to be treated seamlessly as `Product` instances within the sales workflow (`Sale` and `SaleService`) without duplicating sales logic[cite: 1].

---

## 2. Common vs. Specific Attributes

- **Common Attributes:**
  - Inherited from `Product`: `id`, `title`, `price`, and `quantity`[cite: 1].
  - Common to all accessories (`Accessory`): The list of compatible console IDs (`compatibleConsoleIds` / `List<String>`)[cite: 1].
- **Specific Attributes:**
  - `Controller`: Connection type (`connectionType`: wireless or wired)[cite: 1].
  - `Cable`: Length in meters (`length`: `double`) and connector type (`connectorType`: `String`, e.g., HDMI, USB, Optical)[cite: 1].
  - `Memory`: Storage capacity in gigabytes (`capacity`: `int`) and memory type (`storageType`: `String`, e.g., SD, microSD, internal card)[cite: 1].
- **Class Hierarchy Reflection:** An abstract class `Accessory` inherits from `Product` to encapsulate the shared compatibility list and declare abstract details[cite: 1]. Three concrete subclasses (`Controller`, `Cable`, and `Memory`) extend `Accessory` to define their type-specific fields and override the `getDescription()` method[cite: 1].

---

## 3. Compatibility Relationship and Persistence

- **Entity Placement:** Compatibility is an attribute of the **accessory** (`Accessory`)[cite: 1]. Accessories are designed and produced to work with specific console models[cite: 1].
- **Design Representation:** In the `Accessory` abstract class, compatibility is modeled as an association/aggregation represented by a private collection of console identifiers (`List<String> compatibleConsoleIds`)[cite: 1].
- **Persistence Representation:** In persistent storage (`data/accessories.csv`), console IDs are serialized as a delimited string (e.g., using a delimiter like `C001|C002` or commas) within the CSV line[cite: 1]. `AccessoryRepository` parses and reconstructs this list when loading data[cite: 1].

---

## 4. Sales Service Modifications

- **Polymorphic Integration:** Because `Accessory` inherits from `Product`, `SaleService.registerSale` can accept item lists containing both standard products and new accessories without changing the method signature or breaking existing functionality[cite: 1].
- **Validation & Total Calculation:** Stock validation rules and total price calculation operate uniformly across all items via the `Product` interface[cite: 1].
- **Delegated Inventory Update:** During sale processing, `SaleService` checks the item type to delegate inventory decrements to the appropriate service: `ProductService` for video games and consoles, or `AccessoryService` for accessories[cite: 1].

---

## 5. Architectural Layer Allocation

The new classes are distributed across the four architectural layers according to their responsibilities[cite: 1, 2]:

1. **Model Layer (`com.gamezone.model`):** Contains `Accessory` (abstract), `Controller`, `Cable`, and `Memory`[cite: 1, 2].
   - _Responsibility:_ Defines domain state, attributes, getters/setters, and specific product descriptions[cite: 1, 2].
2. **Persistence Layer (`com.gamezone.persistence`):** Contains `AccessoryRepository`[cite: 1, 2].
   - _Responsibility:_ Handles file I/O operations for `data/accessories.csv` and instantiates concrete objects using type discriminators during loading[cite: 1, 2].
3. **Service Layer (`com.gamezone.service`):** Contains `AccessoryService`[cite: 1, 2].
   - _Responsibility:_ Implements business logic, stock updates, accessory registration, and compatibility filtering by console ID[cite: 1, 2].
4. **UI Layer (`com.gamezone.ui`):** Contains extensions within `ConsoleMenu`[cite: 1, 2].
   - _Responsibility:_ Presents the accessory management submenu options and formats input/output for console users[cite: 1, 2].

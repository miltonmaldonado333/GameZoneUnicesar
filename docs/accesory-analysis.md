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

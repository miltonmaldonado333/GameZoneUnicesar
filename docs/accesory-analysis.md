# Accessory Module Analysis and Architectural Design

## 1. Overview

The **Accessory Module** extends the GameZone system's product lineup by incorporating hardware accessories such as controllers, cables, and memory units into inventory management and sales workflows.

---

## 2. Domain Model & Hierarchy

Accessories inherit core properties from the base `Product` class while introducing specialized attributes per subtype:

- **`Product` (Base Abstract Class):**
  - `id` (String): Unique product identifier
  - `title` (String): Display name
  - `price` (double): Base retail price
  - `stock` (int): Available inventory quantity

- **`Accessory` (Abstract Class extending `Product`):**
  - `brand` (String): Manufacturer/Brand name
  - `compatibility` (String): Compatible system or console

- **Concrete Subclasses:**
  - **`Controller`:** `isWireless` (boolean), `color` (String)
  - **`Cable`:** `lengthMeters` (double), `connectorType` (String)
  - **`Memory`:** `capacityGB` (int), `readSpeedMBs` (double)

---

## 3. Persistence Layer (`AccessoryRepository`)

- **Storage Format:** Standardized CSV persistence in `data/accessories.csv`.
- **Data Fields:** `type,id,title,price,stock,brand,compatibility,extraField1,extraField2`
- **Operations:**
  - `loadAll()`: Parses CSV lines and instantiates specific `Controller`, `Cable`, or `Memory` objects.
  - `saveAll(List<Accessory>)`: Serializes current accessory instances to CSV format.
  - `findById(String id)`: Retrieves specific accessory records.

---

## 4. Service Layer (`AccessoryService`)

Handles accessory business logic and inventory checks:

- **Creation & Management:** Registering new controllers, cables, and memory cards.
- **Querying:** Searching accessories by ID, type, brand, or console compatibility.
- **Stock Management:** Deducting stock during sales and restoring stock upon returns or inventory restocking.

---

## 5. UI Integration (`GameZoneUI`)

Integrated into the main navigation flow with a dedicated submenu:

1. Register new accessory (Controller / Cable / Memory)
2. List all available accessories
3. Filter accessories by type or compatibility
4. Update accessory stock / details

---

## 6. Commit & Sync Instructions

Once saved, stage and commit the file to Git:

```bash
git add docs/accesory-analysis.md
git commit -m "docs: recreate accessory module analysis documentation"
git push origin feature/warranty-module
```

# 📝 System Analysis & Architecture

This document answers the mandatory project analysis questions, outlining the domain rules, architectural decisions, and object-oriented principles applied to the **GameZoneUnicesar** system.

---

## 👥 1. Regarding the People in the System

### Q1. What attributes are common to all people who interact with the store, and which are specific to each particular type of person? How is this distinction reflected in a class hierarchy?
**Solution:**
* **Common Attributes for All Persons:** All persons share the attributes `name`, `identification`, and `phone`.
* **Specific Attributes by Type:**
  * **Client:** Features the specific attribute `email`.
  * **Seller:** Features specific attributes `employeeCode` and `workShift`.
* **Reflection in the Class Hierarchy:** This distinction is implemented using an abstract base class named `Person` that encapsulates all shared attributes. The specialized roles (`Client` and `Seller`) inherit from `Person` via generalization (`Person <|-- Client` and `Person <|-- Seller`), allowing them to reuse common code while defining their own unique attributes.

### Q2. Should there be a class representing a "generic person" without specifying their role? Why or why not? What implication does this decision have regarding the possibility of instantiating that class?
**Solution:** 
No, a generic `Person` class without a specific role shouldn't be instantiable, which is why it is defined as an `abstract` class in the system.
* **Justification:** In the context of the store, every person interacting with the system needs to have a specific, well-defined role—either as a client who makes purchases or as an employee (seller) handling transactions. There is no business need for a person without a defined role.
* **Implications on Instantiation:** Making `Person` an abstract class (`<<abstract>>`) stops the system from creating direct instances (objects) of it using keywords like `new Person()`, making sure that only concrete subclasses (`Client` and `Seller`) can be instantiated.

---

## 📦 2. Regarding the System Products

### Q3. What characteristics do all the products sold by the store have in common, regardless of their type? What characteristics are specific to each type of product?
**Solution:**
* **Common Attributes for All Products:** Regardless of their type, every product commercialized by the store shares basic attributes such as an identifier (`id`), a title (`title`), a price (`price`), and the available quantity in inventory (`stock`).
* **Specific Attributes by Product Type:**
  * **Video Games:** Specifically characterized by the platform they are developed for, their genre, and the recommended age rating.
  * **Consoles:** Specifically characterized by their brand, model, and generation.

### Q4. Each product type must be able to provide a description that incorporates its specific characteristics. How should this behavior be declared in the base class to ensure that all subclasses implement it in their own way? Which OOP mechanism enables this?
**Solution:**
* **Declaration in the Base Class:** This behavior should be declared as an abstract method in the base class (`Product`), specifically as `public abstract String getFullDescription();`. Declaring it as abstract forces all subclasses to provide their own implementation.
* **OOP Mechanism:** The object-oriented programming mechanism that enables this is **polymorphism** combined with **abstract methods and inheritance**, allowing each subclass (`VideoGame` and `Console`) to override the description method and execute its specific behavior dynamically.

---

## 🛒 3. Sales and Entity Relationships

### Q5. A sale involves a customer, a salesperson, and one or more products. What types of relationships exist between the class representing the sale and the other classes in the system?
**Solution:**
* **Type of Relationship:** The relationships between the `Sale` class and the other classes in the system are **associations** (with specific multiplicities like 1-to-1 and 1-to-many), rather than inheritance.
* **Justification with Client and Seller:** The `Sale` class relates via association to `Client` and `Seller` because a transaction requires tracking which specific customer made the purchase and which employee handled it.
* **Justification with Products:** The relationship with `Product` is a **one-to-many** association (`1..*`), since the business rule mandates that a sale must contain at least one product to be successfully registered.
* **Exclusion of Inheritance:** There is no inheritance relationship (generalization/specialization) between `Sale` and these classes because `Sale` represents an independent commercial transaction that collaborates with and references people and items, but does not share a common attribute hierarchy with them.

### Q6. Should the sale be responsible for calculating its own total, or should this responsibility fall to another class? Justify your decision.
**Solution:**
Yes, the `Sale` class should calculate its own total. 
* **Justification:** Since the sale class already stores the list of products sold and how many of each were bought, it has the exact information right there to do the math. There is no need to make other classes look for that data elsewhere.
* **Encapsulation:** If another part of the program (like services or menus) did the calculation, it would have to pull the data out of the sale forcefully. Leaving the calculation inside the sale itself avoids messes and keeps the code clean and well-organized.

---

## 🛡️ 4. Business Restrictions

### Q7. How does the design ensure that a sale cannot be recorded without at least one product? At what point in the system should this rule be validated?
**Solution:**
* **Design Guarantee:** It is ensured in the design by establishing a **one-to-many multiplicity (`1..*`)** in the relationship with products and implementing logical validations within the sale's structure. 
* **Validation Point:** This rule must be validated in the service layer (`SaleService`) when processing the registration request, right before updating the inventory and saving the transaction to persistence.

### Q8. How is the automatic inventory update reflected in the design when a sale is recorded? Which classes are involved in this operation?
**Solution:**
The automatic inventory update is reflected in the design through **service-layer coordination**, where registering a sale triggers a stock-adjustment method that modifies available quantities before saving changes to persistence.
* **Involved Classes:**
  * `Sale`: Contains the collection of products and quantities purchased during the transaction.
  * `SaleService`: Orchestrates the business rules for the transaction and delegates the inventory update task.
  * `ProductService`: Handles product-level operations and exposes the specific method to update or decrease stock levels.
  * `Product` (and subclasses `VideoGame`, `Console`): Encapsulates the inventory `stock` attribute that gets reduced.
  * `ProductRepository`: Responsible for persisting the updated inventory states to files.

---

## 🏗️ 5. Layered Organization

### Q9. The system must be organized into four layers: model, persistence, services, and user interface. What types of classes belong to each layer? What criterion determines placement?
**Solution:**
* **Types of Classes per Layer:**
  * **Model Layer (`model`):** Contains the core domain entities and business objects that represent real-world concepts (e.g., `Person`, `Client`, `Seller`, `Product`, `VideoGame`, `Console`, and `Sale`).
  * **Persistence Layer (`persistence`):** Contains classes responsible for managing data storage, file input/output, and data recovery (e.g., `PersonRepository`, `ProductRepository`, and `SaleRepository`).
  * **Services Layer (`service`):** Contains classes that encapsulate business rules, validations, inventory updates, and transaction coordination (e.g., `PersonService`, `ProductService`, and `SaleService`).
  * **User Interface Layer (`ui` / root):** Contains classes responsible for interacting with the user, displaying menus, gathering input, and bootstrapping the application (e.g., `ConsoleUI` and `Main`).
* **Placement Criterion:** The primary criterion is **separation of concerns**, guided by the architectural responsibilities of each layer and strict dependency rules. A class is placed in a specific layer based on *what responsibility it fulfills* and how it adheres to the rule that lower layers must not depend on upper layers.

### Q10. Why should the logic for saving and retrieving data from files not be placed within domain classes? What problems arise when these responsibilities are mixed?
**Solution:**
File handling and persistence logic belong exclusively in the infrastructure layer, ensuring domain classes focus purely on core business concepts without being tied to input/output operations. Mixing these creates several architectural problems:
* **High Coupling:** Domain entities become tightly bound to specific file paths or storage formats, making the entire codebase rigid and difficult to modify.
* **Violation of the Single Responsibility Principle:** Classes end up handling both business logic and data persistence simultaneously instead of focusing on a single job.
* **Reduced Maintainability & Scalability:** Modifying how files are read or written requires altering core domain models, significantly increasing the risk of introducing bugs.
* **Difficult Unit Testing:** Testing domain logic becomes cumbersome because tests are forced to depend on the local file system and handle input/output operations instead of validating pure business rules.

### Q11. Which dependencies are permitted between the layers, and which are prohibited? Justify the rationale behind the permitted dependencies.
**Solution:**
* **Allowed Dependencies:**
  * User Interface (`ui`) depends on Services (`service`).
  * Services (`service`) depend on Model (`model`) and Persistence (`persistence`).
  * Persistence (`persistence`) depends on Model (`model`).
* **Prohibited Dependencies:**
  * Model (`model`) depending on any other layer (it must remain completely independent).
  * User Interface (`ui`) directly accessing the Persistence layer (`persistence`) without passing through Services.
  * Persistence depending on Services or UI.
* **Justification of Allowed Dependencies:**
  * **Layer Isolation and Integrity:** This strict, unidirectional flow ensures a clean separation of concerns. The Model layer stays pure, focusing entirely on core business entities.
  * **Controlled Orchestration:** The Service layer acts as the gatekeeper for business logic, coordinating domain entities and persistence safely.
  * **Decoupling the UI:** The User Interface is shielded from direct technical details like file manipulation, ensuring changes to data storage do not break the presentation layer.
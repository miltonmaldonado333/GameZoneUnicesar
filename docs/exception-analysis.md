# Exception Analysis and Validation Layer Architecture

## 1. Root Exception Hierarchy: Checked vs. Unchecked

For the GameZone project, declaring `GameZoneException` as an **unchecked exception** (`extends RuntimeException`) is the most suitable design choice.

- **Architectural Cleanliness:** Modern Java enterprise standards favor unchecked exceptions for domain logic and validation errors. Checked exceptions force calling methods to either explicitly declare them via `throws` or handle them immediately, leading to boiler-plate code and unnecessary coupling between layers.
- **Impact on Method Signatures:** Extending `RuntimeException` eliminates the need to pollute service and repository interface signatures with `throws GameZoneException`. This decouples the interface contracts from operational failures and allows caller layers (like the controller or UI layer) to handle exceptions cleanly without repetitive exception propagation.

---

## 2. Advantages of a Custom Exception Hierarchy

Using a dedicated exception hierarchy rooted in `GameZoneException` over standard generic exceptions like `IllegalArgumentException` provides several key design advantages:

- **Domain Expressiveness (Ubiquitous Language):** Custom exceptions like `EntityNotFoundException` or `ValidationException` make the codebase self-documenting and clearly signal domain-specific failure conditions.
- **Targeted Exception Handling:** A rich hierarchy allows high-level controllers (such as `ConsoleMenu`) to catch distinct categories of errors and provide specialized error handling or precise user notifications.
- **Design Principles Applied:**
  - **Single Responsibility Principle (SRP):** Each custom exception subclass encapsulates a specific error condition rather than overloading a single generic exception type.
  - **Open/Closed Principle (OCP):** New application-specific exception types can be introduced without altering existing handling logic that relies on the base class `GameZoneException`.
  - **Separation of Concerns:** Separates raw Java runtime/language errors from domain-level operational failures.

---

## 3. Validation Layer Placement and Coupling Rules

### Flow Execution and Layer Alignment

Validators in `com.gamezone.validation` must be invoked in the **Service Layer** prior to executing core business or domain logic.

This placement is fully coherent with N-tier layered architecture:

- The UI layer (`ConsoleMenu`) collects raw user inputs.
- The Service layer acts as the orchestrator: it runs input through the validation layer before delegating persistent state changes to repositories. This guarantees that invalid domain models or state updates never reach the persistence layer or pollute the system.

### Persistence Access Restriction

Validators **should not directly access the persistence package**.

- **Architectural Justification:** The validation package must focus on stateless constraint checking (e.g., non-null checks, string formatting, numerical range validation).
- If a stateful check requires database interaction (such as verifying unique constraints like `existsById`), the query must be executed by a **Service** component, which then passes the retrieved state to the validator or evaluates the business rule itself. Keeping validators decoupled from persistence preserves a strict top-down dependency flow (`UI → Service → Repository`).

---

## 4. Refactoring Preservation Strategy and Verification

To ensure that the refactoring preserves existing system behavior across all 10 menu operations, the following strategy and verification workflow are implemented:

- **Refactoring Strategy:**
  - **Regression Testing via Automated Unit/Integration Tests:** Executing a suite of JUnit tests covering all 10 service operations both before and after applying custom exceptions to verify functional equivalence.
  - **Contract Verification:** Ensuring that invalid input scenarios trigger equivalent error responses (now surfaced through formatted custom exception messages rather than raw stack traces).
- **Demonstrable Evidence:**
  1. A successful **JUnit Test Execution Report** displaying 100% pass rate for existing test cases covering the 10 menu workflows.
  2. **Console Execution Screenshots / Test Logs** illustrating that user-facing outputs and menu navigation remain identical when handling invalid data inputs.

---

## 5. Exception Catching Strategy in `ConsoleMenu`

The `ConsoleMenu` class handles exceptions using **multiple specific `catch` blocks in cascade within a single `try` block**, finalized by a fallback catch for the base exception.

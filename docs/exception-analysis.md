# Exception Handling Analysis - Requirement 6

## 1. GameZoneException: Checked vs Unchecked
We chose **Unchecked Exceptions** (extending `RuntimeException`). 
- **Reason:** Business validations and missing resource checks occur frequently in user interactions. Using unchecked exceptions keeps method signatures in services and repositories clean without forcing explicit `throws` clauses everywhere, simplifying codebase maintenance while allowing central handling at `ConsoleMenu`.

## 2. Custom Exception Hierarchy Advantages
Using `GameZoneException` over generic `IllegalArgumentException`:
- **Domain Specificity:** Allows distinct catch blocks in UI for granular user feedback.
- **Design Principles:** Adheres to Single Responsibility Principle (SRP) and Open/Closed Principle (OCP), as new error categories can be added without breaking existing handler code.

## 3. Architecture Role of `com.gamezone.validation`
- **Invocation Point:** Called directly inside Service methods before executing domain logic or calling repositories.
- **Layer Compliance:** Respects layered architecture. Validators do **NOT** access persistence or repository layers; they only check invariants on passed entities or DTOs.

## 4. Preserving External Behavior Strategy
- **Strategy:** Retaining identical condition checks while replacing generic exceptions with structured ones. 
- **Evidence:** Running all end-to-end console menu operations to verify that success paths and error outcomes yield expected user-facing messages.

## 5. UI Catch Strategy in ConsoleMenu
- **Approach:** Cascading multi-catch block catching specific subclasses (`ResourceNotFoundException`, `BusinessRuleException`, `InvalidDataException`, `PersistenceException`) before catching `GameZoneException`.
- **Reason:** Guarantees tailored user-friendly messages for each error category without exposing stack traces.
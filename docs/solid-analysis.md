# SOLID Analysis and Team Discussion

**Project:** GameZone Unicesar  
**Requirement:** 8 - SOLID Audit and Refactoring  
**Date:** 2026-10-21  
**Technical Leader:** Milton Andrés Maldonado Salcedo

---

## Question 1: Classification Criteria and Evidence Requirements

_What criteria did the team use to decide whether a code snippet constitutes a violation of a SOLID principle, if the principle is satisfied, or if it does not apply? What minimum evidence must accompany each verdict for it to be accepted in the consolidation?_

**Response:**
The team established standardized evaluation criteria across all 5 SOLID principles:

1. **Violation Criteria:**
   - **SRP:** Assigned if a class handles domain logic alongside presentation, or manages multiple distinct business domains (e.g., persistence parsing within service classes).
   - **OCP:** Assigned if adding new business entities or persistence formats requires modifying existing code structures instead of extending via polymorphism or abstractions.
   - **LSP:** Assigned if sub-types override superclass methods by throwing `UnsupportedOperationException`, breaking base contracts, or altering expected behavior invariants.
   - **ISP:** Assigned if classes depend on monolithic interfaces with unused methods, or if domain/persistence services lack segregated interface abstractions.
   - **DIP:** Assigned if high-level modules instantiate or directly reference concrete low-level implementations rather than depending on interface abstractions.

2. **Satisfied Criteria:** Assigned when code demonstrably adheres to abstraction boundaries, maintains single responsibilities, and leverages polymorphism without rigid type branching.

3. **Not Applicable (N/A) Criteria:** Assigned when a class does not participate in an inheritance hierarchy (LSP) or does not implement/expose interfaces (ISP).

4. **Minimum Evidence Requirements:** Each verdict in individual reports must specify:
   - File path and exact line numbers.
   - Code snippet or method signature demonstrating the finding.
   - Technical explanation detailing the root cause and architectural impact.
   - Proposed refactoring solution.

---

## Question 2: Boundary Findings

_Some violations manifest at the boundary between the code of two team members. What findings of this type did the team identify? How were they recorded in the consolidation and to whom was their correction assigned?_

**Response:**
Boundary findings occur when a class under one member's responsibility violates SOLID principles due to direct coupling or shared contracts with code assigned to another member.

The team identified key boundary findings during the audit:

- **`SaleService` (Technical Leader) & `ProductService` / `PersonService` (Developer 1 / Developer 2):** `SaleService` was parsing raw strings to reconstruct `Product` and `Person` domain objects. The boundary violation was consolidated under `SaleService` (DIP/SRP) and assigned to the Technical Leader to delegate reconstruction responsibility to persistence mappers.
- **`GameZoneUI` (Technical Leader) & All Domain Services:** `GameZoneUI` was performing explicit `instanceof Console` checks to determine extended warranty eligibility during sale creation. This boundary violation was consolidated under `GameZoneUI` (SRP) and assigned to the Technical Leader to encapsulate warranty eligibility rules within `SaleService` and domain models.

In the consolidation matrix, boundary findings are assigned a single primary owner (the maintainer of the class hosting the violation) while noting secondary contributors for coordinated pull requests.

---

## Question 3: Relationship between SOLID and GRASP Audits

_What relationship did the team find between the findings of this audit and those of the GRASP audit from Requirement 7? Were there SOLID violations in code already fixed by GRASP, or GRASP fixes that solved a SOLID problem?_

**Response:**
The team identified a direct synergy between GRASP patterns and SOLID principles:

1. **GRASP Fixes resolving SOLID Issues:**
   - Applying **GRASP Controller / Information Expert** in Requirement 7 removed direct persistence logic from `GameZoneUI` and moved calculation responsibilities into entities, directly resolving multiple **SRP** violations.
   - Introducing **GRASP Protected Variations (V-14)** in `Sale.canBeReturned(LocalDate)` eliminated direct reliance on system clocks, facilitating adherence to **OCP** and testability.

2. **SOLID Violations persisting in GRASP Refactored Code:**
   - Although GRASP introduced interfaces like `IStockService` for stock reduction, `SaleService` and `GameZoneUI` still depended on concrete repositories and services (`SaleRepository`, `ProductService`), maintaining **DIP** violations.
   - While low-level persistence files were created in Req 7 (`FileWarrantyRepository`), higher-level services lacked interface contracts (`ISaleRepository`, `ISaleService`), highlighting the need for full **DIP/ISP** compliance in Requirement 8.

---

## Question 4: Non-Regression Strategy

_The correction must preserve the external behavior of the system. What strategy does the team follow to guarantee that all menu operations continue to work identically? What evidence is shown at the end?_

**Response:**
To guarantee absolute functional non-regression, the team follows a structured verification protocol:

1. **Baseline Menu Execution:** Before applying refactoring changes, all 8 interactive sub-menus in `GameZoneUI` (Products, Persons, Sales, Accessories, Promotions, Returns, Monthly Balance, Warranties) are executed using a standardized test input script.
2. **Automated Compilation & Verification:** After each refactoring step, `mvn clean compile` is executed to verify compilation integrity and contract compliance.
3. **End-to-End Behavioral Equivalence:** The interactive console UI is tested with identical input sequences post-refactoring to verify that receipt formatting, error messages, and output summaries remain identical to baseline runs.
4. **Final Evidence:** Comparative execution logs and `BUILD SUCCESS` confirmation outputs are documented in the final refactoring report.

---

## Question 5: Order of Corrections and Coordination

_In what order are violations corrected and why? When correcting a violation involves code from more than one team member, how is work coordinated to avoid conflicts on the shared branch?_

**Response:**

1. **Refactoring Execution Order:**
   - **Step 1: Interface Abstractions (DIP/ISP):** Define repository and service interfaces (`ISaleRepository`, `ISaleService`, `IProductService`, etc.) to establish contracts first.
   - **Step 2: Persistence Layer (SRP/OCP):** Implement concrete repositories and file mappers, moving raw CSV parsing out of business services.
   - **Step 3: Business Services (SRP/OCP):** Refactor domain services to depend exclusively on interface abstractions via constructor injection.
   - **Step 4: Presentation / UI Layer (SRP/OCP):** Modularize UI controllers and inject service abstractions into `GameZoneUI` and `Main`.

2. **Branch Coordination & Git Workflow:**
   - Work is performed sequentially on feature branches branching off `develop`.
   - When refactoring touches shared contracts (e.g., modifying `ProductService` signatures used by `SaleService`), team members notify the group and update interface contracts first.
   - Pull Requests (PRs) targeting `develop` require code review and passing `mvn clean compile` checks before merging to avoid branch divergence.

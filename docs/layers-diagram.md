# Layers Diagram — GameZoneUnicesar

This diagram shows the four architectural layers of the system, the classes that belong to each layer, and the allowed dependencies between them.

\```mermaid
flowchart TB

    subgraph UI["UI Layer"]
        ConsoleUI
        Main
    end

    subgraph Service["Service Layer"]
        PersonService
        ProductService
        SaleService
    end

    subgraph Persistence["Persistence Layer"]
        PersonRepository
        FilePersonRepository
        ProductRepository
        FileProductRepository
        SaleRepository
        FileSaleRepository
    end

    subgraph Model["Model Layer"]
        Person
        Client
        Seller
        Product
        VideoGame
        Console
        Sale
    end

    UI --> Service
    Service --> Persistence
    Service --> Model
    Persistence --> Model
\```

## Dependency rules

- **UI** depends only on **Service**. It never accesses Persistence or Model directly.
- **Service** depends on both **Persistence** (to store/retrieve data) and **Model** (to work with domain objects).
- **Persistence** depends only on **Model** (to know what it is saving/loading).
- **Model** does not depend on any other layer. It contains no file-access logic and no references to the UI layer.
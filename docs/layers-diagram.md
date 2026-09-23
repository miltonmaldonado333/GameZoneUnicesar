# Arquitectura por Capas - GameZone Unicesar

```mermaid
graph TD
    subgraph UI["Capa de Presentación (UI)"]
        GameZoneUI[GameZoneUI.java]
        MainClass[Main.java]
    end

    subgraph Service["Capa de Servicio (Lógica de Negocio)"]
        SaleService[SaleService.java]
        AccessoryService[AccessoryService.java]
        PersonService[PersonService.java]
    end

    subgraph Persistence["Capa de Persistencia"]
        AccessoryRepository[AccessoryRepository.java]
        PersonRepository[PersonRepository.java]
        SaleRepository[SaleRepository.java]
    end

    subgraph Model["Capa de Modelo (Entidades)"]
        Product[Product.java]
        Accessory[Accessory.java]
        Controller[Controller.java]
        Cable[Cable.java]
        Memory[Memory.java]
        Person[Person.java]
    end

    MainClass --> GameZoneUI
    MainClass --> AccessoryService
    MainClass --> AccessoryRepository

    GameZoneUI --> SaleService
    GameZoneUI --> AccessoryService
    GameZoneUI --> PersonService

    SaleService --> AccessoryService

    AccessoryService --> AccessoryRepository
    PersonService --> PersonRepository

    AccessoryRepository --> Accessory
    PersonRepository --> Person

    Controller --> Accessory
    Cable --> Accessory
    Memory --> Accessory
    Accessory --> Product

    style UI fill:#2d3748,color:#fff
    style Service fill:#2c5282,color:#fff
    style Persistence fill:#2f855a,color:#fff
    style Model fill:#744210,color:#fff
```

## Descripción de capas

- **UI:** interactúa con el usuario y delega en los servicios.
- **Service:** contiene la lógica de negocio (validaciones, reglas de registro).
- **Persistence:** se encarga de guardar y leer datos (archivos CSV).
- **Model:** representa las entidades del dominio. `Controller`, `Cable` y `Memory` heredan de `Accessory`, que a su vez hereda de `Product`.

# Arquitectura por Capas - GameZone Unicesar

````mermaid
graph TD
    subgraph UI["Capa de Presentacion (UI)"]
# Arquitectura por Capas - GameZone Unicesar

```mermaid
graph TD
    subgraph UI["Capa de Presentación (UI)"]
        GameZoneUI[GameZoneUI.java]
        MainClass[Main.java]
    end

    subgraph Service["Capa de Servicio (Logica de Negocio)"]
        SaleService[SaleService.java]
        AccessoryService[AccessoryService.java]
        PersonService[PersonService.java]
        PromotionService[PromotionService.java]
    subgraph Service["Capa de Servicio (Lógica de Negocio)"]
        SaleService[SaleService.java]
        AccessoryService[AccessoryService.java]
        PersonService[PersonService.java]
    end

    subgraph Persistence["Capa de Persistencia"]
        AccessoryRepository[AccessoryRepository.java]
        PersonRepository[PersonRepository.java]
        SaleRepository[SaleRepository.java]
        PromotionRepository[PromotionRepository.java]
    end

    subgraph Model["Capa de Modelo (Entidades)"]
        Product[Product.java]
        Accessory[Accessory.java]
        Controller[Controller.java]
        Cable[Cable.java]
        Memory[Memory.java]
        Person[Person.java]
        Sale[Sale.java]
        Promotion[Promotion.java]
        PercentageDiscount[PercentageDiscount.java]
        CategoryDiscount[CategoryDiscount.java]
        BulkPurchaseDiscount[BulkPurchaseDiscount.java]
    end

    MainClass --> GameZoneUI
    MainClass --> AccessoryService
    MainClass --> AccessoryRepository

    GameZoneUI --> SaleService
    GameZoneUI --> AccessoryService
    GameZoneUI --> PersonService
    GameZoneUI --> PromotionService

    SaleService --> AccessoryService
    SaleService --> PromotionService

    AccessoryService --> AccessoryRepository
    PersonService --> PersonRepository
    PromotionService --> PromotionRepository

    AccessoryRepository --> Accessory
    PersonRepository --> Person
    PromotionRepository --> Promotion

    SaleService --> AccessoryService

    AccessoryService --> AccessoryRepository
    PersonService --> PersonRepository

    AccessoryRepository --> Accessory
    PersonRepository --> Person

    Controller --> Accessory
    Cable --> Accessory
    Memory --> Accessory
    Accessory --> Product

    PercentageDiscount --> Promotion
    CategoryDiscount --> Promotion
    BulkPurchaseDiscount --> Promotion

    PromotionService -.->|calculateDiscount| Sale
    CategoryDiscount -.->|filters by type| Product
    SaleService -.->|applies best promotion| Sale

    style UI fill:#2d3748,color:#fff
    style Service fill:#2c5282,color:#fff
    style Persistence fill:#2f855a,color:#fff
    style Model fill:#744210,color:#fff
````

## Descripcion de capas

- **UI:** interactua con el usuario y delega en los servicios.
- **Service:** contiene la logica de negocio (validaciones, reglas de registro, seleccion de la mejor promocion).
- **Persistence:** se encarga de guardar y leer datos (archivos CSV).
- **Model:** representa las entidades del dominio. Controller, Cable y Memory heredan de Accessory, que a su vez hereda de Product. PercentageDiscount, CategoryDiscount y BulkPurchaseDiscount heredan de la clase abstracta Promotion.

## Modulo de Promociones (nuevo)

- PromotionRepository persiste todas las promociones en data/promotions.csv usando un discriminador de tipo (PERCENTAGE, CATEGORY, BULK).
- PromotionService implementa el registro de cada tipo de promocion, el listado de promociones vigentes, y la logica de seleccion de la mejor promocion aplicable a una venta (findBestPromotionFor).
- SaleService.registerSale invoca PromotionService.findBestPromotionFor(sale) al registrar una venta, y si retorna una promocion, aplica su descuento al total de la venta.
- CategoryDiscount.calculateDiscount depende de Product (mediante instanceof VideoGame / instanceof Console) para filtrar los productos de la categoria objetivo.

## Descripción de capas

- **UI:** interactúa con el usuario y delega en los servicios.
- **Service:** contiene la lógica de negocio (validaciones, reglas de registro).
- **Persistence:** se encarga de guardar y leer datos (archivos CSV).
- **Model:** representa las entidades del dominio. `Controller`, `Cable` y `Memory` heredan de `Accessory`, que a su vez hereda de `Product`.

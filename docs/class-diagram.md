classDiagram
direction TB

    %% ==========================================
    %% 1. MODEL LAYER (com.gamezone.model)
    %% ==========================================
    namespace com.gamezone.model {
        class Person {
            <<abstract>>
            -String id
            -String name
            -String phone
            +getId() String
            +getName() String
        }

        class Client {
            -String email
            -List~Sale~ purchaseHistory
        }

        class Seller {
            -String employeeId
            -String shift
        }

        class Product {
            <<abstract>>
            -String id
            -String title
            -double price
            -int quantity
            +getDescription()* String
            +getId() String
            +getPrice() double
            +getQuantity() int
            +setQuantity(quantity)
        }

        class VideoGame {
            -String platform
            -String genre
            -String ageRating
            +getDescription() String
        }

        class Console {
            -String brand
            -String model
            -String generation
            +getDescription() String
        }

        class Accessory {
            <<abstract>>
            -List~String~ compatibleConsoleIds
            +getCompatibleConsoleIds() List~String~
            +getDescription()* String
        }

        class Controller {
            -String connectionType
            +getDescription() String
        }

        class Cable {
            -double length
            -String connectorType
            +getDescription() String
        }

        class Memory {
            -int capacity
            -String storageType
            +getDescription() String
        }

        class Sale {
            -String id
            -String date
            -double total
            +calculateTotal() double
        }
    }

    %% ==========================================
    %% 2. PERSISTENCE LAYER (com.gamezone.persistence)
    %% ==========================================
    namespace com.gamezone.persistence {
        class PersonRepository {
            <<interface>>
        }
        class FilePersonRepository {
            +saveAll(List~Person~)
            +loadAll() List~Person~
        }
        class ProductRepository {
            <<interface>>
        }
        class FileProductRepository {
            +saveAll(List~Product~)
            +loadAll() List~Product~
        }
        class SaleRepository {
            +saveAll(List~Sale~)
            +loadAll() List~Sale~
        }
        class AccessoryRepository {
            +saveAll(List~Accessory~)
            +loadAll() List~Accessory~
        }
    }

    %% ==========================================
    %% 3. SERVICE LAYER (com.gamezone.service)
    %% ==========================================
    namespace com.gamezone.service {
        class PersonService {
            +registerClient(Client)
            +listClients()
            +listSellers()
        }
        class ProductService {
            +registerVideoGame(VideoGame)
            +registerConsole(Console)
            +listProducts()
            +updateStock(id, quantity)
        }
        class AccessoryService {
            +registerController(Controller)
            +registerCable(Cable)
            +registerMemory(Memory)
            +listAllAccessories()
            +findCompatibleWith(consoleId)
            +updateStock(id, quantity)
        }
        class SaleService {
            +registerSale(Client, Seller, items)
            +listAllSales()
        }
    }

    %% ==========================================
    %% 4. USER INTERFACE LAYER (com.gamezone.ui)
    %% ==========================================
    namespace com.gamezone.ui {
        class GameZoneUI {
            +start()
            -showMainMenu()
            -showAccessoryMenu()
        }
    }

    %% ==========================================
    %% STRUCTURAL AND INHERITANCE RELATIONSHIPS
    %% ==========================================
    Person <|-- Client
    Person <|-- Seller

    Product <|-- VideoGame
    Product <|-- Console
    Product <|-- Accessory

    Accessory <|-- Controller
    Accessory <|-- Cable
    Accessory <|-- Memory

    PersonRepository <|.. FilePersonRepository
    ProductRepository <|.. FileProductRepository

    %% ==========================================
    %% DOMAIN RELATIONSHIPS (Association/Aggregation)
    %% ==========================================
    Sale o-- Client : "places"
    Sale o-- Seller : "handles"
    Sale o-- Product : "contains"

    %% ==========================================
    %% ARCHITECTURAL DEPENDENCIES (Layer Rules)
    %% ==========================================
    %% 1. UI interacts with Services
    GameZoneUI ..> PersonService
    GameZoneUI ..> ProductService
    GameZoneUI ..> SaleService
    GameZoneUI ..> AccessoryService

    %% 2. Services interact with each other and Persistence
    SaleService ..> PersonService : "validates"
    SaleService ..> ProductService : "updates stock"
    SaleService ..> AccessoryService : "updates stock"
    SaleService ..> SaleRepository

    PersonService ..> FilePersonRepository
    ProductService ..> FileProductRepository
    AccessoryService ..> AccessoryRepository

    %% 3. Persistence injects into Model
    FilePersonRepository ..> Person
    FileProductRepository ..> Product
    AccessoryRepository ..> Accessory
    SaleRepository ..> Sale

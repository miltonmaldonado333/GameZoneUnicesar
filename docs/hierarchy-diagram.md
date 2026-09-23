classDiagram
direction TB

    %% ==========================================
    %% 1. PERSON HIERARCHY
    %% ==========================================
    class Person {
        <<abstract>>
    }
    class Client {
        <<concrete>>
    }
    class Seller {
        <<concrete>>
    }

    %% ==========================================
    %% 2. PRODUCT & ACCESSORY HIERARCHY
    %% ==========================================
    class Product {
        <<abstract>>
    }
    class VideoGame {
        <<concrete>>
    }
    class Console {
        <<concrete>>
    }

    class Accessory {
        <<abstract>>
    }
    class Controller {
        <<concrete>>
    }
    class Cable {
        <<concrete>>
    }
    class Memory {
        <<concrete>>
    }

    %% ==========================================
    %% 3. PROMOTION HIERARCHY (Requirement 2)
    %% ==========================================
    class Promotion {
        <<abstract>>
    }
    class PercentageDiscount {
        <<concrete>>
    }
    class CategoryDiscount {
        <<concrete>>
    }
    class BulkPurchaseDiscount {
        <<concrete>>
    }

    %% ==========================================
    %% INHERITANCE RELATIONSHIPS
    %% ==========================================

    %% Person Branch
    Person <|-- Client
    Person <|-- Seller

    %% Product Base Branch
    Product <|-- VideoGame
    Product <|-- Console
    Product <|-- Accessory

    %% Accessory Sub-Branch
    Accessory <|-- Controller
    Accessory <|-- Cable
    Accessory <|-- Memory

    %% Promotion Branch
    Promotion <|-- PercentageDiscount
    Promotion <|-- CategoryDiscount
    Promotion <|-- BulkPurchaseDiscount
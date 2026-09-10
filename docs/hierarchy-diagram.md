```mermaid

classDiagram

&#x20;   direction TB



&#x20;   class Person {

&#x20;       <<abstract>>

&#x20;   }

&#x20;   class Client {

&#x20;   }

&#x20;   class Seller {

&#x20;   }



&#x20;   Person <|-- Client

&#x20;   Person <|-- Seller



&#x20;   class Product {

&#x20;       <<abstract>>

&#x20;   }

&#x20;   class VideoGame {

&#x20;   }

&#x20;   class Console {

&#x20;   }



&#x20;   Product <|-- VideoGame

&#x20;   Product <|-- Console


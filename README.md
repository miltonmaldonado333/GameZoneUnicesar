# GameZone Unicesar[cite: 1]

## Project Description[cite: 1]
GameZone Unicesar is an information system developed for a video game store located in the university sector of Valledupar, dedicated to the sale of video games and consoles.[cite: 1] The system allows systematizing the store's processes, managing the recording of relevant information automatically.[cite: 1]

The system supports the following main functionalities:[cite: 1]
* **People and roles management:** Administration of customers (purchase history) and sellers (employee code and shift).[cite: 1]
* **Products management:** Inventory administration of video games (platform, genre, rating) and consoles (brand, model, generation).[cite: 1]
* **Sales registration:** Transactions with automatic inventory deduction, total calculation, and association of customers and sellers.[cite: 1]
* **Information query:** Inventory, customer, seller, and sales history listings.[cite: 1]

All managed information (products, people, and sales) is preserved between executions thanks to a file persistence system.[cite: 1]

## System Architecture[cite: 1]
The project is developed in **Java** and configured as a **Maven** project.[cite: 1] The design is organized under the four-layer architecture model:[cite: 1]

1. **Model (`com.gamezone.model`):** Contains the business domain classes with their attributes and abstract hierarchies.[cite: 1]
2. **Persistence (`com.gamezone.persistence`):** Contains the classes responsible for saving and retrieving information from the files managed by the application.[cite: 1]
3. **Services (`com.gamezone.service`):** Contains the classes responsible for business logic, validations, and rules (e.g., stock validation).[cite: 1]
4. **User Interface (`com.gamezone.ui`):** Contains the interactive console menu that communicates exclusively with the services layer.[cite: 1]

## Compilation and Execution Instructions[cite: 1]

### Prerequisites
* Java Development Kit (JDK) installed (version 17 or higher recommended).
* Apache Maven installed and configured in the environment variables.

### 1. Compile the project
To compile the source code and download the necessary dependencies, open a terminal in the project's root folder and run the following command:
```bash
mvn clean install
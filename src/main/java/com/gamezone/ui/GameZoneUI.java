package com.gamezone.ui;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.PersistenceException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Accessory;
import com.gamezone.model.Client;
import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.Promotion;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.model.Warranty;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.PromotionService;
import com.gamezone.service.ReturnService;
import com.gamezone.service.SaleService;
import com.gamezone.service.WarrantyService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Handles the interactive console user interface for the GameZone system.
 * Serves strictly as a presentation controller in accordance with GRASP principles.
 * Manages user interaction, menu rendering, and input/output parsing, delegating 
 * all domain operations and orchestration tasks to specialized service classes.
 * 
 * <p>Applies the Single Responsibility Principle (SRP) by isolating console I/O, 
 * ensuring high cohesion and low coupling with business entities.</p>
 */
public class GameZoneUI {

    private final BufferedReader reader;
    private final SaleService saleService;
    private final ProductService productService;
    private final PersonService personService;
    private final AccessoryService accessoryService;
    private final PromotionService promotionService;
    private final ReturnService returnService;
    private final WarrantyService warrantyService;

    /**
     * Constructs a {@code GameZoneUI} instance initializing console reader streams and injecting 
     * required service dependencies.
     * 
     * <p>Applies Dependency Inversion (SOLID) by receiving service instances externally rather 
     * than constructing them internally. Configures standard output and input streams to use UTF-8.</p>
     *
     * @param saleService the service handling sale transactions
     * @param productService the service managing product catalog operations
     * @param personService the service handling client and seller records
     * @param accessoryService the service managing game accessories
     * @param promotionService the service orchestrating promotional discounts
     * @param returnService the service processing product returns and financial balances
     * @param warrantyService the service managing warranty certificates
     */
    public GameZoneUI(SaleService saleService, ProductService productService,
            PersonService personService, AccessoryService accessoryService,
            PromotionService promotionService, ReturnService returnService,
            WarrantyService warrantyService) {
        
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8.name()));
        } catch (java.io.UnsupportedEncodingException e) {
            System.out.println("No se pudo configurar UTF-8 en la consola: " + e.getMessage());
        }

        this.reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        
        this.saleService = saleService;
        this.productService = productService;
        this.personService = personService;
        this.accessoryService = accessoryService;
        this.promotionService = promotionService;
        this.returnService = returnService;
        this.warrantyService = warrantyService;
    }

    /**
     * Starts the main application menu loop, maintaining session lifecycle 
     * until the user selects the exit option.
     */
    public void start() {
        int option = -1;

        System.out.println("===========================================");
        System.out.println("        BIENVENIDO AL SISTEMA GAMEZONE     ");
        System.out.println("===========================================");

        do {
            displayMainMenu();
            option = readOption();
            processMainMenuOption(option);
        } while (option != 0);

        System.out.println("Saliendo del sistema GameZone... ¡Adiós!");

        try {
            reader.close();
        } catch (IOException e) {
            System.out.println("Error al cerrar el lector: " + e.getMessage());
        }
    }

    // ================= I/O HELPER METHODS (SRP / GRASP) =================

    /**
     * Prompts the user and reads a raw string from standard input.
     *
     * @param message prompt message to display
     * @return user input string
     * @throws IOException if an I/O error occurs
     */
    private String promptString(String message) throws IOException {
        System.out.print(message);
        return reader.readLine();
    }

    /**
     * Prompts the user and parses an integer value from standard input.
     *
     * @param message prompt message to display
     * @return parsed integer value
     * @throws IOException if an I/O error occurs
     * @throws NumberFormatException if the input cannot be parsed into an integer
     */
    private int promptInt(String message) throws IOException, NumberFormatException {
        System.out.print(message);
        return Integer.parseInt(reader.readLine());
    }

    /**
     * Prompts the user and parses a double precision floating-point number.
     *
     * @param message prompt message to display
     * @return parsed double value
     * @throws IOException if an I/O error occurs
     * @throws NumberFormatException if the input cannot be parsed into a double
     */
    private double promptDouble(String message) throws IOException, NumberFormatException {
        System.out.print(message);
        return Double.parseDouble(reader.readLine());
    }

    /**
     * Prompts the user and parses an ISO local date (YYYY-MM-DD).
     *
     * @param message prompt message to display
     * @return parsed {@link LocalDate} instance
     * @throws IOException if an I/O error occurs
     * @throws DateTimeParseException if the input format is invalid
     */
    private LocalDate promptDate(String message) throws IOException, DateTimeParseException {
        System.out.print(message);
        return LocalDate.parse(reader.readLine());
    }

    // ================= MAIN MENU =================

    /**
     * Displays main navigation options to the console.
     */
    private void displayMainMenu() {
        System.out.println("\n---------------- MENÚ PRINCIPAL ----------------");
        System.out.println("1. Gestión de Productos (Consolas y Videojuegos)");
        System.out.println("2. Gestión de Personas");
        System.out.println("3. Gestión de Ventas");
        System.out.println("4. Gestión de Accesorios");
        System.out.println("5. Gestión de Promociones");
        System.out.println("6. Gestión de Devoluciones");
        System.out.println("7. Consultar Balance Mensual");
        System.out.println("8. Gestión de Garantías");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opción: ");
    }

    /**
     * Reads menu selection input safely without crashing on formatting errors.
     *
     * @return chosen option number, or {@code -1} if invalid
     */
    private int readOption() {
        try {
            return Integer.parseInt(reader.readLine());
        } catch (NumberFormatException e) {
            System.out.println("Error: Por favor, ingrese una opción numérica válida.");
            return -1;
        } catch (IOException e) {
            System.out.println("Error leyendo la entrada: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Routes the selected main menu option to its corresponding subsystem handler.
     *
     * @param option selected menu option number
     */
    private void processMainMenuOption(int option) {
        switch (option) {
            case 1 -> handleProductMenu();
            case 2 -> handlePersonMenu();
            case 3 -> handleSalesMenu();
            case 4 -> handleAccessoryMenu();
            case 5 -> handlePromotionMenu();
            case 6 -> handleReturnMenu();
            case 7 -> generateMonthlyBalance();
            case 8 -> handleWarrantyMenu();
            case 0, -1 -> {
            }
            default -> System.out.println("Opción inválida. Por favor, intente de nuevo.");
        }
    }

    // ================= PRODUCT SUBMENU =================

    /**
     * Manages the interactive product subsystem menu loop.
     */
    private void handleProductMenu() {
        int option = -1;
        do {
            System.out.println("\n============= GESTIÓN DE PRODUCTOS ==============");
            System.out.println("1. Registrar un nuevo videojuego");
            System.out.println("2. Registrar una nueva consola");
            System.out.println("3. Listar todos los productos en inventario");
            System.out.println("0. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");

            option = readOption();
            switch (option) {
                case 1 -> registerVideoGame();
                case 2 -> registerConsole();
                case 3 -> listProducts();
                case 0 -> {
                }
                default -> System.out.println("Opción inválida.");
            }
        } while (option != 0);
    }

    /**
     * Prompts for video game attributes and delegates registration to {@link ProductService}.
     */
    private void registerVideoGame() {
        try {
            System.out.println("\n--- REGISTRAR VIDEOJUEGO ---");
            String id = promptString("ID: ");
            String title = promptString("Título: ");
            double price = promptDouble("Precio: ");
            int stock = promptInt("Stock: ");
            String platform = promptString("Plataforma: ");
            String genre = promptString("Género: ");
            String ageRating = promptString("Clasificación de Edad: ");

            boolean registered = productService.registerVideoGame(platform, genre, ageRating, id, title, price, stock);
            if (registered) {
                System.out.println("¡Videojuego registrado y guardado exitosamente!");
            } else {
                System.out.println("Error: Ya existe un producto con este ID.");
            }
        } catch (ResourceNotFoundException e) {
            System.out.println("No se encontró el recurso solicitado: " + e.getMessage());
        } catch (BusinessRuleException e) {
            System.out.println("No se puede completar la operación: " + e.getMessage());
        } catch (InvalidDataException e) {
            System.out.println("Los datos ingresados no son válidos: " + e.getMessage());
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (NumberFormatException e) {
            System.out.println("Los datos ingresados no son válidos: Se esperaba un valor numérico.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Prompts for gaming console details and delegates creation to {@link ProductService}.
     */
    private void registerConsole() {
        try {
            System.out.println("\n--- REGISTRAR CONSOLA ---");
            String id = promptString("ID: ");
            String title = promptString("Título: ");
            double price = promptDouble("Precio: ");
            int stock = promptInt("Stock: ");
            String brand = promptString("Marca: ");
            String model = promptString("Modelo: ");
            String generation = promptString("Generación: ");

            boolean registered = productService.registerConsole(brand, model, generation, id, title, price, stock);
            if (registered) {
                System.out.println("¡Consola registrada y guardada exitosamente!");
            } else {
                System.out.println("Error: Ya existe un producto con este ID.");
            }
        } catch (ResourceNotFoundException e) {
            System.out.println("No se encontró el recurso solicitado: " + e.getMessage());
        } catch (BusinessRuleException e) {
            System.out.println("No se puede completar la operación: " + e.getMessage());
        } catch (InvalidDataException e) {
            System.out.println("Los datos ingresados no son válidos: " + e.getMessage());
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (NumberFormatException e) {
            System.out.println("Los datos ingresados no son válidos: Se esperaba un valor numérico.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Fetches and displays all registered products in the inventory.
     */
    private void listProducts() {
        try {
            System.out.println("\n--- INVENTARIO DE PRODUCTOS ---");
            List<Product> products = productService.getAllProducts();
            if (products == null || products.isEmpty()) {
                System.out.println("El inventario está vacío.");
            } else {
                for (Product p : products) {
                    System.out.println(p.getFullDescription());
                }
            }
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    // ================= PERSON SUBMENU =================

    /**
     * Manages the person records subsystem menu loop.
     */
    private void handlePersonMenu() {
        int option = -1;
        do {
            System.out.println("\n============= GESTIÓN DE PERSONAS ==============");
            System.out.println("1. Registrar un nuevo cliente");
            System.out.println("2. Registrar un nuevo vendedor");
            System.out.println("3. Listar todos los clientes registrados");
            System.out.println("4. Listar todos los vendedores registrados");
            System.out.println("0. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");

            option = readOption();
            switch (option) {
                case 1 -> registerClient();
                case 2 -> registerSeller();
                case 3 -> listClients();
                case 4 -> listSellers();
                case 0 -> {
                }
                default -> System.out.println("Opción inválida.");
            }
        } while (option != 0);
    }

    /**
     * Prompts for client details and delegates insertion to {@link PersonService}.
     */
    private void registerClient() {
        try {
            System.out.println("\n--- REGISTRAR CLIENTE ---");
            String name = promptString("Nombre: ");
            String id = promptString("Identificación: ");
            String phone = promptString("Teléfono: ");
            String email = promptString("Correo Electrónico: ");

            personService.registerClient(name, id, phone, email);
            System.out.println("¡Cliente registrado exitosamente!");
        } catch (ResourceNotFoundException e) {
            System.out.println("No se encontró el recurso solicitado: " + e.getMessage());
        } catch (BusinessRuleException e) {
            System.out.println("No se puede completar la operación: " + e.getMessage());
        } catch (InvalidDataException e) {
            System.out.println("Los datos ingresados no son válidos: " + e.getMessage());
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Prompts for seller details and delegates creation to {@link PersonService}.
     */
    private void registerSeller() {
        try {
            System.out.println("\n--- REGISTRAR VENDEDOR ---");
            String name = promptString("Nombre: ");
            String identification = promptString("Identificación: ");
            String phone = promptString("Teléfono: ");
            String employeeCode = promptString("Código de Empleado: ");
            String workShift = promptString("Turno de Trabajo: ");

            personService.registerSeller(name, identification, phone, employeeCode, workShift);
            System.out.println("¡Vendedor registrado exitosamente!");
        } catch (ResourceNotFoundException e) {
            System.out.println("No se encontró el recurso solicitado: " + e.getMessage());
        } catch (BusinessRuleException e) {
            System.out.println("No se puede completar la operación: " + e.getMessage());
        } catch (InvalidDataException e) {
            System.out.println("Los datos ingresados no son válidos: " + e.getMessage());
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Fetches and displays all registered client records.
     */
    private void listClients() {
        try {
            System.out.println("\n--- LISTA DE CLIENTES ---");
            List<Client> clients = personService.getAllClients();
            if (clients == null || clients.isEmpty()) {
                System.out.println("No hay clientes registrados.");
            } else {
                for (Client c : clients) {
                    System.out.println("ID: " + c.getIdentification() + " | Nombre: " + c.getName() + " | Correo: " + c.getEmail());
                }
            }
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Fetches and displays all registered seller records.
     */
    private void listSellers() {
        try {
            System.out.println("\n--- LISTA DE VENDEDORES ---");
            List<Seller> sellers = personService.getAllSellers();
            if (sellers == null || sellers.isEmpty()) {
                System.out.println("No hay vendedores registrados.");
            } else {
                for (Seller s : sellers) {
                    System.out.println("Código: " + s.getEmployeeCode() + " | Nombre: " + s.getName() + " | Turno: " + s.getWorkShift());
                }
            }
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    // ================= SALES SUBMENU =================

    /**
     * Manages the sales transaction subsystem menu loop.
     */
    private void handleSalesMenu() {
        int option = -1;
        do {
            System.out.println("\n============= GESTIÓN DE VENTAS ==============");
            System.out.println("1. Registrar una nueva venta");
            System.out.println("2. Consultar el historial completo de ventas");
            System.out.println("3. Consultar el historial de compras de un cliente");
            System.out.println("4. Consultar las ventas atendidas por un vendedor");
            System.out.println("0. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");

            option = readOption();
            switch (option) {
                case 1 -> registerSale();
                case 2 -> listAllSales();
                case 3 -> listSalesByClient();
                case 4 -> listSalesBySeller();
                case 0 -> {
                }
                default -> System.out.println("Opción inválida.");
            }
        } while (option != 0);
    }

    /**
     * Orchestrates interactive sale registration for multiple items.
     * 
     * <p><b>GRASP Refactoring Note:</b> Uses a unified facade method 
     * ({@link ProductService#findSaleableItemById(String)}) to resolve products or accessories, 
     * replacing sequential fallback exception catching from presentation code.</p>
     */
    private void registerSale() {
        try {
            System.out.println("\n--- REGISTRAR NUEVA VENTA UNIFICADA ---");

            String clientId = promptString("Ingrese la Identificación del Cliente: ");
            Client client = personService.findClientById(clientId);

            String sellerCode = promptString("Ingrese el Código de Empleado del Vendedor: ");
            Seller seller = personService.findSellerByCode(sellerCode);

            List<Product> products = new ArrayList<>();
            List<String> extendedWarrantyProductIds = new ArrayList<>();
            String addMore;
            do {
                String itemId = promptString("Ingrese el ID del Producto o Accesorio a añadir: ");

                Product item = productService.findProductById(itemId);

                if (item != null) {
                    products.add(item);
                    System.out.println("Artículo añadido al carrito: " + item.getTitle());

                    if (item instanceof Console) {
                        String extendedChoice = promptString("¿Desea agregar garantía extendida (+10% del costo) a la consola '" 
                                + item.getTitle() + "'? (s/n): ");
                        if (extendedChoice.equalsIgnoreCase("s")) {
                            extendedWarrantyProductIds.add(item.getId());
                        }
                    }
                } else {
                    System.out.println("Error: Producto o Accesorio no encontrado con ID: " + itemId);
                }

                addMore = promptString("¿Agregar otro artículo? (s/n): ");
            } while (addMore.equalsIgnoreCase("s"));

            Sale registeredSale = saleService.registerSale(client, seller, products, extendedWarrantyProductIds);
            System.out.println("\n¡Venta registrada exitosamente!");
            System.out.println(registeredSale.generateReceipt());

        } catch (ResourceNotFoundException e) {
            System.out.println("No se encontró el recurso solicitado: " + e.getMessage());
        } catch (BusinessRuleException e) {
            System.out.println("No se puede completar la operación: " + e.getMessage());
        } catch (InvalidDataException e) {
            System.out.println("Los datos ingresados no son válidos: " + e.getMessage());
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Fetches and displays the complete store sales history.
     */
    private void listAllSales() {
        try {
            System.out.println("\n--- HISTORIAL COMPLETO DE VENTAS ---");
            List<Sale> allSales = saleService.getAllSales();
            printSalesList(allSales);
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Prompts for a client ID and displays their purchase history.
     */
    private void listSalesByClient() {
        try {
            String searchClientId = promptString("Ingrese la Identificación del Cliente: ");
            List<Sale> clientSales = saleService.getSalesByClient(searchClientId);
            System.out.println("\n--- HISTORIAL DE COMPRAS DEL CLIENTE ---");
            printSalesList(clientSales);
        } catch (ResourceNotFoundException e) {
            System.out.println("No se encontró el recurso solicitado: " + e.getMessage());
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Prompts for a seller code and displays their processed sales history.
     */
    private void listSalesBySeller() {
        try {
            String searchSellerId = promptString("Ingrese el Código del Vendedor: ");
            List<Sale> sellerSales = saleService.getSalesBySeller(searchSellerId);
            System.out.println("\n--- HISTORIAL DE VENTAS DEL VENDEDOR ---");
            printSalesList(sellerSales);
        } catch (ResourceNotFoundException e) {
            System.out.println("No se encontró el recurso solicitado: " + e.getMessage());
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Formats and prints a collection of sale receipts to standard output.
     *
     * @param sales list of sales to display
     */
    private void printSalesList(List<Sale> sales) {
        if (sales == null || sales.isEmpty()) {
            System.out.println("No se encontraron registros de ventas.");
        } else {
            for (Sale s : sales) {
                System.out.println(s.generateReceipt());
            }
        }
    }

    // ================= ACCESSORY SUBMENU =================

    /**
     * Manages the accessory subsystem menu loop.
     */
    private void handleAccessoryMenu() {
        int option = -1;
        do {
            System.out.println("\n============= GESTIÓN DE ACCESORIOS ==============");
            System.out.println("1. Registrar un nuevo control");
            System.out.println("2. Registrar un nuevo cable");
            System.out.println("3. Registrar una nueva memoria");
            System.out.println("4. Listar todos los accesorios");
            System.out.println("5. Listar accesorios por tipo");
            System.out.println("6. Consultar accesorios compatibles para una consola");
            System.out.println("0. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");

            option = readOption();
            switch (option) {
                case 1 -> registerController();
                case 2 -> registerCable();
                case 3 -> registerMemory();
                case 4 -> listAllAccessories();
                case 5 -> listAccessoriesByType();
                case 6 -> consultCompatibleAccessories();
                case 0 -> {
                }
                default -> System.out.println("Opción inválida.");
            }
        } while (option != 0);
    }

    /**
     * Prompts for controller details and delegates creation to {@link AccessoryService}.
     */
    private void registerController() {
        try {
            System.out.println("\n--- REGISTRAR CONTROL ---");
            String id = promptString("ID: ");
            String title = promptString("Título: ");
            double price = promptDouble("Precio: ");
            int stock = promptInt("Stock: ");
            String connection = promptString("Tipo de Conexión (inalámbrico/cableado): ");

            String consolesInput = promptString("IDs de Consolas Compatibles (separados por coma, ej., 11,12) [Dejar vacío si no hay]: ");
            List<String> compatibleConsoles = new ArrayList<>();
            if (!consolesInput.isBlank()) {
                compatibleConsoles = Arrays.asList(consolesInput.split("\\s*,\\s*"));
            }

            accessoryService.registerController(id, title, price, stock, connection, compatibleConsoles);
            System.out.println("¡Control registrado exitosamente!");
        } catch (ResourceNotFoundException e) {
            System.out.println("No se encontró el recurso solicitado: " + e.getMessage());
        } catch (BusinessRuleException e) {
            System.out.println("No se puede completar la operación: " + e.getMessage());
        } catch (InvalidDataException e) {
            System.out.println("Los datos ingresados no son válidos: " + e.getMessage());
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (NumberFormatException e) {
            System.out.println("Los datos ingresados no son válidos: Se esperaba un valor numérico.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Prompts for cable accessory attributes and delegates registration to {@link AccessoryService}.
     */
    private void registerCable() {
        try {
            System.out.println("\n--- REGISTRAR CABLE ---");
            String id = promptString("ID: ");
            String title = promptString("Título: ");
            double price = promptDouble("Precio: ");
            int stock = promptInt("Stock: ");
            double length = promptDouble("Longitud (metros): ");
            String type = promptString("Tipo de Conector (ej., HDMI, USB): ");

            String consolesInput = promptString("IDs de Consolas Compatibles (separados por coma) [Dejar vacío si no hay]: ");
            List<String> compatibleConsoles = new ArrayList<>();
            if (!consolesInput.isBlank()) {
                compatibleConsoles = Arrays.asList(consolesInput.split("\\s*,\\s*"));
            }

            accessoryService.registerCable(id, title, price, stock, length, type, compatibleConsoles);
            System.out.println("¡Cable registrado exitosamente!");
        } catch (ResourceNotFoundException e) {
            System.out.println("No se encontró el recurso solicitado: " + e.getMessage());
        } catch (BusinessRuleException e) {
            System.out.println("No se puede completar la operación: " + e.getMessage());
        } catch (InvalidDataException e) {
            System.out.println("Los datos ingresados no son válidos: " + e.getMessage());
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (NumberFormatException e) {
            System.out.println("Los datos ingresados no son válidos: Se esperaba un valor numérico.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Prompts for memory card specs and delegates creation to {@link AccessoryService}.
     */
    private void registerMemory() {
        try {
            System.out.println("\n--- REGISTRAR MEMORIA ---");
            String id = promptString("ID: ");
            String title = promptString("Título: ");
            double price = promptDouble("Precio: ");
            int stock = promptInt("Stock: ");
            int capacity = promptInt("Capacidad (GB): ");
            String type = promptString("Tipo de Memoria (ej., SD, microSD): ");
            
            String consolesInput = promptString("IDs de Consolas Compatibles (separados por coma) [Dejar vacío si no hay]: ");
            List<String> compatibleConsoles = new ArrayList<>();
            if (!consolesInput.isBlank()) {
                compatibleConsoles = Arrays.asList(consolesInput.split("\\s*,\\s*"));
            }

            accessoryService.registerMemory(id, title, price, stock, capacity, type, compatibleConsoles);
            System.out.println("¡Memoria registrada exitosamente!");
        } catch (ResourceNotFoundException e) {
            System.out.println("No se encontró el recurso solicitado: " + e.getMessage());
        } catch (BusinessRuleException e) {
            System.out.println("No se puede completar la operación: " + e.getMessage());
        } catch (InvalidDataException e) {
            System.out.println("Los datos ingresados no son válidos: " + e.getMessage());
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (NumberFormatException e) {
            System.out.println("Los datos ingresados no son válidos: Se esperaba un valor numérico.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Displays all accessories stored in the inventory.
     */
    private void listAllAccessories() {
        try {
            System.out.println("\n--- TODOS LOS ACCESORIOS ---");
            List<Accessory> accessories = accessoryService.listAllAccessories();
            if (accessories == null || accessories.isEmpty()) {
                System.out.println("No hay accesorios registrados.");
            } else {
                accessories.forEach(a -> System.out.println(a.getFullDescription()));
            }
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Prompts for an accessory category type and lists matches.
     */
    private void listAccessoriesByType() {
        try {
            String type = promptString("Ingrese el tipo de accesorio (Controller/Cable/Memory): ");
            List<Accessory> accessories = accessoryService.listAccessoriesByType(type);
            if (accessories == null || accessories.isEmpty()) {
                System.out.println("No se encontraron accesorios de ese tipo.");
            } else {
                accessories.forEach(a -> System.out.println(a.getFullDescription()));
            }
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Queries and prints accessories compatible with a specified console ID.
     */
    private void consultCompatibleAccessories() {
        try {
            String consoleId = promptString("Ingrese el ID de la Consola para comprobar compatibilidad: ");
            List<Accessory> accessories = accessoryService.findAccessoriesCompatibleWith(consoleId);
            if (accessories == null || accessories.isEmpty()) {
                System.out.println("No se encontraron accesorios compatibles.");
            } else {
                accessories.forEach(a -> System.out.println(a.getFullDescription()));
            }
        } catch (ResourceNotFoundException e) {
            System.out.println("No se encontró el recurso solicitado: " + e.getMessage());
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    // ================= PROMOTION SUBMENU =================

    /**
     * Manages the promotion management subsystem menu loop.
     */
    private void handlePromotionMenu() {
        int option = -1;
        do {
            System.out.println("\n============= GESTIÓN DE PROMOCIONES ==============");
            System.out.println("1. Registrar promoción de descuento por porcentaje");
            System.out.println("2. Registrar promoción de descuento por categoría");
            System.out.println("3. Registrar promoción de descuento por compra al por mayor");
            System.out.println("4. Listar todas las promociones activas");
            System.out.println("5. Listar todas las promociones (activas e inactivas)");
            System.out.println("0. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");

            option = readOption();
            switch (option) {
                case 1 -> registerPercentageDiscount();
                case 2 -> registerCategoryDiscount();
                case 3 -> registerBulkPurchaseDiscount();
                case 4 -> listActivePromotions();
                case 5 -> listAllPromotions();
                case 0 -> {
                }
                default -> System.out.println("Opción inválida.");
            }
        } while (option != 0);
    }

    /**
     * Prompts for percentage discount parameters and registers it in {@link PromotionService}.
     */
    private void registerPercentageDiscount() {
        try {
            System.out.println("\n--- REGISTRAR DESCUENTO POR PORCENTAJE ---");
            String id = promptString("ID: ");
            String name = promptString("Nombre: ");
            LocalDate startDate = promptDate("Fecha de Inicio (YYYY-MM-DD): ");
            LocalDate endDate = promptDate("Fecha de Fin (YYYY-MM-DD): ");
            double percentage = promptDouble("Porcentaje de Descuento (ej., 10 o 15.5): ");

            promotionService.registerPercentageDiscount(id, name, startDate, endDate, percentage);
            System.out.println("¡Descuento por porcentaje registrado exitosamente!");
        } catch (BusinessRuleException e) {
            System.out.println("No se puede completar la operación: " + e.getMessage());
        } catch (InvalidDataException e) {
            System.out.println("Los datos ingresados no son válidos: " + e.getMessage());
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (NumberFormatException | DateTimeParseException e) {
            System.out.println("Los datos ingresados no son válidos: Formato de número o fecha incorrecto.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Prompts for category discount attributes and registers the promotion.
     * 
     * <p><b>GRASP Refactoring Note:</b> Invokes {@link ProductService#getAvailableCategories()} 
     * to obtain categories dynamically, removing reflection operations from presentation logic.</p>
     */
    private void registerCategoryDiscount() {
        try {
            System.out.println("\n--- REGISTRAR DESCUENTO POR CATEGORÍA ---");
            String id = promptString("ID: ");
            String name = promptString("Nombre: ");
            LocalDate startDate = promptDate("Fecha de Inicio (YYYY-MM-DD): ");
            LocalDate endDate = promptDate("Fecha de Fin (YYYY-MM-DD): ");
            double percentage = promptDouble("Porcentaje de Descuento (ej., 10 o 15.5): ");

            List<String> availableCategories = productService.getAvailableCategories();

            System.out.println("\nCategorías disponibles en inventario: " + availableCategories);
            String category = promptString("Ingrese la Categoría Objetivo de la lista anterior (VIDEOGAME, CONSOLE, ACCESSORY): ");

            promotionService.registerCategoryDiscount(id, name, startDate, endDate, percentage, category);
            System.out.println("¡Descuento por categoría registrado exitosamente!");
        } catch (BusinessRuleException e) {
            System.out.println("No se puede completar la operación: " + e.getMessage());
        } catch (InvalidDataException e) {
            System.out.println("Los datos ingresados no son válidos: " + e.getMessage());
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (NumberFormatException | DateTimeParseException e) {
            System.out.println("Los datos ingresados no son válidos: Formato de número o fecha incorrecto.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Prompts for bulk purchase parameters and registers a bulk discount promotion.
     */
    private void registerBulkPurchaseDiscount() {
        try {
            System.out.println("\n--- REGISTRAR DESCUENTO POR COMPRA AL POR MAYOR ---");
            String id = promptString("ID: ");
            String name = promptString("Nombre: ");
            LocalDate startDate = promptDate("Fecha de Inicio (YYYY-MM-DD): ");
            LocalDate endDate = promptDate("Fecha de Fin (YYYY-MM-DD): ");
            int minQuantity = promptInt("Cantidad Mínima: ");
            double percentage = promptDouble("Porcentaje de Descuento (ej., 10 o 15.5): ");

            promotionService.registerBulkPurchaseDiscount(id, name, startDate, endDate, minQuantity, percentage);
            System.out.println("¡Descuento por compra al por mayor registrado exitosamente!");
        } catch (BusinessRuleException e) {
            System.out.println("No se puede completar la operación: " + e.getMessage());
        } catch (InvalidDataException e) {
            System.out.println("Los datos ingresados no son válidos: " + e.getMessage());
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (NumberFormatException | DateTimeParseException e) {
            System.out.println("Los datos ingresados no son válidos: Formato de número o fecha incorrecto.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Fetches and displays currently active promotional discounts.
     */
    private void listActivePromotions() {
        try {
            System.out.println("\n--- PROMOCIONES ACTIVAS ---");
            List<Promotion> activePromos = promotionService.listActivePromotions();

            if (activePromos == null || activePromos.isEmpty()) {
                System.out.println("No hay promociones activas disponibles en este momento.");
                return;
            }

            for (Promotion promo : activePromos) {
                System.out.printf("ID: %s | Nombre: %s | Activa: %s a %s | %s%n",
                        promo.getId(),
                        promo.getName(),
                        promo.getStartDate(),
                        promo.getEndDate(),
                        promo.getDetails());
            }
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Fetches and displays all promotions (active and expired).
     */
    private void listAllPromotions() {
        try {
            System.out.println("\n--- TODAS LAS PROMOCIONES ---");
            List<Promotion> promotions = promotionService.listAllPromotions();
            if (promotions == null || promotions.isEmpty()) {
                System.out.println("No hay promociones registradas.");
            } else {
                for (Promotion p : promotions) {
                    System.out.println("ID: " + p.getId() + " | Nombre: " + p.getName()
                            + " | Activa: " + p.getStartDate() + " a " + p.getEndDate());
                }
            }
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    // ================= RETURN SUBMENU =================

    /**
     * Manages the product return subsystem menu loop.
     */
    private void handleReturnMenu() {
        int option = -1;
        do {
            System.out.println("\n============= GESTIÓN DE DEVOLUCIONES ==============");
            System.out.println("1. Registrar una nueva devolución");
            System.out.println("2. Consultar todas las devoluciones");
            System.out.println("3. Consultar devoluciones por cliente");
            System.out.println("4. Consultar devoluciones por venta");
            System.out.println("0. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");

            option = readOption();
            switch (option) {
                case 1 -> registerReturn();
                case 2 -> listAllReturns();
                case 3 -> listReturnsByClient();
                case 4 -> listReturnsBySale();
                case 0 -> {
                }
                default -> System.out.println("Opción inválida.");
            }
        } while (option != 0);
    }

    /**
     * Prompts for sale and item information to register a product return via {@link ReturnService}.
     */
    private void registerReturn() {
        try {
            System.out.println("\n--- REGISTRAR NUEVA DEVOLUCIÓN ---");
            String saleId = promptString("Ingrese el ID de la Venta: ");
            String inputProducts = promptString("Ingrese los IDs de los Productos a devolver (separados por coma, ej., P01,P02): ");
            List<String> productIds = Arrays.asList(inputProducts.split("\\s*,\\s*"));
            String reason = promptString("Ingrese el Motivo de la Devolución: ");

            Return processedReturn = returnService.registerReturn(saleId, productIds, reason);
            System.out.println("\n¡Devolución procesada exitosamente!");
            System.out.println(processedReturn.generateReturnReceipt());

        } catch (ResourceNotFoundException e) {
            System.out.println("No se encontró el recurso solicitado: " + e.getMessage());
        } catch (BusinessRuleException e) {
            System.out.println("No se puede completar la operación: " + e.getMessage());
        } catch (InvalidDataException e) {
            System.out.println("Los datos ingresados no son válidos: " + e.getMessage());
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Formats and prints a collection of return receipts to the console.
     *
     * @param returns list of return transactions to display
     */
    private void printReturnList(List<Return> returns) {
        if (returns == null || returns.isEmpty()) {
            System.out.println("No se encontraron registros de devoluciones.");
        } else {
            for (Return r : returns) {
                System.out.println(r.generateReturnReceipt());
                System.out.println("----------------------------------------");
            }
        }
    }

    /**
     * Fetches and lists all processed returns.
     */
    private void listAllReturns() {
        try {
            System.out.println("\n--- TODAS LAS DEVOLUCIONES ---");
            List<Return> returns = returnService.viewAllReturns();
            printReturnList(returns);
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Prompts for a customer ID and displays their registered returns.
     */
    private void listReturnsByClient() {
        try {
            String customerId = promptString("Ingrese el ID del Cliente: ");
            List<Return> returns = returnService.viewReturnsByCustomer(customerId);
            System.out.println("\n--- DEVOLUCIONES POR CLIENTE ---");
            printReturnList(returns);
        } catch (ResourceNotFoundException e) {
            System.out.println("No se encontró el recurso solicitado: " + e.getMessage());
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Prompts for a sale transaction ID and lists associated returns.
     */
    private void listReturnsBySale() {
        try {
            String saleId = promptString("Ingrese el ID de la Venta: ");
            List<Return> returns = returnService.viewReturnsBySale(saleId);
            System.out.println("\n--- DEVOLUCIONES POR VENTA ---");
            printReturnList(returns);
        } catch (ResourceNotFoundException e) {
            System.out.println("No se encontró el recurso solicitado: " + e.getMessage());
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Prompts for month and year parameters to compute and print monthly net income.
     */
    private void generateMonthlyBalance() {
        try {
            System.out.println("\n--- REPORTE DE BALANCE MENSUAL ---");
            int month = promptInt("Ingrese el Mes (1-12): ");
            int year = promptInt("Ingrese el Año (ej., 2026): ");

            double netBalance = returnService.generateMonthlyBalance(month, year);
            System.out.println(String.format("\n=== BALANCE MENSUAL (%02d/%d) ===", month, year));
            System.out.println(String.format("Balance Neto (Ventas - Devoluciones): $%.2f", netBalance));
            
        } catch (BusinessRuleException e) {
            System.out.println("No se puede completar la operación: " + e.getMessage());
        } catch (InvalidDataException e) {
            System.out.println("Los datos ingresados no son válidos: " + e.getMessage());
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (NumberFormatException e) {
            System.out.println("Los datos ingresados no son válidos: Por favor, ingrese valores numéricos para mes y año.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    // ================= WARRANTY SUBMENU =================

    /**
     * Manages the warranty certificates subsystem menu loop.
     */
    private void handleWarrantyMenu() {
        int option = -1;
        do {
            System.out.println("\n============= GESTIÓN DE GARANTÍAS ==============");
            System.out.println("1. Consultar garantía para un producto específico en una venta");
            System.out.println("2. Listar todas las garantías registradas");
            System.out.println("3. Listar las garantías activas al día de hoy");
            System.out.println("4. Listar las garantías que expirarán pronto");
            System.out.println("0. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");

            option = readOption();
            switch (option) {
                case 1 -> consultWarrantyByProductAndSale();
                case 2 -> listAllWarranties();
                case 3 -> listActiveWarranties();
                case 4 -> listWarrantiesExpiringSoon();
                case 0 -> {
                }
                default -> System.out.println("Opción inválida.");
            }
        } while (option != 0);
    }

    /**
     * Prompts for sale ID and product ID to display warranty certificate details via {@link WarrantyService}.
     */
    private void consultWarrantyByProductAndSale() {
        try {
            System.out.println("\n--- CONSULTAR GARANTÍA DE PRODUCTO ---");
            String saleId = promptString("Ingrese el ID de la Venta: ");
            String productId = promptString("Ingrese el ID del Producto: ");

            if (warrantyService == null) {
                System.out.println("Error: El servicio de garantías no está disponible.");
                return;
            }

            Warranty warranty = warrantyService.findWarrantyByProduct(productId, saleId);
            if (warranty != null) {
                System.out.println("\n--- CERTIFICADO DE GARANTÍA ---");
                System.out.println(warranty.generateWarrantyCertificate());
            } else {
                System.out.println("No se encontró garantía para el ID de Producto '" + productId + "' en el ID de Venta '" + saleId + "'.");
            }
        } catch (ResourceNotFoundException e) {
            System.out.println("No se encontró el recurso solicitado: " + e.getMessage());
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Fetches and displays all registered warranty certificates.
     */
    private void listAllWarranties() {
        try {
            System.out.println("\n--- TODAS LAS GARANTÍAS REGISTRADAS ---");
            if (warrantyService == null) {
                System.out.println("Error: El servicio de garantías no está disponible.");
                return;
            }

            List<Warranty> warranties = warrantyService.listAllWarranties();
            printWarrantyList(warranties);
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Fetches and displays warranties currently active as of today.
     */
    private void listActiveWarranties() {
        try {
            System.out.println("\n--- GARANTÍAS ACTIVAS (HOY) ---");
            if (warrantyService == null) {
                System.out.println("Error: El servicio de garantías no está disponible.");
                return;
            }

            List<Warranty> activeWarranties = warrantyService.listActiveWarranties();
            printWarrantyList(activeWarranties);
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Prompts for a day threshold and lists warranties expiring within that period.
     */
    private void listWarrantiesExpiringSoon() {
        try {
            System.out.println("\n--- GARANTÍAS PRÓXIMAS A EXPIRAR ---");
            int daysAhead = promptInt("Ingrese el número de días a futuro para evaluar (ej., 30): ");

            if (warrantyService == null) {
                System.out.println("Error: El servicio de garantías no está disponible.");
                return;
            }

            List<Warranty> expiringWarranties = warrantyService.listWarrantiesExpiringSoon(daysAhead);
            printWarrantyList(expiringWarranties);
        } catch (InvalidDataException e) {
            System.out.println("Los datos ingresados no son válidos: " + e.getMessage());
        } catch (PersistenceException e) {
            System.out.println("Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador.");
        } catch (NumberFormatException e) {
            System.out.println("Los datos ingresados no son válidos: Por favor, ingrese un número válido de días.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Formats and prints a collection of warranty certificate objects.
     *
     * @param warranties list of warranties to print
     */
    private void printWarrantyList(List<Warranty> warranties) {
        if (warranties == null || warranties.isEmpty()) {
            System.out.println("No se encontraron garantías.");
        } else {
            for (Warranty w : warranties) {
                System.out.println("------------------------------------------------------------------");
                System.out.println(w.generateWarrantyCertificate());
            }
            System.out.println("------------------------------------------------------------------");
        }
    }
}
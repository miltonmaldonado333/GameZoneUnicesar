package com.gamezone.service;

import com.gamezone.model.Accesory;
import com.gamezone.model.Client;
import com.gamezone.model.Seller;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.persistence.SaleRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service handling sale transactions, stock validation, and inventory delegation.
 * Integrated to handle both standard products and accessories.
 */
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductService productService;
    private final AccesoryService accesoryService;

    /**
     * Constructs a SaleService with the required dependencies.
     *
     * @param saleRepository The repository used to persist sales.
     * @param productService The service used to manage product inventory.
     * @param accessoryService the service managing accessories
     */
    public SaleService(SaleRepository saleRepository, ProductService productService,  AccesoryService accesoryService) {
        this.saleRepository = saleRepository;
        this.productService = productService;
        this.accesoryService = accesoryService;
    }

    /**
     * Registers a new sale transaction in the system.
     * Validates stock availability, updates inventory across appropriate services,
     * and persists the transaction.
     *
     * @param client the customer making the purchase
     * @param seller the employee handling the transaction
     * @param items  the list of products/accessories being purchased
     * @return the created Sale instance
     * @throws IllegalArgumentException if items list is empty or stock is insufficient
     */
    private Sale registerSale(Client client,Seller seller, List<Product> items){
        if(items == null || items.isEmpty()){
            throw new IllegalArgumentException("A sale must containt at least one item.");
        }
        
        //1.Validate stock for all items prior to transaction
        for(Product item : items){
            if(item.getQuantity() < 1){
              throw new IllegalArgumentException("Insufficient stock");
            }
        }
        
        for (Product item : items) {
            int newQuantity= item.getQuantity() -1;
            if(item instanceof Accesory){
                accesoryService.updateStick(item.getId(), newQuantity);
            }else{
                 accesoryService.updateStick(item.getId(), newQuantity);
            }
        }
        
       String saleId = "SALE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String currentDate = LocalDate.now().toString();

        Sale newSale = new Sale(saleId, currentDate, client, seller, items);
        newSale.calculateTotal();

        List<Sale> currentSales = saleRepository.loadAll();
        if (currentSales == null) {
            currentSales = new ArrayList<>();
        }
        currentSales.add(newSale);
        saleRepository.saveAll(currentSales);

        return newSale;
    }
    
    
    /**
     * Retrieves the complete history of registered sales.
     *
     * @return A list containing all sales.
     */
    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    /**
     * Retrieves the purchase history of a specific client.
     *
     * @param clientIdentification The identification of the client.
     * @return A list of sales made by the specified client.
     */
    public List<Sale> getSalesByClient(String clientIdentification) {
        List<Sale> allSales = saleRepository.findAll();
        List<Sale> clientSales = new ArrayList<>();
        
        for (Sale sale : allSales) {
            if (sale.getClient().getIdentification().equals(clientIdentification)) {
                clientSales.add(sale);
            }
        }
        return clientSales;
    }

    /**
     * Retrieves the sales history attended by a specific seller.
     *
     * @param employeeCode The employee code of the seller.
     * @return A list of sales attended by the specified seller.
     */
    public List<Sale> getSalesBySeller(String employeeCode) {
        List<Sale> allSales = saleRepository.findAll();
        List<Sale> sellerSales = new ArrayList<>();
        
        for (Sale sale : allSales) {
            if (sale.getSeller().getEmployeeCode().equals(employeeCode)) {
                sellerSales.add(sale);
            }
        }
        return sellerSales;
    }
}
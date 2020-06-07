/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rohwederinventorysystem.Model;

import static java.util.stream.Collectors.toList;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.collections.ObservableList;

/**
 *
 * @author tim
 */
public class Inventory {
    
    private ObservableList<Part> allParts = observableArrayList();
    private ObservableList<Product> allProducts = observableArrayList();
    
    public void addPart(Part newPart) {
        allParts.add(newPart);
    }
    
    public void addProduct(Product newProduct) {
        allProducts.add(newProduct);
    }
    
    public Part lookupPart(int partId) {
        for (Part part : allParts) {
            if (part.getId() == partId) return part;
        }
        return null;
    }
    
    public Product lookupProduct(int productId) {
        for (Product product : allProducts) {
            if (product.getId() == productId) return product;
        }
        return null;
    }
    
    public ObservableList<Part> lookupPart(String partName) {
        ObservableList<Part> result = observableArrayList(allParts.stream()
                .filter(part -> part.getName().toLowerCase().contains(partName.toLowerCase()))
                .collect(toList()));
        
        return result.isEmpty() ? null : result;
    }
    
    public ObservableList<Product> lookupProduct(String productName) {
        ObservableList<Product> result = observableArrayList(allProducts.stream()
                .filter(product -> product.getName().toLowerCase().contains(productName.toLowerCase()))
                .collect(toList()));
        
        return result.isEmpty() ? null : result;
    }
    
    public void updatePart(int index, Part selectedPart) {
        allParts.set(index, selectedPart);
    }
    
    public void updateProduct(int index, Product newProduct) {
        allProducts.set(index, newProduct);
        
    }
    
    public boolean deletePart(Part selectedPart) {
        return allParts.remove(selectedPart);
    }
    
    public boolean deleteProduct(Product selectedProduct) {
        return allProducts.remove(selectedProduct);
    }
    
    public ObservableList<Part> getAllParts() {
        return allParts;
    }
    
    public ObservableList<Product> getAllProducts() {
        return allProducts;
    }
    
}

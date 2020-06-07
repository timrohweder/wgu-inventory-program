/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rohwederinventorysystem.View_Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import rohwederinventorysystem.Exceptions.InvalidInventoryRangeException;
import rohwederinventorysystem.Exceptions.InvalidProductPriceException;
import rohwederinventorysystem.Model.Part;
import rohwederinventorysystem.Model.Product;
import rohwederinventorysystem.RohwederInventorySystem;

/**
 * FXML Controller class
 *
 * @author tim
 */
public class ProductScreenController implements Initializable {
    
    private boolean modifyScreen = false;
    private Product productToModify;
    private ObservableList<Part> associatedParts = observableArrayList();
    @FXML private Label addOrModifyLabel;
    
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField inventoryField;
    @FXML private TextField costField;
    @FXML private TextField maxField;
    @FXML private TextField minField;
    
    @FXML private TableView<Part> partsTable;
    @FXML private TableColumn<Part, Integer> partIdColumn;
    @FXML private TableColumn<Part, String> partNameColumn;
    @FXML private TableColumn<Part, Integer> partInventoryColumn;
    @FXML private TableColumn<Part, Double> partCostColumn;
    @FXML private TextField partSearchField;
    @FXML private Button clearSearchButton;
    
    @FXML private TableView<Part> associatedPartsTable;
    @FXML private TableColumn<Part, Integer> associatedPartIdColumn;
    @FXML private TableColumn<Part, String> associatedPartNameColumn;
    @FXML private TableColumn<Part, Integer> associatedPartInventoryColumn;
    @FXML private TableColumn<Part, Double> associatedPartCostColumn;
    
    public void setProductScreenToModify(Product product) {
        modifyScreen = true;
        productToModify = product;
        addOrModifyLabel.setText("Modify Product");
        
        idField.setText(Integer.toString(product.getId()));
        nameField.setText(product.getName());
        inventoryField.setText(Integer.toString(product.getStock()));
        costField.setText(Double.toString(product.getPrice()));
        maxField.setText(Integer.toString(product.getMax()));
        minField.setText(Integer.toString(product.getMin()));
        product.getAllAssociatedParts().forEach(associatedPart -> associatedParts.add(associatedPart));
    }
    
    public int generateProductId() {
        Random random = new Random();
        ObservableList<Product> allProducts = RohwederInventorySystem.inventory.getAllProducts();
        List<Integer> allProductIds = new ArrayList<>();
        for (Product product : allProducts) {
            allProductIds.add(product.getId());
        }
        int productId;
        do {
            productId = random.nextInt(Integer.MAX_VALUE);
        }
        while(allProductIds.contains(productId));
        return productId;
    }
    
    public void handlePartAdd() {
        associatedParts.add(partsTable.getSelectionModel().getSelectedItem());
    }
    
    public void handleAssociatedPartDelete() {
        associatedParts.remove(associatedPartsTable.getSelectionModel().getSelectedItem());
    }
    
    public void handleSave(ActionEvent event) throws IOException {
       
        Product productToSave = new Product(
            modifyScreen ? productToModify.getId() : generateProductId(),  
            nameField.getText(),
            Double.parseDouble(costField.getText()),
            Integer.parseInt(inventoryField.getText()),
            Integer.parseInt(minField.getText()),
            Integer.parseInt(maxField.getText())                
        );
        
        for (Part part : associatedParts) {
            productToSave.addAssociatedPart(part);
        }
        
        try {
            double sumOfAssociatedPartPrices = 0;
            
            for (Part part : productToSave.getAllAssociatedParts()) {
                sumOfAssociatedPartPrices += part.getPrice();
            }
            
            if (productToSave.getPrice() < sumOfAssociatedPartPrices) {
                throw new InvalidProductPriceException("The price of the product must not be less than the summed cost of its associated parts.");
            }
            
            if (productToSave.getStock() < productToSave.getMin() || productToSave.getStock() > productToSave.getMax()) {
                throw new InvalidInventoryRangeException("Inventory must be between Min and Max values.");
            }

            if (this.modifyScreen) {
                int productToModifyIndex = RohwederInventorySystem.inventory.getAllProducts().indexOf(productToModify);
                RohwederInventorySystem.inventory.updateProduct(productToModifyIndex, productToSave);
            } else {
                RohwederInventorySystem.inventory.addProduct(productToSave);
            }

            returnToMainScreen(event);
            
        } catch (InvalidInventoryRangeException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("InvalidInventoryRangeException");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (InvalidProductPriceException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("InvalidProductPriceException");
            alert.setContentText(e.getMessage());
            alert.showAndWait();            
        }       

    }
    
    public boolean checkIfInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public void handlePartSearch() {
        clearSearchButton.setVisible(true);
        String searchTerm = partSearchField.getText();
        ObservableList<Part> newPartList = observableArrayList();
        if (checkIfInteger(searchTerm)) {
            Part part = RohwederInventorySystem.inventory.lookupPart(Integer.parseInt(searchTerm));
            newPartList.add(part);
        } else {
            newPartList = RohwederInventorySystem.inventory.lookupPart(searchTerm);
        }
        partsTable.setItems(newPartList);
        
    }
    
    public void clearPartSearch() {
        clearSearchButton.setVisible(false);
        partSearchField.setText("");
        partsTable.setItems(RohwederInventorySystem.inventory.getAllParts());
    }
    
    public void returnToMainScreen(ActionEvent event) throws IOException {
        Parent mainScreenParent = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
        Scene mainScreenScene = new Scene(mainScreenParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(mainScreenScene);
        window.show();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        partIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        partNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        partInventoryColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partCostColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        associatedPartIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        associatedPartNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        associatedPartInventoryColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        associatedPartCostColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        partsTable.setItems(RohwederInventorySystem.inventory.getAllParts());
        associatedPartsTable.setItems(associatedParts);
        
    }    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rohwederinventorysystem.View_Controller;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import rohwederinventorysystem.Model.InHouse;
import rohwederinventorysystem.Model.Outsourced;
import rohwederinventorysystem.Model.Part;
import rohwederinventorysystem.Model.Product;
import rohwederinventorysystem.RohwederInventorySystem;
import rohwederinventorysystem.Exceptions.MultipleModificationAttemptException;

/**
 * FXML Controller class
 *
 * @author tim
 */
public class MainScreenController implements Initializable {
   
    @FXML private TableView<Part> partsTable;
    @FXML private TableColumn<Part, Integer> partIdColumn;
    @FXML private TableColumn<Part, String> partNameColumn;
    @FXML private TableColumn<Part, Integer> partInventoryColumn;
    @FXML private TableColumn<Part, Double> partCostColumn;
    @FXML private TextField partSearchField;
    @FXML private Button partClearSearchButton;
    
    @FXML private TableView<Product> productsTable;
    @FXML private TableColumn<Product, Integer> productIdColumn;
    @FXML private TableColumn<Product, String> productNameColumn;
    @FXML private TableColumn<Product, Integer> productInventoryColumn;
    @FXML private TableColumn<Product, Double> productCostColumn;
    @FXML private TextField productSearchField;
    @FXML private Button productClearSearchButton;    
    
    public boolean checkIfInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public void handlePartSearch() {
        partClearSearchButton.setVisible(true);
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
        partClearSearchButton.setVisible(false);
        partSearchField.setText("");
        partsTable.setItems(RohwederInventorySystem.inventory.getAllParts());
    }
    
    public void handlePartDelete() {
        List<Part> selectedParts = new ArrayList<>(partsTable.getSelectionModel().getSelectedItems());
        
        selectedParts.forEach((part) -> {
            RohwederInventorySystem.inventory.deletePart(part);
        });
    }    
    
    public void handleProductSearch() {
        productClearSearchButton.setVisible(true);
        String searchTerm = productSearchField.getText();
        ObservableList<Product> newProductList = observableArrayList();
        if (checkIfInteger(searchTerm)) {
            Product product = RohwederInventorySystem.inventory.lookupProduct(Integer.parseInt(searchTerm));
            newProductList.add(product);
        } else {
            newProductList = RohwederInventorySystem.inventory.lookupProduct(searchTerm);
        }
        productsTable.setItems(newProductList);
        
    }
    
    public void clearProductSearch() {
        productClearSearchButton.setVisible(false);
        productSearchField.setText("");
        productsTable.setItems(RohwederInventorySystem.inventory.getAllProducts());
    }    
    
    public void handleProductDelete() {
        List<Product> selectedProducts = new ArrayList<>(productsTable.getSelectionModel().getSelectedItems());
        
        selectedProducts.forEach((product) -> {
            RohwederInventorySystem.inventory.deleteProduct(product);
        });
    }
            

    public void openPartScreen(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("PartScreen.fxml"));
        Parent PartScreenParent = loader.load();
        Scene PartScreenScene = new Scene(PartScreenParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();        
        String buttonText = ((Button) event.getSource()).getText();
        PartScreenController controller = loader.getController();

        if (buttonText.equalsIgnoreCase("add")) {
            controller.setPartScreenToAdd();
            window.setScene(PartScreenScene);
            window.show();
        } else if (buttonText.equalsIgnoreCase("modify")) {
            try {
                if (partsTable.getSelectionModel().getSelectedItems().size() > 1) {
                    throw new MultipleModificationAttemptException("You may only select one part at a time to modify.");
                }
                controller.setPartScreenToModify(partsTable.getSelectionModel().getSelectedItems().get(0));
                window.setScene(PartScreenScene);
                window.show();                
            } catch(MultipleModificationAttemptException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("InvalidInventoryRangeException");
                alert.setContentText(e.getMessage());
                alert.showAndWait();                
            }
        }
    }   
    
    public void openProductScreen(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("ProductScreen.fxml"));
        Parent ProductScreenParent = loader.load();
        Scene ProductScreenScene = new Scene(ProductScreenParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        String buttonText = ((Button) event.getSource()).getText();
        ProductScreenController controller = loader.getController();

        if (buttonText.equalsIgnoreCase("add")) {
//            controller.setproductScreenToAdd();
            window.setScene(ProductScreenScene);
            window.show();
        } else if (buttonText.equalsIgnoreCase("modify")) {
            try {
                if (productsTable.getSelectionModel().getSelectedItems().size() > 1) {
                    throw new MultipleModificationAttemptException("You may only select one product at a time to modify.");
                }
                controller.setProductScreenToModify(productsTable.getSelectionModel().getSelectedItems().get(0));
                window.setScene(ProductScreenScene);
                window.show();                
            } catch(MultipleModificationAttemptException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("InvalidInventoryRangeException");
                alert.setContentText(e.getMessage());
                alert.showAndWait();                
            }
        }       
    }
    
    public void handleExit() {
        System.exit(0);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        
        partCostColumn.setCellFactory(tableCell -> new TableCell<Part, Double>() {

            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(formatter.format(price));
                }
            }
        });
        
        productCostColumn.setCellFactory(tableCell -> new TableCell<Product, Double>() {

            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(formatter.format(price));
                }
            }
        });        
        
        partIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        partNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        partInventoryColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partCostColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        productInventoryColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        productCostColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        // dummy text for testing purposes - note: will re-add new parts and products each time MainScreen is opened
      
//        RohwederInventorySystem.inventory.addPart(new InHouse(1,"An InhousePart", 5.74,1,1,10, 5));
//        RohwederInventorySystem.inventory.addPart(new InHouse(2,"Another InhousePart", 13.45,10,2,102, 5));
//        RohwederInventorySystem.inventory.addPart(new Outsourced(3,"An OutsourcedPart", 12.45,10,2,102, "ACME"));
//        RohwederInventorySystem.inventory.addPart(new Outsourced(4,"Another OutsourcedPart", 7.27,3,0,100, "ACME"));
//        RohwederInventorySystem.inventory.addProduct(new Product(1,"A Product", 5.74,1,1,10));
//        RohwederInventorySystem.inventory.addProduct(new Product(2,"Another Product", 12.45,10,2,102));
        
        partsTable.setItems(RohwederInventorySystem.inventory.getAllParts());
        productsTable.setItems(RohwederInventorySystem.inventory.getAllProducts());
        
        partsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        productsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }    
    
}

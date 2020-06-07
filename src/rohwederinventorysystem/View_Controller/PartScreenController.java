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
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import rohwederinventorysystem.Model.InHouse;
import rohwederinventorysystem.Model.Outsourced;
import rohwederinventorysystem.Model.Part;
import rohwederinventorysystem.RohwederInventorySystem;
import rohwederinventorysystem.Exceptions.InvalidInventoryRangeException;

/**
 * FXML Controller class
 *
 * @author tim
 */
public class PartScreenController implements Initializable {
    
    private boolean inHouse = true;
    private boolean modifyScreen = false;
    
    @FXML private RadioButton inHouseRadioButton;
    @FXML private RadioButton outsourcedRadioButton;
    private ToggleGroup partSourceToggleGroup;
    
    @FXML private Label addOrModifyLabel;
    @FXML private Label companyOrMachineLabel;
    
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField inventoryField;
    @FXML private TextField costField;
    @FXML private TextField maxField;
    @FXML private TextField minField;
    @FXML private TextField companyOrMachineTextField;
    
    private Part partToModify;
    
    public void setPartScreenToAdd() {
        modifyScreen = false;
        addOrModifyLabel.setText("Add Part");
    }
    
    public void setPartScreenToModify(Part part) {
        modifyScreen = true;
        partToModify = part;
        inHouse = part instanceof InHouse;
        addOrModifyLabel.setText("Modify Part");
        
        idField.setText(Integer.toString(part.getId()));
        nameField.setText(part.getName());
        inventoryField.setText(Integer.toString(part.getStock()));
        costField.setText(Double.toString(part.getPrice()));
        maxField.setText(Integer.toString(part.getMax()));
        minField.setText(Integer.toString(part.getMin()));
        
        if (inHouse) {
            changeFormToInHouse();
            companyOrMachineTextField.setText(Integer.toString(((InHouse) part).getMachineId()));
            this.inHouseRadioButton.setSelected(true);
        } else {
            changeFormToOutsourced();
            companyOrMachineTextField.setText(((Outsourced) part).getCompanyName());
            this.outsourcedRadioButton.setSelected(true);
        }
    }
    
    public int generatePartId() {
        Random random = new Random();
        ObservableList<Part> allParts = RohwederInventorySystem.inventory.getAllParts();
        List<Integer> allPartIds = new ArrayList<>();
        for (Part part : allParts) {
            allPartIds.add(part.getId());
        }
        int partId;
        do {
            partId = random.nextInt(Integer.MAX_VALUE);
        }
        while(allPartIds.contains(partId));
        return partId;
    }
    
    public void changeFormToInHouse() {
        this.inHouse = true;
        this.companyOrMachineLabel.setText("Machine ID");
        this.companyOrMachineTextField.setPromptText("Machine ID");
    }
    
    public void changeFormToOutsourced() {
        this.inHouse = false;
        this.companyOrMachineLabel.setText("Company Name");
        this.companyOrMachineTextField.setPromptText("Company Name");
    }
    
    public void changeRadioButton() {
        if (this.partSourceToggleGroup.getSelectedToggle().equals(this.inHouseRadioButton)) changeFormToInHouse();
        if (this.partSourceToggleGroup.getSelectedToggle().equals(this.outsourcedRadioButton)) changeFormToOutsourced();
    }
    
    public void savePartButton(ActionEvent event) throws IOException {

        Part partToSave;
        int id = modifyScreen ? partToModify.getId() : generatePartId();
        
        if (this.inHouse) {
            partToSave = new InHouse(
                    id,
                    nameField.getText(),
                    Double.parseDouble(costField.getText()),
                    Integer.parseInt(inventoryField.getText()),
                    Integer.parseInt(minField.getText()),
                    Integer.parseInt(maxField.getText()),
                    Integer.parseInt(companyOrMachineTextField.getText())
            ); 
        } else {
            partToSave = new Outsourced(
                    id,
                    nameField.getText(),
                    Double.parseDouble(costField.getText()),
                    Integer.parseInt(inventoryField.getText()),
                    Integer.parseInt(minField.getText()),
                    Integer.parseInt(maxField.getText()),
                    companyOrMachineTextField.getText()
            );             
        }
        
        try {
            if (partToSave.getStock() < partToSave.getMin() || partToSave.getStock() > partToSave.getMax()) {
                throw new InvalidInventoryRangeException("Inventory must be between Min and Max values.");
            }

            if (this.modifyScreen) {
                int partToModifyIndex = RohwederInventorySystem.inventory.getAllParts().indexOf(partToModify);
                RohwederInventorySystem.inventory.updatePart(partToModifyIndex, partToSave);
            } else {
                RohwederInventorySystem.inventory.addPart(partToSave);
            }

            returnToMainScreen(event);
            
        } catch (InvalidInventoryRangeException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("InvalidInventoryRangeException");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
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
       
        partSourceToggleGroup = new ToggleGroup();
        this.inHouseRadioButton.setToggleGroup(partSourceToggleGroup);
        this.outsourcedRadioButton.setToggleGroup(partSourceToggleGroup);
        if (!modifyScreen) {
            this.inHouseRadioButton.setSelected(true);
        }
        
    }    
    
}

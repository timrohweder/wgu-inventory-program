/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rohwederinventorysystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import rohwederinventorysystem.Model.InHouse;
import rohwederinventorysystem.Model.Inventory;

/**
 *
 * @author tim
 */
public class RohwederInventorySystem extends Application {
    
    public static Inventory inventory = new Inventory();
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("View_Controller/MainScreen.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
        
//        RohwederInventorySystem.inventory.addPart(new InhousePart(1,"widget", 30.4,10,2,12, 777));
    }
    
}

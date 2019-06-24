package vehiclepanel;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import vehiclepanel.vehiclePanelControl.VehiclePanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Controller {

    @FXML
    private HBox hboxVehiclePanels;
    @FXML
    private VBox vbox;

    @FXML
    private void initialize() {
        VehiclePanel vehiclePanel1 = new VehiclePanel();
        vbox.getChildren().remove(hboxVehiclePanels);
        //vbox.getChildren().add(hboxVehiclePanels);
        vbox.getChildren().add(vehiclePanel1);

        vehiclePanel1.displayPanel(new Vehicle());

        VehiclePanel vehiclePanel2 = new VehiclePanel();
        vehiclePanel1.setName("Lane 1");
        vehiclePanel2.setName("Lane 2");
        vbox.getChildren().add(vehiclePanel2);
        Vehicle vehicle2 = new Vehicle(new ArrayList<Double>(Arrays.asList(2.0,2.5,2.0,1.5,1.2,1.4,2.0,1.4)));
        vehiclePanel2.displayPanel(vehicle2);
        vehiclePanel1.displayPanel(vehicle2);
        vehiclePanel1.displayPanel(new Vehicle());

    }

}

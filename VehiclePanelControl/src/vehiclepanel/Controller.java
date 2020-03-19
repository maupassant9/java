package vehiclepanel;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import vehiclepanel.vehiclePanelControl.VehiclePanel;

import java.util.ArrayList;
import java.util.Arrays;

public class Controller {

    @FXML
    private HBox hboxVehiclePanels;
    @FXML
    private VBox vbox;

    private ListView<Vehicle> listViewVehicles;
    private ListView<Vehicle> ListViewVehiclesCalibration;

    @FXML
    private void initialize() throws Exception {
        VehiclePanel vehiclePanel1 = new VehiclePanel();
        //vbox.getChildren().remove(hboxVehiclePanels);
        //vbox.getChildren().add(hboxVehiclePanels);
        vbox.getChildren().add(vehiclePanel1);
        vehiclePanel1.setName("Lane 1");

        vehiclePanel1.displayPanel(new Vehicle(
                new ArrayList<Double>(Arrays.asList(3.0,3.0)),
                28, new ArrayList<Double>(Arrays.asList(3000.0,2356.0,3366.0)),
                5356));

        VehiclePanel vehiclePanel2 = new VehiclePanel();

        vehiclePanel2.setName("Lane 2");
        vbox.getChildren().add(vehiclePanel2);
        Vehicle vehicle2 = new Vehicle(new ArrayList<Double>(Arrays.asList(2.0,2.5,2.0,1.5,1.2,1.4)),
                55, new ArrayList<Double>(Arrays.asList(3000.0,2000.0,1200.0,888.0,777.0,666.8,222.6)),250000.0);
        vehiclePanel2.displayPanel(vehicle2);


        listViewVehicles = new ListView<>();
        ListViewVehiclesCalibration = new ListView<>();

    }

}

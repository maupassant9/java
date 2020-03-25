package vehiclepanel;

import javafx.animation.PathTransition.OrientationType;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.effect.SepiaTone;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import vehiclepanel.CommSDIP.CommThread;
import vehiclepanel.vehiclePanelControl.VehiclePanel;

import java.util.ArrayList;
import java.util.Arrays;

public class Controller {

    @FXML
    public VBox vbox;

    public SimpleStringProperty caliInfoText = new SimpleStringProperty();

    private ListView<Vehicle> listViewVehicles;
    private ListView<Vehicle> listViewVehiclesCalibration;
    private ObservableList<Vehicle> vehicleList = FXCollections.observableArrayList(new ArrayList<Vehicle>());
    private ObservableList<Vehicle> calibrateVehicleList = FXCollections.observableArrayList(new ArrayList<Vehicle>());

    @FXML
    private void initialize() throws Exception {


        VehiclePanel vehiclePanel1 = new VehiclePanel();
        
        vbox.getChildren().add(vehiclePanel1);
        vehiclePanel1.setName("Lane 1");

        vehiclePanel1.displayPanel(new Vehicle(new ArrayList<Double>(Arrays.asList(3.0, 3.0)), 28,
                new ArrayList<Double>(Arrays.asList(3000.0, 2356.0, 3366.0)), 5356));

        VehiclePanel vehiclePanel2 = new VehiclePanel();

        vehiclePanel2.setName("Lane 2");
        vbox.getChildren().add(vehiclePanel2);
        Vehicle vehicle2 = new Vehicle(new ArrayList<Double>(Arrays.asList(2.0, 2.5, 2.0, 1.5, 1.2, 1.4)), 55,
                new ArrayList<Double>(Arrays.asList(3000.0, 2000.0, 1200.0, 888.0, 777.0, 666.8, 222.6)), 250000.0);
        vehiclePanel2.displayPanel(vehicle2);

        listViewVehicles = new ListView<Vehicle>();
        listViewVehiclesCalibration = new ListView<Vehicle>();
        listViewVehicles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listViewVehiclesCalibration.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ListProperty<Vehicle> listPropertyForVehicleList = new SimpleListProperty<Vehicle>();
        listPropertyForVehicleList.set(vehicleList);
        listViewVehicles.itemsProperty().bind(listPropertyForVehicleList);

        ListProperty<Vehicle> listPropertyForCalibrateVehicle = new SimpleListProperty<Vehicle>();
        listPropertyForCalibrateVehicle.set(calibrateVehicleList);
        listViewVehiclesCalibration.itemsProperty().bind(listPropertyForCalibrateVehicle);

        

        HBox hbox = new HBox();
        
        Button arrowLeft = new Button("<-");
        Button arrowRight = new Button("->");

        VBox vboxButtons = new VBox(arrowLeft, arrowRight);
        vboxButtons.setAlignment(Pos.CENTER);

        hbox.getChildren().addAll(listViewVehicles, vboxButtons, listViewVehiclesCalibration);
        listViewVehicles.setBackground(new Background(new BackgroundFill(Color.AQUA, CornerRadii.EMPTY, Insets.EMPTY)));
        vbox.getChildren().add(new Label("")); //add some space
        vbox.getChildren().add(hbox);
        

        hbox.prefHeightProperty().bind(vbox.prefHeightProperty());
        VBox.setVgrow(hbox, Priority.ALWAYS);
        hbox.setSpacing(10);
        hbox.setPadding(new Insets(0, 20, 0, 10));
        hbox.setAlignment(Pos.BOTTOM_LEFT);
        
        vbox.getChildren().add(new Label(""));
        Button calibrateButton = new Button("Gen Table");
        calibrateButton.setFont(new Font(10.0));
        Button saveButton = new Button("Save Table");
        saveButton.setFont(new Font(10.0));
        Button sendButton = new Button("Send Table");
        sendButton.setFont(new Font(10.0));
        
        VBox vbox2 = new VBox(calibrateButton,saveButton,sendButton);
        vbox2.autosize();
        vbox2.setSpacing(5);
        hbox.getChildren().add(vbox2);

        TextArea calibrateInfo = new TextArea();
        calibrateInfo.textProperty().bind(caliInfoText);
        caliInfoText.set("Initialized ok...");
        calibrateInfo.setPrefWidth(120);
        calibrateInfo.setPrefHeight(100);
        calibrateInfo.setMinWidth(120);
        calibrateInfo.setMinHeight(100);
        calibrateInfo.setMaxWidth(120);
        calibrateInfo.setMaxHeight(100);
        hbox.getChildren().add(calibrateInfo);
        calibrateButton.setOnAction(new EventHandler<ActionEvent>(){
        
            @Override
            public void handle(ActionEvent arg0) {
                // TODO Auto-generated method stub
                
            }
        });


        arrowLeft.setOnAction(new EventHandler<ActionEvent>(){
        
            @Override
            public void handle(ActionEvent event) {
                vehicleList.add(vehicle2);
            }
        });
        
        arrowRight.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                calibrateVehicleList.add(vehicle2);
            }
        });

        Object lock = new Object();

        //start the CommThread
        CommThread comm = new CommThread(lock, this);
        Thread commThread = new Thread(comm);
        commThread.setDaemon(true);
        commThread.start();


        //Send a notification to CommThread
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                synchronized(lock){
                    lock.notify();
                }
                
            }
        }); 
    }


    

}

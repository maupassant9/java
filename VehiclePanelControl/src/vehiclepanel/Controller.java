package vehiclepanel;

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
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import vehiclepanel.Calibrator.Calibrator;
import vehiclepanel.Calibrator.LaneMonitor;
import vehiclepanel.Calibrator.VehicleFilter;
import vehiclepanel.CommSDIP.CommThread;
import vehiclepanel.vehiclePanelControl.VehiclePanel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Controller {

    @FXML
    public VBox vbox;

    public SimpleStringProperty caliInfoText = new SimpleStringProperty();

    private ListView<Vehicle> listViewVehicles;
    private ListView<Vehicle> listViewVehiclesCalibration;
    private ObservableList<Vehicle> vehicleList = 
        FXCollections.observableArrayList(new ArrayList<Vehicle>());
    private ObservableList<Vehicle> calibrateVehicleList = 
        FXCollections.observableArrayList(new ArrayList<Vehicle>());

    VehicleFilter filter = new VehicleFilter();
    LaneMonitor laneMonitor = new LaneMonitor(vehicleList);
    
    private Button calibrateButton;
    public Button setCaliParameters;

    @FXML
    private void initialize() throws Exception {

        VehiclePanel vehiclePanel1 = new VehiclePanel();
        
        vbox.getChildren().add(vehiclePanel1);
        vehiclePanel1.setName("Lane 1");

        HashMap<Integer,ArrayList<Double>> eixoWtPerSensor = new HashMap<>();;
        eixoWtPerSensor.put(1,new ArrayList<Double>(Arrays.asList(3.0,3.0)));
        eixoWtPerSensor.put(2,new ArrayList<Double>(Arrays.asList(3.0,3.0)));
        vehiclePanel1.displayPanel(new Vehicle("0",
            new ArrayList<Double>(Arrays.asList(3.0, 3.0)),
            28,
            new ArrayList<Double>(Arrays.asList(3000.0, 2356.0, 3366.0)), 5356,10,1,
            eixoWtPerSensor));
        // vehiclePanel1.clearPanel();
        VehiclePanel vehiclePanel2 = new VehiclePanel();

        vehiclePanel2.setName("Lane 2");
        vbox.getChildren().add(vehiclePanel2);
        // Vehicle vehicle2 = new Vehicle("0",
        //     new ArrayList<Double>(
        //         Arrays.asList(2.0, 2.5, 2.0, 1.5, 1.2, 1.4)), 
        //     55,
        //     new ArrayList<Double>(
        //         Arrays.asList(3000.0, 2000.0, 1200.0, 888.0, 777.0, 666.8, 222.6)),
        //             250000.0);
        vehiclePanel2.clearPanel();

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
        //listViewVehicles.setBackground(new Background(
        //    new BackgroundFill(Color.AQUA, CornerRadii.EMPTY, Insets.EMPTY)));
        vbox.getChildren().add(new Label("")); //add some space
        vbox.getChildren().add(hbox);
        

        hbox.prefHeightProperty().bind(vbox.prefHeightProperty());
        VBox.setVgrow(hbox, Priority.ALWAYS);
        hbox.setSpacing(10);
        hbox.setPadding(new Insets(0, 20, 0, 10));
        hbox.setAlignment(Pos.BOTTOM_LEFT);
        
        vbox.getChildren().add(new Label(""));
        setCaliParameters = new Button("Set Paras");
        setCaliParameters.setFont(new Font(10.0));
        setCaliParameters.setDisable(true);
        calibrateButton = new Button("Gen Table");
        calibrateButton.setFont(new Font(10.0));
        calibrateButton.setDisable(true);
        Button saveButton = new Button("Save Table");
        saveButton.setFont(new Font(10.0));
        saveButton.setDisable(true);
        Button sendButton = new Button("Send Table");
        sendButton.setFont(new Font(10.0));
        sendButton.setDisable(true);
        Button loadButton = new Button("Load Vehicle");
        loadButton.setFont(new Font(10.0));
        loadButton.setDisable(false);
        
        VBox vbox2 = new VBox(setCaliParameters,calibrateButton,
            saveButton,sendButton,loadButton);
        vbox2.autosize();
        vbox2.setSpacing(5);
        hbox.getChildren().add(vbox2);

        TextArea calibrateInfo = new TextArea();
        calibrateInfo.setWrapText(true);
        calibrateInfo.textProperty().bind(caliInfoText);
        caliInfoText.set("Initialized ok...");
        calibrateInfo.setPrefWidth(120);
        calibrateInfo.setPrefHeight(100);
        calibrateInfo.setMinWidth(120);
        calibrateInfo.setMinHeight(100);
        calibrateInfo.setMaxWidth(120);
        calibrateInfo.setMaxHeight(100);
        hbox.getChildren().add(calibrateInfo);

        setCaliParameters.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                popup();
            }
        });
        
        calibrateButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent arg0) {

                if(calibrateVehicleList.size() < 1){
                    Alert alert = new Alert(AlertType.ERROR, 
                        "No calibration itens selected.", 
                        ButtonType.OK);
                    alert.showAndWait();
                } else {
                    Calibrator cali = Calibrator.getCalibrator(
                            calibrateVehicleList,
                            filter);
                    cali.setFunction(Calibrator.GENERATE_CALIBRATE_TABLE);
                    cali.setSaveButton(saveButton);
                    Thread caliThread = new Thread(cali);
                    caliThread.setDaemon(true);
                    caliThread.start();
                }
            }
        });

        saveButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                FileChooser fc = new FileChooser();
                fc.setInitialDirectory(new File(System.getProperty("user.dir")));
                fc.setTitle("Save the calibration table:");

                File fid = fc.showSaveDialog(saveButton.getScene().getWindow());
                if(fid != null)
                {
                    Calibrator cali = Calibrator.getCalibrator(calibrateVehicleList,filter);
                    cali.setFunction(Calibrator.SAVE_CALIBRATE_TABLE);
                    cali.setFile(fid);
                    cali.setSaveButton(sendButton);
                    Thread caliThread = new Thread(cali);
                    caliThread.start();
                } else {
                    Alert alert = new Alert(AlertType.ERROR,
                        "Please select the right file.", 
                        ButtonType.OK);
                    alert.showAndWait();
                }
            }
        });

        sendButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
                Calibrator cali = Calibrator.getCalibrator(calibrateVehicleList,filter);
                cali.setFunction(Calibrator.SEND_CALIBRATE_TABLE);
                Thread caliThread = new Thread(cali);
                caliThread.start();
			}
        });

        loadButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                FileChooser fch = new FileChooser();
                fch.setInitialDirectory(new File(System.getProperty("user.dir")));
                File fid = fch.showOpenDialog(loadButton.getScene().getWindow());
                if(fid != null){
                    Calibrator cali = Calibrator.getCalibrator(calibrateVehicleList, filter);
                    cali.setFunction(Calibrator.LOAD_CALIBRATE_TABLE);
                    cali.setFile(fid);
                    Thread caliThread = new Thread(cali);
                    caliThread.start();
                }
            }
        });

        arrowLeft.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                //remove the selected calibration vehicle list
                ObservableList<Integer> selectedIndices = 
                    listViewVehiclesCalibration.getSelectionModel().getSelectedIndices();
                ArrayList<Vehicle> removeVehicles = new ArrayList<>();
                for(Integer idx: selectedIndices){
                    vehicleList.add(calibrateVehicleList.get(idx));
                    removeVehicles.add(calibrateVehicleList.get(idx));
                }
                calibrateVehicleList.removeAll(removeVehicles);
            }
        });
        
        arrowRight.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //remove the selected vehicle list
                ObservableList<Integer> selectedIndices = 
                    listViewVehicles.getSelectionModel().getSelectedIndices();
                ArrayList<Vehicle> removeVehicles = new ArrayList<>();
                for(Integer idx: selectedIndices){
                    calibrateVehicleList.add(vehicleList.get(idx));
                    removeVehicles.add(vehicleList.get(idx));
                }
                vehicleList.removeAll(removeVehicles);
            }
        });
    
        //start the CommThread
        Object lock = new Object();
        CommThread comm = CommThread.getCommThread(lock, this);
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

        //create the calibrator thread
        laneMonitor.setPanel1(vehiclePanel1);
        laneMonitor.setPanel2(vehiclePanel2);
        laneMonitor.setCtr(this);
        laneMonitor.setFilter(filter);
        Thread laneMonitorThread = new Thread(laneMonitor);
        laneMonitorThread.setDaemon(true);
        laneMonitorThread.start();

        //setup the calibration thread too
        Calibrator cali = Calibrator.getCalibrator(calibrateVehicleList,filter);
        cali.setFunction(Calibrator.ENTER_CALIBRATION_MODE);
        Thread caliThread = new Thread(cali);
        caliThread.setDaemon(true);
        caliThread.start();
    }


    //popup dialog
    public void popup(){
        final Stage dialog = new Stage();
        dialog.setTitle("Calibration Setting");
        Label vehicleInfo = new Label("Vehicle For Calibration:");
        Label wtInfo = new Label("Weight:    ");
        Label maxWtInfo = new Label("Max. Weight: ");
        Label minWtInfo = new Label("Min. Weight: ");
        Label maxSpeedInfo = new Label("Max. Speed:  ");
        Label minSpeedInfo = new Label("Min. Speed:  ");
        Label asleNoInfo = new Label("Asle No.     ");

        TextField tfWtInfo = new TextField();
        RadioButton faixaA = new RadioButton("Lane A");
        RadioButton faixaB = new RadioButton("Lane B");
        ToggleGroup group = new ToggleGroup();
        faixaA.setToggleGroup(group);
        faixaB.setToggleGroup(group);
        TextField tfMaxWtInfo = new TextField();
        TextField tfMinWtInfo = new TextField();
        TextField tfMaxSpeedInfo = new TextField();
        TextField tfMinSpeedInfo = new TextField();
        ObservableList<Integer> eixoNoList = FXCollections.observableArrayList();
        for(int i = 2; i < 10; i++)
            eixoNoList.add(i);
        ComboBox<Integer> comboAsleNoInfo = new ComboBox<Integer>(eixoNoList);
        CheckBox useFilter = new CheckBox("Use filter");

        Button buttonOk = new Button("OK");
        Button buttonCancel = new Button("Cancel");
        
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(vbox.getScene().getWindow());
        VBox vboxMain = new VBox();
        vboxMain.getChildren().add(vehicleInfo);
        vboxMain.getChildren().add(new HBox(wtInfo,tfWtInfo));
        vboxMain.getChildren().add(new HBox(faixaA, faixaB));
        vboxMain.getChildren().add(new Separator(Orientation.HORIZONTAL));
        vboxMain.getChildren().add(useFilter);
        vboxMain.getChildren().add(new HBox(maxSpeedInfo,tfMaxSpeedInfo));
        vboxMain.getChildren().add(new HBox(minSpeedInfo,tfMinSpeedInfo));
        vboxMain.getChildren().add(new HBox(maxWtInfo,tfMaxWtInfo));
        vboxMain.getChildren().add(new HBox(minWtInfo,tfMinWtInfo));
        vboxMain.getChildren().add(new HBox(asleNoInfo, comboAsleNoInfo));
        vboxMain.getChildren().add(new Label(" "));
        HBox hbox1 = new HBox(buttonOk,buttonCancel);
        hbox1.setSpacing(50);
        hbox1.setAlignment(Pos.TOP_CENTER);
        vboxMain.getChildren().add(hbox1);

        vboxMain.setSpacing(10);
        vboxMain.setPadding(new Insets(10, 10, 10, 10));

        tfMaxSpeedInfo.disableProperty().bind(useFilter.selectedProperty().not());
        tfMinSpeedInfo.disableProperty().bind(useFilter.selectedProperty().not());
        tfMaxWtInfo.disableProperty().bind(useFilter.selectedProperty().not());
        tfMinWtInfo.disableProperty().bind(useFilter.selectedProperty().not());
        comboAsleNoInfo.disableProperty().bind(useFilter.selectedProperty().not());
        

        buttonCancel.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                dialog.close();
            }
        });
        buttonOk.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {

                 try{
                    double maxVal, minVal;

                    filter = new VehicleFilter();
                    filter.calibrateWeight = Double.parseDouble(tfWtInfo.getText());
                    if(faixaA.isSelected()) filter.setFaixa(1);
                    if(faixaB.isSelected()) filter.setFaixa(2);
                    if(useFilter.isSelected()){
                        filter.setEixoNo(comboAsleNoInfo.getItems().get(
                            comboAsleNoInfo.getSelectionModel().getSelectedIndex()));
                        maxVal = Double.parseDouble(tfMaxWtInfo.getText());
                        minVal = Double.parseDouble(tfMinWtInfo.getText());
                        if(maxVal <= minVal) throw new Exception();
                        if(filter.calibrateWeight > maxVal) throw new Exception();
                        if(filter.calibrateWeight < minVal) throw new Exception();
                        filter.setPesoTotalMaxKg(maxVal);
                        filter.setPesoTotalMinKg(minVal);

                        maxVal = Double.parseDouble(tfMaxSpeedInfo.getText());
                        minVal = Double.parseDouble(tfMinSpeedInfo.getText());
                        if(maxVal <= minVal) throw new Exception();
                        filter.setSpeedKmhRangeMax(maxVal);
                        filter.setSpeedKmhRangeMin(minVal);
                    }

                    //set filter
                    laneMonitor.setFilter(filter);
                    Calibrator.setFilter(filter);
                    Calibrator.getCalibrator(calibrateVehicleList,filter);
                    dialog.close();
                    calibrateButton.setDisable(false);
                } catch(Exception e){
                    Alert alert = new Alert(AlertType.ERROR,"Parameter error!!!",ButtonType.OK);
                    alert.showAndWait();
                }
            }
        });

        Scene dialogScene = new Scene(vboxMain,250,380);
        dialog.setScene(dialogScene);
        dialog.setResizable(false);
        dialog.show();
    }

    
}

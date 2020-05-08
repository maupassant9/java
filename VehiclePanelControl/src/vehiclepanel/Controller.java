package vehiclepanel;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import java.util.concurrent.atomic.AtomicBoolean;

public class Controller {

    @FXML
    public VBox vbox;


    public static SimpleStringProperty caliInfoText = new SimpleStringProperty();

    private ListView<Vehicle> listViewVehicles;
    private ListView<Vehicle> listViewVehiclesCalibration;
    private ObservableList<Vehicle> vehicleList = FXCollections.observableArrayList(new ArrayList<Vehicle>());
    private ObservableList<Vehicle> calibrateVehicleList = FXCollections.observableArrayList(new ArrayList<Vehicle>());

    LaneMonitor laneMonitor = new LaneMonitor(vehicleList);

    private Button saveButton;
    public Button settingButton;
    public static AtomicBoolean writeToSdipFinished = new AtomicBoolean(false);

    @FXML
    private void initialize() throws Exception {

        
        VehiclePanel vehiclePanel1 = new VehiclePanel();
        
        vbox.getChildren().add(vehiclePanel1);
        vehiclePanel1.setName("Lane 1");
        vehiclePanel1.clearPanel();

        VehiclePanel vehiclePanel2 = new VehiclePanel();
        vehiclePanel2.setName("Lane 2");
        vbox.getChildren().add(vehiclePanel2);
        vehiclePanel2.clearPanel();

        VehicleFilter.getVehicleFilter();

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
        
        HBox hbox2 = new HBox();
        hbox2.setStyle("-fx-font-size: 12pt");
        Label labelForListView1 = new Label("Vehicles List");
        Label labelForListView2 = new Label("Calibrate vehicles List");
        CheckBox checkBoxCaliState = new CheckBox("Enter into Calibration");
        checkBoxCaliState.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
            public void changed(ObservableValue<? extends Boolean> arg0, 
                                Boolean oldVal, Boolean newVal) {
				if(newVal == true){
                    //setup the calibration thread too
                    Calibrator cali = Calibrator.getCalibrator(calibrateVehicleList);
                    cali.setFunction(Calibrator.ENTER_CALIBRATION_MODE);
                    Thread caliThread = new Thread(cali);
                    caliThread.setDaemon(true);
                    caliThread.start();
                } else {
                    //setup the calibration thread too
                    Calibrator cali = Calibrator.getCalibrator(calibrateVehicleList);
                    cali.setFunction(Calibrator.LEFT_CALI_STATE);
                    Thread caliThread = new Thread(cali);
                    caliThread.setDaemon(true);
                    caliThread.start();
                }
			}
        });

        hbox2.getChildren().addAll(labelForListView1,
                new Label("                       "),
                labelForListView2,
                new Label("    "),
                checkBoxCaliState);
        hbox2.setAlignment(Pos.CENTER_LEFT);
        hbox2.setPadding(new Insets(10,50,0,50));
        hbox2.setSpacing(40);
        vbox.getChildren().add(hbox2);


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
        hbox.setPadding(new Insets(0, 10, 0, 10));
        hbox.setAlignment(Pos.TOP_LEFT);
        
        vbox.getChildren().add(new Label(""));
        settingButton = new Button("Settin" +
                "g");
        settingButton.setFont(new Font(10.0));
        settingButton.setDisable(true);
        MenuButton SDIPSettingButton = new MenuButton("SDIP Setting");
        SDIPSettingButton.setFont(new Font(10.0));
        SDIPSettingButton.setDisable(false);
        MenuItem sdipRst = new MenuItem("Reset SDIP");
        sdipRst.setOnAction(evt->{

        });
        MenuItem sdipCap = new MenuItem("Set Capacitor Bank");
        SDIPSettingButton.getItems().add(sdipRst);
        SDIPSettingButton.getItems().add(sdipCap);

        saveButton = new Button("Save Table");
        saveButton.setFont(new Font(10.0));
        saveButton.setDisable(false);
        Button sendButton = new Button("Send Table");
        sendButton.setFont(new Font(10.0));
        sendButton.setDisable(true);
        Button loadButton = new Button("Load Vehicle");
        loadButton.setFont(new Font(10.0));
        loadButton.setDisable(false);
        ProgressBar pb = new ProgressBar();
        pb.setMaxSize(80, 10);
        VBox.setVgrow(pb,Priority.ALWAYS);
        pb.setProgress(0);
        
        VBox vbox2 = new VBox(settingButton,loadButton,
            saveButton,sendButton,pb,SDIPSettingButton);
        VBox.setVgrow(saveButton, Priority.ALWAYS);
        VBox.setVgrow(sendButton, Priority.ALWAYS);
        VBox.setVgrow(settingButton,Priority.ALWAYS);
        VBox.setVgrow(SDIPSettingButton, Priority.ALWAYS);
        VBox.setVgrow(loadButton, Priority.ALWAYS);
        saveButton.setMaxWidth(Double.MAX_VALUE);
        sendButton.setMaxWidth(Double.MAX_VALUE);
        SDIPSettingButton.setMaxWidth(Double.MAX_VALUE);
        settingButton.setMaxWidth(Double.MAX_VALUE);
        loadButton.setMaxWidth(Double.MAX_VALUE);
        
        //vbox2.autosize();
        vbox2.setSpacing(5);
        hbox.getChildren().add(vbox2);

        TextArea calibrateInfo = new TextArea();
        calibrateInfo.setWrapText(true);
        calibrateInfo.setFont(new Font(10));
        calibrateInfo.textProperty().bind(caliInfoText);
        caliInfoText.set("Initialized ok...");
        calibrateInfo.setPrefWidth(130);
        calibrateInfo.setPrefHeight(150);
        calibrateInfo.setMinWidth(130);
        calibrateInfo.setMinHeight(150);
        calibrateInfo.setMaxWidth(130);
        calibrateInfo.setMaxHeight(150);
        //calibrateInfo.
        hbox.getChildren().add(calibrateInfo);

        //Set the event handler for all the buttons
        settingButton.setOnAction(event -> popup());
        sdipRst.setOnAction(event -> {
            Calibrator cali = Calibrator.getCalibrator(calibrateVehicleList);
            cali.setFunction(Calibrator.SDIP_DSP_RST);
            Thread caliThread = new Thread(cali);
            caliThread.start();
        });
        sdipCap.setOnAction(evt->{

        });
        saveButton.setOnAction(event -> {
            if(calibrateVehicleList.size() < 1){
                Alert alert = new Alert(AlertType.ERROR,
                        "No calibration itens selected.",
                        ButtonType.OK);
                alert.showAndWait();
            }else{
                FileChooser fc = new FileChooser();
                fc.setInitialDirectory(new File(System.getProperty("user.dir")));
                fc.setTitle("Save the calibration table:");

                File fid = fc.showSaveDialog(saveButton.getScene().getWindow());
                if(fid != null)
                {
                    Calibrator cali = Calibrator.getCalibrator(calibrateVehicleList);
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
        sendButton.setOnAction(event -> {
            Calibrator cali = Calibrator.getCalibrator(calibrateVehicleList);
            cali.setFunction(Calibrator.SEND_CALIBRATE_TABLE);
            Thread caliThread = new Thread(cali);
            caliThread.start();
            pb.progressProperty().bind(Calibrator.getProgressProperty());
        });
        loadButton.setOnAction(event -> {
            FileChooser fch = new FileChooser();
            fch.setInitialDirectory(new File(System.getProperty("user.dir")));
            File fid = fch.showOpenDialog(loadButton.getScene().getWindow());
            if(fid != null){
                Calibrator cali = Calibrator.getCalibrator(calibrateVehicleList);
                cali.setFunction(Calibrator.LOAD_CALIBRATE_TABLE);
                cali.setFile(fid);
                Thread caliThread = new Thread(cali);
                caliThread.start();
            }
        });
        arrowLeft.setOnAction(event -> {
            //remove the selected calibration vehicle list
            ObservableList<Integer> selectedIndices =
                listViewVehiclesCalibration.getSelectionModel().getSelectedIndices();
            ArrayList<Vehicle> removeVehicles = new ArrayList<>();
            for(Integer idx: selectedIndices){
                vehicleList.add(calibrateVehicleList.get(idx));
                removeVehicles.add(calibrateVehicleList.get(idx));
            }
            calibrateVehicleList.removeAll(removeVehicles);
        });
        arrowRight.setOnAction(event -> {
            //remove the selected vehicle list
            ObservableList<Integer> selectedIndices =
                listViewVehicles.getSelectionModel().getSelectedIndices();
            ArrayList<Vehicle> removeVehicles = new ArrayList<>();
            for(Integer idx: selectedIndices){
                calibrateVehicleList.add(vehicleList.get(idx));
                removeVehicles.add(vehicleList.get(idx));
            }
            vehicleList.removeAll(removeVehicles);
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

        //create the Lane Monitor thread
        laneMonitor.setPanel1(vehiclePanel1);
        laneMonitor.setPanel2(vehiclePanel2);
        laneMonitor.setCtr(this);
        Thread laneMonitorThread = new Thread(laneMonitor);
        laneMonitorThread.setDaemon(true);
        laneMonitorThread.start();

    }


    //popup dialog
    public void popup(){

        VehicleFilter filter = VehicleFilter.getVehicleFilter();
        final Stage dialog = new Stage();
        dialog.setTitle("Calibration Setting");
        Label vehicleInfo = new Label("Vehicle For Calibration:");
        Label wtInfo = new Label("Weight:    ");
        Label maxWtInfo = new Label("Max. Weight: ");
        Label minWtInfo = new Label("Min. Weight: ");
        Label maxSpeedInfo = new Label("Max. Speed:  ");
        Label minSpeedInfo = new Label("Min. Speed:  ");
        Label axisNoInfo = new Label("Axis No.     ");

        TextField tfWtInfo = new TextField(filter.calibrateWeight+"");
        RadioButton faixaA = new RadioButton("Lane A");
        RadioButton faixaB = new RadioButton("Lane B");
        ToggleGroup group = new ToggleGroup();
        faixaA.setToggleGroup(group);
        faixaB.setToggleGroup(group);
        if(filter.getFaixa() == 1){
            faixaA.setSelected(true);
        }
        if(filter.getFaixa() == 2){
            faixaB.setSelected(true);
        }

        TextField tfMaxWtInfo = new TextField(filter.getPesoTotalMaxKg()+"");
        TextField tfMinWtInfo = new TextField(filter.getPesoTotalMinKg()+"");
        TextField tfMaxSpeedInfo = new TextField(filter.getSpeedKmhRangeMax()+"");
        TextField tfMinSpeedInfo = new TextField(filter.getSpeedKmhRangeMin()+"");
        ObservableList<Integer> eixoNoList = FXCollections.observableArrayList();
        for(int i = 2; i < 10; i++)
            eixoNoList.add(i);
        ComboBox<Integer> comboAxisNoInfo = new ComboBox<Integer>(eixoNoList);
        int idx = comboAxisNoInfo.getItems().indexOf(filter.getAxisNo());
        comboAxisNoInfo.getSelectionModel().select(idx);
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
        vboxMain.getChildren().add(new HBox(axisNoInfo, comboAxisNoInfo));
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
        comboAxisNoInfo.disableProperty().bind(useFilter.selectedProperty().not());
        
        if(filter.getSpeedKmhRangeMax() > 10000){
            useFilter.setSelected(false);
        }

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

                    VehicleFilter filter = VehicleFilter.getVehicleFilter();
                    filter.calibrateWeight = Double.parseDouble(tfWtInfo.getText());
                    if(faixaA.isSelected()) filter.setFaixa(1);
                    if(faixaB.isSelected()) filter.setFaixa(2);
                    if(useFilter.isSelected()){
                        filter.setAxisNo(comboAxisNoInfo.getItems().get(
                            comboAxisNoInfo.getSelectionModel().getSelectedIndex()));
                        maxVal = Double.parseDouble(tfMaxWtInfo.getText());
                        minVal = Double.parseDouble(tfMinWtInfo.getText());
                        if(maxVal <= minVal) throw new Exception();
                        filter.setPesoTotalMaxKg(maxVal);
                        filter.setPesoTotalMinKg(minVal);

                        maxVal = Double.parseDouble(tfMaxSpeedInfo.getText());
                        minVal = Double.parseDouble(tfMinSpeedInfo.getText());
                        if(maxVal <= minVal) throw new Exception();
                        filter.setSpeedKmhRangeMax(maxVal);
                        filter.setSpeedKmhRangeMin(minVal);
                    } else {
                        filter.clear();
                    }

                    //set filter
                    Calibrator.getCalibrator(calibrateVehicleList);
                    dialog.close();
                    saveButton.setDisable(false);
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

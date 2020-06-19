package vehiclepanel;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import vehiclepanel.Calibrator.CalibrationPntsValidator;
import vehiclepanel.Calibrator.Calibrator;
import vehiclepanel.Calibrator.Communicator;

public class ChartController {
    
    @FXML
    BorderPane pane;
    @FXML
    Label chartTitle;
    @FXML
    HBox chartHbox;
    @FXML
    Button chartButtonLtArrow;
    @FXML
    Button chartButtonRtArrow;

    public void setCalibrateVehicleList(ObservableList<Vehicle> calibrateVehicleList) {
        this.calibrateVehicleList = calibrateVehicleList;
    }

    private ObservableList<Vehicle> calibrateVehicleList;

    //Organized vehicles - corresponding to the vehcles selected in each 
    //speed table:
    // Integer : speed value
    // ArrayList<Vehicle>: all the vehicles
    private HashMap<Integer, ArrayList<VehicleWithSelectMark>> vehicleDB;

    //List of Vehicles that needs to be added
    protected ArrayList<Vehicle> vehicles;
    
    private final int[] speedList = CalibrationPntsValidator.speedList;

    //control which chart is shown
    // 0 - speedList: calibration chart is shown
    // >= speedList.size() : calibration table chart
    private int currentChartIdx = 0;

    private ArrayList<ScatterChart<Number,Number>> charts;

    //calibration table
    //generate the calibration table
    //HashMap<SensorNo, HashMap<Speed, Table[]>>
    HashMap<Integer, HashMap<Integer, Integer[]>> table = null;
    
    public void initialize(){

        //add a button
        Button genTableButton = new Button("Generate Table");
        Button sendTable = new Button("Send Table");
        chartHbox.getChildren().add(1,genTableButton);
        chartHbox.getChildren().add(2, sendTable);

        vehicleDB = new HashMap<>();
        for(int speed: speedList){
            vehicleDB.put(speed, new ArrayList<VehicleWithSelectMark>());
        }
        currentChartIdx = 0;
        chartButtonLtArrow.setOnAction(event->{
            showPreviousChart();
        });

        chartButtonRtArrow.setOnAction(event->{
            showNextChart();
        });
        sendTable.setOnAction(actionEvent -> {
            Communicator comm = Communicator.getCommunicator(calibrateVehicleList);
            comm.setFunction(Communicator.SEND_CALIBRATE_TABLE);
            comm.setCalibrationTable(table);
            Thread commThread = new Thread(comm);
            commThread.start();
        });
        sendTable.setDisable(true);
        genTableButton.setOnAction(event->{
            Calibrator calibrator = Calibrator.getCalibrator();
            //get all the selected vehicles
            ArrayList<Vehicle> selectedVehicles = new ArrayList<>();
            for(Integer speed: vehicleDB.keySet()){
                for(VehicleWithSelectMark vel: vehicleDB.get(speed)){
                    if(vel.isSelected){
                        selectedVehicles.add(vel.vel);
                    }
                }
            }
            //validate the table
            //TODO: validate the datas

            try{
                //generate the calibration table
                table = calibrator.generateTable(selectedVehicles);
                if(table == null) throw new Exception();
                if(table.size() == 0){
                    table = null;
                    throw new Exception();
                }
            } catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Calibration process aborted, unknow error.",
                        ButtonType.OK);
            }
            //show chart
            currentChartIdx = speedList.length;
            showChart();
            sendTable.setDisable(false);
      });
        charts = new ArrayList<>();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                while(vehicles == null){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                String errorMsg = "The following vehicles can not meet the calibration specification:\n";
                int errorMsgInitLen = errorMsg.length();
                //get all the speed vehicles
                for(Vehicle vel: vehicles){
                    Integer speedVal = CalibrationPntsValidator.getSpeedInList(vel.getSpeed());
                    if(vehicleDB.keySet().contains(speedVal)){
                        vehicleDB.get(speedVal).add(new VehicleWithSelectMark(vel));
                    } else{
                        //TODO: show to user some data is not correct
                        errorMsg += vel.toString() + "\n";
                    }
                }
                if(errorMsg.length() > errorMsgInitLen){
                    Alert alert = new Alert(Alert.AlertType.WARNING,errorMsg,ButtonType.OK);
                    alert.showAndWait();
                }
                showDataAtSpeed(speedList[currentChartIdx]);
            }
        });

    }


    private void showNextChart()
    {
        currentChartIdx++;
        if(currentChartIdx == speedList.length){
            //check if there is any table ready or not
            if(table == null) currentChartIdx = 0;
        }
        //check the boudary
        if(currentChartIdx == speedList.length*3){
            currentChartIdx = 0;
        }

        showChart();
    }

    private void showChart(){
        if(currentChartIdx < speedList.length){
            showDataAtSpeed(speedList[currentChartIdx]);
        } else if(currentChartIdx < 2*speedList.length){
            showCalibrationData(1,speedList[currentChartIdx-speedList.length]);
        }else{
            showCalibrationData(3,speedList[currentChartIdx-2*speedList.length]);
        }
    }


    private void showPreviousChart()
    {
        currentChartIdx--;
        if(currentChartIdx == -1){
            //check if there is any table ready or not
            if(table == null) currentChartIdx = speedList.length-1;
            else {
                currentChartIdx = 3*speedList.length -1;
            }
        }
        showChart();
    }


    private void showDataAtSpeed(int speed)
    {
        ArrayList<VehicleWithSelectMark> vels = vehicleDB.get(speed);

        if(vels.size() == 0) charts.add(null);
        int[] limTemp = getExtremeTemperature(vels);
        int[] limWeight = getExtremWeight(vels);

        final NumberAxis xAxis = new NumberAxis("Temperature",limTemp[0],limTemp[1],1);
        final NumberAxis yAxis = new NumberAxis("Weight(Kg)",limWeight[0],limWeight[1],100);
        final ScatterChart<Number,Number> sc = new ScatterChart<>(xAxis, yAxis);
        final XYChart.Series<Number,Number> series = new XYChart.Series<>();
        sc.getData().addAll(series);
        sc.setLegendVisible(false);

        //remove the pane center node : chart node
        pane.getChildren().remove(pane.getCenter());
        sc.setTitle("Pnts for Speed = "+speed+"km/h");
        chartTitle.setText(" "+ getSelectedVehiclesNumber(vels)+" Selected.");
        pane.setCenter(sc);

        for(VehicleWithSelectMark vel: vels)
        {
            XYChart.Data<Number,Number> data =
                    new XYChart.Data<Number,Number>(vel.vel.getTemperature(),vel.vel.getTotalWeight());
            series.getData().add(data);
            //decorate the mark in chart
            decorateMark(data.getNode(), vel);
            data.getNode().setOnMouseClicked(e-> {
                vel.setSelected(!vel.isSelected);
                chartTitle.setText(" "+ getSelectedVehiclesNumber(vels)+" Selected.");
                decorateMark(data.getNode(), vel);
            });
        }

    }

    //decorate the node of a data mark according to the
    //vehicle selection property
    private void decorateMark(Node node, @NotNull VehicleWithSelectMark vel){
        if(vel.isSelected){
            node.setStyle("-fx-background-color: #860061, red;\n"
                    + "    -fx-background-insets: 0, 2;\n"
                    + "    -fx-background-radius: 5px;\n"
                    + "    -fx-padding: 5px;");
        } else {
            node.setStyle("-fx-background-color: #860061, white;\n"
                    + "    -fx-background-insets: 0, 2;\n"
                    + "    -fx-background-radius: 5px;\n"
                    + "    -fx-padding: 5px;");
        }
    }

    //get the number of selected vehicles
    private int getSelectedVehiclesNumber(ArrayList<VehicleWithSelectMark> vels)
    {
        int cnter = 0;
        for(VehicleWithSelectMark vel:vels){
            if(vel.isSelected){
                cnter++;
            }
        }
        return cnter;
    }


    //get the largest and smallest temperature from list
    private int[] getExtremeTemperature(@NotNull ArrayList<VehicleWithSelectMark> vels){
        int maxVal = Integer.MIN_VALUE;
        int minVal = Integer.MAX_VALUE;

        for (VehicleWithSelectMark vel: vels){
            int temp = vel.vel.getTemperature();
            if(temp >= maxVal) maxVal = temp;
            if(temp <= minVal) minVal = temp;
        }

        int[] tempLimit =  new int[]{minVal,maxVal};
        
        //enlarge the limit
        tempLimit[0] -= 10;
        tempLimit[1] += 10;

        return tempLimit;

    }

    //get the largest and smallest weight from list
    @NotNull
    private int[] getExtremWeight(ArrayList<VehicleWithSelectMark> vels){
        int maxVal = Integer.MIN_VALUE;
        int minVal = Integer.MAX_VALUE;

        for (VehicleWithSelectMark vel: vels){
            int weight = (int) vel.vel.getTotalWeight();
            if(weight >= maxVal) maxVal = weight;
            if(weight <= minVal) minVal = weight;
        }

        int[] weightLimit =  new int[]{minVal,maxVal};
        
        //enlarge the limit
        weightLimit[0] -= 100;
        weightLimit[1] += 100;

        return weightLimit;
    }


    private class VehicleWithSelectMark{
        protected Vehicle vel;

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        protected boolean isSelected;

        public VehicleWithSelectMark(Vehicle v){
            isSelected = false;
            vel = v;
        }
    }

    private int[] getMaxVal(Integer[] datas){
        int maxVal = Integer.MIN_VALUE;
        int minVal = Integer.MAX_VALUE;
        for(int data:datas){
            maxVal = Math.max(data, maxVal);
            minVal = Math.min(data,minVal);
        }
        return new int[]{maxVal, minVal};
    }

    private void showCalibrationData(int sensorNo, int speed)
    {
        Integer[] datas = table.get(sensorNo).get(speed);
        int[] maxMinVal = getMaxVal(datas);

        final NumberAxis xAxis = new NumberAxis("Temperature",-20, 80,1);
        final NumberAxis yAxis = new NumberAxis("Factor", 0, maxMinVal[0]+200,10);
        final LineChart<Number,Number> sc = new LineChart<>(xAxis, yAxis);
        final XYChart.Series<Number,Number> series = new XYChart.Series<>();
        sc.getData().addAll(series);
        sc.setLegendVisible(false);

        //remove the pane center node : chart node
        pane.getChildren().remove(pane.getCenter());
        sc.setTitle("Table for Sensor "+ sensorNo+" @ "+speed+"km/h.");
        pane.setCenter(sc);

        int temperature = -19;
        for(int factor: datas)
        {
            XYChart.Data<Number,Number> data =
                    new XYChart.Data<Number,Number>(temperature++,factor);
            series.getData().add(data);
        }
    }
}
package vehiclepanel;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.fxml.FXML;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

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

    //Organized vehicles - corresponding to the vehcles selected in each 
    //speed table:
    // Integer : speed value
    // ArrayList<Vehicle>: all the vehicles selected
    private HashMap<Integer, ArrayList<Vehicle>> vehicleDB;

    //List of Vehicles that needs to be added
    protected ArrayList<Vehicle> vehicles;  
    
    private final int[] speedList = {40,50,60,70,80};
    
    public void initialize(){
        vehicleDB = new HashMap<>();
        for(int speed: speedList){
            vehicleDB.put(speed, new ArrayList<Vehicle>());
        }

        //get all the speed vehicles
        for(Vehicle vel: vehicles){
            int speedVal = (int)(Math.round(vel.getSpeed()/10))*10;
            if(vehicleDB.keySet().contains(speedVal)){
                vehicleDB.get(speedVal).add(vel);
            } else{
                //TODO: show to user some data is not correct
            }
        }

    }


    private void showDataAtSpeed(int speed)
    {
        //remove the pane center node : chart node
        pane.getChildren().remove(pane.getCenter());
        
        ArrayList<Vehicle> vels = vehicleDB.get(speed);

        
        int[] limTemp = getExtremeTemperature(vels);
        int[] limWeight = getExtremWeight(vels);
        
        final NumberAxis xAxis = new NumberAxis("Temperature",limTemp[0],limTemp[1],1);
        final NumberAxis yAxis = new NumberAxis("Weight(Kg)",limWeight[0],limWeight[1],100);
        final ScatterChart<Number,Number> sc = new ScatterChart<>(xAxis, yAxis);

        sc.setTitle("Speed = " + speedList[0] + " Km/h");  

        final XYChart.Series<Number,Number> series = new XYChart.Series<>();
        
        for(Vehicle vel: vels)
        {
            series.getData().add(
                new XYChart.Data<Number,Number>(vel.getTemperature(),vel.getTotalWeight()));
        }
        sc.getData().addAll(series);
        pane.setCenter(sc);
    }


    //get the largest and smallest temperature from list
    private int[] getExtremeTemperature(ArrayList<Vehicle> vels){
        int maxVal = Integer.MIN_VALUE;
        int minVal = Integer.MAX_VALUE;

        for (Vehicle vel: vels){
            int temp = vel.getTemperature();
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
    private int[] getExtremWeight(ArrayList<Vehicle> vels){
        int maxVal = Integer.MIN_VALUE;
        int minVal = Integer.MAX_VALUE;

        for (Vehicle vel: vels){
            int weight = (int) vel.getTotalWeight();
            if(weight >= maxVal) maxVal = weight;
            if(weight <= minVal) minVal = weight;
        }

        int[] weightLimit =  new int[]{minVal,maxVal};
        
        //enlarge the limit
        weightLimit[0] -= 1000;
        weightLimit[1] += 1000;

        return weightLimit;
    }

}
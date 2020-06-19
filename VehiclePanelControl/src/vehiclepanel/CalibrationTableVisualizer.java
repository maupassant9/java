package vehiclepanel;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

//Visulize the calibration chart
public class CalibrationTableVisualizer implements Runnable {

    protected ChartController chartCtr;
    public static  Stage chartStage;
    protected final ObservableList<Vehicle> vehicleList;

    public CalibrationTableVisualizer(ObservableList<Vehicle> vehicleList){
        this.vehicleList = vehicleList;
    }


    @Override
    public void run() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
              //load the chart function
                Parent root = null;
                try {
                  FXMLLoader loader = new FXMLLoader(getClass().getResource("chart.fxml"));
                  root = loader.load();
                  chartCtr = loader.getController();

                } catch (IOException e) {
                  e.printStackTrace();
                }

                chartStage = new Stage();
                chartStage.setTitle("Select Calibration Points ....");
                chartStage.setScene(new Scene(root));
                chartStage.setResizable(false);
                chartStage.initModality(Modality.APPLICATION_MODAL);
                chartStage.initOwner(Main.stage);
                chartStage.showAndWait();

                chartStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

                  @Override
                  public void handle(WindowEvent event) {
                      // TODO Auto-generated method stub
                      //here should close the thread used
                  }
                });

            }
        });

        //is interrupted?
        try {
            while(chartCtr == null)
                Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //generate the array list and pass to controller
        chartCtr.setCalibrateVehicleList(vehicleList);

        ArrayList<Vehicle> list = new ArrayList<Vehicle>();
        for(Vehicle vel:vehicleList){
            list.add(vel);
        }
        chartCtr.vehicles = list;
        while(true){
            try{
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        chartStage.fireEvent(
                                new WindowEvent(
                                        chartStage,
                                        WindowEvent.WINDOW_CLOSE_REQUEST));
                    }
                });
                break;
            }
        }
    }
}

package vehiclepanel;

import java.io.IOException;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

//Visulize the calibration chart
public class CalibrationTableVisualizer implements Runnable {

    protected ChartController chartCtr;
    protected Stage chartStage;
    protected final ObservableList<Vehicle> vehicleList;

    public CalibrationTableVisualizer(ObservableList<Vehicle> vehicleList){
        this.vehicleList = vehicleList;
    }


    @Override
    public void run() {
        Platform.runLater(new Runnable(){
            
            @Override
            public void run() {
                //load the chart function
                Parent root = null;
                try{
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("chart.fxml"));
                    chartCtr = loader.getController();
                    root = loader.load();
                } catch (IOException e){
                    e.printStackTrace();
                }
                //TODO: chartCtr.vehicles = 
                chartStage = new Stage();
                chartStage.setTitle("Select Calibration Points ....");
                chartStage.setScene(new Scene(root));
                chartStage.setResizable(false);
                chartStage.show();

                chartStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

                    @Override
                    public void handle(WindowEvent event) {
                        // TODO Auto-generated method stub
                        //here should close the thread used 

                    }
                    
                });


                //is interrupted?
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
        }});
    }
}

package vehiclepanel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("vehiclePanelExample.fxml"));
        primaryStage.setTitle("VehiclePanel");
        Scene scene = new Scene(root, 800,500);
        // scene.getStylesheets().add(getClass().
        //         getResource("vehiclePanelControl/vehiclePanel.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

package switchingpower;
import java.io.IOException;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import switchingpower.view.RootCtr;

public class SwitchingPowerApp extends Application{
	private Stage primaryStage;
	private AnchorPane rootLayout;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Switching Power V0.1");

		setUserAgentStylesheet(STYLESHEET_CASPIAN);
		//Load root layout from fxml file generated by SceneBuilder
		initRootLayout();
	}
		
	
	public void initRootLayout(){
		try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SwitchingPowerApp.class.getResource("view/bkgroundPane.fxml"));
            rootLayout = (AnchorPane) loader.load();

            RootCtr rootCtr = loader.getController();
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.setResizable(true); //froze the resize of window
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}

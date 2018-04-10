package electronicLab;
	
import java.io.IOException;

import electronicLab.model.Communication;
import electronicLab.model.Serial;
import electronicLab.view.PainelGerador;
import electronicLab.view.RootController;
import electronicLab.view.SettingController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


public class MainApp extends Application {
	private Stage primaryStage;
	private Stage secondStage;
	private BorderPane rootLayout;
	
	private Communication communication;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("ElectronicLab V0.1");

		setUserAgentStylesheet(STYLESHEET_CASPIAN);
		
		initRootLayout();
		showMainView();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	
    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

	
	public void initRootLayout(){
		try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            RootController rootCtr = loader.getController();
            rootCtr.setMainApp(this);
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false); //froze the resize of window
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	
	public void showMainView(){
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/mainView.fxml"));
            AnchorPane mainView = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(mainView);
            
            //connect the controller with the mainApp
            PainelGerador controller = loader.getController();
            controller.setMainApp(this);
            
            //connect to device, open uart
            
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void showAbout(){
    		Alert alert = new Alert(Alert.AlertType.INFORMATION);
    		alert.setTitle("About");
    		alert.setHeaderText("About");
    		alert.setContentText("Author: Dong Xia\nVer:0.9\nAddress: Curitiba,"
    				+ "Brasil\nEmail:xiadongSimple@gmail.com\nMinha Loja:www.xxx.com");
    		alert.showAndWait();	
 	}
	

	public boolean showSetting(){
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/SerialSetting.fxml"));
            AnchorPane SettingView = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Configuração");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(SettingView);
            dialogStage.setScene(scene);
            
            SettingController ctr = loader.getController();
            ctr.setDialogStage(dialogStage);
            ctr.setMainApp(this);
            dialogStage.setResizable(false);
            
            dialogStage.showAndWait();
            
            return ctr.isConnectClicked();
            
            //connect to device, open uart
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
	}

	public void setCommunication(Communication s)
	{
		this.communication = s;
	}
	
	public Communication getCommunication()
	{
		return this.communication;
	}
}

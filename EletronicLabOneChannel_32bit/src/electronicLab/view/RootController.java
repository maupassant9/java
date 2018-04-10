package electronicLab.view;

import electronicLab.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class RootController {

	MainApp mainApp;
	
	@FXML
	private void handleMenuAbout(){
		mainApp.showAbout();
	}
	
	@FXML
	private void handleSerialSetting(){
		mainApp.showSetting();
	}
	
	@FXML
	private void initialize() {
	
	}
	
	public void setMainApp(MainApp m){
		this.mainApp = m;
	}
	
	@FXML
	private void showSetting(){
		if(mainApp.getCommunication() == null||(mainApp.getCommunication().isReady == false))
		{
			this.mainApp.showSetting();	
		}
		else
		{
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Info");
			alert.setHeaderText("O dispositivo j¨¢ est¨¢ connectado!");
			//alert.setContentText("Connect to device fails!");
			alert.showAndWait();
		}
		if(mainApp.getCommunication() == null||(mainApp.getCommunication().isReady == false))
		{
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Device connection error");
			alert.setHeaderText("Please check the state of the device");
			alert.setContentText("Connect to device fails!");
			alert.showAndWait();
		}
	}
	
	@FXML
	private void handleClose() throws Exception{
		//Close everything
		System.out.println("App Stopped");
		System.exit(0);
		
	}
}

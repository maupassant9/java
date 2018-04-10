package electronicLab.view;

import electronicLab.MainApp;
import electronicLab.model.CommandGenerator;
import electronicLab.model.Communication;
import electronicLab.model.Serial;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

public class SettingController {

	private Stage dialogStage;
	
	private MainApp mainapp;
	
	@FXML
	private Button buttonConfigurar;
	@FXML
	private Button buttonCancel;
	@FXML
	private ChoiceBox<String> serialList;
	
	public void setDialogStage(Stage dialogStage){
		this.dialogStage = dialogStage;
	}
	
	@FXML
	private void initialize() {
		serialList.setItems(FXCollections.observableArrayList(
			    "COM1", "COM2 ", "COM3", "COM4", "COM5"));

	}
		
	public boolean isConnectClicked(){
		return true;
	}
	
	public void setMainApp(MainApp mainapp)
	{
		this.mainapp = mainapp;
	}
	
	@FXML
	private void handleConfigure(){
		//connect to the device
		try{
			String serialName = serialList.getValue();
			Communication connect = new Communication(serialName);
			// - Sending the command to connect board, if any error happens
			//    generate communication error.
			//byte [] cmd = {(byte) 0xa5,(byte) 0xa5,(byte) 0xa5,0x33,0x55,0x55,0x55};
			byte ack = connect.send(CommandGenerator.CMD_CONNECT);
			System.out.println(ack);
			if(ack == CommandGenerator.CMD_RESPONDE_OK)
			{
				connect.isReady = true;
				mainapp.setCommunication(connect);
				System.out.println("connected");
			}
			else
			{
				connect.close();
				System.out.println("closed");
			}

		}
		catch(Exception e)
		{
			//inform the user that it is error
			System.err.println(e);
		}
		finally {
			dialogStage.close();
		}
	}
	
	
}

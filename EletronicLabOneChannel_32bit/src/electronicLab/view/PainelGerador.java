package electronicLab.view;

import java.io.IOException;
import electronicLab.MainApp;
import electronicLab.model.CommandGenerator;
import electronicLab.model.Communication;
import electronicLab.model.DataInvalidException;
import electronicLab.model.Drawer;
import electronicLab.model.Serial;
import electronicLab.model.WaveConf;
import electronicLab.model.WaveformConstants;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class PainelGerador {
		
	@FXML
	private ToggleButton switchCH0;

	//Toggle buttons for selecting wave type
	private ToggleGroup groupWaveTypeCH0;
	@FXML
	private ToggleButton senButtonCH0;
	@FXML
	private ToggleButton triButtonCH0;
	@FXML
	private ToggleButton serraButtonCH0;
	@FXML
	private ToggleButton serraInvButtonCH0;
	@FXML
	private ToggleButton quadradoButtonCH0;
	@FXML
	private ToggleButton userDefinedButtonCH0;
	
	//Toggle buttons for selecting frequency
	private ToggleGroup groupFreqCH0;
	@FXML
	private ToggleButton kHzButtonCH0;
	@FXML
	private ToggleButton hzButtonCH0;
	
	//Toggle buttons for selecting altitude
	private ToggleGroup groupAltitudeCH0;
	@FXML
	private ToggleButton ampVoltsButtonCH0;
	@FXML
	private ToggleButton ampMicroVoltsButtonCH0;
	
	
	//Text Field for ch 0
	@FXML
	private TextField freqFieldCH0;
	@FXML
	private TextField altitudeFieldCH0;
	@FXML
	private TextField dutyFieldCH0;
	@FXML
	private TextField offsetFieldCH0;
	
	@FXML
	private Label dutyLabelCH0;
	@FXML
	private Label dutyUnitLabelCH0;
	@FXML
	private LineChart<Number, Number> chart;
	@FXML
	private NumberAxis xAxis;
	@FXML
	private NumberAxis yAxis;
	
	private MainApp mainApp;
	
	private Drawer chartDrawer;
	
	public void setMainApp(MainApp app){
		this.mainApp = app;
	}
	
	@FXML
	private void initialize() {
		groupWaveTypeCH0 = new ToggleGroup();
		senButtonCH0.setToggleGroup(groupWaveTypeCH0);
		triButtonCH0.setToggleGroup(groupWaveTypeCH0);
		serraButtonCH0.setToggleGroup(groupWaveTypeCH0);
		serraInvButtonCH0.setToggleGroup(groupWaveTypeCH0);
		quadradoButtonCH0.setToggleGroup(groupWaveTypeCH0);
		userDefinedButtonCH0.setToggleGroup(groupWaveTypeCH0);
		
		groupFreqCH0 = new ToggleGroup();
		kHzButtonCH0.setToggleGroup(groupFreqCH0);
		hzButtonCH0.setToggleGroup(groupFreqCH0);

		groupAltitudeCH0 = new ToggleGroup();
		ampVoltsButtonCH0.setToggleGroup(groupAltitudeCH0);
		ampMicroVoltsButtonCH0.setToggleGroup(groupAltitudeCH0);
		
		
		freqFieldCH0.setText("1000");
		hzButtonCH0.setSelected(true);

		altitudeFieldCH0.setText("1.0");
		ampVoltsButtonCH0.setSelected(true);
		
		offsetFieldCH0.setText("0");
		dutyFieldCH0.setText("0");
		
		chartDrawer = new Drawer(chart);
		//chart.setTitle("A Chart title");
	}
		
	private int getWaveformIndex(ToggleGroup tg){
		String str = tg.selectedToggleProperty().toString();
		if(str.contains("senButton")) return WaveformConstants.WAVETYPE_SIN;
		if(str.contains("triButton")) return WaveformConstants.WAVETYPE_TRIANGLE;
		if(str.contains("serraButton")) return WaveformConstants.WAVETYPE_SERRA;
		if(str.contains("serraInvButton")) return WaveformConstants.WAVETYPE_SERRA_INV;
		if(str.contains("quadradoButton")) return WaveformConstants.WAVETYPE_SQUARE;
		if(str.contains("userDefinedButton")) return WaveformConstants.WAVETYPE_USERDEFINED;
		return 0;
	}
	
	private float getFreq(ToggleGroup tg, float freq){
		String str = tg.selectedToggleProperty().toString();
		if(str.contains("kHzButton")) return freq*1000f;
		if(str.contains("hzButton")) return freq;
		return 0;
	}
	
	private float getAltitude(ToggleGroup tg, float altitude){
		String str = tg.selectedToggleProperty().toString();
		if(str.contains("ampMicroVoltsButton")) return (altitude/1000f);
		if(str.contains("ampVoltsButton")) return altitude;
		return 0;
	}
	
	
	private void disableChannelTotal(){
		disableChannel();
		switchCH0.setDisable(true);
	}
	
	private void disableChannel( ){
		
			senButtonCH0.setDisable(true);
			triButtonCH0.setDisable(true);
			serraButtonCH0.setDisable(true);
			userDefinedButtonCH0.setDisable(true);
			kHzButtonCH0.setDisable(true);
			hzButtonCH0.setDisable(true);
			ampVoltsButtonCH0.setDisable(true);
			ampMicroVoltsButtonCH0.setDisable(true);
			
			freqFieldCH0.setDisable(true);
			altitudeFieldCH0.setDisable(true);
			serraInvButtonCH0.setDisable(true);
			quadradoButtonCH0.setDisable(true);
			offsetFieldCH0.setDisable(true);
			dutyFieldCH0.setDisable(true);
	}
	
	private void enableChannel(){

			senButtonCH0.setDisable(false);
			triButtonCH0.setDisable(false);
			serraButtonCH0.setDisable(false);
			userDefinedButtonCH0.setDisable(false);
			kHzButtonCH0.setDisable(false);
			hzButtonCH0.setDisable(false);
			ampVoltsButtonCH0.setDisable(false);
			ampMicroVoltsButtonCH0.setDisable(false);
			serraInvButtonCH0.setDisable(false);
			quadradoButtonCH0.setDisable(false);
			offsetFieldCH0.setDisable(false);
			dutyFieldCH0.setDisable(false);
			
			freqFieldCH0.setDisable(false);
			altitudeFieldCH0.setDisable(false);
	}

	//Handles for buttons:ON
	@FXML
	private void handleTurnOnCH0()   {
		//if The buttion is turned on
		if(switchCH0.isSelected()){
			sendWave();		
		}
		else{
			//6 - Send to cmd to MCU to stop.
			Communication comm = mainApp.getCommunication();
			try {
				comm.send(CommandGenerator.CMD_CLOSE);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			enableChannel();
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("A Canal 0 está desligado.");
			alert.setHeaderText("Aviso: A Canal 0 está desligado!");
			alert.setContentText(null);
			alert.showAndWait();
			}
	}

	
	private void sendWave(){
		//1 - Check the communication status
		// if erro throw a communication error
		if(mainApp.getCommunication() == null){
			mainApp.showSetting();
		}
		if((mainApp.getCommunication() !=null)&&mainApp.getCommunication().isReady){
			//2 - Formulating data structure from the data filled
			try{
				WaveConf waveCH0 = new WaveConf();
				waveCH0.setWaveType(getWaveformIndex(groupWaveTypeCH0));
				waveCH0.setFreq(getFreq(groupFreqCH0,
						Float.parseFloat(freqFieldCH0.getText())));
				System.out.println(getAltitude(groupAltitudeCH0,
						Float.parseFloat(altitudeFieldCH0.getText())));
				waveCH0.setAltitude(getAltitude(groupAltitudeCH0,
						Float.parseFloat(altitudeFieldCH0.getText())));
				waveCH0.setOffset(Float.parseFloat(offsetFieldCH0.getText()));
				if(waveCH0.getWaveform() == WaveformConstants.WAVETYPE_SIN){
					waveCH0.setPhase(Float.parseFloat(dutyFieldCH0.getText()));
				}
				else if(waveCH0.getWaveform() == WaveformConstants.WAVETYPE_SQUARE){
					waveCH0.setDuty(Integer.parseInt(dutyFieldCH0.getText()));
				}

				waveCH0.setChannel(0);
				
				//3 - generate the command and data for MCU
				CommandGenerator cg = new CommandGenerator(waveCH0);
				byte[] buffer = cg.generatePackage();
				Communication comm = mainApp.getCommunication();
				
				//Send the command at most four times
				byte ack = 0, times = 4;
				while(ack != CommandGenerator.CMD_QUERY_CONFIGURED){
					comm.send(buffer);
					comm.delay();
					comm.delay();
					comm.delay();
					comm.delay();
					comm.delay();
					comm.delay();
					comm.delay();
					//Query if the device received successfully
					comm.send(CommandGenerator.CMD_QUERY);
					ack = comm.getResponse();
					System.out.println("ack = "+ack);
					if(times-- == 0) throw new IOException();
					if(ack == CommandGenerator.CMD_QUERY_DISCONNECTED)
						throw new IOException();
				}
				System.out.println("Parameters correctely sent!");
				comm.delay();
				comm.delay();
				comm.delay();
				comm.delay();
				comm.delay();
				comm.delay();
				comm.delay();
				
				buffer = cg.genWaveform();
				byte[] test = cg.generatePackage(buffer);
				for(int i = 0; i < test.length; i++) System.out.printf("%x,",test[i]);
				ack = comm.send(test);
				//check if need to send data too.
				//cg.calcParameters();
				//4 - Sending the command or the data to MCU, if any error happens
				//    generate communication error.
				//5 - wait for response and inform the user the results.
				
				//6 - All success, then we inform user and disable the buttons
				disableChannel();
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("A Canal 0 está ligado.");
				alert.setHeaderText("Aviso: A Canal 0 está ligado!");
				alert.setContentText(null);
				alert.showAndWait();	
					
			}
			catch (DataInvalidException e){
				e.printStackTrace();
				switchCH0.setSelected(false);
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Invalid Field");
				alert.setHeaderText("Please correct invalid fields");
				alert.setContentText("O Amplitude tem que ser dentro de [0.1,10] "
						+ "volts!\n A frequência tem que ser dentro de [1,100kHz]");
				alert.showAndWait();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				switchCH0.setSelected(false);
				mainApp.getCommunication().close();
				mainApp.setCommunication(null);
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Device connection error");
				alert.setHeaderText("Please check the state of the device");
				alert.setContentText("Connect to device fails!");
				alert.showAndWait();
				e.printStackTrace();
			} catch (purejavacomm.PureJavaIllegalStateException e){
				switchCH0.setSelected(false);
				mainApp.getCommunication().close();
				mainApp.setCommunication(null);
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Device connection error");
				alert.setHeaderText("Please check the state of the device");
				alert.setContentText("Connect to device fails!");
				alert.showAndWait();
				e.printStackTrace();
			}
		}
		else{
			switchCH0.setSelected(false);
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Device connection error");
			alert.setHeaderText("Please check the state of the device");
			alert.setContentText("Connect to device fails!");
			alert.showAndWait();
		}
	}	
	
	//Handlees for button: UserDefined
	@FXML 
	private void handleUserDefinedCH0() throws Exception {
		if(userDefinedButtonCH0.isSelected()){
			//Open a file chooser
			FileChooser fileChooser = new FileChooser();
			//文档类型过滤器
			ExtensionFilter extFilter = new ExtensionFilter("wve files (*.wve)", "*.wve");
			fileChooser.getExtensionFilters().add(extFilter);
			fileChooser.showOpenDialog(mainApp.getPrimaryStage());
			
			//check data
		}
	}
	
	//handler for button: SenButton
	@FXML
	private void handleSenButtonCH0(){
		if(senButtonCH0.isSelected()){
			//draw the chart
			chartDrawer.drawSin();
			dutyFieldCH0.setDisable(false);
			dutyLabelCH0.setText("Fase");
			dutyUnitLabelCH0.setText("Grau");
		}
		else{
			chartDrawer.clear();
		}
	}
	

	
	//Handlees for button: TriButtonCH0
	@FXML 
	private void handleTriButtonCH0() throws Exception {
		if(triButtonCH0.isSelected()){
			//draw the chart
			dutyFieldCH0.setDisable(true);
			chartDrawer.drawTri();
		}
		else{
			//clear the chart
			dutyFieldCH0.setDisable(false);
			chartDrawer.clear();
		}
	}
	
	//Handlees for button: SerraButtonCH0
	@FXML 
	private void handleSerraButtonCH0() throws Exception {
		if(serraButtonCH0.isSelected()){
			//draw the chart
			dutyFieldCH0.setDisable(true);
			chartDrawer.drawSerra();
		}
		else{
			//clear the chart
			dutyFieldCH0.setDisable(false);
			chartDrawer.clear();
		}
	}
	
	//Handlees for button: SerraButtonCH0
	@FXML 
	private void handleSerraInvButtonCH0() throws Exception {
		if(serraInvButtonCH0.isSelected()){
			//draw the chart
			dutyFieldCH0.setDisable(true);
			chartDrawer.drawInv();
		}
		else{
			//clear the chart
			dutyFieldCH0.setDisable(false);
			chartDrawer.clear();
		}
	}
	
	//Handlees for button: SerraButtonCH0
	@FXML 
	private void handleQuadradoButtonCH0() throws Exception {
		if(quadradoButtonCH0.isSelected()){
			//System.out.println("quadrado seleteed");
			dutyFieldCH0.setDisable(false);
			dutyLabelCH0.setText("Duty");
			dutyUnitLabelCH0.setText("%");
			chartDrawer.drawQua();
		}
		else{
			//clear the chart
			dutyFieldCH0.setDisable(true);
			chartDrawer.clear();
		}
	}
	
}

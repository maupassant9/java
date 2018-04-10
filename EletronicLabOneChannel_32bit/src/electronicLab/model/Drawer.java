package electronicLab.model;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

//Draw beautiful signals

public class Drawer {

	private LineChart<Number, Number> chartHandler;	
	private XYChart.Series<Number,Number> dataSin = new XYChart.Series<>();
	private XYChart.Series<Number,Number> dataTri = new XYChart.Series<>();
	private XYChart.Series<Number,Number> dataSerra = new XYChart.Series<>();
	private XYChart.Series<Number,Number> dataInv = new XYChart.Series<>();
	private XYChart.Series<Number,Number> dataQua = new XYChart.Series<>();
	private XYChart.Series<Number,Number> dataUser = new XYChart.Series<>();

	public Drawer(LineChart<Number,Number> chart){
		chartHandler = chart;
		chartHandler.setCreateSymbols(false);
		//Construct the data of wave
		dataSin.setName("Sin");
		for(int i = 0; i < 1000; i++){
			dataSin.getData().add(new XYChart.Data<Number, Number>(i, Math.sin(2*Math.PI*i/200)));
		}
		
		dataSerra.setName("Serra");
		dataInv.setName("INV");
		dataQua.setName("Qua");
		dataTri.setName("TRI");
		for(int i = 0; i < 1000; i++){
			int j = i%100;
			dataSerra.getData().add(new XYChart.Data<Number,Number>(i,j/50.0-1));
			dataInv.getData().add(new XYChart.Data<Number,Number>(i,1-(j/50.0)));
			dataQua.getData().add(new XYChart.Data<Number,Number>(i,(j>50?1.0:-1.0)));
			dataTri.getData().add(new XYChart.Data<Number,Number>(i,(j<50?j:100-j)/25.0-1));
		}
		
	}
	
	public void drawSin(){
		//Clear first
		clear();
		chartHandler.getData().add(dataSin);
	}
	
	public void drawSerra(){
		clear();
		chartHandler.getData().add(dataSerra);
	}
	
	public void drawInv(){
		clear();
		chartHandler.getData().add(dataInv);
	}
	
	public void drawQua(){
		clear();
		chartHandler.getData().add(dataQua);
	}
	
	public void drawTri(){
		clear();
		chartHandler.getData().add(dataTri);
	}
	
	
	//Clear the chart
	public void clear(){
		chartHandler.getData().clear();
	}
	
	public void append(int data){
		
	}
	
	public void append(int[] data){
		
		
	}
}

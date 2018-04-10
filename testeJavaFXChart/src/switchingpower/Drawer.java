package switchingpower;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

public class Drawer{

	private LineChart<Number,Number> chart;
	private XYChart.Series<Number,Number> data;
	
	public Drawer(LineChart<Number, Number> chart,
			XYChart.Series<Number,Number> data){
		this.chart = chart;
		this.chart.setCreateSymbols(false);
		this.data = data;
	}
	
	public void draw(){
		synchronized(chart){
			chart.getData().add(data);
		}
	}
	

		
	//Clear the chart
	public void clear(){
		chart.getData().clear();
	}


}

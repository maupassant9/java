package switchingpower.view;
import java.util.ArrayList;
import java.util.Collection;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.CacheHint;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.*;
import javafx.util.Duration;
import switchingpower.Drawer;

public class RootCtr {

	@FXML
	private LineChart<Integer,Integer> chart;
	@FXML
	private NumberAxis xAxis;
	@FXML
	private NumberAxis yAxis;
	@FXML 
	private TabPane tp;
	@FXML
	private Canvas canvas;

	
	@FXML
	private void initialize() throws InterruptedException {

		chart.setCache(true);
		chart.setCacheHint(CacheHint.SPEED);
		//Create a thread to draw
		XYChart.Series<Integer,Integer> data = new XYChart.Series<>();
		chart.setCreateSymbols(false);
		chart.getData().add(data); //bind to data
		
		//chart.getXAxis().setAutoRanging(true);
		Tarefa tarefa = new Tarefa(chart,data);
		
		ScheduledService<Series<Integer,Integer>>  svc = new ScheduledService<Series<Integer,Integer>>(){
			protected Task<Series<Integer,Integer>> createTask(){
				return new Task<Series<Integer,Integer>>(){
					@Override
					protected Series<Integer,Integer> call() throws Exception {
						// TODO Auto-generated method stub
						Platform.runLater(tarefa);
						return null;
					}
				};
			}
					
		};
					
		svc.setDelay(Duration.millis(1000));
		svc.setPeriod(Duration.millis(67));
		svc.start();
	}
	
	static class Tarefa implements Runnable{
		static int j = 0;
		LineChart<Integer,Integer> chart;
		Series<Integer,Integer> tmpData1 = new Series<Integer,Integer>();
		Series<Integer,Integer> tmpData2 = new Series<Integer,Integer>();
		Series<Integer, Integer> tmpData;
		ArrayList<Data<Integer,Integer>> tmp1,tmp2;
				
		public Tarefa(LineChart<Integer,Integer> chart, Series<Integer,Integer> data){
			this.chart = chart;
			this.tmpData = data;
			tmpData.getData().add(new Data<Integer,Integer>(0,0));
			tmp1 = new ArrayList<>();
			tmp2 = new ArrayList<>();
			
			for(int i = 0; i < 1000; i++){
				if(i < 300) {
					tmpData1.getData().add(new Data<Integer,Integer>(i,50));
					tmpData2.getData().add(new Data<Integer,Integer>(i,50));
					tmp1.add((new Data<Integer,Integer>(i,50)));
					tmp2.add((new Data<Integer,Integer>(i,50)));
					
				}else {
					if(i < 600) {
						tmpData1.getData().add(new Data<Integer,Integer>(i,(int) (50*Math.sin(2*Math.PI*i/10)+50)));
						tmpData2.getData().add(new Data<Integer,Integer>(i,50));
						tmp1.add(new Data<Integer,Integer>(i,(int) (50*Math.sin(2*Math.PI*i/10)+50)));
						tmp2.add((new Data<Integer,Integer>(i,50)));
					} else {
						if( i < 900) {
							tmpData1.getData().add(new Data<Integer,Integer>(i,50));
							tmpData2.getData().add(new Data<Integer,Integer>(i,(int) (50*Math.sin(2*Math.PI*i/10 + Math.PI)+50)));
							tmp2.add(new Data<Integer,Integer>(i,(int) (50*Math.sin(2*Math.PI*i/10)+50)));
							tmp1.add((new Data<Integer,Integer>(i,50)));
						}else {
							tmpData1.getData().add(new Data<Integer,Integer>(i,50));
							tmpData2.getData().add(new Data<Integer,Integer>(i,50));
							tmp1.add((new Data<Integer,Integer>(i,50)));
							tmp2.add((new Data<Integer,Integer>(i,50)));
						}
					}
				}
			}
			
		}
		@Override
//		public void run(){
//			j++; j = j%2;
//			chart.getData().clear();
//			if(j == 0){
//				chart.getData().add(tmpData1);
//			}else{
//				chart.getData().add(tmpData2);
//			}		
//		}
//		
		public void run(){
			j++; j = j%2;
			tmpData.getData().clear();
			if(j == 0){
				tmpData.getData().addAll(tmp1);
				
			}else{
				tmpData.getData().addAll(tmp2);
			}
		}
		
	}
	
}

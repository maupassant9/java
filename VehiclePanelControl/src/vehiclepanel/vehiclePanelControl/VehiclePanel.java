package vehiclepanel.vehiclePanelControl;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Font;
import vehiclepanel.Vehicle;

import static javafx.scene.paint.Color.BLACK;

public class VehiclePanel extends Region {

    final private int PREFERRED_WIDTH = 800;
    final private int PREFERRED_HEIGHT = 150;
    final private int MINIMUM_WIDTH = 800;
    final private int MINIMUM_HEIGHT = 150;
    final private int MAXIMUM_WIDTH = 800;
    final private int MAXIMUM_HEIGHT = 150;

    final private int VEHICLE_TAIL_LENGTH = 30;
    final private int VEHICLE_WHEEL_SIZE = 12;
    final private int VEHICLE_HEAD_WIDTH = 10;
    final private int VEHICLE_HEAD_HEIGHT = 30;

    final private int MARGIN_VEHICLE_TAIL_LEFT = 20;
    final private int MARGIN_VEHICLE_TAIL_TOP = PREFERRED_HEIGHT - VEHICLE_WHEEL_SIZE - VEHICLE_TAIL_LENGTH;
    final private int MARGIN_VEHICLE_LINE_TOP = PREFERRED_HEIGHT - VEHICLE_WHEEL_SIZE;
    final private int MARGIN_WHEEL_TOP = PREFERRED_HEIGHT - VEHICLE_WHEEL_SIZE * 2;
    final private int MARGIN_HEAD_TOP = MARGIN_VEHICLE_LINE_TOP - VEHICLE_HEAD_HEIGHT - 1;

    private String name;

    private Vehicle vehicle;


    public VehiclePanel(Vehicle vehicle, String name){
        this.vehicle = vehicle;
        this.name = name;

        //init the region
        init();
        initGraphic();
    }

    public VehiclePanel(Vehicle vehicle){
        this(vehicle, "Anonymous Lane");
    }

    public VehiclePanel(){
        this(null);
    }

    public void init(){
        if (Double.compare(getWidth(), 0) <= 0 || Double.compare(getHeight(), 0) <= 0 ||
                Double.compare(getPrefWidth(), 0) <= 0 || Double.compare(getPrefHeight(), 0) <= 0) {
            //setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT+20);
        }
        if (Double.compare(getMinWidth(), 0) <= 0 || Double.compare(getMinHeight(), 0) <= 0) {
            //setMinSize(MINIMUM_WIDTH, MINIMUM_HEIGHT+20);
        }
        if (Double.compare(getMaxWidth(), 0) <= 0 || Double.compare(getMaxHeight(), 0) <= 0) {
           // setMaxSize(MAXIMUM_WIDTH, MAXIMUM_HEIGHT+20);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    private void initGraphic(){
        setBackground(new Background(new BackgroundFill(Color.LIGHTGREY,CornerRadii.EMPTY,new Insets(0))));
    }


    private Line createLine(int x0, int y0, int x1, int y1){
        Line line = new Line();
        line.setStartX(x0);
        line.setStartY(y0);
        line.setEndX(x1);
        line.setEndY(y1);
        line.setStrokeWidth(2);
        return line;
    }

    private Circle createWheel(double radius){
        Circle wheel = new Circle(radius);
        wheel.setFill(Color.AQUA);
        wheel.setStroke(BLACK);
        return wheel;
    }

    private void addVehicleTail(HBox hbox){
        //Add vehicle tail
        final Line line = createLine(0,0,0,VEHICLE_TAIL_LENGTH);
        hbox.getChildren().add(line);
        HBox.setMargin(line,new Insets(MARGIN_VEHICLE_TAIL_TOP,
                0,0,MARGIN_VEHICLE_TAIL_LEFT));
    }


    private void addVehicleLine(HBox hbox, int sz){
        //Add vehicle tail---first wheel
        final Line line = createLine(0,0,sz,0);
        hbox.getChildren().add(line);
        HBox.setMargin(line, new Insets(MARGIN_VEHICLE_LINE_TOP,0,0,0));
    }

    private void addWheel(HBox hbox){
        final Circle wheel = createWheel(VEHICLE_WHEEL_SIZE);
        hbox.getChildren().add(wheel);
        HBox.setMargin(wheel, new Insets(MARGIN_WHEEL_TOP,0,0,0));
    }

    private void addVehicleHead(HBox hbox){

        Polyline head = new Polyline(new double[]{0.0,0.0,
                VEHICLE_HEAD_WIDTH,VEHICLE_HEAD_HEIGHT,0,VEHICLE_HEAD_HEIGHT});
        head.setStrokeWidth(2);
        HBox.setMargin(head,new Insets(MARGIN_HEAD_TOP,0,0,0));
        hbox.getChildren().add(head);
    }

    private void addDirectionArrow(VBox vbox){
        Polyline arrow = new Polyline(new double[]{0.0,5.0,40.0,5.0,40.0,0.0,45.0,5.0,40.0,10.0,40.0,5.0});
        arrow.setStrokeWidth(2);
        //VBox.setMargin(arrow,new Insets(MARGIN_HEAD_TOP,0,0,0));
        vbox.getChildren().add(arrow);
    }

    private void addLaneSpeedInfo(HBox hbox){

        VBox vboxInfos = new VBox();
        Label laneLabel = new Label();

        //laneLabel.getStylesheets().add("vehiclePanel-speedtext");


        laneLabel.setText(name);
        //laneLabel.setFont(Font.font("Arial Black", 20));
        laneLabel.setId("laneNameStyle");
        vboxInfos.getChildren().add(laneLabel);
        vboxInfos.setAlignment(Pos.BASELINE_RIGHT);
        Label laneLabel2 = new Label("Speed:  "+vehicle.getSpeed()+" km/h");
        //laneLabel2.setFont(Font.font(20));
        //laneLabel2.setTextFill(Color.IVORY);
        laneLabel2.setId("SpeedStyle");
        vboxInfos.getChildren().add(laneLabel2);
        addDirectionArrow(vboxInfos);
        vboxInfos.setPadding(new Insets(5,10,0,0));
        HBox.setHgrow(vboxInfos,Priority.ALWAYS);
        hbox.getChildren().add(vboxInfos);
    }

    public void displayPanel(Vehicle vehicle){
        getChildren().clear();
        if(vehicle == null) return;

        this.vehicle = vehicle;
        VBox vbox = new VBox();
        //vbox.getStylesheets().add(getClass().getResource("vehiclePanel.css").toExternalForm());
        HBox hbox = new HBox();
        HBox hboxDistInfos = new HBox();
        HBox hboxEixoInfos = new HBox();


        hbox.setPrefSize(PREFERRED_WIDTH,PREFERRED_HEIGHT);
        hbox.setMinSize(MINIMUM_WIDTH,MINIMUM_HEIGHT);
        hbox.setMaxSize(MAXIMUM_WIDTH,MAXIMUM_HEIGHT);
        hbox.setBackground(new Background(new BackgroundFill(Color.SLATEGRAY, CornerRadii.EMPTY, new Insets(0))));
        hboxDistInfos.setSpacing(10.0);
        hboxDistInfos.setPadding(new Insets(0,0,0,10));
        hboxEixoInfos.setSpacing(10.0);
        hboxEixoInfos.setPadding(new Insets(0,0,0,10));
        vbox.setBorder(new Border(new BorderStroke(Color.DIMGRAY, BorderStrokeStyle.SOLID,CornerRadii.EMPTY,BorderWidths.DEFAULT)));

        addVehicleTail(hbox);

        //Add distance information
        addVehicleLine(hbox, 20);
        addWheel(hbox);
        for(int i = 0; i < vehicle.getWtPerEixo().size() - 1; i++){
            //hboxDistInfos.getChildren().add(new Label("D"+i+":   "+ vehicle.getDistEntreEixos().get(i)+"m"));
            addVehicleLine(hbox, (int) (vehicle.getDistEntreEixos().get(i)*10.0));
            addWheel(hbox);
        }
        int lineWidth= (int)(vehicle.getDistEntreEixos().get(vehicle.getDistEntreEixos().size() - 1)*10);
        //add the last Distance information
        //hboxDistInfos.getChildren().add(new Label("D"+i+":   "+ vehicle.getDistEntreEixos().get(i)+"m"));
        addVehicleLine(hbox, 20);

        addDistLabels(hboxDistInfos.getChildren(),vehicle);
        addEixoLabels(hboxEixoInfos.getChildren(),vehicle);
        hboxDistInfos.setId("DistInfoBg");
        hboxEixoInfos.setId("EixoInfoBg");


        if((lineWidth - VEHICLE_HEAD_WIDTH) > 0)
            addVehicleLine(hbox, lineWidth-VEHICLE_HEAD_WIDTH);
        addVehicleHead(hbox);

        addLaneSpeedInfo(hbox);
        vbox.getChildren().add(hbox);

        vbox.getChildren().add(hboxDistInfos);
        vbox.getChildren().add(hboxEixoInfos);
        getChildren().addAll(vbox);
    }


    protected void addDistLabels(ObservableList<Node> nodes, Vehicle vehicle){
        for(int i = 0; i < vehicle.getDistEntreEixos().size(); i++) {
            Label label = new Label("D" + i + ":   " + vehicle.getDistEntreEixos().get(i) + "m");
            nodes.add(label);
            label.setId("DistTextStyle");
        }
    }

    protected void addEixoLabels(ObservableList<Node> nodes, Vehicle vehicle){
        for(int i = 0; i < vehicle.getDistEntreEixos().size(); i++) {
            Label label = new Label("E" + i + ":   " + vehicle.getWtPerEixo().get(i) + "kg");
            nodes.add(label);
            label.setId("EixoTextStyle");
        }
    }


}

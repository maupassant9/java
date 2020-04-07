package vehiclepanel.Calibrator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import vehiclepanel.vehiclePanelControl.VehiclePanel;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import vehiclepanel.*;
import vehiclepanel.CommSDIP.*;

public class LaneMonitor implements Runnable {

    private final byte COM_TEMPERATURE = (byte) 0x05;
    private final byte COM_VEHICLE_RECORD = (byte) 0x04;
    private final byte COM_ENTER_CALIBRATION_MODE = (byte)0x60;
    private final byte COM_RELEASE_CALIBRATION_MODE = (byte)0x61;
    private final byte COM_TABLE_RSVED_OK = (byte)0x62;
    private final byte COM_WRITE_FLASH_OK = (byte)0x58;
    private final int MAX_WAITING_SIZE = 20;
    private final int COM_RESET_DSP = (byte)0x40;
    private final int COM_RELEASE_DSP = (byte)0x63;

    private static VehicleFilter filter;
    private Controller ctr;

    private ObservableList<Vehicle> vehicleList;

    private ArrayList<Vehicle> vehicleWaitForDisplayOnPanel1;
    private ArrayList<Vehicle> vehicleWaitForDisplayOnPanel2;
    private VehiclePanel panel1;
    private VehiclePanel panel2;

    private int currentTemp;
    private int cnter = 5; // very bad habit, but i will keep this way.
    public static volatile boolean calibrated = false;
    public static volatile AtomicBoolean notified = new AtomicBoolean(false);
    public static volatile Object laneLock;


    public LaneMonitor(ObservableList<Vehicle> vehs) {
        vehicleList = vehs;

        vehicleWaitForDisplayOnPanel1 = new ArrayList<>();
        vehicleWaitForDisplayOnPanel2 = new ArrayList<>();
        currentTemp = 0;

        panel1 = null;
        panel2 = null;
        ctr = null;
        filter = new VehicleFilter();
        laneLock = new Object();
    }


    private class PanelUpdater implements Runnable{

        @Override
        public void run() {
            while(true){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // check if there is any vehicle should be shown on panel
                cnter--;
                if (cnter == 0) { // every 5 times, execute here.
                    clrPanel(panel1);
                    clrPanel(panel2);
                    cnter = 20;
                }
    
                showOnPanel(vehicleWaitForDisplayOnPanel1, panel1);
                showOnPanel(vehicleWaitForDisplayOnPanel2, panel2);
            }
        }

    }


    @Override
    public void run() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        
        Thread panelUpdaterTh = new Thread(new PanelUpdater());
        panelUpdaterTh.setDaemon(true);
        panelUpdaterTh.start();

        while (true) {
            //System.out.println("Calibrator executed...");
            // read from bufferRx
            while (CommThread.bufferRx.size() != 0) {
                ArrayList<Byte> rxDatas = CommThread.bufferRx.poll();
                switch (rxDatas.get(2)) // check the type
                {
                    case COM_TEMPERATURE:
                        currentTemp = rxDatas.get(4);
                        break;
                    case COM_VEHICLE_RECORD:
                        try {
                            addVehicles(rxDatas);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case COM_ENTER_CALIBRATION_MODE:
                        calibrated = true;
                        Platform.runLater(new Runnable(){
                        
                            @Override
                            public void run() {
                                ctr.setCaliParameters.setDisable(false);
                                ctr.caliInfoText.set("Enter into Calibration Mode...");
                            }
                        });
                        
                        break;
                    case COM_TABLE_RSVED_OK:
                        synchronized(laneLock){
                            notified.set(true);
                            laneLock.notifyAll();
                        }
                        Platform.runLater(new Runnable(){
                        
                            @Override
                            public void run() {
                                ctr.caliInfoText.set("Table updated OK.");
                            }
                        });
                        break;
                    case COM_WRITE_FLASH_OK:
                        synchronized(laneLock){
                            notified.set(true);
                            laneLock.notifyAll();
                        }
                        Platform.runLater(new Runnable(){
                        
                            @Override
                            public void run() {
                                ctr.caliInfoText.set("Flash updated OK.");
                            }
                        });
                        break;
                    case COM_RESET_DSP:
                    case COM_RELEASE_DSP:
                    case COM_RELEASE_CALIBRATION_MODE:
                        synchronized(laneLock){
                            notified.set(true);
                            laneLock.notifyAll();
                        }
                        break;
                }
            }

           
        }
    }

    // check if there is any vehicle in waiting list to be shown
    // if yes, show the vehicle, and remove it from the waiting list
    private void showOnPanel(ArrayList<Vehicle> ls, VehiclePanel panel) {

        if (ls.size() != 0) {
            Vehicle vehicle = ls.get(0);
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    panel.displayPanel(vehicle);
                }
            });
            ls.remove(0);
        }
    }

    // check if there is a vehicle in panel showing
    // if yes: clear the panel and move the vehcile to
    // vehicleList
    private void clrPanel(VehiclePanel panel) {
        if (panel != null) {
            if ((panel.getVehicle() != null)) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        vehicleList.add(panel.getVehicle());
                        panel.clearPanel();
                    }
                });
            }
        }
    }

    // Generate a vehicle
    // 1. generate vehicles from the serial data
    // 2. filter the vehicles
    // 3. Add the vehicles to the waiting list if there is some space in waiting
    // list
    private void addVehicles(ArrayList<Byte> vehicleSerialData) throws Exception {

        Vehicle vehicle = generateVehicle(vehicleSerialData);

        if(filter != null)
                vehicle = filter.filter(vehicle);
        
        if (vehicle != null)
        {
            if(vehicle.getFaixa() == 1)
            {
                if(vehicleWaitForDisplayOnPanel1.size() <= MAX_WAITING_SIZE)
                    vehicleWaitForDisplayOnPanel1.add(vehicle);
            }
            if(vehicle.getFaixa() == 2)
            {
                if(vehicleWaitForDisplayOnPanel2.size() <= MAX_WAITING_SIZE)
                    vehicleWaitForDisplayOnPanel2.add(vehicle);
            }
        }
    }

    private final byte COM_WIM_RESULT_OK = 0x00;

    // generate vehicle instance from serialized data
    Vehicle generateVehicle(ArrayList<Byte> serializedData) {
        int pnter = 0;
        int vehicleId = 0;
        double speed;
        int faixa;
        double totalWt;
        // get data flag
        pnter = 4;
        if (serializedData.get(pnter) != COM_WIM_RESULT_OK)
            return null;
        // valid data for wim
        pnter = 8;
        vehicleId = (UByteLikeC.wrapVal(serializedData.get(pnter++)) << 8);
        vehicleId += UByteLikeC.wrapVal(serializedData.get(pnter++));
        // speed from m/s to km/h
        speed = (double) (UByteLikeC.wrapVal(serializedData.get(pnter++)) << 8);
        speed += (double) UByteLikeC.wrapVal(serializedData.get(pnter++));
        speed *= 3.6;
        speed = (double)Math.round(speed)/100.0;
        // lane no.
        faixa = serializedData.get(pnter++)+1;
        // total weight
        totalWt = (double) (UByteLikeC.wrapVal(serializedData.get(pnter++)) << 16);
        totalWt += (double) (UByteLikeC.wrapVal(serializedData.get(pnter++)) << 8);
        totalWt += (double) (UByteLikeC.wrapVal(serializedData.get(pnter++)));

        // eixo no
        int numEixo = serializedData.get(pnter++);
        int category = serializedData.get(pnter++);
        // get eixo weight and distance
        ArrayList<Double> eixoWtList = new ArrayList<>();
        ArrayList<Double> distList = new ArrayList<>();
        for (int i = 0; i < numEixo; i++) {
            double eixoWt;
            // eixo weight hi
            eixoWt = (double) (UByteLikeC.wrapVal(serializedData.get(pnter++)) << 8);
            eixoWt += (double) (UByteLikeC.wrapVal(serializedData.get(pnter++)));
            double dist;
            // if not the last one
            if (i != numEixo - 1) {
                dist = (double) (UByteLikeC.wrapVal(serializedData.get(pnter++)) << 8);
                dist += (double) (UByteLikeC.wrapVal(serializedData.get(pnter++)));

                dist = (double)Math.round(dist)/100.0;
                distList.add(dist);
            }
            eixoWtList.add(eixoWt);
        }

        //Add eixo weight measured by each sensor
        HashMap<Integer,ArrayList<Double>> eixoWtPerSensors = new HashMap<>();
        
        for(int eixo = 1; eixo <= 3; eixo++){
            ArrayList<Double> eixoWtPerOneSensor = new ArrayList<>();
            for(int i = 0; i < numEixo; i++){
                double value = (double)(UByteLikeC.wrapVal(
                        serializedData.get(pnter++))<<8);
                value += (UByteLikeC.wrapVal(serializedData.get(pnter++)));
                eixoWtPerOneSensor.add(value);
            }
            eixoWtPerSensors.put(eixo, eixoWtPerOneSensor);
        }
        
        currentTemp = UByteLikeC.wrapVal(serializedData.get(pnter++));

        Vehicle vehicle = null;
        try {
            vehicle = new Vehicle(vehicleId + "", 
                distList, speed, eixoWtList, 
                totalWt, currentTemp, faixa,eixoWtPerSensors);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vehicle;
    }


    //to set the panel 1 for this class
    public void setPanel1(VehiclePanel panel1) {
        this.panel1 = panel1;
    }
    //to set the panel 2 for this class
    public void setPanel2(VehiclePanel panel2) {
        this.panel2 = panel2;
    }

    public void setFilter(VehicleFilter filter) {
        this.filter = filter;
    }

    public void setCtr(Controller ctr) {
        this.ctr = ctr;
    }

}
package vehiclepanel.Calibrator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import vehiclepanel.Vehicle;
import vehiclepanel.CommSDIP.CommThread;
import vehiclepanel.CommSDIP.UByteLikeC;

public class Calibrator implements Runnable {
    private ObservableList<Vehicle> vehicles;
    public static final int SAVE_CALIBRATE_TABLE = 0x01;
    public static final int GENERATE_CALIBRATE_TABLE = 0x02;
    public static final int SEND_CALIBRATE_TABLE = 0x03;
    public static final int LOAD_CALIBRATE_TABLE = 0x04;
    public static final int ENTER_CALIBRATION_MODE = 0x05;

    // =======SDIP COMMANDS=========//
    private final byte COM_SDIP_ENTER_INTO_CALI = (byte) 0x60;
    private final byte COM_SDIP_RELEASE_FROM_CALI = (byte) 0x61;
    private final byte COM_SDIP_UPDATE_TABLE_IN_RAM = (byte) 0x59;
    private final byte COM_SDIP_SAVE_IN_FLASH = (byte)0x58;
    private final byte COM_SDIP_RESET_DSP = (byte)0x40;
    private final byte COM_SDIP_RELEASE_DSP = (byte)0x63;

    private Integer function;
    private File fid = null;

    private Button button;

    private static Calibrator myown = null;

    private static ConcurrentLinkedQueue<ArrayList<Byte>> bufferTx;

    // This is the raw data of the calibration vehicles, this data
    // should be saved in a file
    // HashMap<Integer, ArrayList<Vehicel>>
    //            |___Temperature
    private HashMap<Integer, ArrayList<Vehicle>> calibrationVehicles;

    // This is the map that should be written into SDIP
    // HashMap<Integer, HashMap<Integer, Integer[]>>
    //           |___Sesnor NO    |         |
    //              Vehicle Speed_|         |
    //                           Table______|
    private HashMap<Integer, HashMap<Integer, Integer[]>> calibrationTable;

    private static VehicleFilter filter;

    private Calibrator(ObservableList<Vehicle> caliVels, VehicleFilter filter) {
        vehicles = caliVels;
        function = GENERATE_CALIBRATE_TABLE;
        this.filter = filter;
        calibrationTable = new HashMap<>();

        for (int sensorNo = 1; sensorNo <= 3; sensorNo++) {
            HashMap<Integer, Integer[]> table = new HashMap<>();
            for (int speed = 40; speed <= 80; speed += 10) {
                table.put(speed, new Integer[100]);
            }
            calibrationTable.put(sensorNo, table);
        }

        calibrationVehicles = new HashMap<>();
        bufferTx = null;
    }

    public static Calibrator getCalibrator(ObservableList<Vehicle> caliVels, VehicleFilter filter) {

        if (myown == null) {
            myown = new Calibrator(caliVels, filter);
        }
        return myown;
    }

    @Override
    public void run() {

        // get the correct bufferTx
        if (bufferTx == null) {
            CommThread comm = null;
            while (true) {
                comm = CommThread.getCommThread();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (comm != null)
                    break;
            }
            bufferTx = CommThread.bufferTx;
        }

        switch (function) {
            case SAVE_CALIBRATE_TABLE:
                saveCalibrationInfos();
                break;
            case GENERATE_CALIBRATE_TABLE:
                generateTable();
                //Enable save table button
                Platform.runLater(new Runnable(){
                
                    @Override
                    public void run() {
                        button.setDisable(false);
                    }
                });
                break;
            case SEND_CALIBRATE_TABLE:
                try {
                    //reset dsp for better communication
                    if(!sendCmdDsp(COM_SDIP_RESET_DSP)) throw new Exception();
                    if(!sendTable()) throw new Exception();
                    //write to flash
                    if(!writeToFlash()) throw new Exception();
                    if(!sendCmdDsp(COM_SDIP_RELEASE_DSP)) throw new Exception();
                    if(!sendCmdDsp(COM_SDIP_RELEASE_FROM_CALI)) throw new Exception();
                } catch (Exception e1) {
                    Platform.runLater(new Runnable(){
                    
                        @Override
                        public void run() {
                            Alert alert = new Alert(AlertType.ERROR,
                                "Write to SDIP error", ButtonType.OK);
                            alert.showAndWait();
                        }
                    });
                    e1.printStackTrace();
                }
                break;
            case LOAD_CALIBRATE_TABLE:
                loadTable();
                break;

            case ENTER_CALIBRATION_MODE:
                try {
                    enterCalibrationMode();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

    }

    public void setFunction(Integer function) {
        this.function = function;
    }

    // Generate the calibration table
    public void generateTable() {
        // add all the vehicles to the calibration
        // vehicle list waiting for process
        addCalibrationVehicles(vehicles);
        HashMap<Integer, Double> caliPts = generateCalibrationFactor(1);
        // generate the table
        generateTable(1, caliPts);
        caliPts = generateCalibrationFactor(2);
        generateTable(2, caliPts);
        caliPts = generateCalibrationFactor(3);
        generateTable(3, caliPts);

    }

    // add the vehicle to the calibration pts list,
    // Organize it with the temperature.
    private void addCalibrationVehicles(List<Vehicle> vels) {
        // add new vehicles to calibrationPts
        for (Vehicle vel : vels) {
            //if no calibration wt infor is avaliable
            if(vel.getCalibrateWt() == null){
                vel.setCalibrateWt(filter.calibrateWeight);
            }
            
            int temp =(int)(Math.round(((double)vel.getTemperature())/10)*10);
            if (calibrationVehicles.containsKey(temp)) {
                calibrationVehicles.get(temp).add(vel);
            } else {
                ArrayList<Vehicle> array = new ArrayList<>();
                array.add(vel);
                calibrationVehicles.put(temp, array);
            }
        }
    }

    // From above calibrationVehicles, generate calibrationPts,
    // this is a temperary which is the processed calibration datas
    // Return value: HashMap <Integer,Double> calibrationPts;
    //              Temperature___|     |_______calibration factor
    private HashMap<Integer, Double> generateCalibrationFactor(int sensorNo) {
        HashMap<Integer, Double> calibrationPts = new HashMap<>();
        // get the measurement of some temperature
        for (int temp : calibrationVehicles.keySet()) {
            double sum = 0;
            double sz = calibrationVehicles.get(temp).size();
            for (Vehicle vels_temp : calibrationVehicles.get(temp)) {
                double totalWt = vels_temp.getTotalWeightPerSensor(sensorNo);
                if(totalWt != 0){
                    sum += (vels_temp.getCalibrateWt()/totalWt);
                } else {
                    sum = 0;
                }
            }
            // get the calibration factor @ temperature
            double factor = sum/sz;
            calibrationPts.put(temp, factor);
        }
        return calibrationPts;
    }

    // generate calibration table for sensor of each speed using
    // calibration point datas
    private void generateTable(int sensorNo, HashMap<Integer, Double> caliPts) {
        
        HashMap<Integer, Integer[]> table = calibrationTable.get(sensorNo);
        ArrayList<Integer> sortedKeySet = new ArrayList<Integer>(caliPts.keySet());
        Collections.sort(sortedKeySet);

        Integer[] table_speed = table.get(40);
        for (int temp = -20; temp < 80; temp++) {
            int idx = (temp + 20);
            double factor = 0;
            if(temp <= sortedKeySet.get(0)){ //if temp @ left most 
                factor = caliPts.get(sortedKeySet.get(0));
            } else if (temp >= sortedKeySet.get(sortedKeySet.size()-1)) {
                //if temp @ right most
                factor = caliPts.get(sortedKeySet.get(sortedKeySet.size()-1));
            } else { //if temp @ middle
                int[] pos = getIndex(temp, sortedKeySet);
                if(pos[0] == pos[1]){
                    factor = caliPts.get(sortedKeySet.get(pos[0]));
                } else{
                    double temp0 = sortedKeySet.get(pos[0]);
                    double delta_temp = sortedKeySet.get(pos[1]) - temp0;
                    double tempNow = (double) temp;
                    double factor0 = caliPts.get(sortedKeySet.get(pos[0]));
                    double delta_factor = caliPts.get(sortedKeySet.get(pos[1])) - factor0;
                    factor += factor0;
                    factor += (tempNow - temp0) * delta_factor / delta_temp;
                }

            }


            table_speed[idx] = (int) (factor * 100);
        }

        // just copy speed table from 40
        Integer[] table_40kmh = table.get(40);
        for (int speed = 50; speed <= 80; speed += 10) {
            table_speed = table.get(speed);
            for (int temp = -20; temp < 80; temp++) {
                int idx = (temp + 20);
                table_speed[idx] = table_40kmh[idx];
            }
        }

        //Enable save table button
        Platform.runLater(new Runnable(){
        
            @Override
            public void run() {
                button.setDisable(false);
            }
        });
    }

    // get temperature index in the calibrated vehicle
    private int[] getIndex(int temp, ArrayList<Integer> list) {
        int[] idxs = new int[2];
        idxs[1] = 0;
        idxs[0] = 1;

        for (Integer val : list) {
            if (temp > val) {
                idxs[1]++;
            } else if (temp < val) {
                idxs[0] = idxs[1] - 1;
                return idxs;
            } else {
                idxs[0] = idxs[1];
                return idxs;
            }
        }

        idxs[0] = idxs[1];
        return idxs;
    }

    private void saveCalibrationInfos() 
    {
        if(fid != null){
            //save the table
            try{
                FileWriter out = new FileWriter(fid);

                for(Integer temp : calibrationVehicles.keySet()){
                    ArrayList<Vehicle> vels = calibrationVehicles.get(temp);
                    for(Vehicle vel : vels){
                        out.write(vel.toStringComplete()+"\n");
                    }
                }
                out.close();
                Platform.runLater(new Runnable(){
                
                    @Override
                    public void run() {
                        button.setDisable(false);
                    }
                });
            }catch (IOException e){
                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        Alert alert = new Alert(AlertType.ERROR,"File Saved Error!!!",ButtonType.OK);
                        alert.showAndWait();
                    }
                });
                     
            }     
        }
    }


    //from calibrationVehicles to writable string
    private void loadTable()
    {
        try{
            BufferedReader in = new BufferedReader(new FileReader(fid));
            List<Vehicle> vels = new ArrayList<>();
            while(true)
            {
                String line = in.readLine();
                if(line == null) break;
                vels.add(Vehicle.parseVehicle(line));
            }
            addCalibrationVehicles(vels);


        }catch (Exception e){
            Platform.runLater(new Runnable(){
            
                @Override
                public void run() {
                    
                    Alert alert = new Alert(AlertType.ERROR,"File format error!", ButtonType.OK);
                    alert.showAndWait();
                    
                }
            }); 
        }

        
    }

    //private sendTable to SDIP
    private boolean sendTable() throws InterruptedException
    {
        boolean res = true;
        for(int sensorNo = 1; sensorNo < 4; sensorNo++)
        {
            HashMap<Integer,Integer[]> tables = calibrationTable.get(sensorNo);
            for(Integer speed:tables.keySet()){

                //send here the table 
                ArrayList<Byte> datas = new ArrayList<>();
                Integer[] table = tables.get(speed);
                datas.add((byte)filter.getFaixa());
                datas.add((byte)sensorNo);
                datas.add((byte)speed.intValue());
                for(Integer val:table){
                    datas.add((byte)((val&0xff00)>>8));
                    datas.add((byte)(val&0xff));
                }
                Byte[] dataInBytes = new Byte[datas.size()];
                dataInBytes = datas.toArray(dataInBytes);
                
                
                int times = 10; // try 3 times.
                while(true)
                {
                    if(times-- == 0) break;
                    bufferTx.add(generateCmd(COM_SDIP_UPDATE_TABLE_IN_RAM, dataInBytes));
                    //wait until received a respond
                    System.out.println("write to table: sensor "+sensorNo +","+speed +"kmh");
                    synchronized(LaneMonitor.laneLock){
                        LaneMonitor.laneLock.wait(10000);
                        if(LaneMonitor.notified.get()){
                            LaneMonitor.notified.set(false);
                            break;
                        }
                    }
                }
                if(times < 0) res = false;
            }
        }
        return res;
    }


    public void setFile(File fid) {
        this.fid = fid;
    }


    public void setSaveButton(Button saveButton) {
        this.button = saveButton;
    }


    private void enterCalibrationMode() throws InterruptedException
    {
        while(true){
            //send the command to enter calibration mode
            bufferTx.add(generateCmd(COM_SDIP_ENTER_INTO_CALI, null));
            //wait until calibrated flag is ture
            Thread.sleep(1000);
            if(LaneMonitor.calibrated) break;
        }
    }


    private ArrayList<Byte> generateCmd(byte type, Byte[] datas)
    {
        ArrayList<Byte> command = new ArrayList<>();
        UByteLikeC crc = new UByteLikeC(0);
        command.add(((byte)(0xaa)));
        crc.add(0xaa);
        command.add((byte)0x01);
        crc.add(0x01);
        command.add(type);
        crc.add(type);
        //add size
        if(datas != null){
            command.add((byte)datas.length);
            crc.add(datas.length);
            for(Byte data: datas){
                command.add((byte)data);
                crc.add(data);
            }
        } else {
            command.add((byte)0);
        }

        command.add((byte)crc.value);

        return command;
    }

    private boolean writeToFlash() throws InterruptedException
    {
        boolean res = true;

        int times = 0x03;
        while(true)
        {
            if(times-- == 0) break;
            Byte[] data = new Byte[1];
            data[0] = Byte.valueOf((byte)filter.getFaixa());
            bufferTx.add(generateCmd(COM_SDIP_SAVE_IN_FLASH, data));
            System.out.println("Write to Flash "+(3-times)+" time...");

            synchronized(LaneMonitor.laneLock){
                LaneMonitor.laneLock.wait(10000);
                if(LaneMonitor.notified.get()){
                    LaneMonitor.notified.set(false);
                    break;
                }
            }
        }
        if(times < 0)
            res = false;
        return res;  

    }


    private void writeToTextView(String str){
        Platform.runLater(new Runnable(){
        
            @Override
            public void run() {
                
                
            }
        });
    }

    public static VehicleFilter getFilter() {
        return filter;
    }

    public static void setFilter(VehicleFilter filter) {
        Calibrator.filter = filter;
    }

    private boolean sendCmdDsp(byte cmd) throws InterruptedException {
        int times = 0x03;
        while(true)
        {
            if(times-- == 0) break;
            bufferTx.add(generateCmd(cmd,null));
            synchronized(LaneMonitor.laneLock){
                LaneMonitor.laneLock.wait(10000);
                if(LaneMonitor.notified.get()){
                    LaneMonitor.notified.set(false);
                    break;
                }
            }
        }
        if(times < 0)
            return false;
        return true;
    }

    
}

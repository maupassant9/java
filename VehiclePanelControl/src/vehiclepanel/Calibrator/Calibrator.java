package vehiclepanel.Calibrator;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import vehiclepanel.Vehicle;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

//Calibrator:
// Generate the calibration table
// with the vehicle calibration points
public class Calibrator {

    // This is the map that should be written into SDIP
    // HashMap<Integer, HashMap<Integer, Integer[]>>
    //           |___Sensor NO    |         |
    //              Vehicle Speed_|         |
    //                           Table______|
    private HashMap<Integer, HashMap<Integer, Integer[]>> calibrationTable;

    private static VehicleFilter filter;

    private final int MULTIPLY_FACTOR_TABLE = 1000;
    //The only one calibrator for this program is
    private static Calibrator cali = null;

    //speed parametes in km/h
    private final int SPEED_HEAD = 40;
    private final int SPEED_INTERVAL = 10;
    private final int SPEED_TAIL = 80;


    private Calibrator(){
        cali = this;
        filter = VehicleFilter.getVehicleFilter();

    }

    public static Calibrator getCalibrator(){
        if(cali == null){
            return new Calibrator();
        } else {
            return cali;
        }
    }

    //=======================================//
    //   The main function for calibration  //
    //======================================//
    //Realize the calibration function here
    // Generate the calibration table
    public HashMap<Integer, HashMap<Integer, Integer[]>>
        generateTable(List<Vehicle> vehicles) throws CalibrationException {

        //generate a new calibration table
        calibrationTable = new HashMap<>();
        // add all the vehicles to the calibration
        // vehicle list waiting for process
        organizeVehicles(vehicles);
        //the points should be validated here
        if(!CalibrationPntsValidator.isValidData(organizedVehicles)){
            Platform.runLater(()->{
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        CalibrationPntsValidator.warningMsg,
                        ButtonType.CANCEL);
                alert.showAndWait();
            });
            throw new CalibrationException(
                    CalibrationPntsValidator.warningMsg,CalibrationException.CRITICAL_ERR);
        }

        //generate the calibration points here
        HashMap<Integer, HashMap<Integer, Double>> caliPts;
        for(int sensor = 1; sensor <= 3; sensor++) {
            caliPts = generateCalibrationFactor(sensor);
            // generate the table
            calibrationTable.put(sensor, generateTable(caliPts));
        }
        writeTable2File(calibrationTable);

        return calibrationTable;
    }


    //===============================//
    //  Organize the vehicle datas   //
    //===============================//
    // This is the raw data of the calibration vehicles, this data
    // should be saved in a file
    // HashMap<Speed, HashMap<Temperature, ArrayList<Vehicle>>>
    private HashMap<Integer, HashMap<Integer, ArrayList<Vehicle>>> organizedVehicles;
    // add the vehicle to the calibration pts list,
    // Organize it with the temperature and speed
    private HashMap<Integer, HashMap<Integer, ArrayList<Vehicle>>>
        organizeVehicles(List<Vehicle> vels)  {

        organizedVehicles = new HashMap<>();

        //HashMap<Speed, ArrayList<Vehicle>>
        HashMap<Integer, ArrayList<Vehicle>> organizedPerSpeed = new HashMap<>();
        // add new vehicles to calibrationPts
        for (int speed = SPEED_HEAD; speed <= SPEED_TAIL; speed += SPEED_INTERVAL) {
            //scan all the speed
            ArrayList<Vehicle> listPerSpeed;

            for (Vehicle vel : vels) {
                //1. list to save the vehicles with the same speed
                //chcek the speed of the vehicle
                if (CalibrationPntsValidator.isValideSpeed(vel.getSpeed(), speed)){
                    //Get listPerSpeed from organizedPerSpeed if exists
                    //else create the listPerSpeed and add to organizedPerSpeed
                    if (!organizedPerSpeed.containsKey(speed)) {
                        listPerSpeed = new ArrayList<>();
                        organizedPerSpeed.put(speed, listPerSpeed);
                    } else {
                        listPerSpeed = organizedPerSpeed.get(speed);
                    }
                    //add the vehicle to this speed list
                    listPerSpeed.add(vel);
                }
            }
        }

        //get temperature group
        for (int speed : organizedPerSpeed.keySet()) {
            HashMap<Integer, ArrayList<Vehicle>> vehiclesWithTempGroup =
                    CalibrationPntsValidator.getGroupTemp(organizedPerSpeed.get(speed));
            organizedVehicles.put(speed, vehiclesWithTempGroup);
        }

        return organizedVehicles;
    }


    //===========================================//
    //  Generate the calibration factor points   //
    //    for all the speed                      //
    //===========================================//
    // From above calibrationVehicles, generate calibrationPts,
    // this is a temporary which is the processed calibration data
    // Return value: HashMap<Speed, HashMap <Temperature,Factor> calibrationPts;
    private HashMap<Integer, HashMap<Integer, Double>>
        generateCalibrationFactor(int sensorNo){

        HashMap<Integer, HashMap<Integer, Double>> retVal = new HashMap<>();

        for(int speed: organizedVehicles.keySet())
        {
            HashMap<Integer, Double> caliFactor4Speed =
                    generateCalibrationFactor(sensorNo,
                        organizedVehicles.get(speed));
            retVal.put(speed, caliFactor4Speed);
        }
        return retVal;
    }

    //===========================================//
    //  Generate the calibration factor points   //
    //    for a certain speed                    //
    //===========================================//
    // From above calibrationVehicles, generate calibrationPts,
    // this is a temporary which is the processed calibration data
    // Return value: HashMap <Integer,Double> calibrationPts;
    //              Temperature___|     |_______calibration factor
    private HashMap<Integer, Double>
        generateCalibrationFactor(int sensorNo,
                                  HashMap<Integer, ArrayList<Vehicle>> vehicles) {

        HashMap<Integer, Double> calibrationPts = new HashMap<>();
        // get the measurement of some temperature
        for (int temp : vehicles.keySet()) {
            double sum = 0;
            double sz = vehicles.get(temp).size();
            for (Vehicle vels_temp : vehicles.get(temp)) {
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

    //===========================================//
    //  Generate the calibration tables          //
    //    for all the speed                      //
    //===========================================//
    // generate calibration table for sensor of each speed using
    // calibration point data.
    private HashMap<Integer, Integer[]>
        generateTable(HashMap<Integer,HashMap<Integer, Double>> caliPts) {
        //HashMap<Speed, table[]>
        HashMap<Integer, Integer[]> table = new HashMap<>();
       //scan in certain speed
       for(int speed: caliPts.keySet()) {

            if(true){
                //TODO: Check the speed contains engough calibration pnts or not
                //CalibrationPntsValidator.isValidCalibrationPnts(caliPts.get(speed))){
                ArrayList<Integer> sortedKeySet = new ArrayList<Integer>(caliPts.get(speed).keySet());
                Collections.sort(sortedKeySet);
                Integer[] table_speed = generateTable(caliPts.get(speed), sortedKeySet);
                table.put(speed, table_speed);
            }
       }
        // just copy from the coloest pnts
        ArrayList<Integer> sortedKeySet = new ArrayList<Integer>(caliPts.keySet());
        Collections.sort(sortedKeySet);
        for(int speed: CalibrationPntsValidator.speedList){
            if(!table.containsKey(speed)){
                Integer[] table_speed = table.get(getCloestInt(speed, sortedKeySet));
                table.put(speed,table_speed);
            }
        }
        return table;
    }


    private int getCloestInt(int val, ArrayList<Integer> sortedValues){

        int len = sortedValues.size();
        if(val <= sortedValues.get(0)) return sortedValues.get(0);
        if(val >= sortedValues.get(len-1)) return sortedValues.get(len-1);

        for(int cmpVal: sortedValues){
            if(cmpVal >= val) return cmpVal;
        }
        return sortedValues.get(len-1);
    }
    //===========================================//
    //  Generate the calibration tables          //
    //    for a certain speed                    //
    //===========================================//
    private Integer[] generateTable(
            HashMap<Integer, Double> caliPts,
            ArrayList<Integer> sortedKeySet){

        Integer[] table_speed = new Integer[100];

        for (int temp = -19; temp <= 80; temp++) {
            int idx = (temp + 19);
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
            table_speed[idx] = (int) (factor * MULTIPLY_FACTOR_TABLE);
        }
        return table_speed;
    }

    //=======================================//
    //  Write into a file the generated      //
    //  calibration tables for all the speed //
    //=======================================//
    //Write all the tables for calibration into a file as a backup.
    private void writeTable2File(HashMap<Integer,
            HashMap<Integer, Integer[]>> tables) {
        //write the table to file
        String fname = System.getProperty("user.dir");
        FileWriter fwr = null;
        try {
            fwr = new FileWriter(fname+"\\table.log", true);
            for(int sensorNo: tables.keySet()){
                writeTable2File(fwr, sensorNo, tables.get(sensorNo));
            }
            fwr.flush();
            fwr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //=========================================//
    //  Write into a file the generated        //
    //  calibration tables for a certain sensor//
    //=========================================//
    //Write the calibration table of a certain sensor to file as a backup.
    private void writeTable2File(
            FileWriter fwr,
            int sensorNo,
            HashMap<Integer, Integer[]> table){
        try {
            fwr.write(";===========Sensor = "+sensorNo+"============\n");
            for(Integer speedKey:table.keySet())
            {
                writeTable2File(fwr, speedKey, table.get(speedKey));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //=========================================//
    //  Write into a file the generated        //
    //  calibration tables for a certain speed //
    //=========================================//
    //write a specific speed calibration table to a file
    private void writeTable2File(FileWriter fwr,
                                int speedKey,
                                 Integer[] table) throws IOException {
        fwr.write(";===========Speed = " + speedKey + "============\n");
        int cnt = 0;
        for (Integer val : table) {
            cnt++;
            fwr.write("" + val.toString() + ", ");
            //System.out.println(val);
            if (cnt == 10) {
                fwr.write('\n');
                cnt = 0;
            }
        }
    }

    //=======================================//
    //  get the index of a temperature       //
    //=======================================//
    // get temperature index in the calibrated vehicle
    // function used by generate table
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
}

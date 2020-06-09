package vehiclepanel.Calibrator;
// Group the calibration vehicle data by following rules:
// 1. Temperature within [tempLimitNeg, tempLimitPos] degrees (tempLimitNeg < 0 and
//    tempLimitPos > 0) and
// 2. Speed within (speedLimitNeg,speedLimitPos) km/h
//    any points that meet the specifications above is grouped as one calibration point

// Validate the calibration points according to the following rules:
// 1. For any group (speed, temperature), the members of this group should have deviation
//    between [DEV_NEG, DEV_POS]%
// 2. All the group (speed, temperature) has at least MIN_PNTS_PER_GROUP points
// 3. At least one given speed, have at least MIN_TEMP_PNTS_PER_SPEED points
// 3. One (temprature), contains the calibration data for speed from 40, 50, 60, 70, 80
// 4. All the other temperature contains the calibration data only at one speed.

import vehiclepanel.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class CalibrationPntsValidator {
    //temperature unit: degree
    //speed unit: km/h
    final static double tempLimitPos = 1.0;
    final static double tempLimitNeg = -1.0;
    final static double speedLimitPos = 3.0;
    final static double speedLimitNeg = -3.0;

    final static int MIN_TEMP_PNTS_PER_SPEED = 5;
    final static int speedList[] = {40,50,60,70,80};

    // speed ___      Temperature
    //         |               |
    private HashMap<Integer, HashMap<Integer, ArrayList<Vehicle>>> calibrationVehicles;

    public CalibrationPntsValidator(){
        calibrationVehicles = new HashMap<>();
    }


    //try to validate the data and try to group the calibration points
    //according to the rules
    public boolean groupCalibrationPnts(ArrayList<Vehicle> vehicles)
    {
        HashMap<Integer, ArrayList<Vehicle>> vehiclesGroupedbySpeed = new HashMap<>();
        for(int speed: speedList){
            vehiclesGroupedbySpeed.put(speed, new ArrayList<Vehicle>());
        }
        //group the speed first
        for(Integer speed: vehiclesGroupedbySpeed.keySet()){
            for(Vehicle v: vehicles)
            {
                if(isValideSpeed(v.getSpeed(),speed))
                    vehiclesGroupedbySpeed.get(speed).add(v);
            }
        }
        // group the temperature
        for(Integer speed: vehiclesGroupedbySpeed.keySet()){
            ArrayList<Vehicle> list = vehiclesGroupedbySpeed.get(speed);

            int[] numberOfPnts = new int[100];
            //check from -19 degree to 80 degree the temperature
            for(int temp = -19; temp <= 80; temp++){
                //a counter for temperature
                int cnt = 0;
                for(Vehicle v: list){
                    if(isValideTemperature(v.getTemperature(), temp)) cnt++;
                }
                numberOfPnts[temp+19] = cnt;
            }
            //analise number of pnts in each temperature

        }



        //0. check if all the speed is valid or not, and check if the temperature is valid or not

        //1. check for all the speed if there is a common temperature


        //2. check if there has one speed that has at least MIN_TEMP_PNTS_PER_SPEED temperature pnts


        //3. check if the other speed has only 1 temperature pnts

        //4. for any given speed and temperature, there is at least MIN_PNTS_PER_GROUP pnts

        //5. for any given speed and temperature, the deviation of any given points should be between
        //   [DEV_NEG, DEV_POS]%


        return true;
    }


    //function returns the common temperature in all the possible speed
    private int[] getTheCommonTemperature()
    {
        Set<Integer> speeds = calibrationVehicles.keySet();
        for(Integer speed: speeds){
            //speed
        }

        return null;
    }


    //get the valide temperature group from int counter array ( for each temperature the counter
    //array saves how many point is valid for certain temperature
    private ArrayList<Integer> getTemperatureGroup(int[] cnterPnts)
    {

    }


    //function to get from all the vehicles the temperature and speed
    private ArrayList<Vehicle> getVehicles(int temperature, int speed, ArrayList<Vehicle> vehicles)
    {
        ArrayList<Vehicle> newArrayList = new ArrayList<>();

        for(Vehicle vehicle: vehicles){
            if((vehicle.getTemperature() == temperature)
                && (vehicle.getSpeed() == speed)))
                newArrayList.add(vehicle);
        }
    }


    //check if the speed is valid
    private boolean isValideSpeed(double speed2Verify, int speed){
        boolean retVal = (speed2Verify <= (speed + speedLimitPos));
        retVal &= (speed2Verify >= speed + speedLimitNeg);
        return retVal;
    }

    //check if the speed is valid
    private boolean isValideTemperature(double temp2Verify, int temp){
        boolean retVal = (temp2Verify <= (temp + tempLimitPos));
        retVal &= (temp2Verify >= temp + tempLimitNeg);
        return retVal;
    }


    //check if the temperature is valid



    //function add one point to the already existed hashmap
    private boolean addPnt2CalibrationDB(Vehicle vehicle)
    {

    }

}

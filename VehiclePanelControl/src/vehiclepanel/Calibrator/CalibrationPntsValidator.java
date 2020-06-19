 package vehiclepanel.Calibrator;
// // Group the calibration vehicle data by following rules:
// // 1. Temperature within [tempLimitNeg, tempLimitPos] degrees (tempLimitNeg < 0 and
// //    tempLimitPos > 0) and
// // 2. Speed within (speedLimitNeg,speedLimitPos) km/h
// //    any points that meet the specifications above is grouped as one calibration point

// // Validate the calibration points according to the following rules:
// // 1. For any group (speed, temperature), the members of this group should have deviation
// //    between [DEV_NEG, DEV_POS]%
// // 2. All the group (speed, temperature) has at least MIN_PNTS_PER_GROUP points
// // 3. At least one given speed, have at least MIN_TEMP_PNTS_PER_SPEED points
// // 3. One (temprature), contains the calibration data for speed from 40, 50, 60, 70, 80
// // 4. All the other temperature contains the calibration data only at one speed.

// import vehiclepanel.Vehicle;

// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.Set;

 import vehiclepanel.Vehicle;

 import java.util.ArrayList;
 import java.util.HashMap;

 public class CalibrationPntsValidator {
     //temperature unit: degree
     //speed unit: km/h
     // defines the valid limits [XXLimitNeg, XXLimitPos]
     final static double tempLimitPos = 1.0;
     final static double tempLimitNeg = -1.0;
     final static double distTempLim = tempLimitPos-tempLimitNeg;
     final static double speedLimitPos = 3.0;
     final static double speedLimitNeg = -3.0;

     //coefficient of variation limit
     final static double cvLim = 0.05;

     //At least one speed should have at least
     //MIN_TEMP_PNTS_PER_SPEED different temperature data
     final static int MIN_TEMP_PNTS_PER_SPEED = 1;
     //For any given temperature and speed, MIN_PNTS_PER_GROUP
     //defines the minimum points needed for calibration
     final static int MIN_PNTS_PER_GROUP = 3;
     //Defines the calibration speed predefined by calibration process
     public final static int speedList[] = {40,50,60,70,80};

     static String warningMsg = new String();

//     // speed ___      Temperature
//     //         |               |
//     private HashMap<Integer, HashMap<Integer, ArrayList<Vehicle>>> calibrationVehicles;

     public CalibrationPntsValidator(){

     }


//     //try to validate the data and try to group the calibration points
//     //according to the rules
//     public boolean groupCalibrationPnts(ArrayList<Vehicle> vehicles)
//     {
//         HashMap<Integer, ArrayList<Vehicle>> vehiclesGroupedbySpeed = new HashMap<>();
//         for(int speed: speedList){
//             vehiclesGroupedbySpeed.put(speed, new ArrayList<Vehicle>());
//         }
//         //group the speed first
//         for(Integer speed: vehiclesGroupedbySpeed.keySet()){
//             for(Vehicle v: vehicles)
//             {
//                 if(isValideSpeed(v.getSpeed(),speed))
//                     vehiclesGroupedbySpeed.get(speed).add(v);
//             }
//         }
//         // group the temperature
//         for(Integer speed: vehiclesGroupedbySpeed.keySet()){
//             ArrayList<Vehicle> list = vehiclesGroupedbySpeed.get(speed);

//             int[] numberOfPnts = new int[100];
//             //check from -19 degree to 80 degree the temperature
//             for(int temp = -19; temp <= 80; temp++){
//                 //a counter for temperature
//                 int cnt = 0;
//                 for(Vehicle v: list){
//                     if(isValideTemperature(v.getTemperature(), temp)) cnt++;
//                 }
//                 numberOfPnts[temp+19] = cnt;
//             }
//             //analise number of pnts in each temperature
//             ArrayList<Integer> tempGroup = getTemperatureGroup(numberOfPnts);


//         }



//         //0. check if all the speed is valid or not, and check if the temperature is valid or not

//         //1. check for all the speed if there is a common temperature


//         //2. check if there has one speed that has at least MIN_TEMP_PNTS_PER_SPEED temperature pnts


//         //3. check if the other speed has only 1 temperature pnts

//         //4. for any given speed and temperature, there is at least MIN_PNTS_PER_GROUP pnts

//         //5. for any given speed and temperature, the deviation of any given points should be between
//         //   [DEV_NEG, DEV_POS]%


//         return true;
//     }


//     //function returns the common temperature in all the possible speed
//     private int[] getTheCommonTemperature()
//     {
//         Set<Integer> speeds = calibrationVehicles.keySet();
//         for(Integer speed: speeds){
//             //speed
//         }

//         return null;
//     }


//     //get the valide temperature group from int counter array ( for each temperature the counter
//     //array saves how many point is valid for certain temperature
//     private ArrayList<Integer> getTemperatureGroup(int[] cnterPnts)
//     {
//         ArrayList<Integer> temp4Group = new ArrayList<Integer>();

//         //find if there is any cnter that has MIN_PNTS_PER_GROUP points
//         for(int idx = 0; idx < cnterPnts.length; idx++){
//             if(cnterPnts[idx] >= MIN_PNTS_PER_GROUP) temp4Group.add(idx-19);
//         }

//         if(temp4Group.size() == 0) return null; //no valid data found
//         //If has only one temperature point, then it must be a valid one
//         if(temp4Group.size()  == 1) return temp4Group;

//         //If  there is more than 1 pnts, then we need to check the if there 
//         // is any very close temperature pnts
//         ArrayList<Integer> stayList = new ArrayList<>();
//         for(int idx = 0; idx < temp4Group.size()-1; idx++){
//             //compare the temperature value of the next pnt
//             double thisPnt = temp4Group.get(idx); 
//             double nextPnt = temp4Group.get(idx+1);
//             double maxPnt = -100;
//             int thisCnterVal = cnterPnts[(int)Math.round(thisPnt)+19];
//             int nextCnterVal = cnterPnts[(int)Math.round(thisPnt)+20];

//             if(isValideTemperature(thisPnt,nextPnt)){
//                 //duplicate temperature pnts, should remove the one that
//                 //has least points
//                 if(thisCnterVal <= nextCnterVal){
//                     maxPnt = 
//                 } else {
//                     stayList.add(idx);
//                 }
//             } else {

//             }
//         }
        
//         return temp4Group;
//     }


//     //function to get from all the vehicles the temperature and speed
//     private ArrayList<Vehicle> getVehicles(int temperature, int speed, ArrayList<Vehicle> vehicles)
//     {
//         ArrayList<Vehicle> newArrayList = new ArrayList<>();

//         for(Vehicle vehicle: vehicles){
//             if((vehicle.getTemperature() == temperature)
//                 && (vehicle.getSpeed() == speed)))
//                 newArrayList.add(vehicle);
//         }
//     }
     //Check if the vehicles is valid
    public static boolean isValidData(
        HashMap<Integer, HashMap<Integer, ArrayList<Vehicle>>> vehicles){

        int maxNoOfPntsPerSpeed = 0;
        for(int speed: vehicles.keySet())
        {
            //get the maximum point number per speed
            if(maxNoOfPntsPerSpeed < vehicles.get(speed).size()){
                maxNoOfPntsPerSpeed = vehicles.get(speed).size();
            }

            for(int temp: vehicles.get(speed).keySet()){
                // 2. All the group (speed, temperature) has at least MIN_PNTS_PER_GROUP points
                ArrayList<Vehicle> vels = vehicles.get(speed).get(temp);
                if(vels.size() < MIN_PNTS_PER_GROUP) {
                    warningMsg = "Not enough calibration Points for "
                            +speed+" km/h @ temperature "+ temp +" degee.";
                    return false;
                }
                // // 1. For any group (speed, temperature), the members of this group should have
                // relative standard deviation < cvLim
                if(!(getRSD(vels) < cvLim)){
                    warningMsg = "RSD value too high for  "
                            +speed+" km/h @ temperature "+ temp +" degee.";
                    return false;
                }
            }
        }

        //check if at least in one speed has equal or more than MIN_TEMP_PNTS_PER_SPEED temperature point
        if(maxNoOfPntsPerSpeed < MIN_TEMP_PNTS_PER_SPEED){
            warningMsg = "Not enough temperature points per speed.";
            return false;
        }
        return true;
    }

    private static double getRSD(ArrayList<Vehicle> vels){
         double meanVal = getMeanWtVal(vels);
         double sum = 0;
         for(Vehicle vel: vels){
             sum += Math.pow(vel.getTotalWeight() - meanVal,2);
         }
         return Math.sqrt(sum/vels.size())/meanVal;
    }

     private static double getMeanWtVal(ArrayList<Vehicle> vels)
     {
         double sum = 0;
         for(Vehicle vel: vels){
             sum+= (double) vel.getTotalWeight();
         }
         return sum/vels.size();
     }



     //check if the speed is valid
     public static boolean isValideSpeed(double speed2Verify, int speed){
         boolean retVal = (speed2Verify <= (speed + speedLimitPos));
         retVal &= (speed2Verify >= speed + speedLimitNeg);
         return retVal;
     }

     //get the speed in the list if it is a valid speed
     public static Integer getSpeedInList(double speed2Verify){
         int speedInList = (int)(Math.round(speed2Verify/10))*10;
         if(isValideSpeed(speed2Verify, speedInList)){
             return speedInList;
         }
         return null;
     }

     //get the distance in temperature for two vehicles
     private static double getTempDist(Vehicle vel1, Vehicle vel2){
         return Math.abs((double)(vel1.getTemperature()-vel2.getTemperature()));
     }

    private static boolean isSameGroupTemp(Vehicle vel1, Vehicle vel2){
         return (getTempDist(vel1,vel2) <= distTempLim);
    }

    //ret: HashMap<GroupNumber,ArrayList<Vehicle>>
    public static HashMap<Integer,ArrayList<Vehicle>> getGroupTemp(ArrayList<Vehicle> vels){

         ArrayList<Vehicle> notGroupedVels = new ArrayList<>(vels);

         HashMap<Integer,ArrayList<Vehicle>> groupedVehicle = new HashMap<>();
         int idx = 0;
         for(int i = 0; i < vels.size(); i++){
             //scan all the vehicles
             //for vehicle i, we need to check all the
             Vehicle vel = vels.get(i);
             if(notGroupedVels.contains(vel)) {
                 //generate a new group
                 //arraylist to save grouped vehicles
                 ArrayList<Vehicle> groupedVelsList = new ArrayList<>();
                 groupedVehicle.put(idx++, groupedVelsList);

                 groupedVelsList.add(vel);
                 notGroupedVels.remove(vel);
                 //vehicles from i+1 -> ->
                 for (int j = i + 1; j < vels.size(); j++) {
                     Vehicle vel2Cmp = vels.get(j);
                     if (notGroupedVels.contains(vel2Cmp)) {
                         if (isSameGroupTemp(vel, vel2Cmp)) {
                             groupedVelsList.add(vel2Cmp);
                             notGroupedVels.remove(vel2Cmp);
                         }
                     }
                 }
             }
         }

        //substitute the index value by mean value of temperature
        HashMap<Integer, ArrayList<Vehicle>> newGroupedVehicle = new HashMap<>();
        for(int groupNo: groupedVehicle.keySet()){
            ArrayList<Vehicle> vehicles = groupedVehicle.get(groupNo);
            int meanVal = (int)Math.round(getMeanTemperatureVal(vehicles));
            if(!newGroupedVehicle.containsKey(meanVal)){
                newGroupedVehicle.put(meanVal,vehicles);
            } else {
                newGroupedVehicle.get(meanVal).addAll(vehicles);
            }
        }
         return newGroupedVehicle;
    }


    private static double getMeanTemperatureVal(ArrayList<Vehicle> vels)
    {
        double sum = 0;
        for(Vehicle vel: vels){
            sum+= (double) vel.getTemperature();
        }
        return sum/vels.size();
    }

    public static boolean isValidCalibrationPnts(HashMap<Integer, Double> caliPnts){
         return (caliPnts.size() >= MIN_TEMP_PNTS_PER_SPEED);
    }

//     //check if the speed is valid
//     private boolean isValideTemperature(double temp2Verify, double temp){
//         boolean retVal = (temp2Verify <= (temp + tempLimitPos));
//         retVal &= (temp2Verify >= temp + tempLimitNeg);
//         return retVal;
//     }


//     //check if the temperature is valid



//     //function add one point to the already existed hashmap
//     private boolean addPnt2CalibrationDB(Vehicle vehicle)
//     {

//     }

 }

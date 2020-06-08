package vehiclepanel.Calibrator;
// Group the calibration vehicle data by following rules:
// 1. Temperature within [tempLimitNeg, tempLimitPos] degrees (tempLimitNeg < 0 and
//    tempLimitPos > 0) and
// 2. Speed within (speedLimitNeg,speedLimitPos) km/h
//    any points that meet the specfications above is grouped as one calibration point

// Validate the calibration points according to the following rules
// All the group has at least MIN_PNTS_PER_GROUP points
// At least MIN_


public class CalibrationPntsValidator {
    //temperature unit: degree
    //speed unit: km/h
    final static double tempLimitPos = 3.0;
    final static double tempLimitNeg = -3.0;
    final static double speedLimitPos = -3.0;
    final static double speedLimitNeg = -3.0;



}

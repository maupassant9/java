package vehiclepanel.Calibrator;

public class CalibrationException extends Exception {
    public final static int CRITICAL_ERR = 3;
    public final static int NORMAL_ERR = 2;
    public final static int WARNING = 1;


    private String info;
    private int errorLevel;

    public CalibrationException(String info, int errorLevel){
        this.info = info;
        this.errorLevel = errorLevel;
    }

    public CalibrationException(int errorLevel){
        this("Calibration points error", errorLevel);
    }

    public CalibrationException(){
        this(WARNING);
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setErrorLevel(int errorLevel) {
        this.errorLevel = errorLevel;
    }

    public String getInfo() {
        return info;
    }

    public int getErrorLevel() {
        return errorLevel;
    }
}

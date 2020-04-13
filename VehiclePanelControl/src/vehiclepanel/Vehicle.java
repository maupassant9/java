package vehiclepanel;

import java.util.ArrayList;
import java.util.HashMap;

public class Vehicle {
    private final ArrayList<Double> distEntreEixos;
    private final double speedKmh;
    private final ArrayList<Double> wtPerEixoKg;
    private final double totalWtKg;
    //this get the each eixo weight por each sensor
    //   sensor No =1,2,3...__        ___a list of each weight of eixo from 1 to n
    //                        |      |
    private final HashMap<Integer,ArrayList<Double>> eixoWtPerSensor;
    private String id;

    private int faixa;

    //well this is not really a property of a vehicle
    //but i have no where to put it.
    private int temperature; 

    private Double calibrateWt = null;

    public double getSpeed() {
        return speedKmh;
    }

    public void setName(String name)
    {
        this.id = name;
    }

    public String getName()
    {
        return this.id;
    }

    public ArrayList<Double> getWtPerEixo() {
        return wtPerEixoKg;
    }

    public double getTotalWeight() {
        return totalWtKg;
    }

    public ArrayList<Double> getDistEntreEixos(){
        return distEntreEixos;
    }

    public Vehicle(String name, 
        ArrayList<Double> distEntreEixos,
        double speed,
        ArrayList<Double> eixoWt,
        double totalWt,
        int temp) throws Exception {
        this(name,distEntreEixos,speed,eixoWt,totalWt,temp,1);
    }

    public Vehicle(String name, 
        ArrayList<Double> distEntreEixos,
        double speed,
        ArrayList<Double> eixoWt,
        double totalWt,
        int temp,
        int faixa) throws Exception {
            this(name,distEntreEixos,speed,eixoWt,totalWt,temp,
                    faixa,new HashMap<Integer,ArrayList<Double>>());
    }


    public Vehicle(String name, 
                   ArrayList<Double> distEntreEixos,
                   double speed,
                   ArrayList<Double> eixoWt,
                   double totalWt,
                   int temp,
                   int faixa,
                   HashMap<Integer,ArrayList<Double>> eixoWtPerSensor) throws Exception {
        if(distEntreEixos.size() != eixoWt.size()-1){
            throw new Exception();
        }
        this.distEntreEixos = distEntreEixos;
        this.speedKmh = speed;
        wtPerEixoKg = eixoWt;
        totalWtKg = totalWt;
        this.id = name;
        temperature = temp;
        this.faixa = faixa;
        this.eixoWtPerSensor = eixoWtPerSensor;
    }

    

    public Vehicle(String name, 
                   ArrayList<Double> distEntreEixos,
                   double speed,
                   ArrayList<Double> eixoWt,
                   double totalWt) throws Exception {
            this(name,distEntreEixos,speed,eixoWt,totalWt,0);
    }

    public int getEixoNumber(){
        return wtPerEixoKg.size();
    }

    @Override
    public String toString(){
        return id+": " +speedKmh + " Km/h " + (double)(totalWtKg/1000.0) + "T " + getEixoNumber() + " eixos";
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getFaixa() {
        return faixa;
    }

    public void setFaixa(int faixa) {
        this.faixa = faixa;
    }

    public double getTotalWeightPerSensor(int sensorNo)
    {
        if(eixoWtPerSensor == null) return 0;
        return sum(eixoWtPerSensor.get(sensorNo));

    }


    public double sum(ArrayList<Double> values)
    {
        double sum = 0;
        for(Double value: values)
            sum+=value;
        return sum;
    }

    public static Vehicle parseVehicle(String str) throws Exception
    {
        String[] datas = str.split(",");
        int idx = 0;
        double caliWt = Double.parseDouble(datas[idx++]);
        int temperature = Integer.parseInt(datas[idx++]);
        int faixaVel = Integer.parseInt(datas[idx++]);
        int idVel = Integer.parseInt(datas[idx++]);
        double speed = Double.parseDouble(datas[idx++]);
        double totalWt = Double.parseDouble(datas[idx++]);
        int eixoNo = Integer.parseInt(datas[idx++]);
        

        //get sensor 1 weight per eixo
        HashMap<Integer,ArrayList<Double>> eixoWtsForAllSensors = new HashMap<>();
        ArrayList<Double> eixoWtSensor = new ArrayList<>();
        int limit = eixoNo + idx;
        for(; idx < limit; idx++){
            eixoWtSensor.add(Double.parseDouble(datas[idx]));
        }
        eixoWtsForAllSensors.put(1, eixoWtSensor);
        //get sensor 2 weight per eixo
        eixoWtSensor = new ArrayList<>();
        limit = idx + eixoNo;
        for(; idx < limit; idx++){
            eixoWtSensor.add(Double.parseDouble(datas[idx]));
        }
        eixoWtsForAllSensors.put(2, eixoWtSensor);
        //get sensor 3 weight per eixo
        limit = idx + eixoNo;
        eixoWtSensor = new ArrayList<>();
        for(; idx < limit; idx++){
            eixoWtSensor.add(Double.parseDouble(datas[idx]));
        }
        eixoWtsForAllSensors.put(3, eixoWtSensor);

        ArrayList<Double> wtPerEixo = new ArrayList<>();
        limit = idx + eixoNo;
        for(; idx < limit; idx++){
            wtPerEixo.add(Double.parseDouble(datas[idx]));
        }

        ArrayList<Double> dist = new ArrayList<>();
        limit = idx + eixoNo - 1;
        for(; idx < limit; idx++){
            dist.add(Double.parseDouble(datas[idx]));
        }
        Vehicle vel = new Vehicle(""+idVel, dist, 
            speed, wtPerEixo, totalWt, 
            temperature, faixaVel, 
            eixoWtsForAllSensors);
        vel.setCalibrateWt(caliWt);
        return vel;
    }


    public void setCalibrateWt(double val)
    {
        calibrateWt = val;
    }

    public Double getCalibrateWt()
    {
        return calibrateWt;
    }

    public String toStringComplete()
    {
        String str = "";
        str += (calibrateWt + ",");
        str += (temperature +",");
        str += faixa +",";
        str += id + ",";
        str += speedKmh + ",";
        str += totalWtKg + ",";
        str += wtPerEixoKg.size()+",";
        

        //get sensor 1 weight per eixo
        for(Double no: eixoWtPerSensor.get(1)){
            str += no + ",";
        }
        //get sensor 2 weight per eixo
        for(Double no: eixoWtPerSensor.get(2)){
            str += no + ",";
        }
        //get sensor 3 weight per eixo
        for(Double no: eixoWtPerSensor.get(3)){
            str += no + ",";
        }

        for(Double no: wtPerEixoKg){
            str += no + ",";
        }

        for(Double no: distEntreEixos){
            str += no + ",";
        }

        return str.substring(0,str.length()-1);
    }
}

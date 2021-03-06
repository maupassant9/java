package vehiclepanel.Calibrator;

import vehiclepanel.Vehicle;

public class VehicleFilter{

    private double speedKmhRangeMax;
    private double speedKmhRangeMin;
    private double pesoTotalMaxKg;
    private double pesoTotalMinKg;
    private int eixoNo;
    private int faixa;

    public double calibrateWeight;


    public VehicleFilter()
    {
        speedKmhRangeMin = 0;
        speedKmhRangeMax = Double.MAX_VALUE;

        pesoTotalMaxKg = Double.MAX_VALUE;
        pesoTotalMinKg = 0;

        eixoNo = 0; //pass all kind of vehicle
        faixa = 0; //pass all lane
    }


    public Vehicle filter(Vehicle vel){
        if(faixa != 0)
        {
            if(vel.getFaixa()!=faixa) return null;
        }
        
        //check for speed
        if(vel.getSpeed() > speedKmhRangeMax) return null;
        if(vel.getSpeed() < speedKmhRangeMin) return null;
        
        //check for total weight
        if(vel.getTotalWeight() > pesoTotalMaxKg) return null;
        if(vel.getTotalWeight() < pesoTotalMinKg) return null;

        
        //check for eixo no
        if(eixoNo == 0) return vel;
        if(vel.getEixoNumber() != eixoNo) return null;
        return vel;
    }

    public double getSpeedKmhRangeMax() {
        return speedKmhRangeMax;
    }

    public void setSpeedKmhRangeMax(double speedKmhRangeMax) {
        this.speedKmhRangeMax = speedKmhRangeMax;
    }

    public double getSpeedKmhRangeMin() {
        return speedKmhRangeMin;
    }

    public void setSpeedKmhRangeMin(double speedKmhRangeMin) {
        this.speedKmhRangeMin = speedKmhRangeMin;
    }

    public double getPesoTotalMaxKg() {
        return pesoTotalMaxKg;
    }

    public void setPesoTotalMaxKg(double pesoTotalMaxKg) {
        this.pesoTotalMaxKg = pesoTotalMaxKg;
    }

    public double getPesoTotalMinKg() {
        return pesoTotalMinKg;
    }

    public void setPesoTotalMinKg(double pesoTotalMinKg) {
        this.pesoTotalMinKg = pesoTotalMinKg;
    }

    public int getEixoNo() {
        return eixoNo;
    }

    public void setEixoNo(int eixoNo) {
        this.eixoNo = eixoNo;
    }

    public double getCalibrateWeight() {
        return calibrateWeight;
    }

    public void setCalibrateWeight(double calibrateWeight) {
        this.calibrateWeight = calibrateWeight;
    }

    public int getFaixa() {
        return faixa;
    }

    public void setFaixa(int faixa) {
        this.faixa = faixa;
    }

}
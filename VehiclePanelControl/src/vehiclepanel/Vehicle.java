package vehiclepanel;

import java.util.ArrayList;

public class Vehicle {
    private final ArrayList<Double> distEntreEixos;
    private final double speedKmh;
    private final ArrayList<Double> wtPerEixoKg;
    private final double totalWtKg;
    private String name;

    public double getSpeed() {
        return speedKmh;
    }

    public void setName(String name)
    {
        this.name = name;
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

    public Vehicle(ArrayList<Double> distEntreEixos,
                   double speed,
                   ArrayList<Double> eixoWt,
                   double totalWt) throws Exception {
        if(distEntreEixos.size() != eixoWt.size()-1){
            throw new Exception();
        }
        this.distEntreEixos = distEntreEixos;
        this.speedKmh = speed;
        wtPerEixoKg = eixoWt;
        totalWtKg = totalWt;

        name = "No.0";
    }


    public int getEixoNumber(){
        return wtPerEixoKg.size();
    }

    @Override
    public String toString(){
        return name+": " +speedKmh + " Km/h " + (double)(totalWtKg/1000.0) + "T " + getEixoNumber() + " eixos";
    }

}

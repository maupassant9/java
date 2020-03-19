package vehiclepanel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Vehicle {
    private final ArrayList<Double> distEntreEixos;
    private final double speedKmh;
    private final ArrayList<Double> wtPerEixoKg;
    private final double totalWtKg;

    public double getSpeed() {
        return speedKmh;
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
    }


    public int getEixoNumber(){
        return wtPerEixoKg.size();
    }

}

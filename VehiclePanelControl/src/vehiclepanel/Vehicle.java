package vehiclepanel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Vehicle {

    private final ArrayList<Double> distEntreEixos;

    public Vehicle(ArrayList<Double> distEntreEixos){

        this.distEntreEixos = distEntreEixos;
    }
    public Vehicle(){
        ArrayList<Double> dist = new ArrayList<>(Arrays.asList(2.0,1.0,3.0));
        this.distEntreEixos = dist;
    }

    public ArrayList<Double> getDistEntreEixos(){
        return distEntreEixos;
    }
}

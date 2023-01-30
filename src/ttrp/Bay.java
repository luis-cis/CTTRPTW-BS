package ttrp;

import java.util.ArrayList;

public class Bay extends Node {
    int capacity;
    ArrayList<Vehicle> Usage = new ArrayList<>();
    int flag;
    public int opening_cost; //in case using the bay represents a cost for the supplier
    
    /*
        Creates a bay
    */
    public Bay(int id, int[] tw, int capacidad)
    {
        this.id = id;
        this.TW = tw;
        this.capacity = capacidad;
    }
}


package ttrp;

import java.util.ArrayList;


public class Depot extends Node{
    public ArrayList<Vehicle> Vehicles = new ArrayList<>();
    
    /*
        Creates a depot
    */
    public Depot(int id, int[] tw)
    {
        this.id = id;
        this.TW = tw;
    }
}

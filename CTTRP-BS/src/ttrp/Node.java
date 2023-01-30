package ttrp;

import java.util.ArrayList;

public class Node {
    
    /*
        Root class for al the nodes within the graph
    */
    
    public int id, id_father = Integer.MAX_VALUE;
    public ArrayList<Edge> Edges = new ArrayList<>();
    public int[] TW;
    
}

package ttrp;

public class Edge {
    int cost;
    Depot destiny_D;
    Bay destiny_B;
    Client destiny_C;

    public Edge(int len)
    {
        this.cost = len;  
    }
    
}

package ttrp;

public class Client extends Node{
    int Servie_Time;
    int Boxes; // demand on the client
    public int Payment;
    public int Type;
    
    /*
        Creates a client
    */
    public Client(int id, int[] tw, int t, int c)
    {
        this.id = id;
        this.TW = tw;
        this.Servie_Time = t;
        this.Boxes = c;
    }
}

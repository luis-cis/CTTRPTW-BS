package ttrp;
/*
    Used to generate solutions for the CTTRPTW-BS
*/

public class MacroNodes {
    
   public int type, hour = 0;
   public Depot d;
   public Bay bh;
   public Bay bh1; // to signal a bay change (macronode 3)
   public Client cl;
   public Vehicle vh;
   public int time_window;
   
   public void Create_macronode_i(Depot dep, Bay Bh, int h, Vehicle vehicle)
   {
        this.type = 0;
        this.d = dep;
        this.bh = Bh;
        this.hour = h;
        this.vh = vehicle;
   }
   
   public void Create_macronode_1(Bay Bh, Client Cl)
   {
        this.type = 1;
        this.bh = Bh;
        this.cl = Cl;
   }
   
   public void Create_macronode_2(Bay Bh, Client cl, Bay Bh2)
   {
       this.type = 2;
       this.cl = cl;
       this.bh = Bh;
       this.bh1 = Bh2;
   }
    public void Create_macronode_F(Depot DF, int h, Vehicle vh, int tw)
   {
        this.type = 3;
        this.hour = h;
        this.d = DF;
        this.vh = vh;
        this.time_window = tw;
   }
}

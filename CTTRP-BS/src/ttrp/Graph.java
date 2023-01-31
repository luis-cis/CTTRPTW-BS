package ttrp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Graph extends Path{
    public ArrayList<Client> Clients = new ArrayList<>();
    public ArrayList<Bay> Bays = new ArrayList<>();
    public ArrayList<Depot> Depots = new ArrayList<>();
    String path =  Get_path;
    String path_orden = Get_orden;
    
    /*
        Creates a graph from the corresponding txt files
    */
    public void Creat_Graph()
    {
        try {
            Creat_Depots();
            Creat_Bays();
            Creat_Clients();
            
            Read_Edges_D();
            Read_Edges_B();    
            Read_Edges_M_C();
            
        } catch (IOException ex) {
            Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    /*
        Creates the corresponfing nodes to represent the time windows 
        input: a CTTRTPW graph
        output: an expanded version of the same CTTRPTW graph
    */
    public void Expand_Graph(Graph g)
    {
        try {
            this.Depots = Expand_D(g);
            this.Bays = Expand_B(g);
            this.Clients = Expand_C(g);
        } catch (IOException ex) {
            Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
        }
        Expand_Edges_D(g);
        Expand_Edges_B(g);
    }
    
    //========================================================== Node creation  ======================================================
    
    /*
        Creates node depots and adds their vehicle fleet from a txt file
    */
    public void Creat_Depots() throws FileNotFoundException, IOException
    {
        BufferedReader in = new BufferedReader(new FileReader(path + "Depot.txt"));
        int i, j, id;
        int[] tw;
        String[] aux;
           
        int num_dep = Integer.parseInt(in.readLine());
            
        for(i = 0; i < num_dep; i++)
        {
            id = Integer.parseInt(in.readLine());
            aux = in.readLine().split(","); 
            tw = new int[aux.length];
            for(j = 0; j < aux.length; j++)
            {
                tw[j] = Read_Tw(aux[j]);
            }
            Depots.add( new Depot(id, tw)); 
        }
        in.close();
        //================== Read vehicle information
        in = new BufferedReader(new FileReader(path + "Vehicles.txt"));
            
        Vehicle Vh;
        int n_v = Integer.parseInt(in.readLine()), k;
        for(i = 0; i < Depots.size(); i++)
        {
            for(j = 0; j < n_v; j++){
                aux = in.readLine().split(" ");
                for(k = 0; k < Integer.parseInt(aux[0]); k++)
                {
                   Vh = new Vehicle(); 
                   Vh.capacity = Integer.parseInt(aux[1]);
                   Vh.type = Integer.parseInt(aux[2]);
                   Vh.id = j;
                   Vh.hourly_cost = Integer.parseInt(aux[3]);
                   Depots.get(i).Vehicles.add(Vh);
                }
            }
        } 
        in.close();
    }
    
    /*
        Creates node bays from a txt file
    */
    public void Creat_Bays() throws FileNotFoundException, IOException
    {
        try (BufferedReader in = new BufferedReader(new FileReader(path + "Bays.txt"))) 
        {
            int i, j, cap;
            int[] tw;
            String[] aux;
            Bay bh;
            int num_bh = Integer.parseInt(in.readLine());
            for(i = 0; i < num_bh; i++)
            {
                aux = in.readLine().split(","); 
                tw = new int[aux.length];
                for(j = 0; j < aux.length; j=j+2)
                {
                    tw[j] = Read_Tw(aux[j]);
                    tw[j+1] = Read_Tw(aux[j+1]);
                }
                cap = Integer.parseInt(in.readLine());
                bh = new Bay(i+1, tw, cap);
                bh.opening_cost = Integer.parseInt(in.readLine());
                
                Bays.add(bh); 
            }
        }
    }
    
    /*
        Creates node clients from a txt file
    */
    public void Creat_Clients() throws FileNotFoundException, IOException
    {
        try (BufferedReader in = new BufferedReader(new FileReader(path + "Clients.txt"))) 
        {
            int i, j;
            int[] tw;
            String[] aux;
            int num_cl = Integer.parseInt(in.readLine());
            for(i = 0; i < num_cl; i++)
            {
                aux = in.readLine().split(",");
                tw = new int[aux.length];
                for(j = 0; j < aux.length; j++)
                {
                    tw[j] = Read_Tw(aux[j]);
                }
                Clients.add( new Client(i+1, tw, 0, 0));
            }
        }
    }
    
    //========================================================== Edges creation  ======================================================
    
    /*
        Creates the edges from the depot to the bays 
    */
    public void Read_Edges_D() throws FileNotFoundException, IOException
    {
        try (BufferedReader in = new BufferedReader(new FileReader(path + "Edges_M_Dep.txt"))) 
        {
            int i,j, len;
            Edge ar;
            String line;
            String[] line_sp;
            for(i = 0; i < Depots.size();i++)
            {
                line = in.readLine();
                line_sp = line.split(",");
                for(j = 0; j < line_sp.length;j++)
                {
                    len = Math.round(Float.parseFloat(line_sp[j]));
                    
                    if(len != 0)
                    {
                        ar = new Edge(len);
                        ar.destiny_B = Bays.get(j);
                        Depots.get(i).Edges.add(ar);
                    }
                }
            }
        }
    }
    
    /*
        Creates edges from bays to depots, bays, and clients
    */
    public void Read_Edges_B() throws FileNotFoundException, IOException
    {
        try (BufferedReader in = new BufferedReader(new FileReader(path + "Edges_M_Ba.txt"))) 
        {
            int i,j, len;
            Edge ar;
            String line;
            String[] line_sp;
            in.readLine();
            line = in.readLine();
            for(i = 0; i < Bays.size();i++)//============================= Edges to Depot
            {
                line_sp = line.split(",");
                for(j = 0; j < line_sp.length;j++)
                {
                    try{
                        len = Math.round(Float.parseFloat(line_sp[j]));
                    }catch(NumberFormatException ex){
                        len = Math.round(Integer.parseInt(line_sp[j]));
                    }
                    
                    if(len != 0)
                    {
                        ar = new Edge(len);
                        ar.destiny_D = Depots.get(j);
                        Bays.get(i).Edges.add(ar);
                    }
                }
                line = in.readLine();
            }
            //                      ====================================== Edges to bays
            line = in.readLine();
            for(i = 0; i < Bays.size();i++)
            {
                line_sp = line.split(",");
                for(j = 0; j < line_sp.length;j++)
                {
                    len = Math.round(Float.parseFloat(line_sp[j]));
                    
                    if(len != 0)
                    {
                        ar = new Edge(len);
                        ar.destiny_B = Bays.get(j);
                        Bays.get(i).Edges.add(ar);
                    }
                }
                line = in.readLine();
            }  
                    //===============================Edges to clients
             
            line = in.readLine();
            for(i = 0; i < Bays.size();i++)
            {
                line_sp = line.split(",");
                for(j = 0; j < line_sp.length;j++)
                {
                    len = Math.round(Float.parseFloat(line_sp[j]));
                    
                    if(len != 0)
                    {
                        ar = new Edge(len);
                        ar.destiny_C = Clients.get(j);
                        Bays.get(i).Edges.add(ar);
                    }
                }
                line = in.readLine();
            }
        }
    }
    
    /*
        Creates edges from clients to bays
    */
    public void Read_Edges_M_C() throws FileNotFoundException, IOException
    {
        BufferedReader in = new BufferedReader(new FileReader(path + "Edges_M_Cl.txt"));
        int i,j, len;
        Edge ar;
        String line;
        String[] line_sp;
        
        for(i = 0; i < Bays.size();i++)
        {
            line = in.readLine();
            line_sp = line.split(",");
            for(j = 0; j < line_sp.length;j++)
            {
                len = Math.round(Float.parseFloat(line_sp[j]));
                
                if(len != 0)
                {
                   ar = new Edge(len); 
                   ar.destiny_B = Bays.get(i);
                   Clients.get(j).Edges.add(ar);
                }
            }
        }
        in.close();
    }
    
    
          //========================================================== Graph expantion  ======================================================
    
          //========================================================== Node expantion  ======================================================
    /*
        Creates a depot node for each time window and an ending depot
        input: a CTTRTPW graph
        outpu: a list of depot nodes
    */
    public ArrayList<Depot> Expand_D(Graph g) throws FileNotFoundException, IOException
    {
        BufferedReader in = new BufferedReader(new FileReader(path + "Tw.txt"));
        int N_tw = Integer.parseInt(in.readLine()), i, j, k;
        
        int[] tw;
        String[] line = new String[2];
        Vehicle vh;
        Depot d;
        ArrayList<Depot> expan = new ArrayList<>(); 
        
        for(i = 0;i < g.Depots.size(); i++)
        {
            for(j = 0; j < N_tw; j++)
            {
                line = in.readLine().split(",");
                tw = new int[2];
                tw[0] = Read_Tw(line[0]);
                tw[1] = Read_Tw(line[1]);
                d = new Depot(j, tw); 
                d.id_father = g.Depots.get(i).id;
                for(k = 0; k < g.Depots.get(i).Vehicles.size(); k++)
                {
                    vh = new Vehicle();
                    vh.capacity = g.Depots.get(i).Vehicles.get(k).capacity;
                    vh.hourly_cost = g.Depots.get(i).Vehicles.get(k).hourly_cost;
                    vh.id = g.Depots.get(i).Vehicles.get(k).id;
                    vh.type = g.Depots.get(i).Vehicles.get(k).type;
                    d.Vehicles.add(vh);
                }
                expan.add(d);
            }
        }
       in.close();
        return expan;  
    }
        
    /*
        Creates a bay node for each time window present (if theres more than one) within each bay 
        input: a CTTRTPW graph
        outpu: a list of bay nodes
    */
    public ArrayList<Bay> Expand_B(Graph g)
    {
        int num_tw = 0, id = 0, i,j;
        int[] new_tw;
        ArrayList<Bay> expan = new ArrayList<>(); 
        Bay bh;
            
        for(i = 0; i < g.Bays.size(); i++)
        {
            id = 0;
            num_tw = g.Bays.get(i).TW.length;
            for(j = 0; j < num_tw; j = j + 2)
            {
                new_tw = new int[2];
                new_tw[0] = g.Bays.get(i).TW[j];
                new_tw[1] = g.Bays.get(i).TW[j+1];
                    
                bh = new Bay(id, new_tw, 2);
                bh.id_father = g.Bays.get(i).id;
                bh.opening_cost = g.Bays.get(i).opening_cost;
                expan.add(bh);
                    
                id++;
            } 
        }
        return expan;
    }
    
    /*
        Creates a client node for each time window present (if theres more than one) within each client 
        input: a CTTRTPW graph
        outpu: a list of client nodes
    */    
    public ArrayList<Client> Expand_C(Graph g)
    {
        int num_tw = 0, id, i ,j;
        int[] new_tw;
        Client nd;
        ArrayList<Client> expan = new ArrayList<>(); 
            
        for(i = 0; i < g.Clients.size(); i++)
        {           
            id = 0;
            num_tw = g.Clients.get(i).TW.length;
            for(j = 0; j < num_tw; j = j + 2)
            {
                new_tw = new int[2];
                new_tw[0] = g.Clients.get(i).TW[j]; 
                new_tw[1] = g.Clients.get(i).TW[j+1];
                nd = new Client(id, new_tw, g.Clients.get(i).Servie_Time, g.Clients.get(i).Boxes);
                nd.id_father = g.Clients.get(i).id;
                expan.add(nd);
                    
                id++;
            } 
        }
        return expan;
    }
    
        //========================================================== Edges expantion ======================================================
    
    /*
        Conects each depot node to the corresponding bay
        input: an expanded CTTRTPW graph
    */
    public void Expand_Edges_D(Graph g)
    {
        int wait, id, i,j, k, l;
        ArrayList<Edge> Edges_o = new ArrayList<>();
        ArrayList<Edge> Edges_e = new ArrayList<>();
        Edge aux;
        int[] tw_array = new int[4];
            
        for(i = 0; i < g.Depots.size(); i++)
        {
            Edges_o = g.Depots.get(i).Edges;
            for(j = 0; j < Edges_o.size(); j++)
            {
                 Edges_e = new ArrayList<>();

                for(k = 0; k < this.Depots.size(); k++)
                {
                    if(this.Depots.get(k).TW[1] != 1440)
                    {
                        id = Edges_o.get(j).destiny_B.id;
                        for(l = 0; l < this.Bays.size(); l++) 
                        {
                            if(id == this.Bays.get(l).id_father)
                            {
                                //================ Time windows verification ===============
                                tw_array[0] = this.Depots.get(k).TW[0]; tw_array[1] = this.Depots.get(k).TW[1];
                                tw_array[2] = this.Bays.get(l).TW[0]; tw_array[3] = this.Bays.get(l).TW[1];
                                if(Compare_tw(tw_array))
                                {
                                        wait = Math.abs(this.Bays.get(l).TW[0] - this.Depots.get(k).TW[0]);//valor absoluto
                                        if(wait <= 0)
                                            wait = Edges_o.get(j).cost;
                                        aux = new Edge(wait);
                                        aux.destiny_B = this.Bays.get(l);
                                        this.Depots.get(k).Edges.add(aux); 
                                }else{
                                    wait = 0;
                                }
                            } 
                        } 
                    }
                }             
            }  
        }

    }
       
     /*
        Conects each bay node to the corresponding depots, bays and clients
        input: an expanded CTTRTPW graph
    */   
    public void Expand_Edges_B(Graph g) 
    {
        int wait, id, i,j, k, l, id_father;
        ArrayList<Edge> Edges_o;
        ArrayList<Edge> Edges_e = new ArrayList<>();
        Edge aux; int[] tw_array = new int[4];

        for(i = 0; i < g.Bays.size(); i++)
        {
            Edges_o = g.Bays.get(i).Edges; 
            id_father = g.Bays.get(i).id; 

            for(j = 0; j < Edges_o.size(); j++)
            {

                if(Edges_o.get(j).destiny_D != null) 
                {
                    id = Edges_o.get(j).destiny_D.id;
                    for(k = 0; k < this.Bays.size(); k++) 
                    {
                        if(this.Bays.get(k).id_father == id_father)
                        {
                            for(l = 0; l < this.Depots.size(); l++) 
                                {
                                    if(id == this.Depots.get(l).id_father && this.Depots.get(l).TW[1] == 1440 ) 
                                    {
                                            wait = 0;

                                            aux = new Edge(Edges_o.get(j).cost + wait);
                                            aux.destiny_D = this.Depots.get(l);
                                            this.Bays.get(k).Edges.add(aux);
                                    }
                                }
                        }
                    }
                }
                if(Edges_o.get(j).destiny_B != null) 
                {
                    id = Edges_o.get(j).destiny_B.id;
                    for(k = 0; k < this.Bays.size(); k++) 
                    {
                        if(this.Bays.get(k).id_father == id_father) 
                        {
                            for(l = 0; l < this.Bays.size(); l++) 
                                {
                                     if(id == this.Bays.get(l).id_father ) 
                                    {
                                        tw_array[0] = this.Bays.get(k).TW[0] + Edges_o.get(j).cost; tw_array[1] = this.Bays.get(k).TW[1];
                                        tw_array[2] = this.Bays.get(l).TW[0]; tw_array[3] = this.Bays.get(l).TW[1];
                                        if(Compare_tw(tw_array))
                                        {
                                            aux = new Edge(Edges_o.get(j).cost);
                                            aux.destiny_B = this.Bays.get(l);
                                            this.Bays.get(k).Edges.add(aux);
                                        }
                                    }
                                }
                        }
                    }
                }
                if(Edges_o.get(j).destiny_C != null) 
                {
                    id = Edges_o.get(j).destiny_C.id; 
                     for(k = 0; k < this.Bays.size(); k++) 
                     {
                          if(this.Bays.get(k).id_father == id_father) 
                          {
                              for(l = 0; l < this.Clients.size(); l++) 
                              {
                                   if(id == this.Clients.get(l).id_father ) 
                                   {
                                        if(this.Bays.get(k).TW[0] <= this.Clients.get(l).TW[0] && this.Bays.get(k).TW[1] >= this.Clients.get(l).TW[1]) 
                                        {
                                            wait = 0;
                                            wait = wait + (Edges_o.get(j).cost * 2); 
                                            wait = wait + this.Clients.get(l).Servie_Time; 
                                        
                                            if(this.Clients.get(l).TW[0] + wait <= this.Bays.get(k).TW[1])
                                            {
                                                aux = new Edge(wait);
                                                aux.destiny_C = this.Clients.get(l); 
                                                this.Bays.get(k).Edges.add(aux);
                                                aux = new Edge(wait);
                                                aux.destiny_B = this.Bays.get(k);
                                                this.Clients.get(l).Edges.add(aux);
                                            }

                                        }
                                   }
                              }
                          }
                     }
                }
            }
        }
    }
        
    /*
        Read the order information (service time, number of boxes and payment) from a txt file
        output: a list of clients with the order information
    */
    public ArrayList<Client> Read_Order() throws FileNotFoundException, IOException
    {
        ArrayList<Client> order = new ArrayList<>();
        BufferedReader in = new BufferedReader(new FileReader(path + path_orden));
        
        int num_c = Integer.parseInt(in.readLine()), i, j, k, id;
        String[] aux = new String[2];
        
        for(i = 0; i < num_c; i++)
        {
            aux = in.readLine().split(" ");
            id = Integer.parseInt(aux[0]);
            for(j = 0; j < this.Clients.size(); j++)
            {
                if(id == this.Clients.get(j).id_father)
                {
                    this.Clients.get(j).Boxes = Integer.parseInt(aux[1]);
                    this.Clients.get(j).Servie_Time = Read_Tw(aux[2]);
                    order.add(this.Clients.get(j));
                }
            }
        }

        in.close();
        return order;
    }
    
    /*
        Read the order information (service time, number of boxes and payment) from a txt file
        output: a list of clients with the order information
    */
    public ArrayList<Client> Read_Order_Celaya() throws FileNotFoundException, IOException{
        ArrayList<Client> orden = new ArrayList<>();
        BufferedReader in = new BufferedReader(new FileReader(path + path_orden));
        
        
        int num_c = Integer.parseInt(in.readLine()), i, j, k, id;
        int[] tw;
        Client cl;
        String[] aux = new String[2];
        
        for(i = 0; i < num_c; i++)
        {
            aux = in.readLine().split(" ");
            id = Integer.parseInt(aux[0]);
            for(j = 0; j < this.Clients.size(); j++)
            {
                if(id == this.Clients.get(j).id_father)
                { 
                    tw = new int[2];
                    tw[0] = Integer.parseInt(aux[5]); tw[1] = Integer.parseInt(aux[6]);
                    if(tw[0] >= this.Clients.get(j).TW[0] && tw[1] <= this.Clients.get(j).TW[1] )
                    {
                        cl = new Client(0,null,0,0);
                        cl.id = this.Clients.get(j).id;
                        cl.id_father = this.Clients.get(j).id_father;
                        cl.Boxes = Integer.parseInt(aux[1]);
                        cl.Servie_Time = Read_Tw(aux[2]);
                        cl.Type  = Integer.parseInt(aux[4]);

                        cl.TW = tw;
                        orden.add(cl);  
                    }
                    
                }
            }
        }
     
        in.close();
        return orden;
    }
     
    /*
        Coverts a clock format time window to its correspondan in minutes. Example: 03:00 would be converted to 180 minutes
        input: a clock format time window
        output: timw window in minutes
    */
    public int Read_Tw(String tw)
    {
        int window_minutes;
        String[] split = tw.split(":");
        window_minutes = (Integer.parseInt(split[0]) * 60) + Integer.parseInt(split[1]) ;
            
        return window_minutes;
    }
    
    /*
        Determines if two time windows intersec
        input: two time windows 
        output: true if their intersec, false otherwise
    */
    public boolean Compare_tw(int[] tw)
    {
        boolean resp = true;
        int[] comp = new int[4];
                            
        comp[0] = tw[0] - tw[2];   //  + + + +  false
        comp[1] = tw[0] - tw[3];   // - - - - false
        comp[2] = tw[1] - tw[2];     
        comp[3] = tw[1] - tw[3];    
                                      
        if(comp[0] < 0 && comp[1] < 0 && comp[2] < 0 && comp[3] < 0 )
            resp = false;
        if(comp[0] > 0 && comp[1] > 0 && comp[2] > 0 && comp[3] > 0)
            resp = false;
        return resp;
    }
    
    /*
        Prints a graph in console
        input: graph
    */
    public void Print_graph(Graph g)
    {
        int i,j,k;
        Depot d;
        Bay b;
        Client c;
        Edge ac;
        Vehicle vh;
        
        
        System.out.print("=======Graph======\n");
        System.out.print("Number of depots"+g.Depots.size()+"\n");
        for(i = 0; i < g.Depots.size(); i++)
        {
            d = g.Depots.get(i);
            System.out.print("Depot "+ d.id);
            if(d.id_father != Integer.MAX_VALUE)
                System.out.print(" Father Depot "+ d.id_father);
            
            System.out.print("\nNumber of Time Windows "+ d.TW.length/2);
            for(j=0; j<d.TW.length;j=j+2)
            {
                System.out.print("Time Windows "+ j+1 + " "+ d.TW[j] +" - " + d.TW[j+1]+ "\n");
            }
            
            System.out.print("\nNumber of edges "+d.Edges.size()+"\n");
            for(j=0; j < d.Edges.size();j++)
            {
                ac = d.Edges.get(j);
                if( ac.destiny_D != null)
                {
                    System.out.print("Edge "+j+" to: Father Depot"+ ac.destiny_D.id_father +
                            " Child depot: "+ ac.destiny_D.id +" cost:"+ ac.cost+ "\n");
                }else if( ac.destiny_B != null)
                {
                    System.out.print("Edge "+j+" to: Father Bay"+ ac.destiny_B.id_father +
                            " Child bay: "+ ac.destiny_B.id +" cost:"+ ac.cost+ "\n");
                }else{
                    System.out.print("Edge "+j+" to: Father Client"+ ac.destiny_C.id_father +
                            " Child client: "+ ac.destiny_C.id +" cost:"+ ac.cost+ "\n");   
                }
            }
            
            System.out.print("Number of vehicles "+d.Vehicles.size()+"\n");
            for(j = 0; j < d.Vehicles.size(); j++)
            {
                vh = d.Vehicles.get(j);
                System.out.print("Vehicle "+j+" capacity: "+ vh.capacity +" \n");
            }
        }
        //===========================================================================================================
        System.out.print("\n\nNumber of Bays "+g.Bays.size()+"\n");
        for(i = 0; i < g.Bays.size(); i++)
        {
            b = g.Bays.get(i);
            System.out.print("Bay "+ b.id);
            if(b.id_father != Integer.MAX_VALUE)
                System.out.print(" Father Bay"+ b.id_father);
            
            System.out.print("Capacity "+b.capacity+"\n");
            System.out.print("Number of the Time Windows"+ b.TW.length/2);
            for(j=0; j<b.TW.length;j=j+2)
            {
                System.out.print("Time Window "+ j+1 + " "+ b.TW[j] +" - " + b.TW[j+1]+"\n");
            }
            
            System.out.print("\nNumber of edges "+b.Edges.size()+"\n");
            for(j=0; j < b.Edges.size();j++)
            {
                ac = b.Edges.get(j);
                if( ac.destiny_D != null)
                {
                    System.out.print("Edge "+j+" to: Deposito padre"+ ac.destiny_D.id_father +
                            " Child bay: "+ ac.destiny_B.id +" cost:"+ ac.cost+ "\n");
                }else if( ac.destiny_B != null)
                {
                    System.out.print("Edge "+j+" to: Bahia padre"+ ac.destiny_B.id_father +
                            " Child bay: "+ ac.destiny_B.id +" cost:"+ ac.cost+ "\n");
                }else{
                    System.out.print("Edge "+j+" to: Cliente padre"+ ac.destiny_C.id_father +
                            " Child client: "+ ac.destiny_C.id +" cost:"+ ac.cost+ "\n");  
                }
            }   
            
        }
        //============================================================================================
        System.out.print("\n\nNumero de Clientes "+g.Clients.size()+"\n");
        for(i = 0; i < g.Clients.size(); i++)
        {
            c = g.Clients.get(i);
            System.out.print("Cliente "+ c.id);
            if(c.id_father != Integer.MAX_VALUE)
                System.out.print("Cliente padre "+ c.id_father);
            
            System.out.print("Cajas "+c.Boxes+"\n");
            System.out.print("Tiempo de servicio "+c.Servie_Time+"\n");
            System.out.print("Ventanas de tiempo "+ c.TW.length/2);
            for(j=0; j<c.TW.length;j=j+2)
            {
                System.out.print("Ventana de tiempo "+ j+1 + " "+ c.TW[j] +" - " + c.TW[j+1]+"\n");
            }
            
            System.out.print("Numero de arcos "+c.Edges.size()+"\n");
            for(j=0; j < c.Edges.size();j++)
            {
                ac = c.Edges.get(j);
                if( ac.destiny_D != null)
                {
                    System.out.print("Edge "+j+" to: Deposito padre"+ ac.destiny_D.id_father +
                            " Child bay: "+ ac.destiny_B.id +" cost:"+ ac.cost+ "\n");
                }else if( ac.destiny_B != null)
                {
                    System.out.print("Edge "+j+" to: Bahia padre"+ ac.destiny_B.id_father +
                            " Child bay: "+ ac.destiny_B.id +" cost:"+ ac.cost+ "\n");
                }else{
                    System.out.print("Edge "+j+" to: Cliente padre"+ ac.destiny_C.id_father +
                            " Child client: "+ ac.destiny_C.id +" cost:"+ ac.cost+ "\n");  
                }
               
            }       
        }
    }
}


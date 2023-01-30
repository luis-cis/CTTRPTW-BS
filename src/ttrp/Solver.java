package ttrp;

import java.util.ArrayList;

public class Solver {
    public int hour = 0;
    
    /*
        Creates  a solution for a CTTRPTW graph
        input: CTTRTPW graph and a set of clients to attend
        output: A set of routes
    */
    public ArrayList<Depot> Solver(ArrayList<Client> order, Graph g)
    {
        int i, j, flag_route, flag_cicle, dist, flag_bay, temp_hour;
        Bay bh;
        Bay bhaux = null;
        Vehicle Vh;
        Client Cl;
        ArrayList<Bay> Participating_Bays;
        ArrayList<Client> Visited_Clients;
        ArrayList<Client> Order;
        ArrayList<Bay> Visited_Bays =  new ArrayList();
        
        MacroNodes Sg; //macronode creator
        
        Order = Read_Order(order, g);
            
            for(i = 0; i < g.Depots.size()-1; i++){
                //Here we discart bays that do not acces any client present on the order
                Participating_Bays = Obtain_Bh_part(g, Order, g.Depots.get(i));
                if(order.isEmpty())
                    break;
                
                if(!Participating_Bays.isEmpty()) // in case any bay have acces to a client
                {
                    for(j = 0; j < g.Depots.get(i).Vehicles.size(); j++){
                        //initialize variables
                    flag_cicle = 0;
                    flag_route = 0; flag_bay = 0; temp_hour = 0;
                    Participating_Bays = Obtain_Bh_part(g, Order, g.Depots.get(i));
                    Visited_Clients = Read_Order(Order, g);
                    Copy_List_B(Participating_Bays, Visited_Bays); 
                    Vh = g.Depots.get(i).Vehicles.get(j);       
                    hour = g.Depots.get(i).TW[0];
                    
                    //========================== Creating initial macronode ===================
                    // It is asumed that no client demand surprass vehicle capacity
                    bh = Obtain_Closest_node_DB(g, g.Depots.get(i), Visited_Bays);
                    hour = hour + Obtain_Closest_time_node_DB(g, g.Depots.get(i), Visited_Bays);
                    Sg = new MacroNodes();
                    Sg.Create_macronode_i(g.Depots.get(i), bh, g.Depots.get(i).TW[0], Vh);
                    Vh.Route.add(Sg);
          
                     //============================== Route creation =======================
                     while(flag_cicle == 0)
                     {
                        switch(flag_route)
                        {
                            case 0: // connection Bay - Client
                                    //Verify vehicle capacity and order
                                    if(Vh.usage == Vh.capacity || Order.isEmpty())
                                    {
                                        flag_route = 2; //signal end of route
                                        break;
                                    }
                                 
                                    Cl = Obtain_Closest_node_BC(g, bh, Visited_Clients, Vh, hour);
                                    
                                    if(Cl != null)
                                    {
                                        dist = Obtain_Closest_Time_Node_BC(g, bh, Visited_Clients);  
                                        if(flag_bay == 1) // In case there was a bay change
                                            dist = dist + temp_hour;
                                        
                                        if(Vh.usage + Cl.Boxes <= Vh.capacity && (hour + dist + Cl.Servie_Time) <= bh.TW[1] && (hour + dist + Cl.Servie_Time) <= Cl.TW[1])  
                                        {
                                            //Waiting time
                                            if((hour + dist) < Cl.TW[0]){         
                                                dist = dist + (Cl.TW[0] - (hour+(dist/2)));
                                            }
                                            
                                            if(Verify_Capacity_B(bh, Vh)) //check bay capacity
                                            {
                                                Sg = new MacroNodes();
                                                if(flag_bay == 1){
                                                    // in case bay swap create macronode type 2
                                                    Sg.Create_macronode_2(bh, Cl, bh);
                                                    flag_bay = 0;
                                                }else{
                                                    // in case No bay swap create macronode type 2
                                                    Sg.Create_macronode_1(bh, Cl);
                                                }
                                                //update vehicle 
                                                Vh.usage = Vh.usage + Cl.Boxes;
                                                Vh.Route.add(Sg);
                                                hour = hour + dist + Cl.Servie_Time;
                                                 
                                                Remove_Client(Visited_Clients, Cl);
                                                Remove_Client(Order, Cl);
                                                flag_bay = 0;
                                             
                                            }else { // In case theres no bay capacity for new vehicle
                                                if(flag_bay==1){
                                                    bh = bhaux; 
                                                }
                                                Remove_Bay(Visited_Bays, bh); // Since candidate bay was unavailable, discart it
                                                Remove_Bay(Participating_Bays, bh);
                                                flag_route = 1; // signal to search for another bay
                                            }
                                        }else
                                        { // a client was founded but there is not enough vehicle capacity or time in time window
                                            Remove_Client(Visited_Clients,Cl);
                                            flag_route = 0; // Try attending other client
                                        }
                                        
                                        if(Visited_Clients.isEmpty()) // in this case there is still clients to be attended, however due to capacity or TW restrictions they cannot be attended by current vehicle
                                        {
                                            Remove_Bay(Visited_Bays, bh);
                                            flag_route = 2; // signal end of route
                                        }
                                    }else { //==================There was no client found
                                        
                                        flag_route = 1; //Signal a change of bay
                                        Remove_Bay(Visited_Bays, bh);
                                        Remove_Bay(Participating_Bays, bh);
               
                                        if(flag_bay == 1)
                                        { 
                                            bh = bhaux;
                                            temp_hour = 0;
                                        }
                                    }
                                        
                                break;
                            case 1: // connection Bay - Bay
                                dist = 0; temp_hour = 0;
                                Bay bh_change;
                                
                                while(!Visited_Bays.isEmpty())
                                {
                                   bhaux = Obtain_Closest_node_BB(g, bh, Visited_Bays);
                                   if(bhaux != null)
                                   {
                                       dist = Obtain_Closest_Time_Node_BB(g, bh, Visited_Bays);
                                       if(hour + dist <= bhaux.TW[1])
                                       {
                                           bh_change = bh;
                                           bh = bhaux;
                                           bhaux = bh_change;
                                           flag_route = 0;
                                           flag_bay = 1;
                                           temp_hour = dist;
                                           break;
                                       }else
                                       {
                                           Remove_Bay(Visited_Bays, bhaux);
                                          // Bahias_Visitadas.remove(bhaux);
                                       }
                                   }else
                                   {//if there is no candidate bays signal route ending
                                       flag_route =2;
                                       break;
                                   }
                                   
                                }
                                if(Visited_Bays.isEmpty())
                                {
                                    flag_route = 2;
                                }
                                
                                break;

                            case 2: //connection Bay - Ddepot
                                Sg = new MacroNodes();
                                int ii;
                                
                                if(Vh.Route.get(Vh.Route.size()-1).bh != null)
                                {
                                   for(ii = 0; ii < Vh.Route.get(Vh.Route.size()-1).bh.Edges.size();ii++){
                                    bh = Vh.Route.get(Vh.Route.size()-1).bh;
                                    if(bh.Edges.get(ii).destiny_D != null){
                                        if(bh.Edges.get(ii).destiny_D.TW[1] == 1440){
                                            hour = hour + bh.Edges.get(ii).cost;
                                            }
                                        }
                                    } 
                                }
                                
                                hour = hour - g.Depots.get(i).TW[0]; //get route lenght
                                Sg.Create_macronode_F(g.Depots.get(g.Depots.size() - 1), hour, Vh, g.Depots.get(g.Depots.size() - 1).id);
                                
                                Vh.Route.add(Sg);
                                g.Depots.get(i).Vehicles.get(j).Route = Vh.Route;
                                flag_cicle = 1;
                                break;
                        }
                    }
                    flag_cicle = 0;
                } 
            }     
        }
        return g.Depots;
        
    }
    
    /*
        Identifies those bays that have access to at least one of the clients present in the order
        input: a CTTRTW graph, a set of clients to attend, the origin depot
        output: a set of bays
    */
    public ArrayList<Bay> Obtain_Bh_part(Graph g, ArrayList<Client> order, Depot d)
    {
        ArrayList<Bay> Part = new ArrayList<>();
        ArrayList<Bay> Part_D = new ArrayList<>();
        int len = order.size(), i , j, k, l;
        Edge ar;

        //1: otain bays present on graph that can access at least one client present on the order
        for(i = 0; i < len; i++)
        {
            for(j = 0; j < g.Bays.size(); j++)
            {
                for(k = 0; k < g.Bays.get(j).Edges.size(); k++)
                {
                    ar = g.Bays.get(j).Edges.get(k);
                    if(ar.destiny_C != null)
                    {
                        for(l=0; l<order.size();l++)
                        {
                            if(order.get(l).id_father == ar.destiny_C.id_father && order.get(l).id == ar.destiny_C.id 
                                    && !Part.contains(g.Bays.get(j)))
                            {  
                                Part.add(g.Bays.get(j));   
                            }
                        }
                    }
                }
            }
        }
        //2: identify bays connected to current depot
        for(i = 0; i < d.Edges.size(); i++)
        {
            if(Part.contains(d.Edges.get(i).destiny_B))
            {
                Part_D.add(d.Edges.get(i).destiny_B);
            }
        }
        return Part_D;
    }
    
    /*
        obtains the colesets bay to a depot
        input: a CTTRTPW graph, the depot from whitch clients will be attend, a set of bays
        output: a bay
    */
    public Bay Obtain_Closest_node_DB(Graph g, Depot dep, ArrayList<Bay> part)
    {
        int i, j, dist = Integer.MAX_VALUE;
        Bay bh = null;
        
        for(i = 0; i < dep.Edges.size(); i++)
        {
            if(dep.Edges.get(i).destiny_B != null)
            {
                if(dep.Edges.get(i).cost < dist)
                {
                    for(j = 0; j < part.size(); j++)
                    {
                        if(dep.Edges.get(i).destiny_B.id == part.get(j).id && dep.Edges.get(i).destiny_B.id_father == part.get(j).id_father)
                        {
                           dist = dep.Edges.get(i).cost;
                            bh = dep.Edges.get(i).destiny_B; 
                        }  
                    } 
                }
            }
        }
         
        return bh;
    }
    
    /*
        obtains the colesets client to a bay
        input: a CTTRTPW graph, the bay from which cleints can be reach, the order of clients, the vehicle used for routing
        output: a client
    */
    public Client Obtain_Closest_node_BC(Graph g, Bay bh, ArrayList<Client> orden, Vehicle vh, int time)
    {
        int i, j, dist = Integer.MAX_VALUE, updated_cost;
        Client cl = null;
        
        if(bh == null) //There should always be a bay due to graph processing
        {
            System.out.print("\nError closest node");
            return null;
        }
        
        for(i = 0; i < bh.Edges.size(); i++)
        {
            if(bh.Edges.get(i).destiny_C != null)
            {
                if(time < bh.Edges.get(i).destiny_C.TW[0])
                    updated_cost = bh.Edges.get(i).cost + (bh.Edges.get(i).destiny_C.TW[0] - time);
                else
                    updated_cost = bh.Edges.get(i).cost;
                
                if(updated_cost < dist)
                {
                    for(j = 0; j < orden.size(); j++)
                    {
                        if(orden.get(j).id ==  bh.Edges.get(i).destiny_C.id && orden.get(j).id_father ==  bh.Edges.get(i).destiny_C.id_father)
                        {
                            if(orden.get(j).Type == vh.type)
                            {
                                dist = updated_cost;
                                cl = orden.get(j); 
                            }
                        }
                    }    
                }
            }
        }
        return cl;
    }
    
    /*
        obtains the colesets bay to the current bay
        input: a CTTRTPW graph, the current bay, a set of posible bays to move
        output: a bay
    */
     public Bay Obtain_Closest_node_BB(Graph g, Bay bh, ArrayList<Bay> part)
    {
        int i, j, dist = Integer.MAX_VALUE;
        Bay get_bh = null;
        
        for(i = 0; i < bh.Edges.size(); i++)
        {
            if(bh.Edges.get(i).destiny_B != null)
            {
                if(bh.Edges.get(i).cost < dist)
                {
                    for(j = 0; j < part.size(); j++){ 
                        //bays must be on participating bays to be selected
                        if(part.get(j).id == bh.Edges.get(i).destiny_B.id && part.get(j).id_father == bh.Edges.get(i).destiny_B.id_father)
                        {
                            dist = bh.Edges.get(i).cost;
                            get_bh = bh.Edges.get(i).destiny_B; 
                        }
                    }  
                }
            }
        }
      
        return get_bh;
    }
     
     /*
        obtains the travel time colesets bay to the current depot
        input: a CTTRTPW graph, the current bay, a set of posible bays to move
        output: a bay
    */    
     public int Obtain_Closest_time_node_DB(Graph g, Depot dep, ArrayList<Bay> part)
    {
        int i, j, dist = Integer.MAX_VALUE;
        
        for(i = 0; i < dep.Edges.size(); i++)
        {
            if(dep.Edges.get(i).destiny_B != null)
            {
                if(dep.Edges.get(i).cost < dist)
                {
                    for(j = 0; j < part.size(); j++)
                    {
                        if(part.get(j).id == dep.Edges.get(i).destiny_B.id && part.get(j).id_father == dep.Edges.get(i).destiny_B.id_father)
                        {
                            dist = dep.Edges.get(i).cost;
                        }
                    }   
                }
            }
        }
      
        return dist;
    }
     
    /*
        obtains the travel time to the closesets client from the current bay
        input: a CTTRTPW graph, the current bay, a set of posible clients to attend
        output: time in minutes
    */
     public int Obtain_Closest_Time_Node_BC(Graph g, Bay bh, ArrayList<Client> orden)
    {
        int i,j , dist = Integer.MAX_VALUE;
        
        for(i = 0; i < bh.Edges.size(); i++)
        {
            if(bh.Edges.get(i).destiny_C != null)
            {
                if(bh.Edges.get(i).cost < dist)
                {
                    for(j = 0; j < orden.size(); j++)
                    {
                        if(orden.get(j).id == bh.Edges.get(i).destiny_C.id && orden.get(j).id_father == bh.Edges.get(i).destiny_C.id_father)
                        {
                            dist = bh.Edges.get(i).cost;
                        }
                    }
                }   
            }
        }
   
        return dist;
    }
     
     
     /*
        obtains travel time to the closesets bay from the current bay
        input: a CTTRTPW graph, the current bay, a set of posible bays to travel
        output: time in minutes
    */
    public int Obtain_Closest_Time_Node_BB(Graph g, Bay bh, ArrayList<Bay> part)
    {
        int i, j, dist = Integer.MAX_VALUE;
        
        for(i = 0; i < bh.Edges.size(); i++)
        {
            if(bh.Edges.get(i).destiny_B != null)
            {
                if(bh.Edges.get(i).cost < dist)
                {
                    for(j = 0; j < part.size(); j++)
                    {
                        if(part.get(j).id == bh.Edges.get(i).destiny_B.id && part.get(j).id_father == bh.Edges.get(i).destiny_B.id_father)
                            dist = bh.Edges.get(i).cost;
                    }
                }
            }
        }
        return dist;
    }
    
    /*
        Verifies if a bay has enought capacity to hold another vehicle, if so it is added
        input: candidate bay and vehicle
        output: true if vehicle can occupy bay, false otherwise
    */ 
     public Boolean Verify_Capacity_B(Bay bh, Vehicle vh)
     {
         if(bh.Usage.size() != bh.capacity){
             
             if(!bh.Usage.contains(vh))
                 bh.Usage.add(vh);
             return true;
         }else
         {
             return false;
         }
     }
     
     /*
        loads the corresponding order information to a CTTRTPW graph
        input: a set of clients to attend, a CTTRTPW graph
        output: returns a set of clients 
     */
     public ArrayList<Client> Read_Order(ArrayList<Client> or, Graph g)
     {
         ArrayList<Client> aux = new ArrayList<>();
         Client cl;
         int j;
         
         for(int i = 0; i < or.size(); i++)
         {
            cl = new Client(or.get(i).id, or.get(i).TW, or.get(i).Servie_Time, or.get(i).Boxes);
            cl.id_father = or.get(i).id_father;
            cl.Type = or.get(i).Type;
            
            for(j = 0; j < g.Clients.size(); j++)
            {
                if(g.Clients.get(j).id == cl.id && g.Clients.get(j).id_father == cl.id_father)
                    if(g.Clients.get(j).TW[0] <= or.get(i).TW[0] && g.Clients.get(j).TW[1] >= or.get(i).TW[1]){
                        cl.Edges = g.Clients.get(j).Edges;
                    }
                    
            }
             aux.add(cl);
         }
 
         return aux;
     }

     /*
        Removes a client from a given list
        input: a list of clients, the client to remove
        output: none
     */
     public void Remove_Client(ArrayList<Client> List, Client rem)
     {
        int i;
       
         for(i = List.size()-1; i >= 0; i--)
         {
             if(List.get(i).id_father == rem.id_father && List.get(i).Type == rem.Type)
             {
                List.remove(List.get(i));
             }
         }
         
     }
     
    /*
        Removes a bay from a given list
        input: a list of bays, the bay to remove
        output: none
     */
    public void Remove_Bay(ArrayList<Bay> List, Bay rem)
    {
        int i;
       
        if(List == null)
            return;
        
        for(i = List.size()-1; i >= 0; i--)
        {
            if( List.get(i).id_father == rem.id_father && List.get(i).id == rem.id)
            {
                List.remove(List.get(i)); 
            }
        }
         
    }
    /*
        Creates a duplicate bay list from  agiven one
        input: a list of bays, a copy
        output: none
     */
    public void Copy_List_B(ArrayList<Bay> Origin, ArrayList<Bay> Copy)
    {
        Bay bh;
        Copy.clear();
        
        for(int i = 0; i < Origin.size(); i++)
        {
            bh = new Bay(Origin.get(i).id, Origin.get(i).TW,Origin.get(i).capacity);
            bh.Usage = Origin.get(i).Usage;
            bh.flag = Origin.get(i).flag;
            bh.Edges = Origin.get(i).Edges;
            bh.id_father = Origin.get(i).id_father;
            Copy.add(bh);
        }
    }

}
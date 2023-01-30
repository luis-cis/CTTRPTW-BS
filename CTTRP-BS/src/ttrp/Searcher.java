package ttrp;

import java.util.ArrayList;

/*
    This class helps the solver during the route creation
*/

public class Searcher {
    
    //======================================== Search the nearest node based on travel time ======================================
    
    public Bay Get_Nearest_Node_DB(Graph g, Depot dep, ArrayList<Bay> part) //From deopt to bays
    {
        int i, dist = Integer.MAX_VALUE;
        Bay bh = null;
        
        for(i = 0; i < dep.Edges.size(); i++)
        {
            if(dep.Edges.get(i).destiny_B != null)
            {
                if(dep.Edges.get(i).cost < dist && part.contains(dep.Edges.get(i).destiny_B))
                {
                    dist = dep.Edges.get(i).cost;
                    bh = dep.Edges.get(i).destiny_B;
                }
            }
        }
        
        return bh;
    }
    
    public Client Get_Nearest_Node_BC(Graph g, Bay bh, ArrayList<Client> orden)//From bay to clients
    {
        int i, dist = Integer.MAX_VALUE;
        Client cl = null;
        
        for(i = 0; i < bh.Edges.size(); i++)
        {
            if(bh.Edges.get(i).destiny_C != null)
            {
                if(bh.Edges.get(i).cost < dist && orden.contains(bh.Edges.get(i).destiny_C))
                {
                    dist = bh.Edges.get(i).cost;
                    cl = bh.Edges.get(i).destiny_C;
                }
            }
        }
      
        return cl;
    }
    
     public Bay Get_Nearest_Node_BB(Graph g, Bay bh, ArrayList<Bay> part) //from a bay to other bays
    {
        int i, dist = Integer.MAX_VALUE;
        Bay get_bh = null;
        
        for(i = 0; i < bh.Edges.size(); i++)
        {
            if(bh.Edges.get(i).destiny_B != null)
            {
                if(bh.Edges.get(i).cost < dist && part.contains(bh.Edges.get(i).destiny_B))
                {
                    dist = bh.Edges.get(i).cost;
                    get_bh = bh.Edges.get(i).destiny_B;
                }
            }
        }
      
        return get_bh;
    }
     
    //======================================== Search the amount of time required to travel to nearest node ======================================
     
     public int Get_Nearest_Time_DB(Graph g, Depot dep, ArrayList<Bay> part)
    {
        int i, dist = Integer.MAX_VALUE;
        //Bay bh = null;
        
        for(i = 0; i < dep.Edges.size(); i++)
        {
            if(dep.Edges.get(i).destiny_B != null)
            {
                if(dep.Edges.get(i).cost < dist && part.contains(dep.Edges.get(i).destiny_B))
                {
                    dist = dep.Edges.get(i).cost;
                  //  bh = dep.Edges.get(i).destiny_B;
                }
            }
        }
       
        return dist;
    }
     
     public int Get_Nearest_Time_BC(Graph g, Bay bh, ArrayList<Client> order)
    {
        int i, dist = Integer.MAX_VALUE;
        //Client cl = null;
        
        for(i = 0; i < bh.Edges.size(); i++)
        {
            if(bh.Edges.get(i).destiny_C != null)
            {
                if(bh.Edges.get(i).cost < dist && order.contains(bh.Edges.get(i).destiny_C))
                {
                    dist = bh.Edges.get(i).cost;
                   // cl = bh.Edges.get(i).destiny_C;
                }
            }
        }
      
        return dist;
    }
     
     public int Get_Nearest_Time_BB(Graph g, Bay bh, ArrayList<Bay> part)
    {
        int i, dist = Integer.MAX_VALUE;
       // Bay get_bh = null;
        
        for(i = 0; i < bh.Edges.size(); i++)
        {
            if(bh.Edges.get(i).destiny_B != null)
            {
                if(bh.Edges.get(i).cost < dist && part.contains(bh.Edges.get(i).destiny_B))
                {
                    dist = bh.Edges.get(i).cost;
                 //   get_bh = bh.Edges.get(i).destiny_B;
                }
            }
        }
      
        return dist;
    }     
}

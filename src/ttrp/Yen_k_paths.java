package ttrp;

import java.io.IOException;
import java.util.ArrayList;
    /*
        This class is responsable for obtaining the K solutions for the CTTRTP
    */
public class Yen_k_paths {
    /*
        K_paths obtains k solutions for a given graph
        Parameter: k - indicates the number of solutions to be obtain
        Output: a set of  solutions stores on a "Routes" object
    */
    public ArrayList<Routes> K_phats(int k) throws IOException
    {
        Graph original = new Graph(); //original CTTRP graph
        Graph expanded = new Graph(); // expanded graph
        
        Solver rv = new Solver(); // mocronode solver
        
        ArrayList<Routes> sol = new ArrayList();
        Routes r = new Routes();
        
        int k_max, i;
        
        original.Creat_Graph(); // create graph from a given instance on Graph class
        expanded.Expand_Graph(original); // expand graph    

        ArrayList<Client> orden = expanded.Read_Order(); // get order
        
        long start = System.currentTimeMillis(); //star timer to measure excecution time
        
        // ============================= Obtaining initial solution ===========================
        
        r.Solution = rv.Solver(orden, expanded); // call the solver to obtain a solution
        r.Get_Routes(); // get edges to be selected by yen algorithm
        r.graph = expanded;// store expanded graph
        r.Del_Seg(); // delete those edges that cannot be altered by yen's algorithm 
        //r.Print_Sol(); //Print initial solution

        sol.add(r); //store initial solution
        
        k_max = r.Selected_Macronodes.size(); //calculate the maximun number of k solution
        if(k_max < k)
            k = k_max; // correct k value if needed
        
        //======================================Obtaining the k solutions==============================
        for(i = 0; i < k-1; i++){
            r = new Routes();
            expanded.Expand_Graph(original); //reset graph
            expanded = Modify_graph(expanded, i, sol.get(0).Selected_Macronodes); //modify graph
            expanded.Read_Order(); //obtain order for the reset graph
            r.Solution = rv.Solver(orden, expanded); //solve
            r.Get_Routes(); r.graph = expanded; r.Del_Seg();
            sol.add(r);//add Kth silution 
        }
        long end = System.currentTimeMillis();
        long time = (end-start);
        
        System.out.print("\nTiempo: " + time + "\n\n");
        return sol;
    }
    
    /*
        Modifies an arc of the graph with respect a k solution
        Parameters:
        g - grapg to modify
        sg - set of segments approved for modification
        index - indicates wich segment is going to be modify
    */
    public Graph Modify_graph(Graph g, int index, ArrayList<MacroNodes> Sg){
        
        int i,j;
        Graph new_g = g;
        if(index == Sg.size())
            return null;
        
        switch(Sg.get(index).type){
            case 0:
                for(i = 0; i < new_g.Depots.size(); i++){
                    if(new_g.Depots.get(i).id == Sg.get(index).d.id && new_g.Depots.get(i).id_father == Sg.get(index).d.id_father)
                        for(j = 0; j < new_g.Depots.get(i).Edges.size(); j++)
                            if(new_g.Depots.get(i).Edges.get(j).destiny_B != null)
                                if(new_g.Depots.get(i).Edges.get(j).destiny_B.id == Sg.get(index).bh.id && new_g.Depots.get(i).Edges.get(j).destiny_B.id_father == Sg.get(index).bh.id_father ){
                                    new_g.Depots.get(i).Edges.get(j).cost = Integer.MAX_VALUE;
                                    return new_g;
                                }
                                
                }
            break;
            case 1:
                for(i = 0; i < new_g.Bays.size(); i++){
                    if(new_g.Bays.get(i).id == Sg.get(index).bh.id && new_g.Bays.get(i).id_father == Sg.get(index).bh.id_father)
                        for(j = 0; j < new_g.Bays.get(i).Edges.size(); j++)
                            if(new_g.Bays.get(i).Edges.get(j).destiny_C != null)
                                if(new_g.Bays.get(i).Edges.get(j).destiny_C.id == Sg.get(index).cl.id && new_g.Bays.get(i).Edges.get(j).destiny_C.id_father == Sg.get(index).cl.id_father ){
                                    new_g.Bays.get(i).Edges.get(j).cost = Integer.MAX_VALUE;
                                    return new_g;
                                }
                                
                }
            break;
            case 2:
                for(i = 0; i < new_g.Bays.size(); i++){
                    if(new_g.Bays.get(i).id == Sg.get(index).bh.id && new_g.Bays.get(i).id_father == Sg.get(index).bh.id_father)
                        for(j = 0; j < new_g.Bays.get(i).Edges.size(); j++)
                            if(new_g.Bays.get(i).Edges.get(j).destiny_C != null)
                                if(new_g.Bays.get(i).Edges.get(j).destiny_C.id == Sg.get(index).cl.id && new_g.Bays.get(i).Edges.get(j).destiny_C.id_father == Sg.get(index).cl.id_father ){
                                    new_g.Bays.get(i).Edges.get(j).cost = Integer.MAX_VALUE;
                                    return new_g;
                                }
                                
                }
            break;
        }
        
        return new_g;
        
    }
    
    
}

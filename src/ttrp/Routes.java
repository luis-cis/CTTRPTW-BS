package ttrp;

import java.util.ArrayList;

public class Routes {
    public ArrayList<Depot> Solution = new ArrayList();
    public ArrayList<MacroNodes> Routes = new ArrayList();
    public ArrayList<MacroNodes> Selected_Macronodes = new ArrayList();
    public ArrayList<Vehicle> Routes_for_AMPL = new ArrayList();
    public Graph graph;
    
    /*
        Recovers the route that each vehicle created
    
    */
    public void Get_Routes(){
        int i, j, k;
        for(i = 0; i < this.Solution.size();i++){
            for(j = 0; j < this.Solution.get(i).Vehicles.size();j++){
                for(k = 0; k < this.Solution.get(i).Vehicles.get(j).Route.size();k++){
                    this.Routes.add(this.Solution.get(i).Vehicles.get(j).Route.get(k));
                }
            }
        }
    }
    
    /*
        Selects the corresponding set of macronodes to be processed by the yen algorithm
    */
    public void Del_Seg(){
      int i,j, count; 
      //Phase 1 - selection
      //choose those macronodes with edges that dont connect to the final depot or are the onle ones that connect with a client node
      for(i = Routes.size()-1; i >= 0; i--){
          
          switch(this.Routes.get(i).type){
              case 0:
                  if(this.Routes.get(i).bh != null)
                    this.Selected_Macronodes.add(Routes.get(i));
              break;
              case 1:
                  
                  if(this.Routes.get(i).cl != null){
                      if(this.Routes.get(i).cl.Edges.size() != 1){
                          this.Selected_Macronodes.add(Routes.get(i));
                          break;
                      }
                        else{
                            count = 0;
                            for(j=0;j<this.graph.Clients.size(); j++){
                                 if(this.Routes.get(i).cl.id_father == this.graph.Clients.get(j).id_father)
                                     count++;
                            }
                            if(count != 1)
                                this.Selected_Macronodes.add(Routes.get(i));
                        }
                  }
                    
              break;
              case 2:
                  if(this.Routes.get(i).cl != null){
                      if(this.Routes.get(i).cl.Edges.size() != 1){
                          this.Selected_Macronodes.add(Routes.get(i));
                          break;
                      }
                        else{
                            count = 0;
                            for(j=0;j<this.graph.Clients.size(); j++){
                                 if(this.Routes.get(i).cl.id_father == this.graph.Clients.get(j).id_father)
                                     count++;
                            }
                            if(count != 1)
                                this.Selected_Macronodes.add(Routes.get(i));
                        }
                  }
              break;
              case 3:  
              break;
          }
      }
      int flag;
      //Phase 2 - delete repeated macronodes
      for(i = this.Selected_Macronodes.size()-1; i >=0 ; i--){
          flag = 0;
          for(j =0; j < this.Selected_Macronodes.size();j++){
              if(j != i){
                  if(this.Selected_Macronodes.get(i).type == this.Selected_Macronodes.get(j).type){
                      switch(this.Selected_Macronodes.get(i).type){
                          case 0:
                              if(this.Selected_Macronodes.get(i).d.id == this.Selected_Macronodes.get(j).d.id){
                                  if(this.Selected_Macronodes.get(i).d.id_father == this.Selected_Macronodes.get(j).d.id_father){
                                      this.Selected_Macronodes.remove(i);
                                      flag = 1;
                                  }
                              }
                          break;
                          case 1:
                              if(this.Selected_Macronodes.get(i).bh.id == this.Selected_Macronodes.get(j).bh.id){
                                  if(this.Selected_Macronodes.get(i).cl.id_father == this.Selected_Macronodes.get(j).cl.id_father){
                                      this.Selected_Macronodes.remove(i);
                                      flag = 1;
                                  }
                              }
                          break;
                          case 2:
                              if(this.Selected_Macronodes.get(i).bh.id == this.Selected_Macronodes.get(j).bh.id){
                                  if(this.Selected_Macronodes.get(i).cl.id_father == this.Selected_Macronodes.get(j).cl.id_father){
                                      this.Selected_Macronodes.remove(i);
                                      flag = 1;
                                  }
                              }
                          break;
                          case 3:
                          break;
                      }
                      if(flag == 1)
                          break;
                  }
                      
              }
          }
      }
    }
    
    /*
        Gets the edges that are present within the selected macronodes
        input: a solved CTTRTPW
        output: a list of edges
    */
    public ArrayList<Edge> Get_Edges(Graph g){
        ArrayList<Edge> Edges = new ArrayList();
        
        int i,j,k;
        for(i = 0; i < this.Selected_Macronodes.size();i++){
            
            switch(this.Selected_Macronodes.get(i).type){
                
                case 0:
                    for(j=0;j < g.Depots.size();j++){
                        if(this.Selected_Macronodes.get(i).d.id == g.Depots.get(j).id && this.Selected_Macronodes.get(i).d.id_father == g.Depots.get(j).id_father){
                           for(k=0;k<g.Depots.get(j).Edges.size();k++){
                               if(g.Depots.get(j).Edges.get(k).destiny_B != null)
                               if(this.Selected_Macronodes.get(i).bh.id == g.Depots.get(j).Edges.get(k).destiny_B.id && this.Selected_Macronodes.get(i).bh.id_father == g.Depots.get(j).Edges.get(k).destiny_B.id_father){
                                   Edges.add(g.Depots.get(j).Edges.get(k));
                                   break;
                               }
                           }
                        }
                    }
                break;
                case 1:
                    for(j=0;j < g.Bays.size();j++){
                        if(this.Selected_Macronodes.get(i).bh.id == g.Bays.get(j).id && this.Selected_Macronodes.get(i).bh.id_father == g.Bays.get(j).id_father){
                           for(k=0;k<g.Bays.get(j).Edges.size();k++){
                               if(g.Bays.get(j).Edges.get(k).destiny_C != null)
                                    if(this.Selected_Macronodes.get(i).cl.id == g.Bays.get(j).Edges.get(k).destiny_C.id && this.Selected_Macronodes.get(i).cl.id_father == g.Bays.get(j).Edges.get(k).destiny_C.id_father){
                                        Edges.add(g.Bays.get(j).Edges.get(k));
                                        break;
                                    }
                           }
                        }    
                    }
                break;
                case 2:
                    for(j=0;j < g.Bays.size();j++){
                        if(this.Selected_Macronodes.get(i).bh.id == g.Bays.get(j).id && this.Selected_Macronodes.get(i).bh.id_father == g.Bays.get(j).id_father){
                           for(k=0;k<g.Bays.get(j).Edges.size();k++){
                               if(g.Bays.get(j).Edges.get(k).destiny_C != null)
                                    if(this.Selected_Macronodes.get(i).cl.id == g.Bays.get(j).Edges.get(k).destiny_C.id && this.Selected_Macronodes.get(i).cl.id_father == g.Bays.get(j).Edges.get(k).destiny_C.id_father){
                                        Edges.add(g.Bays.get(j).Edges.get(k));
                                        break;
                                    }
                           }
                        }    
                    }
                break;
            }
        }
        return Edges;
    }
    
    public void Print_Sol(){
        int i;
        String salida = "";
        System.out.print("\n========================= Solucion ====================== \n\n");
        for(i = 0; i < this.Routes.size(); i++){
            switch(this.Routes.get(i).type){
                case 0:
                   salida = salida + "D "+ this.Routes.get(i).d.id_father + " - "+ this.Routes.get(i).d.id;
                   if(this.Routes.get(i).bh != null)
                        salida = salida + " => B "+ this.Routes.get(i).bh.id_father + " - "+ this.Routes.get(i).bh.id;    
                break;
                case 1:
                   salida = salida + " => C "+ this.Routes.get(i).cl.id_father + " - "+ this.Routes.get(i).cl.id;
                   salida = salida + " => B "+ this.Routes.get(i).bh.id_father + " - "+ this.Routes.get(i).bh.id;
                break;
                case 2:
                   salida = salida + " => B "+ this.Routes.get(i).bh.id_father + " - "+ this.Routes.get(i).bh.id;
                   salida = salida + " => C "+ this.Routes.get(i).cl.id_father + " - "+ this.Routes.get(i).cl.id;
                   
                   if(this.Routes.get(i).bh1 != null)
                       salida = salida + " => B "+ this.Routes.get(i).bh1.id_father + " - "+ this.Routes.get(i).bh1.id;
                       
                break;
                case 3:
                   salida = salida + " => D "+ this.Routes.get(i).d.id_father + " - "+ this.Routes.get(i).d.id;
                   System.out.print(salida + "\n");
                   salida = "";
                break;
            }
        }
        
    }
    
    public String Vh_Types()
    {
        String t = "";
        ArrayList<Integer> types = new ArrayList();
        int i,j ;
        for(i = 0; i < this.Solution.size(); i++){
            for(j = 0; j < this.Solution.get(i).Vehicles.size(); j++){
                if(!types.contains(this.Solution.get(i).Vehicles.get(j).type))
                    types.add(this.Solution.get(i).Vehicles.get(j).type);
            }
        }
        
        for(i = 0; i < types.size(); i++){
            t = t + types.get(i) + " ";
        }
        
        return t;
    }
        

    
}

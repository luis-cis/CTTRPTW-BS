package TTRP_E;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import ttrp.Bay;
import ttrp.*;

/*
    This class is used during the creation of the dat file
*/

public class Routs_T extends Path{
    
    public ArrayList<Vehicle> Rutas = new ArrayList();
    public ArrayList<Client> Cl_T = new ArrayList();
    public ArrayList<Bay> Bh_T = new ArrayList();
    public Graph g;
    
    String path = Get_path;
    
    public Routs_T(){
        try {
            Read_Vh();
        } catch (IOException ex) {
            Logger.getLogger(Routs_T.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void Read_Vh() throws FileNotFoundException, IOException{
        
        BufferedReader in = new BufferedReader(new FileReader(path + "Vehiculos.txt"));
            
        Vehicle Vh;
        int i;
        String[] aux;
        
        int n_v = Integer.parseInt(in.readLine()), k;
        
        for(i = 0; i< n_v; i++){
            aux = in.readLine().split(" ");
            for(k = 0; k < Integer.parseInt(aux[0]); k++){
                Vh = new Vehicle(); 
                Vh.type = Integer.parseInt(aux[2]);
                Vh.hourly_cost = Integer.parseInt(aux[3]);
                Vh.id = k + 1;
                this.Rutas.add(Vh);
           }
        }
        in.close();
    }
    
    public void Get_Sol(Routes Sol){
        int i,j, k, index;
        
        for(i = 0; i < Sol.Solution.size() - 1; i++){
            for(j = 0; j < Sol.Solution.get(i).Vehicles.size(); j++){
                if(Sol.Solution.get(i).Vehicles.get(j).Route.size() > 2){
                    Add_Rout(Sol.Solution.get(i).Vehicles.get(j));
                }        
            }
        }   
    }
    
    public void Add_Rout(Vehicle Vh){
        int index = 0, i ,j;
        
        for(i = 0; i < this.Rutas.size(); i++){
            if(this.Rutas.get(i).id == Vh.id && this.Rutas.get(i).type == Vh.type){
                index = i; break;
            }
                
        }
        for(i = 0; i < Vh.Route.size(); i++){
            this.Rutas.get(index).Route.add(Vh.Route.get(i));
        }
        
    }
    
    public void Get_Cl_Bh(){
        Client cl;
        Bay bh;
        int i, j, k;
                            //================================= Add clients to array =========
        for(i = 0; i < this.Rutas.size(); i++){
            for(j = 0; j < this.Rutas.get(i).Route.size(); j++){
                if(this.Rutas.get(i).Route.get(j).type == 1 || this.Rutas.get(i).Route.get(j).type == 2){
                    cl = new Client(this.Rutas.get(i).Route.get(j).cl.id, this.Rutas.get(i).Route.get(j).cl.TW, 0, 0);
                    cl.id_father = this.Rutas.get(i).Route.get(j).cl.id_father;
                    if(Verify_Cl(cl))
                        Cl_T.add(cl);
                }
                    
            }
        }
                         //================================= Add bays to array =========
        for(i = 0; i < this.Rutas.size(); i++){
            for(j = 0; j < this.Rutas.get(i).Route.size(); j++){
                if(this.Rutas.get(i).Route.get(j).type != 3){
                    bh = new Bay(this.Rutas.get(i).Route.get(j).bh.id, this.Rutas.get(i).Route.get(j).bh.TW, 0);
                    bh.id_father = this.Rutas.get(i).Route.get(j).bh.id_father;
                    if(Verify_Bh(bh))
                        Bh_T.add(bh);
                }
            }
        }
    }
    
    public Boolean Verify_Cl(Client cl){
        int i;
        for(i = 0; i < this.Cl_T.size(); i++){
            if(this.Cl_T.get(i).id == cl.id && this.Cl_T.get(i).id_father == cl.id_father)
                return false;
        }
        return true;
    }
    
    public Boolean Verify_Bh(Bay bh){
        int i;
        for(i = 0; i < this.Bh_T.size(); i++){
            if(this.Bh_T.get(i).id == bh.id && this.Bh_T.get(i).id_father == bh.id_father)
                return false;
        }
        return true;
    }
}

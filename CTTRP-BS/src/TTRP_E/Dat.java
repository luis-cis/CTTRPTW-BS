package TTRP_E;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import ttrp.*;

public class Dat extends Path{
    
    String path = Get_path;
    String orden = Get_orden;
    
    //KRP-RG Solution
    ArrayList<Routes> Sol = new ArrayList();
    ArrayList<Routs_T> Sol_T = new ArrayList();
    
    //Simple sets
    String K = "";
    String C = "";
    String T = "";
    
    //Complex sets
    ArrayList<String> BT = new ArrayList();
    ArrayList<String> RK = new ArrayList();
    ArrayList<String> RC = new ArrayList();
    ArrayList<String> RB = new ArrayList();
    ArrayList<String> n = new ArrayList();
    ArrayList<String> ir = new ArrayList();
    ArrayList<String> cr = new ArrayList(); 
    ArrayList<String> f = new ArrayList();
    
    //Auxiliary variables
    ArrayList<Client> Cl = new ArrayList();
    ArrayList<Bay> Bh = new ArrayList();
    
    
    int route_number;
    
    public Dat(ArrayList<Routes> s, ArrayList<Routs_T> S){
        this.Sol = s;
        this.Sol_T = S;
    }
    
    /*
        Writes .dat file, if the file already has data it get overwriten
    */
    public void Write_Dat() throws IOException{
        int i;
        
        try{
            FileWriter clean = new FileWriter("AMPL/TTRP.dat");
            clean.write("");
            clean.flush();
            clean.close();
            
            FileWriter wr = new FileWriter("AMPL/TTRP.dat");
           
            
            wr.write("set K:= "+ this.K + "; \n");
            wr.write("set C:= "+ this.C + "; \n");
            wr.write("set T:= "+ this.T + "; \n");
            wr.write("\n");
            for(i = 0; i < this.BT.size(); i++)
                wr.write("set BT[" + (i+1) + "]:= " + this.BT.get(i)+ "; \n");
            
             wr.write("\n");
            for(i = 0; i < this.RK.size(); i++)
                wr.write("set RK[" + (i+1) + "]:= " + this.RK.get(i)+ "; \n");
            
            wr.write("\n");
            for(i = 0; i < this.RC.size(); i++)
                wr.write("set RC[" + (i+1) + "]:= " + this.RC.get(i)+ "; \n");
            
            wr.write("\n");
            for(i = 0; i < this.RB.size(); i++)
                wr.write("set RB[" + (i+1) + "]:= " + this.RB.get(i)+ ";\n");
            
           
            wr.write("\nparam n:= ");
            for(i = 0; i < this.n.size(); i++)
                wr.write("\n" + this.n.get(i));
            wr.write(";\n");
            
            wr.write("\nparam ir:= ");
            for(i = 0; i < this.ir.size(); i++)
                wr.write("\n" + this.ir.get(i));
            wr.write(";\n");
            
            wr.write("\nparam cr:=");
            for(i = 0; i < this.cr.size(); i++)
                wr.write("\n" + this.cr.get(i));
         //   wr.write(";\n");
            
            wr.write("\nparam f:=");
            for(i = 0; i < this.f.size(); i++)
                wr.write("\n" +this.f.get(i));
           // wr.write(";\n");
            
            wr.flush(); 
            wr.close();
        }catch(Exception ex){
            ex.getMessage();
        }
        
        
        
    }
    
    /*
        Formats a KRP-RG solution into the corresponding AMPL format
    */
    public void Get_Variables(){
        Get_K(); // vehicle types
        Get_C(); // clients
        Get_T(); // time windows
        Get_BT(); // bays that have time window T
        Get_RK(); // Routes that are served by vehicle k
        Get_RC(); // Routes on wich client c is attended
        Get_RB(); // clients attended by bay 
        try {
            Get_n(); // number of vehicle types
            Get_ir(); // route income
            
        } catch (IOException ex) {
            Logger.getLogger(Dat.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        Get_cr(); // route cost
        Get_f(); // bay opening cost
    }
    
    public void Get_K(){
        int i;
        for(i = 0; i < this.Sol.get(0).graph.Depots.size()-1; i++){
            this.K = this.K + (i + 1) + " ";
        }
    }
    
    public void Get_C(){
        int i,j,k, flag = 0;
        Routes r;
        ArrayList<Integer> c = new ArrayList();
        Client cl;
        
        for(i = 0; i < this.Sol.size();i++){
            r = this.Sol.get(i);
            for(j = 0; j < r.Routes.size(); j++){
                if(r.Routes.get(j).type == 1 || r.Routes.get(j).type == 2)
                    if(r.Routes.get(j).cl != null){
                        cl = new Client(0, null, 0, 0);
                        cl.id_father = r.Routes.get(j).cl.id_father;
                        cl.TW = r.Routes.get(j).cl.TW;
                        flag = 0;
                        for(k = 0; k < this.Cl.size(); k++){
                            if(this.Cl.get(k).id_father == cl.id_father){
                                flag = 1; 
                                break;
                            }
                        }
                        if(flag == 0)
                            this.Cl.add(cl);
                    }
                      
            }
        }
      
        for(i = 0; i < this.Cl.size(); i++){
            this.Cl.get(i).id = i + 1;
            this.C = this.C + (i+1) +" ";
        }
    }
    
    public void Get_T(){
        int i;
        for(i = 0; i < this.Sol.get(0).graph.Depots.size()-1; i++){
            this.T = this.T + (i + 1) + " ";
        }
    }
        
    // ============================== Complex Sets
    public void Get_BT(){
       int i, j, k, tw, flag, id = 0;
       Routs_T rt;
       ArrayList<Integer> founded = new ArrayList<>();
       String line = "";
       Bay bh, new_bh;
       //Obtain bays that appear on solutions
       
       for(i = 0; i < this.Sol_T.size(); i++){
           for(j=0; j<this.Sol_T.get(i).Bh_T.size(); j++){
               bh = this.Sol_T.get(i).Bh_T.get(j);
               flag = 0;
               for(k = 0; k < this.Bh.size(); k++){
                   if(bh.id_father == this.Bh.get(k).id_father)
                       flag = 1;
               }
               if(flag == 0){
                   new_bh = new Bay(id, null, 0);
                   new_bh.id_father = bh.id_father;
                   this.Bh.add(new_bh);
                   id++;
               }
           }
       }
       
       for(i=0; i< this.T.split(" ").length;i++){
           tw = i;
           for(j = 0; j < this.Sol_T.size(); j++){
               rt = this.Sol_T.get(j);
               
               for(k = 0; k < rt.Bh_T.size(); k++){
                   id = Get_CB_Id(rt.Bh_T.get(k).id_father, 1);
                   if(rt.Bh_T.get(k).id == tw && founded.contains(id) == false){
                       founded.add(id);
                       id++;
                       line = line + " " + id;
                   }
               }
           }
           this.BT.add(line);
           line = "";
           founded.clear();
       }
        line = "";
    }
    
    public void Get_RK(){
        int i, j, k, c = 1, num, id_aux;
        
        num = this.Sol.get(0).graph.Depots.size()-1;
        String line;
        for(i = 0; i < num; i++){
            line = ""; c = 1;
            
             for(j = 0; j < this.Sol.size();j++){   
                 for(k = 0; k<this.Sol.get(j).Routes.size(); k++){
                     if(this.Sol.get(j).Routes.get(k).type==0){
                         id_aux = this.Sol.get(j).Routes.get(k).d.id;
                         if(id_aux == i){
                             line = line + Integer.toString(c) + " " ;
                         }
                         c++;
                     }
                 }
             }
            this.RK.add(line);
        } 
        this.route_number = c-1;
    }
    
    public void Get_RC(){
        int i, j, k,c = 1, flag;
        String line = "";
        
        for(i = 1; i <= this.route_number; i++){
            line = ""; c = 1;
            for(j = 0; j < this.Sol.size();j++){
                    
                for(k = 0; k<this.Sol.get(j).Routes.size(); k++){
                    
                    if(this.Sol.get(j).Routes.get(k).type==1 || this.Sol.get(j).Routes.get(k).type==2){
                         if(c == i){
                             line = line + Integer.toString(this.Sol.get(j).Routes.get(k).cl.id_father) + " ";
                         }else if(c > i)
                             //break cicle
                             break;   
                     }
                    
                    if(this.Sol.get(j).Routes.get(k).type==3){
                        c++;
                    }   
                 }
            }
            this.RC.add(line);
        }
        flag = 1;
    }
    
    public void Get_RB(){
        int i, j, k,c = 1, flag;
        String line = "";
        ArrayList<Integer> ids = new ArrayList();
        
        for(i = 1; i <= this.route_number; i++){
            line = ""; c = 1; ids.clear();
            for(j = 0; j < this.Sol.size();j++){
                    
                for(k = 0; k<this.Sol.get(j).Routes.size(); k++){
                    
                    if(this.Sol.get(j).Routes.get(k).type==1){
                         if(c == i && ids.contains(this.Sol.get(j).Routes.get(k).bh.id_father) == false){
                             line = line + Integer.toString(this.Sol.get(j).Routes.get(k).bh.id_father) + " ";
                             ids.add(this.Sol.get(j).Routes.get(k).bh.id_father);
                         }else if(c > i)
                             //break cicle
                             break;   
                     }else if(this.Sol.get(j).Routes.get(k).type==2 && ids.contains(this.Sol.get(j).Routes.get(k).bh1.id_father) == false){
                         if(c == i){
                             line = line + Integer.toString(this.Sol.get(j).Routes.get(k).bh1.id_father) + " ";
                             ids.add(this.Sol.get(j).Routes.get(k).bh1.id_father);
                         }else if(c > i)
                             //break cicle
                             break; 
                     }
                    
                    if(this.Sol.get(j).Routes.get(k).type==3){
                        c++;
                    }   
                 }
            }
            this.RB.add(line);
        }
        flag = 1;
    }
    
    public int Get_CB_Id(int id_padre, int flag){
        int i, j;
        if(flag == 0){
            for(i = 0; i < this.Cl.size(); i++){
                if(id_padre == this.Cl.get(i).id_father)
                    return this.Cl.get(i).id;
            }
        }else{              
            for(i = 0; i < this.Bh.size(); i++){
                if(id_padre == this.Bh.get(i).id_father)
                    return this.Bh.get(i).id;
            }
        }
        
        return 0;
    }
    
    public void Get_n() throws FileNotFoundException, IOException{
       
        BufferedReader in = new BufferedReader(new FileReader(path + "Vehiculos.txt"));
        
        int i, nv = Integer.parseInt(in.readLine());
        String[] read_line;
        String line;
        int[][] n_aux = new int[nv][2];
        
        for(i = 0; i < nv; i++){
            read_line = in.readLine().split(" ");
            line = i+" "+read_line[0];
            if(i == nv)
                line=line+";";
            this.n.add(line);
        }
        
    }  
    
    public void Get_ir() throws FileNotFoundException, IOException{
        BufferedReader in = new BufferedReader(new FileReader(path + orden));
        int i, j, k, h, id, count, ir, n_c = Integer.parseInt(in.readLine()); 
        String[] aux;
        Vehicle vh;
        String line;
        
        for(i = 0; i < n_c; i++){
            aux = in.readLine().split(" ");
            id = Integer.parseInt(aux[0]);
            for(j = 0; j < this.Cl.size(); j++){
                if(id == this.Cl.get(j).id_father){
                    this.Cl.get(j).Payment = Integer.parseInt(aux[3]);
                    break;
                }
            }
        }
        in.close();
        count = 1;
        
        for(i = 0; i < this.Sol.size(); i++){
            line = ""; ir = 0;
            for(j = 0; j < this.Sol.get(i).Routes.size();j++){   
                switch(this.Sol.get(i).Routes.get(j).type){
                    case 1: case 2:
                        for(k = 0; k < this.Cl.size();k++){
                        if(this.Cl.get(k).id_father == this.Sol.get(i).Routes.get(j).cl.id_father){
                            ir = ir + this.Cl.get(k).Payment;
                        }
                    }
                    break;
                    case 3:
                        
                        line = Integer.toString(count) + " " + Integer.toString(ir);
                        if(count == this.route_number){
                            line = line + ";";
                        }
                        this.ir.add(line);
                        count++; ir = 0; line = "";
                    break;
                }
            }    
        } 
    }
    
    public void Get_cr(){
       int i, j, k, cost = 0, aux, count = 1; 
       Vehicle vh = null;
       String line;
       
       for(i = 0; i < this.Sol.size(); i++){           
           for(j = 0; j < this.Sol.get(i).Routes.size(); j++){
               
                             
                switch(this.Sol.get(i).Routes.get(j).type){
                       case 0:
                           cost = 0; 
                           vh =  this.Sol.get(i).Routes.get(j).vh;
                        break;
                       case 1: case 2:
                           aux = this.Sol.get(i).Routes.get(j).bh.opening_cost;
                           cost = cost + aux;
                        break;
                       case 3:
                           aux = ((this.Sol.get(i).Routes.get(j).hour) * vh.hourly_cost);
                            cost = cost + aux;   
                            line = count + " " + cost;
                            if(count == this.route_number){
                                line = line + ";";
                            }
                            this.cr.add(line);
                            cost = 0; count++;
                       break;  
                   }
                   
                   
           }
       }
       
    }
    
    public void Get_f(){
        int i, id;
        
        for(i = 0; i < this.Bh.size(); i++){
            id = this.Bh.get(i).id + 1;
            this.f.add(id + " "+ this.Bh.get(i).opening_cost);
        }
        i = 0;
    }
}

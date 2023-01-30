package TTRP_E;

import java.io.IOException;
import java.util.ArrayList;
import ttrp.*;

public class Translator {
    
    public ArrayList<Routes> Soluciones = new ArrayList(); 
     
    
    public int[] Traducir_AMPL(ArrayList<Routes> Sol) throws IOException{
        int i;
        ArrayList<Routs_T> Rut_T = new ArrayList();
        Routs_T Rt;
        
        // Routes T has onyl those clients and bays that are present whithin a solution
        for(i = 0; i < Sol.size(); i++){
            Rt = new Routs_T();
            Rt.Get_Sol(Sol.get(i));
            Rt.Get_Cl_Bh();
            Rt.g = Sol.get(i).graph;
            Rut_T.add(Rt);
            
        }
       //print_sol(Sol);
       Dat dat = new Dat(Sol, Rut_T);
       
       //===== dat
       dat.Get_Variables();
       dat.Write_Dat();
       
       return null;
    }
    
    public void print_sol(ArrayList<Routes> r){
        int i, j, k;
        
        for(i=0; i< r.size(); i++)
        {
           System.out.print("\nS" + i + "\n"); 
            for(j = 0; j < r.get(i).Routes.size(); j++){
                switch(r.get(i).Routes.get(j).type){
                    case 0:
                        System.out.print("D - B" + r.get(i).Routes.get(j).bh.id_father + " - ");
                    break;
                    case 1:
                        System.out.print("C" + r.get(i).Routes.get(j).cl.id_father + " - ");
                    break;
                    case 2:
                        System.out.print("B "+ r.get(i).Routes.get(j).bh1.id_father + "  - C" + r.get(i).Routes.get(j).cl.id_father + " - ");
                    break;
                    case 3:
                        System.out.print("D \n");
                    break;
                }
            }
        }
    }
}

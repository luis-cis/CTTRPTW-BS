package ttrp;

import TTRP_E.Translator;
import java.io.IOException;
import java.util.ArrayList;

public class TTRP {
    /*
        ============ Main class ==================
        This class is responsable to initiazte the KRP-RG process
    */
    public static void main(String[] args) throws IOException {
        
       Yen_k_paths yen = new Yen_k_paths(); // Yen class for the k-shortest path moduler
       ArrayList<Routes> Solutions; 
       Translator Send_AMPL = new Translator(); // AMPL API
       
       Solutions = yen.K_phats(10); // obtains k solutions
       
       Send_AMPL.Traducir_AMPL(Solutions); // sends the k solutions to be processed by AMPL
       
    }
 
    
}

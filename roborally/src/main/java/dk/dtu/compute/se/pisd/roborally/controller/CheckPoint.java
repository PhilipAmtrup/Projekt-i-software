package dk.dtu.compute.se.pisd.roborally.controller;
/**
 * @author s235459
 * Her bliver typen CheckPoint defineret, som herefter bliver brugt og kaldt til checkpoint funktioner
 */


public class CheckPoint  {
    
    private int Crow;
    private int Ccol;



    public CheckPoint(int Crow , int Ccol){
        this.Crow = Crow;
        this.Ccol = Ccol;

    }

    /**
     * Giver rækker og felter tilfældige koordinater, så checkpoints opstår forskellige steder for hver gang. 
     */
    
     
    public int getCheckrow(){
        
        return this.Crow;
    
    }
    
    public void setCrow(int Crow){
        this.Crow = Crow;
    }
    
    public int getCheckCol(){
        return this.Ccol;
    }

    public void setCcol(int Ccol){
        this.Ccol = Ccol;
    }
    
    
}

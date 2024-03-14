package dk.dtu.compute.se.pisd.roborally.controller;

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

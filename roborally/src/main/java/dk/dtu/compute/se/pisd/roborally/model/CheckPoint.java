package dk.dtu.compute.se.pisd.roborally.model;

import java.util.Random;

public class CheckPoint {
    
    private int Crow;
    private int Ccol;



    public CheckPoint(int Crow , int Ccol){
        this.Crow = Crow;
        this.Ccol = Ccol;

    }

    /**
     * Giver rækker og felter tilfældige koordinater, så checkpoints opstår forskellige steder for hver gang. 
     */
    public void setRandomCoordinates(){
        this.Crow = getRandomNumber();
        this.Ccol = getRandomNumber();

    }

    public int getCheckrow(){
        
        return this.Crow;
    
    }



    public int getCheckCol(){
        return this.Ccol;
    }

    /**
     * 
     * Metode til at returnere tilfældige numre, som bruges til koordinaterne. 
     * @param Ccol  Column for checkpoints
     * @param Crow  Rows for checkpoints
     * @return
     */
    private static int getRandomNumber(){

        Random random = new Random();

        return random.nextInt(100);
    
    }

}

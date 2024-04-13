package dk.dtu.compute.se.pisd.roborally.controller;
/**
 * @author s235459
 * Her bliver typen CheckPoint defineret, som herefter bliver brugt og kaldt til checkpoint funktioner
 */


 public class CheckPoint {

    private int number;
    private int x;
    private int y;

    public CheckPoint(int x, int y, int number){
        this.x = x;
        this.y = y;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }


    public int getX(){
        return this.x;
    }

    public void setX(int x){
        this.x = x;
    }

    public int getY(){
        return this.y;
    }

    public void setY(int y){
        this.y = y;
    }
}



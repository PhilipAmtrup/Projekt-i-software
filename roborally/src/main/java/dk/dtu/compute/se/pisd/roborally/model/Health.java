package dk.dtu.compute.se.pisd.roborally.model;

public class Health {
    

    private Player player;
    public int health = 30;

    public Health (int health , Player player){
        this.health = health;
        this.player = player;
    }

    /*
    public Health (int health , Player player){
        this.health = health;
        this.player = player;
    } 
    */
    /**
     * Makes it determin which player is in play
     * @return the active player
     */
    public Player getPlayer(){
        return player;
    }

    

}

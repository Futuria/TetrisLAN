import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * The menu world for multiplayers, where the player chooses to host or join. 
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class chooseRoom extends World
{

    /**
     * Constructor for objects of class chooseRoom.
     * 
     */
    createRoom cr = new createRoom();
    enterRoom er = new enterRoom();
    boolean a;
    /**
     * Creates a world with the option of hosting a room or joining a room represented by 2 buttons. 
     * 
     * @Param thingy    Determines if the game being created/joined is Tetrix or Pentrix. 
     */
    public chooseRoom(boolean thingy){    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(800, 600, 1);
        GreenfootImage titleimg = new GreenfootImage("tetrisbg.jpg");

        setBackground(titleimg);
        addObject(cr, 400,200);
        addObject(er, 400,330);
        a=thingy;
    }
    
    /**
     * Checks which button is clicked and which World to generate next. 
     */
    
    public void act(){
        if (Greenfoot.mouseClicked(cr)) {//true if you clicked at this object, resets the world

            Greenfoot.setWorld(new TetrisWorldServer(a));

        } else if (Greenfoot.mouseClicked(er)) {//true if you clicked at this object, resets the world

            Greenfoot.setWorld(new TetrisWorldClient(a));

        }
    }
}

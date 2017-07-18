import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * The world after the start screen where the player choosess the gamemode. 
 * 
 * @author Eric Sun
 * @version June 2017
 */
public class Menu extends World
{

    /**
     * Constructor for objects of class Menu.
     * 
     */
    Tetris t = new Tetris();
    Tetris t2 = new Tetris();
    Pentrix p = new Pentrix();
    Pentrix p2 = new Pentrix();
    
    /**
     * Creates a menu page with 4 buttons to choose the gamemodes with. 
     */
    
    public Menu()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(800, 600, 1); 
        GreenfootImage titleimg = new GreenfootImage("menubg.png");

        setBackground(titleimg);
        addObject(t, 235, 150);
        addObject(t2, 235, 440);
        addObject(p, 235, 240);
        addObject(p2, 235, 520);
    }
    
    /**
     * Checks if any of the buttons are clicked and create a new World corresponding to the button clicked. 
     */
    public void act(){
        if (Greenfoot.mouseClicked(t)) {//true if you clicked at this object, resets the world

            Greenfoot.setWorld(new TetrisMain(0,2,100,500));

        }else if (Greenfoot.mouseClicked(t2)) {//true if you clicked at this object, resets the world

            Greenfoot.setWorld(new chooseRoom(true)); // fix dimensions

        }else if (Greenfoot.mouseClicked(p)) {//true if you clicked at this object, resets the world

            Greenfoot.setWorld(new TetrisMain(0,3,100,500));

        } else if (Greenfoot.mouseClicked(p2)) {//true if you clicked at this object, resets the world

            Greenfoot.setWorld(new chooseRoom(false));

        }
    }
}

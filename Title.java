import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * The starting screen of the game. 
 * 
 * @author Eric Sun
 * @version June 2017
 */
public class Title extends World
{

    /**
     * Constructor for objects of class Title.
     * 
     */
    Button1 b1 = new Button1();//start button
    
    /**
     * Creates the starting world with a start button. 
     */
    
    public Title()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(800, 600, 1); 
        GreenfootImage titleimg = new GreenfootImage("realbg.png");
        
        setBackground(titleimg);
        addObject(b1, 405, 450);

    }
    /**
     * Checks if the start button is clicked. 
     */
    public void act(){
        if (Greenfoot.mouseClicked(b1)) {//true if you clicked at this object, resets the world
            
            Greenfoot.setWorld(new Menu());
            
        }
    }
    
}

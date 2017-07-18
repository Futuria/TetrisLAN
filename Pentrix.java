import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;
/**
 * The button for choosing the Pentrix gamemode. 
 * 
 * @author Eric Sun
 * @version June 2017
 */
public class Pentrix extends Actor
{
    /**
     * Creates an effect when the button is hovered over. 
     */
    public void act() 
    {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        //Change Button.class to the name of your button class.
        if (mouse != null) {
            //change the file to what you want for when the mouse is not over the button.
            setImage("pentrix.png");
            List objects = getWorld().getObjectsAt(mouse.getX(), mouse.getY(), Pentrix.class);
            for (Object object : objects)
            {
                if (object == this)
                {
                    //change the file to what you want for when the mouse is over the button.
                    setImage("pentrix2.png");
                }
            }
        }
        
    }    
}

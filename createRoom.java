import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;
/**
 * The button for creating a room in multiplayer. 
 * 
 * @author Eric Sun
 * @version June 2017
 */
public class createRoom extends Actor
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
            setImage("create.png");
            List objects = getWorld().getObjectsAt(mouse.getX(), mouse.getY(), createRoom.class);
            for (Object object : objects)
            {
                if (object == this)
                {
                    //change the file to what you want for when the mouse is over the button.
                    setImage("create2.png");
                }
            }
        }
    }    
}

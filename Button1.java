import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;
/**
 * The start button that the players clicks to begin. 
 * 
 * @author Eric Sun
 * @version June 2017
 */
public class Button1 extends Actor
{
    /**
     * Creates an effect when the mouse hovers ove the button. 
     */
    public void act() 
    {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        //Change Button.class to the name of your button class.
        if (mouse != null) {
            //change the file to what you want for when the mouse is not over the button.
            setImage("playbutton1.png");
            List objects = getWorld().getObjectsAt(mouse.getX(), mouse.getY(), Button1.class);
            for (Object object : objects)
            {
                if (object == this)
                {
                    //change the file to what you want for when the mouse is over the button.
                    setImage("playbutton2.png");
                }
            }
        }
    }

}

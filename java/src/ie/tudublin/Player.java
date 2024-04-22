package ie.tudublin;

import processing.core.PConstants;
import processing.core.PVector;

public class Player extends GameObject{
    private PVector pos;
    private float width;
    private float halfW;

   
    public float getWidth() {
        return width;
    }
    public void setWidth(float width) {
        this.width = width;
    }
    public float getHalfW() {
        return halfW;
    }
    public void setHalfW(float halfW) {
        this.halfW = halfW;
    }

    public Player(YASC yasc, float x, float y, float rotation, int col, float width) {
        super(yasc, x, y, rotation, col);
        this.width = width;
        this.halfW = width * 0.5f;
        forward = newPvector(0, -1);
    }    

    public void render()
    {
        forward.x = yasc.sin
        if (yasc.keyPressed)
        {
            
            if (yasc.keyCode == PConstants.LEFT)
            {
                rotation -= 0.1f;
            }

            if (yasc.keyCode == PConstants.RIGHT)
            {
                rotation += 0.1f;
            }

            if (yasc.keyCode == PConstants.UP)
            {
                pos.y = pos.y - 1;
            }

            if (yasc.keyCode == PConstants.DOWN)
            {
                pos.y = pos.y + 1;
            }

            
        }
        // yasc.circle(pos.x, pos.y, width);
        yasc.stroke(255);
        yasc.translate(pos.x, pos.y);
        yasc.rotate(rotation);        
        yasc.line(- halfW, halfW, 0, - halfW);
        yasc.line(0, - halfW, halfW, halfW);
        yasc.line(halfW, halfW, 0, 0);
        yasc.line(0, 0, - halfW, halfW);
           
    }
    
}

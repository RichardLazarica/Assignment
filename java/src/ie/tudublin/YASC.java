package ie.tudublin;

import processing.core.PApplet;

public class YASC extends PApplet
{
    Player player, p1;

    public void setup()
    {
        player = new Player(this, width / 2, height / 2, 0, 0, 100);
        p1 = new Player(this, 100, 100, 0, 0, 67);

    }

    public void settings()
    {
        size(500, 500);
    }

    public void draw()
    {
        background(0);
        player.render();
    }
}

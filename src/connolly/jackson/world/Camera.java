package connolly.jackson.world;

/**
 * Camera Class
 * Created by Jackson on 11/7/16.
 */

import connolly.jackson.Input;


public class Camera {

    //public Point cameraPosition;
    //public Tile cameraTile;
    public int px;
    public int py;

    public int width;
    public int height;

    private World world;

    public Camera(int w, int h, World wRef) {
        world = wRef;
        width = w;
        height = h;
        px = 0;
        py = 0;
    }

    public void tick(Input i) {
        if (i.buttons[Input.UP] || i.buttons[Input.W])   {py = (py + world.height - 1) % world.height;}
        if (i.buttons[Input.DOWN] || i.buttons[Input.S]) {py = (py + world.height + 1) % world.height;}
        if (i.buttons[Input.LEFT] || i.buttons[Input.A]) {px = (px + world.width - 1) % world.width;}
        if (i.buttons[Input.RIGHT] || i.buttons[Input.D]){px = (px + world.width + 1) % world.width;}
    }

    public void setSize(int w, int h) {
        width = w;
        height = h;
    }

}

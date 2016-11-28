package connolly.jackson;

/**
 * Handles Keyboard and Mouse Input
 * Created by Jackson on 11/3/16.
 */

import connolly.jackson.world.Camera;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;


public class Input {
    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;

    public static final int SPACE = 6;
    public static final int ENTER = 7;


    public static final int Q = 8;
    public static final int W = 9;
    public static final int E = 10;
    public static final int R = 11;
    public static final int S = 12;
    public static final int A = 13;
    public static final int D = 14;
    public static final int F = 15;

    public static final int SHIFT = 32;

    public static final int LEFTCLICK = 60;
    public static final int RIGHTCLICK = 61;

    public static final int ESCAPE = 63;

    public boolean[] buttons = new boolean[64];
    public boolean[] oldButtons = new boolean[64];

    private Game game;
    //private Camera camera;

    public Vect mouse;

    public Input(Game g, Camera c) {
        mouse = new Vect();
        game = g;
        //camera = c;
    }

    public void set(int key, boolean down) {
        int button = -1;

        if (key == KeyEvent.VK_UP) button = UP;
        if (key == KeyEvent.VK_LEFT) button = LEFT;
        if (key == KeyEvent.VK_DOWN) button = DOWN;
        if (key == KeyEvent.VK_RIGHT) button = RIGHT;

        if (key == KeyEvent.VK_Q) button = Q;
        if (key == KeyEvent.VK_W) button = W;
        if (key == KeyEvent.VK_E) button = E;
        if (key == KeyEvent.VK_R) button = R;
        if (key == KeyEvent.VK_A) button = A;
        if (key == KeyEvent.VK_S) button = S;
        if (key == KeyEvent.VK_D) button = D;
        if (key == KeyEvent.VK_F) button = F;

        if (key == KeyEvent.VK_SHIFT) button = SHIFT;

        if (key == KeyEvent.VK_SPACE) button = SPACE;
        if (key == KeyEvent.VK_ENTER) button = ENTER;

        if (key == KeyEvent.VK_ESCAPE) button = ESCAPE;

        if (button >= 0) { buttons[button] = down;}
    }

    public void setMouse(Point p, int key, boolean down) {
        //returns mouse location in terms of the game world.
        int button = -1;
        /*
        if (down) {
            mouse.set(p.x, p.y);
            mouse.add(Vect.convert(game.getLocationOnScreen()).inverse());
            mouse.add(camera.getTopLeft());
            mouse.scale((double) 1 / game.SCALE);
            //mouse.debug("mouse: ");
        }*/

        if (key == MouseEvent.BUTTON1) button = LEFTCLICK;
        if (key == MouseEvent.BUTTON3) button = RIGHTCLICK;

        if (button >= 0) {
            buttons[button] = down;
        }
    }

    public boolean clicked(int button) {
        // doesn't waste time checking, USE CAREFULLY
        return (buttons[button] && !oldButtons[button]);
    }

    public void tick() {
        for (int i = 0; i < buttons.length; i++) {
            oldButtons[i] = buttons[i];
        }
        mouse.set(Vect.convert(MouseInfo.getPointerInfo().getLocation()));
        mouse.add(Vect.convert(game.getLocationOnScreen()).inverse());
        mouse.scale((double) 1 / Game.SCALE);
        //mouse.add(camera.getTopLeft());

    }

    public void releaseAllKeys() {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = false;
        }
    }

}

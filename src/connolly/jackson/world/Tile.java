package connolly.jackson.world;

/**
 * Map Tile Data-type
 * Created by root on 11/3/16.
 */

import connolly.jackson.Sprite;

import java.awt.image.BufferedImage;
import java.io.IOException;


public class Tile {

    public static final int WIDTH = 16;
    public static final int HEIGHT = 16;

    public static final int EAST = 0;
    public static final int NORTH = 1;
    public static final int WEST = 2;
    public static final int SOUTH = 3;

    public static final int OPEN = 0;
    public static final int FOOD = 1;
    public static final int SNAKE_HEAD = 2;
    public static final int SNAKE = 3;
    public static final int BARRIER = 4;

    private static BufferedImage tileset[][];

    static {
        try {
            tileset = Sprite.split(Sprite.load("res/tileset-simple.png"), WIDTH, HEIGHT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int state;           // Tracks Tile Value
    public double temp;         // Used by Snakes to weight tiles temporarily
    public Tile next;           // Points to next part of snakes Body (from tail to head)
    public Tile[] neighbours;

    public double foodValue;
    public double trailValue;
    public int trailColor;

    public Tile() {

        state = OPEN;
        temp = 0.0;
        next = null;           // Points to next part of snakes Body (from tail to head)
        neighbours = new Tile[4];

        foodValue  = 0.0;
        trailValue = 0.0;
        trailColor = 0x00808080;

    }

    public Tile( int state) {
        this.state = state;
        temp = -1;
        next = null;           // Points to next part of snakes Body (from tail to head)
        neighbours = new Tile[4];

        foodValue  = 0.0;
        trailValue = 0.0;
        trailColor = 0x00808080;    //Dark Gray?
    }

    public void reset(int s) {
        state = s;
        temp = 0.0;

        foodValue = 0.0;
        trailValue = 0.0;
        //trailColor = new Color( 0, 0, 0);
    }

	/*
	public Tile( Tile t) {
		this.value = t.value;
		this.temp = t.temp;
	}//*/

    public boolean isSolid() {
        return (state > 1);
    }

    public BufferedImage texture() {

        if (state == 0) { return null; }
        else {
            //				   x      y
            return tileset[state - 1][0];
        }
    }

}

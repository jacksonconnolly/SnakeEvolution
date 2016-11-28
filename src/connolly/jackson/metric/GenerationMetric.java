package connolly.jackson.metric;

/**
 * Highest generation attribute tracker
 * Created by Jackson on 11/6/16.
 */

import connolly.jackson.Input;
import connolly.jackson.world.Snake;
import connolly.jackson.world.World;

import java.awt.*;
import java.util.ArrayList;


public class GenerationMetric extends Metric {

    private World world;
    private ArrayList<Snake> snakes;

    private int currentGen;
    private int maxGen;

    public GenerationMetric(World w, ArrayList<Snake> s) {
        super(w);
        snakes = s;

        currentGen = 1;
        maxGen = 0;
    }

    public void tick(Input i) {
        int minGen = currentGen + 1;
        maxGen = 0;

        for (Snake focus : snakes) {
            if (focus.generation > maxGen) {
                maxGen = focus.generation;
            } else if (focus.generation < minGen && focus.generation != 0) {
                minGen = focus.generation;
            }
        }
        currentGen = minGen;
    }

    public void render(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawString("Current Generation: " + currentGen, 0, 0);
        g.drawString("Max Generation:     " + maxGen, 0, 12);
    }



}

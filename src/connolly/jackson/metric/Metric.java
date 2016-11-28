package connolly.jackson.metric;

/**
 * Tracks and displays population distribution of various traits
 * Created by Jackson on 11/6/16.
 */

import connolly.jackson.Input;
import connolly.jackson.world.World;

import java.awt.*;
import java.text.DecimalFormat;

public class Metric {

    protected static final DecimalFormat decFormat = new DecimalFormat("0.00");

    private World world;

    public Metric(World w) {
        world = w;
    }

    public void tick(Input i) {}

    public void render(Graphics g) {}

}

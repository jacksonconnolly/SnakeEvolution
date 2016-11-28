package connolly.jackson.metric;

/**
 * Snake length histogram metric
 * Created by Jackson on 11/7/16.
 */

import connolly.jackson.world.Snake;
import connolly.jackson.world.World;

import java.util.ArrayList;


public class LengthMetric extends  HistogramMetric {

    public LengthMetric(World w, ArrayList<Snake> s) {
        super(w, s);
    }

    public double getValue(Snake s) { return (double) s.length; }

}

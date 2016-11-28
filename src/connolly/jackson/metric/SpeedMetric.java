package connolly.jackson.metric;

/**
 * Snake Speed Histogram Metric
 * Created by Jackson on 11/7/16.
 */

import connolly.jackson.world.Snake;
import connolly.jackson.world.World;

import java.util.ArrayList;


public class SpeedMetric extends HistogramMetric {

    public SpeedMetric(World w, ArrayList<Snake> s) {
        super(w, s);
        minValue = 0.15;
    }

    public double getValue(Snake s) { return s.speed; }

}

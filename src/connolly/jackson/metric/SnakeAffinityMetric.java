package connolly.jackson.metric;

/**
 * Snake-pheromone affinity histogram metric
 * Created by Jackson on 11/7/16.
 */

import connolly.jackson.world.Snake;
import connolly.jackson.world.World;

import java.util.ArrayList;

public class SnakeAffinityMetric extends  HistogramMetric {

    public SnakeAffinityMetric(World w, ArrayList<Snake> s) {
        super(w, s);
    }

    public double getValue(Snake s) { return s.snakeAffinity; }

}

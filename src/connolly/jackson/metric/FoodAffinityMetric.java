package connolly.jackson.metric;

/**
 * Food-pheromone affinity histogram metric
 * Created by root on 11/7/16.
 */

import connolly.jackson.world.Snake;
import connolly.jackson.world.World;

import java.util.ArrayList;


public class FoodAffinityMetric extends  HistogramMetric {

    public FoodAffinityMetric(World w, ArrayList<Snake> s) {
        super(w, s);
    }

    public double getValue(Snake s) { return s.foodAffinity; }

}

package connolly.jackson.metric;

/**
 * Histogram Metric Base Class
 * Created by root on 11/7/16.
 */

import connolly.jackson.Input;
import connolly.jackson.world.Snake;
import connolly.jackson.world.World;

import java.awt.*;
import java.util.ArrayList;


public class HistogramMetric extends Metric {

    private World world;
    private ArrayList<Snake> snakes;

    private static final int HISTOGRAM_SIZE=21;

    protected double minValue;
    private double maxValue;

    private int[] histogram;

    public HistogramMetric(World w, ArrayList<Snake> s) {
        super(w);
        snakes = s;

        minValue = 0.0;
        maxValue = 0.0;

        histogram = new int[HISTOGRAM_SIZE];
    }

    public void tick(Input i) {

        // Uncomment to reset histogram bounds each cycle
        minValue = getValue(snakes.get(0));
        maxValue = getValue(snakes.get(0));

        for (int j = 1; j < snakes.size(); j++) {
            Snake focus = snakes.get(j);
            if (getValue(focus) > maxValue) {
                maxValue = getValue(focus);
            }
            else if (getValue(focus) < minValue) {
                minValue = getValue(focus);
            }
        }

        for (int j = 0; j < HISTOGRAM_SIZE; j++) { histogram[j]=0;}

        for (int j = 1; j < snakes.size(); j++) {
            Snake focus = snakes.get(j);
            histogram[(int)((HISTOGRAM_SIZE-1)*((getValue(focus) - minValue)/(maxValue - minValue)))]++;
        }
    }

    public double getValue(Snake s) { return 0.0; }

    public void render(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawString("" + decFormat.format(minValue), 0, 0);
        g.drawString("" + decFormat.format(maxValue), 72, 0);

        for (int i = 0; i < HISTOGRAM_SIZE; i++) {
            g.drawLine(i*5, 6, i*5, 6 + 6 * histogram[i]);
        }
    }

}

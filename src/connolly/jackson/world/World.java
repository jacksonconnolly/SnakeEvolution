package connolly.jackson.world;

/**
 * Handles Entity Logic and tilemap
 * Created by Jackson on 11/3/16.
 */

import connolly.jackson.Game;
import connolly.jackson.Input;
import connolly.jackson.Logfile;
import connolly.jackson.Vect;
import connolly.jackson.metric.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;


public class World {

    private static final int MAX_SNAKES = 64;
    private static final int MIN_FOOD = 3;
    private static final int MAX_FOOD = 16;

    private ArrayList<Snake> snakes;
    //private ArrayList<Tile> food;

    private LengthMetric lengthMetric;

    private ArrayList<Metric> populationMetrics;

    private int snakeCount = 0;
    public int foodCount = 0;

    private boolean resetStats;

    private Tile[][] mapTiles = null;
    private Tile[][] oldTiles = null;
    public Random random;
    public Game game;

    public int width;
    public int height;

    public World(int w, int h, Game g) {
        this.game = g;

        random = new Random();

        width = w;
        height = h;

        snakes = new ArrayList<Snake>(MAX_SNAKES); //size for 64 snakes by default
        //food = new ArrayList<Tile>(MAX_FOOD); //size for 64 snakes by default


        lengthMetric = new LengthMetric(this, snakes);

        populationMetrics = new ArrayList<Metric>(8);
        populationMetrics.add(new SpeedMetric(this, snakes));
        populationMetrics.add(new SnakeAffinityMetric(this, snakes));
        populationMetrics.add(new FoodAffinityMetric(this, snakes));
        populationMetrics.add(new GenerationMetric(this, snakes));

        mapTiles = new Tile[width][height];
        oldTiles = new Tile[width][height];

        game.log.print("Creating Tile Map...", Logfile.CONFIG);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                mapTiles[i][j] = new Tile();
            }
            //if (i % (width / 5) == 0) { game.log.print(((101 * i) / width) + "%", Logfile.FINE); }
        }

        // Create Oldtiles separately to avoid data fragmentation
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                oldTiles[i][j] = new Tile();
            }
            //if (i % (width / 5) == 0) { game.log.print(((101 * i) / width) + "%", Logfile.FINE); }
        }

        game.log.print("Creating Tile References...", Logfile.CONFIG);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //System.out.printf("%d %d\n", i, j);
                mapTiles[i][j].neighbours[Vect.NORTH] = mapTiles[i][(j-1+height) % height];
                mapTiles[i][j].neighbours[Vect.EAST]  = mapTiles[(i+width+1) % width][j];
                mapTiles[i][j].neighbours[Vect.SOUTH] = mapTiles[i][(j+height+1) % height];
                mapTiles[i][j].neighbours[Vect.WEST]  = mapTiles[(i+width-1) % width][j];

                oldTiles[i][j].neighbours[Vect.NORTH] = oldTiles[i][(j-1+height) % height];
                oldTiles[i][j].neighbours[Vect.EAST]  = oldTiles[(i+width+1) % width][j];
                oldTiles[i][j].neighbours[Vect.SOUTH] = oldTiles[i][(j+height+1) % height];
                oldTiles[i][j].neighbours[Vect.WEST]  = oldTiles[(i+width-1) % width][j];
            }
            if (i % (width / 5) == 0) { game.log.print(((101 * i) / width) + "%", Logfile.FINE); }
        }
        game.log.print("Tile References Complete.", Logfile.CONFIG);
    }

    public void tick(Input input) {

        clearMap();     // Record tile values in spare array and Initialize main array
        updateMap();    // Update pheromone trails and food smells

        // Spawn Food randomly
        if (foodCount < MAX_FOOD ) {
            if (foodCount * random.nextDouble() < 0.5) { generateFood();}
        }

        if (snakeCount < MAX_SNAKES) {
            // Spawn additional snakes
            if (snakeCount * random.nextDouble() < 0.2) {
                spawnSnake();
            }
        }

        for (int i = 0; i < snakes.size(); i++) {
            Snake focus = snakes.get(i);
            if (focus.state == Snake.ALIVE) {
                focus.tick(input);
            } else {
                snakes.remove(focus);
                snakeCount--;

                // Update Population Metrics
                for (Metric populationMetric : populationMetrics) {
                    populationMetric.tick(null);
                }
            }
        }
        lengthMetric.tick(null);

    }

    private void clearMap() {
        // Reset Map info
        Tile focus;
        Tile oldFocus;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                focus = mapTiles[i][j];
                oldFocus = oldTiles[i][j];

                if (focus.state == Tile.OPEN) {
                    // saves foodvalue and trailvalue variables
                    oldFocus.trailValue = focus.trailValue;
                    oldFocus.trailColor = focus.trailColor;
                    oldFocus.foodValue = focus.foodValue;


                    focus.trailValue = 0.8 * oldFocus.trailValue;
                    if (focus.trailValue < 0.05) {
                        focus.trailValue = 0.0;
                        focus.trailColor = 0x0f0f0f;
                    }
                    focus.foodValue = 0.7 * oldFocus.foodValue;
                    if (focus.foodValue < 0.01) { focus.foodValue = 0.0; }
                }
            }
        }
    }

    private void updateMap() {

        Tile focus;
        Tile oldFocus;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                focus = mapTiles[i][j];
                oldFocus = oldTiles[i][j];

                if (focus.state == Tile.OPEN) {

                    // for each neighbour
                    for (int k = 0; k < 4; k++) {
                        // Add surrounding snakey pheromones
                        if (focus.neighbours[k].state == Tile.OPEN) {
                            if (oldFocus.neighbours[k].trailValue > 0.5){
                                focus.trailValue += 0.04 * oldFocus.neighbours[k].trailValue;
                                focus.trailColor = blendColors(oldFocus.neighbours[k].trailColor, focus.trailColor, 0.04 * oldFocus.neighbours[k].trailValue / focus.trailValue);
                            }
                        }
                        else if (focus.neighbours[k].state == Tile.SNAKE) {
                            focus.trailValue += 0.5;
                            focus.trailColor = blendColors(focus.neighbours[k].trailColor, focus.trailColor, 0.5 / focus.trailValue);
                        }

                        // Add surrounding food smell
                        if (focus.neighbours[k].state == Tile.OPEN) {
                            focus.foodValue += 0.07 * oldFocus.neighbours[k].foodValue;
                        }
                        else if (focus.neighbours[k].state == Tile.FOOD) {
                            focus.foodValue += 0.6 * focus.neighbours[k].foodValue;
                        }

                    }
                }
            }
        }

        //for (int i = 0; i < width; i++) {for (int j = 0; j < height; j++) {}}

    }

    private void spawnSnake() {

        ArrayList<Snake> sexySnakes = new ArrayList<Snake>(MAX_SNAKES);

        // Generate a list of eligible mates
        for (Snake focus : snakes) {
            if (focus.foodStore > 10) {
                sexySnakes.add(focus);
            }
        }
        if (sexySnakes.size() < 3) {
            // Randomly Generate a new snake
            snakes.add(new Snake(getRandomOpenTile(), this));
            snakeCount++;
        }
        else {
            // Randomly select breeding pair from eligible mating pool
            Snake mother = sexySnakes.get(random.nextInt(sexySnakes.size() - 1));
            sexySnakes.remove(mother);
            Snake father = sexySnakes.get(random.nextInt(sexySnakes.size() - 1));

            snakes.add(new Snake(mother, father, getRandomOpenTile(), this));
            snakeCount++;

        }

        // Update Population Metrics
        for (Metric populationMetric : populationMetrics) {
            populationMetric.tick(null);
        }
    }

    private void generateFood() {

        Tile temp = getRandomOpenTile();

        temp.state = Tile.FOOD;
        temp.foodValue = 2 * (1 + 2 * random.nextDouble());

        foodCount++;
        game.log.print("Food Spawned, foodValue: " + temp.foodValue, Logfile.FINER);
    }

    public void render(Graphics g, Camera c) {

        Tile focus;
        BufferedImage texture;

        int iEnd = (c.width / Tile.WIDTH) + 1;
        int jEnd = (c.height / Tile.HEIGHT) + 1;

        for (int i = 0; i < iEnd; i++) {
            for (int j = 0; j < jEnd; j++) {
                focus = mapTiles[(c.px + i) % width][(c.py + j) % height];
                texture = focus.texture();
                if (texture == null) {
                    g.setColor(Color.BLACK);
                    int tone = ((c.px + i) % width + (c.py + j) % height) / 10;
                    g.setColor(new Color(tone, tone, tone));
                    g.fillRect( i * Tile.WIDTH, j * Tile.HEIGHT, 16, 16);
                    if (Game.debug) {

                        if (focus.trailValue > 0.1) {
                            int temp = 0x20000000;//(int) (0x01000000 * (Math.min(Math.max(127 + 1 * mapTiles[i][j].trailValue, 0), 255)));
                            temp += (int)(focus.trailValue * 0x6f000000) & 0xdf000000;
                            g.setColor(new Color(temp + focus.trailColor, true));
                            //g.setColor(new Color(127, (int) (Math.max(127 - 32 * mapTiles[i][j].trailValue, 0)), 0));
                            g.drawString(Integer.toString((int) focus.trailValue), i * Tile.WIDTH + 5, j * Tile.HEIGHT + 14);
                        }
                        if (focus.foodValue > 0.1) {
                            g.setColor(Color.lightGray);
                            g.drawString(Integer.toString((int) focus.foodValue), i * Tile.WIDTH +5, j * Tile.HEIGHT +14);
                        }//*/
                    }
                } else {
                    g.setColor(new Color(focus.trailColor));
                    //g.setColor(Color.CYAN);
                    g.fillRect( i * Tile.WIDTH, j * Tile.HEIGHT, 16, 16);
                    g.drawImage(texture, i * Tile.WIDTH, j * Tile.HEIGHT, null);

                    //if (focus.next !=null) {
                    //    g.setColor(Color.RED);
                    //    g.drawString("0", i * Tile.WIDTH +5, j * Tile.HEIGHT +14);
                    //}
                }
            }
        }
        g.setColor(Color.WHITE);
        g.drawString("Snake Count: " + snakeCount, 2, 36);
        g.drawString("Food Count: " + foodCount, 2, 48);

        g.translate(2, 72);
        lengthMetric.render(g);
        g.translate(0,72);

        // Update Population Metrics
        for (Metric populationMetric : populationMetrics) {
            populationMetric.render(g);
            g.translate(0, 72);
        }

        g.translate(-2, -144 - 72 * populationMetrics.size());

    }

    private Tile getRandomOpenTile() {
        int x, y;

        do {
            x = random.nextInt(width - 1);
            y = random.nextInt(height - 1);
            //System.out.print("searching");
        } while (mapTiles[x][y].state != Tile.OPEN);
        return mapTiles[x][y];
    }

    public Color getRandomColor() {
        int r = random.nextInt(255);
        int g = random.nextInt(255);
        int b = random.nextInt(255);
        return new Color(r, g, b);
    }

    private int blendColors(int rgb1, int rgb2, double ratio) {

        if (ratio > 1 || ratio < 0) {
            System.out.print("DANGER, COLOR RATIO OUTSIDE BOUNDS!!\n");
        }

        int r1 = (rgb1 & 0xff0000);
        int r2 = (rgb2 & 0xff0000);
        int r = (int)((r1 * ratio) + (r2 * (1-ratio)));

        int g1 = (rgb1 & 0xff00);
        int g2 = (rgb2 & 0xff00);
        int g = (int)((g1 * ratio) + (g2 * (1-ratio)));

        int b1 = (rgb1 & 0xff);
        int b2 = (rgb2 & 0xff);
        int b = (int)((b1 * ratio) + (b2 * (1-ratio)));

        //return (r+g+b);
        return ((r & 0xff0000) + (g & 0xff00) + (b & 0xff) );
    }


}

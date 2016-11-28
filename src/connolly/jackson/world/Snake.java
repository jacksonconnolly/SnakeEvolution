package connolly.jackson.world;

/**
 * Class for snake logic and behaviour
 * Created by Jackson on 11/3/16.
 */

import connolly.jackson.Input;
import connolly.jackson.Logfile;

public class Snake {

    // Constant Values
    public static final int EAST  = 0;
    public static final int NORTH = 1;
    public static final int WEST  = 2;
    public static final int SOUTH = 3;

    public static final int DEAD = 0;
    public static final int ALIVE = 1;


    private World world;
    public int generation;

    // Position Tracking Objects
    private Tile head;
    private Tile tail;

    // Physical Traits
    private int heading;
    public int length;
    public int state;

    private double clock;
    public double foodStore;

    // Heritable Traits
    public double speed;    // Also contributes to metabolic rate
    public int brood;
    public double scent;
    public double snakeAffinity;
    public double foodAffinity;
    public int favHeading;

    public Snake(Tile t, World w) {
        this.world = w;

        generation = 0;

        head = t;
        tail = t;

        length = 0;
        state = ALIVE;
        clock = 0.0;
        foodStore = 4 * (1 + world.random.nextDouble()) / 2;

        favHeading = world.random.nextInt(3);
        heading = favHeading;

        speed = (1.5 + world.random.nextDouble()) / 10;      //in tiles/tick
        brood = world.getRandomColor().getRGB();
        scent = (1 + 4 * world.random.nextDouble());

        snakeAffinity = world.random.nextDouble() - 0.5;
        foodAffinity = world.random.nextDouble() - 0.5;

        head.state = Tile.SNAKE_HEAD;
        head.trailColor = this.brood;

        world.game.log.print("Snake Spawned, foodStore: " + foodStore, Logfile.INFO);

    }

    public Snake(Snake mother, Snake father, Tile t, World w) {
        this.world = w;

        generation = Math.min(mother.generation, father.generation) + 1;

        head = t;
        tail = t;

        length = 0;
        state = ALIVE;
        clock = 0.0;
        foodStore = 4 * (2 + world.random.nextDouble()) / 2;

        mother.foodStore -= 3;
        father.foodStore -= 3;

        // Speed gene
        double gene = world.random.nextDouble();
        if (gene < 0.4)         { speed = mother.speed; } // Inherit mothers trait
        else if (gene < 0.8)    { speed = father.speed; }// Inherit fathers trait
        else if (gene < 0.95)   { speed = (mother.speed + father.speed) / 2; }// Inherit average trait
        else                    { speed = (mother.speed + father.speed) / 2 + ((world.random.nextDouble() - 0.5) / 10); } // Average + mutation

        // favHeading gene
        gene = world.random.nextDouble();
        if (gene < 0.45)         { favHeading = mother.favHeading; } // Inherit mothers trait
        else if (gene < 0.9)    { favHeading = father.favHeading; }// Inherit fathers trait
        else                     { favHeading = (mother.favHeading + father.favHeading) % 4; }// Inherit average trait

        // brood Gene
        gene = world.random.nextDouble();
        if (gene < 0.45)        { brood = mother.brood; } // Inherit mothers trait
        else if (gene < 0.9)    { brood = father.brood; }// Inherit fathers trait
        else                    { brood = world.getRandomColor().getRGB(); }// Inherit random trait

        // Scent gene
        gene = world.random.nextDouble();
        if (gene < 0.4)         { scent = mother.scent; } // Inherit mothers trait
        else if (gene < 0.8)    { scent = father.scent; }// Inherit fathers trait
        else if (gene < 0.95)   { scent = (mother.scent + father.scent) / 2; }// Inherit average trait
        else                    { scent = (mother.scent + father.scent) / 2 + (world.random.nextDouble() - 0.5); } // Average + mutation

        // snakeAffinity gene
        gene = world.random.nextDouble();
        if (gene < 0.4)         { snakeAffinity = mother.snakeAffinity; } // Inherit mothers trait
        else if (gene < 0.8)    { snakeAffinity = father.snakeAffinity; }// Inherit fathers trait
        else if (gene < 0.95)   { snakeAffinity = (mother.snakeAffinity + father.snakeAffinity) / 2; }// Inherit average trait
        else                    { snakeAffinity = (mother.snakeAffinity + father.snakeAffinity) / 2 + (world.random.nextDouble() - 0.5); } // Average + mutation

        // foodAffinity gene
        gene = world.random.nextDouble();
        if (gene < 0.4)         { foodAffinity = mother.foodAffinity; } // Inherit mothers trait
        else if (gene < 0.8)    { foodAffinity = father.foodAffinity; }// Inherit fathers trait
        else if (gene < 0.95)   { foodAffinity = (mother.foodAffinity + father.foodAffinity) / 2; }// Inherit average trait
        else                    { foodAffinity = (mother.foodAffinity + father.foodAffinity) / 2 + (world.random.nextDouble() - 0.5); } // Average + mutation

        head.state = Tile.SNAKE_HEAD;
        head.trailColor = this.brood;

        world.game.log.print("Snake Bred, foodStore: " + foodStore, Logfile.INFO);

    }

    public void tick(Input i) {

        clock += speed;

        if (clock >= 1.0) {
            clock--;

            // Snake Logic, only adjusts 'heading'

            Tile focus;
            for (int k=0; k < 4; k++) { // For each direction
                focus = head.neighbours[k];
                focus.temp = 0.0;

                focus.temp += focus.trailValue * snakeAffinity;
                focus.temp += focus.foodValue * foodAffinity;

                if (focus.state == Tile.FOOD) {
                    focus.temp = 9001;
                }

                if (focus.isSolid()) {
                    focus.temp = -9001;
                }

            }

            heading = favHeading;
            for (int k=0; k < 4; k++) { // For each direction
                if (head.neighbours[k].temp > head.neighbours[heading].temp) {
                    heading = k;
                }
            }

            // Snake Behaviour
            if (head.neighbours[heading].isSolid()) {
                kill();
            } else {

                if (head.neighbours[heading].state == Tile.FOOD) {
                    foodStore += head.neighbours[heading].foodValue;
                    world.foodCount--;
                }

                head.state = Tile.SNAKE;
                head.next = head.neighbours[heading];
                head = head.next;
                head.state = Tile.SNAKE_HEAD;
                head.trailColor = this.brood;
                length++;
            }
        }

        foodStore -= Math.pow(speed, 2) / 400;

        if (foodStore < 0) {
            kill();
        }
        else {
            if (length > foodStore + 1) {
                trim();
                length--;
            }
        }
    }

    private void trim() {
        if ( tail.next != null) {
            Tile temp = tail;
            tail = tail.next;
            temp.next = null;
            temp.state = Tile.OPEN;
            temp.trailValue = scent;
            temp.foodValue = 0.0;
        } else {
            System.out.print("Critical Error!!");
        }
    }

    private void kill() {
        if (state != DEAD) {
            state = DEAD;
            for (int i = length; i > 0; i--) { trim(); }
            head.state = Tile.OPEN;
        }
    }

}

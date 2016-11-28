package connolly.jackson;

/**
 * 2d Vector Data-type
 * Created by Jackson on 11/3/16.
 */

import java.awt.Point;

public class Vect {

    public double x, y;

    public static final int EAST  = 0;
    public static final int NORTH = 1;
    public static final int WEST  = 2;
    public static final int SOUTH = 3;

    public Vect() {
        this.x = 0;
        this.y = 0;
    }

    public Vect(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vect(Vect v) {
        this.x = v.x;
        this.y = v.y;
    }

    public Vect inverse() { return new Vect( -x, -y);}

    public Vect scale(double factor) {
        x = x * factor;
        y = y * factor;
        return this;
    }

    public double mag() {
        return Math.pow(Math.pow( x, 2) + Math.pow( y, 2), 0.5);
    }

    public void unit() {
        double m = this.mag();
        if ( m > 0.000001) {
            x = x / m;
            y = y / m;
        }
        else {
            x = 0;
            y = 0;
        }
    }

    public Vect getUnit() {
        double m = this.mag();
        if ( m > 0.000001) {
            return new Vect(x / m, y / m);
        }
        else { return new Vect(); }
    }

    public Vect add( Vect v) {
        x = x + v.x;
        y = y + v.y;
        return this;
    }

    public Vect dot( Vect v) { return new Vect( x * v.x, y * v.y);}

    public Point shift( Point p) { return new Point( p.x + (int)x, p.y + (int)y);}

    public void debug() {
        System.out.printf("%f, %f\n", x , y);
    }

    public void debug(String s) {
        System.err.printf("%s: %.2f, %.2f\n", s, x, y);
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void set(Vect v) {
        this.x = v.x;
        this.y = v.y;
    }

    public static Vect convert(Point p) {
        return new Vect(p.x, p.y);
    }

    public double dist(Vect v) {
        return Math.sqrt(Math.pow(x - v.x, 2) + Math.pow(y - v.y, 2));
    }

    public int getDirection() {
        if (Math.abs(x) > Math.abs(y)) {
            if (x > 0 ) { return EAST; } else {return WEST;}
        }
        else {
            if (y > 0 ) { return SOUTH; } else {return NORTH;}
        }
    }

    public Point getPoint() {
        return new Point((int) x, (int) y);
    }
	/* not worth it in 2d
	public Vect cross( Vect v) {

		return new Vect( x , y );
	}//*/
}
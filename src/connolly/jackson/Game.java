package connolly.jackson;

/**
 * Threaded container class + listeners for game use
 * Created by Jackson on 11/3/16.
 */

import connolly.jackson.world.Camera;
import connolly.jackson.world.Tile;
import connolly.jackson.world.World;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import javax.swing.JFrame;

//import connolly.jackson.world.Camera;
//import connolly.jackson.world.World;


public class Game extends Canvas implements Runnable {

    private boolean running = false;
    public static final boolean debug = true;

    private static final String NAME = "Snake: Evolution";
    private static int HEIGHT = 320*3+80;
    private static int WIDTH = 640*3;
    public static final int SCALE = 1;

    private int fps = 0;

    private JFrame frame;
    private BufferedImage frameBuffer;

    private Input input;
    public Logfile log;
    private World world;
    private Camera mainCam;

    private void init(JFrame f) {
        // Sets up References and Creates Event Listeners

        this.frame = f;

        // Create References
        log = new Logfile(this, Logfile.FINE);

        world = new World(WIDTH / Tile.WIDTH, HEIGHT / Tile.HEIGHT, this);

        mainCam = new Camera(WIDTH, HEIGHT, world);
        input = new Input(this, mainCam);

        frameBuffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        this.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent ke) {
                input.set(ke.getKeyCode(), true);
            }
            public void keyReleased(KeyEvent ke) {
                input.set(ke.getKeyCode(), false);
            }
            public void keyTyped(KeyEvent ke) {}
        });

        this.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent me) {}
            public void mousePressed(MouseEvent me) {
                input.setMouse(me.getLocationOnScreen(), me.getButton(), true);
            }
            public void mouseReleased(MouseEvent me) { input.setMouse(me.getLocationOnScreen(), me.getButton(), false); }
            public void mouseEntered(MouseEvent me) {}
            public void mouseExited(MouseEvent me) {}
        });

        this.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent arg0) {
                log.print("Focus Gained", Logfile.INFO);
            }

            public void focusLost(FocusEvent arg0) {
                input.releaseAllKeys();
                log.print("Focus Lost", Logfile.INFO);
            }
        });

        frame.addComponentListener(new ComponentListener() { //resize listener
            public void componentResized(ComponentEvent e) {
                Dimension d = frame.getSize();
                Insets insets = frame.getInsets();
                HEIGHT = (d.height - insets.top - insets.bottom) / SCALE;
                WIDTH = (d.width - insets.right - insets.left) / SCALE;
                frameBuffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
                mainCam.setSize(WIDTH, HEIGHT);
                log.print("Screen Resized: " + WIDTH + "x" + HEIGHT, Logfile.INFO);
            }

            public void componentHidden(ComponentEvent e) {}
            public void componentMoved(ComponentEvent e) {}
            public void componentShown(ComponentEvent e) {}

        });

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                stop();
            }
        });
    }

    private void start() {
        running = true;
        new Thread(this).start();
    }

    private void stop() {
        log.print("Stopping.. ", Logfile.INFO);

        //world.save();

        running = false;
        log.print("Process Terminated", Logfile.INFO);
        log.close();

    }

    public void run() {
        long MillisPerFrame = 16;   // Minimum time for Frame

        requestFocus();

        while(running) {

            long lastTime = System.currentTimeMillis();

            this.tick();
            this.render();

            long now = System.currentTimeMillis();
            long sleepTime = MillisPerFrame - now + lastTime;

            if (sleepTime > 0) {
                fps = 60;
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                fps = (int) (1000 / (16 - sleepTime));
                log.print( "FPS: " + fps, Logfile.FINE);
                //System.out.println("Sleeptime = " + sleepTime);
            }
        }
    }

    private void tick() {
        // Game Logic

        if (hasFocus()) {
            mainCam.tick(input);
            world.tick(input);
            input.tick();
        }
    }

    private void render() {
        // Game Appearance

        Graphics g = frameBuffer.getGraphics();
        // Paint Buffered Frame

        // Fill Background
        setBackground(Color.WHITE);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0,0,WIDTH,HEIGHT);

        // Test Graphics
        g.setColor(Color.YELLOW);
        g.drawOval(0,0,WIDTH - 1,HEIGHT - 1);

        // Draw Game World
        world.render(g, mainCam);

        // Debug Information
        if (debug) {
            DecimalFormat df=new DecimalFormat("0.00");
            g.setColor(Color.WHITE);
            g.drawString("fps: " + fps, 2, 12);
            g.drawString("mouse: " + df.format(input.mouse.x) + ", " + df.format(input.mouse.y), 2, 24);
            //g.drawString("cam: " + df.format(mainCam.p.x) + ", " + df.format(mainCam.p.y), 2, 36);

        }

        try {  // Paint Frame to Canvas
            g = this.getGraphics();
            g.drawImage(frameBuffer, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, 0, 0, WIDTH, HEIGHT, null);
            g.dispose();
        } catch (Throwable e) {
            log.print( "Unable to Render Frame!!", Logfile.SEVERE);
        }
    }


    public static void main(String[] args) {
        Game game = new Game();
        game.setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        game.setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        game.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));

        JFrame frame = new JFrame(Game.NAME);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(game, BorderLayout.CENTER);
        frame.pack();
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        game.init(frame);
        game.start();
    }

}

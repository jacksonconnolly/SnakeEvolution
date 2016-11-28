package connolly.jackson;

/**
 * Interface for game log files
 * Created by Jackson on 11/3/16.
 */

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logfile {

    public static final int SEVERE = 5;
    public static final int WARNING = 4;
    public static final int INFO = 3;
    public static final int CONFIG = 2;
    public static final int FINE = 1;
    public static final int FINER = 0;

    private BufferedWriter log = null;
    private int threshhold = 0;

    // Create new logfile for game info
    public Logfile(Game g, int t) {
        log = null;
        threshhold = t;

        // Get base directory name
        String dirpath;
        try {
            File dir = new File(".");
            dirpath = dir.getCanonicalPath() + File.separator + "logs" + File.separator;
        } catch (IOException e) {
            System.out.println("Unable to Locate Base Directory");
            System.out.println("Unable to Create Logfile");
            return;
        }

        // Generate Unique Logfile Name
        // Note: after 200 log files in 1 day the oldest logfile will be overwritten
        String filepath = "";
        String date = new SimpleDateFormat("yy-MM-dd-").format(new Date());
        for ( int i = 0; i < 200; i++) {
            filepath = dirpath + "" + date + i;
            File f = new File(filepath);
            if(!f.exists()) { break; }
        }

        // Open Write Stream
        try {
            FileWriter fstream = new FileWriter(filepath, true); //true tells to append data.
            log = new BufferedWriter(fstream);
            log.write("--- " + filepath + " ---\n");
            System.out.println("Logfile Created: " + filepath);
        }
        catch (IOException e) { System.out.println("Unable to Create Logfile");  }
    }

    // Output String to Logfile
    public void print(String s, int priority) {
        String timeStamp = new SimpleDateFormat("] [HH.mm.ss] ").format(new Date());

        if (priority >= threshhold) {
            System.out.println(s);
        }
        try {
            log.write("\n[" + priority + timeStamp + s);
        }
        catch (IOException e) { System.out.println("Logfile Write Error"); }
    }

    // Cleanup Operations
    public void close() {
        try {
            if (log != null) {
                log.close();
                System.out.println("Logfile Closed");
            } else {
                System.out.println("Logfile Null Error");
            }
        }
        catch (IOException e) {
            System.out.println("Logfile Cleanup Error");
        }
    }
}

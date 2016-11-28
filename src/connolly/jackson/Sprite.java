package connolly.jackson;

/**
 * Image Loading Utility Class
 * Created by root on 8/9/16.
 */

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Sprite {
    public static BufferedImage load(String name) throws IOException {
        try {
            File dir = new File(".");
            String path = dir.getCanonicalPath() + File.separator + name;//+ "\\res\\sprites\\" + name;
            System.out.println("read: " + path);
            BufferedImage org = ImageIO.read(new File(path));
            BufferedImage res = new BufferedImage(org.getWidth(), org.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics g = res.getGraphics();
            g.drawImage(org, 0, 0, null, null);
            g.dispose();
            return res;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public static void save(BufferedImage src, String name) throws IOException {
        try {
            File dir = new File(".");
            String path = dir.getCanonicalPath() + File.separator + name;//+ "\\res\\sprites\\" + name;
            System.out.println("saved: res\\sprites\\" + name);
            ImageIO.write(src, "png", new File(path));
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public static BufferedImage[][] split(BufferedImage src, int xs, int ys) {
        int xSlices = src.getWidth() / xs;
        int ySlices = src.getHeight() / ys;
        BufferedImage[][] res = new BufferedImage[xSlices][ySlices];
        for (int x = 0; x < xSlices; x++) {
            for (int y = 0; y < ySlices; y++) {
                res[x][y] = new BufferedImage(xs, ys, BufferedImage.TYPE_INT_ARGB);
                Graphics g = res[x][y].getGraphics();
                g.drawImage(src, -x * xs, -y * ys, null);
                g.dispose();
            }
        }
        return res;
    }

}

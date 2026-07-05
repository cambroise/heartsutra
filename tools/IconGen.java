import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Builds the adaptive-icon foreground from a source hanko image
 * (~/Pictures/shin.png): white background is knocked out to transparent so the
 * paper-coloured icon background shows through the seal, and the seal is scaled
 * to sit inside the adaptive-icon safe zone so no mask clips the brush ring.
 */
public class IconGen {
    static final String RES = "/Users/cambroise/Documents/MyLife/codes/heartsutra/app/src/main/res";
    static final String SRC = System.getProperty("user.home") + "/Pictures/shin.png";
    static final String[] BUCKETS = {"mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi"};
    static final int[] SIZES = {108, 162, 216, 324, 432};   // 108dp foreground per density
    static final double SEAL_DP = 84.0;                      // seal diameter within the 108dp canvas

    public static void main(String[] args) throws Exception {
        BufferedImage seal = cropToInk(knockoutWhite(ImageIO.read(new File(SRC))));
        for (int i = 0; i < BUCKETS.length; i++) {
            render(seal, SIZES[i], new File(RES + "/mipmap-" + BUCKETS[i] + "/ic_launcher_foreground.png"));
        }
        System.out.println("done");
    }

    /** Turn white/near-white into transparency; keep the red ink (incl. brush texture). */
    static BufferedImage knockoutWhite(BufferedImage in) {
        int w = in.getWidth(), h = in.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int p = in.getRGB(x, y);
                int r = (p >> 16) & 0xFF, g = (p >> 8) & 0xFF, b = p & 0xFF;
                // Ink is red: alpha driven by how little green/blue there is.
                int alpha = 255 - Math.min(g, b);
                out.setRGB(x, y, (alpha << 24) | (r << 16) | (g << 8) | b);
            }
        }
        return out;
    }

    /** Crop to a centered square around the bounding box of non-transparent pixels. */
    static BufferedImage cropToInk(BufferedImage in) {
        int w = in.getWidth(), h = in.getHeight();
        int minX = w, minY = h, maxX = 0, maxY = 0;
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                if (((in.getRGB(x, y) >>> 24) & 0xFF) > 24) {
                    if (x < minX) minX = x; if (x > maxX) maxX = x;
                    if (y < minY) minY = y; if (y > maxY) maxY = y;
                }
        int cx = (minX + maxX) / 2, cy = (minY + maxY) / 2;
        int side = Math.max(maxX - minX, maxY - minY);
        int half = side / 2 + 2;
        int x0 = Math.max(0, cx - half), y0 = Math.max(0, cy - half);
        int x1 = Math.min(w, cx + half), y1 = Math.min(h, cy + half);
        return in.getSubimage(x0, y0, x1 - x0, y1 - y0);
    }

    static void render(BufferedImage seal, int size, File out) throws Exception {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double target = SEAL_DP / 108.0 * size;
        double off = (size - target) / 2.0;
        g.drawImage(seal, (int) Math.round(off), (int) Math.round(off),
                (int) Math.round(target), (int) Math.round(target), null);
        g.dispose();

        out.getParentFile().mkdirs();
        ImageIO.write(img, "png", out);
        System.out.println("wrote " + out.getName() + " @" + size + "px");
    }
}

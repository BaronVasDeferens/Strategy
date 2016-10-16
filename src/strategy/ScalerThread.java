package strategy;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by skot on 10/14/16.
 */
enum Hint {
    SPEED, QUALITY, BALANCE
}

public class ScalerThread extends Thread {

    private BufferedImage image;
    public BufferedImage scaledImage;
    private float scale;
    Hint hint;

    public ScalerThread (BufferedImage image, float scale, Hint hint) {

        this.image = image;
        this.scale = scale;
        this.hint = hint;
    }

    @Override
    public void run() {
        int width = (int)(image.getWidth() * scale);
        int height = (int)(image.getHeight() * scale);
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.drawImage(image,0,0,width, height, null);
        g.dispose();
        scaledImage = newImage;
    }
}

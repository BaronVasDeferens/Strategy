package strategy;

import java.awt.image.BufferedImage;


public class Entity {
    int x, y;
    Hex hex;
    BufferedImage image;

    public Entity(BufferedImage image) {
        this.image = image;
        x = 0;
        y = 0;
    }

    public Entity(BufferedImage image, int x, int y) {
        this.image = image;
        this.x = x;
        this.y = y;
    }

}

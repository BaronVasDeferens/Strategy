package strategy;

import com.sun.xml.internal.org.jvnet.mimepull.MIMEConfig;

/**
 * Created by skot on 10/3/16.
 */
public enum ScaleFactor {
    // level, hexSize, lineThickness
    MINIMUM     (0, 20, 1.0f),
    ONE         (1, 40, 2.5f),
    TWO         (2, 80, 3.0f),
    THREE       (3, 100, 3.5f),
    FOUR        (4, 120, 4.0f),
    MAXIMUM     (5, 150, 6.0f);

    public int hexSize;
    public int sequence;
    public float strokeThickness;

    ScaleFactor(int sequence, int hexSize, float strokeThickness) {
        this.sequence = sequence;
        this.hexSize = hexSize;
        this.strokeThickness = strokeThickness;
    }

    public ScaleFactor increase () {
        switch (sequence) {
            case 0:
                return ONE;
            case 1:
                return TWO;
            case 2:
                return THREE;
            case 3:
                return FOUR;
            case 4:
                return MAXIMUM;
            default:
                return MAXIMUM;
        }
    }

    public ScaleFactor decrease () {
        switch (sequence) {
            case 0:
                return MINIMUM;
            case 1:
                return MINIMUM;
            case 2:
                return ONE;
            case 3:
                return TWO;
            case 4:
                return THREE;
            case 5:
                return FOUR;
            default:
                return MINIMUM;
        }
    }
}

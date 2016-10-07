package strategy;

public enum ScaleFactor {



    // level, hexSize, lineThickness
    MINIMUM     (0, 34,  3.0f),
    ONE         (1, 100, 4.0f),
    TWO         (2, 132, 6.0f),
    THREE       (3, 150, 7.5f),
    FOUR        (4, 160, 8.0f),
    MAXIMUM     (5, 200, 10.0f);

    public int hexSize;
    public int sequence;
    public float strokeThickness;
    static int rows, cols;
    static int MAX_IMAGE_WIDTH;

    ScaleFactor(int sequence, int hexSize, float strokeThickness) {
        this.sequence = sequence;
        this.hexSize = hexSize;
        this.strokeThickness = strokeThickness;
    }

    ScaleFactor (int sequence, float strokeThickness) {
        this.sequence = sequence;
        this.strokeThickness = strokeThickness;
        //this.hexSize = (int) ((4 * imageSize) / 7 * cols);
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getHexSizeForCols(int imageSize) {
        return (int) ((4 * imageSize) / 7 * cols); // 26 columns
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

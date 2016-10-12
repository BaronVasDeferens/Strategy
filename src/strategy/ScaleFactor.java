package strategy;

class ScaleFactor {

    private static int hexSize;
    private static float scaleFactor;
    private static float strokeThickness;

    private static int currentSequence = 5;
    private static final int MAX_HEX_SIZE = 0;
    private static final float MAX_STROKE = 10.0f;

    private static final int MAX_SEQ = 5;
    private static final int MIN_SEQ = 1;

    private static int screenMinWidth;
    private static int screenMinHeight;
    private static int maxMapWidth = 0;
    private static int maxMapHeight = 0;
    private static int cols;


    public ScaleFactor(HexMap hexMap, int screenMinWidth, int screenMinHeight, int maxMapWidth, int maxMapHeight) {
        ScaleFactor.screenMinWidth = screenMinWidth;
        ScaleFactor.screenMinHeight = screenMinHeight;
        ScaleFactor.maxMapWidth = maxMapWidth;
        ScaleFactor.maxMapHeight = maxMapHeight;
        ScaleFactor.cols = hexMap.cols;

        ScaleFactor.scaleFactor = (currentSequence * 1.0f) / MAX_SEQ * 1.0f;

        getHexSize();
        getStrokeThickness();

        System.out.println("ScaleFactor initialized!!!");
        prnt();
    }


    public void increase() {

        if ((currentSequence+1) > MAX_SEQ) {
            System.out.println("NO INCREASE : " + currentSequence + "/" + MAX_SEQ);
            return;
        }


        System.out.println("inc");
        ScaleFactor.currentSequence++;
        ScaleFactor.strokeThickness += 1.25f;
        ScaleFactor.scaleFactor = (currentSequence * 1.0f) / MAX_SEQ * 1.0f;
        ScaleFactor.hexSize = 4 * getMapWidth() / (7 * cols);
        prnt();
    }

    public void decrease() {
        if ((currentSequence-1) < MIN_SEQ) {
            System.out.println("NO DECREASE : " + currentSequence + "/" + MAX_SEQ);
            return;
        }


        System.out.println("dec");
        ScaleFactor.currentSequence--;
        ScaleFactor.strokeThickness -= 1.25f;
        ScaleFactor.scaleFactor = (currentSequence * 1.0f) / MAX_SEQ * 1.0f;
        ScaleFactor.hexSize = 4 * getMapWidth() / (7 * cols);
        prnt();
    }

    public int getMapWidth() {

        int mapHeight = (int)(maxMapHeight * scaleFactor);
        int mapWidth = (int)(maxMapWidth * scaleFactor);

        if ((mapHeight >= maxMapHeight) || (mapWidth >= maxMapWidth))
            return maxMapWidth;
        else if ((mapHeight <= screenMinHeight) || (mapWidth <= screenMinWidth))
            return mapWidth + (int) (((Math.abs(screenMinWidth - mapWidth) / 1.0f)));
        else
            return mapWidth;
    }

    public int getMapHeight() {
        int mapHeight = (int)(maxMapHeight * scaleFactor);
        int mapWidth = (int)(maxMapWidth * scaleFactor);

        if ((mapHeight >= maxMapHeight) || (mapWidth >= maxMapWidth))
            return maxMapHeight;
        else if ((mapHeight <= screenMinHeight) || (mapWidth <= screenMinWidth))
            return mapHeight + (int) (((Math.abs(screenMinHeight - mapHeight)) / 1.0f));
        else
            return mapHeight;
    }




    public int getHexSize() {
        ScaleFactor.hexSize = 4 * getMapWidth() / (7 * cols);
        return hexSize;
    }

    public float getScaleFactor() {
        return ScaleFactor.scaleFactor;
    }

    public float getStrokeThickness() {
        ScaleFactor.strokeThickness = MAX_STROKE * scaleFactor;
        return strokeThickness;
    }

    private void prnt() {
        System.out.println(">>>>> <<<<<<");
        System.out.println("seq: " + currentSequence + "/" + MAX_SEQ);
        System.out.println("scale: " + scaleFactor);
        System.out.println("currentBkrnd: " + getMapWidth() + "x" + getMapHeight());
        System.out.println("hx: " + hexSize);
        System.out.println("stroke: " + strokeThickness);
    }

}

package strategy;

/**
 * Created by skot on 10/8/16.
 */
public class testo {

    public static void main (String ... args) {

        final int width = 1000;
        final int height = 850;

        final int MIN_WIDTH = 300;
        final int MIN_HEIGHT = 250;


        float scale = 1.0f;

        while (scale > 0.0) {

            System.out.print(scale + " : ");

            if ( ((width * scale) > MIN_WIDTH) && ((height * scale) > MIN_HEIGHT))
                System.out.println(width * scale + "x" + height * scale);
            else
                System.out.println(MIN_WIDTH + "x" + MIN_HEIGHT);

            scale -= 0.1f;

        }




    }

}

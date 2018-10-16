import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;

public class Canny{

    BufferedImage image;
    private static final float[] sobel1 = { 1.0f, 0.0f, -1.0f};
    private static final float[] sobel2 = { 1.0f, 2.0f,  1.0f};
    private static final boolean[] sobelBoolean = {true, false};

    public Canny(){
//        int[] filter = {1, 2, 1, 2, 4, 2, 1, 2, 1};
//        int filterWidth = 3;
        int[] filter = {1,4,7,4,1,4,16,26,16,4,7,26,41,26,7,4,16,26,16,4,1,4,7,4,1};
        int filterWidth = 5;
        try{
            image = ImageIO.read(new File("IMG_1269.jpg"));
            image = grayScale();
            image = blur(image, filter, filterWidth);

            File outputfile = new File("saved2.png");
            ImageIO.write(image, "png", outputfile);
        }catch (IOException e){System.out.print(e);}
    }

    public BufferedImage sobelFilter() {
        int[][] filterGx = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] filterGy = {{1, 2, 1}, {0, 0, 0}, {-1, 2, 1}};
        return null;
    }


    public BufferedImage blur(BufferedImage image, int[] filter, int filterWidth) {
        int width = image.getWidth();
        int height = image.getHeight();
        int sum = IntStream.of(filter).sum();

        int[] input = image.getRGB(0, 0, width, height, null, 0, width);
        int[] output = new int[input.length];

        int pixelIndexOffset = width - filterWidth;
        int centerOffsetX = filterWidth / 2;
        int centerOffsetY = filter.length / filterWidth / 2;

        for (int h = height - filter.length / filterWidth + 1, w = width - filterWidth + 1, y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int r = 0;
                int g = 0;
                int b = 0;
                for (int filterIndex = 0, pixelIndex = y * width + x; filterIndex < filter.length; pixelIndex += pixelIndexOffset) {
                    for (int fx = 0; fx < filterWidth; fx++, pixelIndex++, filterIndex++) {
                        int col = input[pixelIndex];
                        int factor = filter[filterIndex];

                        r += ((col >>> 16) & 0xFF) * factor;
                        g += ((col >>> 8) & 0xFF) * factor;
                        b += (col & 0xFF) * factor;
                    }
                }
                if(sum <= 0){
                    r = 0;
                    g = 0;
                    b = 0;
                }else {
                    r /= sum;
                    g /= sum;
                    b /= sum;
                }

                output[x + centerOffsetX + (y + centerOffsetY) * width] = (r << 16) | (g << 8) | b | 0xFF000000;
            }
        }

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        result.setRGB(0, 0, width, height, output, 0, width);
        return result;
    }

    public BufferedImage grayScale(){

        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                Color c = new Color(image.getRGB(j, i));
                int red = (int) (c.getRed() * 0.299);
                int green = (int) (c.getGreen() * 0.587);
                int blue = (int) (c.getBlue() * 0.114);
                Color newColor = new Color(
                        red + green + blue,
                        red + green + blue,
                        red + green + blue);
                image.setRGB(j, i, newColor.getRGB());
            }
        }
        return image;
    }

    public static void main(String[] arguments){
        new Canny();
    }

}




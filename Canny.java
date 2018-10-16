import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;

public class Canny{

    BufferedImage image;

    public Canny(){
//        int[] filter = {1, 2, 1, 2, 4, 2, 1, 2, 1};
//        int filterWidth = 3;
        int[] filter = {1,4,7,4,1,4,16,26,16,4,7,26,41,26,7,4,16,26,16,4,1,4,7,4,1};
        int filterWidth = 5;
        try{
            image = ImageIO.read(new File("IMG_1269.jpg"));
            image = grayScale();
            image = blur(image, filter, filterWidth);
            image = sobelFilter();
            File outputfile = new File("saved2.png");
            ImageIO.write(image, "png", outputfile);
        }catch (IOException e){System.out.print(e);}
    }

    public BufferedImage sobelFilter(){
        int[][] filterGx = {{-1,0,1},{-2,0,2},{-1,0,1}};
        int[][] filterGy = {{1,2,1},{0,0,0},{-1,2,1}};

        BufferedImage result = image;

        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 1; y < height-1; y++) {
            for (int x=1; x < width-1; x++) {
                Color a = new Color(image.getRGB(x - 1, y - 1));
                Color b = new Color(image.getRGB(x, y - 1));
                Color c = new Color(image.getRGB(x + 1, y - 1));
                Color d = new Color(image.getRGB(x - 1, y));
                Color e = new Color(image.getRGB(x, y));
                Color f = new Color(image.getRGB(x + 1, y));
                Color g = new Color(image.getRGB(x - 1, y + 1));
                Color h = new Color(image.getRGB(x, y + 1));
                Color i = new Color(image.getRGB(x + 1, y + 1));

                double pixel_x = (filterGx[0][0] * a.getRed()) + (filterGx[0][1] * b.getRed()) + (filterGx[0][2] * c.getRed()) +
                        (filterGx[1][0] * d.getRed()) + (filterGx[1][1] * e.getRed()) + (filterGx[1][2] * f.getRed()) +
                        (filterGx[2][0] * g.getRed()) + (filterGx[2][1] * h.getRed()) + (filterGx[2][2] * i.getRed());
                double pixel_y =
                        (filterGy[0][0] * a.getRed()) + (filterGx[0][1] * b.getRed()) + (filterGx[0][2] * c.getRed()) +
                                (filterGy[1][0] * d.getRed()) + (filterGx[1][1] * e.getRed()) + (filterGx[1][2] * f.getRed()) +
                                (filterGy[2][0] * g.getRed()) + (filterGx[2][1] * h.getRed()) + (filterGx[2][2] * i.getRed());

                int val = (int)Math.sqrt((pixel_x * pixel_x) + (pixel_y * pixel_y));
                if(val < 0) {
                    val = 0;
                }
                if(val > 255) {
                    val = 255;
                }

                result.setRGB(x,y,val);
            }
        }
        return result;
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




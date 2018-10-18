import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class Canny{

    BufferedImage image;
    double threshold1 = 1.0;
    double threshold2 = 0.3;

    public Canny(){
//        int[] filter = {1, 2, 1, 2, 4, 2, 1, 2, 1};
//        int filterWidth = 3;
        int[] filter = {1,4,7,4,1,4,16,26,16,4,7,26,41,26,7,4,16,26,16,4,1,4,7,4,1};
        int[][] filterGx = {{-1,0,1},{-2,0,2},{-1,0,1}};
        int filterWidth = 5;
        try{
            image = ImageIO.read(new File("butters.png"));
            image = grayScale();
            image = blur(image, filter, filterWidth);
            image = sobelFilter(filterGx);
            CannyAlgor();
            File outputfile = new File("saved2.jpg");
            ImageIO.write(image, "jpg", outputfile);
        }catch (IOException e){System.out.print(e);}
    }

    public void CannyAlgor(){

        int[][] cord = new int[image.getHeight()][image.getWidth()];

        int white = new Color(255,255,255).getRed();

        for(int r=0; r<image.getHeight(); r++){
            for(int c=0; c<image.getWidth(); c++){
                int cl = new Color(image.getRGB(c,r)).getRed();
                if (cl > threshold1) {
                    int maxX = c;
                    int maxY = r;
                    int max = 0;
                    for(int r2 = -1; r2 <= 1; r2++ ) {
                        for (int c2 = -1; c2 <= 1; c2++) {
                            if(r+r2 > 0 && r+r2 < image.getHeight() && c+c2 > 0 && c+c2 < image.getWidth()) {
                                int cl2 = new Color(image.getRGB(c + c2, r + r2)).getRed();
                                if (cl2 > max) {
                                    max = cl2;
                                    maxX = c + c2;
                                    maxY = r + r2;
                                }
                            }
                        }
                    }
                    if (max < threshold2*white) {
                        image.setRGB(maxX, maxY, 0);
                    }else{
                        image.setRGB(maxX, maxY, 0);
                        cord[maxY][maxX] = 1;
                    }
                }
            }
        }
        for(int r=0; r<image.getHeight(); r++) {
            for (int c = 0; c < image.getWidth(); c++) {
                if(cord[r][c] == 1){
                    image.setRGB(c,r,new Color(255,255,255).getRGB());
                }else{
                    image.setRGB(c,r,new Color(0).getRGB());
                }
            }
        }
    }

    public BufferedImage sobelFilter(int[][] filter){

        BufferedImage result = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_RGB);

        for(int i=1;i<image.getWidth()-1;i++){
            for(int j=1;j<image.getHeight()-1;j++){
                filter[0][0]=new Color(image.getRGB(i-1,j-1)).getRed();
                filter[0][1]=new Color(image.getRGB(i-1,j)).getRed();
                filter[0][2]=new Color(image.getRGB(i-1,j+1)).getRed();
                filter[1][0]=new Color(image.getRGB(i,j-1)).getRed();
                filter[1][2]=new Color(image.getRGB(i,j+1)).getRed();
                filter[2][0]=new Color(image.getRGB(i+1,j-1)).getRed();
                filter[2][1]=new Color(image.getRGB(i+1,j)).getRed();
                filter[2][2]=new Color(image.getRGB(i+1,j+1)).getRed();

                int gy=(filter[0][0]*-1)+(filter[0][1]*-2)+(filter[0][2]*-1)+(filter[2][0])+(filter[2][1]*2)+(filter[2][2]);
                int gx=(filter[0][0])+(filter[0][2]*-1)+(filter[1][0]*2)+(filter[1][2]*-2)+(filter[2][0])+(filter[2][2]*-1);

                int edge=(int)Math.sqrt(Math.pow(gy,2)+Math.pow(gx,2));
                result.setRGB(i,j,(edge<<16 | edge<<8 | edge));
            }
        }
        return result;
    }

    public BufferedImage blur(BufferedImage image, int[] filter, int filterWidth) {
        int width = image.getWidth();
        int height = image.getHeight();
        int sum = IntStream.of(filter).sum();

        int[] input = image.getRGB(0, 0, width, height, null, 0, width);

        BufferedImage result = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_ARGB);

        int pixelIndexOffset = width - filterWidth;

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

                result.setRGB(x,y,((r << 16) | (g << 8) | b | 0xFF000000));
            }
        }

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




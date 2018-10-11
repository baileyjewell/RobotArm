import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.io.*;

public class ScanImage {

    int rows;
    int cols;

    Color[][] image;

    public ScanImage() {
    }

    public void loadData(String fileName){
        File file = new File(fileName);
        try {
            Scanner scan = new Scanner(file);
            while(scan.hasNext()){

            }
        }catch(Exception e){ System.out.print(e.getMessage());}
    }

    public void loadImage(String imageName) {
        try {
            BufferedImage img = ImageIO.read(new File(imageName));
            rows = img.getHeight();
            cols = img.getWidth();
            Color[][] image = new Color[rows][cols];
            for (int row = 0; row < rows; row++){
                for (int col = 0; col < cols; col++){
                    Color c = new Color(img.getRGB(col, row));
                    image[row][col] = c;
                }
            }
        } catch(IOException e){System.out.println("Image reading failed: "+e);}
    }

    public void getImageSize(String fileName){
        File file = new File(fileName);

    }

    public static void main(String[] arguments){
        new ScanImage();
    }

}
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileConvertToImage {

    public void getNormalImage(String[] result, String name) throws IOException {
        BufferedImage image = new BufferedImage(
                Integer.parseInt(result[0]),
                Integer.parseInt(result[1]),
                Integer.parseInt(result[3]));
        File fileTxt = new File(result[2]);
        FileReader fileReader = new FileReader(fileTxt);

        int width = Integer.parseInt(result[0]);
        int height = Integer.parseInt(result[1]);
        char[] a = new char[width*height*3*9];
        fileReader.read(a);
        String fileDate = String.copyValueOf(a);
        String[] rgbArray = fileDate.split("\n");

        for (int i = 0, k = 0, x = 0, y = 0; i < (width*height); i++, k += 3) {
            if (y == height) {
                y = 0;
                x++;
            }
            int red = Integer.parseInt(rgbArray[k], 2);
            int green = Integer.parseInt(rgbArray[k+1], 2);
            int blue = Integer.parseInt(rgbArray[k+2], 2);
            Color newColor = new Color(red, green, blue);
            image.setRGB(x, y, newColor.getRGB());
            y++;
        }

        File output = new File(name + "New" + "." + "jpg");
        ImageIO.write(image, "jpg", output);

        fileReader.close();
     }
}

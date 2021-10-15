import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileConvertToTxt {

    public String[] getDecimalImage(String imageFullName, String name) throws IOException {
        File file = new File(imageFullName);
        BufferedImage image = ImageIO.read(file);
        File fileTxt = new File(name + ".txt");
        fileTxt.createNewFile();
        FileWriter writer = new FileWriter(fileTxt);

        String[] result = new String[4];
        result[0] = String.valueOf(image.getWidth());
        result[1] = String.valueOf(image.getHeight());
        result[2] = name + ".txt";
        result[3] = String.valueOf(image.getType());

        for (int x = 0; x < image.getWidth(); x++)
            for (int y = 0; y < image.getHeight(); y++) {
                int clr = image.getRGB(x, y);
                int red = (clr & 0x00ff0000) >> 16;
                int green = (clr & 0x0000ff00) >> 8;
                int blue = clr & 0x000000ff;

                printBinaryform(red, writer);
                printBinaryform(green, writer);
                printBinaryform(blue, writer);
            }

        writer.flush();
        writer.close();
        return result;
    }

    private static String printBinaryform(int number, FileWriter writer) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(Integer.toBinaryString(number));

        while (buffer.length() < 8)
            buffer.insert(0,"0");

        buffer.insert(8, "\n");

        try {
            writer.write(buffer.toString());
        } catch (IOException exception) {
            System.out.println("Неможливо записати з-ня у файл");
        }

        return buffer.toString();
    }
}

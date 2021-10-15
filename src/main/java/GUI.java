import com.github.sarxos.webcam.Webcam;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

public class GUI extends JDialog {
    private JPanel contentPane;
    private JButton captureButton;
    private JLabel imageHolder;
    private JTextField imageName;
    private JComboBox<String> comboBox;
    private JComboBox<Webcam> comboBox1;
    private JButton startRecordButton;
    private JButton stopRecordButton;
    private JTextField textField1;

    List<Webcam> webcams;
    Webcam webcam;
    private Boolean isRunning = false;
    private VideoFeed feed = new VideoFeed();
    Record record = new Record();

    private static String imageFullName;
    private static String name;


    public GUI() throws LineUnavailableException {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(captureButton);

        webcams = Webcam.getWebcams();

        comboBox.addItem("JPG");
        comboBox.addItem("JPEG");
        comboBox.addItem("PNG");

        if (!isRunning) {
            isRunning = true;
            feed.start();
        } else isRunning = false;

        transform();
    }

    public static void main(String[] args) throws LineUnavailableException {
        GUI dialog = new GUI();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void transform() {
        capture();
//        recordAudio();
        StartRecord();
        StopRecord();
    }

    private void capture() {
        captureButton.addActionListener(e -> {
            name = imageName.getText();

            File f = new File("Results" + "\\" + name);
            FileConvertToTxt fileConvertToTxt = new FileConvertToTxt();
            FileConvertToImage fileConvertToImage = new FileConvertToImage();
            String format = comboBox.getItemAt(comboBox.getSelectedIndex());

            f.mkdir();
            String path = "Results" + "\\" + name + "\\";

            String[] date = {path + name, format.toLowerCase(Locale.ROOT)};
            imageFullName = path + name + "." + format.toLowerCase(Locale.ROOT);
            try {
                ImageIO.write(comboBox1.getItemAt(comboBox1.getSelectedIndex()).getImage(), format, new File(imageFullName));
            } catch (IOException exception) {
                System.out.println("Cant take a photo!");
            }

            String[] result;
            try {
                result = fileConvertToTxt.getDecimalImage(imageFullName, date[0]);
                fileConvertToImage.getNormalImage(result, date[0]);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
    }

    class VideoFeed extends Thread {
        @Override
        public void run() {
            for (Webcam web : webcams) {
                comboBox1.addItem(web);
            }
            while (isRunning) {
                webcam = comboBox1.getItemAt(comboBox1.getSelectedIndex());
                webcam.open();

                try {
                    Image image = webcam.getImage();
                    imageHolder.setIcon(new ImageIcon(image));
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    System.out.println("Video is interrupted!");
                }
            }
        }
    }

    private void StartRecord() {
        startRecordButton.addActionListener(e -> {
            record.startRecord(textField1);
        });
    }

    private void StopRecord() {
        stopRecordButton.addActionListener(e -> {
            record.stopRecord();
        });
    }

    static class Record {
        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
        DataLine.Info dataInfo = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(dataInfo);
        Thread audioRecordThread;

        Record() throws LineUnavailableException {

        }

        public void startRecord(JTextField textField1) {
            audioRecordThread = new Thread() {
                @Override
                public void run() {
                    AudioInputStream recordingStream = new AudioInputStream(targetDataLine);
                    File createFolder = new File("AudioResult" + File.separator + textField1.getText());
                    createFolder.mkdir();
                    File outputFileWav = new File(createFolder.toString() + File.separator + textField1.getText() +  ".wav");
                    File outputFileTxt = new File(createFolder.toString() + File.separator + textField1.getText() +  ".txt");
                    try {
                        AudioSystem.write(recordingStream, AudioFileFormat.Type.WAVE, outputFileWav);
                    } catch (IOException e) {
                        System.out.println(e);
                    }

                    try {
                        byte[] bytes = Files.readAllBytes(Paths.get(outputFileWav.getPath()));
                        FileOutputStream out = new FileOutputStream(outputFileTxt);
                        out.write(bytes);
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("піздец");
                    }
                }
            };

            try {
                if (!AudioSystem.isLineSupported(dataInfo))
                    System.out.println("Not supported");

                targetDataLine.open();
                targetDataLine.start();

                audioRecordThread.start();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        public void stopRecord() {
            targetDataLine.stop();
            targetDataLine.close();
        }
    }
}
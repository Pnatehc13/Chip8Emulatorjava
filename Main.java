import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
        Chip8Emulator chip8 = new Chip8Emulator();
        chip8.loadfontset(chip8.fontset);
        Chip8Display screen = new Chip8Display(chip8.getdisplay(),chip8.getkeys());
        JFrame frame = new JFrame("Chip 8 Emulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(screen);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
        screen.requestFocus();


        byte[] program = Files.readAllBytes(Paths.get("out/production/emulator/Pong.ch8"));
        chip8.loadprogram(program);


        new Thread(() -> {
            chip8.run();
        }).start();
        new Timer(16, e -> {
            screen.updateDisplay(chip8.getdisplay());
        }).start();


    }
}
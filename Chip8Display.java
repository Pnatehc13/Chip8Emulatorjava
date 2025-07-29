import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Chip8Display extends JPanel implements KeyListener {
    private final int SCALE = 10;
    private boolean[][] display;
    private boolean[] keys;

    public Chip8Display(boolean[][] display,boolean[] keys)
    {
        this.display = display;
        setPreferredSize(new Dimension(64 * SCALE, 32 * SCALE));
        setBackground(Color.BLACK);
        this.keys = keys;
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();
    }
    public void updateDisplay(boolean[][] display)
    {
        this.display = display;
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        for (int y = 0; y < 32; y++)
            for (int x = 0; x < 64; x++)
                if (display[x][y])
                    g.fillRect(x * SCALE, y * SCALE, SCALE, SCALE);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int chip8key = mapKey(e.getKeyCode());
        if(chip8key != -1) keys[chip8key] = true;
    }

    private int mapKey(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_1: return 0x1;
            case KeyEvent.VK_2: return 0x2;
            case KeyEvent.VK_3: return 0x3;
            case KeyEvent.VK_4: return 0xC;
            case KeyEvent.VK_Q: return 0x4;
            case KeyEvent.VK_W: return 0x5;
            case KeyEvent.VK_E: return 0x6;
            case KeyEvent.VK_R: return 0xD;
            case KeyEvent.VK_A: return 0x7;
            case KeyEvent.VK_S: return 0x8;
            case KeyEvent.VK_D: return 0x9;
            case KeyEvent.VK_F: return 0xE;
            case KeyEvent.VK_Z: return 0xA;
            case KeyEvent.VK_X: return 0x0;
            case KeyEvent.VK_C: return 0xB;
            case KeyEvent.VK_V: return 0xF;
            default: return -1;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int chip8Key = mapKey(e.getKeyCode());
        if (chip8Key != -1) keys[chip8Key] = false;
    }

    @Override public void keyTyped(KeyEvent e) {}
}

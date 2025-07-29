import java.awt.event.*;
import java.util.Random;

public class Chip8Emulator {
    private byte[] memory = new byte[4096];
    private byte[] V = new byte[16] ;
    private int I =0;
    private int pc = 0x200;
    private boolean waitingForKey = false;
    private int waitingRegister = 0;
    Random rand = new Random();
    byte[] fontset = {
            (byte) 0xF0, (byte) 0x90, (byte) 0x90, (byte) 0x90, (byte) 0xF0, // 0
            (byte) 0x20, (byte) 0x60, (byte) 0x20, (byte) 0x20, (byte) 0x70, // 1
            (byte) 0xF0, (byte) 0x10, (byte) 0xF0, (byte) 0x80, (byte) 0xF0, // 2
            (byte) 0xF0, (byte) 0x10, (byte) 0xF0, (byte) 0x10, (byte) 0xF0, // 3
            (byte) 0x90, (byte) 0x90, (byte) 0xF0, (byte) 0x10, (byte) 0x10, // 4
            (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x10, (byte) 0xF0, // 5
            (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x90, (byte) 0xF0, // 6
            (byte) 0xF0, (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x40, // 7
            (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x90, (byte) 0xF0, // 8
            (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x10, (byte) 0xF0, // 9
            (byte) 0xF0, (byte) 0x90, (byte) 0xF0, (byte) 0x90, (byte) 0x90, // A
            (byte) 0xE0, (byte) 0x90, (byte) 0xE0, (byte) 0x90, (byte) 0xE0, // B
            (byte) 0xF0, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0xF0, // C
            (byte) 0xE0, (byte) 0x90, (byte) 0x90, (byte) 0x90, (byte) 0xE0, // D
            (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x80, (byte) 0xF0, // E
            (byte) 0xF0, (byte) 0x80, (byte) 0xF0, (byte) 0x80, (byte) 0x80  // F
    };




    private int[] Stack = new int[16];
    private int Sp =0 ;

    private byte delayTimer = 0;
    private byte soundTimer = 0;

    private boolean[][] display = new boolean[64][32];
    public boolean[][] getdisplay()
    {
        return display;
    }
    private boolean[] keys = new boolean[16];
    boolean[] getkeys()
    {
        return keys;
    }

    public void loadprogram(byte[] program)
    {
        for(int i=0;i<program.length ; i++)
            memory[0x200 + i] = program[i];
    }

    private int mapToChip8Key(int keyCode) {
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

    public void loadfontset(byte[] fontset)
    {
        for (int i = 0; i < fontset.length; i++) {
            memory[i] = fontset[i];
        }

    }
    public void keyPressed(KeyEvent e) {
        int chip8Key = mapToChip8Key(e.getKeyCode());
        if (chip8Key != -1) {
            keys[chip8Key] = true;

            if (waitingForKey) {
                V[waitingRegister] = (byte) chip8Key;
                waitingForKey = false;
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        int chip8Key = mapToChip8Key(e.getKeyCode());
        if (chip8Key != -1) {
            keys[chip8Key] = false;
        }
    }

    public void startTimers() {
        Thread timerThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000 / 60); // ~16.67 ms (60 Hz)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (delayTimer > 0) delayTimer--;
                if (soundTimer > 0) {
                    soundTimer--;
                    if (soundTimer == 0) {
                        // TODO: Add sound buzzer logic here
                        System.out.println("BEEP!");
                    }
                }
            }
        });

        timerThread.setDaemon(true); // Optional: ends with the app
        timerThread.start();
    }


    public void run()
    {
        startTimers();
        while (true)
        {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            int opcode = ((memory[pc]&0xFF)<<8)|(memory[pc+1]&0xFF);
            pc+=2;
            System.out.printf("Executing opcode: 0x%04X at pc: 0x%04X\n", opcode, pc - 2);
            int nn,nnn, x;

            switch (opcode & 0xF000)
            {
                case 0x0000:
                    x = opcode&0xFF;
                    switch (x)
                    {
                        case 0xE0:
                            for (int i = 0; i < 64; i++) {
                                for (int j = 0; j < 32; j++) {
                                    display[i][j] = false;
                                }
                            }
                            break;
                        case 0xEE:
                            pc = Stack[--Sp];
                            break;
                    }
                    break;
                case 0x1000:
                    pc = opcode & 0xFFF;
                    break;
                case 0x2000:
                    Stack[Sp++] = pc;
                    pc = opcode&0x0FFF;
                    break;
                case 0xC000:
                    x = (opcode & 0x0F00) >> 8;
                    nn = opcode & 0x00FF;
                    V[x] = (byte)(rand.nextInt(256) & nn);  // Use java.util.Random
                    break;

                case 0x6000:
                    x = (opcode & 0x0F00)>>8;
                    nn = opcode & 0x0FF;
                    V[x] = (byte) nn;
                    break;

                case 0x7000:// ADD NN to Vx
                    x = (opcode & 0x0F00)>>8;
                    nn = opcode & 0x0FF;
                    V[x] += (byte) nn;
                    break;
                case 0x8000://Arthemetic and logic
                    x = (opcode & 0x0F00)>>8;
                    int y = (opcode & 0x00F0)>>4;
                    switch (opcode & 0x000F)
                    {
                        case 0x0000:
                            V[x] = V[y];
                            break;
                        case 0x0001:
                            V[x] = (byte) (V[x]|V[y]);
                            break;
                        case 0x0002:
                            V[x] = (byte) (V[x]&V[y]);
                            break;
                        case 0x0003:
                            V[x] = (byte) (V[x]^V[y]);
                            break;
                        case 0x0004:
                            V[0xF] = (byte) (((V[x]+V[y]) > 255 ) ? (1):0);
                            V[x] = (byte) ((V[x]+V[y]) & 0xFF);
                            break;
                        case 0x0005:
                            V[0xF] = (byte)((V[x]>=V[y])? 1:0);
                            V[x] = (byte) ( (V[x]-V[y])&0xFF);
                            break;
                        case 0x0007:
                            V[0xF] = (byte)((V[x]<=V[y])?1:0);
                            V[x] = (byte) ((V[y] - V[x])&0xFF);
                            break;
                        case 0x0006:
                            V[0xF] = (byte)(V[x] & 0x1); // Save LSB before shift
                            V[x] = (byte)((V[x] & 0xFF) >>> 1);
                            break;
                        case 0x000E:
                            V[0xF] = (byte)((V[x] & 0x80) >> 7); // Save MSB before shift
                            V[x] = (byte)(V[x] << 1);
                            break;

                        default:
                            System.out.printf("Unknown opcode: 0x%04X\n", opcode);
                            return;
                    }
                    break;

                case 0xA000:
                    nnn = opcode&0x0FFF;
                    I = nnn;
                    break;

                case 0xF000:
                    nn = (opcode&0x00FF);
                    int a;
                    switch (nn)
                    {
                        case 0x0007:
                            a = (opcode&0x0F00)>>8;
                            V[a] = delayTimer;
                            break;
                        case 0x001E:
                            a = (opcode&0x0F00)>>8;
                            I = (I + (V[a]&0xFF))&0xFFFF;
                            break;
                        case 0x0029:
                            a = (opcode&0x0F00)>>8;
                            I = (V[a] & 0xFF)*5;
                            break;

                        case 0x0033:
                            a = (opcode&0x0F00)>>8;
                            int val = V[a] & 0xFF;
                            memory[I] = (byte) (val/100);
                            memory[I +1 ] = (byte) ((val/10)%10);
                            memory[I+2] = (byte) (val%10);
                            break;

                        case 0x0015:
                            a = (opcode&0x0F00)>>8;
                            delayTimer = V[a];
                            break;
                        case 0x0018:
                            a = (opcode&0x0F00)>>8;
                            soundTimer = V[a];
                            break;


                        case 0x0055:
                            a = (opcode&0x0F00)>>8;
                            for (int i=0;i<=a;i++)
                                memory[I+i] = V[i];
                            break;
                        case 0x0065:
                            a = (opcode&0x0F00)>>8;
                            for (int i=0;i<=a;i++)
                                V[i] = memory[I+i];
                            break;

                        case 0x000A:
                            a = (opcode&0x0F00)>>8;
                            waitingForKey = true;
                            waitingRegister = a;
                            while (waitingForKey) {
                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            break;

                        default:
                            System.out.printf("Unknown opcode: 0x%04X\n", opcode);
                            return;
                    }
                    break;
                case 0xD000: {
                    x = V[(opcode & 0x0F00) >> 8] & 0xFF;
                    y = V[(opcode & 0x00F0) >> 4] & 0xFF;
                    int height = opcode & 0x000F;
                    V[0xF] = 0;

                    for (int row = 0; row < height; row++) {
                        int spriteByte = memory[I + row];

                        for (int col = 0; col < 8; col++) {
                            int spriteBit = (spriteByte >> (7 - col)) & 1;
                            int px = (x + col) % 64;
                            int py = (y + row) % 32;

                            if (spriteBit == 1) {
                                if (display[px][py]) {
                                    V[0xF] = 1; // Collision!
                                }
                                display[px][py] ^= true; // XOR the pixel
                            }
                        }
                    }

                    break;
                }

                case 0xE000:
                    x = (opcode&0x0F00)>>8;
                    nn = opcode&0x00FF;
                    switch (nn)
                    {
                        case 0x009E:
                            if(keys[V[x]])pc+=2;
                            break;
                        case 0x00A1:
                            if (!keys[V[x]])pc+=2;
                            break;
                    }

                    break;


                case 0x5000:
                    x = (opcode & 0x0F00) >> 8;
                    y = (opcode & 0x00F0) >> 4;
                    if ((opcode & 0x000F) == 0) {
                        if (V[x] == V[y]) pc += 2;
                    }
                    break;
                case 0x9000:
                    x = (opcode & 0x0F00) >> 8;
                    y = (opcode & 0x00F0) >> 4;
                    if ((opcode & 0x000F) == 0) {
                        if (V[x] != V[y]) pc += 2;
                    }
                    break;
                case 0x3000:
                    x = (opcode & 0x0F00) >> 8;
                    nn = opcode & 0x00FF;
                    if ((V[x] & 0xFF) == nn) pc += 2;

                    break;

                case 0x4000:
                    x = (opcode & 0x0F00) >> 8;
                    nn = opcode & 0x00FF;
                    if ((V[x] & 0xFF) != nn) pc += 2;

                    break;

                default:
                    System.out.printf("Unknown opcode: 0x%04X\n", opcode);
                    return;
            }
        }
    }

    public Chip8Emulator(){}
}

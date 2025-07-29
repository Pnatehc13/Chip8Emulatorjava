# CHIP-8 Emulator in Java

A fully functional CHIP-8 emulator written in Java. This project emulates a simple virtual machine from the 1970s capable of running classic CHIP-8 games like Pong, Space Invaders, and more.

## 🎮 Features
- 35+ opcode support
- 64×32 monochrome display
- Keyboard input mapped to CHIP-8 keypad
- Delay and sound timers emulated
- Separate CPU and display update threads
- Built with Java Swing (no external dependencies)

## ⌨️ Controls (Mapped to CHIP-8 Keypad)
CHIP-8    Keyboard
keypad    keypad
+-+-+-+-+ +-+-+-+-+
|1|2|3|C| |1|2|3|4|
+-+-+-+-+ +-+-+-+-+
|4|5|6|D| |Q|W|E|R|
+-+-+-+-+ => +-+-+-+-+
|7|8|9|E| |A|S|D|F|
+-+-+-+-+ +-+-+-+-+
|A|0|B|F| |Z|X|C|V|
+-+-+-+-+ +-+-+-+-+

## 🛠 How to Run

1. Clone the repository
2. Compile the `.java` files with any Java 11+ compiler
3. Make sure to place a CHIP-8 ROM (e.g. `Pong.ch8`) in the root directory
4. Run the `Main.java` file

```bash
javac Main.java
java Main



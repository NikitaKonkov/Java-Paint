import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Paint {
    public static int X = 800;
    public static int Y = 800;
    public static int div = 5;
    public static int width = X/div;
    public static int height = Y/div;
    public static int movX = X/div/2;
    public static int movY = height/2;
    public static int size = 1;
    public static float hue = 0f;
    public static float lum = 1f;
    public static int rgb = 1;
    public static int color = HexFormat.fromHexDigits("ffffff");
    public static int m0 = 0;
    public static int m1 = 0;
    public static int m2 = 0;
    public static int[][] copy;
    public static int[][] matrix;
    public int[][] paint = new int[X/div][Y/div];
    public static final Set<Integer> keysPressed = new HashSet<>();
    public static String[] keys = {"W","S","A","D","SPACE","0","1","2","3","4","5","6","7","8","9","Q","E","Y","X","F","R","C","V"};
    public static String[] func = {"PAINT", "FILL", "0", "0","FAST","SLOW"};
    public void space_painter(){
        for (int a = 0; a<size;a++){
            for (int b = 0; b<size;b++){
                paint[movX+a][movY+b] = color;
            }
        }
    }
    public void savingMatrixAsFile(String fileName){
        try (PrintWriter writer = new PrintWriter(fileName)) {
            for (int x = 0; x < height; x++) {
                for (int y = 0; y < width; y++) {
                    writer.print(STR."\{Integer.toHexString(paint[y][x])} ");
                }
                writer.println(); // New line for each row
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void loadMatrixFromFile(String fileName) {
        try (Scanner scanner = new Scanner(new File(fileName))) {
            for (int i = 0; i < height; i++) {
                String[] line = scanner.nextLine().trim().split(" ");
                for (int j = 0; j < width; j++) {
                    paint[j][i] = HexFormat.fromHexDigits(line[j]);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void shuffle(int[][] matrix, int columns, Random rnd) {
        int size = matrix.length * columns;
        for (int i = size - 1; i > 0; i--) {
            swap(matrix, columns, i, rnd.nextInt(i + 1));
        }
    }
    public static void swap(int[][] matrix, int columns, int i, int j) {
        int rowI = i / columns;
        int colI = i % columns;
        int rowJ = j / columns;
        int colJ = j % columns;
        int tmp = matrix[rowI][colI];
        matrix[rowI][colI] = matrix[rowJ][colJ];
        matrix[rowJ][colJ] = tmp;
    }
    public static int rand(int n) {
        return new Random().nextInt(n);
    }
    public static void sleep(int n){try {Thread.sleep(n);} catch (InterruptedException ex) {throw new RuntimeException(ex);}}
    static void floodFillUtil(int[][] screen, int x, int y, int prevC, int newC) {
        if (x < 0 || x >= width || y < 0 || y >= height || screen[x][y] != prevC) {return;}
        screen[x][y] = newC;
        floodFillUtil(screen, x + 1, y, prevC, newC);
        floodFillUtil(screen, x - 1, y, prevC, newC);
        floodFillUtil(screen, x, y + 1, prevC, newC);
        floodFillUtil(screen, x, y - 1, prevC, newC);
    }
    static void floodFill(int[][] screen, int x, int y, int newC) {
        int prevC = screen[x][y];
        if (prevC == newC) {return;}
        floodFillUtil(screen, x, y, prevC, newC);
    }
    public void loadImageToMatrix(String imagePath) {
        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            resizedImage.getGraphics().drawImage(originalImage, 0, 0, width, height, null);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int rgb = resizedImage.getRGB(x, y);
                    paint[x][y] = rgb;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void copy(){
        int[][] copy = new int[size][size];
        for (int x = 0; x < size; x++){
            System.arraycopy(paint[movX + x], movY, copy[x], 0, size);
        }
        Paint.copy = copy;
    }
    public void paste(){
        int[][] copy = Paint.copy;
        for (int x = 0; x < copy.length; x++){
            System.arraycopy(copy[x], 0, paint[movX + x], movY, copy.length);
        }
    }
    public void matrix(){
        int[][] matrix = new int[size][size];
        for (int x = 0; x < size; x++){
            System.arraycopy(paint[movX + x], movY, matrix[x], 0, size);
        }
        Paint.matrix = matrix;
        shuffle(matrix, matrix[0].length, new Random());
        for (int x = 0; x < matrix.length; x++){
            System.arraycopy(matrix[x], 0, paint[movX + x], movY, matrix.length);
        }
    }
    public void main(String[] args) {
        JFrame frame = new JFrame("Pixel Art Paint");
        // frame.setUndecorated(true); // Removed
        // frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Removed
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(X, Y); // Added
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        JPanel panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g.setColor(new Color(0,0,0));
                g2d.scale(div, div);
                for (int x = 0; x < width; x++){
                    for (int y = 0; y < height; y++){
                        int n = paint[x][y];
                        g.setColor(new Color((n >> 16) & 0xFF,(n >> 8) & 0xFF,n & 0xFF));
                        g.fillRect(x,y,1,1);
                    }
                }
                g2d.scale(0.2, 0.2);
                for (int x = 0; x<3 ; x++) {
                    if (x % 2 == 0){g.setColor(new Color(0,0,0));}else {g.setColor(new Color(255,255,255));}
                    g.drawRect((int) (movX*div+Math.pow(2,x)), (int) (movY*div+Math.pow(2,x)), (int) (size*div-Math.pow(2,x+1)), (int) (size*div-Math.pow(2,x+1)));
                }
                for (int x = 0; x < width; x++){
                    for (int y = 0; y < height; y++){
                        g.drawRect(x*div,y*div,10,10);
                    }
                }
                g.setColor(new Color((color >> 16) & 0xFF,(color >> 8) & 0xFF,color & 0xFF));
                g.drawString(STR."█ rgb [\{(int) hue}] con [\{(int) (lum * 100)}] brg [\{(int) (rgb/167000%100)}]",20,20);
                g.drawString(STR."\{func[m0]} \{func[m1 + 4]}",190,20);
            }
        };
        Timer timer = new Timer(0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (keysPressed.contains(KeyEvent.VK_0)){loadImageToMatrix("C:\\Users\\nikit\\IdeaProjects\\Paint\\src\\1.jpg");}
                if (keysPressed.contains(KeyEvent.VK_8) && movY+size <= height && movX+size <= width){copy();}
                if (keysPressed.contains(KeyEvent.VK_V) && movY+size <= height && movX+size <= width){matrix();}
                if (keysPressed.contains(KeyEvent.VK_9) && movY+size <= height && movX+size <= width){paste();}
                if (keysPressed.contains(KeyEvent.VK_1)){m0 = 0;}
                if (keysPressed.contains(KeyEvent.VK_2)){m0 = 1;}
                if (keysPressed.contains(KeyEvent.VK_3)){color = paint[movX][movY];}
                if (keysPressed.contains(KeyEvent.VK_4)){int col = rgb & 0xFF;col = (col+1)%255;rgb = (col << 16) | (col << 8) | col;color = rgb;}
                if (keysPressed.contains(KeyEvent.VK_5)){hue = (hue + 3.6f) % 360; color = Color.HSBtoRGB(hue / 360f, 1f, lum);}
                if (keysPressed.contains(KeyEvent.VK_6)){color = (rand(255) << 16) | (rand(255) << 8) | rand(255);}
                if (keysPressed.contains(KeyEvent.VK_7)){if(m2 == 0){color = HexFormat.fromHexDigits("0");m2=1;} else if (m2 == 1) {color = HexFormat.fromHexDigits("ffffff");m2=0;}sleep(100);}
                if (keysPressed.contains(KeyEvent.VK_C)){switch (m1){ case 0: m1 = 1;break; case 1: m1 = 0;break;}sleep(200);}
                if (keysPressed.contains(KeyEvent.VK_W) && movY > 0) {movY-=1;          if (m1 == 1){ sleep(50);}}
                if (keysPressed.contains(KeyEvent.VK_S) && movY < height-size) {movY+=1;if (m1 == 1){ sleep(50);}}
                if (keysPressed.contains(KeyEvent.VK_A) && movX > 0) {movX-=1;          if (m1 == 1){ sleep(50);}}
                if (keysPressed.contains(KeyEvent.VK_D) && movX < width-size) {movX+=1; if (m1 == 1){ sleep(50);}}
                if (keysPressed.contains(KeyEvent.VK_SPACE)){if (m0 == 0 || m0 == 2){space_painter();}else if (m0 == 1) {floodFill(paint, movX, movY, color);} else if (m0 == 3){color = paint[movX][movY];}}
                if (keysPressed.contains(KeyEvent.VK_E) && movY < height-size ){ size++;if (m1 == 1){ sleep(50);}}
                if (keysPressed.contains(KeyEvent.VK_Q) && size > 1 && movX < width-size) {size--;if (m1 == 1){ sleep(50);}}
                if (keysPressed.contains(KeyEvent.VK_X)){savingMatrixAsFile("file.txt");}
                if (keysPressed.contains(KeyEvent.VK_Y)){loadMatrixFromFile("file.txt");}
                panel.repaint();
            }
        });
        for (String key : keys) {
            int keyCode = KeyStroke.getKeyStroke(key).getKeyCode();
            panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyCode, 0, false), key);
            panel.getActionMap().put(key, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    keysPressed.add(keyCode);
                }
            });
            panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyCode, 0, true), STR."r\{key}");
            panel.getActionMap().put(STR."r\{key}", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {keysPressed.remove(keyCode);}
            });
        }
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (m0 == 0 && !keysPressed.contains(KeyEvent.VK_3)) {
                        for (int a = 0; a < size; a++) {
                            for (int b = 0; b < size; b++) {
                                paint[e.getX() / div + a][e.getY() / div + b] = color;
                            }
                        }
                    }
                }
                movX = e.getX() / div;
                movY = e.getY() / div;
            }
        });
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if(m0 == 0 && !keysPressed.contains(KeyEvent.VK_3)){
                        for (int a = 0; a<size;a++){
                            for (int b = 0; b<size;b++){
                                paint[e.getX() / div + a][e.getY() / div + b] = color;
                            }
                        }
                    } else if (m0 == 1 && !keysPressed.contains(KeyEvent.VK_3)) {
                    floodFill(paint, movX, movY, color);
                    }
                }
                movX = e.getX() / div;
                movY = e.getY() / div;
            }
        });
        panel.addMouseWheelListener(e -> {
            if (keysPressed.contains(KeyEvent.VK_R)) {
                int notches = e.getWheelRotation();
                if (notches < 0) {hue = (hue + 3.6f) % 360;} else {hue = (hue - 3.6f + 360) % 360;}
            }
            if (keysPressed.contains(KeyEvent.VK_F)) {
                int notches = e.getWheelRotation();
                if (notches < 0) {lum = Math.min(lum + 0.1f, 1f);} else {lum = Math.max(lum - 0.1f, 0f);}
            }
            color = Color.HSBtoRGB(hue / 360f, 1f, lum);
            panel.repaint();
        });
        timer.start();
        frame.add(panel);
        frame.setVisible(true);
    }
}
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LEVEL_EDITOR {
    static int size = 0;
    public static int[][] paint = new int[2560][1440];
    public static void main(String[] args) {
        JFrame frame = new JFrame("LEVEL EDITOR");
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);

        MouseAdapter m = new MouseAdapter() {
            Point mouseDownCompCoords = null;
            final int titleBarHeight = 30;
            @Override
            public void mouseReleased(MouseEvent e) { mouseDownCompCoords = null; }
            @Override
            public void mousePressed(MouseEvent e) { mouseDownCompCoords = e.getPoint(); }
            @Override
            public void mouseDragged(MouseEvent e) {
                if (mouseDownCompCoords != null && e.getY() <= titleBarHeight) {
                    Point currCoords = e.getLocationOnScreen();
                    frame.setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
                }
            }
        };
        frame.addMouseListener(m);
        frame.addMouseMotionListener(m);

        JPanel panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                for (int x = 0; x < 1280; x++){
                    for (int y = 0; y < 720; y++){
                        g.setColor(new Color(0,0,0));
                        g.fillRect(x*size,y*size,1,1);
                    }
                }
            }
        };

        JButton minimizeButton = new JButton("\uD83D\uDDD5");
        JButton closeButton = new JButton("\uD83D\uDDD9");
        JButton toggleButton = new JButton("\uD83D\uDDD6");

        minimizeButton.setForeground(Color.BLACK);
        minimizeButton.setBackground(Color.WHITE);
        toggleButton.setForeground(Color.BLACK);
        toggleButton.setBackground(Color.WHITE);
        closeButton.setForeground(Color.BLACK);
        closeButton.setBackground(Color.WHITE);

        minimizeButton.addActionListener(e -> frame.setExtendedState(JFrame.ICONIFIED));
        toggleButton.addActionListener(e -> {if (frame.getExtendedState() == JFrame.MAXIMIZED_BOTH) {frame.setExtendedState(JFrame.NORMAL);toggleButton.setText("\uD83D\uDDD6");size = 1;} else {frame.setExtendedState(JFrame.MAXIMIZED_BOTH);toggleButton.setText("\uD83D\uDDD7"); size = 2;}});
        closeButton.addActionListener(e -> System.exit(0));

        panel.add(minimizeButton);
        panel.add(toggleButton);
        panel.add(closeButton);

        frame.add(panel);
        frame.setVisible(true);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package screenshotcheck;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

/**
 *
 * @author waterbucket
 */
public class ScreenShotRectangle {

//    JFrame screen;
    private final JWindow screen;
    private final PanelDrag drawOn;
    
    public ScreenShotRectangle() {
        screen = new JWindow();
        drawOn = new PanelDrag();
        screen.setAlwaysOnTop(true);
        screen.setContentPane(drawOn);
        screen.setAlwaysOnTop(true);
        screen.setSize((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth());
        screen.setBackground(new Color(0, 0, 0, 50));
        drawOn.setOpaque(false);
    }

    public static void main(String[] args) {
        ScreenShotRectangle screenShotter = new ScreenShotRectangle();
        screenShotter.screen.setVisible(true);
    }
        
    class PanelDrag extends JPanel {

        private Shape s;

        public PanelDrag() {
            MouseControl madapt = new MouseControl();
            addMouseMotionListener(madapt);
            addMouseListener(madapt);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.clearRect(0, 0, getWidth(), getHeight());
            g2d.setColor(new Color(150, 150, 150, 50));
            if (s == null) {
                return;
            }
            s.draw(g2d);
        }

        private class MouseControl extends MouseAdapter {

            @Override
            public void mousePressed(MouseEvent me) {
                if (SwingUtilities.isLeftMouseButton(me)) {
                    s = new Shape(me.getPoint(), me.getPoint());
                } else if (SwingUtilities.isRightMouseButton(me)) {
                    System.exit(0);
                }
            }

            @Override
            public void mouseDragged(MouseEvent me) {
                s.rect.setBounds(Math.min(s.getClickedPoint().x, me.getPoint().x), Math.min(s.getClickedPoint().y, me.getPoint().y),
                        s.getHorizDistance(me.getPoint()), s.getVertDistance(me.getPoint()));
                drawOn.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                screen.setVisible(false);
                saveScreen();
                System.out.println("Released");
                System.exit(0);
            }

            private void saveScreen() {
                try {
                    BufferedImage screenshot = new Robot().createScreenCapture(s.rect);
                    ImageIO.write(screenshot, "jpg", new File("temp.jpg"));
                } catch (AWTException | IOException ex) {
                    Logger.getLogger(ScreenShotRectangle.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    class Shape {

        Point clickedPoint;
        Rectangle rect;

        public Shape(Point p1, Point p2) {
            this.clickedPoint = p1;
            rect = new Rectangle(p1);
        }

        public Point getClickedPoint() {
            return clickedPoint;
        }

        public Rectangle getRect() {
            return rect;
        }
        
        public void draw(Graphics2D g) {
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
            g.setColor(Color.WHITE);
            g.drawString("x: " + rect.width, MouseInfo.getPointerInfo().getLocation().x + 2,
                    MouseInfo.getPointerInfo().getLocation().y - 10);
            g.drawString("y: " + rect.height, MouseInfo.getPointerInfo().getLocation().x + 2,
                    MouseInfo.getPointerInfo().getLocation().y);
        }

        private int getVertDistance(Point point) {
//            Point pointTo = new Point(getClickedPoint().x, point.y);
//            int height = (int) Math.sqrt(Math.pow((pointTo.x - getClickedPoint().x), 2) + Math.pow((pointTo.y - getClickedPoint().y), 2));
//            int verticalDistance = point.y - getClickedPoint().y;
            int verticalDistance = Math.abs(getClickedPoint().y - point.y);
            return verticalDistance;
        }

        private int getHorizDistance(Point point) {
//            Point pointTo = new Point(point.x, getClickedPoint().y);
//            int width = (int) Math.sqrt(Math.pow((pointTo.x - getClickedPoint().x), 2) + Math.pow((pointTo.y - getClickedPoint().y), 2));
//            int horizDistance = point.x - getClickedPoint().x;
            int horizDistance = Math.abs(getClickedPoint().x - point.x);
            return horizDistance;
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package screenshotcheck;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

/**
 *
 * @author aroyal1
 */
public class ScreenShotPolygon {

    private final JWindow screen;
    private final PanelDrag drawOn;

    public ScreenShotPolygon() {
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
        ScreenShotPolygon screenShotter = new ScreenShotPolygon();
        screenShotter.screen.setVisible(true);
    }

    private static class MyLine {

        Point previousClicked;
        Point currentMousePos;

        public MyLine(Point clickedPoint) {
            this.previousClicked = clickedPoint;
            this.currentMousePos = clickedPoint;
        }

        private void setCurrentMousePos(Point currentMousePos) {
            this.currentMousePos = currentMousePos;
        }
        private void setPreviousClicked(Point previousClicked){
            this.previousClicked = previousClicked;
        }
        private void draw(Graphics2D g2d) {
            g2d.drawLine(previousClicked.x, previousClicked.y,
                    currentMousePos.x, currentMousePos.y);
        }

    }

    /**
     * @param args the command line arguments
     */
    class PanelDrag extends JPanel {

        private Shape s;
        private MyLine currentLine;

        public PanelDrag() {
            MouseControl madapt = new MouseControl();
            addMouseMotionListener(madapt);
            addMouseListener(madapt);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(142, 185, 255, 50));
            if (currentLine == null || s == null) {
                return;
            }
            currentLine.draw(g2d);
            if (s.pointsClicked.size() > 2) {
                s.draw(g2d);
            }
        }

        private class MouseControl extends MouseAdapter {

            @Override
            public void mousePressed(MouseEvent me) {
                if (SwingUtilities.isLeftMouseButton(me)) {
                    if (s == null) {
                        currentLine = new MyLine(me.getPoint());
                        s = new Shape();
                        s.addPoint(me.getPoint());
                    } else {
                        s.addPoint(me.getPoint());
                        currentLine.setPreviousClicked(s.getPreviousPoint());
                    }
                } else if (SwingUtilities.isRightMouseButton(me)) {
                    System.exit(0);
                }
            }

            @Override
            public void mouseMoved(MouseEvent me) {
                if (currentLine != null) {
                    currentLine.setCurrentMousePos(me.getPoint());
                    drawOn.repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent me) {

            }

//            private void saveScreen() {
//                try {
//                    BufferedImage screenshot = new Robot().createScreenCapture(s.rect);
//                    ImageIO.write(screenshot, "jpg", new File("temp.jpg"));
//                } catch (AWTException | IOException ex) {
//                    Logger.getLogger(ScreenShotCheck.class.getName()).log(Level.SEVERE, null, ex);
//                }
        }
    }

    // takes a clicke
    class Shape {

        Point clickedPoint;
        ArrayList<Point> pointsClicked;

        public Shape() {
            pointsClicked = new ArrayList<>();
        }

        // adds a new point 
        private void addPoint(Point addedPoint) {
            pointsClicked.add(addedPoint);
        }

        private Point getPreviousPoint() {
            return pointsClicked.get(pointsClicked.size() - 1);
        }

        //draw lines to all the points
        void draw(Graphics2D g2d) {
            for (int i = 0; i < pointsClicked.size() - 1; i++) {
                g2d.drawLine(pointsClicked.get(i).x, pointsClicked.get(i).y,
                        pointsClicked.get(i - 1).x, pointsClicked.get(i - 1).y);
            }
        }
    }
}

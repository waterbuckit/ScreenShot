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
 * @author waterbuckit
 */
public class ScreenShotPolygon {

    private final JWindow screen;
    private final PanelDrag drawOn;

    /**
     * Constructor for main object, sets up GUI elements.
     */
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

    /**
     * Main method for initialising the screenShot class.
     *
     * @param args
     */
    public static void main(String[] args) {
        ScreenShotPolygon screenShotter = new ScreenShotPolygon();
        screenShotter.screen.setVisible(true);
    }

    /**
     * Class for manipulating the line object
     */
    private static class MyLine {

        Point previousClicked;
        Point currentMousePos;

        /**
         * creates a new MyLine, takes only clicked point as the current mouse
         * position at the start will be the point clicked.
         *
         * @param clickedPoint
         */
        public MyLine(Point clickedPoint) {
            this.previousClicked = clickedPoint;
            this.currentMousePos = clickedPoint;
        }

        /**
         * sets the current mouse position
         *
         * @param currentMousePos
         */
        private void setCurrentMousePos(Point currentMousePos) {
            this.currentMousePos = currentMousePos;
        }

        /**
         * sets the previous clicked point
         *
         * @param previousClicked
         */
        private void setPreviousClicked(Point previousClicked) {
            this.previousClicked = previousClicked;
        }

        /**
         * Draw method for drawing the current instance of the MyLine
         *
         * @param g2d
         */
        private void draw(Graphics2D g2d) {
            g2d.drawLine(previousClicked.x, previousClicked.y,
                    currentMousePos.x, currentMousePos.y);
        }
    }

    /**
     * Custom JPanel for drawing on.
     */
    class PanelDrag extends JPanel {

        private Shape s;
        private MyLine currentLine;

        /**
         * constructor sets up the listeners.
         */
        public PanelDrag() {
            MouseControl madapt = new MouseControl();
            addMouseMotionListener(madapt);
            addMouseListener(madapt);
        }

        /**
         * Allows to paint custom objects to screen
         *
         * @param g
         */
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(8, 255, 0, 255));
            if (currentLine == null || s == null) {
                return;
            }
            currentLine.draw(g2d);
            s.draw(g2d);
        }

        /**
         * custom MouseAdapter for handling mouse events
         */
        private class MouseControl extends MouseAdapter {

            /**
             * Takes a mouse event and uses that to decide whether to create a
             * new shape or simply add a new point to the shape
             *
             * @param me
             */
            @Override
            public void mousePressed(MouseEvent me) {
                if (SwingUtilities.isLeftMouseButton(me)) {
                    if (s == null) {
                        currentLine = new MyLine(me.getPoint());
                        s = new Shape();
                        s.addPoint(me.getPoint());
//                        System.out.println(me.getPoint().toString());
                    } else {
//                        System.err.println(me.getPoint().toString());
                        s.addPoint(me.getPoint());
                        for (Point point : s.pointsClicked) {
                            System.out.println(point.toString());
                        }
                        currentLine.setPreviousClicked(s.getPreviousPoint());
                    }
                } else if (SwingUtilities.isRightMouseButton(me)) {
                    System.exit(0);
                }
            }

            /**
             * Updates the current position of the mouse on the MyLine object
             *
             * @param me
             */
            @Override
            public void mouseMoved(MouseEvent me) {
                if (currentLine != null) {
                    currentLine.setCurrentMousePos(me.getPoint());
                    drawOn.repaint();
                }
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

    /**
     * shape class for drawing all the points of the polygon.
     */
    class Shape {

        Point clickedPoint;
        ArrayList<Point> pointsClicked;

        /**
         * Constructor makes a new list of points that have already been
         * clicked.
         */
        public Shape() {
            pointsClicked = new ArrayList<>();
        }

        /**
         * adds new points to the to-be polygon.
         *
         * @param addedPoint
         */
        private void addPoint(Point addedPoint) {
            pointsClicked.add(addedPoint);
        }

        /**
         * returns the previous point.
         *
         * @return
         */
        private Point getPreviousPoint() {
            return pointsClicked.get(pointsClicked.size() - 1);
        }

        /**
         * Draws all the previous lines by iterating over the list, drawing in
         * form of (current line in list.x, current line in list.y, previous
         * line in list.x, previous line in list.y
         *
         * @param g2d
         */
        void draw(Graphics2D g2d) {
            for (int i = 1; i < pointsClicked.size(); i++) {
                g2d.drawLine(pointsClicked.get(i).x, pointsClicked.get(i).y,
                        pointsClicked.get(i - 1).x, pointsClicked.get(i - 1).y);
            }
        }
    }
}

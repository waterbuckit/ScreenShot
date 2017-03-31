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
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
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

        private Point getCurrentMousePos() {
            return currentMousePos;
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
                        s.addPoint(currentLine.getCurrentMousePos());
//                        System.out.println(me.getPoint().toString());
                    } else {
                        if (currentLine.getCurrentMousePos() == s.getFirstPoint()) {
//                            screen.setVisible(false);
                            screenshot(new Rectangle(s.getRectangleOfPoly()));
                            System.exit(0);
                        }
                        s.addPoint(currentLine.getCurrentMousePos());
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
             * Updates the current position of the mouse based on the MyLine
             * object and checks whether on a single mouse movement, the cursor
             * is within five pixels of the first point.
             *
             * @param me
             */
            @Override
            public void mouseMoved(MouseEvent me) {
                if (currentLine != null) {
                    currentLine.setCurrentMousePos(me.getPoint());
                    if (getDistFromFirstPoint() < 10 && s.pointsClicked.size() >= 3) {
                        currentLine.setCurrentMousePos(s.getFirstPoint());
                    }
                    drawOn.repaint();
                }
            }

            /**
             * gets the integer euclidean distnace between the current mouse
             * position and the first point.
             *
             * @return
             */
            private int getDistFromFirstPoint() {
                return (int) Math.sqrt(Math.pow((currentLine.getCurrentMousePos().x - s.getFirstPoint().x), 2) + Math.pow((currentLine.getCurrentMousePos().y - s.getFirstPoint().y), 2));
            }

            private void screenshot(Rectangle rectangle) {
                try {
                    BufferedImage screenshot = new Robot().createScreenCapture(rectangle);
                    screenshot.createGraphics().setClip(new Polygon(s.getXCoords(), s.getYCoords(), s.pointsClicked.size()));
                    ImageIO.write(screenshot, "png", new File("temp.png"));
                } catch (AWTException | IOException ex) {
                    Logger.getLogger(ScreenShotRectangle.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
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

        private Rectangle getRectangleOfPoly() {
            return new Rectangle(this.getLowestXCoord(), this.getLowestYCoord(), this.getWidth(), this.getHeight());
        }
        private int[] getXCoords(){
            int[] xCoords = new int[pointsClicked.size()];
            for(int i = 0; i < xCoords.length; i++){
                xCoords[i] = pointsClicked.get(i).x;
            }
            return xCoords;
        }
        private int[] getYCoords(){
            int[] yCoords = new int[pointsClicked.size()];
            for(int i = 0; i < yCoords.length; i++){
                yCoords[i] = pointsClicked.get(i).x;
            }
            return yCoords;
        }
        private int getHeight() {
            int max = 0;
            for (Point point : pointsClicked) {
                if (point.y > max) {
                    max = point.y;
                }
            }
            max = max - getLowestYCoord();
            System.out.println("Height: " + max);
            return max;
        }

        private int getWidth() {
            int max = 0;
            for (Point point : pointsClicked) {
                if (point.x > max) {
                    max = point.x;
                }
            }
            max = max - getLowestXCoord();
            System.out.println("Width: " + max);
            return max;
        }

        private int getLowestYCoord() {
            int min = pointsClicked.get(0).y;
            for (Point point : pointsClicked) {
                if (point.y < min) {
                    min = point.y;
                }
            }
            System.out.println("Y: " + min);
            return min;
        }

        private int getLowestXCoord() {
            int min = pointsClicked.get(0).x;
            for (Point point : pointsClicked) {
                if (point.x < min) {
                    min = point.x;
                }
            }
            System.out.println("X: " + min);
            return min;
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
         * returns the first point stored in the array list.
         *
         * @return
         */
        private Point getFirstPoint() {
            return pointsClicked.get(0);
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

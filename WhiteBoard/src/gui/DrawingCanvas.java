/**
 * @Name: Jiaming XIAN
 * @Student ID: 1336110
 */

package gui;


import shape.Shape;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class DrawingCanvas extends Canvas {

    private final int width;
    private final int height;
    private final List<Shape> shapeList;


    public DrawingCanvas(List<Shape> shapeList) {
        this.shapeList = shapeList;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1050, 660));
        setMaximumSize(new Dimension(1050, 660));
        this.width = getPreferredSize().width;
        this.height = getPreferredSize().height;
    }

    public BufferedImage createCanvasImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        paint(g2d);
        g2d.dispose();
        return image;
    }

    public void drawImage(BufferedImage image) {
        Graphics2D g2d = (Graphics2D) getGraphics();
        g2d.drawImage(image, 0, 0, null);
    }

    @Override
    public void update(Graphics g) {
        Image offscreen = createImage(getWidth(), getHeight());
        Graphics2D g2d = (Graphics2D) offscreen.getGraphics();
        for (Shape s : shapeList) {
            s.draw(g2d);
        }
        g.drawImage(offscreen, 0, 0, null);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        update(g2d);
    }
}

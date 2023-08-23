/**
 * @Name: Jiaming XIAN
 * @Student_ID: 1336110
 */

package gui;

import org.json.simple.JSONObject;
import service.WhiteBoardServerService;
import shape.Line;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.rmi.RemoteException;

public class DrawLineMouseListener implements MouseListener, MouseMotionListener {
    private static int x1, y1, x2, y2;
    private final Graphics2D graphics;
    private final DrawingCanvas canvas;
    private final JPanel currentColorPanel;
    private final JSlider strokeSlider;
    private final JButton lineButton;
    private final WhiteBoardServerService serverService;

    public DrawLineMouseListener(Graphics2D graphics, DrawingCanvas canvas, JPanel currentColorPanel, JSlider strokeSlider, JButton lineButton, WhiteBoardServerService serverService) {
        this.graphics = graphics;
        this.canvas = canvas;
        this.currentColorPanel = currentColorPanel;
        this.strokeSlider = strokeSlider;
        this.lineButton = lineButton;
        this.serverService = serverService;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && lineButton.getModel().isPressed()) {
            x1 = e.getX();
            y1 = e.getY();
            x2 = x1;
            y2 = y1;
        }
        else {
            canvas.removeMouseListener(this);
            canvas.removeMouseMotionListener(this);
            canvas.setCursor(Cursor.getDefaultCursor());
            lineButton.getModel().setPressed(false);
            lineButton.getModel().setArmed(false);
            canvas.repaint();
            WhiteBoardGUI.setSelectedShapeButton(null);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        graphics.setXORMode(Color.WHITE);
        graphics.setColor(currentColorPanel.getBackground());
        graphics.setStroke(new BasicStroke(strokeSlider.getValue()));
        graphics.drawLine(x1, y1, x2, y2);
        x2 = e.getX();
        y2 = e.getY();
        graphics.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        x2 = e.getX();
        y2 = e.getY();
        graphics.setXORMode(Color.WHITE);
        graphics.setColor(currentColorPanel.getBackground());
        graphics.setStroke(new BasicStroke(strokeSlider.getValue()));
        graphics.drawLine(x1, y1, x2, y2);
        try {
            serverService.addShape(new Line(x1, y1, x2, y2, currentColorPanel.getBackground(), strokeSlider.getValue()));
            if (!WhiteBoardGUI.isManager) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("request", "addShape");
                WhiteBoardGUI.sendEncrypted(jsonObject.toJSONString(), WhiteBoardGUI.writer);
            }
            else {
                canvas.repaint();
            }
        } catch (RemoteException re) {
            throw new RuntimeException(re);
        }
        canvas.removeMouseListener(this);
        canvas.removeMouseMotionListener(this);
        canvas.setCursor(Cursor.getDefaultCursor());
        lineButton.getModel().setPressed(false);
        lineButton.getModel().setArmed(false);
        WhiteBoardGUI.setSelectedShapeButton(null);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!lineButton.getModel().isPressed()) {
            canvas.removeMouseListener(this);
            canvas.removeMouseMotionListener(this);
            canvas.setCursor(Cursor.getDefaultCursor());
            lineButton.getModel().setPressed(false);
            lineButton.getModel().setArmed(false);
            WhiteBoardGUI.setSelectedShapeButton(null);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

}

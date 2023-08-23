/**
 * @Name: Jiaming XIAN
 * @Student ID: 1336110
 */

package gui;

import org.json.simple.JSONObject;
import service.WhiteBoardServerService;
import shape.Circle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.rmi.RemoteException;

public class DrawCircleMouseListener implements MouseListener, MouseMotionListener {
    private static int x1, y1, x2, y2, tx, ty;
    private int diameter;
    private final Graphics2D graphics;
    private final Canvas canvas;
    private final JPanel currentColorPanel;
    private final JSlider strokeSlider;
    private final JButton circleButton;
    private final WhiteBoardServerService serverService;

    public DrawCircleMouseListener(Graphics2D graphics, Canvas canvas, JPanel currentColorPanel, JSlider strokeSlider, JButton circleButton, WhiteBoardServerService serverService) {
        this.graphics = graphics;
        this.canvas = canvas;
        this.currentColorPanel = currentColorPanel;
        this.strokeSlider = strokeSlider;
        this.circleButton = circleButton;
        this.serverService = serverService;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && circleButton.getModel().isPressed()) {
            x1 = e.getX();
            y1 = e.getY();
            x2 = x1;
            y2 = y1;
            tx = x1;
            ty = y1;
            diameter = 0;
        }
        else {
            canvas.removeMouseListener(this);
            canvas.removeMouseMotionListener(this);
            canvas.setCursor(Cursor.getDefaultCursor());
            circleButton.getModel().setPressed(false);
            circleButton.getModel().setArmed(false);
            canvas.repaint();
            WhiteBoardGUI.setSelectedShapeButton(null);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        graphics.setXORMode(Color.WHITE);
        graphics.setColor(currentColorPanel.getBackground());
        graphics.setStroke(new BasicStroke(strokeSlider.getValue()));
        graphics.drawOval(tx, ty, diameter, diameter);
        x2 = e.getX();
        y2 = e.getY();
        diameter = Math.min(Math.abs(x2 - x1), Math.abs(y2 - y1));
        int[] xy = computeX1Y1(x1, y1, x2, y2, diameter);
        tx = xy[0];
        ty = xy[1];
        graphics.drawOval(tx, ty, diameter, diameter);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        x2 = e.getX();
        y2 = e.getY();
        diameter = Math.min(Math.abs(x2 - x1), Math.abs(y2 - y1));
        int[] xy = computeX1Y1(x1, y1, x2, y2, diameter);
        tx = xy[0];
        ty = xy[1];
        graphics.setXORMode(Color.WHITE);
        graphics.setColor(currentColorPanel.getBackground());
        graphics.setStroke(new BasicStroke(strokeSlider.getValue()));
        graphics.drawOval(tx, ty, diameter, diameter);
        try {
            serverService.addShape(new Circle(tx, ty, diameter, currentColorPanel.getBackground(), strokeSlider.getValue()));
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
        circleButton.getModel().setPressed(false);
        circleButton.getModel().setArmed(false);
        WhiteBoardGUI.setSelectedShapeButton(null);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!circleButton.getModel().isPressed()) {
            canvas.removeMouseListener(this);
            canvas.removeMouseMotionListener(this);
            canvas.setCursor(Cursor.getDefaultCursor());
            circleButton.getModel().setPressed(false);
            circleButton.getModel().setArmed(false);
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

    private int[] computeX1Y1(int x1, int y1, int x2, int y2, int diameter) {
        int[] xy = new int[2];
        if (x2 < x1) {
            if (y2 < y1) {
                xy[0] = x1 - diameter;
                xy[1] = y1 - diameter;
            }
            else {
                xy[0] = x1 - diameter;
                xy[1] = y1;
            }
        }
        else {
            if (y2 < y1) {
                xy[0] = x1;
                xy[1] = y1 - diameter;
            }
            else {
                xy[0] = x1;
                xy[1] = y1;
            }
        }
        return xy;
    }
}

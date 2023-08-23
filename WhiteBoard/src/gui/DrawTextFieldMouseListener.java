/**
 * @Name: Jiaming XIAN
 * @Student ID: 1336110
 */

package gui;

import org.json.simple.JSONObject;
import service.WhiteBoardServerService;
import shape.Text;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Objects;

public class DrawTextFieldMouseListener implements MouseListener, MouseMotionListener {
    private static int x1, y1;
    private final String text;
    private final Graphics2D graphics;
    private final Canvas canvas;
    private final JPanel currentColorPanel;
    private final JComboBox<String> fontComboBox;
    private final JComboBox<String> styleComboBox;
    private final JComboBox<Integer> sizeComboBox;
    private final JButton textButton;
    private final WhiteBoardServerService serverService;

    public DrawTextFieldMouseListener(Graphics2D graphics, Canvas canvas, String text, JPanel currentColorPanel, JComboBox<String> fontComboBox, JComboBox<String> styleComboBox, JComboBox<Integer> sizeComboBox, JButton textButton, WhiteBoardServerService serverService) {
        this.graphics = graphics;
        this.canvas = canvas;
        this.text = text;
        this.currentColorPanel = currentColorPanel;
        this.fontComboBox = fontComboBox;
        this.styleComboBox = styleComboBox;
        this.sizeComboBox = sizeComboBox;
        this.textButton = textButton;
        this.serverService = serverService;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && textButton.getModel().isPressed()) {
            x1 = e.getX();
            y1 = e.getY();
        }
        else {
            canvas.removeMouseListener(this);
            canvas.removeMouseMotionListener(this);
            canvas.setCursor(Cursor.getDefaultCursor());
            textButton.getModel().setPressed(false);
            textButton.getModel().setArmed(false);
            canvas.repaint();
            WhiteBoardGUI.setTextComboBoxVisible(false);
            WhiteBoardGUI.setSelectedShapeButton(null);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        graphics.setPaintMode();
        graphics.setColor(currentColorPanel.getBackground());
        String fontName = (String) fontComboBox.getSelectedItem();
        int fontStyle = Font.PLAIN;
        if (Objects.equals(styleComboBox.getSelectedItem(), "Bold")) {
            fontStyle = Font.BOLD;
        }
        else if (Objects.equals(styleComboBox.getSelectedItem(), "Italic")) {
            fontStyle = Font.ITALIC;
        }
        else if (Objects.equals(styleComboBox.getSelectedItem(), "Bold Italic")) {
            fontStyle = Font.BOLD | Font.ITALIC;
        }
        int fontSize = 12;
        if(sizeComboBox.getSelectedItem() != null) {
            fontSize = (Integer) sizeComboBox.getSelectedItem();
        }
        try {
            serverService.addShape(new Text(x1, y1, text, currentColorPanel.getBackground(), fontName, fontStyle, fontSize));
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
        textButton.getModel().setPressed(false);
        textButton.getModel().setArmed(false);
        WhiteBoardGUI.setTextComboBoxVisible(false);
        WhiteBoardGUI.setSelectedShapeButton(null);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!textButton.getModel().isPressed()) {
            canvas.removeMouseListener(this);
            canvas.removeMouseMotionListener(this);
            canvas.setCursor(Cursor.getDefaultCursor());
            textButton.getModel().setPressed(false);
            textButton.getModel().setArmed(false);
            WhiteBoardGUI.setTextComboBoxVisible(false);
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

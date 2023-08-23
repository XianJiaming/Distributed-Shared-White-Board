/**
 * @Name: Jiaming XIAN
 * @Student_ID: 1336110
 */

package shape;

import org.json.simple.JSONObject;

import java.awt.*;
import java.io.Serializable;

public class Rectangle extends Shape implements Serializable {
    private final int width;
    private final int height;
    private final int strokeValue;

    public Rectangle(int x1, int y1, int width, int height, Color color, int strokeValue) {
        super(x1, y1, color);
        this.width = width;
        this.height = height;
        this.strokeValue = strokeValue;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getStrokeValue() {
        return strokeValue;
    }

    @Override
    public String getShapeType() {
        return "Rectangle";
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(new Color(getColorValue()));
        g.setStroke(new BasicStroke(getStrokeValue()));
        g.drawRect(getX1(), getY1(), getWidth(), getHeight());
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("type", getShapeType());
        obj.put("x1", getX1());
        obj.put("y1", getY1());
        obj.put("width", getWidth());
        obj.put("height", getHeight());
        obj.put("colorValue", getColorValue());
        obj.put("strokeValue", getStrokeValue());
        return obj;
    }
}

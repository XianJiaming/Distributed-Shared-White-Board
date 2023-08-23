/**
 * @Name: Jiaming XIAN
 * @Student_ID: 1336110
 */

package shape;

import org.json.simple.JSONObject;

import java.awt.*;
import java.io.Serializable;

public class Line extends Shape implements Serializable {
    private final int x2, y2;
    private final int strokeValue;

    public Line(int x1, int y1, int x2, int y2, Color color, int strokeValue) {
        super(x1, y1, color);
        this.x2 = x2;
        this.y2 = y2;
        this.strokeValue = strokeValue;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }

    public int getStrokeValue() {
        return strokeValue;
    }

    @Override
    public String getShapeType() {
        return "Line";
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(new Color(getColorValue()));
        g.setStroke(new BasicStroke(getStrokeValue()));
        g.drawLine(getX1(), getY1(), getX2(), getY2());
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("type", getShapeType());
        obj.put("x1", getX1());
        obj.put("y1", getY1());
        obj.put("x2", getX2());
        obj.put("y2", getY2());
        obj.put("colorValue", getColorValue());
        obj.put("strokeValue", getStrokeValue());
        return obj;
    }

}

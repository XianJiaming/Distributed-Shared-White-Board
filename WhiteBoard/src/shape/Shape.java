/**
 * @Name: Jiaming XIAN
 * @Student_ID: 1336110
 */

package shape;

import org.json.simple.JSONObject;

import java.awt.*;
import java.io.Serializable;

public abstract class Shape implements Serializable {
    private final int x1, y1;
    private final int colorValue;

    public Shape(int x1, int y1, Color color) {
        this.x1 = x1;
        this.y1 = y1;
        this.colorValue = color.getRGB();
    }

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getColorValue() {
        return colorValue;
    }

    public abstract String getShapeType();

    public abstract void draw(Graphics2D g);

    public abstract JSONObject toJSONObject();
}

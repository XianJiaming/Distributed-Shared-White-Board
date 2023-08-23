/**
 * @Name: Jiaming XIAN
 * @Student ID: 1336110
 */

package shape;

import org.json.simple.JSONObject;

import java.awt.*;
import java.io.Serializable;

public class Text extends Shape implements Serializable {
    private final String text;
    private final String fontName;
    private final int fontStyle, fontSize;

    public Text(int x1, int y1, String text, Color color, String fontName, int fontStyle, int fontSize) {
        super(x1, y1, color);
        this.text = text;
        this.fontName = fontName;
        this.fontStyle = fontStyle;
        this.fontSize = fontSize;
    }

    public String getText() {
        return text;
    }

    public String getFontName() {
        return fontName;
    }

    public int getFontStyle() {
        return fontStyle;
    }

    public int getFontSize() {
        return fontSize;
    }

    @Override
    public String getShapeType() {
        return "Text";
    }

    @Override
    public void draw(Graphics2D g) {
    	g.setColor(new Color(getColorValue()));
        Font font = new Font(getFontName(), getFontStyle(), getFontSize());
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics(font);
        g.drawString(getText(), getX1()-(metrics.stringWidth(text)/2), getY1()+(metrics.getHeight()/4));
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("type", getShapeType());
        obj.put("x1", getX1());
        obj.put("y1", getY1());
        obj.put("text", getText());
        obj.put("fontName", getFontName());
        obj.put("fontStyle", getFontStyle());
        obj.put("fontSize", getFontSize());
        obj.put("colorValue", getColorValue());
        return obj;
    }
}

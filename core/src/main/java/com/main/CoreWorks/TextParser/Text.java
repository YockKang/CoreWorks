package com.main.CoreWorks.TextParser;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.utils.JsonValue;

public class Text {
    public final String text;
    public final Color color;
    public final boolean newline;

    public Text(String text, Color color, boolean newline) {
        this.text = text;
        this.color = color;
        this.newline = newline;
    }

    public Text(String text, Color color) {
        this.text = text;
        this.color = color;
        this.newline = false;
    }

    public Text(JsonValue data) {
        Color color1;
        this.text = data.getString("Word");
        JsonValue colorData = data.get("Color");
        if (colorData.isArray()) {
            float[] rgba = colorData.asFloatArray();
            for (int i = 0; i < rgba.length; i++) {
                rgba[i] /= 255;
            }
            if (rgba.length > 3) {
                color1 = new Color(rgba[0], rgba[1], rgba[2], rgba[3]);
            } else {
                color1 = new Color(rgba[0], rgba[1], rgba[2], 1);
            }
        } else if (colorData.isString()) {
            color1 = Colors.get(colorData.asString().toUpperCase());
            if (color1 == null) {
                try {
                    color1 = Color.valueOf(colorData.asString().toUpperCase());
                } catch (Exception e) {
                    color1 = Color.WHITE;
                }
            }
        } else if (colorData.isNumber()) {
            int rgba8888 = colorData.asInt();
            color1 = new Color(rgba8888);
        } else {
            color1 = Color.WHITE;
        }
        this.color = color1;
        this.newline = false;
    }

    @Override
    public String toString() {
        return text + ", " + color + ".";
    }
}

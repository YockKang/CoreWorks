package com.main.CoreWorks.TextParser;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.JsonValue;
import com.main.CoreWorks.entities.Relics.Relic;

public class Text {
    public final String text;
    public final Color color;

    public Text(String text, Color color) {
        this.text = text;
        this.color = color;
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
            try {
                color1 = Color.valueOf(colorData.asString().toUpperCase());
            } catch (Exception e) {
                color1 = Color.WHITE;
            }

        } else if (colorData.isNumber()){
            int rgba8888 = colorData.asInt();
            color1 = new Color(rgba8888);
        } else {
            color1 = Color.WHITE;
        }
        this.color = color1;
    }

    @Override
    public String toString() {
        return text + ", " + color + ".";
    }
}

package com.main.CoreWorks.TextParser;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.JsonValue;

public class Keyword extends Text {
    public final String description;

    public Keyword(JsonValue data) {
        super(data);
        this.description = data.getString("Description");
    }

    public Keyword(String text, Color color, String description) {
        super(text, color);
        this.description = description;
    }

    @Override
    public String toString() {
        return text + ", " + color +  ", " + description + ".";
    }
}

package com.main.CoreWorks.Codex;

import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.JsonValue;

public abstract class Entry {
    protected String id;
    protected String name;
    protected String description;
    protected Table infoTable = new Table();

    public Entry(JsonValue data) {
        id = data.getString("id");
        name = data.getString("Name");
        if (data.get("Description") != null) {
            description = data.getString("Description");
        }
    }

    public Entry(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Entry(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // public abstract void generateInfoTable(Skin skin);
}

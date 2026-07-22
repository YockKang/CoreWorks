package com.main.CoreWorks.Codex;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.JsonValue;
import com.main.CoreWorks.TextParser.Sentence;

public abstract class Entry {
    protected String id;
    protected String name;
    protected Sentence description = null;
    protected Table infoTable = new Table();

    public Entry(JsonValue data) {
        id = data.getString("id");
        name = data.getString("Name");
        if (data.get("Description") != null) {
            description = new Sentence(data.getString("Description"), true);
        }
    }

    public Entry(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Entry(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = new Sentence(description);
    }

    public Entry(String id, String name, Sentence description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public void generateInfoTable(Skin skin) {
        infoTable.add(new Label(name, skin)).row();
        if (description != null) {
            infoTable.add(description.toTable(skin)).row();
        }
    }
}

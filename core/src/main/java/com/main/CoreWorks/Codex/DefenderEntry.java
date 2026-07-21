package com.main.CoreWorks.Codex;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Queue;

public class DefenderEntry extends BuildingEntry{

    public DefenderEntry(JsonValue data) {
        super(data);
    }

    @Override
    public void generateInfoTable(Skin skin) {
        super.generateInfoTable(skin);
        infoTable.add(new Label("Base Magazine Size: " + data.getInt("MagSize"), skin)).row();
        infoTable.add(new Label("Base Defense: " + data.getFloat("BaseDef"), skin)).row();
        String action = data.getString("Action");
    }
}

package com.main.CoreWorks.Codex;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.JsonValue;

public class ShooterEntry extends BuildingEntry {

    public ShooterEntry(JsonValue data) {
        super(data);
    }


    @Override
    public void generateInfoTable(Skin skin) {
        super.generateInfoTable(skin);
        infoTable.add(new Label("Base Magazine Size: " + data.getInt("MagSize"), skin)).row();
        infoTable.add(new Label("Base Damage: " + data.getFloat("BaseDmg"), skin)).row();
    }
}

package com.main.CoreWorks.Codex;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.JsonValue;
import com.main.CoreWorks.moveset.*;

public class EnemyEntry extends Entry{

    protected JsonValue data;

    public EnemyEntry(JsonValue data) {
        super(data);
        this.data = data;
    }

    @Override
    public void generateInfoTable(Skin skin) {
        super.generateInfoTable(skin);


        infoTable.add(new Label("Base HP: " + data.getInt("HP"), skin)).row();
        int baseShield = 0;
        if (data.get("Shield") != null) {
            baseShield = data.getInt("Shield");
        }
        infoTable.add(new Label("Base Shield: " + baseShield, skin)).row();
        int gracePeriod = 0;
        if (data.get("GracePeriod") != null) {
            gracePeriod = data.getInt("GracePeriod");
        }
        infoTable.add(new Label("Initial Cooldown: " + gracePeriod, skin)).row();

        infoTable.add(new Label("Moves:", skin)).row();

        JsonValue moves = data.get("Moveset");
        Table moveTable = new Table();

        for (JsonValue mv : moves) {
            try {
                String type = mv.getString("Type");
                int value = mv.getInt("Value");
                int charge = mv.getInt("Charge");
                switch (type) {
                    case "Damage", "Attack" -> {
                        moveTable.add(new Label(String.valueOf(value), skin));
                        moveTable.add(new Label("Damage", skin));
                    }
                    case "Heal" -> {
                        moveTable.add(new Label(String.valueOf(value), skin));
                        moveTable.add(new Label("Heal", skin));
                    }
                    case "Shield" -> {
                        moveTable.add(new Label(String.valueOf(value), skin));
                        moveTable.add(new Label("Shield", skin));
                    }
                    case "Disable" -> {
                        moveTable.add(new Label(String.valueOf(value), skin));
                        moveTable.add(new Label("Disable", skin));
                    }
                    case "StatusEffect" -> {
                        moveTable.add(new Label(mv.get("Effect").getString("Value"), skin));
                        moveTable.add(new Label(mv.get("Effect").getString("Type"), skin));
                    }
                }

                moveTable.add(new Label("Cooldown: " + charge, skin));
                moveTable.row();
            } catch (Exception ignored) {

            }
        }

        infoTable.add(moveTable);

    }
}

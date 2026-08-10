package com.main.CoreWorks.Codex;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonValue;
import com.main.CoreWorks.TextParser.Sentence;

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
        if (whitelistResources.size > 0) {

            int itemsPerRow = 3;
            int inThisRow = 0;
            Table validAmmoTable = new Table();
            for (ResourceEntry resource : whitelistResources) {
                TextButton resourceButton = new TextButton(resource.name, skin);
                resourceButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Codex.setSelectedItem(resource);
                    }
                });
                resourceButton.addListener(new InputListener() {
                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        resourceButton.addAction(Actions.color(new Color(.7f, .7f, .7f, 1), 0.15f));
                    }

                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                        resourceButton.addAction(Actions.color(new Color(.9f, .9f, .9f, 1), 0.15f));
                    }
                });
                Sentence tooltipText = new Sentence("Expected Protection: " +
                    (int) (data.getFloat("BaseDef") * resource.dmgMult));
                System.out.println(tooltipText.text);
                Table expectedDamage = tooltipText.toTable(skin, Align.center);
                expectedDamage.setBackground("default-round");
                Tooltip<Table> descToolTip = new Tooltip<>(expectedDamage);
                descToolTip.setInstant(true);
                resourceButton.addListener(descToolTip);
                validAmmoTable.add(resourceButton);
                inThisRow++;
                if (inThisRow % itemsPerRow == 0) {
                    inThisRow = 0;
                    validAmmoTable.row();
                }
            }
            infoTable.add(validAmmoTable).row();
        } else {
            infoTable.add(new Label("All", skin));
        }
    }
}

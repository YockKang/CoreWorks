package com.main.CoreWorks.Codex;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
        infoTable.add(new Label("Valid Ammo:", skin)).row();
        if (whitelistResources.size > 0) {

            int itemsPerRow = 3;
            int inThisRow = 0;
            Table validAmmoTable = new Table();
            for (ResourceEntry resource : whitelistResources) {
                TextButton recipeButton = new TextButton(resource.name, skin);
                recipeButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        infoTable.remove();
                        Codex.ContentTable.add(resource.infoTable);
                        Codex.selectedItem = resource.infoTable;
                    }
                });
                recipeButton.addListener(new InputListener() {
                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        recipeButton.addAction(Actions.color(new Color(.7f, .7f, .7f, 1), 0.15f));
                    }
                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                        recipeButton.addAction(Actions.color(new Color(.9f, .9f, .9f, 1), 0.15f));
                    }
                });
                validAmmoTable.add(recipeButton);
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

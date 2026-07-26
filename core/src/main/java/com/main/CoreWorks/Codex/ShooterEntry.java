package com.main.CoreWorks.Codex;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.JsonValue;
import com.main.CoreWorks.TextParser.Sentence;
import com.main.CoreWorks.TextParser.Text;

import java.util.Objects;

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
                TextButton resourceButton = new TextButton(resource.name, skin);
                resourceButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        infoTable.remove();
                        Codex.ContentTable.add(resource.infoTable);
                        Codex.selectedItem = resource.infoTable;
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
                String damageType;
                if (data.get("Damage Type") != null) {
                    damageType = data.getString("Damage Type");
                } else {
                    damageType = resource.damageType;
                }
                Sentence tooltipText = new Sentence("Expected Damage: " +
                    (int) (data.getFloat("BaseDmg") * resource.dmgMult) + " ");
                if (Objects.equals(damageType, "Poison")) {
                    System.out.println(new Text(damageType, Color.GREEN));
                    tooltipText.appendText(new Text(damageType, Color.GREEN));
                } else if (Objects.equals(damageType, "True")) {
                    tooltipText.appendText(new Text(damageType, new Color(0, 1, 1, 1)));
                } else {
                    tooltipText.appendText(new Text(damageType, Color.WHITE));
                }
                System.out.println(tooltipText.text);
                Table expectedDamage = tooltipText.toTable(skin);
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

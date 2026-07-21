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
import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Recipe.Recipe;
import com.main.CoreWorks.Resources.Modifier;
import com.main.CoreWorks.database.RecipeDatabase;

import java.util.Objects;

public class ResourceEntry extends Entry{

    protected float dmgMult;
    protected String damageType = null;
    protected Array<RecipeEntry> asInput = new Array<>();
    protected Array<RecipeEntry> asOutput = new Array<>();

    public ResourceEntry(JsonValue data) {
        super(data);
        dmgMult = data.getFloat("DmgMult");
        if (data.get("Modifiers") != null) {
            JsonValue mods = data.get("Modifiers");
            if (mods.isArray()) {
                for (JsonValue mod : mods) {
                    String type = mod.getString("type");
                    switch (type) {
                        case "DamageType" -> {
                            damageType = mod.getString("str");
                        }
                    }
                }
            }
            if (damageType == null) {
                damageType = "Normal";
            }
        }
    }

    protected void addAsInput(RecipeEntry r) {
        asInput.add(r);
    }

    protected void addAsOutput(RecipeEntry r) {
        asOutput.add(r);
    }

    @Override
    public void generateInfoTable(Skin skin) {
        super.generateInfoTable(skin);
        infoTable.add(new Label("Damage Multiplier: " + dmgMult, skin)).row();

        int itemsPerRow = 3;
        int inThisRow = 0;
        infoTable.add(new Label("Used in:", skin)).row();
        Table asInputTable = new Table();
        for (RecipeEntry recipe : asInput) {
            TextButton recipeButton = new TextButton(recipe.recipe.getName(), skin);
            recipeButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    infoTable.remove();
                    Codex.ContentTable.add(recipe.infoTable);
                    Codex.selectedItem = recipe.infoTable;
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
            asInputTable.add(recipeButton);
            inThisRow++;
            if (inThisRow % itemsPerRow == 0) {
                inThisRow = 0;
                asInputTable.row();
            }
        }
        infoTable.add(asInputTable).row();

        Table asOutputTable = new Table();
        inThisRow = 0;
        infoTable.add(new Label("Crafted from:", skin)).row();
        for (RecipeEntry recipe : asOutput) {
            TextButton recipeButton = new TextButton(recipe.recipe.getName(), skin);
            recipeButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    infoTable.remove();
                    Codex.ContentTable.add(recipe.infoTable);
                    Codex.selectedItem = recipe.infoTable;
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
            asOutputTable.add(recipeButton);
            inThisRow++;
            if (inThisRow % itemsPerRow == 0) {
                inThisRow = 0;
                asOutputTable.row();
            }
        }
        infoTable.add(asOutputTable).row();
    }
}

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

public class MinerEntry extends BuildingEntry{

    public MinerEntry(JsonValue data) {
        super(data);
    }

    @Override
    public void generateInfoTable(Skin skin) {
        super.generateInfoTable(skin);

        infoTable.add(new Label("Can Craft:", skin)).row();
        for (RecipeEntry recipe : craftRecipes) {
            TextButton recipeButton = new TextButton(recipe.recipe.getName(), skin);
            recipeButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    infoTable.remove();
                    Codex.CodexTable.add(recipe.infoTable);
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
            infoTable.add(recipeButton).row();
        }
    }
}

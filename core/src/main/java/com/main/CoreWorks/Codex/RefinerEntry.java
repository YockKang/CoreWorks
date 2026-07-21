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

public class RefinerEntry extends BuildingEntry{

    public RefinerEntry(JsonValue data) {
        super(data);
    }

    @Override
    public void generateInfoTable(Skin skin) {
        super.generateInfoTable(skin);

        int itemsPerRow = 3;
        int inThisRow = 0;
        infoTable.add(new Label("Can Craft:", skin)).row();
        Table craftablesTable = new Table();
        for (RecipeEntry recipe : craftRecipes) {
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
            craftablesTable.add(recipeButton);
            inThisRow++;
            if (inThisRow % itemsPerRow == 0) {
                inThisRow = 0;
                craftablesTable.row();
            }
        }
        infoTable.add(craftablesTable).row();
    }

}

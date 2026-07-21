package com.main.CoreWorks.Codex;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.*;

public class BuildingEntry extends Entry{

    Array<RecipeEntry> craftRecipes = new Array<>();
    Array<ResourceEntry> whitelistResources = new Array<>();

    protected JsonValue data;

    public BuildingEntry(JsonValue data) {
        super(data);
        this.data = data;
    }

    public void addCraftRecipe(RecipeEntry recipeEntry) {
        if (!craftRecipes.contains(recipeEntry, true)) {
            craftRecipes.add(recipeEntry);
        }
    }

    @Override
    public void generateInfoTable(Skin skin) {
        super.generateInfoTable(skin);
    }

    /*

        cancelButton.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            clearRecipeUI();
        }
    });
        cancelButton.addListener(new InputListener() {
        @Override
        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
            cancelButton.addAction(Actions.color(new Color(1, 0, 0, 1), 0.15f));
        }

        @Override
        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
            cancelButton.addAction(Actions.color(new Color(.75f, 0, 0, 1), 0.15f));
        }
    });
     */
}

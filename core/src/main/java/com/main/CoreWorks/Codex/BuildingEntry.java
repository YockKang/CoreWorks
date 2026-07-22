package com.main.CoreWorks.Codex;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Resources.ResourceTemplate;

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

    public void addWhitelistResource(ResourceEntry resourceEntry) {
        whitelistResources.add(resourceEntry);
    }
}

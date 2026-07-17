package com.main.CoreWorks.Codex;

import com.badlogic.gdx.utils.*;

public class BuildingEntry extends Entry{

    Array<RecipeEntry> craftRecipes = new Array<>();

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
}

package com.main.CoreWorks.Codex;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Recipe.Recipe;

public class RecipeEntry extends Entry{

    protected Recipe recipe;

    Array<ResourceEntry> inputs = new Array<>();
    Array<ResourceEntry> outputs = new Array<>();

    Array<BuildingEntry> craftBuildings = new Array<>();

    public RecipeEntry(Recipe rec) {
        super(rec.getId(), rec.getName());
        recipe = rec;
    }

    public void addInput(ResourceEntry resourceEntry) {
        inputs.add(resourceEntry);
    }

    public void addOutput(ResourceEntry resourceEntry) {
        outputs.add(resourceEntry);
    }

    public void addCraftBuilding(BuildingEntry buildingEntry) {
        craftBuildings.add(buildingEntry);
    }
}

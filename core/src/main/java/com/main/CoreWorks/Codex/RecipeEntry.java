package com.main.CoreWorks.Codex;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Recipe.Recipe;

public class RecipeEntry extends Entry {

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

    @Override
    public void generateInfoTable(Skin skin) {
        super.generateInfoTable(skin);

        int itemsPerRow = 3;
        int inThisRow = 0;
        infoTable.add(new Label("Inputs:", skin)).row();
        Table inputsTable = new Table();
        for (int i = 0; i < inputs.size; i++) {
            ResourceEntry resource = inputs.get(i);
            int multiplier = recipe.getInputMultipliers().get(i);
            TextButton resourceButton = new TextButton(resource.name + " x" + multiplier, skin);
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
            inputsTable.add(resourceButton);
            inThisRow++;
            if (inThisRow % itemsPerRow == 0) {
                inThisRow = 0;
                inputsTable.row();
            }
        }
        infoTable.add(inputsTable).row();

        infoTable.add(new Label("Duration: " + recipe.getDuration() + " ticks", skin)).row();

        inThisRow = 0;
        infoTable.add(new Label("Outputs:", skin)).row();
        Table outputsTable = new Table();
        for (int i = 0; i < outputs.size; i++) {
            ResourceEntry resource = outputs.get(i);
            int multiplier = recipe.getOutputMultipliers().get(i);
            TextButton resourceButton = new TextButton(resource.name + " x" + multiplier, skin);
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
            outputsTable.add(resourceButton);
            inThisRow++;
            if (inThisRow % itemsPerRow == 0) {
                inThisRow = 0;
                outputsTable.row();
            }
        }
        infoTable.add(outputsTable).row();


        inThisRow = 0;
        infoTable.add(new Label("Crafted in:", skin)).row();
        Table buildingsTable = new Table();
        for (BuildingEntry building : craftBuildings) {
            TextButton buildingButton = new TextButton(building.name, skin);
            buildingButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    infoTable.remove();
                    Codex.ContentTable.add(building.infoTable);
                    Codex.selectedItem = building.infoTable;
                }
            });
            buildingButton.addListener(new InputListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    buildingButton.addAction(Actions.color(new Color(.7f, .7f, .7f, 1), 0.15f));
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    buildingButton.addAction(Actions.color(new Color(.9f, .9f, .9f, 1), 0.15f));
                }
            });
            buildingsTable.add(buildingButton);
            inThisRow++;
            if (inThisRow % itemsPerRow == 0) {
                inThisRow = 0;
                buildingsTable.row();
            }
        }

        infoTable.add(buildingsTable).row();
    }

}

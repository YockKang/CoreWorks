package com.main.CoreWorks.Codex;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Factory.BuildingTemplate.BuildingTemplate;
import com.main.CoreWorks.Recipe.Recipe;
import com.main.CoreWorks.Resources.ResourceTemplate;
import com.main.CoreWorks.database.*;
import com.main.CoreWorks.entities.EnemyFactory;
import com.main.CoreWorks.entities.Relics.Relic;

public class Codex {
    private static final Array<Entry> Resources = new Array<>();
    private static final Array<Entry> Recipes = new Array<>();
    private static final Array<Entry> Buildings = new Array<>();
    private static final Array<Entry> Enemies = new Array<>();
    private static final Array<Entry> Relics = new Array<>();

    public static void register() {
        // generate resource entries
        ObjectMap<String, ResourceTemplate> resources = ResourceDatabase.getDB();
        ObjectMap<String, ResourceEntry> resourceEntries = new ObjectMap<>();
        for (ObjectMap.Entry<String, ResourceTemplate> entry : resources) {
            String id = entry.key;
            JsonValue stats = entry.value.getData();
            ResourceEntry e = new ResourceEntry(stats);
            resourceEntries.put(id, e);
        }

        // generate recipe entries
        ObjectMap<String, Recipe> recipes = RecipeDatabase.getDB();
        ObjectMap<String, RecipeEntry> recipeEntries = new ObjectMap<>();
        for (ObjectMap.Entry<String, Recipe> entry : recipes) {
            String id = entry.key;
            Recipe stats = entry.value;
            RecipeEntry e = new RecipeEntry(stats);
            recipeEntries.put(id, e);
        }

        // generate building entries
        ObjectMap<String, BuildingTemplate> buildings = BuildingDatabase.getDB();
        ObjectMap<String, BuildingEntry> buildingEntries = new ObjectMap<>();
        for (ObjectMap.Entry<String, BuildingTemplate> entry : buildings) {
            String id = entry.key;
            BuildingEntry e = entry.value.generateCodexEntry();
            buildingEntries.put(id, e);
        }

        // generate enemy entries
        ObjectMap<String, EnemyFactory> enemies = EnemyDatabase.getDB();
        ObjectMap<String, EnemyEntry> enemyEntries = new ObjectMap<>();
        for (ObjectMap.Entry<String, EnemyFactory> entry : enemies) {
            String id = entry.key;
            JsonValue stats = entry.value.getData();
            EnemyEntry e = new EnemyEntry(stats);
            enemyEntries.put(id, e);
        }

        // generate relic entries
        ObjectMap<String, Relic> relics = RelicDatabase.getDB();
        ObjectMap<String, RelicEntry> relicEntries = new ObjectMap<>();
        for (ObjectMap.Entry<String, Relic> entry : relics) {
            String id = entry.key;
            Relic stats = entry.value;
            RelicEntry e = new RelicEntry(stats);
            relicEntries.put(id, e);
        }

        // generate connections
        for (ObjectMap.Entry<String, RecipeEntry> recipe : recipeEntries) {
            for (String e : recipe.value.recipe.getInputs()) {
                ResourceEntry resource = resourceEntries.get(e);
                resource.addAsInput(recipe.value);
                recipe.value.addInput(resource);
            }
            for (String e : recipe.value.recipe.getOutputs()) {
                ResourceEntry resource = resourceEntries.get(e);
                resource.addAsOutput(recipe.value);
                recipe.value.addOutput(resource);
            }
        }

        for (ObjectMap.Entry<String, BuildingEntry> building : buildingEntries) {
            JsonValue buildingData = building.value.data;

            Array<RecipeEntry> validRecipes = null;
            if (buildingData.get("Group") != null) {
                validRecipes = new Array<>();
                addBuildingRecipes(buildingData.get("Group"), validRecipes, recipeEntries);
            }

            if (validRecipes != null) {
                ObjectSet<RecipeEntry> seen = new ObjectSet<>();
                Array<RecipeEntry> unique = new Array<>();

                for (RecipeEntry item : validRecipes) {
                    if (seen.add(item)) {
                        unique.add(item);
                    }
                }
                validRecipes = unique;

                validRecipes.sort((r1, r2) -> r1.name.compareTo(r2.name));

                for (RecipeEntry rec : validRecipes) {
                    rec.addCraftBuilding(building.value);
                    building.value.addCraftRecipe(rec);
                }
            }
        }

        for (ObjectMap.Entry<String, RecipeEntry> recipe : recipeEntries) {
            recipe.value.craftBuildings.sort((r1, r2) -> r1.name.compareTo(r2.name));
        }

        Resources.addAll(resourceEntries.values().iterator().toArray());
        Resources.sort((r1, r2) -> r1.name.compareTo(r2.name));

        Recipes.addAll(recipeEntries.values().iterator().toArray());
        Recipes.sort((r1, r2) -> r1.name.compareTo(r2.name));

        Buildings.addAll(buildingEntries.values().iterator().toArray());
        Buildings.sort((r1, r2) -> r1.name.compareTo(r2.name));

        Enemies.addAll(enemyEntries.values().iterator().toArray());
        Enemies.sort((r1, r2) -> r1.name.compareTo(r2.name));

        Relics.addAll(relicEntries.values().iterator().toArray());
        Relics.sort((r1, r2) -> r1.name.compareTo(r2.name));

    }


    private static void addBuildingRecipes(JsonValue data, Array<RecipeEntry> recipesArray, ObjectMap<String, RecipeEntry> recipes) {
        if (data.isArray()) {
            data.forEach( item -> addBuildingRecipes(item, recipesArray, recipes));
        } else {
            for (Recipe rec : RecipeGroupDatabase.get(data.asString())) {
                recipesArray.add(recipes.get(rec.getId()));
            }
        }
    }
}

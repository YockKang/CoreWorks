package com.main.CoreWorks.Codex;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
    protected static final Table CodexTable = new Table();
    protected static final Container<Actor> TableInDiv = new Container<>(CodexTable);
    protected static final Container<Actor> selectedCategoryDiv = new Container<>();
    protected static final Container<Actor> selectedItemDiv = new Container<>();
    static final Table ContentTable = new Table();
    private static final Table ResourcesList = new Table();
    private static final Table RecipesList = new Table();
    private static final Table BuildingsList = new Table();
    private static final Table EnemiesList = new Table();
    private static final Table RelicsList = new Table();
    static Actor selectedItem;
    static Actor selectedCategory;
    public static boolean isOnScreen = false;

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

            if (buildingData.get("Whitelist") != null) {
                String[] whitelistArr = buildingData.get("Whitelist").asStringArray();
                Array<String> whitelist = new Array<>(whitelistArr);
                for (String rsc : whitelist) {
                    building.value.addWhitelistResource(resourceEntries.get(rsc));
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
        Buildings.sort((r1, r2) -> {
            int r1prio = Integer.MAX_VALUE;
            int r2prio = Integer.MAX_VALUE;
            switch (r1) {
                case MinerEntry minerEntry -> {
                    r1prio = 0;
                }
                case RefinerEntry refinerEntry -> {
                    r1prio = 1;
                }
                case ShooterEntry shooterEntry -> {
                    r1prio = 2;
                }
                case DefenderEntry defenderEntry -> {
                    r1prio = 3;
                }
                default -> {}
            }
            switch (r2) {
                case MinerEntry minerEntry -> {
                    r2prio = 0;
                }
                case RefinerEntry refinerEntry -> {
                    r2prio = 1;
                }
                case ShooterEntry shooterEntry -> {
                    r2prio = 2;
                }
                case DefenderEntry defenderEntry -> {
                    r2prio = 3;
                }
                default -> {}
            }
            if (r1prio != r2prio) {
                return r1prio - r2prio;
            } else {
                int r1tier = ((BuildingEntry) r1).data.getInt("Tier");
                int r2tier = ((BuildingEntry) r2).data.getInt("Tier");
                if (r1tier != r2tier) {
                    return r1tier - r2tier;
                } else {
                    return r1.name.compareTo(r2.name);
                }
            }
        });

        Enemies.addAll(enemyEntries.values().iterator().toArray());
        Enemies.sort((r1, r2) -> r1.name.compareTo(r2.name));

        Relics.addAll(relicEntries.values().iterator().toArray());
        Relics.sort((r1, r2) -> r1.name.compareTo(r2.name));

    }

    public static void generateInfoTable(Skin skin) {
        CodexTable.setSkin(skin);
        CodexTable.setBackground("default-round");
        TextButton exitButton = new TextButton("X", skin);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isOnScreen = false;
                TableInDiv.remove();
            }
        });
        exitButton.setColor(new Color(.75f, 0, 0, 1));
        exitButton.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                exitButton.addAction(Actions.color(new Color(1, 0, 0, 1), 0.15f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                exitButton.addAction(Actions.color(new Color(.75f, 0, 0, 1), 0.15f));
            }
        });
        CodexTable.add(exitButton).top().right().row();
        CodexTable.add(new Label("Codex", skin)).row();


        selectedCategoryDiv.minHeight(200);
        selectedCategoryDiv.minWidth(400);
        selectedItemDiv.minWidth(400);
        ContentTable.add(selectedCategoryDiv);
        ContentTable.add(selectedItemDiv);

        ResourcesList.defaults().pad(5);
        BuildingsList.defaults().pad(5);
        RecipesList.defaults().pad(5);
        EnemiesList.defaults().pad(5);
        RelicsList.defaults().pad(5);

        int itemsPerRow = 3;
        int inThisRow = 0;
        for (Entry resource : Resources) {
            TextButton selectButton = new TextButton(resource.name, skin);
            selectButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    setSelectedItem(resource);
                }
            });
            selectButton.addListener(new InputListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    selectButton.addAction(Actions.color(new Color(.7f, .7f, .7f, 1), 0.15f));
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    selectButton.addAction(Actions.color(new Color(.9f, .9f, .9f, 1), 0.15f));
                }
            });

            ResourcesList.add(selectButton);
            inThisRow++;
            if (inThisRow % itemsPerRow == 0) {
                inThisRow = 0;
                ResourcesList.row();
            }
        }

        inThisRow = 0;
        for (Entry recipe : Recipes) {
            TextButton selectButton = new TextButton(recipe.name, skin);
            selectButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    setSelectedItem(recipe);
                }
            });
            selectButton.addListener(new InputListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    selectButton.addAction(Actions.color(new Color(.7f, .7f, .7f, 1), 0.15f));
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    selectButton.addAction(Actions.color(new Color(.9f, .9f, .9f, 1), 0.15f));
                }
            });

            RecipesList.add(selectButton);
            inThisRow++;
            if (inThisRow % itemsPerRow == 0) {
                inThisRow = 0;
                RecipesList.row();
            }
        }

        inThisRow = 0;
        for (int i = 0; i < Buildings.size; i++) {
            Entry building = Buildings.get(i);
            Entry nextEntry = null;
            if (i < Buildings.size - 2) {
                nextEntry = Buildings.get(i+1);
            }
            TextButton selectButton = new TextButton(building.name, skin);
            selectButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    setSelectedItem(building);
                }
            });
            selectButton.addListener(new InputListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    selectButton.addAction(Actions.color(new Color(.7f, .7f, .7f, 1), 0.15f));
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    selectButton.addAction(Actions.color(new Color(.9f, .9f, .9f, 1), 0.15f));
                }
            });

            BuildingsList.add(selectButton);
            inThisRow++;
            if (inThisRow % itemsPerRow == 0 || (i < Buildings.size - 2 && building.getClass() != nextEntry.getClass())) {
                inThisRow = 0;
                BuildingsList.row();
            }
        }

        inThisRow = 0;
        for (Entry enemy : Enemies) {
            TextButton selectButton = new TextButton(enemy.name, skin);
            selectButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    setSelectedItem(enemy);
                }
            });
            selectButton.addListener(new InputListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    selectButton.addAction(Actions.color(new Color(.7f, .7f, .7f, 1), 0.15f));
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    selectButton.addAction(Actions.color(new Color(.9f, .9f, .9f, 1), 0.15f));
                }
            });

            EnemiesList.add(selectButton);
            inThisRow++;
            if (inThisRow % itemsPerRow == 0) {
                inThisRow = 0;
                EnemiesList.row();
            }
        }

        inThisRow = 0;
        for (Entry relic : Relics) {
            TextButton selectButton = new TextButton(relic.name, skin);
            selectButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    setSelectedItem(relic);
                }
            });
            selectButton.addListener(new InputListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    selectButton.addAction(Actions.color(new Color(.7f, .7f, .7f, 1), 0.15f));
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    selectButton.addAction(Actions.color(new Color(.9f, .9f, .9f, 1), 0.15f));
                }
            });

            RelicsList.add(selectButton);
            inThisRow++;
            if (inThisRow % itemsPerRow == 0) {
                inThisRow = 0;
                RelicsList.row();
            }
        }

        Table headerTable = new Table();
        headerTable.defaults().pad(5);

        TextButton resourceHeader = new TextButton("Resources", skin);
        resourceHeader.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedCategory(ResourcesList);
            }
        });
        headerTable.add(resourceHeader);

        TextButton recipeHeader = new TextButton("Recipes", skin);
        recipeHeader.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedCategory(RecipesList);
            }
        });
        headerTable.add(recipeHeader);

        TextButton buildingHeader = new TextButton("Buildings", skin);
        buildingHeader.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedCategory(BuildingsList);
            }
        });
        headerTable.add(buildingHeader);

        TextButton enemyHeader = new TextButton("Enemies", skin);
        enemyHeader.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelectedCategory(EnemiesList);
            }
        });
        headerTable.add(enemyHeader);

        TextButton relicHeader = new TextButton("Relics", skin);
        relicHeader.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
               setSelectedCategory(RelicsList);
            }
        });
        headerTable.add(relicHeader);
        CodexTable.add(headerTable);
        CodexTable.row();


        Container<Actor> contentDiv = new Container<>(ContentTable);

        CodexTable.add(contentDiv);


        for (Entry e : Resources) {
            System.out.println(e.name);
            e.generateInfoTable(skin);
        }
        for (Entry e : Recipes) {
            System.out.println(e.name);
            e.generateInfoTable(skin);
        }
        for (Entry e : Buildings) {
            System.out.println(e.name);
            e.generateInfoTable(skin);
        }
        for (Entry e : Enemies) {
            System.out.println(e.name);
            e.generateInfoTable(skin);
        }
        for (Entry e : Relics) {
            System.out.println(e.name);
            e.generateInfoTable(skin);
        }
    }

    private static void addBuildingRecipes(JsonValue data, Array<RecipeEntry> recipesArray, ObjectMap<String, RecipeEntry> recipes) {
        if (data.isArray()) {
            data.forEach(item -> addBuildingRecipes(item, recipesArray, recipes));
        } else {
            for (Recipe rec : RecipeGroupDatabase.get(data.asString())) {
                recipesArray.add(recipes.get(rec.getId()));
            }
        }
    }

    public static Actor getTable() {
        return CodexTable;
    }

    public static Container<Actor> getTableInDiv() {
        return TableInDiv;
    }

    protected static void setSelectedCategory(Table category) {
        selectedItem = null;
        selectedItemDiv.setActor(null);
        selectedCategory = category;
        selectedCategoryDiv.setActor(category);
    }

    protected static void setSelectedItem(Entry e) {
        selectedItemDiv.setActor(e.infoTable);
        selectedItem = e.infoTable;
    }
}

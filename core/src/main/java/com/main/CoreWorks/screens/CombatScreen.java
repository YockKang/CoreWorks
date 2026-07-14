package com.main.CoreWorks.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Coreworks;
import com.main.CoreWorks.Recipe.Recipe;
import com.main.CoreWorks.entities.Relics.Relic;
import com.main.CoreWorks.util.*;
import com.main.CoreWorks.Factory.*;
import com.main.CoreWorks.Factory.Tubes.Tube;
import com.main.CoreWorks.RunPersistence.RunState;
import com.main.CoreWorks.entities.*;
import com.main.CoreWorks.simulators.*;

public class CombatScreen implements Screen {

    Coreworks game;
    RunState runState;
    CombatController controller;
    private float accumulator = 0f;
    private static final float TIME_STEP = 1 / 4f; // 4 Ticks per second
    private int tickCount = 0;
    private Vector2 mouse2DCoords = new Vector2();
    private ShapeRenderer shapeRenderer;
    private Coords hoveredGridCoords = null;
    private boolean hoveredCanPlace = false;
    private boolean isPaused = true;

    // tube placement fields
    private boolean tubeMode = false;
    DirectedCoords downPoint;
    DirectedCoords upPoint;

    // recipe UI fields
    private boolean recipeUIOn = false;


    // Below field handles the scene2D UI
    private Stage stage;
    private Skin skin;
    private boolean needRefresh = true;


    // Hardcoded grid size for milestone 1 testing purposes
    // Should be deleted eventually since it should be handled by the global runState which carries over the factory
    private final int gridWidth;
    private final int gridHeight;

    // Temp Layout since we have not decided how we want the final UI to look like yet
    // Rmb that everything is drawn in a coordinate system (check Coreworks class for the public static final screen size)
    private final int gridSize = 450;

    private final int tileSize;

    private final int gridMidX = (int) (Coreworks.VIEWPORT_WIDTH / 2);
    private final int gridMidY = 420;

    private final int gridStartX;
    private final int gridEndX;
    private final int gridEndY;
    private final int gridStartY;

    private final int inventoryStartX = 512;
    private final int inventoryStartY = 40;
    // Each inventory slot is a square for now
    private final int inventorySlotSize = 96;
    private final int inventorySlotGap = 16;

    private Building selectedBuilding;

    private ObjectMap<String, Actor> UIElements = new ObjectMap<>();
    private Queue<Label> combatLog = new Queue<>();
    private Recipe selectedRecipe;

    public CombatScreen(Coreworks game, RunState runstate, Array<Enemy> enemies) {

        this.game = game;
        this.runState = runstate;
        // Initialize the controllers
        FactorySim factorySim = new FactorySim(runstate.getFactoryGrid());
        CombatSim combatSim = new CombatSim(runstate.getPlayer(), enemies);
        this.controller = new CombatController(factorySim, combatSim);
        // Initialize the grid based on what is present in the player's factory grid
        this.gridWidth = runstate.getPlayer().getFactoryGrid().getMaxWidth();
        this.gridHeight = runstate.getPlayer().getFactoryGrid().getMaxHeight();
        this.tileSize = Math.min(gridSize / gridWidth, gridSize / gridHeight);
        this.gridStartX = gridMidX - tileSize * gridWidth / 2;
        this.gridEndX = gridMidX - tileSize * gridWidth / 2;
        this.gridEndY = gridMidY + tileSize * gridHeight / 2;
        this.gridStartY = gridMidY - tileSize * gridHeight / 2;

    }

    @Override
    public void show() {
        stage = new Stage(game.viewport, game.batch);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        Gdx.input.setInputProcessor(stage);

        game.viewport.apply();
        game.camera.update();
        shapeRenderer = new ShapeRenderer();

        // The below builds the scene2D UI overlay for everything but the grid and its related functions
        buildCombatUI();
    }

    public void buildCombatUI() {
        stage.clear();

        // assign all fixed UI elements
        UIElements.put("MasterTable", new Table(skin));
        UIElements.put("CenterStack", new Stack());

        // Info Sheets
        UIElements.put("infotableL", new Table(skin));
        UIElements.put("infotableR", new Table(skin));
        UIElements.put("title", new Label("Coreworks", skin));
        UIElements.put("tickcount", new Label("Tick:\n" + tickCount, skin));
        UIElements.put("playerdata", new Label(runState.getPlayer().toString(), skin));
        UIElements.put("buildingselect", new Label("Selected: None ", skin));
        UIElements.put("rotationhelp", new Label("Q/E\nRotate Buildings", skin));
        ((Label) UIElements.get("rotationhelp")).setAlignment(Align.center);
        UIElements.put("recipeselecthelp", new Label("R\nChange Selected\nBuilding Recipe", skin));
        ((Label) UIElements.get("recipeselecthelp")).setAlignment(Align.center);
        UIElements.put("tubemode", new Label("T\nAdd Tubes", skin));
        ((Label) UIElements.get("tubemode")).setAlignment(Align.center);

        UIElements.put("paused", new Label("PAUSED\nPress Space to Continue", skin));
        ((Label) UIElements.get("paused")).setAlignment(Align.center);


        // combat log
        UIElements.put("logtable", new Table(skin));
        UIElements.put("logheader", new Label("Combat Log:", skin));
        UIElements.put("logbody", new Table(skin));


        // enemies
        UIElements.put("enemytable", new Table(skin));
        UIElements.put("enemyheader", new Label("Enemies:", skin));
        UIElements.put("enemybody", new Table(skin));

        // inventory
        UIElements.put("inventorytable", new Table(skin));
        UIElements.put("inventoryheader", new Label("Inventory", skin));
        UIElements.put("inventorybody", new Table(skin));

        // relics
        UIElements.put("relictable", new Table(skin));

        // recipe
        UIElements.put("recipetable", new Table(skin));
        UIElements.put("recipeheader", new Label("Recipes for", skin));
        UIElements.put("recipeselect", new Table(skin));
        UIElements.put("recipedisplay", new Table(skin));
        UIElements.put("recipeinfo", new Table(skin));
        ((Table) UIElements.get("recipeinfo")).add(new Label("Selected: None", skin));

        Actor factoryViewport = new Actor();
        UIElements.put("factoryviewport", factoryViewport);

        Stack centerStack = (Stack) UIElements.get("CenterStack");
        Table maintable = (Table) UIElements.get("MasterTable");
        centerStack.setFillParent(true);
        stage.addActor(centerStack);
        centerStack.add(maintable);

        // subsections of the screen
        /*
        +-------+---------+-------+
        | left  | top bar | right |
        | bar   +---------+ bar   |
        |       | factory |       |
        +-------+---------+-------+
        |       bottom bar        |
        +-------------------------+
         */
        Table topBar = new Table(skin);
        UIElements.put("topbar", topBar);
        Table bottomBar = new Table(skin);
        UIElements.put("bottombar", bottomBar);
        Table middle = new Table(skin);
        Table leftBar = new Table(skin);
        UIElements.put("leftbar", leftBar);
        Table rightBar = new Table(skin);
        UIElements.put("rightbar", rightBar);
        Table upperBar = new Table(skin);

        // debugging box boundaries
        /*
        topBar.setBackground("default-round");
        bottomBar.setBackground("default-round");
        leftBar.setBackground("default-round");
        rightBar.setBackground("default-round");
         */

        // assembling middle
        middle.add(topBar).growX().height(Coreworks.VIEWPORT_HEIGHT - gridEndY).row();
        middle.add(factoryViewport).size(gridSize);

        // assembling upperBar
        upperBar.add(leftBar).width((Coreworks.VIEWPORT_WIDTH - gridSize) / 2).growY();
        upperBar.add(middle).expand().fill();
        upperBar.add(rightBar).width((Coreworks.VIEWPORT_WIDTH - gridSize) / 2).growY();

        // assembling maintable
        maintable.add(upperBar);
        maintable.row();
        maintable.add(bottomBar).growX().height(gridStartY);

        // making UI elements
        // top-left info
        Table infotableL = (Table) (UIElements.get("infotableL"));
        infotableL.add(UIElements.get("title")).pad(5);
        infotableL.add(UIElements.get("tickcount")).pad(5).row();
        infotableL.add(UIElements.get("playerdata")).pad(5);
        infotableL.add(UIElements.get("buildingselect")).pad(5).growX();

        // top-right info
        Table infotableR = (Table) (UIElements.get("infotableR"));
        infotableR.add(UIElements.get("tubemode")).pad(5);
        infotableR.add(UIElements.get("rotationhelp")).pad(5);
        infotableR.add(UIElements.get("recipeselecthelp")).pad(5);

        // inventory
        Table inventoryTable = (Table) UIElements.get("inventorytable");
        inventoryTable.add(UIElements.get("inventoryheader")).pad(10).row();
        inventoryTable.add(UIElements.get("inventorybody")).growY().row();
        updateInventoryUI();

        // combat log (empty, to be filled when actions happen)
        Table logTable = (Table) UIElements.get("logtable");
        logTable.add(UIElements.get("logheader")).row();
        logTable.add(UIElements.get("logbody")).pad(10).growY().row();
        updateCombatLog();

        // Enemy Cards (empty, to be filled on start)
        Table enemyTable = (Table) UIElements.get("enemytable");
        enemyTable.add(UIElements.get("enemyheader")).row();
        enemyTable.add(UIElements.get("enemybody")).pad(10).growY().row();
        updateEnemies();

        // relics
        Table relicTable = (Table) UIElements.get("relictable");
        relicTable.clear();
        topBar.add(relicTable);

        for (Relic relic : runState.getRelics()) {
            Table table = new Table(skin);
            Label label3 = new Label(relic.getName(), skin);
            label3.setColor(Color.GOLD);
            Tooltip<Table> descToolTip = new Tooltip<>(relic.getDescription().toTable(skin));
            descToolTip.setInstant(true);
            label3.addListener(descToolTip);
            table.setBackground("default-round");
            table.add(label3);
            relicTable.add(table).pad(2);
        }

        // Recipe Select Cards (empty, to be filled when used)
        Table recipeTable = (Table) UIElements.get("recipetable");
        Container<Actor> recipeDiv = new Container<>(recipeTable);
        UIElements.put("recipediv", recipeDiv);
        recipeTable.top().center();
        recipeTable.add(UIElements.get("recipeheader")).colspan(2).row();
        UIElements.get("recipeselect").setWidth(200);
        recipeTable.add(UIElements.get("recipeselect"));
        UIElements.get("recipedisplay").setWidth(100);
        ((Table) UIElements.get("recipedisplay")).add(UIElements.get("recipeinfo"));
        recipeTable.add(UIElements.get("recipedisplay")).row();


        TextButton cancelButton = new TextButton("Cancel", skin);
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

        TextButton confirmButton = new TextButton("Confirm", skin);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedRecipe != null) {
                    selectedBuilding.setRecipe(selectedRecipe);
                    clearRecipeUI();
                    needRefresh = true;
                }
            }
        });
        confirmButton.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                confirmButton.addAction(Actions.color(new Color(0, 1, 0, 1), 0.15f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                confirmButton.addAction(Actions.color(new Color(0, .75f, 0, 1), 0.15f));
            }
        });

        recipeTable.add(cancelButton).right().pad(2);
        recipeTable.add(confirmButton).left().pad(2);

        recipeTable.setBackground("default-round");

        // filling in contents
        leftBar.add(infotableL).growX().top().pad(20).row();
        leftBar.add(inventoryTable).growY().row();

        rightBar.add(infotableR).pad(20).top().row();
        rightBar.add(logTable).growY().row();

        bottomBar.add(enemyTable).pad(5);


        // OLD CODE DELETE LATER
        /*
        Table table = new Table();
        table.setFillParent(true);
        table.setName("MainTable");
        // stage.addActor(table);

        table.top();

        // The below builds the top part of the UI + pause screen
        Table topTable = new Table();
        topTable.setName("TopBar");
        topTable.top().left().pad(10);
        topTable.add(new Label("Coreworks", skin)).pad(10);
        topTable.add(new Label("Tick:\n" + tickCount, skin)).pad(10);
        topTable.add(new Label(runState.getPlayer().toString(), skin)).pad(10);
        if (selectedBuilding == null) {
            topTable.add(new Label("Selected: None ", skin)).pad(10);
        } else {
            topTable.add(new Label("Selected: " + selectedBuilding.displayName(), skin)).pad(10);
            topTable.add(new Label("Press Q/E to rotate \n Current rotation: " + selectedBuilding.getRotation(), skin)).pad(10);
        }
        if (tubeMode) {
            topTable.add(new Label("Adding Tubes\nPress T to exit", skin)).pad(10);
        } else {
            topTable.add(new Label("Press T to add Tubes", skin)).pad(10);
        }
        if (isPaused) {
            topTable.add(new Label("PAUSED \nPress Space to Continue", skin)).pad(10);
        }
        topTable.add(new Label("Use Mousewheel to scroll\nthe enemy display and\ncombat Log when paused", skin)).pad(10);
        table.add(topTable).colspan(3).expandX().row();

        // Create a middle table to handle other parts of the HUD
        Table middleTable = new Table();

        // The below builds the combat Log
        Table logTable = new Table();
        logTable.top().center();
        logTable.add(new Label("Combat Log:", skin)).row();
        Array<String> log = controller.getCombatSim().getCombatLog();
        int logsThisTick = controller.getCombatSim().getLogsThisTick();
        int start = Math.max(0, log.size - logsThisTick);
        for (int i = start; i < log.size; i++) {
            logTable.add(new Label(log.get(i), skin)).right().row();
        }
        middleTable.add(logTable).expand().top().row();

        // The below builds the enemy display
        Table enemyTable = new Table();
        enemyTable.top().center().pad(10);
        enemyTable.defaults().width(185);
        enemyTable.add(new Label("Enemies:", skin)).row();
        Array<Enemy> enemies = controller.getCombatSim().getEnemies();
        int enemyCount = 0;
        int maxEnemyPerRow = 2;
        for (Enemy enemy : enemies) {
            // Draw the enemy in a table (disguised as a card) to look neater
            Table enemyCard = new Table(skin);
            enemyCard.setBackground("default-round");
            enemyCard.defaults().pad(2);
            enemyCard.add(new Label(enemy.toString(), skin));
            enemyTable.add(enemyCard).pad(2);
            enemyCount++;
            if (enemyCount % maxEnemyPerRow == 0) {
                enemyTable.row();
            }
        }
        middleTable.add(enemyTable).top();

        // Allows both the enemy table and combat log to be scrollable so it doesn't screw over the inventory
        ScrollPane middleScroll = new ScrollPane(middleTable, skin);
        middleScroll.setScrollingDisabled(true, false);
        middleScroll.setColor(Color.BLACK);

        // The below builds the inventoryTable
        Table inventoryTable = new Table();
        inventoryTable.left();
        inventoryTable.add(new Label("Inventory", skin)).row();
        Table buildingsInInv = new Table();
        buildingsInInv.defaults().width(120).height(60).pad(5);
        Array<Building> inventory = controller.getCombatSim().getPlayer().getInventory();
        int maxBuildingsPerRow = 3;
        int buildingCount = 0;

        for (Building building : inventory) {
            TextButton buildingButton = new TextButton(building.displayName(), skin);
            if (building == selectedBuilding) {
                buildingButton.setColor(Color.GREEN);
            }
            if (!recipeUIOn) {
                buildingButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        selectedBuilding = building;
                        needRefresh = true;
                    }
                });
            }
            buildingsInInv.add(buildingButton);
            buildingCount++;
            if (buildingCount % maxBuildingsPerRow == 0) {
                buildingsInInv.row();
            }
        }
        inventoryTable.add(buildingsInInv);

        // Create a body table with a center spacer for the grid, left is inventory and right is the combat log & enemy list
        Table body = new Table();
        body.add(inventoryTable).width(250).left();
        body.add().expandX();
        body.add(middleScroll).expandX().right();

        table.add(body).expand().fill().row();

        // Simple String-based UI to display all relics at the bottom (To be improved on eventually)
        table.add(new Label("Relics:", skin)).row();
        Table relicTable = new Table();
        for (Relic relic : runState.getRelics()) {
            Label label3 = new Label("- " + relic.getName(), skin);
            label3.setColor(Color.GOLD);
            Tooltip<Table> descToolTip = new Tooltip<>(relic.getDescription().toTable(skin));
            descToolTip.setInstant(true);
            label3.addListener(descToolTip);
            relicTable.add(label3);
        }
        table.add(relicTable).row();

        if (recipeUIOn) {
            Table recipeTable = new Table();
            recipeTable.setFillParent(true);
            recipeTable.setName("RecipeUI");
            recipeTable.top().center();
            recipeTable.add(new Label("hi fafuiwhiulGFWRYkjlHWDPFGAWOPIKL;FMNBQHWGUIFKJNWMEDVHIUOAPq  kmnqbjGUYIUHIJURGVNJhaio tbftryugibkjgvFRY5 J HYGCTBDJrbfynhrjgytnb", skin));

            stage.addActor(recipeTable);

        }

         */
        needRefresh = false;
    }

    private void updateEnemies() {
        // The below builds the enemy display
        Table enemyTable = (Table) UIElements.get("enemybody");
        enemyTable.clear();
        enemyTable.defaults().width(185);
        Array<Enemy> enemies = controller.getCombatSim().getEnemies();
        int enemyCount = 0;
        int maxEnemyPerRow = 15;
        for (Enemy enemy : enemies) {
            // Draw the enemy in a table (disguised as a card) to look neater
            Table enemyCard = new Table(skin);
            enemyCard.setBackground("default-round");
            enemyCard.defaults().pad(2);
            enemyCard.add(new Label(enemy.toString(), skin));
            enemyTable.add(enemyCard).pad(2);
            enemyCount++;
            if (enemyCount % maxEnemyPerRow == 0) {
                enemyTable.row();
            }
        }
    }

    private void updateCombatLog() {
        Table logTable = (Table) UIElements.get("logbody");
        Array<String> log = controller.getCombatSim().getCombatLog();
        int newlines = controller.getCombatSim().getLogsThisTick();
        int start = Math.max(0, log.size - newlines);
        for (int i = start; i < log.size; i++) {
            Label newlog = new Label(log.get(i), skin);
            logTable.add(newlog).right().row();
            combatLog.addLast(newlog);
        }
        while (combatLog.size > 10) {
            Label oldlog = combatLog.removeFirst();
            oldlog.remove();
        }
        controller.getCombatSim().assertLogUpdated();
    }

    private void updateInventoryUI() {
        // The below rebuilds the inventory body
        Table buildingsInInv = (Table) UIElements.get("inventorybody");
        buildingsInInv.clear();
        buildingsInInv.defaults().width(120).height(60).pad(5);
        Array<Building> inventory = controller.getCombatSim().getPlayer().getInventory();
        int maxBuildingsPerRow = 3;
        int buildingCount = 0;

        for (Building building : inventory) {
            TextButton buildingButton = new TextButton(building.gridName(), skin);
            if (building == selectedBuilding) {
                buildingButton.setColor(Color.GREEN);
            }
            buildingButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!recipeUIOn) {
                        // dont allow building selecting with recipe UI enabled
                        selectedBuilding = building;
                        needRefresh = true;
                    }
                }
            });
            buildingsInInv.add(buildingButton);
            buildingCount++;
            if (buildingCount % maxBuildingsPerRow == 0) {
                buildingsInInv.row();
            }
        }
    }

    private void rebuildRecipeUI() {
        // The below rebuilds the recipe selection
        ((Label) UIElements.get("recipeheader")).setText("Recipes for " + selectedBuilding.displayName());

        Table recipeSelect = (Table) UIElements.get("recipeselect");
        recipeSelect.clear();
        recipeSelect.defaults().height(30).pad(5);
        Array<Recipe> craftable = selectedBuilding.getValidRecipes();
        int maxRecipesPerRow = 3;
        int recipeCount = 0;

        for (Recipe recipe : craftable) {
            TextButton recipeButton = new TextButton(recipe.getName(), skin);
            if (recipe == selectedRecipe) {
                recipeButton.setColor(Color.GREEN);
            }
            recipeButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectedRecipe = recipe;
                    displayRecipeSelection();
                }
            });
            recipeSelect.add(recipeButton);
            recipeCount++;
            if (recipeCount % maxRecipesPerRow == 0) {
                recipeSelect.row();
            }
        }
    }

    private void displayRecipeSelection() {
        UIElements.get("recipeinfo").clear();
        if (selectedRecipe == null) {
            ((Table) UIElements.get("recipeinfo")).add(new Label("Selected: None", skin));
        } else {
            ((Table) UIElements.get("recipeinfo")).add(selectedRecipe.displayStats(skin));
        }
    }

    private void clearRecipeUI() {
        UIElements.get("recipediv").remove();
        selectedRecipe = null;
        recipeUIOn = false;
        UIElements.get("recipeinfo").clear();
        ((Table) UIElements.get("recipeinfo")).add(new Label("Selected: None", skin));
    }


    private void updateUI() {
        ((Label) UIElements.get("tickcount")).setText("Tick:\n" + tickCount);
        ((Label) UIElements.get("playerdata")).setText(runState.getPlayer().toString());
        if (selectedBuilding != null) {
            ((Label) UIElements.get("buildingselect")).setText("Selected: " + selectedBuilding.displayName());
        } else {
            ((Label) UIElements.get("buildingselect")).setText("Selected: None");
        }
        updateInventoryUI();
        updateCombatLog();
        needRefresh = false;
    }

    @Override
    public void render(float delta) {
        // Anti-"Lag spike spiral of death" code
        delta = Math.min(delta, 1 / 8f);

        externalInput();

        // Tick Advancement code below
        if (!isPaused && !controller.isWin() && !controller.isLost()) {
            accumulator += delta;
            while (accumulator >= TIME_STEP) {
                System.out.println();
                System.out.println("Tick " + tickCount);
                controller.advanceTick(runState, tickCount);
                tickCount += 1;
                accumulator -= TIME_STEP;
                updateEnemies();
                needRefresh = true;
            }
        }

        // Clears the screen + update camera if needed
        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.camera.update();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(game.viewport.getCamera().combined);

        // Drawing functions below
        drawGrid();
        drawPlacementPreview();
        drawBuildings();
        drawIOPorts();

        // Draws the Scene2D UI
        if (needRefresh) {
            updateUI();
        }
        stage.act(delta);
        stage.draw();

        // Handles win/loss screen transitions
        checkWinLoss();
    }

    /*
    All drawing related functions should be handled from here on
     */

    public void drawPlacementPreview() {
        if (selectedBuilding == null || hoveredGridCoords == null || selectedBuilding.isOnGrid()) {
            return;
        }

        boolean[][] rotatedShape = selectedBuilding.getProjectedShape();

        // Set Green if valid, Red if not valid
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (hoveredCanPlace) {
            shapeRenderer.setColor(Color.GREEN);
        } else {
            shapeRenderer.setColor(Color.RED);
        }

        // Draws the preview
        for (int y = 0; y < rotatedShape.length; y++) {
            for (int x = 0; x < rotatedShape[y].length; x++) {
                if (!rotatedShape[y][x]) { // If not filled, do not draw anything
                    continue;
                }

                // Get the base x,y coord in the grid 2D shape array for drawing
                int gridX = hoveredGridCoords.x + x;
                int gridY = hoveredGridCoords.y + y;

                // For now, no drawing of any parts of the building outside of grid
                // When we have sprites maybe can change this part
                if (gridX < 0 || gridY < 0 || gridX >= gridWidth || gridY >= gridHeight) {
                    continue;
                }

                float tileX = gridStartX + gridX * tileSize;
                float tileY = gridEndY - (gridY + 1) * tileSize;

                shapeRenderer.rect(tileX, tileY, tileSize, tileSize);
            }
        }

        shapeRenderer.end();
    }

    public void drawIOPorts() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.CYAN);

        for (Building building : controller.getFactorySim().getGrid().getBuildings()) {
            // Code here handles the IOPort drawing, Line for now until we get proper sprites
            for (IOPort port : building.getPorts()) {
                DirectedCoords globalPortCoords = building.getPortGlobalCoords(port);

                float drawX = gridStartX + globalPortCoords.x * tileSize + tileSize / 2f;
                float drawY = gridEndY - globalPortCoords.y * tileSize - tileSize / 2f;

                drawCardinalArrow(drawX, drawY, globalPortCoords.dir, 20, 4);
            }
        }

        shapeRenderer.end();
    }

    public void drawCardinalArrow(float x, float y, int direction, float length, float arrowSize) {
        float endX = x;
        float endY = y;

        // Draw the arrow line
        switch (direction) {
            case 0:
                endY += length;
                break;
            case 1:
                endX += length;
                break;
            case 2:
                endY -= length;
                break;
            case 3:
                endX -= length;
                break;
        }

        shapeRenderer.rectLine(x, y, endX, endY, 1);

        // Draw the arrowhead
        switch (direction) {
            case 0:
                shapeRenderer.triangle(endX, endY, endX - arrowSize, endY - arrowSize, endX + arrowSize, endY - arrowSize);
                break;
            case 1:
                shapeRenderer.triangle(endX, endY, endX - arrowSize, endY - arrowSize, endX - arrowSize, endY + arrowSize);
                break;
            case 2:
                shapeRenderer.triangle(endX, endY, endX - arrowSize, endY + arrowSize, endX + arrowSize, endY + arrowSize);
                break;
            case 3:
                shapeRenderer.triangle(endX, endY, endX + arrowSize, endY - arrowSize, endX + arrowSize, endY + arrowSize);
                break;
        }
    }

    public void drawGrid() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draws the Outline of occupied grids
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                int bottomLeftCorner = gridStartX + x * tileSize;
                int topLeftCorner = gridEndY - (y + 1) * tileSize; // offset by one since libGDX stores its object origins in the bottom left
                Structure occupied = controller.getFactorySim().getGrid().getStructureAt(x, y);

                // If there is a non-disabled building, draw it as blue
                if (occupied instanceof Building bldg) {
                    if (bldg.isEnabled()) {
                        shapeRenderer.setColor(Color.BLUE);
                        shapeRenderer.rect(bottomLeftCorner, topLeftCorner, tileSize, tileSize);
                    }

                    // If there is a disabled building, draw it as yellow
                    if (!bldg.isEnabled()) {
                        shapeRenderer.setColor(Color.YELLOW);
                        shapeRenderer.rect(bottomLeftCorner, topLeftCorner, tileSize, tileSize);
                    }
                } else if (occupied instanceof Tube tube) {
                    // if its a tube, do something else
                    int i = 0;
                    for (boolean conn : tube.getConnections1()) {
                        if (conn) {
                            pipeDrawSwitcher(i, bottomLeftCorner, topLeftCorner, Color.GRAY);
                        }
                        i++;
                    }
                    if (tube.getDouble()) {
                        i = 0;
                        for (boolean conn : tube.getConnections2()) {
                            if (conn) {
                                pipeDrawSwitcher(i, bottomLeftCorner, topLeftCorner, Color.LIGHT_GRAY);
                            }
                            i++;
                        }
                    }
                }
            }
        }

        shapeRenderer.end();

        // Draws the outline of unoccupied grids
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        if (isPaused) {
            shapeRenderer.setColor(Color.RED);
        } else {
            shapeRenderer.setColor(Color.WHITE);
        }

        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                int bottomLeftCorner = gridStartX + x * tileSize;
                int topLeftCorner = gridEndY - (y + 1) * tileSize; // offset by one since libGDX shape draws the object origins in the bottom left, but we start from top left
                shapeRenderer.rect(bottomLeftCorner, topLeftCorner, tileSize, tileSize);
            }
        }

        shapeRenderer.end();
    }

    public void drawBuildings() {
        game.batch.begin();

        for (Building building : controller.getFactorySim().getGrid().getBuildings()) {
            Coords coords = building.getGlobalCoord(0, 0);
            float nameX = gridStartX + coords.x * tileSize + 10;
            float nameY = gridEndY - coords.y * tileSize - 20;
            game.font.getData().setScale(0.75f);
            game.font.draw(game.batch, building.gridName(), nameX, nameY);
            if (building.getRecipe() != null) {
                game.font.draw(game.batch, building.getRecipe().getName(), nameX, nameY - 30);
            }
        }

        game.font.getData().setScale(1f);
        game.batch.end();
    }

    public void checkWinLoss() {
        // Below draws the screen transitions
        if (controller.isWin()) {
            controller.getFactorySim().clear();
            game.resetCamera();
            game.setScreen(new WinScreen(game, runState));
            return;
        } else if (controller.isLost()) {
            controller.getFactorySim().clear();
            game.resetCamera();
            game.setScreen(new LoseScreen(game));
            return;
        }
    }

    /*
    All mouse inputs should be handled here
     */

    private void externalInput() {
        // Keyboard inputs handled below
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            System.out.println(selectedBuilding);
        }

        // Press E to rotate building CW, Q for CCW
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (selectedBuilding == null || selectedBuilding.isOnGrid()) {
                return;
            }
            int nextRotation = (selectedBuilding.getRotation() + 1) % 4;
            selectedBuilding.setRotation(nextRotation);
            needRefresh = true;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            if (selectedBuilding == null || selectedBuilding.isOnGrid()) {
                return;
            }
            int nextRotation = (selectedBuilding.getRotation() - 1) % 4;
            selectedBuilding.setRotation(nextRotation);
            needRefresh = true;
        }

        // Pause will be tied to Spacebar
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            isPaused = !isPaused;
            /*
            if (isPaused) {
                stage.addActor(UIElements.get("paused"));
            } else {
                UIElements.get("paused").remove();
            }
            needRefresh = true;
             */
        }

        // Tube placement mode is T (for now)
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            if (!recipeUIOn) {
                tubeMode = !tubeMode;
                if (tubeMode) {
                    // deselect building
                    selectedBuilding = null;
                    // tube mode handling here for now
                    Gdx.input.setInputProcessor(new InputAdapter() {

                        @Override
                        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                            if (button == Input.Buttons.LEFT) {
                                Vector2 mTCoords = translateMouseToWorld();
                                downPoint = getGridQuadrantAt(mTCoords.x, mTCoords.y);
                            }
                            return true;
                        }

                        @Override
                        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                            if (button == Input.Buttons.LEFT) {
                                Vector2 mTCoords = translateMouseToWorld();
                                upPoint = getGridQuadrantAt(mTCoords.x, mTCoords.y);
                            }
                            return true;
                        }

                    });
                } else {
                    Gdx.input.setInputProcessor(stage);
                    downPoint = null;
                    upPoint = null;
                }
                needRefresh = true;
            }
        }

        // Recipe selection is R (for now)
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            if (selectedBuilding != null && selectedBuilding.isOnGrid() && selectedBuilding.getValidRecipes() != null) {
                recipeUIOn = !recipeUIOn;
                if (recipeUIOn) {
                    ((Stack) UIElements.get("CenterStack")).add(UIElements.get("recipediv"));
                    rebuildRecipeUI();
                } else {
                    clearRecipeUI();
                }
            }
        }

        // Mouse inputs handled below
        Vector2 mouseTranslatedCoords = translateMouseToWorld();

        float mouseTranslatedX = mouseTranslatedCoords.x;
        float mouseTranslatedY = mouseTranslatedCoords.y;

        Actor hit = stage.hit(mouseTranslatedX, mouseTranslatedY, true);

        // Handles potential bugs with scene2D UI and grid inputs by prioritizing scene2D UI if there is an overlap (except the reserved space for the grid itself)
        if (stage.hit(mouseTranslatedX, mouseTranslatedY, true) != null &&
            hit != UIElements.get("factoryviewport")) {
            return;
        }

        // Handles Placement preview via mouse hovering
        hoveredGridCoords = getGridAt(mouseTranslatedX, mouseTranslatedY);

        if (!tubeMode) {
            // Handles Placement preview via mouse hovering
            if (selectedBuilding != null && hoveredGridCoords != null) {
                hoveredGridCoords = getGridAt(
                    mouseTranslatedX - (float) (selectedBuilding.getProjectedShape()[0].length * tileSize) / 2 + tileSize / 2,
                    mouseTranslatedY + (float) (selectedBuilding.getProjectedShape().length * tileSize) / 2 - tileSize / 2);
                if (hoveredGridCoords != null) {
                    hoveredCanPlace = controller.getFactorySim().getGrid().checkValidPosition(selectedBuilding, hoveredGridCoords.x, hoveredGridCoords.y, selectedBuilding.getRotation());
                } else {
                    hoveredCanPlace = false;
                }
            } else {
                hoveredCanPlace = false;
            }

            // Handles left and right clicks
            if (Gdx.input.justTouched()) {
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    leftClick(mouseTranslatedX, mouseTranslatedY);
                }

                if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                    rightClick(mouseTranslatedX, mouseTranslatedY);
                }
            }
        } else {
            if (upPoint != null) {
                if (downPoint != null) {
                    if (upPoint.x == downPoint.x &&
                        upPoint.y == downPoint.y &&
                        upPoint.dir != downPoint.dir) {
                        controller.getFactorySim().getGrid().addTube(upPoint.x, upPoint.y, downPoint.dir, upPoint.dir);
                    }
                }
                upPoint = null;
                downPoint = null;
            }
            if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                controller.getFactorySim().getGrid().removeTube(hoveredGridCoords.x, hoveredGridCoords.y);
            }
        }
    }


    // Translates mouse coordinates to world coordinates
    private Vector2 translateMouseToWorld() {
        mouse2DCoords.set(Gdx.input.getX(), Gdx.input.getY());
        game.viewport.unproject(mouse2DCoords);
        return mouse2DCoords;
    }

    /*
    Left clicks will handle (c.a.a Milestone 1)
        1. Selecting a building from inventory
        2. Placing it on the grid if grid is clicked + a building is selected in inventory
     */

    private void leftClick(float mouseTranslatedX, float mouseTranslatedY) {
        if (hoveredGridCoords != null && selectedBuilding != null && !selectedBuilding.isOnGrid()) {
            boolean successfulPlacement = controller.getFactorySim().getGrid().placeBuilding(selectedBuilding, hoveredGridCoords.x, hoveredGridCoords.y, selectedBuilding.getRotation());
            if (successfulPlacement) {
                controller.getCombatSim().getPlayer().removeBuilding(selectedBuilding);
                needRefresh = true;
                selectedBuilding = null;
                return;
            }
        }
        hoveredGridCoords = getGridAt(mouseTranslatedX, mouseTranslatedY);
        if (hoveredGridCoords != null) {
            selectedBuilding = controller.getFactorySim().getGrid().getBuildingAt(hoveredGridCoords.x, hoveredGridCoords.y);
            needRefresh = true;
        }
    }

    /*
    Right clicks will handle (c.a.a Milestone 3)
        1. Deselecting a building
        2. Removing building from the grid if grid is clicked back into inventory
        3. exiting recipe UI
     */

    private void rightClick(float mouseTranslatedX, float mouseTranslatedY) {
        if (recipeUIOn) {
            clearRecipeUI();
        } else {
            Coords coords = getGridAt(mouseTranslatedX, mouseTranslatedY);
            if (selectedBuilding != null || coords == null) {
                selectedBuilding = null;
            } else {
                Building building = controller.getFactorySim().getGrid().removeBuilding(coords.x, coords.y);
                if (building != null) {
                    controller.getCombatSim().getPlayer().addBuilding(building);
                }
            }
        }
        needRefresh = true;

    }

    // draws pipes
    private void pipeDrawSwitcher(int rot, float tileX, float tileY, Color colour) {
        shapeRenderer.setColor(colour);
        switch (rot) {
            case 0 -> {
                shapeRenderer.rect(
                    tileX + (float) tileSize / 3,
                    tileY + (float) tileSize / 3,
                    (float) tileSize / 3,
                    (float) (2 * tileSize) / 3);
            }
            case 1 -> {
                shapeRenderer.rect(
                    tileX + (float) tileSize / 3,
                    tileY + (float) tileSize / 3,
                    (float) (2 * tileSize) / 3,
                    (float) tileSize / 3);
            }
            case 2 -> {
                shapeRenderer.rect(
                    tileX + (float) tileSize / 3,
                    tileY,
                    (float) tileSize / 3,
                    (float) (2 * tileSize) / 3);
            }
            case 3 -> {
                shapeRenderer.rect(
                    tileX,
                    tileY + (float) tileSize / 3,
                    (float) (2 * tileSize) / 3,
                    (float) tileSize / 3);
            }
        }
    }

    // Generic code that translates mouse clicks on grid into an x and y coord of a 2D array (in this case grid's 2D array)
    private Coords getGridAt(float mouseTranslatedX, float mouseTranslatedY) {
        boolean insideWholeGridX = mouseTranslatedX >= gridStartX && mouseTranslatedX < gridStartX + gridWidth * tileSize;
        boolean insideWholeGridY = mouseTranslatedY <= gridEndY && mouseTranslatedY > gridEndY - gridHeight * tileSize;

        if (!insideWholeGridX || !insideWholeGridY) {
            return null;
        }

        // Since the whole grid is scaled up by size (including both its x and y coords), we can divide the mouse grid coordinates by the tile size to scale it back down to get the unscaled tile coordinate
        int unscaledTileX = (int) ((mouseTranslatedX - gridStartX) / (tileSize));
        int unscaledTileY = (int) ((gridEndY - mouseTranslatedY) / (tileSize));

        return new Coords(unscaledTileX, unscaledTileY);
    }

    // Get quadrant of a grid (may be helpful for pointing at stuff)
    private DirectedCoords getGridQuadrantAt(float mouseTranslatedX, float mouseTranslatedY) {
        boolean insideWholeGridX = mouseTranslatedX >= gridStartX && mouseTranslatedX < gridStartX + gridWidth * tileSize;
        boolean insideWholeGridY = mouseTranslatedY <= gridEndY && mouseTranslatedY > gridEndY - gridHeight * tileSize;

        if (!insideWholeGridX || !insideWholeGridY) {
            return null;
        }

        // Since the whole grid is scaled up by size (including both its x and y coords), we can divide the mouse grid coordinates by the tile size to scale it back down to get the unscaled tile coordinate
        int unscaledTileX = (int) ((mouseTranslatedX - gridStartX) / (tileSize));
        int unscaledTileY = (int) ((gridEndY - mouseTranslatedY) / (tileSize));

        int tileX = (int) ((mouseTranslatedX - gridStartX) % tileSize);
        int tileY = (int) ((gridEndY - mouseTranslatedY) % tileSize);
        boolean topRight = tileX > tileY;
        boolean botRight = tileX > (tileSize - tileY);

        int dir;

        if (topRight) {
            if (botRight) {
                dir = 1;
            } else {
                dir = 0;
            }
        } else {
            if (botRight) {
                dir = 2;
            } else {
                dir = 3;
            }
        }

        return new DirectedCoords(unscaledTileX, unscaledTileY, dir);
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void pause() {
        // TBD
    }

    @Override
    public void resume() {
        // TBD
    }

    @Override
    public void hide() {
        // TBD
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        stage.dispose();
        skin.dispose();
    }
}

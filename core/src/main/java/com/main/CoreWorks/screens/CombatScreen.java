package com.main.CoreWorks.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Codex.Codex;
import com.main.CoreWorks.Coreworks;
import com.main.CoreWorks.Recipe.Recipe;
import com.main.CoreWorks.Resources.Resource;
import com.main.CoreWorks.TextParser.Sentence;
import com.main.CoreWorks.TextParser.Text;
import com.main.CoreWorks.database.ResourceDatabase;
import com.main.CoreWorks.entities.Relics.Relic;
import com.main.CoreWorks.simulators.PopUpTutorial.PopUpManager;
import com.main.CoreWorks.util.*;
import com.main.CoreWorks.Factory.*;
import com.main.CoreWorks.Factory.Tubes.Tube;
import com.main.CoreWorks.RunPersistence.RunState;
import com.main.CoreWorks.entities.*;
import com.main.CoreWorks.simulators.*;

public class CombatScreen extends GameScreen {

    private RunState runState;
    private CombatController controller;
    private double accumulator = 0;
    private static final double TIME_STEP = (double) 1 / 4; // 4 Ticks per second
    private static final int[] speeds = new int[]{1, 2, 3, 4, 10, -1};
    private static final double[] trueSpeeds = new double[speeds.length];
    private static int speedPointer = 0;
    private int tickCount = 0;
    private Vector2 mouse2DCoords = new Vector2();
    private ShapeRenderer shapeRenderer;
    private Coords hoveredGridCoords = null;
    private boolean hoveredCanPlace = false;
    private boolean isPaused = true;

    // performace tools
    private final Queue<Double> lastNTicks = new Queue<>();
    private double lastNTicksSum = 0;
    private final int nTicks = 40;
    private boolean resetTPSCount;

    // tube placement fields
    private boolean tubeMode = false;
    private DirectedCoords downPoint;
    private DirectedCoords upPoint;
    private InputProcessor tubelogger = new InputAdapter() {
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if (button == Input.Buttons.LEFT) {
                Vector2 mTCoords = translateMouseToWorld();
                downPoint = getGridQuadrantAt(mTCoords.x, mTCoords.y);
            }
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (button == Input.Buttons.LEFT) {
                Vector2 mTCoords = translateMouseToWorld();
                upPoint = getGridQuadrantAt(mTCoords.x, mTCoords.y);
            }
            return false;
        }
    };


    // recipe UI fields
    private boolean recipeUIOn = false;

    // Below field handles the scene2D UI
    private final InputMultiplexer multiplexer = new InputMultiplexer();
    private Skin skin75pct;
    private Skin skin50pct;
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

    private Queue<Table> combatLog = new Queue<>();
    private Recipe selectedRecipe;

    public CombatScreen(Coreworks game, RunState runstate, Array<Enemy> enemies) {
        super(game);
        this.runState = runstate;
        // Initialize the controllers
        FactorySim factorySim = new FactorySim(runstate.getFactoryGrid());
        CombatSim combatSim = new CombatSim(game, runstate.getPlayer(), enemies);
        this.controller = new CombatController(factorySim, combatSim);
        // Initialize the grid based on what is present in the player's factory grid
        this.gridWidth = runstate.getPlayer().getFactoryGrid().getMaxWidth();
        this.gridHeight = runstate.getPlayer().getFactoryGrid().getMaxHeight();
        this.tileSize = Math.min(gridSize / gridWidth, gridSize / gridHeight);
        this.gridStartX = gridMidX - tileSize * gridWidth / 2;
        this.gridEndX = gridMidX + tileSize * gridWidth / 2;
        this.gridEndY = gridMidY + tileSize * gridHeight / 2;
        this.gridStartY = gridMidY - tileSize * gridHeight / 2;
    }

    public static void calculateSpeeds() {
        for (int i = 0; i < speeds.length; i++) {
            if (speeds[i] > 0) {
                trueSpeeds[i] = TIME_STEP / speeds[i];
            } else {
                trueSpeeds[i] = 0;
            }
        }
    }

    @Override
    public void show() {
        skin75pct = new Skin(Gdx.files.internal("uiskin.json"));

        BitmapFont font75 = skin75pct.getFont("default-font");
        font75.getData().setScale(0.75f);
        font75.setUseIntegerPositions(false);

        skin50pct = new Skin(Gdx.files.internal("uiskin.json"));

        BitmapFont font50 = skin75pct.getFont("default-font");
        font50.getData().setScale(0.75f);
        font50.setUseIntegerPositions(false);

        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);

        // Sets the popup manager
        game.getPopUpManager().setScene2D(stage, skin);

        game.getPopUpManager().requestPopup(
            "combat_screen",
            "Combat",
            "This is the combat screen where all fights will occur in.\nThe next few pop-ups will explain the UI, so PAY CAREFUL ATTENTION!",
            true
        );

        game.getPopUpManager().requestPopup(
            "top_hud_explanation",
            "Top HUD",
            "The top of the screen displays all the relics you have.\nHover over them to see what they do!",
            true
        );

        game.getPopUpManager().requestPopup(
            "left_hud_explanation",
            "Left HUD",
            "The left of the screen displays player & game information, such as Health, Money, current game tick etc.\nIt also displays your inventory, which you can interact with for building selection.\nMore information about buildings will be given when you select a building via left click.",
            true
        );

        game.getPopUpManager().requestPopup(
            "bottom_hud_explanation",
            "Bottom HUD",
            "The bottom of the screen displays the list of your enemies.\nEach enemy card will have additional information, such as its next move, health, cooldowns etc.\nRefer to the codex for more detailed information about each enemy.",
            true
        );

        game.getPopUpManager().requestPopup(
            "right_hud_explanation",
            "Right HUD",
            "The right of the screen displays the combat log and keyboard shortcuts.\nThe combat log records every damaging action taken, be it friend or foe.\nRefer to it to see what combat actions your factory / enemies have taken.\nMore information about the keyboard shortcuts will appear when you use those shortcuts!",
            true
        );

        game.getPopUpManager().requestPopup(
            "middle_hud_explanation",
            "Middle HUD",
            "The middle of the screen displays your factory grid.\nIt is where all your buildings will be placed, and where all your factory actions are processed.\nA building only works if placed in the grid, and is inactive in the inventory.\nYou need to consider the space taken by buildings when placing them, since the grid isn't infinite!",
            true
        );

        game.getPopUpManager().requestPopup(
            "tick_explanation",
            "Combat & ticks",
            "The combat system runs on a tick-based system.\nBuildings operate automatically, and take actions based on passed number of ticks.\nEnemies take action after certain number of ticks have passed, represented by cooldowns." +
                "\nYou win if all enemies are defeated." +
                "\nYou lose if your health drops to 0." +
                "\nDifferent buildings can defeat enemies in different ways. Refer to codex with C for more information!",
            true
        );

        game.getPopUpManager().requestPopup(
            "Pause_explanation",
            "Pausing / Unpausing",
            "You can use SpaceBar to pause / unpause the game." +
                "\nWhen paused, the grid will be outlined RED, otherwise it will be outlined WHITE." +
                "\nWhen paused, you can still do everything as usual, but no ticks will progress." +
                "\nThis means no production or actions can be taken, which can help with devising strategies without time pressure!",
            true
        );

        game.viewport.apply();
        game.camera.update();
        shapeRenderer = new ShapeRenderer();

        // The below builds the scene2D UI overlay for everything but the grid and its related functions
        buildCombatUI();
    }

    public void buildCombatUI() {
        // assign all fixed UI elements
        UIElements.put("MasterTable", new Table(skin));
        UIElements.put("CenterStack", centerStack);

        // Info Sheets
        UIElements.put("infotableL", new Table(skin));
        UIElements.put("infotableR", new Table(skin));
        UIElements.put("title", new Label("Coreworks", skin));
        UIElements.put("tickcount", new Label("Tick:\n" + tickCount, skin));
        UIElements.put("codexhelp", new Label("C\n Toggle Codex", skin));
        UIElements.put("playerdata", new Label(runState.getPlayer().toString(), skin));
        UIElements.put("speedSelect", new Table(skin));
        UIElements.put("rotationhelp", new Label("Q/E\nRotate Buildings", skin));
        ((Label) UIElements.get("rotationhelp")).setAlignment(Align.center);
        UIElements.put("recipeselecthelp", new Label("R\nChange Selected\nBuilding Recipe", skin));
        ((Label) UIElements.get("recipeselecthelp")).setAlignment(Align.center);
        UIElements.put("tubemode", new Label("", skin));
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

        BuildingToolTip buildingCard = new BuildingToolTip();
        stage.addActor(buildingCard);
        UIElements.put("buildingtooltip", buildingCard);

        // generate tooltip cards for buildings
        factoryViewport.addListener(
            new InputListener() {
                @Override
                public boolean mouseMoved(InputEvent event, float x, float y) {
                    hoveredGridCoords = getGridAt(translateMouseToWorld().x, translateMouseToWorld().y);
                    Building hoveredBuilding = null;
                    if (hoveredGridCoords != null) {
                        hoveredBuilding = controller.getFactorySim().getGrid().getBuildingAt(hoveredGridCoords.x, hoveredGridCoords.y);
                    }

                    if (hoveredBuilding != null && !recipeUIOn && !codexOnScreen) {
                        buildingCard.show(hoveredBuilding.tooltipDisplay(skin), event.getStageX(), event.getStageY());
                    } else {
                        buildingCard.hide();
                    }

                    return false;
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    buildingCard.hide();
                }
            }
        );

        UIElements.put("factoryviewport", factoryViewport);

        Table maintable = (Table) UIElements.get("MasterTable");
        centerStack.clearChildren();
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

        float gridBlockStartY = gridMidY - (float) gridSize / 2;
        float gridBlockEndY = gridMidY + (float) gridSize / 2;

        // assembling middle
        middle.add(topBar).growX().height(Coreworks.VIEWPORT_HEIGHT - gridBlockEndY).row();
        middle.add(factoryViewport).size(gridSize);

        // assembling upperBar
        upperBar.add(leftBar).width((Coreworks.VIEWPORT_WIDTH - gridSize) / 2).growY();
        upperBar.add(middle).expand().fill();
        upperBar.add(rightBar).width((Coreworks.VIEWPORT_WIDTH - gridSize) / 2).growY();

        // assembling maintable
        maintable.add(upperBar);
        maintable.row();
        maintable.add(bottomBar).growX().height(gridBlockStartY);

        // making UI elements
        // top-left info
        Table infotableL = (Table) (UIElements.get("infotableL"));
        infotableL.add(UIElements.get("title")).pad(5);
        infotableL.add(UIElements.get("tickcount")).pad(5);
        infotableL.add(UIElements.get("codexhelp")).pad(5).row();
        infotableL.add(UIElements.get("playerdata")).pad(5);
        infotableL.add(UIElements.get("speedSelect")).pad(5).colspan(2).growX();
        Table speedSelect = (Table) UIElements.get("speedSelect");
        speedSelect.defaults().pad(2);
        TextButton slowDownButton = new TextButton("<<", skin);
        slowDownButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                slowDownTime();
            }
        });
        TextButton fastForwardButton = new TextButton(">>", skin);
        fastForwardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                speedUpTime();
            }
        });

        Label currentSpeed = new Label("1x", skin);

        UIElements.put("currentspeed", currentSpeed);
        UIElements.put("actualTPS", new Label("0", skin));
        UIElements.put("projectedTPS", new Label("(4)", skin));

        speedSelect.add(slowDownButton);
        speedSelect.add(currentSpeed);
        speedSelect.add(fastForwardButton);
        speedSelect.row();
        speedSelect.add(UIElements.get("actualTPS"));
        speedSelect.add();
        speedSelect.add(UIElements.get("projectedTPS"));
        updateSpeedDisplay();


        // top-right info
        Table infotableR = (Table) (UIElements.get("infotableR"));
        infotableR.add(UIElements.get("tubemode")).pad(5);
        infotableR.add(UIElements.get("rotationhelp")).pad(5);
        infotableR.add(UIElements.get("recipeselecthelp")).pad(5);

        // inventory
        Table inventoryTable = (Table) UIElements.get("inventorytable");
        inventoryTable.add(UIElements.get("inventoryheader")).pad(10).row();
        ScrollPane inventoryScroller = new ScrollPane(UIElements.get("inventorybody"));
        inventoryScroller.setScrollingDisabled(true, false);
        inventoryTable.add(inventoryScroller).growY().row();
        updateInventoryUI();

        // combat log (empty, to be filled when actions happen)
        Table logTable = (Table) UIElements.get("logtable");
        logTable.add(UIElements.get("logheader")).row();
        logTable.add(UIElements.get("logbody")).pad(10).growY().row();
        updateCombatLog();

        // Enemy Cards (empty, to be filled on start)
        Table enemyTable = (Table) UIElements.get("enemytable");
        enemyTable.add(UIElements.get("enemyheader")).row();
        ScrollPane enemyScroller = new ScrollPane(UIElements.get("enemybody"));
        inventoryScroller.setScrollingDisabled(false, true);
        enemyTable.add(enemyScroller).pad(10).growY().row();
        updateEnemies();

        // relics
        Table relicTable = (Table) UIElements.get("relictable");
        relicTable.clear();
        topBar.add(relicTable);

        for (Relic relic : runState.getRelics()) {
            Table table = new Table(skin);
            Label label3 = new Label(relic.getName(), skin);
            label3.setColor(Color.GOLD);
            Tooltip<Table> descToolTip = new Tooltip<>(relic.getDescription().toTable(skin, Align.center));
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
                // Play button click sound
                game.getSoundManager().playButtonClick();
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
        cancelButton.setColor(new Color(.75f, 0, 0, 1));

        TextButton confirmButton = new TextButton("Confirm", skin);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedRecipe != null) {
                    // Play button click sound
                    game.getSoundManager().playButtonClick();
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
        confirmButton.setColor(new Color(0, .75f, 0, 1));

        recipeTable.add(cancelButton).right().pad(2);
        recipeTable.add(confirmButton).left().pad(2);

        recipeTable.setBackground("default-round");

        // filling in contents
        leftBar.add(infotableL).growX().top().pad(20).row();
        leftBar.add(inventoryTable).growY().row();

        rightBar.add(infotableR).pad(20).top().row();
        rightBar.add(logTable).growY().row();

        bottomBar.add(enemyTable).pad(5);

        tubeUIRefresh();

        needRefresh = false;
    }

    private void speedUpTime() {
        if (speedPointer < speeds.length - 1) {
            speedPointer++;
            updateSpeedDisplay();
        }
    }


    private void slowDownTime() {
        if (speedPointer > 0) {
            speedPointer--;
            updateSpeedDisplay();
        }
    }

    private void updateSpeedDisplay() {
        Label currSpeed = ((Label) UIElements.get("currentspeed"));
        Label projTPS = ((Label) UIElements.get("projectedTPS"));
        int speedMult = speeds[speedPointer];
        double speed = trueSpeeds[speedPointer];
        if (speedMult < 0) {
            currSpeed.setText("Max");
            projTPS.setText("(Max)");
        } else {
            currSpeed.setText(speedMult + "x");
            projTPS.setText("(" + MathExtras.roundDP(1 / speed, 1) + ")");
        }
        resetTPSCount = true;
    }

    private void updateEnemies() {
        // The below builds the enemy display
        Table enemyTable = (Table) UIElements.get("enemybody");
        enemyTable.clear();
        enemyTable.defaults().width(150);
        Array<Enemy> enemies = controller.getCombatSim().getEnemies();
        for (Enemy enemy : enemies) {
            // Draw the enemy in a table (disguised as a card) to look neater
            Table enemyCard = new Table(skin);
            enemyCard.setBackground("default-round");
            enemyCard.defaults().pad(2);
            enemyCard.add(new Label(enemy.toString(), skin75pct));
            enemyTable.add(enemyCard).pad(2);
        }
    }

    private void updateCombatLog() {
        Table logTable = (Table) UIElements.get("logbody");
        Array<Sentence> log = controller.getCombatSim().getCombatLog();
        int newlines = controller.getCombatSim().getLogsThisTick();
        int start = Math.max(0, log.size - newlines);
        for (int i = start; i < log.size; i++) {
            Table newlog = log.get(i).toTable(skin75pct, Align.center);
            logTable.add(newlog).right().row();
            combatLog.addLast(newlog);
        }
        while (combatLog.size > 15) {
            Table oldlog = combatLog.removeFirst();
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
            TextButton buildingButton = new TextButton(building.gridName(), skin75pct);

            if (building == selectedBuilding) {
                buildingButton.setColor(Color.GREEN);
            }
            buildingButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Play button click sound
                    game.getSoundManager().playButtonClick();

                    if (!(recipeUIOn || codexOnScreen || tubeMode)) {
                        // allow building selecting when recipe UI and codex are both off-screen and not in tubeMode
                        selectedBuilding = building;
                        needRefresh = true;
                    }
                    game.getPopUpManager().requestPopup(
                        "building_selection_explanation",
                        "Building selection & placement",
                        "You can select buildings from inventory & place them onto the grid with left click.\nYou can deselect selected buildings & remove buildings from the grid with right click.",
                        true
                    );
                    game.getPopUpManager().requestPopup(
                        "building_factory_explanation",
                        "Building operations & factory",
                        "When buildings are placed on the grid, they will operate automatically every tick.\nBuildings can have output ports, represented by small arrows on the building when placed on the grid.\nThese output ports are directions where buildings will output their resources, if any." +
                            "\nBuildings that can take in inputs can do so from any direction not occupied by an output port.",
                        true
                    );
                    game.getPopUpManager().requestPopup(
                        "building_types_explanation",
                        "Building types",
                        "There are different types of buildings, each with their own unique roles." +
                            "\nMiners produce raw resources for your factory, and can only output but do not take inputs." +
                            "\nRefiners refine raw resources into better products, and can both output and take inputs." +
                            "\nDefenders take in products to produce healing / shielding directly, and do not have outputs." +
                            "\nShooters take in products to take actions against enemies, and do not have outputs." +
                            "\nTo know exact details about buildings, refer to the codex by pressing button C.",
                        true
                    );
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
        game.batch.setColor(Color.WHITE);
    }

    private void displayRecipeSelection() {
        UIElements.get("recipeinfo").clear();
        if (selectedRecipe == null) {
            ((Table) UIElements.get("recipeinfo")).add(new Label("Selected: None", skin));
        } else {
            ((Table) UIElements.get("recipeinfo")).add(selectedRecipe.displayStats(selectedBuilding, skin));
        }
    }

    private void clearRecipeUI() {
        UIElements.get("recipediv").remove();
        selectedRecipe = null;
        recipeUIOn = false;
        UIElements.get("recipeinfo").clear();
        ((Table) UIElements.get("recipeinfo")).add(new Label("Selected: None", skin));
    }

    private void updateTPS() {
        if (lastNTicks.size > 0) {
            double avg = lastNTicksSum / lastNTicks.size;
            ((Label) UIElements.get("actualTPS")).setText(String.valueOf(MathExtras.roundDP(1 / avg, 1)));
        } else {
            ((Label) UIElements.get("actualTPS")).setText(0);
        }
    }

    private void tubeUIRefresh() {
        Label tubeHelper = (Label) UIElements.get("tubemode");
        if (tubeMode) {
            tubeHelper.setText("T\nTo Exit\n" + controller.getCombatSim().getPlayer().getTubeBudget() + " Remaining");
        } else {
            tubeHelper.setText("T\nAdd Tubes\n" + controller.getCombatSim().getPlayer().getTubeBudget() + " Remaining");
        }
    }

    private void updateUI() {
        ((Label) UIElements.get("tickcount")).setText("Tick:\n" + tickCount);
        ((Label) UIElements.get("playerdata")).setText(runState.getPlayer().toString());
        updateInventoryUI();
        updateCombatLog();
        updateTPS();
        needRefresh = false;
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        double minInterval = trueSpeeds[speedPointer];

        externalInput();

        // PopUp manager will spawn the next popup if needed (and exists), while pausing the game as well
        PopUpManager popUpManager = game.getPopUpManager();
        if (popUpManager != null && popUpManager.showNext(() -> isPaused = true, () -> isPaused = true)) {
            ScreenUtils.clear(Color.BLACK);
            stage.act(delta);
            stage.draw();
            return;
        }

        // Tick Advancement code below
        if (!isPaused && !controller.isWin() && !controller.isLost()) {
            if (delta >= minInterval || accumulator >= minInterval * 2) {
                System.out.println();
                System.out.println("Tick " + tickCount);
                controller.advanceTick(runState, tickCount);
                tickCount += 1;
                accumulator = 0;
                recordTick(delta);
                updateEnemies();
                needRefresh = true;
            } else {
                accumulator += delta;
                if (accumulator >= minInterval) {
                    System.out.println();
                    System.out.println("Tick " + tickCount);
                    controller.advanceTick(runState, tickCount);
                    tickCount += 1;
                    recordTick(minInterval);
                    accumulator -= minInterval;
                    updateEnemies();
                    needRefresh = true;
                }
            }

        }

        // Clears the screen + update camera if needed
        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.camera.update();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.setColor(Color.WHITE);
        // Drawing functions below
        drawGrid();
        drawBuildings();
        // drawIOPorts();
        drawPlacementPreview();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Draws the Scene2D UI
        if (needRefresh) {
            updateUI();
        }
        stage.act(delta);
        stage.draw();

        // Handles win/loss screen transitions
        checkWinLoss();

        if (resetTPSCount) {
            lastNTicks.clear();
            resetTPSCount = false;
            lastNTicksSum = 0;
        }
    }

    private void recordTick(double time) {
        lastNTicks.addLast(time);
        lastNTicksSum += time;
        if (lastNTicks.size > nTicks) {
            lastNTicksSum -= lastNTicks.removeFirst();
        }
    }

    /*
    All drawing related functions should be handled from here on
     */

    public void drawPlacementPreview() {
        if (selectedBuilding == null || hoveredGridCoords == null || selectedBuilding.isOnGrid()) {
            return;
        }

        boolean[][] rotatedShape = selectedBuilding.getProjectedShape();

        if (hoveredGridCoords.x + rotatedShape[0].length - 1 >= gridWidth || hoveredGridCoords.y + rotatedShape.length - 1 >= gridHeight) {
            return;
        }

        // Set Green if valid, Red if not valid
        if (hoveredCanPlace) {
            game.batch.setColor(1, 1, 1, .75f);
        } else {
            game.batch.setColor(1, 0, 0, .75f);
        }

        float scale = (float) tileSize / 44;

        game.batch.begin();

        // Draws the preview
        for (int y = 0; y < rotatedShape.length; y++) {
            for (int x = 0; x < rotatedShape[y].length; x++) {
                int leftEdge = gridStartX + (hoveredGridCoords.x + x) * tileSize;
                int bottomEdge = gridEndY - (hoveredGridCoords.y + y + 1) * tileSize; // offset by one since libGDX stores its object origins in the bottom left


                TextureRegion textureRegions = selectedBuilding.getTexture()[selectedBuilding.getRotation()][0];

                boolean[][] shape = selectedBuilding.getShape();

                TextureRegion[][] buildingImage = textureRegions.split(
                    44,
                    44
                );

                game.batch.draw(buildingImage[y][x],
                    leftEdge,
                    bottomEdge,
                    44 * scale,
                    44 * scale
                );

            }
        }
        game.batch.setColor(0, 0, 0, 1);
        game.batch.end();







        game.getPopUpManager().requestPopup(
            "placement_preview_explanation",
            "Placement Preview",
            "When a building is selected and you hover over the grid, you can see a preview of its position.\nGreen means placement is valid, Red means placement invalid.\nYou can only place buildings when the placement is valid." +
                "\nThe preview does NOT show direction of output ports (if any).",
            true
        );
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

        // Draws the outline of unoccupied grids
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        if (isPaused) {
            shapeRenderer.setColor(Color.RED);
        } else {
            shapeRenderer.setColor(Color.WHITE);
        }

        for (
            int x = 0;
            x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                int bottomLeftCorner = gridStartX + x * tileSize;
                int topLeftCorner = gridEndY - (y + 1) * tileSize; // offset by one since libGDX shape draws the object origins in the bottom left, but we start from top left
                shapeRenderer.rect(bottomLeftCorner, topLeftCorner, tileSize, tileSize);
            }
        }


        shapeRenderer.end();


        game.batch.begin();

        // Draws the Outline of occupied grids
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                int leftEdge = gridStartX + x * tileSize;
                int bottomEdge = gridEndY - (y + 1) * tileSize; // offset by one since libGDX stores its object origins in the bottom left
                Structure occupied = controller.getFactorySim().getGrid().getStructureAt(x, y);

                float scale = (float) tileSize / 44;
                // If there is a building, draw it
                if (occupied instanceof Building building) {
                    TextureRegion textureRegions = building.getTexture()[building.getRotation()][0];

                    boolean[][] shape = building.getShape();

                    int largeAxis = Math.max(shape.length, shape[0].length);
                    TextureRegion[][] buildingImage = textureRegions.split(
                        44,
                        44
                    );

                    Coords imgLocCords = new Coords(x - building.getX(), y - building.getY());

                    game.batch.draw(buildingImage[imgLocCords.y][imgLocCords.x],
                        leftEdge,
                        bottomEdge,
                        44 * scale,
                        44 * scale
                    );

                } else if (occupied instanceof Tube tube) {
                    // if its a tube, do something else

                    pipeDrawSwitcher(tube, leftEdge, bottomEdge);

                }
            }
        }
        game.batch.end();

        // draw border
        /*
        for (Building building : controller.getFactorySim().getGrid().getBuildings()) {
            Array<DirectedCoords> border = building.getBorder();
            Color drawColor = null;
            if (building == selectedBuilding) {
                drawColor = Color.WHITE;
            } else {
                drawColor = Color.GRAY;
            }
            for (DirectedCoords coords : border) {
                DirectedCoords trueLocation = building.getGlobalCoord(coords.x, coords.y).addDirection((coords.dir + building.getRotation()) % 4);
                int botLeftX = gridStartX + trueLocation.x * tileSize;
                int botLeftY = gridEndY - (trueLocation.y + 1) * tileSize; // offset by one since libGDX stores its object origins in the bottom left
                shapeRenderer.setColor(drawColor);
                switch (trueLocation.dir) {
                    case 0 -> {
                        shapeRenderer.rect(
                            botLeftX,
                            botLeftY + tileSize - (float) tileSize / 20,
                            tileSize,
                            (float) tileSize / 20);
                    }
                    case 1 -> {
                        shapeRenderer.rect(
                            botLeftX + tileSize - (float) tileSize / 20,
                            botLeftY,
                            (float) tileSize / 20,
                            tileSize);
                    }
                    case 2 -> {
                        shapeRenderer.rect(
                            botLeftX,
                            botLeftY,
                            tileSize,
                            (float) tileSize / 20);
                    }
                    case 3 -> {
                        shapeRenderer.rect(
                            botLeftX,
                            botLeftY,
                            (float) tileSize / 20,
                            tileSize);
                    }
                }
            }
         */


        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // new border (selected only)
        if (selectedBuilding != null && selectedBuilding.isOnGrid()) {
            Array<DirectedCoords> border = selectedBuilding.getBorder();
            Color drawColor = new Color(1, 1, 1, 0.4f);

            for (DirectedCoords coords : border) {
                DirectedCoords trueLocation = selectedBuilding.getGlobalCoord(coords.x, coords.y).addDirection((coords.dir + selectedBuilding.getRotation()) % 4);
                trueLocation = trueLocation.pointingToSide();
                int botLeftX = gridStartX + trueLocation.x * tileSize;
                int botLeftY = gridEndY - (trueLocation.y + 1) * tileSize; // offset by one since libGDX stores its object origins in the bottom left
                shapeRenderer.setColor(drawColor);
                switch (trueLocation.dir) {
                    case 0 -> {
                        shapeRenderer.rect(
                            botLeftX,
                            botLeftY + tileSize - (float) tileSize / 20,
                            tileSize,
                            (float) tileSize / 20);
                    }
                    case 1 -> {
                        shapeRenderer.rect(
                            botLeftX + tileSize - (float) tileSize / 20,
                            botLeftY,
                            (float) tileSize / 20,
                            tileSize);
                    }
                    case 2 -> {
                        shapeRenderer.rect(
                            botLeftX,
                            botLeftY,
                            tileSize,
                            (float) tileSize / 20);
                    }
                    case 3 -> {
                        shapeRenderer.rect(
                            botLeftX,
                            botLeftY,
                            (float) tileSize / 20,
                            tileSize);
                    }
                }
            }
        }
        shapeRenderer.end();

    }

    public void drawBuildings() {
        game.batch.begin();


        for (Building building : controller.getFactorySim().getGrid().getBuildings()) {

            float scale = (float) (tileSize * 9 / 10) / 40;


            Coords nameCoords = building.getGlobalCoord(building.getDisplaySquare());
            float nameX = gridStartX + nameCoords.x * tileSize + 10;
            float nameY = gridEndY - nameCoords.y * tileSize - 20;

            // draws buffers

            if ((building instanceof Miner || building instanceof Refiner) && building.getRecipe() != null) {
                for (int i = 0; i < building.getInputBuffer().size; i++) {
                    ResourceBuffer buffer = building.getInputBuffer().get(i);


                    game.batch.draw(SpriteManager.getTexture("ResourceContainer"),
                        gridStartX + nameCoords.x * tileSize + (float) tileSize / 20 + (i * 10 * scale),
                        gridEndY - (nameCoords.y + 1) * tileSize + (float) tileSize / 20,
                        10 * scale,
                        40 * scale
                    );
                    String id = buffer.getResourceId();
                    String visType = ResourceDatabase.getDB().get(id).getData().getString("VisType");
                    switch (visType) {
                        case "Piece" -> {
                            Texture resourceTexture = SpriteManager.getTexture(id);
                            for (int j = 0; j < buffer.getBuffer().size; j++) {
                                game.batch.draw(resourceTexture,
                                    gridStartX + nameCoords.x * tileSize + (float) tileSize / 20 + (i * 10 * scale) + scale,
                                    (float) (gridEndY - (nameCoords.y + 1) * tileSize + (float) tileSize / 20 + 2f * scale + ((double) j / buffer.getCapacity()) * (36 * scale)),
                                    (float) (scale / 1.5 * 12),
                                    (float) (scale / 1.5 * 8)
                                );
                            }
                        }
                        case "Liquid", "Bulk" -> {
                            TextureRegion[][] resourceTexture = SpriteManager.getTextureRegion(id + visType, 12, 8);
                            for (int j = 0; j < buffer.getBuffer().size; j++) {
                                TextureRegion tx = null;
                                if (j < buffer.getBuffer().size - 1) {
                                    tx = resourceTexture[1][0];
                                } else {
                                    tx = resourceTexture[0][0];
                                }
                                game.batch.draw(tx,
                                    gridStartX + nameCoords.x * tileSize + (float) tileSize / 20 + (i * 10 * scale) + scale,
                                    (float) (gridEndY - (nameCoords.y + 1) * tileSize + (float) tileSize / 20 + 2f * scale + ((double) j / buffer.getCapacity()) * (36 * scale)),
                                    (float) (scale / 1.5 * 12),
                                    (float) ((double) 1 / buffer.getCapacity()) * (36 * scale)
                                );
                            }
                        }
                    }
                }

                for (int i = 0; i < building.getOutputResourceBuffer().size; i++) {
                    ResourceBuffer buffer = building.getOutputResourceBuffer().get(i);


                    game.batch.draw(SpriteManager.getTexture("ResourceContainer"),
                        gridStartX + (nameCoords.x + 1) * tileSize - (float) tileSize / 20 - ((i + 1) * 10 * scale),
                        gridEndY - (nameCoords.y + 1) * tileSize + (float) tileSize / 20,
                        10 * scale,
                        40 * scale
                    );
                    String id = buffer.getResourceId();
                    String visType = ResourceDatabase.getDB().get(id).getData().getString("VisType");
                    switch (visType) {
                        case "Piece" -> {
                            Texture resourceTexture = SpriteManager.getTexture(id);
                            for (int j = 0; j < buffer.getBuffer().size; j++) {
                                game.batch.draw(resourceTexture,
                                    gridStartX + (nameCoords.x + 1) * tileSize - (float) tileSize / 20 - ((i + 1) * 10 * scale) + scale,
                                    (float) (gridEndY - (nameCoords.y + 1) * tileSize + (float) tileSize / 20 + 2f * scale + ((double) j / buffer.getCapacity()) * (36 * scale)),
                                    (float) (scale / 1.5 * 12),
                                    (float) (scale / 1.5 * 8)
                                );
                            }
                        }
                        case "Liquid", "Bulk" -> {
                            TextureRegion[][] resourceTexture = SpriteManager.getTextureRegion(id + visType, 12, 8);
                            for (int j = 0; j < buffer.getBuffer().size; j++) {
                                TextureRegion tx = null;
                                if (j < buffer.getBuffer().size - 1) {
                                    tx = resourceTexture[1][0];
                                } else {
                                    tx = resourceTexture[0][0];
                                }
                                game.batch.draw(tx,
                                    gridStartX + (nameCoords.x + 1) * tileSize - (float) tileSize / 20 - ((i + 1) * 10 * scale) + scale,
                                    (float) (gridEndY - (nameCoords.y + 1) * tileSize + (float) tileSize / 20 + 2f * scale + ((double) j / buffer.getCapacity()) * (36 * scale)),
                                    (float) (scale / 1.5 * 12),
                                    (float) ((double) 1 / buffer.getCapacity()) * (36 * scale)
                                );
                            }
                        }
                    }
                }
            }
            if (building instanceof Shooter shooter) {
                game.batch.draw(SpriteManager.getTexture("ResourceContainer"),
                    gridStartX + nameCoords.x * tileSize + (float) tileSize / 20,
                    gridEndY - (nameCoords.y + 1) * tileSize + (float) tileSize / 20,
                    10 * scale,
                    40 * scale
                );
                for (int j = 0; j < shooter.getMagazine().size; j++) {
                    Resource resource = shooter.getMagazine().get(j);
                    Texture resourceTexture = SpriteManager.getTexture(resource.getId());
                    game.batch.draw(resourceTexture,
                        gridStartX + nameCoords.x * tileSize + (float) tileSize / 20 + scale,
                        (float) (gridEndY - (nameCoords.y + 1) * tileSize + (float) tileSize / 20 + 2f * scale + ((double) j / shooter.getMagSize()) * (36 * scale)),
                        (float) (scale / 1.5 * 12),
                        (float) (scale / 1.5 * 8)
                    );
                }
            }
            if (building instanceof Defender defender) {
                game.batch.draw(SpriteManager.getTexture("ResourceContainer"),
                    gridStartX + nameCoords.x * tileSize + (float) tileSize / 20,
                    gridEndY - (nameCoords.y + 1) * tileSize + (float) tileSize / 20,
                    10 * scale,
                    40 * scale
                );
                for (int j = 0; j < defender.getMagazine().size; j++) {
                    Resource resource = defender.getMagazine().get(j);
                    Texture resourceTexture = SpriteManager.getTexture(resource.getId());
                    game.batch.draw(resourceTexture,
                        gridStartX + nameCoords.x * tileSize + (float) tileSize / 20 + scale,
                        (float) (gridEndY - (nameCoords.y + 1) * tileSize + (float) tileSize / 20 + 2f * scale + ((double) j / defender.getMagSize()) * (36 * scale)),
                        (float) (scale / 1.5 * 12),
                        (float) (scale / 1.5 * 8)
                    );
                }
            }


            /*
            game.font.getData().setScale(0.75f);
            game.font.draw(game.batch, building.gridName(), nameX, nameY);
            if (building instanceof Miner ||
                building instanceof Refiner) {
                if (building.getRecipe() != null) {
                    game.font.draw(game.batch, building.getRecipe().getName(), nameX, nameY - 30);
                } else {
                    BitmapFont tempfont = skin75pct.getFont("default-font");
                    tempfont.setColor(Color.RED);
                    tempfont.draw(game.batch, "Recipe not set!", nameX, nameY - 30);
                }
            }

             */
        }

        game.batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // draw bottleneck UI

        for (Building building : controller.getFactorySim().getGrid().getBuildings()) {
            Coords nameCoords = building.getGlobalCoord(building.getDisplaySquare());
            switch (building.getStatus()) {
                case Building.Status.WORKING -> {
                    shapeRenderer.setColor(Color.GREEN);
                }
                case Building.Status.DISABLED -> {
                    shapeRenderer.setColor(Color.RED);
                }
                case Building.Status.FULL_OUTPUT -> {
                    shapeRenderer.setColor(Color.YELLOW);
                }
                case Building.Status.NO_INPUT -> {
                    shapeRenderer.setColor(Color.ORANGE);
                }
            }

            shapeRenderer.circle(gridStartX + nameCoords.x * tileSize + (float) tileSize / 20 + 5, gridEndY - nameCoords.y * tileSize - ((float) tileSize / 20 + 5), 5);
        }
        shapeRenderer.end();

    }

    public void checkWinLoss() {
        // Below draws the screen transitions
        if (controller.isWin()) {
            controller.getFactorySim().clear();

            // Add the money to the player only on a win
            float multiplier = runState.getCurrNode().getMultiplier();
            int money = (int) (20 * multiplier);
            runState.getPlayer().gainMoney(money);

            game.resetCamera();
            game.setScreen(new WinScreen(game, runState));
            return;
        } else if (controller.isLost()) {
            game.getSoundManager().playSound("defeat", 1);
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

        codexOnScreen = Codex.isOnScreen;
        codexCheck();

        // comma & period for speed incrementing
        if (Gdx.input.isKeyJustPressed(Input.Keys.COMMA)) {
            slowDownTime();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.PERIOD)) {
            speedUpTime();
        }

        // Press E to rotate building CW, Q for CCW
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (selectedBuilding == null || selectedBuilding.isOnGrid()) {
                return;
            }
            int nextRotation = (selectedBuilding.getRotation() + 1) % 4;
            selectedBuilding.setRotation(nextRotation);
            needRefresh = true;

            game.getPopUpManager().requestPopup(
                "rotation_explanation",
                "Rotating Buildings",
                "You can rotate buildings using E (for clockwise rotation) or Q (for anti-clockwise rotation)." +
                    "\nThis can be useful to orient output ports in certain directions.",
                true
            );
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new PauseMenuScreen(game, runState, this));
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            if (selectedBuilding == null || selectedBuilding.isOnGrid()) {
                return;
            }
            int nextRotation = (selectedBuilding.getRotation() - 1) % 4;
            while (nextRotation < 0) {
                nextRotation += 4;
            }
            System.out.println(nextRotation);
            selectedBuilding.setRotation(nextRotation);
            needRefresh = true;

            game.getPopUpManager().requestPopup(
                "rotation_explanation",
                "Rotating Buildings",
                "You can rotate buildings using E (for clockwise rotation) or Q (for anti-clockwise rotation)." +
                    "\nThis can be useful to orient output ports in certain directions.",
                true
            );
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
                    Label tubeHelper = (Label) UIElements.get("tubemode");
                    tubeHelper.setText("T\nTo Exit\n" + controller.getCombatSim().getPlayer().getTubeBudget() + " Remaining");
                    // deselect building
                    selectedBuilding = null;
                    // tube mode handling here for now
                    multiplexer.addProcessor(tubelogger);
                } else {
                    Label tubeHelper = (Label) UIElements.get("tubemode");
                    tubeHelper.setText("T\nAdd Tubes\n" + controller.getCombatSim().getPlayer().getTubeBudget() + " Remaining");
                    multiplexer.removeProcessor(tubelogger);
                    downPoint = null;
                    upPoint = null;
                }
                needRefresh = true;
            }

            game.getPopUpManager().requestPopup(
                "tube_explanation",
                "Tubes",
                "Press T to enter / exit tube drawing mode." +
                    "\nTubes can be used to connect output ports to another building, as if they were directly connected." +
                    "\nTo draw a tube, draw the desired shape of the segment on an empty square when in tube mode." +
                    "\nRight click when in tube mode to remove a tube." +
                    "\nTubes can only be drawn one segment at a time.",
                true
            );
        }

        // Recipe selection is R (for now)
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            if (selectedBuilding != null && selectedBuilding.isOnGrid() && selectedBuilding.getValidRecipes() != null) {
                recipeUIOn = !recipeUIOn;
                if (recipeUIOn) {
                    centerStack.add(UIElements.get("recipediv"));
                    rebuildRecipeUI();
                } else {
                    clearRecipeUI();
                }
            }

            game.getPopUpManager().requestPopup(
                "Recipe_explanation",
                "Recipe selection",
                "Press R after selecting an appropriate building to open recipe selection" +
                    "\nRefer to the codex to see which buildings offer recipe selection capabilities." +
                    "\nYou can choose which recipe you would like the building to operate with, which determines its inputs / outputs.",
                true
            );
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

        // if codex is open lock out the rest of the UI
        if (stage.hit(mouseTranslatedX, mouseTranslatedY, true) != null &&
            codexOnScreen &&
            hit != Codex.getTable()) {
            return;
        }

        // Handles Placement preview via mouse hovering
        hoveredGridCoords = getGridAt(mouseTranslatedX, mouseTranslatedY);

        if (!tubeMode) {
            // Handles Placement preview via mouse hovering
            if (selectedBuilding != null && hoveredGridCoords != null) {
                hoveredGridCoords = getGridAt(
                    mouseTranslatedX - (float) (selectedBuilding.getProjectedShape()[0].length * tileSize) / 2 + (float) tileSize / 2,
                    mouseTranslatedY + (float) (selectedBuilding.getProjectedShape().length * tileSize) / 2 - (float) tileSize / 2);
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
                    if (Math.abs(upPoint.x - downPoint.x) + Math.abs(upPoint.y - downPoint.y) <= 2) {
                        // check movement is within 2 squares
                        boolean tubePlaced = false;
                        int tubeX = 0;
                        int tubeY = 0;
                        int tubeDir1 = 0;
                        int tubeDir2 = 0;
                        switch (Math.abs(upPoint.x - downPoint.x) + Math.abs(upPoint.y - downPoint.y)) {
                            case 0 -> {
                                if (upPoint.dir != downPoint.dir) {
                                    tubeX = upPoint.x;
                                    tubeY = upPoint.y;
                                    tubeDir1 = downPoint.dir;
                                    tubeDir2 = upPoint.dir;
                                    tubePlaced = true;
                                }
                            }
                            case 1 -> {
                                if (downPoint.pointingTo().x == upPoint.x &&
                                    downPoint.pointingTo().y == upPoint.y) {
                                    if (downPoint.pointingToSide().dir != upPoint.dir) {
                                        tubeX = upPoint.x;
                                        tubeY = upPoint.y;
                                        ;
                                        tubeDir1 = downPoint.pointingToSide().dir;
                                        tubeDir2 = upPoint.dir;
                                        tubePlaced = true;
                                    }
                                } else if (downPoint.x == upPoint.pointingTo().x &&
                                    downPoint.y == upPoint.pointingTo().y) {
                                    if (upPoint.pointingToSide().dir != downPoint.dir) {
                                        tubeX = upPoint.x;
                                        tubeY = upPoint.y;
                                        ;
                                        tubeDir1 = downPoint.dir;
                                        tubeDir2 = upPoint.pointingToSide().dir;
                                        tubePlaced = true;
                                    }
                                }

                            }
                            case 2 -> {
                                boolean downCheck = false;
                                boolean upCheck = false;
                                switch (downPoint.dir & 3) {
                                    case 0 -> {
                                        if (downPoint.y > upPoint.y) {
                                            downCheck = true;
                                        }
                                    }
                                    case 1 -> {
                                        if (downPoint.x < upPoint.x) {
                                            downCheck = true;
                                        }
                                    }
                                    case 2 -> {
                                        if (downPoint.y < upPoint.y) {
                                            downCheck = true;
                                        }
                                    }
                                    case 3 -> {
                                        if (downPoint.x > upPoint.x) {
                                            downCheck = true;
                                        }
                                    }
                                }

                                switch (upPoint.dir & 3) {
                                    case 0 -> {
                                        if (downPoint.y < upPoint.y) {
                                            upCheck = true;
                                        }
                                    }
                                    case 1 -> {
                                        if (downPoint.x > upPoint.x) {
                                            upCheck = true;
                                        }
                                    }
                                    case 2 -> {
                                        if (downPoint.y > upPoint.y) {
                                            upCheck = true;
                                        }
                                    }
                                    case 3 -> {
                                        if (downPoint.x < upPoint.x) {
                                            upCheck = true;
                                        }
                                    }
                                }
                                if (downCheck && upCheck) {
                                    DirectedCoords downPointer = downPoint.pointingToSide();
                                    DirectedCoords upPointer = upPoint.pointingToSide();
                                    if (downPointer.x == upPointer.x &&
                                        downPointer.y == upPointer.y) {
                                        tubeX = upPointer.x;
                                        tubeY = upPointer.y;
                                        tubeDir1 = downPointer.dir;
                                        tubeDir2 = upPointer.dir;
                                        tubePlaced = true;
                                    }
                                }
                            }
                        }
                        if (tubePlaced) {
                            if (controller.getCombatSim().getPlayer().getTubeBudget() > 0) {
                                boolean newSegment = controller.getFactorySim().getGrid().addTube(tubeX, tubeY, tubeDir1, tubeDir2);
                                if (newSegment) {
                                    controller.getCombatSim().getPlayer().spendTube(1);
                                    tubeUIRefresh();
                                }
                            }
                        }

                    }
                }
                upPoint = null;
                downPoint = null;
            }
            if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                Tube removed = controller.getFactorySim().getGrid().removeTube(hoveredGridCoords.x, hoveredGridCoords.y);
                if (removed != null) {
                    controller.getCombatSim().getPlayer().refundTube(1);
                    tubeUIRefresh();
                }
            }
        }
        if (codexOnScreen || recipeUIOn) {
            ((BuildingToolTip) UIElements.get("buildingtooltip")).hide();
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
                game.getSoundManager().playSound("buildingPlace", 1);
                controller.getCombatSim().getPlayer().removeBuilding(selectedBuilding);
                needRefresh = true;
                selectedBuilding = null;
                return;
            }
        }
        hoveredGridCoords = getGridAt(mouseTranslatedX, mouseTranslatedY);
        if (hoveredGridCoords != null && !recipeUIOn && !codexOnScreen) {
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
                    game.getSoundManager().playSound("buildingRemove", 1);
                    controller.getCombatSim().getPlayer().addBuilding(building);
                }
            }
        }
        needRefresh = true;

    }

    // draws pipes
    private void pipeDrawSwitcher(Tube tube, float tileX, float tileY) {
        TextureRegion[][] tubeSheet = SpriteManager.getTextureRegion("Pipes", 44, 44);


        if (tube.getDouble()) {
            if (tube.getConnections1()[0] == tube.getConnections1()[2]) {
                if (tube.getConnections1()[0]) {
                    game.batch.draw(tubeSheet[2][2],
                        tileX, tileY,
                        tileSize, tileSize
                    );
                } else {
                    game.batch.draw(tubeSheet[2][3],
                        tileX, tileY,
                        tileSize, tileSize
                    );
                }
            } else {
                if (tube.getConnections1()[0] == tube.getConnections1()[1]) {
                    game.batch.draw(tubeSheet[0][1],
                        tileX, tileY,
                        tileSize, tileSize
                    );
                    game.batch.draw(tubeSheet[0][3],
                        tileX, tileY,
                        tileSize, tileSize
                    );
                } else {
                    game.batch.draw(tubeSheet[0][0],
                        tileX, tileY,
                        tileSize, tileSize
                    );
                    game.batch.draw(tubeSheet[0][2],
                        tileX, tileY,
                        tileSize, tileSize
                    );
                }
            }
        } else {
            int count = 0;
            for (boolean value : tube.getConnections1()) {
                if (value) count++;
            }
            switch (count) {
                case 2 -> {
                    if (tube.getConnections1()[0]) {
                        if (tube.getConnections1()[1]) {
                            game.batch.draw(tubeSheet[0][1],
                                tileX, tileY,
                                tileSize, tileSize
                            );
                        } else if (tube.getConnections1()[3]) {
                            game.batch.draw(tubeSheet[0][0],
                                tileX, tileY,
                                tileSize, tileSize
                            );
                        } else {
                            game.batch.draw(tubeSheet[2][0],
                                tileX, tileY,
                                tileSize, tileSize
                            );
                        }
                    } else if (tube.getConnections1()[2]) {
                        if (tube.getConnections1()[1]) {
                            game.batch.draw(tubeSheet[0][2],
                                tileX, tileY,
                                tileSize, tileSize
                            );
                        } else {
                            game.batch.draw(tubeSheet[0][3],
                                tileX, tileY,
                                tileSize, tileSize
                            );
                        }
                    } else {
                        game.batch.draw(tubeSheet[2][1],
                            tileX, tileY,
                            tileSize, tileSize
                        );
                    }
                }
                case 3 -> {
                    for (int i = 0; i < 4; i++) {
                        if (!tube.getConnections1()[i]) {
                            game.batch.draw(tubeSheet[1][i],
                                tileX, tileY,
                                tileSize, tileSize
                            );
                        }
                    }
                }
                case 4 -> {
                    game.batch.draw(tubeSheet[3][0],
                        tileX, tileY,
                        tileSize, tileSize
                    );
                }
            }
        }
/*
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
 */
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

    private static class BuildingToolTip extends Container<Actor> {

        public BuildingToolTip() {
            super();
            pack();
            setVisible(false);
            setTouchable(Touchable.disabled);
        }

        public void show(Actor content, float stageX, float stageY) {
            setActor(content);
            pack();
            setPosition(stageX + 16, stageY - getHeight() - 16);
            setVisible(true);
            toFront();
        }

        public void hide() {
            setVisible(false);
        }
    }
}

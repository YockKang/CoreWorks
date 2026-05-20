package com.main.CoreWorks.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.main.CoreWorks.Coreworks;
import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Factory.FactoryGrid;
import com.main.CoreWorks.database.EnemyDatabase;
import com.main.CoreWorks.database.PlayerDatabase;
import com.main.CoreWorks.entities.Enemy;
import com.main.CoreWorks.simulators.CombatController;
import com.main.CoreWorks.simulators.CombatSim;
import com.main.CoreWorks.simulators.FactorySim;
import org.checkerframework.checker.units.qual.C;

public class CombatScreen implements Screen {

    Coreworks game;
    CombatController controller;
    private float accumulator = 0f;
    private static final float TIME_STEP = 1/4f; // 4 Ticks per second
    private Vector2 mouse2DCoords = new Vector2();
    private ShapeRenderer shapeRenderer;

    // Temp Layout since we have not decided how we want the final UI to look like yet
    // Rmb that everything is drawn in a coordinate system (check Coreworks class for the public static final screen size)
    private final int gridStartX = 80;
    private final int gridEndY = 320;
    private final int tileSize = 56;

    // Hardcoded grid size for milestone 1 testing purposes
    // Should be deleted eventually since it should be handled by the global runState which carries over the factory
    private final int gridWidth = 4;
    private final int gridHeight = 4;

    private final int inventoryStartX = 80;
    private final int inventoryStartY = 60;
    // Each inventory slot is a square for now
    private final int inventorySlotSize = 64;
    private final int inventorySlotGap = 12;

    private Building selectedBuilding;

    public CombatScreen(Coreworks game) {

        this.game = game;
        // Since this is milestone 1, we will be hardcoding the encounter and grid for now
        // Eventually combatSim should be handled by the Map Screen and node generation code to create the combat encounter
        // and factorySim would be carried over via the global runState class or something
        FactorySim factorySim = new FactorySim(new FactoryGrid(gridHeight,gridWidth));
        Array<Enemy> enemies = new Array<>();
        enemies.add(EnemyDatabase.createMissileDrone());
        enemies.add(EnemyDatabase.createShieldDrone());
        CombatSim combatSim = new CombatSim(PlayerDatabase.createEngineer(), enemies);
        this.controller = new CombatController(factorySim, combatSim);
    }

    @Override
    public void show() {
        game.viewport.apply();
        game.camera.update();
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render(float delta) {
        // Anti-"Lag spike spiral of death" code
        delta = Math.min(delta, 1/8f);

        mouseInput();

        // Tick Advancement code below
        if (!controller.isWin() && !controller.isLost()) {
            accumulator += delta;
            while (accumulator >= TIME_STEP) {
                controller.advanceTick();
                accumulator -= TIME_STEP;
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
        drawInventory();
        drawCombatHUD();
    }

    /*
    All drawing related functions should be handled from here on
     */

    public void drawGrid() {
        // TBD
    }

    public void drawCombatHUD() {
        // TBD
    }

    public void drawInventory() {
        // TBD
    }


    /*
    All mouse inputs should be handled here
     */

    private void mouseInput() {
        // Keyboard inputs handled below

        // Press R to rotate building
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            if (selectedBuilding == null) {
                return;
            }
            int nextRotation = (selectedBuilding.getRotation() + 1) % 4;
            selectedBuilding.setRotation(nextRotation);
        }

        // Mouse inputs handled below
        if (Gdx.input.justTouched()) {
            Vector2 mouseTranslatedCoords = translateMouseToWorld();

            float mouseTranslatedX = mouseTranslatedCoords.x;
            float mouseTranslatedY = mouseTranslatedCoords.y;

            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                leftClick(mouseTranslatedX, mouseTranslatedY);
            }

            if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                rightClick(mouseTranslatedX, mouseTranslatedY);
            }
        }
    }

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
        Building clickedBuilding = getInventoryBuildingAt(mouseTranslatedX, mouseTranslatedY);
        if (clickedBuilding != null) {
            selectedBuilding = clickedBuilding;
            return;
        }
        Coords coords = getGridAt(mouseTranslatedX, mouseTranslatedY);
        if (coords != null && selectedBuilding != null) {
            boolean successfulPlacement = controller.getFactorySim().getGrid().placeBuilding(selectedBuilding, coords.x, coords.y, selectedBuilding.getRotation());
            if (successfulPlacement) {
                controller.getCombatSim().getPlayer().removeBuilding(selectedBuilding);
                selectedBuilding = null;
            }
        }
    }

    /*
    Right clicks will handle (c.a.a Milestone 1)
        1. Deselecting a building
        2. Removing building from the grid if grid is clicked
     */

    private void rightClick(float mouseTranslatedX, float mouseTranslatedY) {
        Coords coords = getGridAt(mouseTranslatedX, mouseTranslatedY);
        if (selectedBuilding != null || coords == null) {
            selectedBuilding = null;
        } else {
            Building building = controller.getFactorySim().getGrid().getBuildingAt(coords.x, coords.y);
            controller.getFactorySim().getGrid().removeBuilding(building);
            controller.getCombatSim().getPlayer().addBuilding(building);
        }
    }

    private Building getInventoryBuildingAt(float mouseTranslatedX, float mouseTranslatedY) {
        for (int i = 0; i < controller.getCombatSim().getPlayer().getInventory().size; i++) {
            int leftBoundInventorySlot = inventoryStartX + i * (inventorySlotSize + inventorySlotGap);
            int rightBoundInventorySlot = leftBoundInventorySlot + inventorySlotSize;
            int topBoundInventorySlot = inventoryStartY + inventorySlotSize;

            boolean clickedSlotX = mouseTranslatedX >= leftBoundInventorySlot && mouseTranslatedX < rightBoundInventorySlot;
            boolean clickedSlotY = mouseTranslatedY >= inventoryStartY && mouseTranslatedY < topBoundInventorySlot;

            if (clickedSlotX && clickedSlotY) {
                return controller.getCombatSim().getPlayer().getBuildingAt(i);
            }
        }
        return null;
    }

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

    /*
    Helper Coordinate class to easily store x and y coordinates (something like Pair class from lectures)
     */

    static final class Coords {
        final int x;
        final int y;

        Coords(int x, int y) {
            this.x = x;
            this.y = y;
        }
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
        game.batch.dispose();
        game.font.dispose();
        shapeRenderer.dispose();
    }
}

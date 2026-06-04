package com.main.CoreWorks.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.main.CoreWorks.Coreworks;
import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.RunPersistence.CombatNode;
import com.main.CoreWorks.RunPersistence.MapNode;
import com.main.CoreWorks.RunPersistence.RunState;

public class MapScreen implements Screen {

    private final Coreworks game;
    private RunState runState;
    private Vector2 mouse2DCoords = new Vector2();
    ShapeRenderer shapeRenderer;

    // The nodes are circular, so we just store the radius
    private int nodeRadius = 80;

    public MapScreen(Coreworks game, RunState runstate) {
        this.game = game;
        this.runState = runstate;
    }

    @Override
    public void show() {
        game.viewport.apply();
        game.camera.update();
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render(float delta) {

        // Handles any inputs
        externalInputs();

        // Clears the screen + update camera if needed
        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.camera.update();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(game.viewport.getCamera().combined);

        // All drawing functions below
        drawConnection();
        drawNodeFilled();
        drawNodeOutline();
        drawNodeName();
    }

    private void drawConnection() {
        // Draws the line that connects between nodes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(Color.WHITE);

        for (MapNode node : runState.getRunMap().getNodes()) {
            for (MapNode next : node.getNextNodes()) {
                shapeRenderer.line(node.getX(), node.getY(), next.getX(), next.getY());
            }
        }

        shapeRenderer.end();
    }

    private void drawNodeFilled() {
        // Draws the internal filled circle of the node
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (MapNode node : runState.getRunMap().getNodes()) {
            // These code will handle the coloring of the different types of nodes
            if (node instanceof CombatNode) {
                shapeRenderer.setColor(Color.ORANGE);
            }

            // This draws the color of non-adjacent nodes to current node
            if (!node.isUnlocked()) {
                shapeRenderer.setColor(Color.GRAY);
            }

            // This draws color of completed nodes
            if (node.isCompleted()) {
                shapeRenderer.setColor(Color.CYAN);
            }

            // The code below handles the actual drawing of the circle shape
            shapeRenderer.circle(node.getX(), node.getY(), nodeRadius);
        }

        shapeRenderer.end();
    }

    private void drawNodeOutline() {
        // Draws the highlighting of current node + any additional special nodes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (MapNode node : runState.getRunMap().getNodes()) {
            if (node == runState.getCurrNode()) {
                shapeRenderer.setColor(Color.YELLOW);

                shapeRenderer.circle(node.getX(), node.getY(), nodeRadius + 4);
            }
        }

        shapeRenderer.end();
    }

    // Eventually this should be deleted, since we should be able to differentiate by sprites only (like STS2)
    private void drawNodeName() {
        game.batch.begin();

        for (MapNode node : runState.getRunMap().getNodes()) {
            game.font.draw(game.batch, node.getName(), node.getX() - 10, node.getY());
        }

        game.batch.end();
    }

    // Translates mouse coordinates to world coordinates
    private Vector2 translateMouseToWorld() {
        mouse2DCoords.set(Gdx.input.getX(), Gdx.input.getY());
        game.viewport.unproject(mouse2DCoords);
        return mouse2DCoords;
    }

    private void externalInputs() {
        // Mouse inputs handled below
        Vector2 mouseTranslatedCoords = translateMouseToWorld();

        float mouseTranslatedX = mouseTranslatedCoords.x;
        float mouseTranslatedY = mouseTranslatedCoords.y;

        if (Gdx.input.justTouched()) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                MapNode clicked = leftClickNode(mouseTranslatedX, mouseTranslatedY);

                // Handles clicking a non-node
                if (clicked == null) {
                    return;
                }

                // Handles clicking a non-adjacent node / completed node
                if (!clicked.isUnlocked() || clicked.isCompleted()) {
                    return;
                }

                // The codes below will handle all the different types of nodes present
                if (clicked instanceof CombatNode combatNode) {
                    runState.setCurrNode(clicked);
                    game.setScreen(new CombatScreen(game, runState, combatNode.getEnemies()));
                }
            }
        }
    }

        /*
    Left clicks will handle (c.a.a Milestone 2)
        1. Selecting the node (Eventually we should add an "are you sure" screen or smth)
     */

    private MapNode leftClickNode(float mouseTranslatedX, float mouseTranslatedY) {

        // Nodes are circular, so we use Pythagoras Theorem to find the distance from the mouse click to the center of the node
        // And see if it lies within the radius of the node's circular shape
        for (MapNode node : runState.getRunMap().getNodes()) {
            float deltaX = mouseTranslatedX - node.getX();
            float deltaY = mouseTranslatedY - node.getY();
            float deltaDist = (float) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
            if (deltaDist <= nodeRadius) {
                return node;
            }
        }
        return null;
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
    }
}


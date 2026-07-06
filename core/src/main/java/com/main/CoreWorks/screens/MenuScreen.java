package com.main.CoreWorks.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Coreworks;
import com.main.CoreWorks.Factory.FactoryGrid;
import com.main.CoreWorks.Generators.RunMapGenerator;
import com.main.CoreWorks.RunPersistence.RunMap;
import com.main.CoreWorks.RunPersistence.RunState;
import com.main.CoreWorks.database.PlayerDatabase;
import com.main.CoreWorks.entities.Player;

public class MenuScreen implements Screen {

    final Coreworks game;
    private Stage stage;
    private Skin skin;

    public MenuScreen(Coreworks game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(game.viewport, game.batch);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        Gdx.input.setInputProcessor(stage);

        // Build the Scene2D UI
        buildMenuUI();
    }

    private void buildMenuUI() {
        stage.clear();

        Table table = new Table();
        table.setFillParent(true);
        // Center the table with default button widths and height
        table.center().defaults().width(220).height(80).padBottom(30).row();
        stage.addActor(table);

        // Create a temporary font to make the main text bigger
        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(2.5f);
        table.add(new Label("COREWORKS", new Label.LabelStyle(titleFont, Color.ORANGE))).padBottom(30).row();

        // Adds the start button
        TextButton startButton = new TextButton("Start a new Game", skin);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Creates the initial runState
                // For now, hardcoded one player type only, eventually might allow selection of different player types with unique abilities for more replayability
                Player player = PlayerDatabase.createEngineer();
                RunState runState = new RunState(player, player.getFactoryGrid());
                // Generates hardcoded RunMap for testing (Uncomment)
                // RunMap runMap = RunMapGenerator.generateHardcodedRunMap(runState);
                // Generates procedurally generated runMap
                RunMap runMap = RunMapGenerator.generateRandomRunMapF1(runState);
                runState.setRunMap(runMap);
                runState.setCurrNode(runMap.getStartNode());

                // Moves to map screen
                game.resetCamera();
                game.setScreen(new MapScreen(game, runState));
                dispose();
            }
        });
        table.add(startButton).row();

        // Adds the Quit button
        TextButton quitButton = new TextButton("Quit Game", skin);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        table.add(quitButton).row();

        // Add more buttons below or above
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        stage.act(delta);
        stage.draw();
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
        stage.dispose();
        skin.dispose();
    }
}

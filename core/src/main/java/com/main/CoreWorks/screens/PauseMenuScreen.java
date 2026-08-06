package com.main.CoreWorks.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.main.CoreWorks.Coreworks;
import com.main.CoreWorks.Generators.RewardGenerator;
import com.main.CoreWorks.Rewards.Reward;
import com.main.CoreWorks.RunPersistence.MapNode;
import com.main.CoreWorks.RunPersistence.RunState;

public class PauseMenuScreen implements Screen {

    private final Coreworks game;
    private RunState runState;
    private Screen lastScreen;
    private Stage stage;
    private Skin skin;

    public PauseMenuScreen(Coreworks game, RunState runState, Screen lastScreen) {
        this.game = game;
        this.runState = runState;
        this.lastScreen = lastScreen;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Coreworks.VIEWPORT_WIDTH, Coreworks.VIEWPORT_HEIGHT), game.batch);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        Gdx.input.setInputProcessor(stage);

        // Build the Scene2D UI
        buildPauseUI();
    }

    public void buildPauseUI() {
        stage.clear();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        table.add(new Label("Game Paused", skin)).pad(20).row();

        TextButton continueButton = new TextButton("Continue", skin);
        continueButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                // Play button click sound
                game.getSoundManager().playButtonClick();
                game.setScreen(lastScreen);
            }
        });

        TextButton quitRunButton = new TextButton("Quit current run (No save)", skin);
        quitRunButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                // Play button click sound
                game.getSoundManager().playButtonClick();
                game.resetCamera();
                game.setScreen(new MenuScreen(game));
            }
        });

        TextButton quitGameButton = new TextButton("Quit game (No save)", skin);
        quitGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                // Play button click sound
                game.getSoundManager().playButtonClick();
                Gdx.app.exit();
            }
        });

        table.add(continueButton).padTop(20).row();
        table.add(quitRunButton).padTop(20).row();
        table.add(quitGameButton).padTop(20).row();
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

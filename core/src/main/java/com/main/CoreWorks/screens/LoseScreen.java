package com.main.CoreWorks.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Coreworks;
import com.main.CoreWorks.Generators.RewardGenerator;
import com.main.CoreWorks.Rewards.Reward;
import com.main.CoreWorks.RunPersistence.MapNode;

public class LoseScreen implements Screen {

    private final Coreworks game;
    private Stage stage;
    private Skin skin;

    public LoseScreen(Coreworks game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(game.viewport, game.batch);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        Gdx.input.setInputProcessor(stage);

        // Build the Scene2D UI
        buildLoseUI();
    }

    private void buildLoseUI() {
        stage.clear();

        Table table = new Table();
        table.setFillParent(true);
        // Center the table with default button widths and height
        table.center().defaults().width(220).height(80).padBottom(30).row();
        stage.addActor(table);

        // Create a temporary font to make the main text bigger
        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(3f);
        table.add(new Label("You Lose!", new Label.LabelStyle(titleFont, Color.RED))).padBottom(60).row();

        // Add a return to menu button
        TextButton menuButton = new TextButton("Return to Main Menu", skin);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.resetCamera();
                game.setScreen(new MenuScreen(game));
                dispose();
            }
        });
        table.add(menuButton).row();
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

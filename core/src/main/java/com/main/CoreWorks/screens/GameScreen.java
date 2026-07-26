package com.main.CoreWorks.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ScreenUtils;
import com.main.CoreWorks.Codex.Codex;
import com.main.CoreWorks.Coreworks;
import org.apache.fory.codegen.Code;

public class GameScreen implements Screen {

    protected final Coreworks game;
    protected Stage stage;
    protected Skin skin;
    protected Stack centerStack = new Stack();
    private Container<Actor> codexDiv = Codex.getTableInDiv();
    protected boolean codexOnScreen = false;

    protected ObjectMap<String, Actor> UIElements = new ObjectMap<>();

    public GameScreen(Coreworks game) {
        this.game = game;
        stage = new Stage(game.viewport, game.batch);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        stage.addActor(centerStack);
        centerStack.setFillParent(true);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {

        ScreenUtils.clear(Color.BLACK);
        stage.act(delta);
        stage.draw();
    }

    public void codexCheck() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            System.out.println();
            codexOnScreen = Codex.isOnScreen;
            System.out.println("codex on: "+ codexOnScreen);
            if (!codexOnScreen) {
                centerStack.add(codexDiv);
                Codex.isOnScreen = true;
            } else {
                codexDiv.remove();
                Codex.isOnScreen = false;
            }
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
        stage.dispose();
        skin.dispose();
    }
}

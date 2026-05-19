package com.main.CoreWorks.screens;

import com.badlogic.gdx.Screen;
import com.main.CoreWorks.Coreworks;

public class RewardScreen implements Screen {

    private final Coreworks game;

    public RewardScreen(Coreworks game) {
        this.game = game;
    }

    @Override
    public void show() {
        // TBD
    }

    @Override
    public void render(float delta) {
        // TBD
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
        // TBD
    }
}

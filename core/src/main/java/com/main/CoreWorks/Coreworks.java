package com.main.CoreWorks;

import com.badlogic.gdx.Game;
import com.main.CoreWorks.screens.CombatScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Coreworks extends Game {

    @Override
    public void create() {
        // For now, starting the game leads straight to combat screen
        this.setScreen(new CombatScreen(this)); // eventually will replace with the Main Menu screen
    }

    @Override
    public void render() {
        // A common mistake is to forget to call super.render() with a Game implementation. Without this call, the Screen that you set in the create() method will not be rendered if you override the render method in your Game class!
        // ^^ According to the libGDX wiki
        super.render();
    }

    @Override
    public void dispose() {
        if (getScreen() != null) {
            getScreen().dispose();
        }
    }
}

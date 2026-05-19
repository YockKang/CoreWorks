package com.main.CoreWorks.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;
import com.main.CoreWorks.Coreworks;
import com.main.CoreWorks.Factory.FactoryGrid;
import com.main.CoreWorks.database.EnemyDatabase;
import com.main.CoreWorks.database.PlayerDatabase;
import com.main.CoreWorks.entities.Enemy;
import com.main.CoreWorks.simulators.CombatController;
import com.main.CoreWorks.simulators.CombatSim;
import com.main.CoreWorks.simulators.FactorySim;

public class CombatScreen implements Screen {

    Coreworks game;
    CombatController controller;

    public CombatScreen(Coreworks game) {

        this.game = game;
        // Since this is milestone 1, we will be hardcoding the encounter and grid for now
        FactorySim factorySim = new FactorySim(new FactoryGrid(4,4));
        Array<Enemy> enemies = new Array<>();
        enemies.add(EnemyDatabase.createMissileDrone());
        enemies.add(EnemyDatabase.createShieldDrone());
        CombatSim combatSim = new CombatSim(PlayerDatabase.createEngineer(), enemies);
        this.controller = new CombatController(factorySim, combatSim);
    }

    @Override
    public void show() {
        // TBD
    }

    @Override
    public void render(float delta) {

        // Tick Simulation + combat + tick advancement goes here

        // Using the default libGDX font as placeholder, all the screen drawing code below must be changed when art is done
        game.batch.begin();

        // The below code handles win/loss
        if (controller.isWin()) {
            // game.setScreen(new WinScreen(game));  Uncomment when WinScreen is done
            game.font.draw(game.batch, "YOU WIN!", 30, 350);
        }

        if (controller.isLost()) {
            // game.setScreen(new LoseScreen(game)); Uncomment when LoseScreen is done
            game.font.draw(game.batch, "YOU LOSE!", 30, 350);
        }

        game.batch.end();
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
    }
}

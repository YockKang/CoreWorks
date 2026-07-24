package com.main.CoreWorks.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Codex.Codex;
import com.main.CoreWorks.Coreworks;
import com.main.CoreWorks.Generators.RewardGenerator;
import com.main.CoreWorks.Rewards.Reward;
import com.main.CoreWorks.Rewards.ShopOffer;
import com.main.CoreWorks.RunPersistence.MapNode;
import com.main.CoreWorks.RunPersistence.RunState;
import com.main.CoreWorks.simulators.PopUpTutorial.PopUpManager;

public class ShopScreen implements Screen {

    private final Coreworks game;
    private RunState runState;
    private Array<ShopOffer> offers;
    private Stage stage;
    private Stack centerStack;
    private Container<Actor> codexDiv = Codex.getTableInDiv();
    private Skin skin;
    private boolean codexOnScreen = false;

    public ShopScreen(Coreworks game, RunState runState, Array<ShopOffer> offers) {
        this.game = game;
        this.runState = runState;
        this.offers = offers;
        centerStack = new Stack();
    }

    @Override
    public void show() {
        stage = new Stage(game.viewport, game.batch);
        stage.addActor(centerStack);

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        Gdx.input.setInputProcessor(stage);

        // Sets the popup manager
        game.getPopUpManager().setScene2D(stage, skin);

        game.getPopUpManager().requestPopup(
            "shop_node",
            "Shopping",
            "Welcome to the Curiosity shop!\nYou can use the money you accumulated to buy special upgrades and relics.\nThe shop does not entertain refunds, so be strict in your selection!",
            false
        );

        game.getPopUpManager().requestPopup(
            "relic_explanation",
            "Relics",
            "Relics are special items that have triggerable unique effects.\nHigher grade relics are stronger, but are also more expensive.\nRefer to the codex for more information about what each relic does!",
            false
        );

        game.getPopUpManager().requestPopup(
            "grid_expansion",
            "Grid Expansion",
            "You can also expand your grid size in the shop!\nHowever, they are quite expensive, so make sure you really need that space.",
            false
        );

        game.getPopUpManager().requestPopup(
            "shop_codex_explanation",
            "Codex in the shop",
            "If there are any relics / buildings that you are unsure of, press C to access the codex!",
            false
        );

        // Build the Scene2D UI
        buildShopUI();
    }

    private void buildShopUI() {
        stage.clear();
        centerStack.clear();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(centerStack);
        centerStack.addActor(table);

        table.add(new Label("Welcome to the Curiosity Shop!", skin)).padTop(10).top().center().row();
        table.add(new Label(runState.getPlayer().toString(), skin)).pad(10).center().row();

        // Creates the shop table + cards
        Table shopTable = new Table();
        int count = 0;
        for (ShopOffer offer : offers) {
            Table shopCard = new Table();
            shopCard.defaults().pad(4);

            shopCard.add(new Label(offer.getReward().getName(), skin)).pad(5).row();
            shopCard.add(new Label(offer.getReward().getDescription(), skin)).pad(5).row();
            shopCard.add(new Label("Cost: " + offer.getCost(), skin)).pad(5).row();

            TextButton buyButton = new TextButton("Buy", skin);
            // Disable the button if too broke
            if (offer.isPurchased()) {
                // Permanently disable the button
                buyButton.setDisabled(true);
                buyButton.setText("SOLD!");
                // Set the background to translucent gray
                shopCard.setBackground(skin.newDrawable("default-round", new Color(0.4f, 0.4f, 0.4f, 0.5f)));
            } else if (runState.getPlayer().getMoney() < offer.getCost()) {
                buyButton.setDisabled(true);
                shopCard.setBackground(skin.newDrawable("default-round", new Color(1f, 0f, 0f, 0.6f)));
            } else {
                shopCard.setBackground(skin.newDrawable("default-round", Color.ORANGE));
            }
            buyButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (runState.getPlayer().getMoney() < offer.getCost()) { return; }
                    // Apply the offer
                    offer.apply(runState);
                    // refresh the UI
                    buildShopUI();
                }
            });
            shopCard.add(buyButton).row();

            shopTable.add(shopCard).pad(10);
            count++;
            if (count % 4 == 0) {
                shopTable.row();
            }
        }
        table.add(shopTable).expand().center().row();

        // Adds the leave shop button
        TextButton leaveButton = new TextButton("Leave Shop", skin);
        leaveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                runState.getCurrNode().setCompleted(true);
                for (MapNode next : runState.getCurrNode().getNextNodes()) {
                    next.setUnlocked(true);
                }
                game.resetCamera();
                game.setScreen(new MapScreen(game, runState));
            }
        });

        table.add(leaveButton).pad(20).width(100).height(50);
    }

    @Override
    public void render(float delta) {
        // PopUp manager will spawn the next popup if needed (and exists), no pause necessary since it doesn't even exist
        PopUpManager popUpManager = game.getPopUpManager();
        if (popUpManager != null && popUpManager.showNext(() -> {}, () -> {})) {
            ScreenUtils.clear(Color.BLACK);
            stage.act(delta);
            stage.draw();
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            if (!codexOnScreen) {
                centerStack.add(codexDiv);
            } else {
                codexDiv.remove();
            }
            codexOnScreen = !codexOnScreen;
        }

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


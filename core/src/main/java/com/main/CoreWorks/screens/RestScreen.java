package com.main.CoreWorks.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.main.CoreWorks.Coreworks;
import com.main.CoreWorks.Generators.RewardGenerator;
import com.main.CoreWorks.Rewards.Reward;
import com.main.CoreWorks.RunPersistence.MapNode;
import com.main.CoreWorks.RunPersistence.RunState;
import com.main.CoreWorks.entities.Player;
import com.main.CoreWorks.simulators.PopUpTutorial.PopUpManager;

public class RestScreen extends GameScreen {

    private RunState runState;

    // Handles switching between select and confirm screen
    private enum RestScreenState {
        CHOOSING,
        CONFIRMING
    }

    // Any additional choices to be added to rest screen needs to be added here as well
    private enum ChoiceMade {
        HEAL,
        REWARD
    }

    private Group mainWindow = new Group();

    private RestScreenState state = RestScreenState.CHOOSING;
    private ChoiceMade pendingChoice;
    private int healAmt;
    private Array<Reward> mysteryReward;


    public RestScreen(Coreworks game, RunState runState) {
        super(game);
        this.runState = runState;
        centerStack.add(mainWindow);

        Player player = runState.getPlayer();
        MapNode currNode = runState.getCurrNode();
        int missingHP = player.displayMaxHp() - player.displayCurrentHp();

        // Healing will be based on their missing HP or a fixed number, whichever is higher
        this.healAmt = (int) Math.max(10 * currNode.getTier(), 0.4 * missingHP);

        mysteryReward = RewardGenerator.generateRestNodeReward(runState);

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        // Sets the popup manager
        game.getPopUpManager().setScene2D(stage, skin);

        game.getPopUpManager().requestPopup(
            "rest_node",
            "Rest Node",
            "Welcome to the Rest Node!\nYou can choose to either rest up and heal here, or upgrade a building.\nYou cannot select both, so be smart about your choice!",
            false
        );

        // Builds the UI
        buildRestUI();

        mainWindow.addActor(UIElements.get("choosingtable"));
    }

    private void buildRestUI() {


        Table choosingTable = new Table();
        UIElements.put("choosingtable", choosingTable);

        Table confirmingHealTable = new Table();
        UIElements.put("confirminghealtable", confirmingHealTable);

        Table confirmingUpgradeTable = new Table();
        UIElements.put("confirminghupgradetable", confirmingUpgradeTable);

        choosingTable.setFillParent(true);
        choosingTable.center().pad(20);
        choosingTable.defaults().width(250).height(100).pad(10);

        choosingTable.add(new Label("You can finally rest...", skin)).pad(15).row();
        choosingTable.add(new Label("What do you want to do?", skin)).pad(15).row();

        TextButton healButton = new TextButton("Heal up!", skin);
        healButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Play button click sound
                game.getSoundManager().playButtonClick();
                choosingTable.remove();
                mainWindow.addActor(confirmingHealTable);
            }
        });

        TextButton rewardButton = new TextButton("Upgrade a Building!", skin);
        rewardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Play button click sound
                game.getSoundManager().playButtonClick();
                choosingTable.remove();
                mainWindow.addActor(confirmingUpgradeTable);
            }
        });

        choosingTable.add(healButton);
        choosingTable.add(rewardButton).row();


        confirmingHealTable.setFillParent(true);
        confirmingHealTable.center().pad(20);
        confirmingHealTable.defaults().width(250).height(100).pad(10);
        confirmingHealTable.add(new Label("Confirm your choice!", skin)).pad(10).row();
        confirmingHealTable.add(new Label("Once confirmed, you cannot go back!", skin)).pad(10).row();
        confirmingHealTable.add(new Label("You chose to heal.", skin)).pad(10).row();
        confirmingHealTable.add(new Label(String.format("You will heal %s HP.", this.healAmt), skin)).pad(10).row();

        {
            TextButton confirmButton = new TextButton("Confirm", skin);
            confirmButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    runState.getPlayer().heal(healAmt);
                    game.getSoundManager().playHeal();
                    game.resetCamera();
                    completeNode();
                    game.setScreen(new MapScreen(game, runState));
                }
            });

            TextButton cancelButton = new TextButton("Cancel", skin);
            cancelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Play button click sound
                    game.getSoundManager().playButtonClick();
                    confirmingHealTable.remove();
                    mainWindow.addActor(choosingTable);
                }
            });
            confirmingHealTable.add(confirmButton);
            confirmingHealTable.add(cancelButton).row();
        }

        confirmingUpgradeTable.setFillParent(true);
        confirmingUpgradeTable.center().pad(20);
        confirmingUpgradeTable.defaults().width(250).height(100).pad(10);
        confirmingUpgradeTable.add(new Label("Confirm your choice!", skin)).pad(10).row();
        confirmingUpgradeTable.add(new Label("Once confirmed, you cannot go back!", skin)).pad(10).row();
        confirmingUpgradeTable.add(new Label("You chose to upgrade a building.", skin)).pad(10).row();
        confirmingUpgradeTable.add(new Label("You will get a randomised building upgrade.", skin)).pad(10).row();

        {
            TextButton confirmButton = new TextButton("Confirm", skin);
            confirmButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Play button click sound
                    game.getSoundManager().playButtonClick();
                    game.resetCamera();
                    completeNode();
                    game.setScreen(new RewardScreen(game, runState, mysteryReward));
                }
            });

            TextButton cancelButton = new TextButton("Cancel", skin);
            cancelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Play button click sound
                    game.getSoundManager().playButtonClick();
                    confirmingUpgradeTable.remove();
                    mainWindow.addActor(choosingTable);
                }
            });
            confirmingUpgradeTable.add(confirmButton);
            confirmingUpgradeTable.add(cancelButton).row();
        }

        /*
        Table table = new Table();
        table.setFillParent(true);
        table.center().pad(20);
        mainWindow.addActor(table);
        table.defaults().width(250).height(100).pad(10);

        if (state == RestScreenState.CHOOSING) {
            table.add(new Label("You can finally rest...", skin)).pad(15).row();
            table.add(new Label("What do you want to do?", skin)).pad(15).row();

            TextButton healButton = new TextButton("Heal up!", skin);
            healButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    healChosen();
                }
            });

            TextButton rewardButton = new TextButton("Upgrade a Building!", skin);
            rewardButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    upgradeChosen();
                }
            });

            table.add(healButton);
            table.add(rewardButton).row();
        }

        if (state == RestScreenState.CONFIRMING) {
            table.add(new Label("Confirm your choice!", skin)).pad(10).row();
            table.add(new Label("Once confirmed, you cannot go back!", skin)).pad(10).row();

            if (pendingChoice == ChoiceMade.HEAL) {
                table.add(new Label("You chose to heal.", skin)).pad(10).row();
                table.add(new Label(String.format("You will heal %s HP.", this.healAmt), skin)).pad(10).row();
            }
            if (pendingChoice == ChoiceMade.REWARD) {
                table.add(new Label("You chose to upgrade a building.", skin)).pad(10).row();
                table.add(new Label("You will get a randomised building upgrade.", skin)).pad(10).row();
            }

            TextButton confirmButton = new TextButton("Confirm", skin);
            confirmButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    runState.getCurrNode().setCompleted(true);
                    for (MapNode next : runState.getCurrNode().getNextNodes()) {
                        next.setUnlocked(true);
                    }
                    if (pendingChoice == ChoiceMade.HEAL) {
                        runState.getPlayer().heal(healAmt);
                        game.resetCamera();
                        game.setScreen(new MapScreen(game, runState));
                        return;
                    }

                    if (pendingChoice == ChoiceMade.REWARD) {
                        game.resetCamera();
                        game.setScreen(new RewardScreen(game, runState, mysteryReward));
                        return;
                    }
                }
            });

            TextButton cancelButton = new TextButton("Cancel", skin);
            cancelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    pendingChoice = null;
                    state = RestScreenState.CHOOSING;
                    buildRestUI();
                }
            });

            table.add(confirmButton);
            table.add(cancelButton).row();
        }

        // Any additional states of the screen will be added below

         */
    }

    private void completeNode() {
        runState.getCurrNode().setCompleted(true);
        for (MapNode next : runState.getCurrNode().getNextNodes()) {
            next.setUnlocked(true);
        }}

    public void healChosen() {

        pendingChoice = ChoiceMade.HEAL;
        state = RestScreenState.CONFIRMING;
        buildRestUI();
    }

    public void upgradeChosen() {
        pendingChoice = ChoiceMade.REWARD;
        state = RestScreenState.CONFIRMING;
        buildRestUI();
    }


    @Override
    public void render(float delta) {
        // PopUp manager will spawn the next popup if needed (and exists), no pause necessary since it doesn't even exist
        PopUpManager popUpManager = game.getPopUpManager();
        if (popUpManager != null && popUpManager.showNext(() -> {
        }, () -> {
        })) {
            ScreenUtils.clear(Color.BLACK);
            stage.act(delta);
            stage.draw();
            return;
        }

        // Handles the pause menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new PauseMenuScreen(game, runState, this));
            return;
        }

        codexCheck();

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

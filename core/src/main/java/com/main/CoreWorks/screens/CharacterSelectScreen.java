package com.main.CoreWorks.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Coreworks;
import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Generators.RunMapGenerator;
import com.main.CoreWorks.RunPersistence.RunMap;
import com.main.CoreWorks.RunPersistence.RunState;
import com.main.CoreWorks.TextParser.Sentence;
import com.main.CoreWorks.database.PlayerDatabase;
import com.main.CoreWorks.entities.Player;
import com.main.CoreWorks.entities.Relics.Relic;
import com.main.CoreWorks.simulators.PopUpTutorial.PopUpManager;

public class CharacterSelectScreen implements Screen {

    private final Coreworks game;
    private Array<Player> characterList;
    private Stage stage;
    private Skin skin;

    private Player selectedPlayer;
    private Table selectedCard;

    public CharacterSelectScreen(Coreworks game) {
        this.game = game;
        this.characterList = createCharacterList();
    }

    @Override
    public void show() {
        stage = new Stage(game.viewport, game.batch);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        Gdx.input.setInputProcessor(stage);

        // Sets the popup manager
        game.getPopUpManager().setScene2D(stage, skin);

        // Generate Screen specific popups here
        game.getPopUpManager().requestPopup(
            "character_select",
            "Choosing a character",
            "Every character has their unique attributes. \nYou can select and preview any character's unique loadout before starting the game.\nOnce chosen, the choice is final!",
            false
        );

        // Build the Scene2D UI
        buildSelectUI();
    }

    // Helper function that creates all the playable characters for selection (More characters will be initialised here)
    private Array<Player> createCharacterList() {
        Array<Player> characterList = new Array<>();
        characterList.add(PlayerDatabase.getPlayer("Engineer"));
        characterList.add(PlayerDatabase.getPlayer("Chemist"));
        characterList.add(PlayerDatabase.getPlayer("Commander"));
        characterList.add(PlayerDatabase.getPlayer("Tester"));

        // Copy-paste code to add more characters

        return characterList;
    }

    // Helper function that builds the main selection UI
    private void buildSelectUI() {
        stage.clear();

        Table table = new Table();
        table.setFillParent(true);
        table.top().pad(20);
        stage.addActor(table);

        table.add(new Label("Choose Your Character", skin)).padBottom(20).row();

        // Builds the preview table
        Table previewTable = new Table(skin);
        previewTable.setBackground(skin.newDrawable("default-round", Color.BLUE));
        previewTable.pad(15);
        table.add(previewTable).pad(20).row();
        previewTable.setName("previewTable");
        refreshPreview(previewTable);


        // Builds the character selection cards + table
        Table selectionCardTable = new Table();
        selectionCardTable.defaults().pad(10).width(130).height(130);
        for (Player player : characterList) {
            Table selectionCard = new Table(skin);
            selectionCard.setBackground(skin.newDrawable("default-round", Color.YELLOW));
            selectionCard.pad(10);

            selectionCard.add(new Label(player.displayName(), skin)).row();
            TextButton selectButton = new TextButton("Select", skin);
            selectButton.setColor(Color.MAGENTA);
            selectButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Set the player for previewing stats in a preview screen
                    selectedPlayer = player;

                    // Highlights the selected card
                    if (selectedCard != null) {
                        selectedCard.setBackground(skin.newDrawable("default-round", Color.YELLOW));
                    }
                    selectedCard = selectionCard;
                    selectedCard.setBackground(skin.newDrawable("default-round", Color.GREEN));

                    refreshPreview(previewTable);
                }
            });
            selectionCard.add(selectButton).pad(10).width(100);

            // Adds to the selection table
            selectionCardTable.add(selectionCard);
        }
        table.add(selectionCardTable).row();

        // Adds the start Run button
        TextButton startButton = new TextButton("Start Run!", skin);
        startButton.setColor(Color.ORANGE);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedPlayer != null) {
                    // Creates the initial runState
                    RunState runState = new RunState(selectedPlayer);
                    // Generates hardcoded RunMap for testing (Uncomment)
                    // RunMap runMap = RunMapGenerator.generateHardcodedRunMap(runState);
                    // Generates procedurally generated runMap
                    RunMap runMap = RunMapGenerator.generateRandomRunMapF1(runState);
                    runState.addNextMap(RunMapGenerator.generateRandomRunMapF2(runState));
                    runState.addNextMap(RunMapGenerator.generateRandomRunMapF3(runState));
                    runState.setRunMap(runMap);
                    runState.setCurrNode(runMap.getStartNode());

                    // Moves to map screen
                    game.resetCamera();
                    game.setScreen(new MapScreen(game, runState));
                    dispose();
                }
            }
        });
        table.add(startButton).pad(15).width(200).height(80);
    }

    // Helper function to refresh the preview screen everytime there is a change to it
    private void refreshPreview(Table preview) {
        preview.clearChildren();

        if (selectedPlayer == null) {
            preview.add(new Label("Select a character to preview their information", skin));
            return;
        }

        // Sets the tooltips for player info
        Sentence playerInfo = new Sentence(selectedPlayer.toString(), true);
        preview.add(playerInfo.toTable(skin)).row();
        preview.add(new Label(String.format("Grid size: %s x %s", selectedPlayer.getFactoryGrid().getMaxWidth(), selectedPlayer.getFactoryGrid().getMaxHeight()), skin)).row();

        // Sets the tooltips for starter buildings (max 3 per row)
        Label label1 = new Label("Starter buildings:", skin);
        label1.setColor(Color.ORANGE);
        preview.add(label1).row();
        Table buildingTable = new Table();
        int countBuilding = 0;
        for (Building building : selectedPlayer.getInventory()) {
            Label buildingLabel = new Label(String.format("- %s ", building.displayName()), skin);
            buildingLabel.setColor(Color.CYAN);
            buildingTable.add(buildingLabel).pad(3);
            // Add future tooltips for description here
            countBuilding++;
            if (countBuilding % 3 == 0) {
                buildingTable.row();
            }
        }
        preview.add(buildingTable).row();

        // Sets the tooltips for starter relics
        Label label2 = new Label("Starter Relics:", skin);
        label2.setColor(Color.ORANGE);
        preview.add(label2).row();
        Table relicTable = new Table();
        int countRelic = 0;
        for (Relic relic : selectedPlayer.getRelics()) {
            Label label3 = new Label("- " + relic.getName(), skin);
            label3.setColor(Color.GOLD);
            Tooltip<Table> descToolTip = new Tooltip<>(relic.getDescription().toTable(skin));
            descToolTip.setInstant(true);
            label3.addListener(descToolTip);
            relicTable.add(label3);
            countRelic++;
            if (countRelic % 5 == 0) {
                relicTable.row();
            }
        }
        preview.add(relicTable);
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

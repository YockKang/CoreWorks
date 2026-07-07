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
import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Generators.RewardGenerator;
import com.main.CoreWorks.Generators.RunMapGenerator;
import com.main.CoreWorks.Rewards.Reward;
import com.main.CoreWorks.RunPersistence.MapNode;
import com.main.CoreWorks.RunPersistence.RunMap;
import com.main.CoreWorks.RunPersistence.RunState;
import com.main.CoreWorks.database.PlayerDatabase;
import com.main.CoreWorks.entities.Player;
import com.main.CoreWorks.entities.Relics.Relic;

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

        // Build the Scene2D UI
        buildSelectUI();
    }

    // Helper function that creates all the playable characters for selection (More characters will be initialised here)
    private Array<Player> createCharacterList() {
        Array<Player> characterList = new Array<>();
        characterList.add(PlayerDatabase.getPlayer("Engineer"));
        characterList.add(PlayerDatabase.getPlayer("Chemist"));
        characterList.add(PlayerDatabase.getPlayer("Commander"));

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

        preview.add(new Label(selectedPlayer.toString(), skin)).row();
        preview.add(new Label(String.format("Grid size: %s x %s", selectedPlayer.getFactoryGrid().getMaxWidth(), selectedPlayer.getFactoryGrid().getMaxHeight()), skin)).row();
        preview.add(new Label("Starter buildings:", skin)).row();
        for (Building building : selectedPlayer.getInventory()) {
            preview.add(new Label(String.format("- %s", building.displayName()), skin)).row();
        }
        preview.add(new Label("Starter Relics:", skin)).row();
        for (Relic relic : selectedPlayer.getRelics()) {
            preview.add(new Label(String.format("- %s: %s", relic.getName(), relic.getDescription()), skin)).row();
        }
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

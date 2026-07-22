package com.main.CoreWorks.simulators.PopUpTutorial;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Queue;

public class PopUpManager {
    private Queue<PopUp> queue = new Queue<>();

    // Create a set that stores all ids so it does not repeatedly trigger an already shown id
    private ObjectSet<String> shownIDs = new ObjectSet<>();

    private Skin skin;
    private Stage stage;

    private boolean popupOpen = false;

    // Toggle to turn on / off tutorials
    private boolean popupEnabled = true;

    public void setScene2D(Stage stage, Skin skin) {
        this.skin = skin;
        this.stage = stage;
    }

    public boolean isPopupEnabled() {
        return popupEnabled;
    }

    public void setPopupEnabled(boolean popupEnabled) {
        this.popupEnabled = popupEnabled;
    }

    public void requestPopup(String id, String title, String message, boolean pauseGame) {
        // If popup is disabled or has been shown before, do nothing
        if (!popupEnabled || shownIDs.contains(id)) {
            return;
        }
        // Else, queue the popup into the queue for extraction
        shownIDs.add(id);
        queue.addLast(new PopUp(id, title, message, pauseGame));
    }

    public boolean isPopupOpen() {
        return popupOpen;
    }
    public boolean hasPendingPopup() {
        return queue.notEmpty();
    }

    public boolean showNext(Runnable onPause, Runnable onResume) {
        if (popupOpen) {
            return true;
        }

        if (!hasPendingPopup()) {
            return false;
        }

        // Remove the first pending popUp and pause the game (if needed)
        PopUp popUp = queue.removeFirst();
        this.popupOpen = true;
        if (popUp.pauseGame) {
            onPause.run();
        }

        // Create the dialog pop up box using scene2D's dialog
        Dialog dialog = new Dialog(popUp.title, skin) {
            @Override
            protected void result(Object object) {
                // If dismissed, close the pop up (and unpause if needed)
                popupOpen = false;
                if (popUp.pauseGame) {
                    onResume.run();
                }
            }
        };

        // Handles the title
        dialog.getTitleLabel().setAlignment(Align.center);
        dialog.getTitleLabel().setColor(Color.GREEN);
        dialog.getTitleTable().defaults().expandX().center();
        dialog.getTitleTable().setBackground(skin.newDrawable("default-round", Color.RED));

        // Handles the body
        Label textLabel = new Label(popUp.message, skin);
        textLabel.setColor(Color.ORANGE);
        dialog.getContentTable().add(textLabel);
        dialog.getContentTable().setBackground(skin.newDrawable("default-round", Color.BLUE));

        dialog.button("Got it!");

        // Block all user input until dismissed
        dialog.setModal(true);
        dialog.setMovable(false);
        dialog.show(stage);

        // Change the popup size
        dialog.setSize(stage.getWidth() * 0.7f, stage.getHeight() * 0.7f);

        // Center the popup
        dialog.setPosition((stage.getWidth() - dialog.getWidth()) / 2f, (stage.getHeight() - dialog.getHeight()) / 2f);

        return true;
    }
}

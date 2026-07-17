package com.main.CoreWorks.simulators.PopUpTutorial;

public class PopUp {
    public String id;
    public String title;
    public String message;
    public boolean pauseGame;

    public PopUp(String id, String title, String message, boolean pauseGame) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.pauseGame = pauseGame;
    }
}

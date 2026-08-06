package com.main.CoreWorks.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

/// Rudimentary method to initialize all sounds in one class for easy access
/// simply add a new sound by adding a new line (following the format)

public class SoundManager {
    private Sound buildingDisable;
    private Sound buildingPlace;
    private Sound buildingRemove;
    private Sound buttonClick;
    private Sound damage;
    private Sound defeat;
    private Sound enemyDown;
    private Sound heal;
    private Sound rewardSelected;
    private Sound shield;
    private Sound shopPurchase;

    public SoundManager() {
        this.buildingDisable = Gdx.audio.newSound(Gdx.files.internal("Audio/sfx/BuildingDisable.wav"));
        this.buildingPlace = Gdx.audio.newSound(Gdx.files.internal("Audio/sfx/BuildingPlace.wav"));
        this.buildingRemove = Gdx.audio.newSound(Gdx.files.internal("Audio/sfx/BuildingRemove.wav"));
        this.buttonClick = Gdx.audio.newSound(Gdx.files.internal("Audio/sfx/ButtonClick.wav"));
        this.damage = Gdx.audio.newSound(Gdx.files.internal("Audio/sfx/Damage.wav"));
        this.defeat = Gdx.audio.newSound(Gdx.files.internal("Audio/sfx/Defeat.wav"));
        this.enemyDown = Gdx.audio.newSound(Gdx.files.internal("Audio/sfx/EnemyDown.wav"));
        this.heal = Gdx.audio.newSound(Gdx.files.internal("Audio/sfx/Heal.wav"));
        this.rewardSelected = Gdx.audio.newSound(Gdx.files.internal("Audio/sfx/RewardSelected.wav"));
        this.shield = Gdx.audio.newSound(Gdx.files.internal("Audio/sfx/Shield.wav"));
        this.shopPurchase = Gdx.audio.newSound(Gdx.files.internal("Audio/sfx/ShopPurchase.wav"));
    }

    // All .play methods for any init sounds will go here

    public void playBuildingDisable() {
        buildingDisable.play();
    }

    public void playBuildingPlace() {
        buildingPlace.play();
    }

    public void playBuildingRemove() {
        buildingRemove.play();
    }

    public void playButtonClick() {
        buttonClick.play();
    }

    public void playDamage() {
        damage.play();
    }

    public void playDefeat() {
        defeat.play();
    }

    public void playEnemyDown() {
        enemyDown.play();
    }

    public void playHeal() {
        heal.play();
    }

    public void playRewardSelected() {
        rewardSelected.play();
    }

    public void playShield() {
        shield.play();
    }

    public void playShopPurchase() {
        shopPurchase.play();
    }

    // Adds a dispose method to dispose of all the sounds in one command
    public void dispose() {
        buildingDisable.dispose();
        buildingPlace.dispose();
        buildingRemove.dispose();
        buttonClick.dispose();
        damage.dispose();
        defeat.dispose();
        enemyDown.dispose();
        heal.dispose();
        rewardSelected.dispose();
        shield.dispose();
        shopPurchase.dispose();
    }
}

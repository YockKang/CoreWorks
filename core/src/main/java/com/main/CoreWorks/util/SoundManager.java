package com.main.CoreWorks.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.ObjectMap;

/// Rudimentary method to initialize all sounds in one class for easy access
/// simply add a new sound by adding a new line (following the format)

public class SoundManager {
    private static AssetManager assetManager;
    private static ObjectMap<String, String> soundsMap = new ObjectMap<>();

    public SoundManager() {
        assetManager = new AssetManager();
        assetManager.load("Audio/sfx/BuildingDisable.wav", Sound.class);
        soundsMap.put("buildingDisable", "Audio/sfx/BuildingDisable.wav");

        assetManager.load("Audio/sfx/BuildingPlace.wav", Sound.class);
        soundsMap.put("buildingPlace", "Audio/sfx/BuildingPlace.wav");

        assetManager.load("Audio/sfx/BuildingRemove.wav", Sound.class);
        soundsMap.put("buildingRemove", "Audio/sfx/BuildingRemove.wav");

        assetManager.load("Audio/sfx/ButtonClick.wav", Sound.class);
        soundsMap.put("buttonClick", "Audio/sfx/ButtonClick.wav");

        assetManager.load("Audio/sfx/Damage.wav", Sound.class);
        soundsMap.put("damage", "Audio/sfx/Damage.wav");

        assetManager.load("Audio/sfx/Defeat.wav", Sound.class);
        soundsMap.put("defeat", "Audio/sfx/Defeat.wav");

        assetManager.load("Audio/sfx/EnemyDown.wav", Sound.class);
        soundsMap.put("enemyDown", "Audio/sfx/EnemyDown.wav");

        assetManager.load("Audio/sfx/Heal.wav", Sound.class);
        soundsMap.put("heal", "Audio/sfx/Heal.wav");

        assetManager.load("Audio/sfx/RewardSelected.wav", Sound.class);
        soundsMap.put("rewardSelected", "Audio/sfx/RewardSelected.wav");

        assetManager.load("Audio/sfx/Shield.wav", Sound.class);
        soundsMap.put("shield", "Audio/sfx/Shield.wav");

        assetManager.load("Audio/sfx/ShopPurchase.wav", Sound.class);
        soundsMap.put("shopPurchase", "Audio/sfx/ShopPurchase.wav");

        assetManager.finishLoading();
    }

    // All .play methods for any init sounds will go here

    public void playSound(String name, float volume) {
        Sound sound = getSound(name);
        sound.play(volume);
    }

    public Sound getSound(String name) {
        return assetManager.get(soundsMap.get(name));
    }

    public void playButtonClick() {
        playSound("buttonClick", 1);
    }

    public void playDamage() {
        playSound("damage", 1);
    }

    public void playHeal() {
        playSound("heal", 1);
    }

    public void playShield() {
        playSound("shield", .5f);
    }


    // Adds a dispose method to dispose of all the sounds in one command
    public void dispose() {
        assetManager.dispose();
    }
}

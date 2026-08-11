package com.main.CoreWorks.util;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.main.CoreWorks.Resources.ResourceTemplate;
import com.main.CoreWorks.database.ResourceDatabase;

public class SpriteManager {

    private static AssetManager assetManager;
    private static ObjectMap<String, String> imgMap = new ObjectMap<>();

    public SpriteManager() {
        assetManager = new AssetManager();
    }

    public void load() {
        assetManager.load("Images/ResourceContainer.png", Texture.class);
        imgMap.put("ResourceContainer", "Images/ResourceContainer.png");
        for (ObjectMap.Entry<String, ResourceTemplate> entry : ResourceDatabase.getDB()) {
            JsonValue data = entry.value.getData();
            String id = data.getString("id");
            String visType = data.getString("VisType");
            String assetFile = "Images/" + id + ".png";
            assetManager.load(assetFile, Texture.class);
            imgMap.put(id, assetFile);
            switch (visType) {
                case "Liquid" -> {
//                    Texture spriteSheet = new Texture(Gdx.files.internal());
//                    TextureRegion[][] frames = TextureRegion.split(spriteSheet, 12, 8);

                    assetFile = "Images/" + id + "_Liquid.png";
                    assetManager.load(assetFile, Texture.class);
                    imgMap.put(id+visType, assetFile);
                }
                case "Bulk" -> {
                    assetFile = "Images/" + id + "_Bulk.png";
                    assetManager.load(assetFile, Texture.class);
                    imgMap.put(id+visType, assetFile);
                }
            }
        }
        assetManager.load("Images/Pipes.png", Texture.class);
        imgMap.put("Pipes", "Images/Pipes.png");


        assetManager.finishLoading();
    }

    public Texture getTexture(String name) {
        return assetManager.get(imgMap.get(name));
    }

    public TextureRegion[][] getTextureRegion(String name, int frameWidth, int frameHeight) {
        return TextureRegion.split(getTexture(name), frameWidth, frameHeight);
    }

    // Adds a dispose method to dispose of all the sprites in one command
    public void dispose() {
        assetManager.dispose();
    }
}



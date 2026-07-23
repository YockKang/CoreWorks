package com.main.CoreWorks.database;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Factory.BuildingTemplate.BuildingTemplate;
import com.main.CoreWorks.Factory.FactoryGrid;
import com.main.CoreWorks.entities.Player;

public class PlayerDatabase {
    private static final ObjectMap<String, JsonValue> PlayerDB = new ObjectMap<>();

    public static Player register(JsonValue data) {
        if (data.isArray()) {
            data.forEach(PlayerDatabase::register);
            return null;
        } else {
            String id = data.getString("Name");
            System.out.println(id);
            System.out.println(data);
            PlayerDB.put(id, data);
            return getPlayer(id);
        }
    }

    public static Player getPlayer(String name) {
        return new Player(PlayerDB.get(name));
    }

    public static String showDB(){
        return PlayerDB.toString();
    }
}

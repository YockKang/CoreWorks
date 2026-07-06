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

    public static Player createEngineer() {
        // Determines the size of the factory grid
        FactoryGrid factoryGrid = new FactoryGrid(6, 6);
        // Add starting relics
        // Add any passives here
        Player player = new Player(50,0, "Engineer", factoryGrid);

        // Adds the starting buildings
        player.addBuilding(BuildingDatabase.getBuilding("Shooter0"));
        player.addBuilding(BuildingDatabase.getBuilding("BigIronMiner"));
        player.addBuilding(BuildingDatabase.getBuilding("Cannonballer"));
        return player;

    }

    // More player types TBD
}

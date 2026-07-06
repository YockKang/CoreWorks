package com.main.CoreWorks.database;

import com.main.CoreWorks.Factory.FactoryGrid;
import com.main.CoreWorks.entities.Player;

public class PlayerDatabase {

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

package com.main.CoreWorks.entities;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Factory.FactoryGrid;
import com.main.CoreWorks.database.BuildingDatabase;
import com.main.CoreWorks.database.RelicDatabase;
import com.main.CoreWorks.database.RelicGroupDatabase;
import com.main.CoreWorks.entities.Relics.Relic;

public class Player extends Character{

    private Array<Building> inventory;
    private Array<Relic> relics;
    private FactoryGrid factoryGrid;
    // Passives Implementation TBD

    public Player(int hp, int shield, String name, FactoryGrid factoryGrid) {
        super(hp, shield, name);
        this.inventory = new Array<>();
        this.factoryGrid = factoryGrid;
    }

    public Player(JsonValue data) {
        super(data);
        this.inventory = new Array<>();
        int[] gridSize = data.get("Grid").asIntArray();
        this.factoryGrid = new FactoryGrid(gridSize[0], gridSize[1]);
        if (data.get("Buildings") != null) {
            String[] buildings = data.get("Buildings").asStringArray();
            for (String b : buildings) {
                addBuilding(BuildingDatabase.getBuilding(b));
            }
        }
        if (data.get("Relics") != null) {
            String[] relics = data.get("Relics").asStringArray();
            for (String r : relics) {
                addRelic(RelicDatabase.get(r));
            }
        }
    }

    public void addBuilding(Building building) {
        inventory.add(building);
    }

    public void addRelic(Relic r) {
        relics.add(r);
    }

    public boolean removeBuilding(Building building) {
        return inventory.removeValue(building, true);
    }

    public Array<Building> getInventory() {
        return inventory;
    }

    public Building getBuildingAt(int index) {
        return inventory.get(index);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public FactoryGrid getFactoryGrid() {
        return factoryGrid;
    }
}

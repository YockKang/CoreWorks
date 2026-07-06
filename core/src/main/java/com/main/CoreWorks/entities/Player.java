package com.main.CoreWorks.entities;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Factory.FactoryGrid;

public class Player extends Character{

    private Array<Building> inventory;
    private FactoryGrid factoryGrid;
    // Relics Implementation TBD
    // Passives Implementation TBD

    public Player(int hp, int shield, String name, FactoryGrid factoryGrid) {
        super(hp, shield, name);
        this.inventory = new Array<>();
        this.factoryGrid = factoryGrid;
    }

    public void addBuilding(Building building) {
        inventory.add(building);
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

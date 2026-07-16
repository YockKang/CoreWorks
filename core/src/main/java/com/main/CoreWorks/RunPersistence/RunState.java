package com.main.CoreWorks.RunPersistence;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Factory.FactoryGrid;
import com.main.CoreWorks.entities.Player;
import com.main.CoreWorks.entities.Relics.Relic;

import java.util.Random;
import java.util.function.BinaryOperator;

public class RunState {
    private Player player;
    private FactoryGrid factoryGrid;
    private RunMap runMap;
    private MapNode currNode;
    private Random random;
    private Array<Relic> relics = new Array<>();

    // Stores the next floor maps here
    private Array<RunMap> nextMaps = new Array<>();
    private int currFloor = 1;

    // Code below stores resourceModifiers that relics can manipulate to increase / decrease damage
    // Each new damage "type" will need its own unique resourceModifiers (not very good implementation but works)
    // Below handles the TEMPORARY playerModifiers (cleared every combat)
    private ObjectMap<String, Float> tempBonuses = new ObjectMap<>();

    // Below handles the PERMANENT playerModifiers (persist thru combat)
    private ObjectMap<String, Float> permBonuses = new ObjectMap<>();

    public RunState(Player player) {
        this.player = player;
        this.factoryGrid = player.getFactoryGrid();
        for (Relic relic : player.getRelics()) {
            addRelic(relic);
        }
        this.random = new Random();
    }

    public RunState(Player player, long seed) {
        this.player = player;
        this.factoryGrid = player.getFactoryGrid();
        for (Relic relic : player.getRelics()) {
            addRelic(relic);
        }
        this.random = new Random(seed);
    }

    public Player getPlayer() {
        return player;
    }

    public FactoryGrid getFactoryGrid() {
        return factoryGrid;
    }

    public RunMap getRunMap() {
        return runMap;
    }

    public void setRunMap(RunMap runMap) {
        this.runMap = runMap;
    }

    public MapNode getCurrNode() {
        return currNode;
    }

    public void setCurrNode(MapNode currNode) {
        this.currNode = currNode;
    }

    public void setFactoryGrid(FactoryGrid factoryGrid) {
        this.factoryGrid = factoryGrid;
    }

    public Array<Building> getOwnedBuildings() {
        Array<Building> arr = new Array<>();
        arr.addAll(factoryGrid.getBuildings());
        arr.addAll(player.getInventory());
        return arr;
    }

    // Handles Temp playerModifiers
    public void addTempBonus(String name, float amt) {
        if (!tempBonuses.containsKey(name)) {
            tempBonuses.put(name, amt);
        } else {
            tempBonuses.put(name, tempBonuses.get(name) + amt);
        }
    }

    public boolean hasTempBonus(String name) {
        return tempBonuses.containsKey(name);
    }

    public float getTempBonus(String name, float fallback) {
        if (tempBonuses.containsKey(name)) {
            return tempBonuses.get(name);
        } else {
            return fallback;
        }
    }

    public float getTempBonus(String name) {
        return getTempBonus(name, 0);
    }

    // Handles Permanent playerModifiers
    public void addPermBonus(String name, float amt) {
        if (!permBonuses.containsKey(name)) {
            permBonuses.put(name, amt);
        } else {
            permBonuses.put(name, permBonuses.get(name) + amt);
        }
    }

    public float getPermBonus(String name, float fallback) {
        if (permBonuses.containsKey(name)) {
            return permBonuses.get(name);
        } else {
            return fallback;
        }
    }

    public float getPermBonus(String name) {
        return getPermBonus(name, 0);
    }

    public boolean hasPermBonus(String name) {
        return permBonuses.containsKey(name);
    }

    public float getBonuses(String name, BinaryOperator<Float> operation, float fallback) {
        return operation.apply(getTempBonus(name, fallback), getPermBonus(name, fallback));
    }

    public float getBonuses(String name, BinaryOperator<Float> operation) {
        return getBonuses(name, operation, 0);
    }

    public boolean hasBonus(String name) {
        return hasTempBonus(name) || hasPermBonus(name);
    }

    public void resetTempCombatModifiers() {
        tempBonuses.clear();
    }


    public Random getRandom() {
        return random;
    }

    public void addRelic(Relic relic) {
        relics.add(relic);
        relic.onAcquire(this);
    }

    public Array<Relic> getRelics() {
        return relics;
    }

    public void addNextMap(RunMap runMap) {
        this.nextMaps.add(runMap);
    }

    public boolean nextFloor() {
        return currFloor < (this.nextMaps.size + 1);
    }

    public Array<RunMap> getNextMaps() {
        return nextMaps;
    }

    public int getCurrFloor() {
        return currFloor;
    }

    public void setCurrFloor(int currFloor) {
        this.currFloor = currFloor;
    }
}

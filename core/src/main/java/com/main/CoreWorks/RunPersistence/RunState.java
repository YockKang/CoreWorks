package com.main.CoreWorks.RunPersistence;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Factory.FactoryGrid;
import com.main.CoreWorks.entities.Player;
import com.main.CoreWorks.entities.Relics.Relic;

import java.util.Random;

public class RunState {
    private Player player;
    private FactoryGrid factoryGrid;
    private RunMap runMap;
    private MapNode currNode;
    private Random random;
    private Array<Relic> relics = new Array<>();

    // Code below stores modifiers that relics can manipulate to increase / decrease damage
    // Each new damage "type" will need its own unique modifiers (not very good implementation but works)
    // Below handles the TEMPORARY modifiers
    private int tempPlayerBonusDmg = 0;
    private int tempPlayerBonusPoisonDmg = 0;
    private int tempPlayerBonusTrueDmg = 0;

    // Below handles the PERMANENT modifiers
    private int permPlayerBonusDmg = 0;
    private int permPlayerBonusPoisonDmg = 0;
    private int permPlayerBonusTrueDmg = 0;

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

    // Handles Temp modifiers
    public void addTempPlayerBonusDmg(int amt) {
        this.tempPlayerBonusDmg += amt;
    }

    public void addTempPlayerBonusPoisonDmg(int amt) {
        this.tempPlayerBonusPoisonDmg += amt;
    }

    public void addTempPlayerBonusTrueDmg(int amt) {
        this.tempPlayerBonusTrueDmg += amt;
    }

    public int getTempPlayerBonusDmg() {
        return tempPlayerBonusDmg;
    }

    public int getTempPlayerBonusPoisonDmg() {
        return tempPlayerBonusPoisonDmg;
    }

    public int getTempPlayerBonusTrueDmg() {
        return tempPlayerBonusTrueDmg;
    }

    public void resetTempCombatModifiers() {
        this.tempPlayerBonusDmg = 0;
        this.tempPlayerBonusPoisonDmg = 0;
        this.tempPlayerBonusTrueDmg = 0;
    }

    // Handles Perm modifiers
    public void addPermPlayerBonusDmg(int amt) {
        this.permPlayerBonusDmg += amt;
    }

    public void addPermPlayerBonusPoisonDmg(int amt) {
        this.permPlayerBonusPoisonDmg += amt;
    }

    public void addPermPlayerBonusTrueDmg(int amt) {
        this.permPlayerBonusTrueDmg += amt;
    }

    public int getPermPlayerBonusDmg() {
        return permPlayerBonusDmg;
    }

    public int getPermPlayerBonusPoisonDmg() {
        return permPlayerBonusPoisonDmg;
    }

    public int getPermPlayerBonusTrueDmg() {
        return permPlayerBonusTrueDmg;
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
}

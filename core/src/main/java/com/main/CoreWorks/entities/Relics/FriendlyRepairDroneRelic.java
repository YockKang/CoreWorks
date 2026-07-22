package com.main.CoreWorks.entities.Relics;

import com.main.CoreWorks.RunPersistence.RunState;

public class FriendlyRepairDroneRelic extends Relic {

    private static int HEAL = 1;
    // Uses count to track the current tick state, uses cooldown as overall "charge" time
    // Both need to be initialized to the same value, else the advertised ability may not be the same as actual ability
    private static int COUNT = 15;
    private static int COOLDOWN = 15;

    public FriendlyRepairDroneRelic() {
        super("FriendlyRepairDroneRelic",
            "Friendly Repair Drone",
            String.format("Heals %s HP every %s ticks", HEAL, COOLDOWN),
            String.format("Heals %s HP every %s ticks in combat", HEAL, COOLDOWN),
            "Friendly Repair Drone healed for " + HEAL + " HP",
            "2");
    }

    @Override
    public boolean onTick(RunState runState) {
        if (COUNT != 0) {
            COUNT--;
            return false;
        }
        runState.getPlayer().heal(HEAL);
        COUNT = COOLDOWN;
        return true;
    }

}

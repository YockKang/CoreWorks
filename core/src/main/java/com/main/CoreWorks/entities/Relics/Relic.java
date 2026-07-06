package com.main.CoreWorks.entities.Relics;

public abstract class Relic {
    protected String name;
    protected String description;

    public Relic(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // Called when relic has on-collection effects
    public abstract void onAcquire();

    // Called when relic has on-combat-start effects
    public abstract void onCombatStart();

    // Called when relic has on-each-tick effects
    public abstract void onTick();

    // Called when relic has on-combat-end effects
    public abstract void onCombatEnd();
}

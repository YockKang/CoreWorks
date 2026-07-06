package com.main.CoreWorks.entities.Relics;

import com.main.CoreWorks.RunPersistence.RunState;

import java.util.Objects;

public abstract class Relic {
    protected String id;
    protected String name;
    protected String description;
    protected String tier;

    public Relic(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getTier() {
        return tier;
    }

    // Called when relic has on-collection effects
    public abstract void onAcquire(RunState runState);

    // Called when relic has on-combat-start effects
    public abstract void onCombatStart(RunState runState);

    // Called when relic has on-each-tick effects
    public abstract void onTick(RunState runState);

    // Called when relic has on-combat-end effects
    public abstract void onCombatEnd(RunState runState);

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Relic oRelic) {
            return Objects.equals(oRelic.id, this.id);
        }
        return false;
    }
}

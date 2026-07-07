package com.main.CoreWorks.entities.Relics;

import com.main.CoreWorks.RunPersistence.RunState;
import com.main.CoreWorks.TextParser.Sentence;

import java.util.Objects;

public abstract class Relic {
    protected String id;
    protected String name;
    protected Sentence description;
    protected String tier;
    protected String log;

    public Relic(String id, String name, String description, String log, String tier) {
        this.id = id;
        this.name = name;
        this.description = new Sentence(description, true);
        this.log = log;
        this.tier = tier;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Sentence getDescription() {
        return description;
    }

    public String getLog() {
        return log;
    }

    public String getTier() {
        return tier;
    }

    // Called when relic has on-collection effects
    public void onAcquire(RunState runState) {};

    // Called when relic has on-combat-start effects
    public boolean onCombatStart(RunState runState) { return false; };

    // Called when relic has on-each-tick effects
    public boolean onTick(RunState runState) { return false; };

    // Called when relic has on-combat-end effects
    public void onCombatEnd(RunState runState) {};

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Relic oRelic) {
            return Objects.equals(oRelic.id, this.id);
        }
        return false;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

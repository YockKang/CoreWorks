package com.main.CoreWorks.Rewards;

import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.RunPersistence.RunState;
import com.main.CoreWorks.entities.Relics.Relic;

public class AddRelicReward extends Reward {
    private Relic relic;

    public AddRelicReward(Relic relic) {
        super("Add a Relic", String.format("Adds 1 %s to your inventory", relic.getName()));
        this.relic = relic;
    }

    @Override
    public boolean needTarget() {
        return false;
    }

    @Override
    public void apply(RunState runState) {
        runState.addRelic(relic);
    }
}

package com.main.CoreWorks.Rewards;

import com.main.CoreWorks.RunPersistence.RunState;

public class AddTubeCountReward extends Reward {
    protected int addCount;

    public AddTubeCountReward(int count) {
        super("More Tubes", String.format("You can place \n %s more tubes", count));
        this.addCount = count;
    }

    public AddTubeCountReward(String desc, int count) {
        super("More Tubes", desc);
        this.addCount = count;
    }

    @Override
    public boolean needTarget() {
        return false;
    }

    @Override
    public void apply(RunState runState) {
        runState.getPlayer().refundTube(addCount);
    }

}


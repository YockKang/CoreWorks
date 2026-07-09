package com.main.CoreWorks.Rewards;

import com.main.CoreWorks.RunPersistence.RunState;

import java.util.Objects;

public class ShopOffer {
    protected int cost;
    protected Reward reward;
    protected boolean purchased = false;

    public ShopOffer(int cost, Reward reward) {
        this.cost = cost;
        this.reward = reward;
    }

    public int getCost() {
        return cost;
    }

    public Reward getReward() {
        return reward;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void apply(RunState runState) {
        runState.getPlayer().loseMoney(cost);
        reward.apply(runState);
        purchased = true;
    }
}

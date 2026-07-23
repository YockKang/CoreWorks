package com.main.CoreWorks.entities.Relics;

import com.main.CoreWorks.RunPersistence.RunState;

public class MoneyRelic extends Relic {

    private static int MONEY = 50;

    public MoneyRelic() {
        super("MoneyRelic",
            "Money Goblet",
            "Gives " + MONEY + " money on acquisition",
            "Gives " + MONEY + " money on acquisition",
            "Money Goblet gave one-time grant of " + MONEY + " coins",
            "0");
    }

    @Override
    public void onAcquire(RunState runState) { runState.getPlayer().gainMoney(MONEY); };

}

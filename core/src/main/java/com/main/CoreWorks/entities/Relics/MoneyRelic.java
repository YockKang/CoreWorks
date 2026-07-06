package com.main.CoreWorks.entities.Relics;

import com.main.CoreWorks.RunPersistence.RunState;

public class MoneyRelic extends Relic {

    private static int MONEY = 50;

    public MoneyRelic() {
        super("MoneyRelic",
            "Money Goblet",
            "Gives " + MONEY + " coins on acquisition",
            "Money Goblet gave one-time grant of " + MONEY + " coins",
            "0");
    }

    // Called when relic has on-collection effects
    public void onAcquire(RunState runState) { runState.getPlayer().gainMoney(MONEY); };

}

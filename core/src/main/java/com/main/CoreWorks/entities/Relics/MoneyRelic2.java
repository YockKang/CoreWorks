package com.main.CoreWorks.entities.Relics;

import com.main.CoreWorks.RunPersistence.RunState;

public class MoneyRelic2 extends Relic {

    private static int MONEY = 100;

    public MoneyRelic2() {
        super("MoneyRelic2",
            "Money Pot",
            "Gives " + MONEY + " money on acquisition",
            "Money Pot gave one-time grant of " + MONEY + " coins",
            "1");
    }

    // Called when relic has on-collection effects
    public void onAcquire(RunState runState) { runState.getPlayer().gainMoney(MONEY); };

}

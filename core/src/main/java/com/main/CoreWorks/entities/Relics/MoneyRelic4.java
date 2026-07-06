package com.main.CoreWorks.entities.Relics;

import com.main.CoreWorks.RunPersistence.RunState;

public class MoneyRelic4 extends Relic {

    private static int MONEY = 300;

    public MoneyRelic4() {
        super("MoneyRelic4",
            "Money Bank",
            "Gives " + MONEY + " coins on acquisition",
            "3");
    }

    // Called when relic has on-collection effects
    public void onAcquire(RunState runState) { runState.getPlayer().gainMoney(MONEY); };

}

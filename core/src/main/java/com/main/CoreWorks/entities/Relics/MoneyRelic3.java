package com.main.CoreWorks.entities.Relics;

import com.main.CoreWorks.RunPersistence.RunState;

public class MoneyRelic3 extends Relic {

    private static int MONEY = 150;

    public MoneyRelic3() {
        super("MoneyRelic3",
            "Money Vault",
            "Gives " + MONEY + " coins on acquisition",
            "2");
    }

    // Called when relic has on-collection effects
    public void onAcquire(RunState runState) { runState.getPlayer().gainMoney(MONEY); };

}
